package br.usp.poli.takiyama.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Standard implementation of {@link Distribution}.
 * 
 * @author Felipe Takiyama
 *
 */
public final class StdDistribution implements Distribution {

	private Set<Parfactor> pSet;
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	/**
	 * Creates an empty distribution with the specified initial capacity.
	 * @param capacity The initial capacity
	 */
	private StdDistribution(int capacity) {
		this.pSet = new HashSet<Parfactor>(capacity);
	}
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/
	
	/**
	 * Creates an empty distribution
	 */
	public static Distribution of() {
		return new StdDistribution(0);
	}
	
	/**
	 * Creates a distribution with one parfactor
	 * @param p1 A parfactor
	 */
	public static Distribution of(Parfactor p1) {
		Distribution dist = new StdDistribution(1);
		dist.add(p1);
		return dist;
	}
	
	/**
	 * Creates a distribution with two parfactors
	 * @param p1 The first parfactor to put in the distribution
	 * @param p2 The second parfactor to put in the distribution
	 */
	public static Distribution of(Parfactor p1, Parfactor p2) {
		Distribution dist = new StdDistribution(2);
		dist.add(p1);
		dist.add(p2);
		return dist;
	}
	
	/**
	 * Creates a distribution with the specified collection of parfactors
	 * @param c A collection of parfactors.s
	 */
	public static Distribution of(Collection<? extends Parfactor> c) {
		Distribution dist = new StdDistribution(Math.max((int) (c.size()/.75f) + 1, 16));
		dist.addAll(c);
		return dist;
	}
	
	/* ************************************************************************
	 *    Inherited methods
	 * ************************************************************************/
	
	@Override
	public boolean add(Parfactor e) {
		return pSet.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends Parfactor> c) {
		return pSet.addAll(c);
	}

	@Override
	public void clear() {
		this.pSet.clear();
	}

	@Override
	public boolean contains(Object o) {
		return pSet.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return pSet.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return pSet.isEmpty();
	}

	@Override
	public Iterator<Parfactor> iterator() {
		return pSet.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return pSet.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return pSet.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return pSet.retainAll(c);
	}

	@Override
	public int size() {
		return pSet.size();
	}

	@Override
	public Object[] toArray() {
		return pSet.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return pSet.toArray(a);
	}

	/* ************************************************************************
	 *    hashCode, equals and toStrings
	 * ************************************************************************/
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pSet == null) ? 0 : pSet.hashCode());
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
		if (!(obj instanceof StdDistribution)) {
			return false;
		}
		StdDistribution other = (StdDistribution) obj;
		if (pSet == null) {
			if (other.pSet != null) {
				return false;
			}
		} else if (!pSet.equals(other.pSet)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return pSet.toString();
	}
}
