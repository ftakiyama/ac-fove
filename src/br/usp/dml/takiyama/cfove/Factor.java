/**
 * 
 */
package br.usp.dml.takiyama.cfove;

import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;

import br.usp.dml.takiyama.trash.SetHandler;

/**
 * Factors are functions that, given a tuple, return a real number.
 * It represents in a tabular form the joint distribution of a set of random
 * variables.
 * @author ftakiyama
 *
 */
public class Factor {
	private Vector<RandomVariable> variables;
	//private Hashtable<Vector<String>, Double> assignment;  
	// TODO Due to precision problems, use BigDecimal instead of Double
	private Hashtable<Integer, Double> assignment; //each element in this object represents a line in the table
	private int base; // The base represents the size of the largest domain among the variables of this factor
	
	/**
	 * Default constructor. Creates an empty Factor.
	 */
	public Factor() {
		this.variables = new Vector<RandomVariable>();
		this.assignment = new Hashtable<Integer, Double>();
		this.base = 0;
	}
	
	//TODO: how to initialize this factor in an efficient manner?
	public Factor(Vector<RandomVariable> vars) {
		this.variables = new Vector<RandomVariable>();
		this.assignment = new Hashtable<Integer, Double>();		
		this.base = 0;
		Iterator<RandomVariable> it = vars.iterator();
		while (it.hasNext()) {
			RandomVariable rv = it.next();
			this.variables.add(rv);
			if (rv.getDomainSize() > this.base) {
				this.base = rv.getDomainSize();
			}
		}
	}
	
	/**
	public void addAssignment(Tuple tuple, double value) {
		this.assignment.put(tuple.getTuple(), value);
	}
	
	public void addAssignment(Vector<String> tuple, double value) {
		this.assignment.put(tuple, value);
	}
	*/
	
	/**
	 * Raw method to put the assignment of a value in the factor
	 */
	public void addAssignment(Integer key, double value) {
		this.assignment.put(key, value);
	}
	
	/**
	 * Put an assignment in the factor.
	 * @param tuple The tuple to be inserted
	 * @param value The value corresponding to the tuple
	 */
	public void addAssignment(Tuple tuple, double value) {
		Integer key = calculateKey(tuple);
		this.assignment.put(key, value);
	}
	
	private Integer calculateKey(Tuple tuple) {
		Iterator<RandomVariable> it = variables.iterator();
		int tuplePosition = 0;
		int key = 0;
		while (it.hasNext()) {
			int digit = it.next().getElementIndex(tuple.getElement(tuplePosition));
			key += digit * Math.pow(base, tuplePosition);
			tuplePosition++;
		}
		return key;
	}
	
	
	public boolean contains(RandomVariable x) {
		return variables.contains(x);
	}
	
	public double getValue(Tuple tuple) {
		Integer key = calculateKey(tuple);
		return assignment.get(key);
	}
	
	/**
	 * Get the value corresponding to the specified tuple. 
	 * @param tuple A space separated String of values
	 * @return The value corresponding to the specified tuple
	 */
	public double getValue(String tuple) {
		Tuple t = new Tuple(tuple);
		Integer key = calculateKey(t);
		return assignment.get(key);
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
	 * Returns the number of random variables in the factor.
	 * @return The number of random variables in the factor.
	 */
	public int getVariablesAmount() {
		return this.variables.size();
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
