package br.usp.poli.takiyama.common;

import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

/**
 * A parfactor represents a set of factors. This set is obtained by applying
 * ground substitutions satisfying constraints to all logical variables 
 * present in the parameterized random variables of the parfactor.
 * <br>
 * <br>
 * The <code>Parfactor</code> interface enforces the implementation of the 
 * following basic operations: sum out and multiplication. 
 * 
 * @author ftakiyama
 *
 */
public interface Parfactor {
	
	public boolean contains(ParameterizedRandomVariable variable); // I think I will use it on global sum out.
	
	//public Set<Parfactor> sumOut(Set<Parfactor> setOfParfactors, ParameterizedRandomVariable variable);
	
	//public boolean canBeMultipliedBy(Parfactor parfactor);
	
	//public Set<Parfactor> multiply(Set<Parfactor> setOfParfactors, Parfactor parfactor);
	
	public ParameterizedFactor getFactor();
	
	// should i enforce getPRV and getConstraints?
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables();
	
	/**
	 * Returns the child parameterized random variable of this parfactor. This
	 * method is applicable only for aggregation parfactors. If the parfactor
	 * is not an aggregation parfactor, this method should return null.
	 * <br>
	 * Not the best solution, but useful as a first solution.
	 * @return The child parameterized random variable of the parfactor.
	 */
	public ParameterizedRandomVariable getChildVariable();
	
	public Set<Constraint> getConstraints();
	
	
	/* ************************************************************************
	 *   ENABLING OPERATIONS
	 * ************************************************************************/
	
	public Set<Parfactor> split(Binding substitution);
}
