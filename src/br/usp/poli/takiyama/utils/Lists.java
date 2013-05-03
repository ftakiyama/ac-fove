package br.usp.poli.takiyama.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
		List<T> list = new ArrayList<T>(2);
		list.add(e1);
		list.add(e2);
		list.add(e3);
		return list;
	}
	
	
	/**
	 * TODO Make it more generic
	 * Puts the specified element the specified number of times in the list.
	 * The list is cleared before filling it.
	 * 
	 * @param <T> The type of element to put in the list 
	 * @param list The container where the  elements will be put
	 * @param element The element to fill the list
	 * @param num The number of elements to put in the list
	 * @throws IllegalArgumentException If <code>num</code> < 0.
	 */
	public static final <T> void fill(List<T> list, T element, int num)
			throws IllegalArgumentException {
		if (num < 0) {
			throw new IllegalArgumentException();
		}
		list.clear();
		for (int i = 0; i < num; i++) {
			list.add(element);
		}
	}
	
	
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
}
