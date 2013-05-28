package br.usp.poli.takiyama.common;

import java.util.Map;
import java.util.Set;

import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Substitution;

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
public interface Marginal<T extends Prv> extends Elimination<T>, 
		Iterable<Parfactor> {
	
	/**
	 * Returns the distribution of this marginal (before summing out the
	 * eliminable variables).
	 * 
	 * @return The distribution of this marginal
	 */
	public Distribution distribution();
	
	/**
	 * Adds the specified parfactor to this marginal and returns the
	 * result.
	 * 
	 * @param p The parfactor to add to this marginal
	 * @return The result of adding the specified parfactor to this
	 * marginal
	 */
//	public Marginal<Prv> add(Parfactor p);
	
	/**
	 * Adds the specified distribution to this marginal and returns the
	 * result.
	 * 
	 * @param d The distribution to add to this marginal
	 * @return The result of adding the specified distribution to this
	 * marginal
	 */
//	public Marginal<Prv> addAll(Distribution d);
	
	/**
	 * Returns the result of applying the specified substitution to this
	 * marginal. The substitution is made on marginal's distribution and on
	 * PRVs to be eliminated.
	 * 
	 * @param s The substitution to be applied to this marginal
	 * @return The result of applying the specified substitution to this
	 * marginal.
	 */
//	public Marginal<Prv> apply(Substitution s);
	
	/**
	 * Returns this marginal as a map. The map associates each parfactor in
	 * distribution with its corresponding PRVs being eliminated.
	 *  
	 * @return this marginal as a map.
	 */
	public Map<Parfactor, Set<T>> toMap();
	
	@Override
	public int hashCode();

	@Override
	public boolean equals(Object o);

	@Override
	public String toString();
}
