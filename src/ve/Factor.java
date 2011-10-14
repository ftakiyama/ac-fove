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
	
	public double getValue(String tuple) {
		Vector<String> t = new Vector<String>();
		String[] tupleSplit = tuple.split(" ");
		for (int i = 0; i < tupleSplit.length; i++) {
			t.add(tupleSplit[i]);
		}
		return assignment.get(t);
	}
	
	public void print() {
		for (int i = 0; i < this.variables.size(); i++) {
			System.out.format("%10s ", this.variables.get(i).getName()); 
		}
		System.out.format("%15s\n", "VALUE");
		System.out.println("----------------------------------------------------------------------");
		
		// Wow! The code below is really messy
		Vector<String> tuple = new Vector<String>();
		
		Vector<Vector<String>> allDomains = new Vector<Vector<String>>();
		
		for (int i = 0; i < this.variables.size(); i++) {
			allDomains.add(this.variables.get(i).getDomain()); 
		}
		cartesianProduct(allDomains, tuple);
		
		for (int i = 0; i < tuple.size(); i++) {
			String[] assignments = tuple.get(i).split(" ");
			for (int j = 0; j < assignments.length; j++) {
				System.out.format("%10s ", assignments[j]);
			}
			System.out.format("%15s\n", getValue(tuple.get(i)));
		}
		System.out.println();
	}
	
	/**
	 * This cartesian product algorithm has its flaws:
	 * 1) It is recursive
	 * 2) Variables names are not very clear
	 * 3) It concatenates Strings using the '+' operator (StringBuilder
	 *    will be more effective for large sets)
	 * 4) I am not completely satisfied with its beauty. Looks very inefficient,
	 * 	  even though I have not tested it for large sets. 
	 * 5) It's in the wrong class! Create an appropriate class for it.
	 * 
	 * There is one advantage: it works as far as I can tell.
	 */
	public void cartesianProduct(Vector<Vector<String>> sets, Vector<String> result) {
		if (sets.size() > 2) {
			Vector<String> aux = sets.remove(0);
			Vector<String> resultAux = new Vector<String>();
			cartesianProduct(sets, result);
			for (int i = 0; i < aux.size(); i++) {
				for (int j = 0; j < result.size(); j++) {
					resultAux.add(aux.get(i) + " " + result.get(j));
				}
			}
			Iterator<String> auxElement = resultAux.iterator();
			result.clear();
			while (auxElement.hasNext()) {
				result.add(auxElement.next());
			}
		} else if (sets.size() == 2){
			for (int i = 0; i < sets.firstElement().size(); i++) {
				for (int j = 0; j < sets.lastElement().size(); j++) {
					result.add(sets.firstElement().get(i) + " " + sets.lastElement().get(j));
				}
			}
		} else {
			Iterator<String> auxElement = sets.firstElement().iterator();
			while (auxElement.hasNext()) {
				result.add(auxElement.next());
			}
		}
	}
}
