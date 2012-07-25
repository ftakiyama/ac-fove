package br.usp.poli.takiyama.acfove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.cfove.MacroOperations;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

/**
 * This class is used to evaluate and execute <code>MacroOperations</code>.
 * The evaluation of the cost of a macro operation is based on the size
 * of the factor the operation creates and the number of random variables
 * eliminated by the operation. 
 * <br>
 * As the GLOBAL-SUM-OUT operations always reduce the size of the parfactors,
 * the cost of this operation tend to be the smallest. The number of random
 * variables criteria is used only to tie-breaking. If the draw persists, the
 * first operation with the smallest cost is chosen.
 * <br>
 * This class stores all the arguments necessary to execute the given operation,
 * and only those necessary to execute it.
 * 
 * @author ftakiyama
 *
 */
public class Operation {
	private Parfactor parfactor;
	private HashSet<Parfactor> setOfParfactors;
	private HashSet<Constraint> setOfConstraints;
	private ParameterizedRandomVariable parameterizedRandomVariable;
	private LogicalVariable logicalVariable;
	
	
	// cost metrics
	private int createdParfactorSize;
	private int numberOfVariablesEliminated; 
	
	private MacroOperation currentOperation;
	
	public static enum MacroOperation {GLOBAL_SUM_OUT, COUNTING_CONVERT, 
		PROPOSITIONALIZE, FULL_EXPAND};
	
	// TODO: change it later
	public Operation() {
		this.parfactor = null;
		this.setOfParfactors = new HashSet<Parfactor>();
		this.setOfConstraints = new HashSet<Constraint>();
		this.parameterizedRandomVariable = null;
		this.logicalVariable = null;
		
		this.createdParfactorSize = Integer.MAX_VALUE;
		this.numberOfVariablesEliminated = 0;
	}
	
	public void checkCostOfGlobalSumOut(Set<Parfactor> parfactors, ParameterizedRandomVariable prv, Set<Constraint> constraints) {
		//TODO: implement
		/*
		 * if is possible to do this operation
		 *     calculate the cost
		 * else
		 *     do nothing
		 */
		
		if (MacroOperations.conditionsForGlobalSumOutAreMet(parfactors, prv, constraints)) {
			int size = 1;
			HashSet<ParameterizedRandomVariable> variables = new HashSet<ParameterizedRandomVariable>();
			for (Parfactor parfactor : parfactors) {
				if (parfactor.contains(prv)) {
					variables.addAll(parfactor.getParameterizedRandomVariables());
				}
			}
			for (ParameterizedRandomVariable variable : variables) {
				size *= variable.getRangeSize();
			}
			size = size / prv.getRangeSize();
			
			if (size < createdParfactorSize ||
					(size == createdParfactorSize && 
							prv.getGroundInstancesSatisfying(constraints).size() > numberOfVariablesEliminated)) {
				this.currentOperation = Operation.MacroOperation.GLOBAL_SUM_OUT;
				this.setOfParfactors = new HashSet<Parfactor>(parfactors);
				this.parameterizedRandomVariable = prv;
				this.setOfConstraints = new HashSet<Constraint>(constraints);
			} 
		}
	}
	
	public void checkCostOfCountingConvert(Set<Parfactor> parfactors, Parfactor p, LogicalVariable logicalVariable) {
		//TODO: implement
	}
	
	public void checkCostOfPropositionalize(Set<Parfactor> parfactors, Parfactor p, LogicalVariable logicalVariable) {
		//TODO: implement
	}
	
	public void checkCostOfFullExpand(Set<Parfactor> parfactors, Parfactor p /*, CountingFormula cf*/) {
		//TODO: implement
	}
	
	public Set<Parfactor> executeCurrentOperation() {
		if (currentOperation.equals(Operation.MacroOperation.GLOBAL_SUM_OUT)) {
			return MacroOperations.globalSumOut(setOfParfactors, parameterizedRandomVariable, setOfConstraints);
		} else if (currentOperation.equals(Operation.MacroOperation.COUNTING_CONVERT)) {
			
		} else if (currentOperation.equals(Operation.MacroOperation.PROPOSITIONALIZE)) {
			
		} else if (currentOperation.equals(Operation.MacroOperation.FULL_EXPAND)) {
			
		} else {
			// throw exception?
		}
		return null;
	}
}
