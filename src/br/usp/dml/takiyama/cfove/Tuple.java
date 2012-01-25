/**
 * 
 */
package br.usp.dml.takiyama.cfove;

import java.util.Iterator;
import java.util.Vector;

/**
 * A tuple is any combination of values of random variables.
 * For example, consider three random variables x1, x2, x3, all binary. So a
 * valid tuple (x1, x2, x3) is (true, false, false).
 * @author ftakiyama
 *
 */
public class Tuple {
	private Vector<String> tuple;
	
	/**
	 * Constructor. Creates a tuple based on a space separated string.
	 * @param values A list of space separated values.
	 */
	public Tuple(String values) {
		this.tuple = new Vector<String>();
		String[] v = values.split(" ");
		for (int i = 0; i < v.length; i++) {
			this.tuple.add(v[i]);
		}
	}
	
	/**
	 * Constructor. Creates a tuple based on another tuple and a new element
	 * that will be put at the position specified by index.
	 * @param tuple The base tuple.
	 * @param index The index of the new element of the tuple.
	 * @param value The value of the new element in the tuple.
	 */
	public Tuple(Tuple tuple, int index, String value) {
		this.tuple = tuple.getTuple();
		this.tuple.add(index, value);
	}
	
	/**
	 * Constructor. Creates a tuple based on slice of another tuple. 
	 * @param tuple The base tuple
	 * @param fromIndex The index of the beginning of the slice  
	 * @param toIndex The index of the end of the slice
	 */
	public Tuple(Tuple tuple, int fromIndex, int toIndex) {
		this.tuple = new Vector<String>();
		for (int cursor = fromIndex; cursor < toIndex; cursor++) {
			this.tuple.add(tuple.getElement(cursor));
		}
	}
	
	/**
	 * Constructor. Creates a tuple based on another tuple and a list of
	 * indexes that will be added in the new tuple. Example: if we have the
	 * tuple (x0, x1, x2, x3) and the list {0, 2}, the new tuple will be
	 * (x0, x2).
	 * @param tuple The base tuple
	 * @param indexesToAdd A list of indexes that are to be kept in the base
	 * tuple. All the other elements will be erased. 
	 */
	public Tuple(Tuple tuple, Vector<Integer> indexesToAdd) {
		this.tuple = new Vector<String>();
		Iterator<Integer> it = indexesToAdd.iterator();
		while (it.hasNext()) {
			this.tuple.add(tuple.getElement(it.next()));
		}
	}

	/**
	 * Return a clone of this tuple. The cast is unchecked but I know that it's
	 * safe.  
	 * @return A Vector of Strings representing this tuple.
	 */
	@SuppressWarnings("unchecked")
	public Vector<String> getTuple() {
		return (Vector<String>) tuple.clone();
	}
	
	/**
	 * Returns the element at the specified position in this Tuple.
	 * @param index Index of the element o return
	 * @return The String at the specified index
	 */
	public String getElement(int index) {
		return this.tuple.get(index);
	}
	
	/**
	 * Returns the tuple's size
	 * @return The tuple's size
	 */
	public int getTupleSize() {
		return this.tuple.size();
	}
	
	/**
	 * Prints the tuple
	 */
	public void print() {
		Iterator<String> it = this.tuple.iterator();
		System.out.print("( ");
		while (it.hasNext()) {
			System.out.print(it.next() + " ");
		}
		System.out.print(")");
	}
}
