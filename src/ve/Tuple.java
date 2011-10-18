/**
 * 
 */
package ve;

import java.util.Vector;

/**
 * A tuple is any combination of random variables domain values.
 * @author ftakiyama
 *
 */
public class Tuple {
	private Vector<String> tuple;

	public Tuple() {
		this.tuple = new Vector<String>();
	}
	
	/**
	 * Constructor. Creates a tuple based on a space separated string.
	 * @param values A space separated list of values.
	 */
	public Tuple(String values) {
		this.tuple = new Vector<String>();
		String[] v = values.split(" ");
		for (int i = 0; i < v.length; i++) {
			this.tuple.add(v[i]);
		}
	}

	public Vector<String> getTuple() {
		return tuple;
	}

	public void setTuple(Vector<String> tuple) {
		this.tuple = tuple;
	}
	
	public void setTuple(String tuple) {
		String tupleSplit[] = tuple.split(" ");
		this.tuple.clear();
		for (int i = 0; i < tupleSplit.length; i++) {
			this.tuple.add(tupleSplit[i]);
		}
	}
	
}
