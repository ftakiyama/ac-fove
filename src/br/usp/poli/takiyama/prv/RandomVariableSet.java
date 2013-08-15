package br.usp.poli.takiyama.prv;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.utils.Sets;

/**
 * Represents the set of random variables represented by a parameterized
 * random variable and a set of constraints.
 * <p>
 * For instance, given the parameterized random variable f(A) with 
 * D(A) = {a1, a2, a3} and the set of constraints C = {A &ne; a1}, the
 * random variable set f(A):C equals to {f(a2), f(a3)}.
 * </p>
 * 
 * @author Felipe Takiyama
 */
public final class RandomVariableSet implements Prv {
	
	private final Prv prv;
	private final Set<Constraint> constraints;
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	/*
	 * What about building in a standard way?
	 * For instance, f(x1, B):{B!=x2} is represented as f(A,B):{A!=x2,...,A!=xn,B!=x1}.
	 * This simplifies comparison between sets.
	 */
	
	/**
	 * Creates a random variable set.
	 * <p>
	 * Constraints that do not involve parameters from the specified 
	 * parameterized random variable are ignored.
	 * </p>
	 * <p>
	 * Constraints that involve two logical variables are also discarded.
	 * </p>
	 * 
	 * @param prv A {@link Prv} 
	 * @param constraints A set of {@link Constraint}
	 */
	private RandomVariableSet(Prv prv, Set<Constraint> constraints) {
		this.prv = prv;
		this.constraints = Sets.getInstance(constraints.size() + prv.constraints().size());
		for (Constraint c : constraints) {
			if (c.isUnary() && prv.contains(c.logicalVariables().iterator().next())) {
				this.constraints.add(c);
			}
		}
		// Enters this block only when PRV is a counting formula.
		// I am removing binary constraints: is it a problem? Not sure...
		for (Constraint c : prv.constraints()) {
			if (c.isUnary()) {
				this.constraints.add(c);
			}
		}
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * Static factory of {@link RandomVariableSet}. Returns an instance of
	 * RandomVariableSet.
	 * <p>
	 * Constraints that do not involve parameters from the specified 
	 * parameterized random variable are not stored in the instance.
	 * </p>
	 * <p>
	 * Constraints that involve two logical variables are also discarded.
	 * </p>
	 * <p>
	 * If the parameterized random variable has no parameters, it returns 
	 * a RandomVariableSet with the specified PRV and an empty set of
	 * constraints.
	 * </p>
	 * 
	 * @param prv A {@link Prv} 
	 * @param constraints A set of {@link Constraint}
	 * @return An instance of RandomVariableSet with the specified parameters
	 */
	public static RandomVariableSet getInstance(Prv prv, Set<Constraint> constraints) {
		if (prv.parameters().size() == 0) {
			return new RandomVariableSet(prv, new HashSet<Constraint>());
		}
		int maxNumConstraints = 0;
		for (LogicalVariable lv : prv.parameters()) {
			maxNumConstraints = maxNumConstraints + lv.population().size();
		}
		if (maxNumConstraints == constraints.size()) {
			return RandomVariableSet.getInstance();
		} else {
			return new RandomVariableSet(prv, constraints);
		}
	}
	
	
	/**
	 * Static factory of {@link RandomVariableSet}. Returns an instance of
	 * RandomVariableSet that is identical to the specified RandomVariableSet.
	 * 
	 * @param rvs The RandomVariableSet to copy.
	 * @return An instance of RandomVariableSet that is identical to the 
	 * specified RandomVariableSet.
	 */
	public static RandomVariableSet getInstance(RandomVariableSet rvs) {
		return new RandomVariableSet(rvs.prv(), rvs.constraints());
	}
	
	
	/**
	 * Builds an empty instance of {@link RandomVariableSet}.
	 * <p>
	 * An empty instance contains an empty PRV (no parameters, nameless functor)
	 * and an empty set of constraints.
	 * </p>
	 * 
	 * @return An empty instance of RandomVariableSet.
	 */
	public static RandomVariableSet getInstance() {
		return new RandomVariableSet(StdPrv.getInstance(), new HashSet<Constraint>(0));
	}
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/
	
	/**
	 * Returns the set of constraints in this set.
	 * 
	 * @return The set of constraints in this set.
	 */
	public Set<Constraint> constraints() {
		return new HashSet<Constraint>(constraints);
	}
	
	
	/**
	 * Returns the prv bound to this set.
	 * 
	 * @return The prv bound to this set.
	 */
	public Prv prv() {
		return prv;
	}
	
	
	/**
	 * TODO reimplement
	 * Returns the complement set of this set.
	 * <br>
	 * <b>Attention!</b> This method is inefficient in that it depends on the
	 * size of the population of each parameter from the PRV.
	 * 
	 * @return The complement set of this set.
	 */
	public RandomVariableSet complement() {
		HashSet<Constraint> constraints = new HashSet<Constraint>();
		for (LogicalVariable lv : prv.parameters()) {
			for (Constant c : lv.individualsSatisfying(constraints)) {
				constraints.add(InequalityConstraint.getInstance(lv, c));
			}
		}
		return new RandomVariableSet(this.prv, constraints);
	}
	
	
	/**
	 * Returns the intersection of this set and the specified RandomVariableSet.
	 * 
	 * @param rvSet A {@link RandomVariableSet}
	 * @return The intersection of this set and the specified RandomVariableSet.
	 */
	public RandomVariableSet intersect(RandomVariableSet rvSet) {
		if (!this.prv.equals(rvSet.prv)) {
			return RandomVariableSet.getInstance();
		} else {
			HashSet<Constraint> constraints = 
					new HashSet<Constraint>(this.constraints);
			constraints.addAll(rvSet.constraints);
			return new RandomVariableSet(this.prv, constraints);
		}
	}
	
	
	/**
	 * Returns the difference between this set and the specified set.
	 * 
	 * @param rvSet A {@link RandomVariableSet}
	 * @return The difference between this set and the specified set.
	 */
	public RandomVariableSet minus(RandomVariableSet rvSet) {
		if (!this.prv.equals(rvSet.prv)) {
			return this;
		} else {
			if (this.constraints.equals(rvSet.constraints)) {
				return RandomVariableSet.getInstance();
			} else {
				RandomVariableSet r = rvSet.complement();
				HashSet<Constraint> constraints = 
						new HashSet<Constraint>(r.constraints);
				constraints.addAll(this.constraints);
				return new RandomVariableSet(r.prv, constraints);
			}
		}
	}
	
	
	/**
	 * Returns the union of this set and the specified set.
	 * 
	 * @param rvSet A {@link RandomVariableSet}
	 * @return The union of this set and the specified set.
	 */
	public Set<RandomVariableSet> union(RandomVariableSet rvSet) {
		HashSet<RandomVariableSet> r = new HashSet<RandomVariableSet>(2);
		if (!this.prv.equals(rvSet.prv)) {
			r.add(this);
			r.add(rvSet);
		} else {
			HashSet<Constraint> constraints = new HashSet<Constraint>();
			if (this.constraints.equals(rvSet.constraints)) {
				constraints.addAll(this.constraints);
			} else { 
				constraints.addAll(this.constraints);
				constraints.retainAll(rvSet.constraints);
			}
			r.add(new RandomVariableSet(this.prv, constraints));
		}
		return r;
	}
	
	/**
	 * Returns <code>true</code> if this set is empty, <code>false</code> 
	 * otherwise.
	 * <p>
	 * An empty set can be created by adding all possible constraints
	 * involving all parameters of the associated parameterized random variable.
	 * </p>
	 * @return <code>true</code> if this set is empty, <code>false</code> 
	 * otherwise.
	 */
	public boolean isEmpty() {
		return prv.parameters().isEmpty() && constraints.isEmpty();
	}
	
	/**
	 * Returns true if this set contains the specified parameterized random
	 * variable.
	 * @param prv
	 * @return
	 */
//	public boolean contains(ParameterizedRandomVariable prv) {
//		// TODO all unification process, again?
//		Substitution mgu = null;
//		try {
//			mgu = this.prv.getMgu(prv);
//		} catch (IllegalArgumentException e) {
//			// firstVariable and secondVariable represent disjoint sets
//			return false;
//		}
//		return false;
//	}
	
	
	// Calling from PRV instead
//	/**
//	 * Returns true if this set represents the same set of random variables
//	 * from the specified counting formula.
//	 * @param cf The counting formula to compare
//	 * @return True if this set represents the same set of random variables
//	 * from the specified counting formula.
//	 */
//	public boolean isEquivalent(Prv prv) {
//		return (this.prv.name().equals(prv.name()) 
//				&& this.constraints.equals(prv.constraints()));
//	}


	@Override
	public boolean isEquivalentTo(RandomVariableSet s) {
		return this.equals(s);
	}
	
	@Override
	public String name() {
		return prv.name();
	}

	@Override
	public List<LogicalVariable> parameters() {
		return prv.parameters();
	}

	@Override
	public List<Term> terms() {
		return prv.terms();
	}

	@Override
	public List<RangeElement> range() {
		return prv.range();
	}

	@Override
	public LogicalVariable boundVariable() {
		return prv.boundVariable();
	}

	@Override
	public int groundSetSize(Set<Constraint> constraints) {
		return prv.groundSetSize(Sets.union(this.constraints, constraints));
	}

	@Override
	public boolean contains(Term t) {
		return prv.contains(t);
	}

	@Override
	public Prv apply(Substitution s) {
		return RandomVariableSet.getInstance(prv.apply(s), Sets.apply(s, constraints));
	}

	/**
	 * Throws a {@link UnsupportedOperationException}.
	 */
	@Override
	public Prv rename(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws a {@link UnsupportedOperationException}.
	 */
	@Override
	public BigDecimal getSumOutCorrection(RangeElement e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Prv getCanonicalForm() {
//		Prv canonicalPrv = prv.getCanonicalForm();
//		Set<Constraint> allConstraints = Sets.union(constraints, prv.constraints());
//		return RandomVariableSet.getInstance(canonicalPrv, allConstraints);
		
		return this.prv.getCanonicalForm();
	}
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/
	
	@Override
	public boolean equals(Object other) {
		if (this == other) 
			return true;
		if (!(other instanceof RandomVariableSet))
			return false;
		RandomVariableSet o = (RandomVariableSet) other;
		return ((this.prv == null) 
						? (o.prv == null) 
						: (this.prv.equals(o.prv)))
				&& ((this.constraints == null) 
						? (o.constraints == null) 
						: (this.constraints.equals(o.constraints)));
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(this.prv.toString());
		result.append(":").append(this.constraints);
		return result.toString();
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 + result + constraints.hashCode();
		result = 31 + result + prv.hashCode();
		return result;
	}
}
