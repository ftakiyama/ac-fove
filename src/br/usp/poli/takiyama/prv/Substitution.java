package br.usp.poli.takiyama.prv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class Substitution implements SubstitutionInterface {
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
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("{ ");
		Iterator<LogicalVariable> bindings = this.bindings.keySet().iterator();
		
		while (bindings.hasNext()) {
			LogicalVariable lv = bindings.next();
			result.append("( " + lv.toString() + " " + this.bindings.get(lv) + " ) ");
		}
		result.append("}");
		
		return result.toString();
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
