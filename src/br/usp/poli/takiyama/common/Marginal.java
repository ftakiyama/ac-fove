package br.usp.poli.takiyama.common;

import java.util.Set;

import br.usp.poli.takiyama.prv.Prv;

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
public interface Marginal<T extends Prv> extends Elimination<T> {
	
	/**
	 * Returns the distribution of this marginal (before summing out the
	 * eliminable variables)
	 * @return The distribution of this marginal
	 */
	public Distribution distribution();
}
