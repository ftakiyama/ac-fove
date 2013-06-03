package br.usp.poli.takiyama.common;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.utils.Sets;


public final class StdMarginal implements Marginal {

	private final Distribution parfactors;
	private final RandomVariableSet preservable;
	
	/* ************************************************************************
	 *    Builder
	 * ************************************************************************/

	public static final class StdMarginalBuilder implements Builder<StdMarginal> {

		private Set<Parfactor> parfactors;
		private RandomVariableSet preservable;

		/**
		 * Constructs an empty builder with the specified capacity.
		 * @param capacity
		 */
		public StdMarginalBuilder(int capacity) {
			parfactors = Sets.getInstance(capacity);
			preservable = RandomVariableSet.getInstance();
		}
		
		/**
		 * Constructs an empty marginal. It is equivalent to an empty set.
		 */
		public StdMarginalBuilder() {
			this(0);
		}
		
		/**
		 * Sets the distribution from this marginal to the specified parfactors.
		 * <p>
		 * At least one parfactor must be specified.
		 * Parfactor's order is not preserved.
		 * </p>
		 * 
		 * @param first The first (mandatory) parfactor 
		 * @param remaining The remaining parfactors
		 * @return This builder with the specified parfactors.
		 */
		public StdMarginalBuilder parfactors(Parfactor first, Parfactor ... remaining) {
			parfactors.add(first);
			parfactors.addAll(Arrays.asList(remaining));
			return this;
		}
		
		/**
		 * Sets the distribution from this marginal to the specified parfactors.
		 * <p>
		 * At least one parfactor must be specified, otherwise nothing is done.
		 * Parfactor's order is not preserved.
		 * </p>
		 * 
		 * @param parfactors The set of parfactors that define this marginal
		 * @return This builder with the specified parfactors.
		 */
		public StdMarginalBuilder parfactors(Set<Parfactor> parfactors) {
			if (parfactors != null && parfactors.size() > 0) {
				this.parfactors.addAll(parfactors);
			}
			return this;
		}
		
		/**
		 * Sets the distribution from this marginal to the parfactors in the
		 * specified distribution.
		 * <p>
		 * At least one parfactor must be in the distribution, otherwise 
		 * nothing is done.
		 * Parfactor's order is not preserved.
		 * </p>
		 * 
		 * @param distribution The distribution that define this marginal
		 * @return This builder with the specified parfactors.
		 */
		public StdMarginalBuilder parfactors(Distribution distribution) {
			return parfactors(distribution.toSet());
		}
		
		/**
		 * Adds parfactors from the specified marginal to this builder.
		 * <p>
		 * The random variable set to be preserved is set to be the same as the
		 * specified marginal.
		 * </p>
		 * 
		 * @param marginal
		 * @return This builder with parfactors from the specified marginal
		 * added.
		 */
		public StdMarginalBuilder add(Marginal marginal) {
			parfactors.addAll(marginal.distribution().toSet());
			preservable = marginal.preservable();
			return this;
		}
		
		/**
		 * Adds the specified parfactor to builder's distribution and returns
		 * the modified builder.
		 * 
		 * @param parfactor The parfactor to add.
		 * @return This builder with the specified parfactor added
		 */
		public StdMarginalBuilder add(Parfactor parfactor) {
			parfactors.add(parfactor);
			return this;
		}
		
		/**
		 * Adds the specified random variable set to this builder and returns
		 * the modified builder.
		 * 
		 * @param rvSet The random variable set to preserve (that is, the one
		 * that will not be eliminated)
		 * @return This builder with the specified random variable set added
		 */
		public StdMarginalBuilder preservable(RandomVariableSet rvSet) {
			preservable = rvSet;
			return this;
		}
		
		/**
		 * @return The set of parfactors from this builder as a distribution.
		 */
		Distribution distribution() {
			return StdDistribution.of(parfactors);
		}
		
		@Override
		public StdMarginal build() {
			return new StdMarginal(this);
		}
	}
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	private StdMarginal(StdMarginalBuilder builder) {
		this.parfactors = builder.distribution();
		this.preservable = builder.preservable;
	}
	
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	@Override
	public Set<RandomVariableSet> eliminables() {
		Set<RandomVariableSet> eliminables = new HashSet<RandomVariableSet>();
		for (Parfactor p : parfactors) {
			for (Prv prv : p.prvs()) {
				if (prv.isEquivalentTo(preservable)) {
					continue;
				}
				RandomVariableSet s = RandomVariableSet.getInstance(prv, p.constraints());
				if (!s.equals(preservable)) {
					eliminables.add(s);
				}
			}
		}
		return eliminables;
	}
	
	@Override
	public RandomVariableSet preservable() {
		return preservable;
	}

	@Override
	public Distribution distribution() {
		return StdDistribution.of(parfactors);
	}
	
	@Override
	public boolean isEmpty() {
		return parfactors.isEmpty();
	}
	
	@Override
	public int size() {
		return parfactors.size();
	}
	
	@Override
	public Iterator<Parfactor> iterator() {
		return parfactors.iterator();
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/
	
	/**
	 * Compares objects using their hash code.
	 */
	private static class HashComparator<T> implements Comparator<T> {
		@Override
		public int compare(T o1, T o2) {
			if (o1.equals(o2)) {
				return 0;
			}
			int hash1 = o1.hashCode();
			int hash2 = o2.hashCode();
			if (hash1 > hash2) {
				return 1;
			} else {
				return -1;
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("\nEliminables PRVs:\n");
		result.append(Sets.sort(eliminables(), new HashComparator<RandomVariableSet>()));
		result.append("\nDistribution:\n");
		result.append(Sets.sort(distribution().toSet(), new HashComparator<Parfactor>()));
		return result.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((parfactors == null) ? 0 : parfactors.hashCode());
		result = prime * result
				+ ((preservable == null) ? 0 : preservable.hashCode());
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
		if (!(obj instanceof StdMarginal)) {
			return false;
		}
		StdMarginal other = (StdMarginal) obj;
		if (parfactors == null) {
			if (other.parfactors != null) {
				return false;
			}
		} else if (!parfactors.equals(other.parfactors)) {
			return false;
		}
		if (preservable == null) {
			if (other.preservable != null) {
				return false;
			}
		} else if (!preservable.equals(other.preservable)) {
			return false;
		}
		return true;
	}
}
