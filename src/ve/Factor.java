/**
 * 
 */
package ve;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

/**
 * Factors are functions that, given a tuple, return a real number.
 * It represents in a tabular form the joint distribution of a set of random
 * variables.
 * @author ftakiyama
 *
 */
public class Factor {
	private Vector<RandomVariable> variables;
	private Hashtable<Vector<String>,Double> assignment; //each element in this object represents a line in the table 
	
	/**
	 * Default constructor. Creates an empty Factor.
	 */
	public Factor() {
		this.variables = new Vector<RandomVariable>();
		this.assignment = new Hashtable<Vector<String>,Double>();
	}
	
	//TODO: how to initialize this factor in an efficient manner?
	public Factor(Vector<RandomVariable> vars) {
		this.variables = new Vector<RandomVariable>();
		this.assignment = new Hashtable<Vector<String>,Double>();		
		Iterator<RandomVariable> it = vars.iterator();
		while (it.hasNext()) {
			this.variables.add(it.next());
		}
	}
	
	public void addAssignment(Vector<String> tuple, double value) {
		this.assignment.put(tuple, value);
	}
	
	public boolean contains(RandomVariable x) {
		return variables.contains(x);
	}
	
	public double getValue(Vector<String> tuple) {
		return assignment.get(tuple);
	}
	
	public void print() {
		for (int i = 0; i < this.variables.size(); i++) {
			System.out.format("%10s", this.variables.get(i).getName()); 
		}
		System.out.print("VALUE");
		System.out.println("----------------------------------------------------------------------");
		
		// How to create all the possible tuples? ... 
		Vector<String> tuple = new Vector<String>();
 		for (int i = 0; i < this.variables.size(); i++) {
			RandomVariable currentVariable = this.variables.get(i); 
			for (int j = 0; j < currentVariable.getDomain().size(); j++) {
				tuple.add(currentVariable.getDomain().get(j));
			}
		}
		
		
	}
}
