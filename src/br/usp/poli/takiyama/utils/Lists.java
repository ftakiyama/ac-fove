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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.prv.Replaceable;
import br.usp.poli.takiyama.prv.Substitution;

/**
 * This class provides set operations for lists.
 *   
 * @author Felipe Takiyama
 *
 */
public final class Lists {
	
	private Lists() {
		// avoids instantiation
	}
	
	/**
	 * Returns the list resulting from the union of the specified lists.
	 * <p>
	 * The order of elements in the result is determined by the order of
	 * elements in <code>list1</code>. If the element is not in this list,
	 * then its ordering is determined by the order of elements in
	 * <code>list2</code>.
	 * </p>
	 * @param <T> The type of element contained in the list
	 * @param list1 The first list to add to the union
	 * @param list2 The second list to add to the union
	 * @return The list resulting from the union of the specified lists
	 */
	public static final <T> List<T> union(List<T> list1, List<T> list2) {
		Set<T> set = new LinkedHashSet<T>(list1.size() + list2.size());

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
	}
	
	
	/**
	 * Returns the result of removing the elements of <code>list2</code> from
	 * <code>list1</code>. The order of elements in <code>list1</code> is
	 * preserved.
	 * 
	 * @param <T> The type of element contained in the list
	 * @param list1 The minuend
	 * @param list2 The subtrahend
	 * @return The list resulting from subtracting the second list from the
	 * first list.
	 */
	public static final <T> List<T> difference(List<T> list1, List<T> list2) {
		Set<T> set = new LinkedHashSet<T>(list1.size());
		
		set.addAll(list1);
		set.removeAll(list2);
		
		return new ArrayList<T>(set);
	}
	

	public static final <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
	}
	
	
	/**
	 * Returns a list containing one element.
	 * @param <T> The type of element contained in the list 
	 * @param e1 The element to put in the list
	 * @return a list containing the specified elements.
	 */
	public static final <T> List<T> listOf(T e1) {
		List<T> list = new ArrayList<T>(1);
		list.add(e1);
		return list;
	}
	
	
	/**
	 * Returns a list containing the specified elements.
	 * @param <T> The type of element contained in the list 
	 * @param e1 The first element of the list
	 * @param e2 The second element of the list
	 * @return a list containing the specified elements.
	 */
	public static final <T> List<T> listOf(T e1, T e2) {
		List<T> list = new ArrayList<T>(2);
		list.add(e1);
		list.add(e2);
		return list;
	}
	
	
	/**
	 * Returns a list containing the specified elements.
	 * @param <T> The type of element contained in the list 
	 * @param e1 The first element of the list
	 * @param e2 The second element of the list
	 * @param e3 The second element of the list
	 * @return a list containing the specified elements.
	 */
	public static final <T> List<T> listOf(T e1, T e2, T e3) {
		List<T> list = new ArrayList<T>(3);
		list.add(e1);
		list.add(e2);
		list.add(e3);
		return list;
	}
	
	
	public static final <T> List<T> listOf(T ... elements) {
		return Arrays.asList(elements);
	}

	
	/**
	 * Returns a lists with size <code>n</code> filled with the specified
	 * element.
	 * @param <T> The type of element contained in the list
	 * @param e The element to fill the list
	 * @param n The size of the list
	 * @return a lists with size <code>n</code> filled with the specified
	 * element.
	 */
	public static final <T> List<T> listOf(T e, int n) {
		List<T> list = new ArrayList<T>(n);
		for (int i = 0; i < n; i++) {
			list.add(e);
		}
		return list;
	}
	
	
	/**
	 * Returns a list containing the elements of the specified 
	 * {@link Collection}. The order of the elements is determined
	 * by the collections iterator.
	 * 
	 * @param <T> The type of element contained in the list
	 * @param c The collection to from where the elements will be gathered
	 * @return a list containing the elements of the specified collection
	 */
	public static final <T> List<T> listOf(Collection<? extends T> c) {
		List<T> list = new ArrayList<T>(c.size());
		list.addAll(c);
		return list;
	}
	
	
	/**
	 * TODO Make it more generic
	 * Puts the specified element the specified number of times in the list.
	 * 
	 * @param <T> The type of element to put in the list 
	 * @param list The container where the  elements will be put
	 * @param element The element to fill the list
	 * @param num The number of elements to put in the list
	 * @throws IllegalArgumentException If <code>num</code> < 0.
	 */
	public static final <T> void fill(List<? super T> list, T element, int num)
			throws IllegalArgumentException {
		if (num < 0) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < num; i++) {
			list.add(element);
		}
	}
		

//	/**
//     * Assigns the specified Object reference to each element of the specified
//     * range of the specified list  The range to be filled
//     * extends from index <tt>fromIndex</tt>, inclusive, to index
//     * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex==toIndex</tt>, the
//     * range to be filled is empty.)
//     *
//     * @param <T> The type of element to put in the list 
//     * @param list the list to be filled
//     * @param fromIndex the index of the first element (inclusive) to be
//     *        filled with the specified value
//     * @param toIndex the index of the last element (exclusive) to be
//     *        filled with the specified value
//     * @param val the value to be stored in all elements of the array
//     * @throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
//     * @throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt>
//     */
//	public static final <T> void fill(List<? super T> list, int fromIndex, int toIndex, T val) {
//		rangeCheck(fromIndex, toIndex);
//		for (int i = fromIndex; i < toIndex; i++) {
//			list.set(i, val);
//		}
//	}
//	
//	
//	/**
//     * Check that fromIndex and toIndex are in range, and throw an
//     * appropriate exception if they aren't.
//     */
//	private static void rangeCheck(int fromIndex, int toIndex) {
//		if (fromIndex > toIndex)
//			throw new IllegalArgumentException("fromIndex(" + fromIndex +
//					") > toIndex(" + toIndex+")");
//		if (fromIndex < 0)
//			throw new ArrayIndexOutOfBoundsException(fromIndex);
//	}
	
	
	/**
	 * Returns <code>true</code> if the specified lists of {@link BigDecimal}
	 * are equal. Equality is verified using 
	 * {@link BigDecimal#compareTo(BigDecimal)}, which does not take into
	 * account the scale of numbers. Thus, {2.0} and {2.00} are considered
	 * to be the same list.
	 * 
	 * @param arg1 The first list to compare
	 * @param arg2 The second list to compare
	 * @return <code>true</code> if the specified lists of {@link BigDecimal}
	 * are equal 
	 * @see BigDecimal#compareTo(BigDecimal)
	 */
	public static final boolean areEqual(List<BigDecimal> arg1, List<BigDecimal> arg2) {
		boolean areEqual = true;
		if (arg1.size() == arg2.size()) {
			for (int i = 0; i < arg1.size(); i++) {
				if (arg1.get(i).compareTo(arg2.get(i)) != 0) {
					areEqual = false;
					break;
				}
			}
		} else {
			areEqual = false;
		}
		return areEqual;
	}
	
	
	/**
	 * Returns the hash code for the specified list of {@link BigDecimal}.
	 * <p>
	 * This method should be used in association with 
	 * {@link #areEqual(List, List)} to maintain consistency between
	 * hashCode() and equals().
	 * </p>
	 * @param list A list of {@link BigDecimal}
	 * @return The hash code for the specified list
	 */
	public static final int hashCode(List<BigDecimal> list) {
		int result = 1;
		for (BigDecimal element : list) {
			// set scale to 15, but this is an arbitrary number
			//result = 31 * result + (element == null ? 0 : element.setScale(15, RoundingMode.HALF_EVEN).hashCode()); 
			// Put a math context in every operation, so i should not need to set scale here
			result = 31 * result + (element == null ? 0 : element.hashCode()); 
		}
		return result;
	}
	
	
	/**
	 * Returns the result of applying the specified substitution to the
	 * elements of the specified list.
	 * <p>
	 * When the result of applying of the substitution to a given element from
	 * the list is invalid, it is not added to the returned list.
	 * </p>
	 * 
	 * @param <T> A {@link Replaceable} type
	 * @param s The substitution to be made
	 * @param list The list containing the elements to be substituted
	 * @return The result of applying the specified substitution to the
	 * elements of the specified list.
	 */
	public static final <T extends Replaceable<T>> List<T> apply(Substitution s, List<T> list) {
		List<T> replaced = new ArrayList<T>(list.size());
		for (Replaceable<T> element : list) {
			try {
				replaced.add(element.apply(s));
			} catch (IllegalArgumentException e) {
				// invalid replaceable is not added to the list
			} catch (IllegalStateException e) {
				// invalid replaceable is not added to the set
			}
		}
		return replaced;
	}
	
	
	/**
	 * Replaces the specified element with another element of same type in the
	 * specified list.
	 * @param <T>
	 * @param list
	 * @param toReplace
	 * @param replacement
	 * @return
	 */
	public static final <T> List<T> replace(List<T> list, T toReplace, T replacement) {
		List<T> replaced = new ArrayList<T>(list);
		int index = list.indexOf(toReplace);
		if (index != -1) {
			replaced.set(index, replacement);
		}
		return replaced;
	}
	
	
	/**
	 * Returns <code>true</code> if the specified lists have the same elements.
	 * @param <T>
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static final <T> boolean sameElements(List<T> list1, List<T> list2) {
		if (list1.size() != list2.size()) {
			return false;
		}
		return (list1.containsAll(list2) && list2.containsAll(list1));
	}
}
