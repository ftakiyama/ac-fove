package br.usp.poli.takiyama.common;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RangeElement;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

public interface Factor extends Iterable<Tuple<RangeElement>> {

	/**
	 * Returns the index of a tuple.
	 * 
	 * @param tuple The tuple to search.   
	 * @return The index of a tuple in this factor.
	 * @throws IllegalArgumentException if the tuple is empty.
	 */
	public int getIndex(Tuple<RangeElement> tuple)
			throws IllegalArgumentException;

	/**
	 * Returns a tuple given its index.
	 * 
	 * @param index The index of the tuple.
	 * @return A tuple at the position specified by the parameter <b>index</b>.
	 */
	public Tuple<RangeElement> getTuple(int index);

	/**
	 * Returns the value of the tuple specified by its index.
	 * 
	 * @param index The index of the tuple in this factor.
	 * @return The value of the tuple specified by its index.
	 */
	public BigDecimal getValue(int index);

	/**
	 * Returns the value of the specified tuple.
	 * 
	 * @param tuple The tuple whose value is to return
	 * @return The value of the specified tuple.
	 */
	public BigDecimal getValue(Tuple<RangeElement> tuple);

	/**
	 * Returns an iterator over all tuples of this factor.
	 * <p>
	 * The tuples returned <b>depend</b> on the order of its {@link Prv}s.
	 * </p>
	 * 
	 * @return An iterator over all tuples of this factor
	 */
	public Iterator<Tuple<RangeElement>> iterator();

	/**
	 * Returns the number of values in this factor, which is the same as the
	 * number of tuples in this factor.
	 * 
	 * @return The number of values in this factor.
	 */
	public int size();

	/**
	 * Returns the name of this factor.
	 * 
	 * @return The name of this factor.
	 */
	public String name();

	/**
	 * Returns the list of {@link Prv}s associated with this factor. This
	 * list has the same order in which Prvs were inserted when creating
	 * this factor.
	 * 
	 * @return The list of {@link Prv}s associated with this factor
	 */
	public List<Prv> variables();

	/**
	 * Returns the values of all tuples, in the order they were created.
	 *  
	 * @return A list containing all values of the factor, in order.
	 */
	public List<BigDecimal> values();

	/**
	 * Returns <code>true</code> if the specified {@link Term} is in this factor.
	 * 
	 * @param t The term to search for.
	 * @return <code>true</code> if the specified term specified is in this 
	 * factor, <code>false</code> otherwise.
	 */
	public boolean contains(Term t);

	/**
	 * Returns the number of occurrences of the specified {@link Term} in 
	 * {@link Prv}s from this factor.
	 * 
	 * @param t The term to search for.
	 * @return the number of occurrences of the specified {@link Term} in 
	 * {@link Prv}s from this factor.
	 */
	public int occurrences(Term t);

	/**
	 * Returns the first occurrence of a PRV in this factor having the 
	 * specified term, according to the order returned by the iterator of 
	 * variables of this factor.
	 * <p>
	 * If there is no PRV having the specified term, returns an empty 
	 * {@link StdPrv}. 
	 * </p>
	 * 
	 * @param t The term to search for
	 * @return The first occurrence of a PRV in this factor having the 
	 * specified term.
	 */
	public Prv getVariableHaving(Term t);

	/**
	 * Returns <code>true</code> if this factor is a sub-factor of the 
	 * specified factor.
	 * <p>
	 * Factor F1 is a sub-factor of factor F2 if the set of {@link Prv}s from
	 * F1 is a subset of the of Prvs from F2.
	 * </p>
	 * 
	 * @return <code>true</code> if this factor is a sub-factor of the 
	 * specified factor, <code>false</code> otherwise.
	 */
	public boolean isSubFactorOf(Factor factor);

	/**
	 * Returns <code>true</code> if this factor is constant.
	 * <p>
	 * A constant factor returns the value 1 for all tuples in the factor.
	 * </p>
	 * 
	 * @return <code>true</code> if this factor is constant, <code>false</code>
	 * otherwise.
	 */
	public boolean isConstant();

	/**
	 * Returns <code>true</code> if this factor is empty.
	 * <p>
	 * An empty factor has no variables, nor values.
	 * </p>
	 * 
	 * @return  <code>true</code> if this factor is empty, <code>false</code>
	 * otherwise.
	 */
	public boolean isEmpty();

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object other);

	@Override
	public String toString();

	/**
	 * Returns the result of applying the specified substitution to this
	 * Factor. The substitution is applied to PRVs in this Factor, but its
	 * values are not modified.
	 * @param s The substitution to apply
	 * @return The result of applying the specified substitution to this
	 * Factor
	 */
	public Factor apply(Substitution s);

	/**
	 * Returns a copy of this factor with the value of the specified tuple
	 * replaced by the specified value.
	 * 
	 * @param tuple The tuple whose value must be modified
	 * @param value The new value of the tuple
	 * @return a copy of this factor with the value of the specified tuple
	 * replaced by the specified value.
	 */
	public Factor set(Tuple<RangeElement> tuple, BigDecimal value);

	/**
	 * Sums out a random variable from a factor.
	 * <p>
	 * Suppose F is a factor on random variables x<sub>1</sub>,...,
	 * x<sub>i</sub>,...,x<sub>j</sub>. The summing out of random variable 
	 * x<sub>i</sub> from F, denoted as &Sigma;<sub>x<sub>i</sub></sub>F 
	 * is the factor on 
	 * random variables x<sub>1</sub>,...,x<sub>i-1</sub>, x<sub>i+1</sub>, 
	 * ..., x<sub>j</sub> such that
	 * </p>
	 * (&Sigma;<sub>x<sub>i</sub></sub> F)
	 *   (x<sub>1</sub>,...,x<sub>i-1</sub>,x<sub>i+1</sub>,...,x<sub>j</sub>) =	
	 * 	   &Sigma; <sub>y &isin; dom(x<sub>i</sub>)</sub> 
	 *     F(x<sub>1</sub>,...,x<sub>i-1</sub>,x<sub>i</sub> = y,
	 *       x<sub>i+1</sub>,...,x<sub>j</sub>).
	 * <p>
	 * If the variable to be summed out does not exist in the factor, this
	 * method returns the specified factor unmodified.
	 * </p>
	 * 
	 * @param prv The {@link Prv} to be summed out.
	 * @return A factor with the specified Prv summed out, or this factor
	 * if <code>prv</code> does not exist in this factor.
	 */
	public Factor sumOut(Prv prv);

	/**
	 * Returns this factor raised by <code>p/q</code>.
	 * <p>
	 * Raising a factor to some exponent is the same as raising its values to
	 * that exponent.  
	 * </p>
	 * 
	 * @param p Exponents's numerator
	 * @param q Exponenet's denominator
	 * @return The value of this factor raised to <code>p/q</code>
	 */
	public Factor pow(int p, int q);

	/**
	 * Multiplies this factor with the specified factor.
	 * <p>
	 * Given two factors 
	 * 	   F<sub>1</sub>(x<sub>1</sub>,...,x<sub>n</sub>,
	 *                   y<sub>1</sub>,...,y<sub>j</sub>) and 
	 *     F<sub>2</sub>(y<sub>1</sub>,...,y<sub>j</sub>,
	 *                   z<sub>1</sub>,...,z<sub>k</sub>) 
	 * the resulting factor will be 
	 * F(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,...,
	 *         y<sub>j</sub>,z<sub>1</sub>,...,z<sub>k</sub>) =
	 *     F<sub>1</sub>(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,..., 
	 *         y<sub>j</sub>) x 
	 *     F<sub>2</sub>(y<sub>1</sub>,...,y<sub>j</sub>,z<sub>1</sub>,...,
	 *         z<sub>k</sub>)
	 * </p>
	 * That is, for each assignment of values to the variables in the factors,
	 * the method multiply the values that have the same assignment for common 
	 * variables. 
	 * 
	 * @param secondFactor The second factor to be multiplied
	 * @return The multiplication of fisrtFactor by secondFactor.
	 */
	public Factor multiply(Factor factor);
	
	/**
	 * Returns this factor reordered using the specified factor as a reference.
	 * The returned factor will have the same Prv order as the reference, with
	 * values reordered in order to not modify the distribution it represents.
	 * 
	 * @param reference A reference factor that dictates the new PRV order
	 * @return This factor reordered using the specified factor as a reference.
	 * @throws IllegalArgumentException If the reference does not have the same
	 * PRVs as this factor.
	 */
	public Factor reorder(Factor reference) throws IllegalArgumentException;

}