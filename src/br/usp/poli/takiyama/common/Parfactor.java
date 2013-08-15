package br.usp.poli.takiyama.common;

import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.cfove.StdParfactor;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Replaceable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

public interface Parfactor extends VisitableParfactor, Replaceable<Parfactor> { // or it is the other way around? 
	
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
	
	
	/**
	 * Returns the set of all {@link Prv} in this parfactor.
	 * 
	 * @return the set of all PRVs in this parfactor.
	 */
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
	 * be expanded on the specified substitution.
	 * 
	 * @param cf The PRV to be expanded
	 * @param s The substitution to expand the counting formula on
	 * @return <code>true</code> if the specified PRV can
	 * be expanded on the specified term, <code>false</code> otherwise
	 */
	public boolean isExpandable(Prv cf, Substitution s);
	
	
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
	
	
	/*
	 * There is no method to check whether a PRV can be eliminated (summed
	 * out) from this parfactor because this verification requires a full
	 * scan of all parfactors in the distribution. In this special case,
	 * the verification must be done by a higher level class.
	 * 
	 * BUT
	 * If I assume that the marginal has been SHATTERED, then I only need
	 * to check some parfactor-level conditions. So, here is an important
	 * assumption: the marginal has been SHATTERED before calling this 
	 * method. Use at your own risk ;P
	 */
	
	/**
	 * Returns <code>true</code> if the specified PRV can be eliminated from
	 * this parfactor.
	 * 
	 * @param prv The PRV to eliminate
	 * @return <code>true</code> if the specified PRV can be eliminated from
	 * this parfactor, <code>false</code> otherwise.
	 */
	public boolean isEliminable(Prv prv);
	
	// Enabling operations
	
	/**
	 * Returns the result of eliminating the specified free logical variable 
	 * from this parfactor. 
	 * 
	 * @param lv The logical variable to eliminate
	 */
	public Parfactor count(LogicalVariable lv);
	
	
	/**
	 * Returns the result of expanding the specified counting formula on the
	 * specified term in this parfactor. 
	 * 
	 * @param cf The counting formula to expand
	 * @param t The term on which to expand the counting formula. It is 
	 * usually a {@link Constant}.
	 * @return The result of expanding the specified counting formula on the
	 * specified term in this parfactor. 
	 */
	public Parfactor expand(Prv cf, Term t);
	
	
	/**
	 * Returns the result of multiplying this parfactor with the specified
	 * parfactor.
	 * 
	 * @param other The parfactor to multiply by
	 * @return The result of multiplying this parfactor with the specified
	 * parfactor
	 */
	public Parfactor multiply(Parfactor other);
	
	
	/**
	 * Returns the result of multiplying this parfactor with the specified
	 * parfactor. This method is a complement to {@link #multiply(Parfactor)},
	 * when its caller is a {@link StdParfactor}.
	 * 
	 * @param other The parfactor to multiply by
	 * @return The result of multiplying this parfactor with the specified
	 * parfactor, if the specified parfactor is a {@link StdParfactor}.
	 */
	public Parfactor multiplicationHelper(Parfactor other);
	
		
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
	
	
	/**
	 * Eliminates the specified {@link Prv} and returns the result.
	 * 
	 * @param prv The PRV to eliminate
	 * @return The result of eliminating the specified PRV from this parfactor
	 */
	public Parfactor sumOut(Prv prv);
	
	
	// Unification
	
//	public Parfactor renameLogicalVariables();
//	
//	public Parfactor restoreLogicalVariables();
//	
	
	public Parfactor simplifyLogicalVariables();
		
	@Override
	public boolean equals(Object o);

	@Override
	public int hashCode();
	
	@Override
	public String toString();
	
}
