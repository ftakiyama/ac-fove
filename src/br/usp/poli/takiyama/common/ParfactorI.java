package br.usp.poli.takiyama.common;

import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.OldCountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

/**
 * A parfactor represents a set of {@link Factor}s. This set is obtained by 
 * applying
 * ground substitutions satisfying constraints to all logical variables 
 * present in the parameterized random variables of the parfactor.
 *  * 
 * @author Felipe Takiyama
 *
 */
public interface ParfactorI {
	
	/**
	 * Checks if the specified variable exists in this parfactor.
	 * 
	 * @param variable The parameterized random variable to check
	 * @return True if the specified variable exists in this parfactor,
	 * false otherwise.
	 */
	public boolean contains(ParameterizedRandomVariable variable); // I think I will use it on global sum out.
		
	/**
	 * @deprecated
	 * Returns the parameterized factor associated with this parfactor
	 * 
	 * @return The parameterized factor associated with this parfactor
	 */
	public ParameterizedFactor getFactor();
	
	/**
	 * Returns the parameterized factor associated with this parfactor
	 * 
	 * @return The parameterized factor associated with this parfactor
	 */
	public ParameterizedFactor factor();
	
	/**
	 * 
	 * @return
	 */
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables();
	
	/**
	 * Returns the child parameterized random variable of this parfactor. This
	 * method is applicable only for aggregation parfactors. If the parfactor
	 * is not an aggregation parfactor, this method should return null.
	 * <br>
	 * Not the best solution, but useful as a first solution.
	 * TODO: put aggregation methods into another interface
	 * 
	 * @return The child parameterized random variable of the parfactor.
	 */
	public ParameterizedRandomVariable getChildVariable();
	
	/**
	 * @deprecated
	 * Returns the set of all constraints in this parfactor.
	 * 
	 * @return The set of all constraints in this parfactor.
	 */
	public Set<Constraint> getConstraints();
	
	/**
	 * Returns the set of all constraints in this parfactor.
	 * 
	 * @return The set of all constraints in this parfactor.
	 */
	public Set<Constraint> constraints();
	
	/**
	 * @deprecated
	 * Returns the set of all logical variables in parameterized random
	 * variables in this parfactor.
	 * 
	 * @return The set of all logical variables in parameterized random
	 * variables in this parfactor.
	 */
	public Set<StdLogicalVariable> getLogicalVariables();
	
	/**
	 * Returns the set of all logical variables in parameterized random
	 * variables in this parfactor.
	 * 
	 * @return The set of all logical variables in parameterized random
	 * variables in this parfactor.
	 */
	public Set<StdLogicalVariable> logicalVariables();
	
	/**
	 * Retunrs the number of factors this parfactor represents.
	 * 
	 * @return The number of factors this parfactor represents.
	 */
	public int size();
	
	/**
	 * Returns true if the parfactor is constant, that is, the neutral
	 * term in multiplication.
	 * 
	 * @return True if the parfactor is constant, false otherwise.
	 */
	public boolean isConstant();
	
	
	/* ************************************************************************
	 *   ENABLING OPERATIONS
	 * ************************************************************************/
	
	public List<ParfactorI> split(Binding s);
	public ParfactorI count(LogicalVariable lv);
	public Set<ParfactorI> propositionalize(LogicalVariable lv);
	public ParfactorI expand(OldCountingFormula countingFormula, Term term);
	public ParfactorI fullExpand(OldCountingFormula countingFormula);
	public ParfactorI multiply(ParfactorI parfactor);
	public ParfactorI sumOut(ParameterizedRandomVariable prv);
	public List<ParfactorI> splitOnConstraints(Set<Constraint> constraints);
	
	
	/* Unification */
	
	/**
	 * Restores the names of all logical variables in the parfactor that
	 * were changed using {@link LogicalVariableNameGenerator}.
	 * @return A new instance of this parfactor, with all logical variables
	 * restored to their old names.
	 */
	public ParfactorI restoreLogicalVariableNames();
	
	public ParfactorI replaceLogicalVariablesConstrainedToSingleConstant();
	public ParfactorI renameLogicalVariables();
	public List<ParfactorI> splitOnMgu(Substitution mgu);
	
	public Set<ParfactorI> unify(ParfactorI parfactor);
	
}
