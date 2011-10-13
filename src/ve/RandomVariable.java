package ve;

import java.util.Vector;
import java.util.Iterator;

/**
 * This class represents random variables. For now, they only have names.
 * @author ftakiyama
 *
 */
public class RandomVariable {
	private String name;
	private Vector<String> domain;
	
	/**
	 * Super basic constructor. Creates an empty random variable.
	 */
	public RandomVariable() {
		this.name = new String();
		this.domain = new Vector<String>();
	}
	
	/**
	 * Basic constructor.
	 * @param name The name of the random variable.
	 * @param domain A set of strings, each representing an element of the domain.
	 */
	public RandomVariable(String name, Vector<String> domain) {
		this.name = name;
		this.domain = new Vector<String>();
		this.domain = domain;
	}
	
	/**
	 * A constructor to be used when creating random variables specified in text files.
	 * I think it will be helpful (or not...). 
	 * @param name The name of the random variable.
	 * @param domain A set of strings, each representing an element of the domain.
	 */
	public RandomVariable(String name, String[] domain) {
		this.name = name;
		this.domain = new Vector<String>();
		for (int i = 0; i < domain.length; i++) {
			this.domain.add(domain[i]);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Vector<String> getDomain() {
		return domain;
	}
	
	/**
	 * Prints this random variable. It simply outputs its name and the 
	 * elements of the domain.
	 */
	public void print() {
		String result = new String();
		result = "dom(" + this.name + ") = {";
		Iterator<String> it = domain.iterator();
		while (it.hasNext()) {
			result += it.next() + ", ";
		}
		result = result.substring(0, result.length() - 2);
		result += "}";
		System.out.print(result);
	}
}
