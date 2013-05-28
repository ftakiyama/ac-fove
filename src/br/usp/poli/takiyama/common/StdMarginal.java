package br.usp.poli.takiyama.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.utils.Sets;

public class StdMarginal implements Marginal<Prv> {

//	private final Set<Prv> eliminables;
//	private final Distribution distribution;
	
	private final Map<Parfactor, Set<Prv>> marginal;
	
	/* ************************************************************************
	 *    Builder
	 * ************************************************************************/

	public static class StdMarginalBuilder 
			implements Builder<StdMarginal>, Iterable<Parfactor> {

		private Map<Parfactor, Set<Prv>> builderMarginal;
		
		public StdMarginalBuilder() {
			builderMarginal = new HashMap<Parfactor, Set<Prv>>();
		}
		
		public StdMarginalBuilder(Marginal<Prv> marginal) {
			builderMarginal = marginal.toMap();
		}
		
		public StdMarginalBuilder set(Parfactor oldParfactor, Parfactor newParfactor) {
			Set<Prv> updatedPrvs = new HashSet<Prv>();
			for (Prv prv : builderMarginal.get(oldParfactor)) {
				int index = oldParfactor.prvs().indexOf(prv);
				Prv update = newParfactor.prvs().get(index);
				updatedPrvs.add(update);
			}
			builderMarginal.remove(oldParfactor);
			builderMarginal.put(newParfactor, updatedPrvs);
			return this;
		}
		
		public StdMarginalBuilder parfactors(Parfactor ... parfactors) {
			Set<Prv> eliminables = eliminables();
			for (Parfactor p : parfactors) {
				builderMarginal.put(p, eliminables);
			}
			// must update when eliminable PRVs already exist
			updateEliminables();
			return this;
		}
		
		private void updateEliminables() {
			Set<Parfactor> parfactors = new HashSet<Parfactor>(builderMarginal.keySet());
			Set<Prv> eliminables = eliminables();
			for (Parfactor p : parfactors) {
				Set<Prv> elim = new HashSet<Prv>(eliminables.size());
				for (Prv prv : eliminables) {
					if (p.contains(prv)) {
						elim.add(prv);
					}
				}
				builderMarginal.put(p, elim);
			}
		}
		
		public StdMarginalBuilder parfactors(Collection<Parfactor> parfactors) {
			return parfactors(parfactors.toArray(new Parfactor[parfactors.size()]));
		}
		
		public StdMarginalBuilder parfactors(Distribution dist) {
			return parfactors(dist.toSet());
		}
		
		private Set<Prv> eliminables() {
			return Sets.union(builderMarginal.values());
		}
		
		public StdMarginalBuilder eliminables(Prv ... prvs) {
			// needed to avoid iterating over a set being changed
			Set<Parfactor> parfactors = new HashSet<Parfactor>(builderMarginal.keySet());
			for (Parfactor p : parfactors) {
				for (Prv prv : prvs) {
					if (p.contains(prv)) {
						builderMarginal.get(p).add(prv);
					}
				}
			}
			return this;
		}
		
		public StdMarginalBuilder eliminables(Collection<Prv> prvs) {
			return eliminables(prvs.toArray(new Prv[prvs.size()]));
		}
		
		public StdMarginalBuilder union(SplitResult sr1, SplitResult sr2) {
			return parfactors(sr1.distribution().toSet())
					.parfactors(sr2.distribution().toSet())
					.eliminables(sr1.eliminables())
					.eliminables(sr2.eliminables());
		}
		
		public StdMarginalBuilder marginal(Marginal<Prv> marginal) {
			parfactors(marginal.distribution().toSet());
			eliminables(marginal.eliminables());
			return this;
		}
		
		@Override
		public StdMarginal build() {
			return new StdMarginal(this);
		}

		@Override
		public Iterator<Parfactor> iterator() {
			return builderMarginal.keySet().iterator();
		}
		
	}
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	public StdMarginal(StdMarginalBuilder builder) {
		this.marginal = builder.builderMarginal;
	}
	
//	/**
//	 * Creates an empty {@link UnconstrainedMarginal}.
//	 */
//	private UnconstrainedMarginal() {
//		eliminables = new HashSet<Prv>(0);
//		distribution = StdDistribution.of();
//	}
	
//	/**
//	 * Creates an {@link UnconstrainedMarginal} with the specified parameters.
//	 * @param eliminables A collection of standard parameterized random 
//	 * variables.
//	 * @param distribution A distribution
//	 */
//	private UnconstrainedMarginal(Collection<? extends Prv> eliminables, Distribution distribution) {
//		this.eliminables = new HashSet<Prv>(eliminables);
//		this.distribution = StdDistribution.of(distribution);
//	}
	
//	/**
//	 * Creates a copy of the specified {@link UnconstrainedMarginal}. 
//	 * @param marginal The unconstrained marginal to copy.
//	 */
//	public UnconstrainedMarginal(UnconstrainedMarginal marginal) {
//		this.eliminables = new HashSet<StdPrv>(marginal.eliminables());
//		this.distribution = new StdDistribution(marginal.distribution());
//	}
	
//	private UnconstrainedMarginal(Prv eliminable, Parfactor p) {
//		this.eliminables = Collections.singleton(eliminable);
//		this.distribution = StdDistribution.of(p);
//	}
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

//	/**
//	 * Creates an empty {@link UnconstrainedMarginal}.
//	 * @return An empty {@link UnconstrainedMarginal}.
//	 */
//	public static UnconstrainedMarginal getInstance() {
//		return new UnconstrainedMarginalBuilder().build();
//	}
//	
//	/**
//	 * Creates an {@link UnconstrainedMarginal} with the specified parameters.
//	 * @param eliminables A collection of standard parameterized random 
//	 * variables.
//	 * @param distribution A distribution
//	 * @return an {@link UnconstrainedMarginal} with the specified parameters.
//	 */
//	public static UnconstrainedMarginal getInstance(Collection<? extends Prv> eliminables, 
//			Distribution distribution) {
//		return new UnconstrainedMarginal(eliminables, distribution);
//	}
//	
//	
//	public static UnconstrainedMarginal getInstance(Prv eliminable, Parfactor p) {
//		return new UnconstrainedMarginal(eliminable, p);
//	}
//	
//	/**
//	 * Returns the union of the specified {@link SplitResult}.
//	 * 
//	 * @param split A split result
//	 * @param anotherSplit Another split result
//	 * @return the union of the specified {@link SplitResult}.
//	 */
//	public static UnconstrainedMarginal getInstance(SplitResult split, SplitResult anotherSplit) {
//		
//		Distribution dist = split.residue();
//		dist = dist.add(split.result());
//		dist = dist.addAll(anotherSplit.residue());
//		dist = dist.add(anotherSplit.result());
//		
//		Set<Prv> elim = split.eliminables();
//		elim.addAll(anotherSplit.eliminables());
//		
//		return new UnconstrainedMarginal(elim, dist);
//	}
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	@Override
	public Set<Prv> eliminables() {
		return Sets.union(marginal.values());
	}

	@Override
	public Distribution distribution() {
		return StdDistribution.of(marginal.keySet());
	}
	
	@Override
	public boolean isEmpty() {
		return marginal.isEmpty();
	}
	
	@Override
	public Map<Parfactor, Set<Prv>> toMap() {
		return new HashMap<Parfactor, Set<Prv>>(marginal);
	}

	@Override
	public Iterator<Parfactor> iterator() {
		return marginal.keySet().iterator();
	}
	
	/* ************************************************************************
	 *    Setters
	 * ************************************************************************/

//	@Override
//	public Marginal<Prv> add(Parfactor p) {
//		Distribution dist = StdDistribution.of(distribution);
//		dist = dist.add(p);
//		return UnconstrainedMarginal.getInstance(eliminables, dist);
//	}
//	
//	@Override
//	public Marginal<Prv> addAll(Distribution d) {
//		Distribution dist = StdDistribution.of(distribution);
//		dist = dist.addAll(d);
//		return UnconstrainedMarginal.getInstance(eliminables, dist);
//	}
	
//	@Override
//	public Marginal<Prv> apply(Substitution s) {
//		Distribution substitutedDistribution = distribution.apply(s);
//		Set<Prv> substitutedEliminables = Sets.apply(s, eliminables);
//		return UnconstrainedMarginal.getInstance(substitutedEliminables, 
//				substitutedDistribution);
//	}
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/
	
	@Override
	public String toString() {
		String result = "\nEliminables PRVs:\n" + Sets.sort(eliminables(), new HashComparator<Prv>()) 
					  + "\nDistribution:\n" +  Sets.sort(distribution().toSet(), new HashComparator<Parfactor>());
		return result;
	}
	
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
				return 0;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((marginal == null) ? 0 : marginal.hashCode());
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
		if (marginal == null) {
			if (other.marginal != null) {
				return false;
			}
		} else if (!marginal.equals(other.marginal)) {
			return false;
		}
		return true;
	}
}
