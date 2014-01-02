/*******************************************************************************
 * Copyright 2014 Felipe Takiyama
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package br.usp.poli.takiyama.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
	
	/**
	 * Returns an empty set with size adapted to load factor of 0.75.
	 */
	public static final <T> Set<T> getInstance(int size) {
		return new HashSet<T>((int) Math.ceil(size / 0.75));
	}
	
	
	public static final <T> Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> set = Sets.getInstance(set1.size() + set2.size());
		set.addAll(set1);
		set.addAll(set2);
		return set;
	}
	
	
	public static final <T> Set<T> union(Set<T> set1, Set<T> set2, Set<T> set3, Set<T> set4) {
		Set<T> union = Sets.getInstance(set1.size() + set2.size() + set3.size() 
				+ set4.size());
		union.addAll(set1);
		union.addAll(set2);
		union.addAll(set3);
		union.addAll(set4);
		return union;
	}
	
	
	public static final <T> Set<T> union(Collection<Set<T>> collectionOfSets) {
		Set<T> union = new HashSet<T>();
		for (Set<T> s : collectionOfSets) {
			union.addAll(s);
		}
		return union;
	}
	
	
	public static final <T> Set<T> setOf(T e1) {
		Set<T> set = Sets.getInstance(1);
		set.add(e1);
		return set;
	}
	
	public static final <T> Set<T> setOf(T e1, T e2) {
		Set<T> set = Sets.getInstance(2);
		set.add(e1);
		set.add(e2);
		return set;
	}
	
	public static final <T> Set<T> setOf(T e1, T e2, T e3) {
		Set<T> set = Sets.getInstance(3);
		set.add(e1);
		set.add(e2);
		set.add(e3);
		return set;
	}
	
	public static final <T> Set<T> setOf(Collection<? extends T> c) {
		return new HashSet<T>(c);
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
		Set<T> replaced = Sets.getInstance(set.size());
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
	
	
	/**
	 * Returns the result of ordering the specified set using the specified
	 * comparator. The result is given in a list.
	 * 
	 * @param <T> The type of element being ordered
	 * @param set The set to be ordered
	 * @param comparator The comparator to use to sort the set.
	 * @return The specified set sorted.
	 */
	public static final <T> List<T> sort(Set<T> set, Comparator<T> comparator) {
		List<T> ordered = new ArrayList<T>(set);
		Collections.sort(ordered, comparator);
		return ordered;
	}
	
	
	/**
	 * Returns the difference between the specified set and the specified 
	 * collection. That is, returns a set containing all elements in the set
	 * that are not in the collection.
	 * @param <T>
	 * @param set
	 * @param collection
	 * @return
	 */
	public static final <T> Set<T> difference(Set<T> set, Collection<T> collection) {
		Set<T> difference = getInstance(set.size());
		for (T element : set) {
			if (!collection.contains(element)) {
				difference.add(element);
			}
		}
		return difference;
	}
	
	
	/**
	 * Returns the intersection of the specified sets.
	 * @param <T>
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static final <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
		Set<T> intersection = getInstance(set1.size());
		for (T element : set1) {
			if (set2.contains(element)) {
				intersection.add(element);
			}
		}
		return intersection;
	}
}
