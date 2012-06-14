package br.usp.dml.takiyama.ve;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class represents random variables.<br>
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
	 * Private constructor.
	 * Creates a random variable given its name and the mapping from the
	 * elements of the domain to real numbers.
	 * @param name The name of this variable
	 * @param domain The domain of this random variable
	 * @param values A mapping from the elements of the domain to the real
	 * numbers.
	 */
	private RandomVariable(String name, ArrayList<String> domain, ArrayList<BigDecimal> values) {
		this.name = name;
		this.domain = new ArrayList<String>(domain);
		this.values = new ArrayList<BigDecimal>(values);
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
	public BigDecimal getValue(String elementFromDomain) {
		if (this.domain.contains(elementFromDomain)) {
			return this.values.get(this.domain.indexOf(elementFromDomain));
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		
		String result = "";
		
		// Create the rule - aesthetical   
		int maxLength = 0;
		for (String elementFromDomain : this.domain) {
			if (elementFromDomain.length() > maxLength) {
				maxLength = elementFromDomain.length();
			}
		}
		String valueCellFormat = "%-10s\n";
		String cellFormat = "%-"+ Integer.toString(maxLength + 4) + "s";
		String midRule = String.format(cellFormat, "").replace(" ", "-") 
			+ String.format(valueCellFormat, "").replace(" ", "-");
		String thickRule = midRule.replace("-", "=");
		
		// Prints the top rule
		result += thickRule;
		
		// Prints the name of the variable
		result += this.name + "\n";
		
		// Prints the mid rule
		result += midRule;
		
		// Print the elements from the domain and their values
		for (String elementFromDomain : this.domain) {
			result += String.format(cellFormat, elementFromDomain);
			result += String.format(valueCellFormat, getValue(elementFromDomain).doubleValue());
		}
		
		// Prints the bottom rule
		result += thickRule + "\n";
		
		return result;
	}
}
