package br.usp.poli.takiyama.common;

import java.util.Set;

/**
 * A set of parfactors that represents a joint probability distribution.
 * 
 * <p>
 * For now, it is merely an alias for a {@code Set} of {@link Parfactor}s.
 * </p>
 * 
 * @author Felipe Takiyama
 *
 */
public interface Distribution extends Set<Parfactor> {
	// Room for expansion - maybe impose an immutable class?

	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();
	
}
