package br.usp.poli.takiyama.utils;

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
	

	public static final <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
	}
}
