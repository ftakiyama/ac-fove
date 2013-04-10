package br.usp.poli.takiyama.common;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public final class Tuple<E> {
	
	private final List<E> values;
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	private Tuple() {
		values = new ArrayList<E>(0);
	}
	
	
	private Tuple(List<E> values) {
		this.values = new ArrayList<E>(values);
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * Returns an empty tuple.
	 * 
	 * @return an empty tuple.
	 */
	public static <E> Tuple<E> getInstance() {
		return new Tuple<E>();
	}
	
	
	/**
	 * Returns a tuple with the elements of the specified list, in the same
	 * order.
	 * 
	 * @param values A list of elements to put in the tuple
	 * @return A tuple with the specified elements
	 */
	public static <E> Tuple<E> getInstance(List<E> values) {
		return new Tuple<E>(values);
	}
	
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	/**
	 * Returns the element at the specified position in this Tuple.
	 *  
	 * @param index Index of the element to return.
	 * @return Returns the element at the specified position in this tuple.
	 */
	public E get(int index) {
		return values.get(index);
	}
	
	
	/**
	 * Returns a sub-tuple of this tuple between the specified 
	 * <code>fromIndex</code> (inclusive) and <code>toIndex</code>, (exclusive).
	 * 
	 * @param fromIndex low end point (inclusive) of the sub-tuple
	 * @param toIndex high end point (exclusive) of the sub-tuple
	 * @return A sub-tuple of this tuple
	 */
	public Tuple<E> subTuple(int fromIndex, int toIndex) {
		List<E> temp = new ArrayList<E>(toIndex - fromIndex);
		for (int i = fromIndex; i < toIndex; i++) {
			temp.add(values.get(i));
		}
		return Tuple.getInstance(temp);
	}
	
	
	/**
	 * Returns a sub-tuple of this tuple composed by the elements given
	 * in the specified list of indexes.
	 * <p>
	 * For instance, let t = (0, 10, 20, 30) be a tuple. If we want the
	 * sub-tuple given by indexes {0, 2} then this method would return 
	 * t' = (0, 20).
	 * </p>
	 * 
	 * @param indexes A list of indexes to extract from this tuple.
	 * @return A sub-tuple of this tuple based on the list of indexes.
	 */
	public Tuple<E> subTuple(int[] indexes) {
		List<E> temp = new ArrayList<E>(values.size() - indexes.length);
		for (int i = 0; i < indexes.length; i++) {
			temp.add(get(indexes[i]));
		}
		return Tuple.getInstance(temp); 
	}
	

	/**
	 * Returns the number of elements in this tuple.
	 * 
	 * @return The number of elements in this tuple. 
	 */
	public int size() {
		return values.size();
	}
	
	
	/**
	 * Returns <code>true</code> if this tuple contains no elements or is null.
	 * 
	 * @return <code>True</code> if this tuple has no elements or is null,
	 * <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return (values == null || values.isEmpty());
	}
	
	
	/* ************************************************************************
	 *    Setters
	 * ************************************************************************/

	/**
	 * Removes the element at the specified index. All values to the right
	 * of the index will be shifted to left.
	 * 
	 * @see {@link ArrayList#remove}
	 * @param index The index of the element to be removed.
	 * @return A copy of this tuple with the element specified removed.
	 */
	public Tuple<E> remove(int index) {
		List<E> newValues = new ArrayList<E>(values);
		newValues.remove(index);
		return Tuple.getInstance(newValues);
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
	public Tuple<E> set(int index, E element) {
		List<E> temp = new ArrayList<E>(values);
		temp.set(index, element);
		return Tuple.getInstance(temp);
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	@Override
	public String toString() {
		return values.toString();
	}
	
		
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
	        return true;
	    if (!(obj instanceof Tuple))
	        return false;
	    ListIterator<E> e1 = values.listIterator();
		@SuppressWarnings("rawtypes")
	    ListIterator e2 = ((Tuple) obj).values.listIterator();
	    while(e1.hasNext() && e2.hasNext()) {
	        E o1 = e1.next();
	        Object o2 = e2.next();
	        if (!(o1 == null ? o2 == null : o1.equals(o2)))
	            return false;
	    }
	    return !(e1.hasNext() || e2.hasNext());
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}
}
