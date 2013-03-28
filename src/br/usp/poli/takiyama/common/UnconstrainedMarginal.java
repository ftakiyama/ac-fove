package br.usp.poli.takiyama.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.prv.StdPrv;

public class UnconstrainedMarginal implements Marginal<StdPrv> {

	private final Set<StdPrv> eliminables;
	private final Distribution distribution;
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	/**
	 * Creates an empty {@link UnconstrainedMarginal}.
	 */
	private UnconstrainedMarginal() {
		eliminables = new HashSet<StdPrv>(0);
		distribution = StdDistribution.of();
	}
	
	/**
	 * Creates an {@link UnconstrainedMarginal} with the specified parameters.
	 * @param eliminables A collection of standard parameterized random 
	 * variables.
	 * @param distribution A distribution
	 */
	private UnconstrainedMarginal(Collection<StdPrv> eliminables, Distribution distribution) {
		this.eliminables = new HashSet<StdPrv>(eliminables);
		this.distribution = StdDistribution.of(distribution);
	}
	
//	/**
//	 * Creates a copy of the specified {@link UnconstrainedMarginal}. 
//	 * @param marginal The unconstrained marginal to copy.
//	 */
//	public UnconstrainedMarginal(UnconstrainedMarginal marginal) {
//		this.eliminables = new HashSet<StdPrv>(marginal.eliminables());
//		this.distribution = new StdDistribution(marginal.distribution());
//	}
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * Creates an empty {@link UnconstrainedMarginal}.
	 * @return An empty {@link UnconstrainedMarginal}.
	 */
	public static UnconstrainedMarginal getInstance() {
		return new UnconstrainedMarginal();
	}
	
	/**
	 * Creates an {@link UnconstrainedMarginal} with the specified parameters.
	 * @param eliminables A collection of standard parameterized random 
	 * variables.
	 * @param distribution A distribution
	 * @return an {@link UnconstrainedMarginal} with the specified parameters.
	 */
	public static UnconstrainedMarginal getInstance(Collection<StdPrv> eliminables, 
			Distribution distribution) {
		return new UnconstrainedMarginal(eliminables, distribution);
	}
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	@Override
	public Set<StdPrv> eliminables() {
		return new HashSet<StdPrv>(eliminables);
	}

	@Override
	public Distribution distribution() {
		return StdDistribution.of(distribution);
	}
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((distribution == null) ? 0 : distribution.hashCode());
		result = prime * result
				+ ((eliminables == null) ? 0 : eliminables.hashCode());
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
		if (!(obj instanceof UnconstrainedMarginal)) {
			return false;
		}
		UnconstrainedMarginal other = (UnconstrainedMarginal) obj;
		if (distribution == null) {
			if (other.distribution != null) {
				return false;
			}
		} else if (!distribution.equals(other.distribution)) {
			return false;
		}
		if (eliminables == null) {
			if (other.eliminables != null) {
				return false;
			}
		} else if (!eliminables.equals(other.eliminables)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		String result = "Eliminables PRVs: " + eliminables 
					  + "\nDistribution: " + distribution;
		return result;
	}
}
