package br.usp.poli.takiyama.common;

import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

public interface Parfactor {
	
	// Getters
	
	/**
	 * Returns the set of all constraints in this parfactor.
	 * 
	 * @return The set of all constraints in this parfactor.
	 */
	public Set<Constraint> constraints();
	
	
	/**
	 * Returns the {@link Factor} associated with this parfactor
	 * 
	 * @return The factor associated with this parfactor
	 */
	public Factor factor();
	
	
	/**
	 * Returns the set of all logical variables in parameterized random
	 * variables from this parfactor.
	 * 
	 * @return The set of all logical variables in parameterized random
	 * variables from this parfactor.
	 */
	public Set<LogicalVariable> logicalVariables();
	
	public List<Prv> prvs();
	
	
	/**
	 * Retunrs the number of factors this parfactor represents.
	 * 
	 * @return The number of factors this parfactor represents.
	 */
	public int size();
	
	
	/**
	 * Returns the result of applying the specified substitution to this
	 * Parfactor. The substitution is applied to PRVs and constraints.
	 * 
	 * @param s The substitution to apply
	 * @return The result of applying the specified substitution to this
	 * Parfactor
	 */
	public Parfactor apply(Substitution s);
	
	
	/**
	 * Returns <code>true</code> if the specified {@link Prv} exists in this 
	 * parfactor.
	 * 
	 * @param prv The parameterized random variable to search
	 * @return <code>true</code> if the specified {@link Prv} exists in this 
	 * parfactor, false otherwise.
	 */
	public boolean contains(Prv prv);
	
	
	// Status check
	
	/**
	 * Returns <code>true</code> if the parfactor is constant (the neutral
	 * term in multiplication).
	 * 
	 * @return <code>true</code> if the parfactor is constant, 
	 * <code>false</code> otherwise.
	 */
	public boolean isConstant();
	
	
	/**
	 * Returns <code>true</code> if the specified logical variable can be 
	 * counted in this parfactor, <code>false</code> otherwise.
	 * <p>
	 * A logical variable is 'countable' when it occurs free in only one 
	 * parameterized random variable in the parfactor.
	 * </p>
	 * 
	 * @param lv The logical variable to test for countability.
	 * @return <code>true</code> if the specified logical variable can be 
	 * counted in this parfactor, <code>false</code> otherwise.
	 */
	public boolean isCountable(LogicalVariable lv);
	
	
	/**
	 * Returns <code>true</code> if the specified {@link Prv} can
	 * be expanded on the specified term.
	 * 
	 * @param cf The PRV to be expanded
	 * @param t The term to expand the counting formula on
	 * @return <code>true</code> if the specified PRV can
	 * be expanded on the specified term, <code>false</code> otherwise
	 */
	public boolean isExpandable(Prv cf, Term t);
	
	
	/**
	 * Returns <code>true</code> if this parfactor can be multiplied by the
	 * specified parfactor.
	 * 
	 * @param other The parfactor to multiply with
	 * @return <code>true</code> if this parfactor can be multiplied by the
	 * specified parfactor, <code>false</code> otherwise
	 */
	public boolean isMultipliable(Parfactor other);
	
	
	/**
	 * Returns <code>true</code> if this parfactor can be split on the
	 * specified substitution.
	 * 
	 * @param s The substitution to split this parfactor on
	 * @return <code>true</code> if this parfactor can be split on the
	 * specified substitution, <code>false</code> otherwise.
	 */
	public boolean isSplittable(Substitution s);
	
	
	// Enabling operations
	
	public Parfactor count(LogicalVariable lv);
	
	public Parfactor expand(Prv cf, Term t);
	
	public Parfactor multiply(Parfactor other);
	
	//public Parfactor propositionalize();
	
	/**
	 * Splits this parfactor on the specified substitution.
	 * 
	 * @param s The substitution upon which this parfactor is to be split
	 * @return The result of splitting this parfactor on the specified
	 * substitution.
	 * @throws IllegalArgumentException If this parfactor is not splittable on
	 * the specified substitution.
	 */
	public SplitResult splitOn(Substitution s) throws IllegalArgumentException;
	
	public Parfactor sumOut(Prv prv);
	
	
	// Unification
	
//	public Parfactor renameLogicalVariables();
//	
//	public Parfactor restoreLogicalVariables();
//	
//	public Parfactor simplifyLogicalVariables();
		
	@Override
	public boolean equals(Object o);

	@Override
	public int hashCode();
	
	@Override
	public String toString();
	
}
