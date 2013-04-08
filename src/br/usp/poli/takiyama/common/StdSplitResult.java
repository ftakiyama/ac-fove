package br.usp.poli.takiyama.common;

import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.cfove.SimpleParfactor;
import br.usp.poli.takiyama.prv.Prv;

/**
 * Standard implementation of {@link SplitResult}. Instances of this class are
 * returned when splitting {@link SimpleParfactor}s and  
 * {@link AggregationParfactor}s (usually).
 * <p>
 * Particularly, this class does not have 'eliminable' random variables, 
 * and thus is considered a simple {@link Distribution} rather than a
 * {@link Marginal}. 
 * </p>
 * 
 * @author Felipe Takiyama
 * @see SplitResult
 * @see SimpleParfactor
 * @see Distribution
 * @see Marginal
 */
public final class StdSplitResult implements SplitResult {

	private final Parfactor result;
	private final Distribution residue;
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	/**
	 * Creates an empty instance of Split Result.
	 */
	private StdSplitResult() {
		result = null;
		residue = StdDistribution.of();
	}
	
//	private StdSplitResult(Parfactor result, Distribution residue) {
//		this.result = result; // use defensive copy!
//		this.residue = residue;
//	}
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * Returns an empty instance of SplitResult.
	 * @return An empty instance of SplitResult.
	 */
	public static StdSplitResult getInstance() {
		return new StdSplitResult();
	}
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	/**
	 * Returns an empty set of {@link Prv}s.
	 */
	@Override
	public Set<Prv> eliminables() {
		return new HashSet<Prv>(0);
	}

	@Override
	public Parfactor result() {
		return SimpleParfactor.getInstance(result);
	}

	@Override
	public Distribution residue() {
		return StdDistribution.of(residue);
	}

	/**
	 * Returns <code>true</code> if this is an empty split result. This 
	 * condition is met if both the result and the residues are not associated
	 * with parfactors.
	 */
	@Override
	public boolean isEmpty() {
		return ((result == null) && residue.isEmpty());
	}
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((residue == null) ? 0 : residue.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StdSplitResult)) {
			return false;
		}
		StdSplitResult other = (StdSplitResult) obj;
		if (residue == null) {
			if (other.residue != null) {
				return false;
			}
		} else if (!residue.equals(other.residue)) {
			return false;
		}
		if (result == null) {
			if (other.result != null) {
				return false;
			}
		} else if (!result.equals(other.result)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "Residue:\n" + residue + "\nResult:\n" + result;
	}
}
