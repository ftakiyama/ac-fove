package br.usp.poli.takiyama.common;

import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Replaceable;
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
public interface Constraint extends Replaceable<Constraint> {
	
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
	 * then throws an {@link IllegalArgumentException}.
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
	public Constraint apply(Substitution s) throws IllegalArgumentException;
	
		
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
	 * Returns <code>true</code> if the specified {@link Binding} satisfies this
	 * constraint. In other words, returns <code>true</code> if applying
	 * the specified binding to this constraint results in a valid sentence.
	 * <p>
	 * This method also returns <code>false</code> when it is not possible to
	 * evaluate whether the resulting constraint is valid or not.
	 * </p> 
	 * <p>
	 * For instance, let X, Y, W and Z be logical variables with D(X) = D(Y) = 
	 * D(W) = D(Z) = {a, b,..., z}. Then:
	 * <li> X != a is consistent with X/b (because b != a is true)
	 * <li> X != Y is consistent with W/Z (because X != Y is still true)
	 * <li> X != a is not consistent with X/a (because a != a is not true)
	 * <li> X != a is not consistent with X/W (because W != a is not necessarily
	 * true)
	 * <li> X != Y is not consistent with X/W (because W != Y is not necessarily
	 * true) 
	 * <li> X == Y is consistent with W/Z (because X == Y is still true)
	 * <li> X == a is consistent with X/a (because a == a is true)
	 * <li> X == a is consistent with X/W (because X = a & X = W implies W = a)
	 * <li> X == Y is consistent with X/W (because X = Y & X = W implies W = Y)
	 * <li> X == a is not consistent with X/b (because b == a is not true)
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

	
	/**
	 * Returns <code>true</code> if this constraint is unary, that is, if
	 * it is composed by a logical variable and a constant.
	 * 
	 * @return <code>true</code> if this constraint is unary, <code>false</code>
	 * otherwise.
	 */
	public boolean isUnary();
	
	
	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();
	
	@Override
	public String toString();
}
