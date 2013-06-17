package br.usp.poli.takiyama.acfove;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.log.ConsoleLogger;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;

public final class ACFOVE {
	
	private final Marginal input;
	private Marginal result;
	private MacroOperation currentOperation;
	
	private final static Logger logger = Logger.getLogger(ACFOVE.class.getName());
	
	/**
	 * This 'buffer' stores all operations that were done in String format.
	 * Used to avoid loops between inverse operations (expanding/counting)
	 */
	private final Set<String> pastOperations;
	
	public ACFOVE(Marginal parfactors) {
		
		ConsoleLogger.setup();
		pastOperations = new HashSet<String>(100);
		
		logger.info("Starting AC-FOVE...");
		this.input = parfactors;
		
		logger.info("Input: \n" + input);
		this.result = performConversionToStdParfactors(input);
		this.result = performInitialShattering(result);

		logger.info("Initial shattering and conversion: \n" + result);
		this.currentOperation = ImpossibleOperation.instance;
	}
	

	private Marginal performInitialShattering(Marginal arg) {
		Marginal result = new ShatterOnQuery(arg).run();
		result = new Shatter(result).run();
		return result;
	}
	
	
	private Marginal performConversionToStdParfactors(Marginal arg) {
		return new ConvertToStdParfactors(arg).run();
	}
	
	
	public Parfactor run() {
		while (thereAreVariablesToEliminate()) {
			runStep();
			storeOperation();
			resetCurrentOperation();
		}
		if (result.size() > 1) {
			throw new IllegalStateException();
		}
		logger.info("\nResult:\n" + result);
		return result.iterator().next();
	}
	
	
	private boolean thereAreVariablesToEliminate() {
		return !result.eliminables().isEmpty();
	}

	
	private void storeOperation() {
		pastOperations.add(currentOperation.toString());
	}
	
	
	Marginal runStep() {
		chooseMacroOperation();
		executeMacroOperation();
		return result;
	}
	
	
	private void chooseMacroOperation() {
		for (Parfactor p : result) {
			for (Prv prv : p.prvs()) {
				evaluateFullExpand(p, prv);
				evaluateGlobalSumOut(prv, p.constraints());
			}
			for (LogicalVariable lv : p.logicalVariables()) {
				evaluateCountingConvert(p, lv);
				evaluatePropositionalize(p, lv);
			}
		}
	}
	
	// TODO improve cost comparison code
	
	private void evaluateGlobalSumOut(Prv prv, Set<Constraint> c) {
		RandomVariableSet eliminables = RandomVariableSet.getInstance(prv, c);
		MacroOperation candidate = new GlobalSumOut(result, eliminables);
		
		logger.info("\nEvaluating candidate " + candidate + " with " + currentOperation + "\n");
		int candidateCost = candidate.cost();
		int currentCost = currentOperation.cost();
		boolean costIsSmaller = (candidateCost < currentCost);
		boolean costIsEqual = (candidateCost == currentCost);
		boolean eliminatesMore = (candidate.numberOfRandomVariablesEliminated() 
				> currentOperation.numberOfRandomVariablesEliminated());
		
		logger.info("\n Candidate costs " + candidateCost + " while current costs " + currentCost + "\n");
		
		if (costIsSmaller || (costIsEqual && eliminatesMore)) {
			logger.info("\nSetting " + candidate + " as current operation.\n");
			currentOperation = candidate;
		} else {
			logger.info("\nKeeping " + currentOperation + " as current operation.\n");
		}
	}
	
	private void evaluateFullExpand(Parfactor p, Prv prv) {
		MacroOperation candidate = new FullExpand(result, p, prv);
		compareAndUpdate(candidate);
	}
	
	private void evaluateCountingConvert(Parfactor p, LogicalVariable v) {
		MacroOperation candidate = new CountingConvert(result, p, v);
		compareAndUpdate(candidate);
	}
	
	private void evaluatePropositionalize(Parfactor p, LogicalVariable v) {
		MacroOperation candidate = new Propositionalize(result, p, v);
		compareAndUpdate(candidate);
	}
	
	/**
	 * Compares the candidate macro operation with current operation and
	 * updates current if candidate's cost is smaller.
	 */
	private void compareAndUpdate(MacroOperation candidate) {
		logger.info("\nEvaluating candidate " + candidate + " with " + currentOperation + "\n");
		boolean costIsSmaller = (candidate.cost() < currentOperation.cost());
		if (costIsSmaller && !pastOperations.contains(candidate.toString())) {
			logger.info("\nSetting " + candidate + " as current operation.\n");
			currentOperation = candidate;
		} else {
			logger.info("\nKeeping " + currentOperation + " as current operation.\n");
		}
	}
	
	private void executeMacroOperation() {
		logger.info("\nRunning " + currentOperation + "\n");
		result = currentOperation.run();
	}
	
	private void resetCurrentOperation() {
		currentOperation = ImpossibleOperation.instance;
	}
}