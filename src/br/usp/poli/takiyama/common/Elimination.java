package br.usp.poli.takiyama.common;

import java.util.Set;

import br.usp.poli.takiyama.prv.Prv;

/**
 * Represents the sum-out operator &Sum;.
 * 
 * @author Felipe Takiyama
 *
 * @param <T> The type of element being summed out.
 */
public interface Elimination <T extends Prv> {
	
	/**
	 * Returns the set of variables to eliminate.
	 * @return The set of variables to eliminate.
	 */
	public Set<T> eliminables();
	
	/**
	 * Returns <code>true</code> if this elimination is empty
	 * @return <code>true</code> if this elimination is empty, 
	 * <code>false</code> otherwise.
	 */
	public boolean isEmpty();
}
