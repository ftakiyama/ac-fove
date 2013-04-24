package br.usp.poli.takiyama.prv;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;

/**
 * A parameterized random variable is either a logical atom or a term. 
 * That is, it is of the form p(t1,...,tn), where each ti is a logical variable 
 * or a constant and p is a functor. Each functor has a set of values called 
 * the range of the functor [Kisynski, 2010]. The parameterized random variable 
 * is said to be parameterized by the logical variables that appear in it.
 * [Poole, 2010]
 * 
 * @author Felipe Takiyama
 *
 */
public class StdPrv implements Prv {

	/*
	 * Parameters are put in a list because they need to be ordered in a 
	 * predictable way.
	 */
	private final List<Term> parameters;
	private final String functor;
	private final List<RangeElement> range;
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	private StdPrv(String f, int pSize, int rSize) {
		parameters = new ArrayList<Term>(pSize);
		range = new ArrayList<RangeElement>(rSize);
		functor = f;
	}
	
	
	/**
	 * Creates an empty Standard Parameterized Random Variable.
	 */
	private StdPrv() {
		this("", 0, 0);
	}
	
	
	/**
	 * Creates a Standard Parameterized Random Variable with one {@link Term}.
	 * 
	 * @param f The name of this PRV
	 * @param r The range of this PRV
	 * @param t The parameter of this PRV
	 */
	private StdPrv(String f, List<RangeElement> r, Term t) {
		this(f, 1, r.size());
		for (RangeElement e : r) {
			range.add(e);
		}
		parameters.add(t);
	}
	
	
	/**
	 * Creates a Standard Parameterized Random Variable with two {@link Term}s.
	 * 
	 * @param f The name of this PRV
	 * @param r The range of this PRV
	 * @param t1 The first parameter of this PRV
	 * @param t2 The second parameter of this PRV
	 */
	private StdPrv(String f, List<RangeElement> r, Term t1, Term t2) {
		this(f, 2, r.size());
		for (RangeElement e : r) {
			range.add(e);
		}
		parameters.add(t1);
		parameters.add(t2);
	}
	
	
	/**
	 * Creates a Standard Parameterized Random Variable.
	 * 
	 * @param f The name of this PRV
	 * @param r The range of this PRV
	 * @param t The parameters of this PRV
	 */
	private StdPrv(String f, List<RangeElement> r, List<Term> t) {
		this(f, t.size(), r.size());
		for (RangeElement e : r) {
			range.add(e);
		}
		for (Term p : t) {
			parameters.add(p);
		}
	}
	
	
	/**
	 * Creates a copy of the specified Prv.
	 * 
	 * @param prv The StdPrv to copy.
	 */
	private StdPrv(Prv prv) throws IllegalArgumentException {
		if (!(prv instanceof StdPrv)) {
			throw new IllegalArgumentException();
		}
		StdPrv std = (StdPrv) prv;
		parameters = new ArrayList<Term>(std.parameters);
		functor = std.functor;
		range = new ArrayList<RangeElement>(std.range);
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * Returns an instance of standard parameterized random variable (StdPrv).
	 * 
	 * @param f The name of the PRV
	 * @param r The range of the PRV
	 * @param t The parameters of the PRV
	 * @return An instance of StdPrv.
	 */
	public static Prv getInstance(String f, List<RangeElement> r, List<Term> t) {
		return new StdPrv(f, r, t);
	}
	
	
	/**
	 * Returns a copy of the given StdPrv.
	 * 
	 * @param prv The StdPrv to copy.
	 * @return A copy of the specified StdPrv.
	 */
	public static Prv getInstance(Prv prv) {
		return new StdPrv(prv);
	}

	
	/**
	 * Returns an empty standard parameterized random variable. 
	 * It has a nameless functor, an empty set of parameters and no range.
	 * 
	 * @return An empty StdPrv
	 */
	public static Prv getInstance() {
		return new StdPrv();
	}
	
	
	/**
	 * Returns a Boolean StdPrv. Its range is {false, true}
	 * 
	 * @param f The name of the functor
	 * @param vars Terms that parameterized the functor
	 * @return a Boolean StdPrv with range {false, true}
	 */
	public static Prv getBooleanInstance(String f, Term ... vars) {
		List<RangeElement> range = new ArrayList<RangeElement>(2);
		range.add(Bool.valueOf(false));
		range.add(Bool.valueOf(true));
		List<Term> terms = Arrays.asList(vars);
		return new StdPrv(f, range, terms);
	}
	
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	/**
	 * Returns an empty set of {@link Constraint}.
	 */
	@Override
	public Set<Constraint> constraints() {
		return new HashSet<Constraint>(0);
	}

	
	@Override
	public String name() {
		return functor;
	}

	
	@Override
	public List<LogicalVariable> parameters() {
		List<LogicalVariable> param = new ArrayList<LogicalVariable>(parameters.size());
		for (Term t : parameters) {
			if (t.isVariable()) {
				param.add((LogicalVariable) t);
			}
		}
		return param;
	}
	
	
	@Override
	public List<Term> terms() {
		return new ArrayList<Term>(parameters);
	}
	

	@Override
	public List<RangeElement> range() {
		return new ArrayList<RangeElement>(range);
	}

	
	@Override
	public int groundSetSize(Set<Constraint> constraints) {
		int size = 1;
		for (LogicalVariable v : parameters()) {
			size = size * v.individualsSatisfying(constraints).size();
		}
		return size;
	}

	
	@Override
	public boolean contains(Term t) {
		return parameters.contains(t);
	}

	
	/**
	 * Returns {@link BigDecimal#ONE}.
	 */
	@Override
	public BigDecimal getSumOutCorrection(RangeElement e) {
		return BigDecimal.ONE;
	}
	
	
	/* ************************************************************************
	 *    Setters
	 * ************************************************************************/

	@Override
	public Prv apply(Substitution s) {
		StdPrv substituted = new StdPrv(this);
		for (Iterator<LogicalVariable> it = s.getSubstitutedIterator(); it.hasNext(); ) {
			LogicalVariable toReplace = it.next();
			Term replacement = s.getReplacement(toReplace);
			substituted.replace(toReplace, replacement);
		}
		return substituted;
	}
	
	
	/**
	 * Replaces all occurrences of the term <code>toReplace</code> by the
	 * <code>replacement</code> 
	 * in the list of parameters. If the term <code>toReplace</code> does not
	 * exist, nothing is done.
	 * 
	 * @param toReplace The term to replace
	 * @param replacement The term that will replace the term 
	 * <code>toReplace</code>.
	 */
	private void replace(Term toReplace, Term replacement) {
		while (contains(toReplace)) {
			int substitutedIndex = parameters.indexOf(toReplace);
			parameters.set(substitutedIndex, replacement);
		}
	}


	@Override
	public Prv rename(String name) {
		return StdPrv.getInstance(name, range, parameters);
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(functor + " ( ");
		for (Term term : parameters) {
			result.append(term).append(" ");
		}
		result.append(")");
		return result.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((functor == null) ? 0 : functor.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((range == null) ? 0 : range.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StdPrv)) {
			return false;
		}
		StdPrv other = (StdPrv) obj;
		if (functor == null) {
			if (other.functor != null) {
				return false;
			}
		} else if (!functor.equals(other.functor)) {
			return false;
		}
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		if (range == null) {
			if (other.range != null) {
				return false;
			}
		} else if (!range.equals(other.range)) {
			return false;
		}
		return true;
	}
	
	
}
