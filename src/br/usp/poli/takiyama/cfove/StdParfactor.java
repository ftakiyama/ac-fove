package br.usp.poli.takiyama.cfove;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.Builder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdSplitResult;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Sets;

public final class StdParfactor implements Parfactor {

	private final Set<Constraint> constraints;
	private final Factor factor;

	
	/* ************************************************************************
	 *    Builder
	 * ************************************************************************/
	
	public static class StdParfactorBuilder implements Builder<StdParfactor> {

		private Set<Constraint> restrictions;
		private List<Prv> prvs;
		private List<BigDecimal> values;
		private Factor factor;
		
		public StdParfactorBuilder() {
			restrictions = new HashSet<Constraint>();
			prvs = new ArrayList<Prv>();
			values = new ArrayList<BigDecimal>();
			factor = null;
		}
		
		public StdParfactorBuilder constraints(Constraint ... c) {
			restrictions.addAll(Arrays.asList(c));
			return this;
		}
		
		public StdParfactorBuilder constraints(Set<Constraint> c) {
			restrictions.addAll(c);
			return this;
		}
			
		public StdParfactorBuilder variables(Prv ... prv) {
			prvs.addAll(Arrays.asList(prv));
			return this;
		}
		
		public StdParfactorBuilder variables(List<Prv> prv) {
			prvs.addAll(prv);
			return this;
		}
		
		public StdParfactorBuilder values(BigDecimal ... v) {
			values.addAll(Arrays.asList(v));
			return this;
		}

		public StdParfactorBuilder values(List<BigDecimal> v) {
			values.addAll(v);
			return this;
		}
		
		public StdParfactorBuilder values(double ... v) {
			for (double d : v) {
				values.add(BigDecimal.valueOf(d));
			}
			return this;
		}
		
		public StdParfactorBuilder factor(Factor f) {
			factor = f;
			return this;
		}
		
		@Override
		public StdParfactor build() {
			return new StdParfactor(this);
		}
		
	}
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	/**
	 * Constructor.
	 * @param constraints A set of {@link Constraint}s on logical variables
	 * @param factor A {@link Factor} from the Cartesian product of ranges of 
	 * parameterized random variables to the reals.
	 */
	private StdParfactor(Set<Constraint> constraints, Factor factor) {
		this.constraints = new HashSet<Constraint>(constraints);
		this.factor = Factor.getInstance(factor);
	}
	

	/**
	 * Creates an instance of StdParfactor based on a 
	 * {@link StdParfactorBuilder}.
	 * 
	 * @param builder A Standard Parfactor Builder
	 */
	private StdParfactor(StdParfactorBuilder builder) {
		Factor factor;
		if (builder.factor == null) {
			factor = Factor.getInstance("", builder.prvs, builder.values);
		} else {
			factor = builder.factor;
		}
		this.constraints = new HashSet<Constraint>(builder.restrictions);
		this.factor = Factor.getInstance(factor);
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * Returns a constant parfactor, the neutral factor for multiplications. 
	 * Any parfactor multiplied by the constant parfactor does not change.
	 * <p>
	 * The constant parfactor contains only one value, 1. There are no
	 * parameterized random variables or constraints in this parfactor. 
	 * </p>
	 *  
	 * @return The constant parfactor.
	 */
	public static Parfactor getInstance() {
		Set<Constraint> constraints = new HashSet<Constraint>(0);
		Factor factor = Factor.getInstance();
		return new StdParfactor(constraints, factor);
	}
	
	/**
	 * Returns a new instance StdParfactor that has the same constraints and
	 * the same factor of the specified Parfactor.
	 * 
	 * @param p The parfactor to "copy"
	 */
	public static Parfactor getInstance(Parfactor p) {
		return new StdParfactor(p.constraints(), p.factor());
	}
	

	/**
	 * Returns an instance of StdParfactor that has the specified constraints
	 * and the specified factor.
	 * 
	 * @param constraints A set of constraints
	 * @param factor A factor
	 * @return  An instance of StdPArfactor that has the specified constraints
	 * and the specified factor
	 */
	public static Parfactor getInstance(Set<Constraint> constraints, Factor factor) {
		return new StdParfactor(constraints, factor);
	}
	
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	@Override
	public Set<Constraint> constraints() {
		return new HashSet<Constraint>(constraints);
	}

	
	@Override
	public Factor factor() {
		return Factor.getInstance(factor);
	}

	
	@Override
	public Set<LogicalVariable> logicalVariables() {
		List<Prv> prvs = factor.variables();
		Set<LogicalVariable> logicalVariables = new HashSet<LogicalVariable>();
		for (Prv prv : prvs) {
			logicalVariables.addAll(prv.parameters());
		}
		return logicalVariables;
	}

	
	@Override
	public List<Prv> prvs() {
		return factor.variables();
	}
	

	@Override
	public int size() throws IllegalStateException {
		if (!isInNormalForm()) {
			throw new IllegalStateException("Parfactor not in normal form");
		}		
		int size = 1;
		Set<Constraint> toVisit = new HashSet<Constraint>(constraints);
		for (LogicalVariable lv : logicalVariables()) {
			size = size * lv.numberOfIndividualsSatisfying(toVisit);
			toVisit = remove(toVisit, lv);
		}
		return size;
	}
	
	
	/**
	 * Returns the specified set of constraints with all constraints that
	 * contain the specified term removed.
	 * 
	 * @param constraints A set of constraints
	 * @param t The term to search in constraints
	 * @return The specified set of constraints with all constraints that
	 * contain the specified term removed.
	 */
	private Set<Constraint> remove(Set<Constraint> constraints, Term t) {
		Set<Constraint> allConstraints = new HashSet<Constraint>(constraints);
		for (Constraint c : allConstraints) {
			if (c.contains(t)) {
				constraints.remove(c);
			}
		}
		return constraints;
	}
	
	
	@Override
	public Parfactor apply(Substitution s) {
		Set<Constraint> substitutedConstraints = applyToConstraints(s);
		Factor substitutedFactor = factor.apply(s);
		return StdParfactor.getInstance(substitutedConstraints, substitutedFactor);
	}
	
	
	/**
	 * Returns the result of applying the specified substitution to the set
	 * of constraints from this parfactor.
	 * @param s The substitution to apply to the constraints of this
	 * parfactor
	 * @return The result of applying the specified substitution to the set
	 * of constraints from this parfactor.
	 */
	private Set<Constraint> applyToConstraints(Substitution s) {
		Set<Constraint> substitutedConstraints = new HashSet<Constraint>(constraints.size());
		for (Constraint c : constraints) {
			Constraint substituted = c.apply(s);
			substitutedConstraints.add(substituted);
		}
		return substitutedConstraints;
	}
	
	
	@Override
	public boolean contains(Prv prv) {
		return factor.variables().contains(prv);
	}

	
	@Override
	public boolean isConstant() {
		boolean hasNoConstraints = constraints.isEmpty();
		boolean hasConstantFactor = factor.isConstant();
		return hasNoConstraints && hasConstantFactor;
	}

	
	@Override
	public boolean isCountable(LogicalVariable lv) {
		return (factor.occurrences(lv) == 1);
	}

	
	/**
	 * Returns <code>true</code> if the specified {@link Prv} can
	 * be expanded on the specified term.
	 * <p>
	 * More specifically, this method checks the following conditions:
	 * <li> This parfactor is in normal form
	 * <li> The specified PRV is a counting formula 
	 * #<sub>A:C<sub>A</sub></sub>[f(...A...)] from this parfactor
	 * <li> The specified term does not belong to the excluded set for A on 
	 * C<sub>A</sub>
	 * <li> The specified term belongs to the excluded set for Y on constraints
	 * of this parfactor, for each Y in the excluded set for A on C<sub>A</sub>.
	 * </p>
	 * @param cf The PRV to be expanded
	 * @param term The term to expand the counting formula on
	 * @return <code>true</code> if the specified PRV can
	 * be expanded on the specified term, <code>false</code> otherwise
	 */
	@Override
	public boolean isExpandable(Prv cf, Term t) {
		
		// Is counting formula?
		boolean isCountingFormula = !cf.boundVariable().isEmpty();
		
		// Is it present in this parfactor?
		boolean belongsHere = factor.variables().contains(cf);
		
		// Is this parfactor in normal form?
		boolean isInNormalForm = isInNormalForm();
		
		// Does t not appear on any constraint from the counting formula?
		boolean isCountable = !contains(cf.constraints(), t);
		
		// For each term Y that appears in constraints from the counting
		// formula, is there a constraint Y != t in this parfactor?
		boolean isOrthogonal = isOrthogonal(cf, t);
		
		return isCountingFormula && belongsHere && isInNormalForm 
				&& isCountable && isOrthogonal;
	}
	

	/**
	 * Returns <code>true</code> if this parfactor is in normal form.
	 * <p>
	 * A parfactor is in normal form if, for each inequality constraint 
	 * (X &ne; Y) &in; C we have &epsilon;<sub>X</sub><sup>C</sup>\{Y} = 
	 *  &epsilon;<sub>Y</sub><sup>C</sup>\{X}. X and Y are logical variables.
	 * </p>
	 * @return <code>true</code> if this parfactor is in normal form, 
	 * <code>false</code> otherwise
	 */
	private boolean isInNormalForm() {
		// This algorithm does not look very clever
		for (Constraint c : constraints) {
			if (c.firstTerm().isVariable() && c.secondTerm().isVariable()) {
				LogicalVariable x = (LogicalVariable) c.firstTerm();
				LogicalVariable y = (LogicalVariable) c.secondTerm();
				Set<Term> ex = x.excludedSet(constraints);
				Set<Term> ey = y.excludedSet(constraints);
				ex.remove(y);
				ey.remove(x);
				if (!ex.equals(ey)) {
					return false;
				}
			}
		}
		return true;
	}
	
		
	/**
	 * Returns <code>true</code> if the specified term is in at least one of
	 * the constraints from the specified set.
	 * 
	 * @param constraints A set of constraints
	 * @param t The term to search in constraints from the set
	 * @return <code>true</code> if the specified term is in at least one of
	 * the constraints from the specified set, <code>false</code> otherwise
	 */
	private boolean contains(Set<Constraint> constraints, Term t) {
		for (Constraint c : constraints) {
			if (c.contains(t)) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Returns <code>true</code> if the specified term is orthogonal to all 
	 * constraints in the specified counting formula.
	 * <p>
	 * A term t is orthogonal to a constraints in a counting formula when,
	 * for each logical variable Y &in; 
	 * &epsilon;<sub>A</sub><sup>C<sub>A</sub></sup>,
	 * we have t &in; &epsilon;<sub>Y</sub><sup>C<sub>i</sub></sup>.
	 * </p>
	 * <p>
	 * In other words, for each logical variable Y that appears in constraints 
	 * C<sub>A</sub>, checks if the constraint Y &ne; t appears in 
	 * C<sub>i</sub> (constraints from this parfactor).
	 * If there is a logical variable Y that does not satisfy it, returns 
	 * <code>false</code>.
	 * </p>
	 * @param cf A counting formula #<sub>A:C<sub>A</sub></sub>[f(...,A,...)]
	 * @param t The term to check
	 * @return <code>true</code> if the specified term is orthogonal to all 
	 * constraints in the specified counting formula, <code>false</code>
	 * otherwise
	 */
	private boolean isOrthogonal(Prv cf, Term t) {
		for (Term y : cf.boundVariable().excludedSet(cf.constraints())) {
			if (y.isVariable()) {
				Set<Term> ey = ((LogicalVariable) y).excludedSet(constraints);
				if (!ey.contains(t)) {
					return false;
				}
			}
		}
		return true;
	}

	
	/**
	 * Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public boolean isMultipliable(Parfactor other) {
		throw new UnsupportedOperationException("Not implemented!");
	}

	
	/**
	 * Returns <code>true</code> if this parfactor can be split on the
	 * specified substitution.
	 * <p>
	 * The following conditions are checked:
	 * <li> The substitution is of the form {X/t}, where X is a logical
	 * variable and t is a term;
	 * <li> X is a logical variable in this parfactor;
	 * <li> t is not in any constraints from this parfactor;
	 * <li> t is a constant such that t &in; D(x) <b>or</b> t is logical
	 * variable present in this parfactor such that D(t) = D(X).
	 * </p>
	 * 
	 * @param s The substitution to split this parfactor on
	 * @return <code>true</code> if this parfactor can be split on the
	 * specified substitution, <code>false</code> otherwise.
	 */
	@Override
	public boolean isSplittable(Substitution s) {
		boolean isSplittable;
		if (s.size() != 1) {
			isSplittable =  false;
		} else {
			Binding b = s.first();
			Term x = b.firstTerm();
			Term t = b.secondTerm();
			boolean isApplicable = logicalVariables().contains(x);
			boolean isNotInConstraints = !contains(constraints, t);
			boolean isValidSubstitution = b.isValid();
			boolean isLogicalVariable = (t.isVariable() ? logicalVariables().contains(t) : true);
			isSplittable = isApplicable && isNotInConstraints 
							&& isValidSubstitution && isLogicalVariable; 
		}
		return isSplittable;
	}
	
	
	@Override
	public Parfactor count(LogicalVariable lv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parfactor expand(Prv cf, Term t) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public Parfactor multiply(Parfactor other) {
		return other.finishMultiplication(this);
	}
	
	
	@Override
	public Parfactor finishMultiplication(Parfactor other) {
		/*
		 * I am sure that 'other' is a StdParfactor, because this method is
		 * not called by AggParfactors.
		 * I am pretty sure this will not cover all the cases when a new 
		 * type of parfactor is invented, but I cannot predict all 
		 * possible expansions (if there will be one) ;)
		 * 
		 * 'other' is the parfactor that called multiply(), thus to keep
		 * consistency:
		 * other = index i 
		 * this = index j
		 */
		
		// Creates intermediate parfactor g = <Ci U Cj, Vi U Vj, Fi x Fj> 
		Set<Constraint> union = Sets.union(other.constraints(), constraints);
		Factor fixfj = other.factor().multiply(factor);
		Parfactor g = new StdParfactorBuilder().constraints(union)
				.factor(fixfj).build();
		
		// Correction exponents
		int giSize = other.size(); 
		int gjSize = size();
		int gSize = g.size();
		
		// Creates factor Fi^ri x Fj^rj
		Factor fi = other.factor().pow(giSize, gSize);
		Factor fj = factor.pow(gjSize, gSize);
		Factor fixfjCorrected = fi.multiply(fj); // order is important here
		
		// Creates the product parfactor g' = <Ci U Cj, Vi U Vj, Fi^ri x Fj^rj>
		Parfactor product = new StdParfactorBuilder().constraints(union)
				.factor(fixfjCorrected).build();
		
		return product;
	}
	

	/**
	 * Splits this parfactor on the specified substitution. The result of
	 * splitting StdParfactor g on substitution {X/t} will
	 * have two parfactors:
	 * <li> g[X/t], the parfactor g after applying substitution {X/t};
	 * <li> g' = &lt; C U {X &ne; t}, V, F &gt;, the residual parfactor.
	 */
	@Override
	public SplitResult splitOn(Substitution s) throws IllegalArgumentException {
		if (!isSplittable(s)) {
			throw new IllegalArgumentException(this + " is not splittable.");
		}
		Parfactor result = apply(s);
		Binding b = s.first();
		Constraint c = InequalityConstraint.getInstance(b.firstTerm(), b.secondTerm());
		Parfactor residue = add(c);
		SplitResult split = StdSplitResult.getInstance(result, residue);
		return split;
	}
	
	
	/**
	 * Returns a Parfactor equal to this one with the specified constraint
	 * added.
	 * 
	 * @param c The constraint to add to this parfactor
	 * @return a Parfactor equal to this one with the specified constraint
	 * added
	 */
	public Parfactor add(Constraint c) {
		Set<Constraint> constraints = new HashSet<Constraint>(this.constraints);
		constraints.add(c);
		return StdParfactor.getInstance(constraints, this.factor);
	}

	
	@Override
	public Parfactor sumOut(Prv prv) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/* ************************************************************************
	 *    toString, equals and hashCode
	 * ************************************************************************/
	
	@Override
	public String toString() {
		String result = "C = " + constraints + ",\n" + factor;
		return result;
	
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof StdParfactor))
	    	return false;
	    // Tests if both have the same attributes
	    StdParfactor targetObject = (StdParfactor) other;
	    return ((constraints == null) ? 
	    		 targetObject.constraints == null : 
		    		 constraints.equals(targetObject.constraints)) &&
    		   ((factor == null) ? 
    		     targetObject.factor == null : 
    		     factor.equals(targetObject.factor));	    		
	}
	
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 + result + constraints.hashCode();
		result = 31 + result + factor.hashCode();
		return result;
	}
	
}
