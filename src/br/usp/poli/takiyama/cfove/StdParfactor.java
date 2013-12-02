package br.usp.poli.takiyama.cfove;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.AggregationParfactor;
import br.usp.poli.takiyama.common.Builder;
import br.usp.poli.takiyama.common.ConstantFactor;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.MultiplicationChecker;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.ParfactorVisitor;
import br.usp.poli.takiyama.common.Scanner;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdFactor;
import br.usp.poli.takiyama.common.Tuple;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Population;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RangeElement;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.MathUtils;
import br.usp.poli.takiyama.utils.Sets;

public final class StdParfactor implements Parfactor {

	private final Set<Constraint> constraints;
	private final Factor factor;

	
	/* ************************************************************************
	 *    Builders
	 * ************************************************************************/
	
	public static class StdParfactorBuilder implements Builder<StdParfactor> {

		private Set<Constraint> restrictions;
		private List<Prv> prvs;
		private List<BigDecimal> values;
		
		public StdParfactorBuilder() {
			restrictions = new HashSet<Constraint>();
			prvs = new ArrayList<Prv>();
			values = new ArrayList<BigDecimal>();
		}
		
		public StdParfactorBuilder(Parfactor p) {
			this();
			constraints(p.constraints());
			variables(p.prvs());
			values(p.factor().values());
			factor(p.factor());
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
				
		/**
		 * Sets the factor for this builder. Values and PRVs from the 
		 * specified factor are added to this builder.
		 * 
		 * @param f The factor 
		 * @return This builder with the factor updated.
		 */
		public StdParfactorBuilder factor(Factor f) {
			variables(f.variables());
			values(f.values());
			return this;
		}
		
		/**
		 * Returns the factor defined by PRVs and values in this builder.
		 * <p>
		 * If no values were set, returns a constant factor.
		 * </p>
		 * <p>
		 * If no variables were set, returns an empty factor.
		 * </p>
		 * @return The factor defined by PRVs and values in this builder.
		 * @throws IllegalStateException 
		 */
		private Factor getFactor() throws IllegalStateException {
			Factor factor;
			List<Prv> variables = new ArrayList<Prv>(this.prvs);
			if (this.values.isEmpty()) {
				factor = ConstantFactor.getInstance(variables);
			} else {
				try {
					factor = StdFactor.getInstance("", variables, values);
				} catch (IllegalArgumentException e) {
					throw new IllegalStateException(variables + "\n" + values);
				}
			}
			return factor;
		}
		
		@Override
		public StdParfactor build() {
//			StdParfactor simplified = new StdParfactor(this);
//			Simplifier simplifier = simplified.new Simplifier(simplified);
//		
//			return ((StdParfactor) simplifier.simplify());
			
			// this is causing some problems
//			StdParfactor current = new StdParfactor(this);
//			return ((StdParfactor) current.simplifyLogicalVariables());
			
			return new StdParfactor(this);
		}
		
		private StdParfactor buildRaw() {
			return new StdParfactor(this);
		}
	}
	
	/**
	 * Encapsulates splitting algorithm.
	 * <p>
	 * Splitting a parfactor "breaks" a parfactor in two.
	 * </p>
	 */
	private final class Splitter {
		
		private Parfactor splittable;
		
		private Substitution substitution;
		
		private Splitter(Parfactor splittable, Substitution substitution) {
			this.splittable = splittable;
			this.substitution = substitution;
		}
		
		/**
		 * Splits this parfactor on the specified substitution. The result of
		 * splitting StdParfactor g on substitution {X/t} will
		 * have two parfactors:
		 * <li> g[X/t], the parfactor g after applying substitution {X/t};
		 * <li> g' = &langle; C U {X &ne; t}, V, F &rangle;, the residual 
		 * parfactor.
		 */
		private SplitResult split() {
			if (!isSplittable(substitution)) {
				throw new IllegalArgumentException(splittable 
						+ " is not splittable on " + substitution);
			}
			Parfactor result = splittable.apply(substitution);
			Parfactor residue = getResidue();
			SplitResult split = SplitResult.getInstance(result, residue);
			return split;
		}
		
		/**
		 * Returns a Parfactor equal to this one with the specified constraint
		 * added.
		 */
		private Parfactor getResidue() {
			Binding b = substitution.first();
			Constraint c = b.toInequalityConstraint();
			Set<Constraint> constraints = splittable.constraints();
			constraints.add(c);
			return new StdParfactorBuilder().constraints(constraints)
					.variables(splittable.prvs())
					.values(splittable.factor().values())
					.build();
		}
	}
	
	/**
	 * Encapsulates counting algorithm.
	 * <p>
	 * Counting a free logical variable eliminates it from the parfactor using
	 * a counting formula.
	 * </p>
	 */
	private class Counter {
		// TODO use aggregation instead of inheritance
		
		// The parfactor being modified
		private Parfactor parfactor;
		
		// PRV that contains the logical variable being counted
		private Prv counted;
		
		// The logical variable being counted
		private LogicalVariable bound;
		
		// Subset of constraints that contains the bound logical variable
		private Set<Constraint> constraintsOnBound;
		
		// A counting formula that replaces PRV 'counted'
		private Prv countingFormula;
		
		// Index of 'counted' in the list of PRVs
		private int countedIndex;
		
		
		// Values from the result
		private List<BigDecimal> values;
		
		// Variables in the result
		private List<Prv> variables;
		
		// Constraints in the result
		private Set<Constraint> constraints;
		
		/*
		 * I use super.factor to navigate through the old factor, and
		 * super.prvs/super.values to set the values for the new factor
		 */
		
		/**
		 * Creates a Counter using the specified parfactor. The 'counted'
		 * parfactor will be built based on the specified parfactor.
		 * 
		 * @param p The parfactor on which counting will take place.
		 */
		private Counter(Parfactor p) {
			parfactor = p;
			counted = StdPrv.getInstance();
			bound = StdLogicalVariable.getInstance();
			constraintsOnBound = new HashSet<Constraint>(parfactor.constraints().size());
			countingFormula = StdPrv.getInstance();
			countedIndex = 0;
			values = parfactor.factor().values();
			variables = parfactor.prvs();
			constraints = parfactor.constraints();
		}
		
		/**
		 * Counts the specified free logical variable in this parfactor.
		 * 
		 * @param lv The logical variable to count
		 * @return A {@link StdParfactorBuilder} with the result of counting.
		 */
		private Parfactor count(LogicalVariable lv) {
			setBound(lv);
			partitionOnBound();
			setPrvOnOnBound();
			setCountingFormulaOnBound();
			replaceCountedWithCountingFormula();
			setValues();
			return new StdParfactorBuilder().constraints(constraints).variables(variables).values(values).build();
		}
		
		/**
		 * Sets the free logical variable to be bound during count.
		 * @param lv The logical variable to bound
		 */
		private void setBound(LogicalVariable lv) {
			bound = StdLogicalVariable.getInstance(lv);
		}
		
		/**
		 * Splits constraints from this parfactor in two subsets, one that
		 * involves the bound logical variable and another that does not.
		 */
		private void partitionOnBound() {
			for (Constraint c : parfactor.constraints()) {
				if (c.contains(bound)) {
					constraintsOnBound.add(c);
				}
			}
			constraints.removeAll(constraintsOnBound);
		}
		
		/**
		 * Searches the PRV in parfactor being processed that contains the 
		 * bound logical variable as a parameter. This method assumes that
		 * there is only one PRV satisfying this condition.
		 * 
		 * @see StdParfactor#isCountable(LogicalVariable)
		 */
		private void setPrvOnOnBound() {
			counted = parfactor.factor().getVariableHaving(bound);
			countedIndex = variables.indexOf(counted);
		}
		
		/**
		 * Builds the counting formula that will replace PRV on bound logical
		 * variable in the new parfactor
		 */
		private void setCountingFormulaOnBound() {
			countingFormula = CountingFormula.getInstance(bound, counted, constraintsOnBound);
		}
		
		/**
		 * Replaces the old PRV being counted with its corresponding counting
		 * formula.
		 */
		private void replaceCountedWithCountingFormula() {
			variables.set(countedIndex, countingFormula);
		}
		
		/**
		 * Builds the array that defines the values in the new parfactor.
		 */
		private void setValues() {
			
			/**
			 * for each tuple t in F'
			 *     value = 1
			 *     for each range element e in PRV's range
			 *         t' = t with counting formula replaced by e
			 *         value = value * F(t)^h(e)
			 *     add value to v[]
			 */
			
			values = new ArrayList<BigDecimal>();
			Factor newStructure = ConstantFactor.getInstance(variables);
			
			for (Tuple<RangeElement> tuple : newStructure) {
				BigDecimal value = BigDecimal.ONE;
				for (RangeElement e : counted.range()) {
					Tuple<RangeElement> old = tuple.set(countedIndex, e);
					CountingFormula cf = (CountingFormula) countingFormula;
					int count = cf.getCount(tuple.get(countedIndex), e);
					value = value.multiply(parfactor.factor().getValue(old).pow(count, MathUtils.CONTEXT), MathUtils.CONTEXT);
				}
				values.add(value);
			}
			// Erase factor so it does not overwrites constructor
			//super.factor = null;
		}
	}
	
	
	/**
	 * Encapsulates simplification algorithm
	 * <p>
	 * Simplifying a parfactor is the process of replacing all logical variables
	 * constrained to a single individual with this individual.
	 * </p>
	 */
	private class Simplifier {
		
		private final Parfactor parfactor;
		private Set<Constraint> unaryConstraints;
		private Set<Constraint> constraints;
		private List<Prv> variables;
		
		private Simplifier(StdParfactor parfactor) {
			this.parfactor = parfactor;
			this.constraints = new HashSet<Constraint>(parfactor.constraints());
			this.variables = new ArrayList<Prv>(parfactor.prvs());
			this.unaryConstraints = getUnaryConstraints();
		}
		
//		/*
//		 * This is a quick, temporary, dirt fix ;P
//		 * I need to simplify all parfactors just after creating them, so I
//		 * will call this simplifier in builder's build() method.
//		 */
//		private Simplifier(Set<Constraint> constraints, List<Prv> variables, List<BigDecimal> values) {
//			this.parfactor = new StdParfactorBuilder().constraints(constraints)
//					.variables(variables).values(values).build();
//			this.constraints = this.parfactor.constraints();
//			this.variables = this.parfactor.prvs();
//			this.unaryConstraints = getUnaryConstraints();
//		}
		
		/**
		 * Returns the subset of unary constraints from the set of constraints
		 */
		private Set<Constraint> getUnaryConstraints() {
			Set<Constraint> unary = new HashSet<Constraint>();
			for (Constraint constraint : constraints) {
				if (constraint.isUnary()) {
					unary.add(constraint);
				}
			}
			return unary;
		}
		
		/**
		 * Replaces all logical variable constrained to a single individual
		 * with this individual
		 */
		private Parfactor simplify() {
			
			simplifyVariablesWithPopulationOne();
			
			LinkedList<LogicalVariable> queue  = getVariablesInConstraints();
			while (!queue.isEmpty()) {
				LogicalVariable logicalVariable = queue.poll();
				int populationSize = logicalVariable
						.numberOfIndividualsSatisfying(unaryConstraints);
				switch (populationSize) {
				case 0:
					return StdParfactor.getInstance();
				case 1:
					Substitution sub = getSubstitution(logicalVariable);
					queue.addAll(variablesInBinaryConstraintsInvolving(logicalVariable));
					constraints = Sets.apply(sub, constraints);
					unaryConstraints = getUnaryConstraints(); 
					variables = Lists.apply(sub, variables);
					break;
				default:
					break;
				}
			}
			Parfactor result = new StdParfactorBuilder().constraints(constraints)
					.variables(variables).values(parfactor.factor().values()).buildRaw();
			return result;
		}
		
		/**
		 * Add logical variables from parfactor's constraints to a queue
		 * >> ugly implementation =(
		 */
		private LinkedList<LogicalVariable> getVariablesInConstraints() {
			Set<LogicalVariable> buffer = new HashSet<LogicalVariable>();
			for (Constraint c : parfactor.constraints()) {
				buffer.addAll(c.logicalVariables());
			}
			return new LinkedList<LogicalVariable>(buffer);
		}
		
		/**
		 * When a logical variable is constrained to a single individual, 
		 * builds the substitution that replaces the logical variable by this
		 * individual.
		 */
		private Substitution getSubstitution(LogicalVariable lv) {
			Term loneGuy = lv.individualsSatisfying(unaryConstraints).iterator().next();
			Binding bind = Binding.getInstance(lv, loneGuy);
			return Substitution.getInstance(bind);
		}
		
		/**
		 * Returns the set of logical variables belonging to binary constraints
		 * that involve the specified logical variable.
		 */
		private Set<LogicalVariable> variablesInBinaryConstraintsInvolving(LogicalVariable lv) {
			Set<Constraint> binaryConstraints = new HashSet<Constraint>(constraints);
			binaryConstraints.removeAll(unaryConstraints);
			
			Set<LogicalVariable> otherVariables = new HashSet<LogicalVariable>();
			for (Constraint binary : binaryConstraints) {
				if (binary.contains(lv) && binary.firstTerm().equals(lv)) {
					otherVariables.add((LogicalVariable) binary.secondTerm());
				} else if (binary.contains(lv) && binary.secondTerm().equals(lv)) {
					otherVariables.add((LogicalVariable) binary.firstTerm());
				}
			}
			return otherVariables;
		}
		
		private void simplifyVariablesWithPopulationOne() {
			Parfactor p = new Scanner(parfactor);
			List<Binding> bindList = new ArrayList<Binding>(p.logicalVariables().size());
			for (LogicalVariable v : p.logicalVariables()) {
				if (v.population().size() == 1) {
					Constant c = v.population().individualAt(0);
					Binding bind = Binding.getInstance(v, c);
					bindList.add(bind);
				}
			}
			Substitution substitution = Substitution.getInstance(bindList);
			variables = Lists.apply(substitution, variables);
		}
		
	}
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	/**
	 * @deprecated
	 * Constructor.
	 * @param constraints A set of {@link Constraint}s on logical variables
	 * @param factor A {@link Factor} from the Cartesian product of ranges of 
	 * parameterized random variables to the reals.
	 */
	private StdParfactor(Set<Constraint> constraints, Factor factor) {
		this.constraints = new HashSet<Constraint>(constraints);
		this.factor = StdFactor.getInstance(factor);
	}
	

	/**
	 * Creates an instance of StdParfactor based on a 
	 * {@link StdParfactorBuilder}.
	 * 
	 * @param builder A Standard Parfactor Builder
	 */
	private StdParfactor(StdParfactorBuilder builder) {
		this.factor = builder.getFactor();
		this.constraints = new HashSet<Constraint>(builder.restrictions);
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * @deprecated
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
		Factor factor = StdFactor.getInstance();
		return new StdParfactor(constraints, factor);
	}
	
	/**
	 * @deprecated
	 * Returns a new instance StdParfactor that has the same constraints and
	 * the same factor of the specified Parfactor.
	 * 
	 * @param p The parfactor to "copy"
	 */
	public static Parfactor getInstance(Parfactor p) {
		return new StdParfactor(p.constraints(), p.factor());
	}
	

	/**
	 * @deprecated
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
		return factor;
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
		Set<Constraint> substitutedConstraints = Sets.apply(s, constraints);
		Factor substitutedFactor = factor.apply(s);
		return StdParfactor.getInstance(substitutedConstraints, substitutedFactor);
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
	public boolean isExpandable(Prv cf, Substitution s) {
		
		if (s.size() != 1) {
			return false;
		}
		
		Binding bind = s.asList().get(0);
		LogicalVariable replaced = bind.firstTerm();
		Term t = bind.secondTerm();
		
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
		
		/*
		 * In theory it is possible to expand a counting formula on a logical
		 * variable. For now, I will not allow it to make shattering simpler.
		 * Need to find an example where expanding on a logical variable is
		 * the only and correct thing to do.
		 */
		// boolean isLocalLogicalVariable = logicalVariables().contains(t);
		
		boolean replacesBound = replaced.equals(cf.boundVariable());
		
		boolean isConstantFromBound = (t.isConstant() ? 
				cf.boundVariable().population().contains((Constant) t) : false);
		
		return isCountingFormula && belongsHere && isInNormalForm 
				&& isCountable && isOrthogonal
				&& replacesBound && isConstantFromBound;
				//&& (isLocalLogicalVariable || isConstantFromBound); // disabled for the reason above
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

	
	@Override
	public boolean isMultipliable(Parfactor other) {
		/*
		 * Uses a ParfactorVisitor to discover the type of 'other'.
		 * The algorithm to check if parfactors are multipliable is
		 * encapsulated in MultiplicationChecker.
		 */
		MultiplicationChecker parfactors = new MultiplicationChecker();
		accept(parfactors, other);
		return parfactors.areMultipliable();
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
			// Split method only works for 1 binding
			isSplittable =  false;
		} else {
			Binding b = s.first();
			Term x = b.firstTerm();
			Term t = b.secondTerm();
			
			// Can the substitution be applied to logical variables in this parfactor?
			boolean isApplicable = logicalVariables().contains(x);
			
			// Is the replacement absent from constraints in this parfactor?
			boolean isNotInConstraints = !contains(constraints, t);
			
			// Does the substitution make sense?
			boolean isValidSubstitution = b.isValid();
			
			// If we are replacing for a logical variable, is it present in
			// this parfactor?
			boolean isLogicalVariable = (t.isVariable() ? logicalVariables().contains(t) : true);
			
			isSplittable = isApplicable && isNotInConstraints 
							&& isValidSubstitution && isLogicalVariable; 
		}
		return isSplittable;
	}
	
	
	@Override
	public boolean isEliminable(Prv prv) {
		Set<LogicalVariable> lvs = Sets.getInstance(0);
		for (Prv v : factor.variables()) {
			lvs.addAll(v.parameters());
		}
		return prv.parameters().containsAll(lvs);
	}
	
	
	@Override
	public Parfactor count(LogicalVariable lv) throws IllegalArgumentException {
		if (!isCountable(lv)) {
			throw new IllegalArgumentException();
		}
		return new Counter(this).count(lv);
	}
	
	// TODO encapsulate expand,  sum out and multiplication
	
	@Override
	public Parfactor expand(Prv cf, Term t) {
		
		/*
		 * First checks whether the set of constraints in the counting formula 
		 * is big enough to limit bound variable population size to 1.
		 * When that happens, expansion converts counting formula 
		 * #.A:C[f(...A...)] to f(...t...), where t = D(A):C.
		 */
		int cfIndex = factor.variables().indexOf(cf);
		Population pop = cf.boundVariable().individualsSatisfying(cf.constraints());
		if (pop.size() == 1) {
			Prv expandedPrv = ((CountingFormula) cf).toStdPrv();
			List<Prv> prvs = Lists.replace(prvs(), cf, expandedPrv);
			Parfactor expanded = new StdParfactorBuilder().constraints(constraints())
					.variables(prvs).values(factor.values()).build();
			return expanded;
		}
		
		// Creates the new set of PRVs
		List<Prv> vars = getExpandedVariables(cf, t);
		
		// Creates a constant factor with the new set of PRVs
		Factor newStructure = ConstantFactor.getInstance(vars);
		
		// Creates an array to store the values of the new parfactor
		Prv takenOut = vars.get(cfIndex + 1);
		int newSize = factor.size() * takenOut.range().size();
		List<BigDecimal> vals = new ArrayList<BigDecimal>(newSize);
		
		// Combines the new histogram with PRV that was taken out to get the
		// corresponding value in this factor
		for (Tuple<RangeElement> tuple : newStructure) {
			Tuple<RangeElement> combined = combine(tuple, cfIndex, cfIndex + 1);
			vals.add(factor.getValue(combined));
		}
		
		// Converts the counting formula to StdPrv if possible
		vars.set(cfIndex, ((CountingFormula)vars.get(cfIndex)).simplify());
		
		// Creates the expanded factor
		Parfactor expanded = new StdParfactorBuilder().constraints(constraints)
				.variables(vars).values(vals).build();
		
		return expanded;
	}
	
	
	/**
	 * Creates the new set of parameterized random variables for 
	 * {@link #expand(Prv, Term)}.
	 * <p>
	 * Let c =  #_A:CA [f(...,A,...)] and 
	 *     c' = #_A:(CA U {A != t}) [f(...,A,...)].
	 * </p>
	 * <p>
	 * Then V' = V \ {c} U {c'}
	 * </p>
	 * 
	 * @param cf The counting formula to expand
	 * @param t The term to be taken out from the counting formula
	 * @return the new set of parameterized random variables for 
	 * {@link #expand(Prv, Term)}.
	 */
	private List<Prv> getExpandedVariables(Prv cf, Term t) {
		List<Prv> vars = new ArrayList<Prv>(factor.variables());
		int cfIndex = vars.indexOf(cf);
		
		Prv expanded = ((CountingFormula) cf).remove(t);
		Prv takenOut = ((CountingFormula) cf).takeOut(t);
		
		vars.set(cfIndex, expanded);
		vars.add(cfIndex + 1, takenOut);
		
		return vars;
	}
	
	// tenho que dar um jeito de converter de volta para histograma...
	/**
	 * Returns the combination of elements at the specified indexes in the 
	 * specified tuple. The combined element is put in position <code>i</code> 
	 * and position <code>j</code> is removed.
	 * 
	 * @see #expand(Prv, Term)
	 * @param t The tuple to manipulate
	 * @param i The index of the counting formula
	 * @param j The index of a 'taken out' PRV
	 * @return The combination of elements at the specified indexes in the 
	 * specified tuple.
	 */
	private Tuple<RangeElement> combine(Tuple<RangeElement> t, int i, int j) {
		Tuple<RangeElement> tuple = Tuple.getInstance(t);
		RangeElement newHistogram = t.get(i).combine(t.get(j));
		tuple = tuple.set(i, newHistogram);
		tuple = tuple.remove(j);
		
		return tuple;
	}
	

	@Override
	public Parfactor multiply(Parfactor other) {
		return other.multiplicationHelper(this);
	}
	
	
	@Override
	public Parfactor multiplicationHelper(Parfactor other) {
		/*
		 * I am sure that 'other' is a StdParfactor, because this method is
		 * not called by AggregationParfactors.multiply()
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
				.variables(fixfj.variables()).values(fixfj.values()).build();
		
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
				.variables(fixfjCorrected.variables())
				.values(fixfjCorrected.values()).build();
		
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
		return  new Splitter(this, s).split();
	}

	
	@Override
	public Parfactor sumOut(Prv prv) {
		
		// Creates the intermediate parfactor g = <C,V\{f},F'>
		Factor sumOut = factor.sumOut(prv);
		Parfactor g = new StdParfactorBuilder().constraints(constraints)
				.factor(sumOut).build();
		
		// Correction exponent r = |gi|/|g|
		int gSize = g.size();
		int thisSize = size();
		
		// Creates the eliminated parfactor g' = <C,V\{f},F'^r>
		Factor corrected = sumOut.pow(thisSize, gSize);
		Parfactor summedOut = new StdParfactorBuilder().constraints(constraints)
				.factor(corrected).build();
		
		return summedOut;
	}
	
	
	@Override
	public Parfactor simplifyLogicalVariables() {
		Simplifier parfactor = new Simplifier(this);
		return parfactor.simplify();
	}
	

	@Override
	public void accept(ParfactorVisitor visitor, Parfactor p) {
		p.accept(visitor, this);
	}
	
	
	@Override
	public void accept(ParfactorVisitor visitor, StdParfactor p) {
		visitor.visit(this, p);
	}
	
	
	@Override
	public void accept(ParfactorVisitor visitor, AggregationParfactor p) {
		visitor.visit(p, this);
	}	
	
	
	/* ************************************************************************
	 *    toString, equals and hashCode
	 * ************************************************************************/
	
	@Override
	public String toString() {
		String result = "\nC = " + constraints + ",\n" + factor;
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
