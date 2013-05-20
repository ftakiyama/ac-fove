package br.usp.poli.takiyama.utils;

import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.prv.Replaceable;
import br.usp.poli.takiyama.prv.Substitution;

/**
 * This class provides common operations over sets.  
 * @author ftakiyama
 *
 */
public final class Sets {
	
	private Sets() { 
		// avoids instantiation
	}
	
	public static final <T> Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> set = new HashSet<T>();
		
		set.addAll(set1);
		set.addAll(set2);
		
		return set;
	}
	
	
	public static final <T> Set<T> union(Set<T> set1, Set<T> set2, Set<T> set3, Set<T> set4) {
		Set<T> union = new HashSet<T>();
		union.addAll(set1);
		union.addAll(set2);
		union.addAll(set3);
		union.addAll(set4);
		return union;
	}
	
	
	public static final <T> Set<T> setOf(T e1) {
		Set<T> set = new HashSet<T>();
		set.add(e1);
		return set;
	}
	
	public static final <T> Set<T> setOf(T e1, T e2) {
		Set<T> set = new HashSet<T>();
		set.add(e1);
		set.add(e2);
		return set;
	}
	
	public static final <T> Set<T> setOf(T e1, T e2, T e3) {
		Set<T> set = new HashSet<T>();
		set.add(e1);
		set.add(e2);
		set.add(e3);
		return set;
	}
	
	
	/**
	 * Returns the result of applying the specified substitution to the
	 * elements of the specified set.
	 * <p>
	 * When the result of applying of the substitution to a given element from
	 * the set is invalid, it is not added to the returned set.
	 * </p>
	 * 
	 * @param <T> A {@link Replaceable} type
	 * @param s The substitution to be made
	 * @param set The set containing the elements to be substituted
	 * @return The result of applying the specified substitution to the
	 * elements of the specified set.
	 */
	public static final <T extends Replaceable<T>> Set<T> apply(Substitution s, Set<T> set) {
		Set<T> replaced = new HashSet<T>((int) (set.size() / 0.75));
		for (Replaceable<T> element : set) {
			try {
				replaced.add(element.apply(s));
			} catch (IllegalArgumentException e) {
				// invalid replaceable is not added to the set
			} catch (IllegalStateException e) {
				// invalid replaceable is not added to the set
			}
		}
		return replaced;
	} 
}
