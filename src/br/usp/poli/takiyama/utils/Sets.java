package br.usp.poli.takiyama.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class provides common operations over sets.  
 * @author ftakiyama
 *
 */
public final class Sets {
	
	/**
	 * Union of two lists.
	 * @param <T>
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static final <T> List<T> union(List<T> list1, List<T> list2) {
		Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
	}
	
	public static final <T> Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> set = new HashSet<T>();
		
		set.addAll(set1);
		set.addAll(set2);
		
		return set;
	}
	
	public static final <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
	}
}
