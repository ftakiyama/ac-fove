package br.usp.poli.takiyama.common;

import java.util.Set;

import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;

/**
 * Represents the marginal &Sum;<sub>&Gamma;</sub> J(&Phi;), where &Gamma; is
 * a set of Random Variables and J(&Phi;) is a {@link Distribution}.
 * 
 * @author Felipe Takiyama
 *
 * @param <E> The type of element being summed out.
 * 
 * @see Prv
 */
public interface Marginal extends Iterable<Parfactor> {
	
	/**
	 * Returns the distribution of this marginal (before summing out the
	 * eliminable variables).
	 * 
	 * @return The distribution of this marginal
	 */
	public Distribution distribution();

	/**
	 * Returns the set of variables to eliminate.
	 * 
	 * @return The set of variables to eliminate.
	 */
	public Set<RandomVariableSet> eliminables();
	
	
	/**
	 * Returns the set of variables to preserve. The set returned by this method
	 * is the complement of the set returned by {@link #eliminables()}.
	 * 
	 * @return The set of variables to preserve.
	 */
	public RandomVariableSet preservable();
	
	/**
	 * Returns <code>true</code> if this elimination is empty
	 * @return <code>true</code> if this elimination is empty, 
	 * <code>false</code> otherwise.
	 */
	public boolean isEmpty();
	
	/**
	 * Returns the number of parfactors in the marginal.
	 * 
	 * @return the number of parfactors in the marginal.
	 */
	public int size();
	
	@Override
	public int hashCode();

	@Override
	public boolean equals(Object o);

	@Override
	public String toString();	
}
