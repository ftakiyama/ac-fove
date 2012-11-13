//package br.usp.poli.takiyama.acfove;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import br.usp.poli.takiyama.cfove.MacroOperations;
//import br.usp.poli.takiyama.cfove.SimpleParfactor;
//import br.usp.poli.takiyama.common.Constraint;
//import br.usp.poli.takiyama.prv.LogicalVariable;
//import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
//
///**
// * This class is used to evaluate and execute <code>MacroOperations</code>.
// * The evaluation of the cost of a macro operation is based on the size
// * of the factor the operation creates and the number of random variables
// * eliminated by the operation. 
// * <br>
// * As the GLOBAL-SUM-OUT operations always reduce the size of the parfactors,
// * the cost of this operation tend to be the smallest. The number of random
// * variables criteria is used only for tie-breaking. If the draw persists, the
// * first operation evaluated with the smallest cost is chosen.
// * <br>
// * This class stores all the arguments necessary to execute the given operation,
// * and only those necessary to execute it.
// * 
// * @author ftakiyama
// *
// */
//public class Evaluator {
//	private SimpleParfactor parfactor;
//	private HashSet<SimpleParfactor> setOfParfactors;
//	private HashSet<Constraint> setOfConstraints;
//	private ParameterizedRandomVariable parameterizedRandomVariable;
//	private LogicalVariable logicalVariable;
//	
//	
//	// cost metrics
//	private int createdParfactorSize;
//	private int numberOfVariablesEliminated; 
//	
//	private MacroOperation currentOperation;
//	
//	public static enum MacroOperation {GLOBAL_SUM_OUT, COUNTING_CONVERT, 
//		PROPOSITIONALIZE, FULL_EXPAND};
//	
//	// TODO: change it later
//	public Evaluator() {
//		this.parfactor = null;
//		this.setOfParfactors = new HashSet<SimpleParfactor>();
//		this.setOfConstraints = new HashSet<Constraint>();
//		this.parameterizedRandomVariable = null;
//		this.logicalVariable = null;
//		
//		this.createdParfactorSize = Integer.MAX_VALUE;
//		this.numberOfVariablesEliminated = 0;
//		
//		this.currentOperation = null;
//	}
//	
//	private void clear() {
//		this.parfactor = null;
//		this.setOfParfactors.clear();
//		this.setOfConstraints.clear();
//		this.parameterizedRandomVariable = null;
//		this.logicalVariable = null;
//		
//		this.createdParfactorSize = Integer.MAX_VALUE;
//		this.numberOfVariablesEliminated = 0;
//
//		this.currentOperation = null;
//	}
//	
//	public ParameterizedRandomVariable getVariable() {
//		return this.parameterizedRandomVariable;
//	}
// 	
//	/**
//	 * Checks the cost of GLOBAL-SUM-OUT.
//	 * <br>
//	 * If this operation is possible, evaluates the cost of the operation and
//	 * stores the relevant data if the cost is smaller than the previous
//	 * costs.
//	 * @param parfactors A set of parfactors
//	 * @param prv The parameterized random varible to be eliminated from the
//	 * set of parfactors.
//	 * @param constraints A set of constraints.
//	 */
//	public void checkCostOfGlobalSumOut(Set<SimpleParfactor> parfactors, ParameterizedRandomVariable prv, Set<Constraint> constraints) {
//		//TODO: implement
//		/*
//		 * if is possible to do this operation
//		 *     calculate the cost
//		 * else
//		 *     do nothing
//		 */
//		
//		if (MacroOperations.conditionsForGlobalSumOutAreMet(parfactors, prv, constraints)) {
//			int size = 1;
//			HashSet<ParameterizedRandomVariable> variables = new HashSet<ParameterizedRandomVariable>();
//			for (SimpleParfactor parfactor : parfactors) {
//				if (parfactor.contains(prv)) {
//					variables.addAll(parfactor.getParameterizedRandomVariables());
//				}
//			}
//			for (ParameterizedRandomVariable variable : variables) {
//				size *= variable.getRangeSize();
//			}
//			size = size / prv.getRangeSize();
//			
//			if (size < createdParfactorSize ||
//					(size == createdParfactorSize && 
//							prv.getGroundInstancesSatisfying(constraints).size() > numberOfVariablesEliminated)) {
//				this.currentOperation = Evaluator.MacroOperation.GLOBAL_SUM_OUT;
//				this.setOfParfactors = new HashSet<SimpleParfactor>(parfactors);
//				this.parameterizedRandomVariable = prv;
//				this.setOfConstraints = new HashSet<Constraint>(constraints);
//				this.createdParfactorSize = size;
//				this.numberOfVariablesEliminated = prv.getGroundInstancesSatisfying(constraints).size();
//			} 
//		}
//	}
//	
//	public void checkCostOfCountingConvert(Set<SimpleParfactor> parfactors, SimpleParfactor p, LogicalVariable logicalVariable) {
//		//TODO: implement
//	}
//	
//	public void checkCostOfPropositionalize(Set<SimpleParfactor> parfactors, SimpleParfactor p, LogicalVariable logicalVariable) {
//		//TODO: implement
//	}
//	
//	public void checkCostOfFullExpand(Set<SimpleParfactor> parfactors, SimpleParfactor p /*, CountingFormula cf*/) {
//		//TODO: implement
//	}
//	
//	public Set<SimpleParfactor> executeCurrentOperation() {
//		if (currentOperation.equals(Evaluator.MacroOperation.GLOBAL_SUM_OUT)) {
//			HashSet<SimpleParfactor> result = new HashSet<SimpleParfactor>(MacroOperations.globalSumOut(setOfParfactors, parameterizedRandomVariable, setOfConstraints));
//			this.clear();
//			return result;
//		} else if (currentOperation.equals(Evaluator.MacroOperation.COUNTING_CONVERT)) {
//			
//		} else if (currentOperation.equals(Evaluator.MacroOperation.PROPOSITIONALIZE)) {
//			
//		} else if (currentOperation.equals(Evaluator.MacroOperation.FULL_EXPAND)) {
//			
//		} else {
//			// throw exception?
//			return new HashSet<SimpleParfactor>();
//		}
//		return null;
//	}
//	
//	@Override
//	public String toString() {
//		if (currentOperation == null) 
//			return "No current operation";
//		
//		if (currentOperation.equals(Evaluator.MacroOperation.GLOBAL_SUM_OUT)) {
////			String prv = parameterizedRandomVariable != null ? parameterizedRandomVariable.toString() : "";
////			String constraints = setOfConstraints != null ? setOfConstraints.toString() : "";
//			return Evaluator.MacroOperation.GLOBAL_SUM_OUT.toString() + ": " + parameterizedRandomVariable.toString() + ", " + setOfConstraints.toString();
//		} else if (currentOperation.equals(Evaluator.MacroOperation.COUNTING_CONVERT)) {
//			
//		} else if (currentOperation.equals(Evaluator.MacroOperation.PROPOSITIONALIZE)) {
//			
//		} else if (currentOperation.equals(Evaluator.MacroOperation.FULL_EXPAND)) {
//			
//		} else {
//			// throw exception?
//			return "No current operation";
//		}
//		return "No current operation"; 
//	}
//}
