package br.usp.poli.takiyama.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A set of parfactors that represents a joint probability distribution.
 * <p>
 * Classes that implement this interface should not allow <code>null</code> 
 * elements.
 * </p>
 * 
 * @author Felipe Takiyama
 *
 */
public interface Distribution {
	
	/**
	 * Returns true if this distribution contains the specified element.
	 * 
	 * @param o Element whose presence in this set is to be tested 
	 * 
	 * @return <code>true</code> if this set contains the specified element 
	 * 
	 * @throws NullPointerException If the specified element is null
	 */
	public boolean contains(Object o) throws NullPointerException;
	
	/**
	 * Returns <code>true</code> if this distribution contains all of the 
	 * elements of the specified distribution.
	 *  
	 * @param d Distribution to be checked for containment in this distribution 
	 * 
	 * @return <code>true</code> if this distribution contains all of the 
	 * elements of the specified distribution 
	 * 
	 * @see Set#containsAll(Collection)
	 */
	public boolean containsAll(Distribution d);
	
	/**
	 * Returns <code>true</code> if this set contains no elements.
	 * 
	 * @return <code>true</code> if this set contains no elements.
	 */
	public boolean isEmpty();
	
	/**
	 * Returns the number of elements in this distribution.
	 * 
	 * @return The number of elements in this distribution.
	 */
	public int size();
	
	/**
	 * Returns an iterator over the elements in this distribution. The elements 
	 * are returned in no particular order.
	 * 
	 * @return An iterator over the elements in this distribution
	 */
	public Iterator<Parfactor> iterator();
	
	/**
	 * Returns a set containing all elements of this distribution.
	 * <p>
	 * The returned set will be "safe" in that no references to it are 
	 * maintained by this distribution. (In other words, this method must 
	 * allocate a new set even if this distribution is backed by an array). 
	 * The caller is thus free to modify the returned array. 
	 * </p>
	 * 
	 * @return A set containing all elements of this distribution.
	 */
	public Set<Parfactor> toSet();
	
	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();

	@Override
	public String toString();
}
