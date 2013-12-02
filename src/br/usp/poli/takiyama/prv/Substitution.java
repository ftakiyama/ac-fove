package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.utils.Lists;

public final class Substitution {
	
	private final Map<LogicalVariable, Term> bindings;
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	/**
	 * Creates an empty substitution.
	 */
	private Substitution() {
		bindings = new LinkedHashMap<LogicalVariable, Term>();
	}
	
	
	/**
	 * Creates a substitution based on a single binding
	 * @param binding A {@link Binding} 
	 */
	private Substitution(Binding binding) {
		this();
		add(binding);
	}
	
	
	/**
	 * Creates a substitution based on a list of bindings.
	 * @param bindings A {@link List} of {@link Binding}s. 
	 */
	private Substitution(List<Binding> bindings) {
		this();
		for (Binding binding : bindings) {
			add(binding);
		}
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/
	
	/**
	 * Returns a substitution based on a list of bindings.
	 * @param bindings A {@link List} of {@link Binding}s.
	 * @return A new substitution built from the list given.
	 */
	public static Substitution getInstance(List<Binding> bindings) {
		return new Substitution(bindings);
	}
	
	
	/**
	 * Returns a substitution based on a single binding.
	 * @param binding A {@link Binding}.
	 * @return A new substitution built from the binding given.
	 */
	public static Substitution getInstance(Binding binding) {
		return new Substitution(binding);
	}
	
	
	/**
	 * Returns a substitution based on two bindings.
	 * 
	 * @param b1 A {@link Binding}.
	 * @param b2 A {@link Binding}.
	 * @return A new substitution built from the binding given.
	 */
	public static Substitution getInstance(Binding b1, Binding b2) {
		List<Binding> args = Lists.listOf(b1, b2);
		return new Substitution(args);
	}
	
	
	/**
	 * Returns a substitution based on three bindings.
	 * 
	 * @param b1 A {@link Binding}.
	 * @param b2 A {@link Binding}.
	 * @param b3 A {@link Binding}.
	 * @return A new substitution built from the binding given.
	 */
	public static Substitution getInstance(Binding b1, Binding b2, Binding b3) {
		List<Binding> args = Lists.listOf(b1, b2, b3);
		return new Substitution(args);
	}
	
	
	/**
	 * Returns an empty substitution.
	 * 
	 * @return an empty substitution.
	 */
	public static Substitution getInstance() {
		return new Substitution();
	}
	
	
	/**
	 * Adds a new binding to this set of substitutions.
	 * @param b A {@link Binding}
	 * @throws IllegalArgumentException If the first term of the specified 
	 * binding is already being replaced in this substitution.
	 */
	private void add(Binding b) throws IllegalArgumentException {
		if (bindings.containsKey(b.firstTerm())) {
			throw new IllegalArgumentException(b.firstTerm() 
					+ " is already being replaced.");
		}
		bindings.put(b.firstTerm(), b.secondTerm());
	}
	
		
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

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
	 * <p>
	 * For instance, if the substitution is given by the set 
	 * {X/Y, Z/r}, then calling this method with the parameter 'X' will
	 * return the logical variable 'Y'.
	 * </p>
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
	 * @return True if this substitution contains the binding specified, false
	 * otherwise.
	 */
	public boolean contains(Binding binding) {
		LogicalVariable key = binding.firstTerm();
		boolean replacedExists = bindings.containsKey(key);
		boolean replacementExists = bindings.get(key).equals(binding.secondTerm());
		
		return  replacedExists && replacementExists;
	}
	
	
	/**
	 * Returns <code>true</code> if there is any binding in this substitution
	 * that replaces the specified {@link LogicalVariable}.
	 * 
	 * @param replaced The logical variable being replaced to search for
	 * @return <code>true</code> if there is any binding in this substitution
	 * that replaces the specified {@link LogicalVariable}, <code>false</code>
	 * otherwise.
	 */
	public boolean contains(LogicalVariable replaced) {
		return bindings.containsKey(replaced);
	}
	
	
	/**
	 * Returns <code>true</code> if there is any binding in this substitution
	 * containing the specified {@link Term}.
	 * <p>
	 * Note that this method differs from {@link #contains(LogicalVariable)} 
	 * in that all terms are searched for, not only the logical variables 
	 * being replaced.
	 * </p>
	 * 
	 * @param t The term to search for
	 * @return <code>true</code> if there is any binding in this substitution
	 * containing the specified {@link Term}, <code>false</code>
	 * otherwise.
	 */
	public boolean has(Term t) {
		return (bindings.containsKey(t) || bindings.containsValue(t));
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
	 * Returns <code>true</code> if this substitution unifies the specified
	 * {@link Prv}
	 * <p>
	 * A substitution &theta; is a unifier of two parameterized random variables 
	 * f (ti1,...,tik ) and f(tj1,...,tjk) if 
	 * f(ti1,...,tik)[&theta;] = f(tj1,...,tjk)[&theta;].
	 * </p>
	 * <p> 
	 * We then say that the two parameterized random variables unify [Kisynski, 2010].
	 * </p>
	 * @param prv1 The first {@link Prv}.
	 * @param prv2 The second {@link Prv}.
	 * @return <code>true</code> if this substitution the specified PRVs,
	 * <code>false</code> otherwise.
	 */
	public boolean isUnifier(Prv prv1, Prv prv2) {		
		return prv1.apply(this).equals(prv2.apply(this));
	}
	
	
	/**
	 * Returns the number of {@link Binding}s in this substitution.
	 * 
	 * @return the number of {@link Binding}s in this substitution.
	 */
	public int size() {
		return bindings.size();
	}
	
	
	/**
	 * Converts this Substitution to a {@link List} of {@link Binding}s.
	 * 
	 * @return this Substitution as a {@link List} of {@link Binding}s.
	 */
	public List<Binding> asList() {
		List<Binding> binds = new ArrayList<Binding>(bindings.size());
		for (LogicalVariable t1 : bindings.keySet()) {
			Binding b = Binding.getInstance(t1, bindings.get(t1));
			binds.add(b);
		}
		return binds;
	}
	
	
	/**
	 * Retrieves, but does not remove, the first {@link Binding} inserted in 
	 * this substitution, throwing an exception if this is empty.
	 *   
	 * @return The first Binding inserted in this substitution.
	 * @throws NoSuchElementException if this substitution is empty.
	 */
	public Binding first() throws NoSuchElementException {
		if (bindings.isEmpty()) {
			throw new NoSuchElementException();
		} else {
			return asList().get(0);
		}
	}
	
	
	/**
	 * Returns <code>true</code> if this substitution is consistent with the
	 * specified set of constraints.
	 * <p>
	 * A substitution is consistent with a set of constraints when applying
	 * the former to the latter generates only valid expressions.
	 * </p>
	 * 
	 * @param constraints The set of constraints to test this substitution
	 * against
	 * @return <code>true</code> if this substitution is consistent with the
	 * specified set of constraints, <code>false</code> otherwise.
	 */
	public boolean isConsistentWith(Set<Constraint> constraints) {
		int size = 0;
		for (Constraint constraint : constraints) {
			try {
				constraint.apply(this);
				size++;
			} catch (IllegalStateException e) {
				// takes into account valid Constant-only constraints 
				size++;
			} catch (IllegalArgumentException e) {
				// ignore invalid constraints
			} 
		}
		return (constraints.size() == size);
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

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
