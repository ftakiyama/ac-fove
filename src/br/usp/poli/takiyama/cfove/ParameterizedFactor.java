package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.usp.poli.takiyama.cfove.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.ve.Tuple;

public class ParameterizedFactor {
	private final String name;
	private final ArrayList<ParameterizedRandomVariable> variables;
	private final ArrayList<Double> mapping;
	
	/**
	 * Private constructor. Creates a new factor on parameterized random variables.
	 * @param name The name of the factor.
	 * @param variables A list of the parameterized random variables of this factor.
	 * @param mapping The values of the factor.
	 * @throws IllegalArgumentException if there is an inconsistency
	 * between the size of the factor and the set of parameterized random variables.
	 */
	private ParameterizedFactor(
			String name, 
			List<ParameterizedRandomVariable> variables,
			List<Number> mapping) 
			throws IllegalArgumentException {
		
		this.name = name;
		this.variables = new ArrayList<ParameterizedRandomVariable>(variables);
		
		// TODO How to convert a list of Numbers to a list of Doubles?
		ArrayList<Double> temp = new ArrayList<Double>();
		for (Number n : mapping) {
			temp.add(n.doubleValue());
		}
		this.mapping = new ArrayList<Double>(temp);
		
		// Ugly, but necessary
		int factorSize = 1;
		for (ParameterizedRandomVariable prv : variables) {
			factorSize *= prv.getRangeSize();
		}
		if (mapping.size() != 0 && factorSize != mapping.size()) {
			throw new IllegalArgumentException("The mapping does " +
					"not have the required number of values. Expected: " + 
					factorSize + " received: " + mapping.size());
		}
		
		
	}
	
	/**
	 * Static factory to get a new instance of Parameterized Factors.
	 * @param name The name of the factor.
	 * @param variables A list of the parameterized random variables of this factor.
	 * @param mapping The values of the factor.
	 * @return An instance of ParameterizedFactor.
	 * @throws IllegalArgumentException if there is an inconsistency
	 * between the size of the factor and the set of parameterized random variables.
	 */
	public static ParameterizedFactor getInstance(
			String name, 
			List<ParameterizedRandomVariable> variables,
			List<Number> mapping) 
			throws IllegalArgumentException {
		return new ParameterizedFactor(name, variables, mapping);
	}
	
	
	
	/**
	 * Returns the index of a tuple.
	 * @param tuple The tuple to search.   
	 * @return The index of a tuple in this factor.
	 * @throws IllegalArgumentException if the tuple is empty.
	 */
	public int getTupleIndex(Tuple tuple) throws IllegalArgumentException {
		if (tuple.isEmpty()) {
			throw new IllegalArgumentException("This tuple is empty!");
		} else if (tuple.size() == 1) {
			return tuple.get(0).intValue();
		} else {
			int lastPosition = tuple.size() - 1;
			Integer lastIndex = tuple.get(lastPosition);
			int domainSize = this.variables.get(lastPosition).getRangeSize();
			return lastIndex + domainSize * getTupleIndex(tuple.subTuple(0, lastPosition));
		}
	}
	
	/**
	 * Returns a tuple given its index.
	 * @param index The index of the tuple.
	 * @return A tuple at the position specified by the parameter <b>index</b>.
	 */
	public Tuple getTuple(int index) {
		ArrayList<Integer> tuple = new ArrayList<Integer>();
		for (int j = variables.size() - 1; j > 0; j--) {
			int domainSize = this.variables.get(j).getRangeSize();
			tuple.add(index % domainSize);
			index = index / domainSize;	
		}
		tuple.add(index);
		Collections.reverse(tuple);
		return new Tuple(tuple);
	}
	
	@Override
	public String toString() {
		String result = this.name + "\n";
		
		if (this.variables.isEmpty()) {
			return this.name + " is empty.";
		}
		
		String thinRule = "";
		String thickRule = "";
		String cellFormat = "%-10s"; 
		String valueCellFormat = "%-10s\n";
		
		// Create the rules - aesthetic
		for (int i = 0; i <= this.variables.size(); i++) {
			thinRule += String.format(cellFormat, "").replace(" ", "-");
		}
		thickRule = thinRule.replace("-", "=");
		
		// Top rule
		result += thickRule + "\n";
		
		// Print the variables names
		for (ParameterizedRandomVariable prv : this.variables) {
			result += String.format(cellFormat, prv.getName()); 
		}
		
		// Value column
		result += String.format(cellFormat + "\n", "VALUE");
		
		// Mid rule
		result += thinRule + "\n";
		
		// Print the contents
		for (int i = 0; i < this.mapping.size(); i++) {
			Tuple tuple = this.getTuple(i);
			for (int j = 0; j < tuple.size(); j++) {
				ParameterizedRandomVariable currentRandomVariable = this.variables.get(j);
				int domainIndex = tuple.get(j);
				String domainValue = currentRandomVariable.getElementFromRange(domainIndex);
				result += String.format(cellFormat, domainValue);
			}
			// Round the value to 6 digits
			result += String.format(valueCellFormat, this.mapping.get(i).toString());			
		}
		
		// Bottom rule
		result += thickRule + "\n";
		
		return result;
	}
	
	/**
	 * Returns the number of values in this factor, which is the same as the
	 * number of tuples in this factor.
	 * @return The number of values in this factor.
	 */
	public int size() {
		return this.mapping.size();
	}
	
	/**
	 * Returns the index of a random variable in this factor, or -1 if
	 * there is no such random variable in this factor.
	 * @param randomVariable The random variable to search for
	 * @return The index of the random variable in this factor, or -1 if
	 * this factor does not contain the random variable
	 */
	public int getParameterizedRandomVariableIndex(ParameterizedRandomVariable randomVariable) {
		return this.variables.indexOf(randomVariable);
	}
	
	/**
	 * Returns the name of this factor.
	 * @return The name of this factor.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns a copy of list of random variables in this factor.
	 * @return A copy of list of random variables in this factor.
	 */
	public ArrayList<ParameterizedRandomVariable> getParameterizedRandomVariables() {
		return new ArrayList<ParameterizedRandomVariable>(this.variables);
	}
	
	/**
	 * Returns the value of the tuple specified by its index.
	 * @param index The index of the tuple in this factor.
	 * @return The value of the tuple specified by its index.
	 */
	public Number getTupleValue(int index) {
		return this.mapping.get(index);
	}
	
	/**
	 * Returns true if the factor is a sub-factor of the specified factor.
	 * @return True if the factor is a sub-factor of the specified factor.
	 */
	public boolean isSubFactorOf(ParameterizedFactor factor) {
		// Quick check
		if (factor.variables.size() < this.variables.size())
			return false;
		
		Iterator<ParameterizedRandomVariable> it = this.variables.iterator();
		while (it.hasNext())
			if (!factor.variables.contains(it.next()))
				return false;
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof ParameterizedFactor))
	    	return false;
	    // Tests if both have the same attributes
	    ParameterizedFactor targetObject = (ParameterizedFactor) other;
	    return this.name.equals(targetObject.name) &&
	    	   ((this.variables == null) ? 
	    		 targetObject.variables == null : 
	    		 this.variables.equals(targetObject.variables)) &&
    		   ((this.mapping == null) ? 
    		     targetObject.mapping == null : 
    		     this.mapping.equals(targetObject.mapping));	    		
	}
	
	@Override
	public int hashCode() { // Algorithm extracted from Bloch,J. Effective Java
		int result = 17;
		result = 31 + result + name.hashCode();
		result = 31 + result + Arrays.hashCode(variables.toArray(new ParameterizedRandomVariable[variables.size()]));
		result = 31 + result + Arrays.hashCode(mapping.toArray(new Double[mapping.size()]));
		return result;
	}
}
