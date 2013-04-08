package br.usp.poli.takiyama.common;

import br.usp.poli.takiyama.prv.Prv;

/**
 * Represents the result of a split operation over {@link Parfactor}s.
 * 
 * <p>
 * Splitting a parfactor on a substitution always results in at least two 
 * other parfactors: the result of applying the substitution to the parfactor
 * and the residue. The residue may or may not be composed of more than one
 * parfactor. 
 * </p>
 * 
 * @author ftakiyama
 * @see Parfactor
 * @see Elimination
 * @see Distribution
 */
public interface SplitResult extends Elimination<Prv> {
	
	/**
	 * Returns the result from the split.
	 * @return The result from the split.
	 */
	public Parfactor result();
	
	/**
	 * Returns the residual parfactors.
	 * @return The residual parfactors.
	 */
	public Distribution residue();
	
	@Override
	public int hashCode();

	@Override
	public boolean equals(Object o);

	@Override
	public String toString();
}
