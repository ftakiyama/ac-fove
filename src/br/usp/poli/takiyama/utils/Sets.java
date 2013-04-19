package br.usp.poli.takiyama.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * This class provides common operations over sets.  
 * @author ftakiyama
 *
 */
public final class Sets {
	
	public static final <T> Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> set = new HashSet<T>();
		
		set.addAll(set1);
		set.addAll(set2);
		
		return set;
	}
	
}
