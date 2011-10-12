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
	
	public void print() {
		System.out.print("dom(" + this.name + ") = {");
		Iterator<String> it = domain.iterator();
		while (it.hasNext()) {
			System.out.print(it.next() + ", ");
		}
		System.out.print("}");
	}
}
