package br.usp.poli.takiyama.common;

import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

/**
 * This class represents constraints of the form X ? Y, where
 * X and Y are {@link Term}s and ? is an comparison operator. For now,
 * only operators '=' and '&ne;' are supported. 
 * <p>
 * A note concerning {@link #equals}: constraints X ? Y and Y ? X should
 * be considered the same, unless '?' is not symmetric.
 * </p>
 * 
 * @author Felipe Takiyama
 *
 */
public interface Constraint {
	
	/**
	 * Returns the left-hand side of this constraint.
	 * 
	 * @return the left-hand side of this constraint.
	 */
	public Term firstTerm();
	
	
	/**
	 * Returns the right-hand side of this constraint.
	 * 
	 * @return the right-hand side of this constraint.
	 */
	public Term secondTerm();
	
	
	/**
	 * Applies the substitution in this constraint.
	 * <p>
	 * If the resulting constraint is always false (for instance, q &ne; q), 
	 * then throws an {@link IllegalStateException}.
	 * </p>
	 * <p>
	 * If the specified substitution does not apply to this constraint, then 
	 * returns this constraint unchanged.
	 * </p>
	 * 
	 * @param s The substitution to apply on the constraint
	 * @return The constraint that results from the application of the
	 * specified substitution to this constraint, following the rules 
	 * specified above.
	 * @throws IllegalStateException If the resulting constraint is always
	 * false
	 */
	public Constraint apply(Substitution s) throws IllegalStateException;
	
		
	/**
	 * Returs <code>true</code> if the specified term is in the constraint
	 * 
	 * @param t The term to check the existence
	 * @return <code>true</code> if the term specified equals one of the 
	 * terms in this constraint, <code>false</code>.
	 */
	public boolean contains(Term t);
	
	
	/**
	 * Returns <code>true</code> if this constraint and the specified 
	 * constraint have a common term.
	 * 
	 * @param c The constraint to compare to.
	 * @return <code>true</code> if the constraints have a common term, 
	 * <code>false</code> otherwise.
	 */
	public boolean hasCommonTerm(Constraint c);
	
	
	/**
	 * Returns <code>true</code> if this constraint is consistent with the
	 * specified {@link Binding}. A constraint is consistent with a binding if
	 * the resulting constraint after applying the binding is a valid one.
	 * <p>
	 * This method also returns <code>false</code> when it is not possible to
	 * evaluate whether the resulting constraint is valid or not.
	 * </p> 
	 * <p>
	 * For instance, let X, Y, W and Z be logical variables with D(X) = D(Y) = 
	 * D(W) = D(Z) = {a, b,..., z}. Then:
	 * <li> X != a is consistent with X/b
	 * <li> X != Y is consistent with W/Z
	 * <li> X != a is not consistent with X/W
	 * <li> X != Y is not consistent with X/W
	 * <li> X == Y is consistent with W/Z
	 * <li> X == a is not consistent with X/a
	 * <li> X == a is not consistent with X/b
	 * <li> X == a is not consistent with X/W
	 * <li> X == Y is not consistent with X/W
	 * </p>
	 * 
	 * @param b The binding to test
	 * @return <code>true</code> if this constraint is consistent with the
	 * specified binding, <code>false</code> otherwise.
	 */
	public boolean isConsistentWith(Binding b);
	
	
	
	/**
	 * Returns the {@link Binding} corresponding to this constraint. That is,
	 * if this constraint is t1 &ne; t2, then this method returns the
	 * binding t1/t2.
	 * 
	 * @return The Binding corresponding to this constraint.
	 * @throws IllegalArgumentException If the first term is not a Logical
	 * Variable. In this case it is not possible to create the binding.
	 */
	public Binding toBinding();
	
	
	/**
	 * Returns the {@link Binding} obtained by inverting the terms of this 
	 * constraint.
	 * That is, if this constraint is t2 &ne; t2, then this method returns
	 * the binding t2/t1.
	 * 
	 * @return The inverse binding corresponding to this constraint.
	 * @throws IllegalArgumentException If the second term is not a Logical
	 * Variable. In this case it is not possible to create the binding.
	 */
	public Binding toInverseBinding();

	
	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();
	
	@Override
	public String toString();
}
