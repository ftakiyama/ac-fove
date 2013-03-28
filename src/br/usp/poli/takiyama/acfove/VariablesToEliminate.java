package br.usp.poli.takiyama.acfove;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

/**
 * @deprecated
 * Singleton class that serves as a buffer to store PRVs created
 * during splits operations on Aggregation Parfactors. These PRVs must be 
 * eliminated later.
 * <br>
 * <br>
 * <b>Attention:</b> this is a quick fix. It would be better to create a
 * class that represents the structure returned by Split; however it would
 * break compatibility with Parfactor interface. Thus, it would also require a
 * lot of code refactoring, which is time consuming and not a priority in the
 * moment.
 * 
 * @author ftakiyama
 *
 */
public final class VariablesToEliminate {
	
	private Set<ParameterizedRandomVariable> prvs;
	
	public static VariablesToEliminate buffer = new VariablesToEliminate();
	
	private VariablesToEliminate() {
		this.prvs = new HashSet<ParameterizedRandomVariable>();
	}
	
	public void add(ParameterizedRandomVariable prv) {
		prvs.add(prv);
	}
	
	public Iterator<ParameterizedRandomVariable> iterator() {
		return prvs.iterator();
	}
	
	public int size() {
		return prvs.size();
	}
	
	public void clear() {
		prvs.clear();
	}
}
