package br.usp.poli.takiyama.acfove;

import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.cfove.SimpleParfactor;
import br.usp.poli.takiyama.cfove.StdParfactor;
import br.usp.poli.takiyama.common.Distribution;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdDistribution;
import br.usp.poli.takiyama.common.UnconstrainedMarginal;
import br.usp.poli.takiyama.prv.Prv;

/**
 * Represents the result of splitting an {@link AggregationParfactor} on a 
 * substitution that involves the extra logical variable from the parent
 * parameterized random variable.
 * 
 * @author Felipe Takiyama
 * @see SplitResult
 */
public final class AggSplitResult implements SplitResult {

	private final Parfactor result;
	
	/*
	 * Even though the 'correct' marginal involves a distribution on
	 * result + residues, only residue Parfactors are stored in this marginal.
	 */
	private final Marginal<? extends Prv> marginal; 
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	/**
	 * Creates an empty Aggregation Split Result.
	 */
	private AggSplitResult() {
		result = StdParfactor.getInstance();
		marginal = UnconstrainedMarginal.getInstance();
	}
	

	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	@Override
	public Set<Prv> eliminables() {
		return new HashSet<Prv>(marginal.eliminables());
	}

	
	/**
	 * Returns the result of the split, which is an instance of 
	 * {@link SimpleParfactor}.
	 * @return The result of the split.
	 */
	@Override
	public Parfactor result() {
		return StdParfactor.getInstance(result);
	}
	

	@Override
	public Distribution residue() {
		return StdDistribution.of(marginal.distribution());
	}
	
	
	@Override
	public boolean isEmpty() {
		return (result.isConstant() && marginal.isEmpty());
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((marginal == null) ? 0 : marginal.hashCode());
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
		if (!(obj instanceof AggSplitResult)) {
			return false;
		}
		AggSplitResult other = (AggSplitResult) obj;
		if (marginal == null) {
			if (other.marginal != null) {
				return false;
			}
		} else if (!marginal.equals(other.marginal)) {
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
		return marginal + "\n" + result;
	}
}
