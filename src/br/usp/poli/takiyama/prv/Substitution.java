package br.usp.poli.takiyama.prv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Substitution {
	
	private HashMap<LogicalVariable, Term> bindings;
	
	
	/**
	 * Creates an empty substitution.
	 */
	private Substitution() {
		bindings = new HashMap<LogicalVariable, Term>();
	}
	
	
	/**
	 * Constructor. Creates a substitution based on a single binding
	 * @param binding A {@link Binding} 
	 */
	private Substitution(Binding binding) {
		this();
		add(binding);
	}
	
	
	/**
	 * Constructor. Creates a substitution based on a list of bindings.
	 * @param bindings A {@link List} of {@link Binding}s. 
	 */
	private Substitution(List<Binding> bindings) {
		this();
		for (Binding binding : bindings) {
			add(binding);
		}
	}
	
	
	/**
	 * Static factory. Creates a substitution based on a list of bindings.
	 * @param bindings A {@link List} of {@link Binding}s.
	 * @return A new substitution built from the list given.
	 */
	public static Substitution getInstance(List<Binding> bindings) {
		return new Substitution(bindings);
	}
	
	
	/**
	 * Static factory. Creates a substitution based on a single binding.
	 * @param bindings A {@link Binding}.
	 * @return A new substitution built from the binding given.
	 */
	public static Substitution getInstance(Binding binding) {
		return new Substitution(binding);
	}
	
	
	/**
	 * Adds a new binding to this set of substitutions.
	 * @param b A {@link Binding}
	 * @throws IllegalArgumentException If the first term of the specified 
	 * binding is already being replaced in this substitution.
	 */
	private void add(Binding b) throws IllegalArgumentException {
		if (bindings.containsKey(b.firstTerm())) {
			throw new IllegalArgumentException(b.firstTerm() + " is already being replaced.");
		}
		bindings.put(b.firstTerm(), b.secondTerm());
	}
	
	
	/** 
	 * @deprecated
	 * Returns a set containing all the logical variables that are in the first
	 * element of each binding in this substitution. I.e., it returns all the
	 * logical variables that are being substituted.
	 * @return A set containing the logical variables that are being substituted
	 */
	public Set<LogicalVariable> getLogicalVariables() {
		return this.bindings.keySet();
	}
	
	
	/**
	 * Returns an iterator over the set containing all the logical 
	 * variables that are in the first
	 * element of each binding in this substitution. I.e., it returns all the
	 * logical variables that are being replaced.
	 * @return A set containing the logical variables that are being substituted
	 */
	public Iterator<LogicalVariable> getSubstitutedIterator() {
		return bindings.keySet().iterator();
	}
	
	
	/**
	 * Returns the replacement of a given logical variable in this substitution.
	 * For instance, if the substitution is given by the set 
	 * {X\Y, Z\r}, then calling this method with the parameter 'X' will
	 * return the logical variable 'Y'.
	 * @param substituted The variable being substituted
	 * @return The replacement of a given logical variable in this substitution
	 */
	public Term getReplacement(LogicalVariable substituted) {
		return bindings.get(substituted);
	}
	
	
	/**
	 * Returns true if this substitution contains the binding specified, false
	 * otherwise.
	 * @param binding The binding to search for.
	 * @return True if this substitution contaisn the binding specified, false
	 * otherwise.
	 */
	public boolean contains(Binding binding) {
		if (this.bindings.containsKey(binding.firstTerm())) {
			return this.bindings.get(binding.firstTerm()).equals(binding);
		} else {
			return false;
		}
	}
	
	
	/**
	 * Returns true if the specified logical variables have the same replacement
	 * in this substitution. That is, if the specified variables are X and Y,
	 * this method returns true if there is a term t such that X/t and Y/t are
	 * both in this substitution. 
	 * <br>
	 * If such term is not found, or if one or both logical variables have no
	 * replacements in this substitution, this method returns false.
	 * @param firstVariable The first variable to compare
	 * @param secondVariable The second variable to compare
	 * @return True if the specified logical variables have a common replacement,
	 * false otherwise.
	 */
	public boolean hasCommonReplacement(LogicalVariable firstVariable, LogicalVariable secondVariable) {
		if (this.bindings.containsKey(firstVariable) && this.bindings.containsKey(secondVariable)) {
			return this.bindings.get(firstVariable).equals(this.bindings.get(secondVariable));
		} else {
			return false;
		}
	}
	
	
	/**
	 * Returns true if there are no elements in this substitution.
	 * @return True if this set is empty (it has no bindings), false otherwise.
	 */
	public boolean isEmpty() {
		return this.bindings.isEmpty();
	}
	
	
	/**
	 * Returns true if this substitution unifies v1 and v2.
	 * 
	 * <p>
	 * A substitution &theta; is a unifier of two parameterized random variables 
	 * f (ti1,...,tik ) and f(tj1,...,tjk) if 
	 * f(ti1,...,tik)[&theta;] = f(tj1,...,tjk)[&theta;].
	 * </p>
	 * <p> 
	 * We then say that the two parameterized random variables unify [Kisynski, 2010].
	 * </p>
	 * @param v1 The first {@link ParameterizedRandomVariable}.
	 * @param v2 The second {@link ParameterizedRandomVariable}.
	 * @return True if this substitution unifies v1 and v2, false otherwise.
	 */
	public boolean isUnifier(ParameterizedRandomVariable v1, ParameterizedRandomVariable v2) {		
		return v1.applySubstitution(this).equals(v2.applySubstitution(this));
	}
	
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("{ ");
		Iterator<LogicalVariable> bindings = this.bindings.keySet().iterator();
		
		while (bindings.hasNext()) {
			LogicalVariable lv = bindings.next();
			result.append("( " + lv.toString() + "/" + this.bindings.get(lv) + " ) ");
		}
		result.append("}");
		
		return result.toString();
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Substitution))
			return false;
		Substitution otherSubstitution = (Substitution) other;
		return this.bindings.equals(otherSubstitution.bindings);
	}
	
	
	@Override
	public int hashCode() {
		int result = 17;
		for (Entry<LogicalVariable, Term> entry : this.bindings.entrySet()) {
			result = 31 * result + entry.getKey().hashCode();
			result = 31 * result + entry.getValue().hashCode();
		}
		return result;
	}
}
