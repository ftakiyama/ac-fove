/**
 * 
 */
package ve;

import java.util.Iterator;
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
	private Hashtable<Vector<String>, Double> assignment; //each element in this object represents a line in the table 
	
	/**
	 * Default constructor. Creates an empty Factor.
	 */
	public Factor() {
		this.variables = new Vector<RandomVariable>();
		this.assignment = new Hashtable<Vector<String>, Double>();
	}
	
	//TODO: how to initialize this factor in an efficient manner?
	public Factor(Vector<RandomVariable> vars) {
		this.variables = new Vector<RandomVariable>();
		this.assignment = new Hashtable<Vector<String>, Double>();		
		Iterator<RandomVariable> it = vars.iterator();
		while (it.hasNext()) {
			this.variables.add(it.next());
		}
	}
	
	public void addAssignment(Tuple tuple, double value) {
		this.assignment.put(tuple.getTuple(), value);
	}
	
	public void addAssignment(Vector<String> tuple, double value) {
		this.assignment.put(tuple, value);
	}
	
	public boolean contains(RandomVariable x) {
		return variables.contains(x);
	}
	
	public double getValue(Tuple tuple) {
		return assignment.get(tuple.getTuple());
	}
	
	// Very ugly
	public double getValue(String tuple) {
		Vector<String> t = new Vector<String>();
		String[] assignments = tuple.split(" ");
		for (int i = 0; i < assignments.length; i++) {
			t.add(assignments[i]);
		}
		return assignment.get(t);
	}
	
	/**
	 * Get the index of a given random variable in this factor.
	 * @param v The random variable to be searched 
	 * @return The index of v in this factor, or -1 if this variable does not
	 * exist in this factor.
	 */
	public int getVariableIndex(RandomVariable v) {
		return this.variables.indexOf(v); 
	}
	
	/**
	 * Returns a copy of the vector containing the variables of this factor.
	 * @return A copy of the vector containing the variables of this factor.
	 */
	@SuppressWarnings("unchecked")
	public Vector<RandomVariable> getVariables() {
		return (Vector<RandomVariable>) this.variables.clone();
	}
	
	public void removeVariable(RandomVariable variable) {
		this.variables.remove(variable);
	}
	
	/**
	 * Print the factor in a tabular form.
	 * For now, it only prints in the console.  
	 */
	public void print() {
		String midRule = new String();
		String thickRule = new String();
		String cellFormat = "%-10s"; 
		String valueCellFormat = "%-10s\n";
		
		// Create the rule - aesthetical   
		for (int i = 0; i <= this.variables.size(); i++) {
			midRule += String.format(cellFormat, "").replace(" ", "-");
		}
		thickRule = midRule.replace("-", "=");
		
		// Top rule
		System.out.println(thickRule);
		
		// Print the variables names
		for (int i = 0; i < this.variables.size(); i++) {
			System.out.format(cellFormat, this.variables.get(i).getName()); 
		}
		
		// Value column
		System.out.format(cellFormat + "\n", "VALUE");
		
		// Mid rule
		System.out.println(midRule);
		
		// Auxiliary variables to create the cartesian product
		Vector<String> tuple = new Vector<String>();
		Vector<Vector<String>> allDomains = new Vector<Vector<String>>();
		for (int i = 0; i < this.variables.size(); i++) {
			allDomains.add(this.variables.get(i).getDomain()); 
		}
		
		// Create the cartesian product of all variable's domains
		SetHandler.cartesianProduct(allDomains, tuple);
		
		// Finally, print the contents
		for (int i = 0; i < tuple.size(); i++) {
			String[] assignments = tuple.get(i).split(" ");
			for (int j = 0; j < assignments.length; j++) {
				System.out.format(cellFormat, assignments[j]);
			}
			System.out.format(valueCellFormat, getValue(tuple.get(i)));
		}
		
		// Bottom rule
		System.out.println(thickRule + "\n");
	}
	
}
