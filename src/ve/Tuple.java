/**
 * 
 */
package ve;

import java.util.Vector;

/**
 * A tuple is any combination of random variables values.
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
}
