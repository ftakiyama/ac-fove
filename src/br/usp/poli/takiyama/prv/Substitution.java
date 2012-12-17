package br.usp.poli.takiyama.prv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import br.usp.poli.takiyama.prv.LogicalVariable;

public class Substitution {
	private HashMap<LogicalVariable, Term> bindings;
	
	/**
	 * Static factory. Creates a substitution based on a list of bindings.
	 * @param bindings A {@link List} of {@link Binding}s.
	 * @return A new substitution built from the list given.
	 */
	public static Substitution create(List<Binding> bindings) {
		return new Substitution(bindings);
	}
	
	/**
	 * Constructor. Creates a substitution based on a list of bindings.
	 * @param bindings A {@link List} of {@link Binding}s. 
	 */
	private Substitution(List<Binding> bindings) {
		this.bindings = new HashMap<LogicalVariable, Term>();
		for (Binding bind : bindings) {
			this.bindings.put(bind.getFirstTerm(), bind.getSecondTerm());
		}
	}
	
	/**
	 * Adds a new binding to this set of substitutions.
	 * @param v A {@link LogicalVariable}
	 * @param t A {@link Term}
	 */
	public void add(LogicalVariable v, Term t) {
		this.bindings.put(v, t);
	}
	
	/**
	 * **DO NOT USE** Returns a set containing all the logical variables that are in the first
	 * element of each binding in this substitution. I.e., it returns all the
	 * logical variables that are being substituted.
	 * @return A set containing the logical variables that are being substituted
	 */
	public Set<LogicalVariable> getLogicalVariables() {
		return this.bindings.keySet();
	}
	
	/**
	 * Weird name. Returns an iterator over the set containing all the logical 
	 * variables that are in the first
	 * element of each binding in this substitution. I.e., it returns all the
	 * logical variables that are being substituted.
	 * @return A set containing the logical variables that are being substituted
	 */
	public Iterator<LogicalVariable> getSubstitutedIterator() {
		return this.bindings.keySet().iterator();
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
		return this.bindings.get(substituted);
	}
	
	/**
	 * Returns true if this substitution contains the binding specified, false
	 * otherwise.
	 * @param binding The binding to search for.
	 * @return True if this substitution contaisn the binding specified, false
	 * otherwise.
	 */
	public boolean contains(Binding binding) {
		if (this.bindings.containsKey(binding.getFirstTerm())) {
			return this.bindings.get(binding.getFirstTerm()).equals(binding);
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
	public boolean haveCommonReplacement(LogicalVariable firstVariable, LogicalVariable secondVariable) {
		if (this.bindings.containsKey(firstVariable) && this.bindings.containsKey(secondVariable)) {
			return this.bindings.get(firstVariable).equals(this.bindings.get(secondVariable));
		} else {
			return false;
		}
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
	
	/**
	 * Returns true if this substitution unifies v1 and v2.<br>
	 * A substitution θ is a unifier of two parameterized random variables 
	 * f (ti1,...,tik ) and f(tj1,...,tjk) if f(ti1,...,tik)[θ] = f(tj1,...,tjk)[θ]. 
	 * We then say that the two parameterized random variables unify [Kisynski, 2010].
	 * @param v1 The first {@link ParameterizedRandomVariable}.
	 * @param v2 The second {@link ParameterizedRandomVariable}.
	 * @return True if this substitution unifies v1 and v2, false otherwise.
	 */
	public boolean isUnifier(ParameterizedRandomVariable v1, ParameterizedRandomVariable v2) {		
		return v1.applySubstitution(this).equals(v2.applySubstitution(this));
	}

	
}
