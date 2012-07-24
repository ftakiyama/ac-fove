package br.usp.poli.takiyama.common;

import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.cfove.ParameterizedFactor;
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
	
	public Set<Parfactor> sumOut(Set<Parfactor> setOfParfactors, ParameterizedRandomVariable variable);
	
	public Set<Parfactor> multiply(Set<Parfactor> setOfParfactors, Parfactor parfactor);
	
	public ParameterizedFactor getFactor();
	
	// should i enforce getPRV and getConstraints?
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables();
	
	public Set<Constraint> getConstraints();
}
