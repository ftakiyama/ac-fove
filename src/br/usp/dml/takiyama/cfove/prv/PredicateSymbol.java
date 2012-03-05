package br.usp.dml.takiyama.cfove.prv;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * A predicate symbol is a word that starts with a lower-case letter. 
 * Constants and predicate symbols are distinguishable by their context in a 
 * knowledge base. 
 * For example, www, m456, x, father can be constants or predicate symbols, 
 * depending on the context; 123 is a constant. [Poole, 2010] 
 * @author ftakiyama
 *
 */
class PredicateSymbol {
	private final String name;
	private final TreeSet<String> range;
	
	/**
	 * Constructor. Creates a Predicate Symbol.
	 * @param name The name of the symbol
	 */
	PredicateSymbol(String name, String... rangeValues) throws IllegalArgumentException {
		if (!Character.isLowerCase(name.charAt(0))) {
			throw new IllegalArgumentException("Exception while creating " +
					"PredicateSymbol: '" + name + "' must start with " +
					"lowercase letter.");
		} else {
			this.name = name;
			this.range = new TreeSet<String>();
			for (String value : rangeValues) {
				this.range.add(value);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("range(" + this.name + ") = { ");
		for (String value : this.range) {
			result.append(value).append(" ");
		}
		result.append("}");
		return result.toString();
	}
	
	
	Iterator<String> getRange() {
		return this.range.iterator();
	}
	
	/**
	 * Returns the name of this Predicate Symbol.
	 * @return the name of this Predicate Symbol.
	 */
	public String getName() {
		return this.name;
	}
}
