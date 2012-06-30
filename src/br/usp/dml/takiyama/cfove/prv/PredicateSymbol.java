package br.usp.dml.takiyama.cfove.prv;

import java.util.ArrayList;
import java.util.Arrays;
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
	private final ArrayList<String> range;
	
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
			this.range = new ArrayList<String>(Arrays.asList(rangeValues));
		}
	}
	
	/**
	 * Constructor. Creates a Predicate Symbol using the same elements of
	 * the specified predicate symbol.
	 * @param s The predicate symbol to be copied
	 */
	PredicateSymbol(PredicateSymbol s) {
		this.name = s.name;
		this.range = new ArrayList<String>(s.range);
	}
	
	/**
	 * Returns an iterator over the range of the predicate symbol.
	 * @return An iterator over the range of the predicate symbol.
	 */
	Iterator<String> getRangeIterator() {
		return this.range.iterator();
	}
	
	/**
	 * Returns a copy of the range of the predicate symbol.
	 * @return A copy of the range of the predicate symbol.
	 */
	ArrayList<String> getRange() {
		return new ArrayList<String>(range);
	}
	
	/**
	 * Returns the name of this Predicate Symbol.
	 * @return the name of this Predicate Symbol.
	 */
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof PredicateSymbol))
	    	return false;
	    // Tests if both have the same attributes
	    PredicateSymbol targetObject = (PredicateSymbol) other;
	    return (this.name == targetObject.name)
	    		&& ((this.range == null) ? (targetObject.range == null) : this.range.equals(targetObject.range));
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() + range.hashCode();
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
	
}
