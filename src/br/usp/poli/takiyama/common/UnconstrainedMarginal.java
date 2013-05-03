package br.usp.poli.takiyama.common;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdPrv;

public class UnconstrainedMarginal implements Marginal<Prv> {

	private final Set<? extends Prv> eliminables;
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
	
	private UnconstrainedMarginal(Prv eliminable, Parfactor p) {
		this.eliminables = Collections.singleton(eliminable);
		this.distribution = StdDistribution.of(p);
	}
	
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
	
	
	public static UnconstrainedMarginal getInstance(Prv eliminable, Parfactor p) {
		return new UnconstrainedMarginal(eliminable, p);
	}
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	@Override
	public Set<Prv> eliminables() {
		return new HashSet<Prv>(eliminables);
	}

	@Override
	public Distribution distribution() {
		return StdDistribution.of(distribution);
	}
	
	@Override
	public boolean isEmpty() {
		return distribution.isEmpty();
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
