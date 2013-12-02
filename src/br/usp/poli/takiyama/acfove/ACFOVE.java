package br.usp.poli.takiyama.acfove;

import java.util.Set;
import java.util.logging.Level;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.utils.Sets;

public class ACFOVE {
	
	private final Marginal input;
	private Marginal result;
	private MacroOperation currentOperation;
	
//	private final static Logger logger = Logger.getLogger(ACFOVE.class.getName());
	
	// change later
//	private final long timeout;
//	private final long deadline;
//	long start, end;
//	double timeSeconds;
	
	
	/*
	 * I need to create a mechanism to avoid deadlocks between expanding and
	 * counting operations.
	 * This situation happens when expanding/counting the same PRV alternates
	 * as the best operation available.
	 */
	
	
	/**
	 * Constructor. Initializes AC-FOVE by shattering the specified marginal on
	 * the query and shattering all parfactors.
	 */
	public ACFOVE(Marginal parfactors, Level logLevel) {
		
//		FileLogger.setup(logLevel);
//		timeout = 100000;
//		deadline = System.currentTimeMillis() + timeout;
		
//		logger.info("Starting AC-FOVE...");
		this.input = parfactors;
		
//		logger.info("Input: \n" + input + "\n");
		
		result = input;
		
//		start = System.currentTimeMillis();
		this.result = performInitialShattering(result);
//		end = System.currentTimeMillis();
		
//		timeSeconds = (end - start) / 1000.0;
//		logger.severe("Time to initial shatter: " + timeSeconds + " s\n");
		
//		logger.info("Initial shattering and conversion: \n" + result + "\n");
		this.currentOperation = new Shatter(result);
	}
	
	/**
	 * Constructor. Initializes AC-FOVE using <code>Level.SEVERE</code> as log
	 * level.
	 */
	public ACFOVE(Marginal parfactors) {
		this(parfactors, Level.OFF); 
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
		Parfactor query = new StdParfactorBuilder()
				.variables(arg.preservable().prv())
				.constraints(arg.preservable().constraints())
				.build();
		Marginal result = new StdMarginal.StdMarginalBuilder().add(arg).add(query).build();
		result = new Shatter(result).run();
		return result;
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
		
		evaluateFinalMultiplication();
		try {
			runStep();
		} catch (UnsupportedOperationException e) {
			throw new IllegalStateException();
		}
		
//		logger.info("Result:\n" + result + "\n");
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
		
//		start = System.currentTimeMillis();
		chooseMacroOperation();
//		end = System.currentTimeMillis();
		
//		logger.severe("Time to choose operation: " + (end - start) + " ms\n");
		
//		start = System.currentTimeMillis();
		executeMacroOperation();
//		end = System.currentTimeMillis();
		
//		timeSeconds = (end - start) / 1000.0;
//		logger.severe("Time to run operation: " + (end - start) + " ms\n");
//		logger.info("Operation result:\n" + result + "\n\n\n");
		
		return result;
	}
	
	
	/**
	 * Chooses the macro operation to execute. The chosen operation must have
	 * a smaller cost than the current operation. 
	 */
	void chooseMacroOperation() {
		for (Parfactor p : result) {
			for (Prv prv : p.prvs()) {
				evaluateFullExpand(p, prv);
				evaluateGlobalSumOut(prv, p.constraints());
			}
			for (LogicalVariable lv : p.logicalVariables()) {
				evaluateCountingConvert(p, lv);
				evaluatePropositionalize(p, lv);
			}
			evaluateConversionToStdParfactors(p);
		}
	}
	
	private void evaluateGlobalSumOut(Prv prv, Set<Constraint> c) {
		RandomVariableSet eliminables = RandomVariableSet.getInstance(prv, c);
		MacroOperation candidate = new GlobalSumOut(result, eliminables);
		compareAndUpdate(candidate);
	}
	
	// used by VariableElimination
	void evaluateGlobalSumOut(Prv prv) {
		RandomVariableSet eliminables = RandomVariableSet.getInstance(prv, Sets.<Constraint>getInstance(0));
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
	
	private void evaluateFinalMultiplication() {
		MacroOperation candidate = new FinalMultiplication(result);
		compareAndUpdate(candidate);
	}
	
	private void evaluateConversionToStdParfactors(Parfactor p) {
		MacroOperation candidate = new ConvertToStdParfactors(result, p);
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
//		logger.fine("Evaluating candidate " + candidate + " with " + currentOperation + "\n");
		int candidateCost = candidate.cost();
		int currentCost = currentOperation.cost();
		int candidateEliminables = candidate.numberOfRandomVariablesEliminated();
		int currentEliminables = currentOperation.numberOfRandomVariablesEliminated();
		
		boolean costIsSmaller = (candidateCost < currentCost);
		boolean eliminatesMore = (candidateEliminables > currentEliminables);
		boolean eliminatesTheSame = (candidateEliminables == currentEliminables);
		
		if (eliminatesMore || (eliminatesTheSame && costIsSmaller)) {
//			logger.fine("Setting " + candidate + " as current operation.\n");
			currentOperation = candidate;
		} else {
//			logger.fine("Keeping " + currentOperation + " as current operation.\n");
		}
	}
	
	
	/**
	 * Executes the current macro operation.
	 * TODO: throw exception when something happens
	 */
	private void executeMacroOperation() {
//		logger.info("Running " + currentOperation + "\n");
		result = currentOperation.run();
	}
	
	/**
	 * Resets the current macro operation for the next step of the algorithm.
	 */
	private void resetCurrentOperation() {
		currentOperation = ImpossibleOperation.instance;
	}
	
	Marginal result() {
		return this.result;
	}
	
	MacroOperation currentOperation() {
		return this.currentOperation;
	}	
}