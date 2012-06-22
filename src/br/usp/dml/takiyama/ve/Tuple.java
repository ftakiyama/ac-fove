package br.usp.dml.takiyama.ve;

import java.util.ArrayList;

/**
 * A tuple is any combination of values of random variables.
 * For example, consider three random variables x1, x2, x3, all binary. So a
 * valid tuple (x1, x2, x3) is (true, false, false).
 * <br>
 * This implementation store indexes of the values instead of the values. The
 * index of a value is an integer representing the position of that value in
 * the domain of the random variable. If the domain of the 
 * {@link RandomVariable} is not ordered, then this class is useless.
 * 
 * @author ftakiyama
 *
 */
public final class Tuple {
	private final ArrayList<Integer> values;
	
	/**
	 * Constructor. Creates a tuple based on a array of integers.
	 * @param values The values of this tuple.
	 */
	public Tuple(ArrayList<Integer> values) {
		this.values = new ArrayList<Integer>(values);
	}
	
	/**
	 * Returns true if this tuple contains no elements or is null.
	 * @return True if this tuple has no elements or is null.
	 */
	public boolean isEmpty() {
		if (this.values == null || this.values.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the number of elements in this tuple.
	 * If this tuple is empty or null, return 0.
	 * @return The number of elements in this tuple. If this tuple is empty or 
	 * null, return 0.
	 */
	public int size() {
		if (this.isEmpty()) {
			return 0;
		} else {
			return this.values.size();
		}
	}
	
	/**
	 * Returns the element at the specified position in this list. 
	 * @param index Index of the element to return.
	 * @return Returns the element at the specified position in this list.
	 */
	public Integer get(int index) {
		return new ArrayList<Integer>(values).get(index);
	}
	
	
	/**
	 * Removes the element at the specified index. All values to the right
	 * of the index will be shifted to left.
	 * @see {@link ArrayList#remove}
	 * @param index The index of the element to be removed.
	 * @return This tuple with the element specified removed.
	 */
	public Integer remove(int index) {
		return new ArrayList<Integer>(values).remove(index);
	}
	
	/**
	 * Returns a sub-tuple of this tuple between the specified 
	 * <code>fromIndex</code> (inclusive) and <code>toIndex</code>, (exclusive).
	 * 
	 * @param fromIndex low end point (inclusive) of the sub-tuple
	 * @param toIndex high end point (exclusive) of the sub-tuple
	 * @return A sub-tuple of this tuple
	 */
	public Tuple subTuple(int fromIndex, int toIndex) {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (int i = fromIndex; i < toIndex; i++) {
			temp.add(this.values.get(i));
		}
		return new Tuple(temp);
	}
	
	/**
	 * Returns a sub-tuple of this tuple composed by the elements given
	 * in the specified list of indexes.
	 * <br>
	 * For instance, let t = (0, 10, 20, 30) be a tuple. If we want the
	 * sub-tuple given by indexes {0, 2} then this method would return 
	 * t' = (0, 20).
	 * @param indexes A list of indexes to extract from this tuple.
	 * @return A sub-tuple of this tuple based on the list of indexes.
	 */
	public Tuple subTuple(int[] indexes) {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (int i = 0; i < indexes.length; i++) {
			temp.add(get(indexes[i]));
		}
		return new Tuple(temp); 
	}
	
	/**
	 * Replaces the element at the specified position in this tuple with the 
	 * specified element. The tuple is not modified, a new tuple is generated
	 * instead.
	 * @param index Index of the element to replace
	 * @param element Element to be stored at the specified position
	 * @return A new tuple with the element at the specified position replaced
	 * by the specified element.
	 */
	public Tuple getModifiedTuple(int index, Integer element) {
		ArrayList<Integer> temp = new ArrayList<Integer>(this.values);
		temp.set(index, element);
		return new Tuple(temp);
	}
	
	@Override
	public String toString() {
		return this.values.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof Tuple))
	    	return false;
	    // Tests if both have the same attributes
	    Tuple targetObject = (Tuple) other;
	    return ((this.values == null) ? targetObject.values == null : this.values.equals(targetObject.values));	    		
	}
	
	@Override
	public int hashCode() {
		return values.hashCode();
	}
}