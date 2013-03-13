package br.usp.poli.takiyama.common;

import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

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
		
	public ParameterizedFactor getFactor();
	
	// should i enforce getPRV and getConstraints?
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables();
	
	/**
	 * Returns the child parameterized random variable of this parfactor. This
	 * method is applicable only for aggregation parfactors. If the parfactor
	 * is not an aggregation parfactor, this method should return null.
	 * <br>
	 * Not the best solution, but useful as a first solution.
	 * TODO: put aggregation methods into another interface
	 * @return The child parameterized random variable of the parfactor.
	 */
	public ParameterizedRandomVariable getChildVariable();
	
	public Set<Constraint> getConstraints();
	
	public Set<LogicalVariable> getLogicalVariables();
	
	/**
	 * Retunrs the number of factors this parfactor represents.
	 * @return The number of factors this parfactor represents.
	 */
	public int size();
	
	/**
	 * Returns true if the parfactor is constant, that is, the neutral
	 * term in multiplication.
	 * @return True if the parfactor is constant, false otherwise.
	 */
	public boolean isConstant();
	
	
	/* ************************************************************************
	 *   ENABLING OPERATIONS
	 * ************************************************************************/
	
	public List<Parfactor> split(Binding s);
	public Parfactor count(LogicalVariable lv);
	public Set<Parfactor> propositionalize(LogicalVariable lv);
	public Parfactor expand(CountingFormula countingFormula, Term term);
	public Parfactor fullExpand(CountingFormula countingFormula);
	public Parfactor multiply(Parfactor parfactor);
	public Parfactor sumOut(ParameterizedRandomVariable prv);
	public List<Parfactor> splitOnConstraints(Set<Constraint> constraints);
	
	
	/* Unification */
	
	/**
	 * Restores the names of all logical variables in the parfactor that
	 * were changed using {@link LogicalVariableNameGenerator}.
	 * @return A new instance of this parfactor, with all logical variables
	 * restored to their old names.
	 */
	public Parfactor restoreLogicalVariableNames();
	
	public Parfactor replaceLogicalVariablesConstrainedToSingleConstant();
	public Parfactor renameLogicalVariables();
	public List<Parfactor> splitOnMgu(Substitution mgu);
	
	public Set<Parfactor> unify(Parfactor parfactor);
	
}
