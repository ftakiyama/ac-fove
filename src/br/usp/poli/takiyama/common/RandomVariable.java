package br.usp.poli.takiyama.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A random variable is a function that, given an element from its domain,
 * returns a real number.
 * @author ftakiyama
 *
 */
public final class RandomVariable {
	
	private final String name;
	private final ArrayList<String> domain;
	private final ArrayList<BigDecimal> values;
	
	/**
	 * A static factory of random variables.
	 * Creates a random variable given its name and the mapping from the
	 * elements of the domain to real numbers.
	 * @param name The name of this variable
	 * @param domain The domain of this random variable
	 * @param values A mapping from the elements of the domain to the real
	 * numbers.
	 * @return A random variable.
	 */
	public static RandomVariable createRandomVariable(String name, ArrayList<String> domain, ArrayList<BigDecimal> values) {
		return new RandomVariable(name, domain, values);
	}
	
	/**
	 * A static factory of random variables.
	 * Creates a random variable given its name and the mapping from the
	 * elements of the domain to real numbers.
	 * @param name The name of this variable
	 * @param domain The domain of this random variable
	 * @return A random variable.
	 */
	public static RandomVariable createRandomVariable(String name, List<String> domain) {
		return new RandomVariable(name, domain);
	}
	
	/**
	 * Returns a copy of the specified random variable.
	 * @param rv The random variable to be copied
	 * @return A copy of the specified random variable.
	 */
	public static RandomVariable copyOf(RandomVariable rv) {
		return new RandomVariable(rv.name, rv.domain, rv.values);
	}
	
	/**
	 * Private constructor.
	 * Creates a random variable given its name and the mapping from the
	 * elements of the domain to real numbers.
	 * @param name The name of this variable
	 * @param domain The domain of this random variable
	 * @param values A mapping from the elements of the domain to the real
	 * numbers.
	 * @throws IllegalArgumentException if the number of values is not equal
	 * to the size of the domain
	 */
	private RandomVariable(String name, ArrayList<String> domain, ArrayList<BigDecimal> values) 
			throws IllegalArgumentException {
		this.name = name;
		this.domain = new ArrayList<String>(domain);
		this.values = new ArrayList<BigDecimal>(values);
		
		if (domain.size() != values.size()) {
			throw new IllegalArgumentException("The domain should have the" +
					" same number of elements as values. Domain size: " +
					domain.size() + ", Values size: " + values.size());
		}
	}
	
	/**
	 * Private constructor.
	 * Creates a random variable given its name and the mapping from the
	 * elements of the domain to real numbers.
	 * @param name The name of this variable
	 * @param domain The domain of this random variable
	 */
	private RandomVariable(String name, List<String> domain) {
		this.name = name;
		this.domain = new ArrayList<String>(domain);
		this.values = null;
	}
	
	/**
	 * Returns an iterator over the domain.
	 * @return An iterator over the domain of this random variable.
	 */
	public Iterator<String> getDomainIterator() {
		return new ArrayList<String>(this.domain).iterator();
	}
	
	/**
	 * Returns the size of the domain.
	 * @return The size of the domain.
	 */
	public int getDomainSize() {
		return this.domain.size();
	}
	
	/**
	 * Returns an element from the domain.
	 * @param index The index of the element from the domain.
	 * @return The element from the domain with the index specified.
	 */
	public String getElementFromDomain(int index) {
		return this.domain.get(index);
	}
	
	/**
	 * Returns the real number corresponding to an element from the domain.
	 * @param elementFromDomain An element from the domain
	 * @return The real number corresponding to an element from the domain.<br>
	 * If the parameter given does not exist in the domain, then returns <b>null</b>.
	 */
//	private BigDecimal getValue(String elementFromDomain) {
//		if (this.domain.contains(elementFromDomain)) {
//			return this.values.get(this.domain.indexOf(elementFromDomain));
//		} else {
//			return null;
//		}
//	}
	
	/**
	 * Returns the name of this random variable.
	 * @return The name of this random variable.
	 */
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		
//		String result = "";
//		
//		// Create the rule - aesthetical   
//		int maxLength = 0;
//		for (String elementFromDomain : this.domain) {
//			if (elementFromDomain.length() > maxLength) {
//				maxLength = elementFromDomain.length();
//			}
//		}
//		String valueCellFormat = "%-10s\n";
//		String cellFormat = "%-"+ Integer.toString(maxLength + 4) + "s";
//		String midRule = String.format(cellFormat, "").replace(" ", "-") 
//			+ String.format(valueCellFormat, "").replace(" ", "-");
//		String thickRule = midRule.replace("-", "=");
//		
//		// Prints the top rule
//		result += thickRule;
//		
//		// Prints the name of the variable
//		result += this.name + "\n";
//		
//		// Prints the mid rule
//		result += midRule;
//		
//		// Print the elements from the domain and their values
//		for (String elementFromDomain : this.domain) {
//			result += String.format(cellFormat, elementFromDomain);
//			result += String.format(valueCellFormat, getValue(elementFromDomain).doubleValue());
//		}
//		
//		// Prints the bottom rule
//		result += thickRule + "\n";
		
		return name + " # " + domain.toString();
	}

	@Override
	public boolean equals(Object other) {
	    // Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof RandomVariable))
	    	return false;
	    // Tests if both have the same attributes
	    RandomVariable targetObject = (RandomVariable) other;
	    
	    return (this.name.equals(targetObject.name)
	    		&& ((this.domain == null) ? targetObject.domain == null : this.domain.equals(targetObject.domain))
	    		&& ((this.values == null) ? targetObject.values == null : this.values.equals(targetObject.values)));	    		
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() + domain.hashCode();
	}
	
}
