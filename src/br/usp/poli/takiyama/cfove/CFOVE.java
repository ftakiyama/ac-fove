package br.usp.poli.takiyama.cfove;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.MathUtils;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.RandomVariableSet;
import br.usp.poli.takiyama.log.ConsoleLogger;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

import static br.usp.poli.takiyama.cfove.MacroOperations.*;

public final class CFOVE {
	
	// Input
	private RandomVariableSet query;
	private HashSet<Parfactor> parfactors;
	
	// Output
	private Parfactor marginalDistribution;
	
	// State variables
	private HashSet<RandomVariableSet> variablesToEliminate;
	//private HashSet<RandomVariableSet> allVariables;
	private int lowestCost;
	private int maxNumEliminatedVariables; // the maximum number of random variables eliminated
	private Operation currentOperation;
	
	// constants
	private final int INFINITE = Integer.MAX_VALUE;
	
	// log
	private final static Logger logger = Logger.getLogger(CFOVE.class.getName());
	
	private interface Operation {
		/**
		 * Executes the macro-operation.
		 */
		public void execute();
		
		@Override
		public String toString();
	}
	
	private class GlobalSumOut implements Operation {
		
		private ParameterizedRandomVariable variableToEliminate;
		private HashSet<Constraint> constraints;
		
		GlobalSumOut(ParameterizedRandomVariable prv, Set<Constraint> constraints) {
			this.variableToEliminate = prv;
			this.constraints = new HashSet<Constraint>(constraints);
		}
		
		@Override
		public void execute() {
			updateVariableSets();
			CFOVE.this.parfactors = new HashSet<Parfactor>(globalSumOut(parfactors, variableToEliminate, constraints));
		}
		
		/**
		 * Removes the variable being eliminated from the set of 
		 * variables to eliminate and from the set of all variables.
		 */
		private void updateVariableSets() {
			RandomVariableSet s = RandomVariableSet.getInstance (
					variableToEliminate, 
					constraints);
			variablesToEliminate.remove(s);
		}
		
		@Override
		public String toString() {
			StringBuilder strb = new StringBuilder();
			strb.append("GLOBAL-SUM-OUT\n")
				.append(this.variableToEliminate.toString())
				.append("\n")
				.append(this.constraints.toString());
			return strb.toString();
		}
	}
	
	private class CountingConvert implements Operation {
		
		private Parfactor parfactor;
		private StdLogicalVariable logicalVariable;
		
		CountingConvert(Parfactor parfactor, StdLogicalVariable logicalVariable) {
			this.parfactor = parfactor;
			this.logicalVariable = logicalVariable;
		}
		
		@Override
		public void execute() {
			CFOVE.this.parfactors = new HashSet<Parfactor>( 
					countingConvert( 
							parfactors, parfactor, logicalVariable));
			updateVariableSets();
		}
		
		/**
		 * Removes the variable being counted from the set of 
		 * variables to eliminate and from the set of all variables.
		 */
		private void updateVariableSets() {
			CFOVE.this.getVariablesToEliminate();
		}
		
		@Override
		public String toString() {
			StringBuilder strb = new StringBuilder();
			strb.append("COUNTING-CONVERT\n")
				.append(this.parfactor.toString())
				.append("\n")
				.append(this.logicalVariable.toString());
			return strb.toString();
		}
	}
	
	private class FullExpand implements Operation {

		private Parfactor parfactor;
		private CountingFormula countingFormula;
		
		FullExpand(Parfactor parfactor, CountingFormula countingFormula) {
			this.parfactor = parfactor;
			this.countingFormula = countingFormula;
		}
		
		@Override
		public void execute() {
			Set<Parfactor> result = fullExpand(parfactors, parfactor, countingFormula);
			CFOVE.this.parfactors = new HashSet<Parfactor>(shatter(result, query));
			updateVariableSets();
		}
		
		private void updateVariableSets() {
			CFOVE.this.getVariablesToEliminate();
			// TODO need to expand the counting formula in the variable set
		}
		
		@Override
		public String toString() {
			StringBuilder strb = new StringBuilder();
			strb.append("FULL-EXPAND\n")
				.append(this.parfactor.toString())
				.append("\n")
				.append(this.countingFormula.toString());
			return strb.toString();
		}
	}
	
	private class Propositionalize implements Operation {

		private Parfactor parfactor;
		private StdLogicalVariable logicalVariable;
		
		Propositionalize(Parfactor parfactor, StdLogicalVariable logicalVariable) {
			this.parfactor = parfactor;
			this.logicalVariable = logicalVariable;
		}
		
		@Override
		public void execute() {
			Set<Parfactor> result = propositionalize(parfactors, parfactor, logicalVariable);
			CFOVE.this.parfactors = new HashSet<Parfactor>(shatter(result, query));
			updateVariableSets();
		}
		
		private void updateVariableSets() {
			CFOVE.this.getVariablesToEliminate();
			// TODO need to propositionalize the variable in the variable set
		}
		
		@Override
		public String toString() {
			StringBuilder strb = new StringBuilder();
			strb.append("PROPOSITIONALIZE\n")
				.append(this.parfactor.toString())
				.append("\n")
				.append(this.logicalVariable.toString());
			return strb.toString();
		}
	}
	
	/**
	 * Constructor.
	 * 
	 */
	public CFOVE(Set<Parfactor> parfactors, RandomVariableSet query) {
		this.parfactors = new HashSet<Parfactor>(parfactors);
		this.query = query;
		this.variablesToEliminate = new HashSet<RandomVariableSet>();
		this.lowestCost = INFINITE;
		this.maxNumEliminatedVariables = 0;
		ConsoleLogger.setup();
	}
	
	/**
	 * Run the C-FOVE algorithm for the set of parfactors and the
	 * set of random variables specified in the constructor. 
	 */
	public Parfactor run() {
		this.parfactors = new HashSet<Parfactor>(shatter(parfactors, query));
		getVariablesToEliminate();
		this.parfactors = new HashSet<Parfactor>(shatter(parfactors));
		while (thereAreVariablesToEliminate()) {
			chooseMacroOperation();
			executeMacroOperation();
			resetCurrentOperation();
		}
		if (parfactors.size() > 1)
			throw new IllegalStateException("Something is wrong. There should" 
					+ " be only one parfactor in the set. There are "
					+ parfactors.size());
		this.marginalDistribution = parfactors.iterator().next();
		return this.marginalDistribution;
	}
	
	/**
	 * Sets the set of variables to 
	 * eliminate.
	 * <br>
	 * This method should be called after shattering the set of parfactors
	 * against the set of queried random variables.
	 */
	private void getVariablesToEliminate() {
		this.variablesToEliminate.clear();
		for (Parfactor p : this.parfactors) {
			for (ParameterizedRandomVariable prv : p.getParameterizedRandomVariables()) {
				RandomVariableSet s = RandomVariableSet.getInstance(prv, p.getConstraints());
				if (prv instanceof CountingFormula
						&& this.query.isEquivalent((CountingFormula) prv)) {
					continue;
				}
				if (!s.equals(query)) {
					this.variablesToEliminate.add(s);
				}
			}
		}
	}
	
	/**
	 * Puts all random variables of all parfactors in the set of "All Random
	 * Variables".
	 * @throws IllegalStateException If the set of all random variables has
	 * not been initialized or if the set of parfactors has not been 
	 * initialized.
	 */
//	private void getAllRandomVariables() throws IllegalStateException {
//		if (this.allVariables == null) {
//			throw new IllegalStateException("The set of all random variables" 
//					+ " has not been initialized.");
//		} else if (this.parfactors == null) {
//			throw new IllegalStateException("The set of parfactors" 
//					+ " has not been initialized.");
//		} else {
//			for (Parfactor p : this.parfactors) {
//				for (ParameterizedRandomVariable prv : p.getParameterizedRandomVariables()) {
//					allVariables.add(RandomVariableSet.getInstance(prv, p.getConstraints()));
//				}
//			}
//		}
//			
//	}
	
	/**
	 * Returns true if the set of non-queried variables is not empty, false
	 * otherwise
	 * @return True if the set of non-queried variables is not empty, false
	 * otherwise
	 */
	private boolean thereAreVariablesToEliminate() {
		return !variablesToEliminate.isEmpty();
	}
	
	/**
	 * Evaluates the cost of all possible macro-operations.
	 * <br>
	 * State variables are updated as lower cost operations are found.
	 */
	private void chooseMacroOperation() {
		for (Parfactor p : parfactors) {
			for (ParameterizedRandomVariable f : p.getParameterizedRandomVariables()) { 
				if (f instanceof CountingFormula) {
					evaluateFullExpand(p, (CountingFormula) f);
				} else if (!RandomVariableSet.getInstance(f, p.getConstraints()).equals(query)) {
					evaluateGlobalSumOut(f, p.getConstraints());
				}
			}
			for (StdLogicalVariable lv : p.getLogicalVariables()) {
				evaluateCountingConvert(p, lv);
				evaluatePropositionalize(p, lv);
			}
		}
	}
	
	/**
	 * Evaluates the cost of GLOBAL-SUM-OUT macro-operation.
	 * <br>
	 * If global sum out is possible with the specified parameters and the 
	 * cost associated is lower than the current operation, then
	 * the current operation is updated.
	 * @param variableToEliminate The PRV to eliminate
	 * @param constraints A set of constraints
	 */
	private void evaluateGlobalSumOut (
			ParameterizedRandomVariable variableToEliminate, 
			Set<Constraint> constraints) {
		
		Set<ParameterizedRandomVariable> resultVariables = 
				new HashSet<ParameterizedRandomVariable>();
		HashSet<Constraint> resultConstraints = new HashSet<Constraint>();
		
		for (Parfactor parfactor : this.parfactors) {
			if (parfactor.contains(variableToEliminate) 
					&& parfactor.getConstraints().containsAll(constraints)) {
				resultVariables.addAll(parfactor.getParameterizedRandomVariables());
				resultConstraints.addAll(parfactor.getConstraints()); // TODO do i need this?
			}
		}
		if (hasAllLogicalVariables(resultVariables, variableToEliminate)) {
			int resultFactorSize = getFactorSize(resultVariables) 
								   / variableToEliminate.getRangeSize();
			int numberVariablesEliminated = variableToEliminate.getGroundSetSize(constraints);
			
			logger.info("GLOBAL-SUM-OUT( " 
					+ variableToEliminate.toString()
					+ ", "
					+ constraints.toString()
					+ ") costs "
					+ resultFactorSize);
			logger.info("GLOBAL-SUM-OUT( " 
					+ variableToEliminate.toString()
					+ ", "
					+ constraints.toString()
					+ ") eliminates "
					+ numberVariablesEliminated
					+ " random variables");
			
			if (resultFactorSize < this.lowestCost
					|| (resultFactorSize == this.lowestCost
							&& numberVariablesEliminated > this.maxNumEliminatedVariables)) { // TODO ground set?
				logger.info("Setting GLOBAL-SUM-OUT as current operation");
				this.lowestCost = resultFactorSize;
				this.maxNumEliminatedVariables = numberVariablesEliminated;
				this.currentOperation = new GlobalSumOut(variableToEliminate, constraints);
			}
		}
	}
	
	/**
	 * Returns true if the set of parameters of the specified parameterized
	 * random variable is a super set of the parameters from parameterized
	 * random variables from the specified set.
	 * @param allVariables A set of parameterized random variables
	 * @param prv The parameterized random variable to check
	 * @return True if the set of parameters of the specified parameterized
	 * random variable is a super set of the parameters from parameterized
	 * random variables from the specified set.
	 */
	private boolean hasAllLogicalVariables (
			Set<ParameterizedRandomVariable> allVariables, 
			ParameterizedRandomVariable prv) {
		for (ParameterizedRandomVariable variable : allVariables) {
			if (!prv.getParameters().containsAll(variable.getParameters())) 
				return false;
		}
		return true;
	}
	
	/**
	 * Returns the size of the factor size on the specified set of 
	 * parameterized random variables.
	 * @param prvs A set of parameterized random variables that compose the
	 * factor to be built
	 * @return The size of the factor size on the specified set of 
	 * parameterized random variables.
	 * @throws IllegalArgumentException If the specified set is empty or null
	 */
	private int getFactorSize(Set<ParameterizedRandomVariable> prvs) 
			throws IllegalArgumentException {
		
		if (prvs == null || prvs.size() == 0)
			throw new IllegalArgumentException("The set of PRVs is empty or null");
		int size = 1;
		for (ParameterizedRandomVariable prv : prvs) {
			size = size * prv.getRangeSize();
		}
		return size;
	}
	
	/**
	 * Evaluates the cost of FULL-EXPAND macro-operation.
	 * <br>
	 * It is always possible to full expand a counting formula (given that
	 * it exists in the specified parfactor), thus the current operation is
	 * updated only if the cost associated with the operation is lower.
	 * @param parfactor The parfactor on which expansion will be made
	 * @param cf The counting formula to expand
	 */
	private void evaluateFullExpand(Parfactor parfactor, CountingFormula cf) {
		int resultFactorSize = parfactor.getFactor().size() / cf.getRangeSize();
		for (int i = 0; 
			 i < cf.getBoundVariable()
			 	   .individualsSatisfying(cf.getConstraints()).size(); 
			 i++) {
			resultFactorSize = resultFactorSize * cf.getRangeSize();
		}
		
		logger.info("FULL-EXPAND(" + cf.toString() + ") costs " + resultFactorSize);
		
		if (resultFactorSize < this.lowestCost) {
			logger.info("Setting FULL-EXPAND as current operation");
			this.lowestCost = resultFactorSize;
			this.currentOperation = new FullExpand(parfactor, cf);
		}
	}
	
	/**
	 * Evaluates the cost of COUNTING-CONVERT macro-operation.
	 * <br>
	 * If counting is possible with the specified parameters and the 
	 * cost associated is lower than the current operation, then
	 * the current operation is updated.
	 * 
	 * @param parfactor The parfactor on which the operation will be performed
	 * @param lv The logical variable to eliminate using counting
	 */
	private void evaluateCountingConvert(Parfactor parfactor, StdLogicalVariable lv) {
		if (parfactor.getFactor().isUnique(lv)) {
			ParameterizedRandomVariable variableToCount = 
					parfactor.getFactor().getVariableToCount(lv);
			int resultFactorSize = parfactor.getFactor().size() 
			                       / variableToCount.getRangeSize() 
			                       * getNumberOfHistograms(
			                    		   parfactor.getConstraints(), 
			                    		   variableToCount, 
			                    		   lv);
			
			logger.info("COUNTING-CONVERT( "
					+ lv.toString()
					+ " ) costs "
					+ resultFactorSize);
			
			if (resultFactorSize < this.lowestCost) {
				logger.info("Setting COUNTING-CONVERT as current operation");
				this.lowestCost = resultFactorSize;
				this.currentOperation = new CountingConvert(parfactor, lv);
			}
		}
	}
	
	/**
	 * Returns the number of histograms created when converting the specified  
	 * standard parameterized random variable to a counting formula.
	 * @param constraints A set of constraints
	 * @param prv The parameterized random variable to associate to the
	 * counting formula
	 * @param lv The bound logical variable
	 * @return the number of histograms created when converting the specified 
	 * standard parameterized random variable to a counting formula.
	 */
	private int getNumberOfHistograms (
			Set<Constraint> constraints, 
			ParameterizedRandomVariable prv, 
			StdLogicalVariable lv) {
		return MathUtils.combination(
				lv.individualsSatisfying(constraints).size()
					+ prv.getRangeSize() - 1, 
				prv.getRangeSize() - 1);
	}
	
	/**
	 * Evaluates the cost of PROPOSITIONALIZE macro-operation.
	 * <br>
	 * It is always possible to propositionalize a PRV (given that
	 * it exists in the specified parfactor), thus the current operation is
	 * updated only if the cost associated with the operation is lower.
	 * @param parfactor The parfactor on which propositionalization will be made
	 * @param lv The logical variable to propositionalizes
	 */
	private void evaluatePropositionalize(Parfactor parfactor, StdLogicalVariable lv) {
		int resultFactorSize = 
				lv.individualsSatisfying(parfactor.getConstraints()).size()
				* parfactor.getFactor().size();
		
		logger.info("PROPOSITIONALIZE( "
				+ lv.toString()
				+ " ) costs "
				+ resultFactorSize);
		
		if (resultFactorSize < this.lowestCost) {
			logger.info("Setting PROPOSITIONALIZE as current operation");
			this.lowestCost = resultFactorSize;
			this.currentOperation = new Propositionalize(parfactor, lv);
		}
	}
	
	/**
	 * Executes the current macro operation.
	 */
	private void executeMacroOperation() {
		logger.info("Executing operation: " + this.currentOperation.toString());
		this.currentOperation.execute();
		logger.info("Result: \n" + parfactors);
	}
	
	/**
	 * Resets the current Operation and the cost associated with it.
	 */
	private void resetCurrentOperation() {
		this.currentOperation = null;
		this.lowestCost = INFINITE;
		this.maxNumEliminatedVariables = 0;
	}
	
	// Debug only
	public void runStep(Set<Parfactor> parfactors) {
		for (Parfactor p : parfactors) {
			
			logger.info("Evaluating parfactor \n" + p.toString());
			
			for (ParameterizedRandomVariable f : p.getParameterizedRandomVariables()) { 
				if (f instanceof CountingFormula) {
					evaluateFullExpand(p, (CountingFormula) f);
				} else if (!RandomVariableSet.getInstance(f, p.getConstraints()).equals(query)) {
					evaluateGlobalSumOut(f, p.getConstraints());
				}
			}
			for (StdLogicalVariable lv : p.getLogicalVariables()) {
				evaluateCountingConvert(p, lv);
				evaluatePropositionalize(p, lv);
			}
		}
		//executeMacroOperation();
		//return this.parfactors;
	}
	
	// Debug only
	public Set<Parfactor> executeStep(Set<Parfactor> parfactor) {
		runStep(parfactor);
		executeMacroOperation();
		return this.parfactors;
	}
	
	/**
	 * Returns the current operation as a String
	 * It may not be the operation to be 
	 * executed, it is just the cheapest operation found at the point this
	 * function is called.
	 * @return The current operation.
	 */
	String getOperationParameters() {
		return this.currentOperation.toString();
	}
}
