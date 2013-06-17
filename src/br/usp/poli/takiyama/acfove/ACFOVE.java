package br.usp.poli.takiyama.acfove;

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
	
	/*
	 * I need to create a mechanism to avoid deadlocks between expanding and
	 * counting operations.
	 * This situation happens when expanding/counting the same PRV alternates
	 * as the best operation available.
	 */
	
	/**
	 * Constructor. Initializes AC-FOVE by shattering the specified marginal on
	 * the query, shattering all parfactors and converting all aggregation
	 * parfactors to standard parfactors.
	 */
	public ACFOVE(Marginal parfactors) {
		
		ConsoleLogger.setup();
		
		logger.info("Starting AC-FOVE...");
		this.input = parfactors;
		
		logger.info("Input: \n" + input);
		this.result = performConversionToStdParfactors(input);
		this.result = performInitialShattering(result);

		logger.info("Initial shattering and conversion: \n" + result);
		this.currentOperation = ImpossibleOperation.instance;
	}
	

	/**
	 * Shatters the specified marginal on the query (preservable of the
	 * marginal). After that shatters all parfactors in the marginal to 
	 * guarantee that for each pair of PRVs in different parfactors, sets of
	 * random variables represented by them are either equal or disjoint.
	 * 
	 * @param arg The marginal to shatter.
	 * @return The specified marginal shattered
	 */
	private Marginal performInitialShattering(Marginal arg) {
		Marginal result = new ShatterOnQuery(arg).run();
		result = new Shatter(result).run();
		return result;
	}
	
	
	/**
	 * Converts all aggregation parfactors in the specified marginal to 
	 * standard parfactors.
	 * @param arg The marginal containing parfactors to convert.
	 * @return The specified marginal with all aggregation parfactors 
	 * converted to standard parfactors.
	 */
	private Marginal performConversionToStdParfactors(Marginal arg) {
		return new ConvertToStdParfactors(arg).run();
	}
	
	
	/**
	 * Runs the AC-FOVE algorithm and returns the result.
	 * @return The result of running the AC-FOVE algorithm on the marginal
	 * specified when creating this instance.
	 */
	public Parfactor run() {
		while (thereAreVariablesToEliminate()) {
			runStep();
			resetCurrentOperation();
		}
		if (result.size() > 1) {
			throw new IllegalStateException();
		}
		logger.info("\nResult:\n" + result);
		return result.iterator().next();
	}
	
	
	/**
	 * Returns <code>true</code> if there are variables to eliminate in the
	 * marginal. All random variables that are not in the query must be 
	 * eliminated.
	 */
	private boolean thereAreVariablesToEliminate() {
		return !result.eliminables().isEmpty();
	}
	
	
	/**
	 * Returns the result of running one step of the algorithm. A step
	 * consists in choosing a macro operation and executing it.
	 * @return the result of running one step of the algorithm.
	 */
	Marginal runStep() {
		chooseMacroOperation();
		executeMacroOperation();
		return result;
	}
	
	
	/**
	 * Chooses the macro operation to execute. The chosen operation must have
	 * a smaller cost than the current operation. 
	 */
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
	
	private void evaluateGlobalSumOut(Prv prv, Set<Constraint> c) {
		RandomVariableSet eliminables = RandomVariableSet.getInstance(prv, c);
		MacroOperation candidate = new GlobalSumOut(result, eliminables);
		compareAndUpdate(candidate);
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
	 * In case of draw, the operation that eliminates more random variables is
	 * chosen.
	 * In case of another draw, current operation is kept.
	 */
	private void compareAndUpdate(MacroOperation candidate) {
		logger.info("\nEvaluating candidate " + candidate + " with " + currentOperation + "\n");
		int candidateCost = candidate.cost();
		int currentCost = currentOperation.cost();
		
		boolean costIsSmaller = (candidateCost < currentCost);
		boolean costIsEqual = (candidateCost == currentCost);
		boolean eliminatesMore = (candidate.numberOfRandomVariablesEliminated() 
				> currentOperation.numberOfRandomVariablesEliminated());
		
		if (costIsSmaller || (costIsEqual && eliminatesMore)) {
			logger.info("\nSetting " + candidate + " as current operation.\n");
			currentOperation = candidate;
		} else {
			logger.info("\nKeeping " + currentOperation + " as current operation.\n");
		}
	}
	
	
	/**
	 * Executes the current macro operation.
	 * TODO: throw exception when something happens
	 */
	private void executeMacroOperation() {
		logger.info("\nRunning " + currentOperation + "\n");
		result = currentOperation.run();
	}
	
	/**
	 * Resets the current macro operation for the next step of the algorithm.
	 */
	private void resetCurrentOperation() {
		currentOperation = ImpossibleOperation.instance;
	}
}