package br.usp.poli.takiyama.acfove;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.cfove.StdParfactor;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.AggregationParfactor;
import br.usp.poli.takiyama.common.Builder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Distribution;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.MultiplicationChecker;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.ParfactorVisitor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdSplitResult;
import br.usp.poli.takiyama.common.Tuple;
import br.usp.poli.takiyama.common.VisitableParfactor;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Operator;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RangeElement;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.MathUtils;
import br.usp.poli.takiyama.utils.Sets;

public class AggParfactor implements AggregationParfactor, VisitableParfactor {

	private final Prv parent;
	private final Prv child;
	private final Factor factor;
	private final Operator<? extends RangeElement> operator;
	private final Set<Constraint> constraintsNotOnExtra;
	private final Set<Constraint> constraintsOnExtra;
	private final LogicalVariable extraVar;
	
	
	/* ************************************************************************
	 *    Builders
	 * ************************************************************************/
	
	/**
	 * Builder for {@link AggParfactor}.
	 * <p>
	 * There are two ways of specifying the factor associated with this
	 * aggregation parfactor: either pass a factor that was previously built
	 * using {@link #factor(Factor)} or pass the values that constitute the
	 * factor using {@link #values()}. If both are used, the former method
	 * has precedence over the latter.
	 * </p>
	 */
	public static class AggParfactorBuilder implements Builder<AggParfactor> {

		// mandatory parameters 
		private final Prv p;
		private Prv c; // not final to allow renaming
		private final Operator<? extends RangeElement> op;
		private final LogicalVariable lv;
		
		// optional parameters
		private Factor f;
		private List<BigDecimal> values;
		private Set<Constraint> constraintsOnExtra;
		private Set<Constraint> constraintsNotOnExtra;
		
		public AggParfactorBuilder(Prv p, Prv c, Operator<? extends RangeElement> op) 
					throws IllegalArgumentException {
			this.p = p;
			this.c = c;
			this.op = op;
			this.f = Factor.getInstance(); // empty factor
			this.values = new ArrayList<BigDecimal>(p.range().size());
			this.constraintsNotOnExtra = new HashSet<Constraint>();
			this.constraintsOnExtra = new HashSet<Constraint>();
			this.lv = setExtra();
		}
				
		/**
		 * Puts in <code>lv</code> the logical variable that is present in
		 * the parent PRV and not in child PRV.
		 * 
		 * @return The extra logical variable in parent PRV
		 * @throws IllegalArgumentException If the parent PRV has a number
		 * of extra variables that is different from 1.
		 */
		private LogicalVariable setExtra() throws IllegalArgumentException {
			List<LogicalVariable> pVars = p.parameters();
			List<LogicalVariable> cVars = c.parameters();
			List<LogicalVariable> diff = Lists.difference(pVars, cVars);
			if (diff.size() == 1) {
				return StdLogicalVariable.getInstance(diff.get(0));
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		public AggParfactorBuilder(AggregationParfactor ap) {
			this.p = ap.parent();
			this.c = ap.child();
			this.op = ap.operator();
			this.f = ap.factor();
			this.values = f.values();
			this.constraintsNotOnExtra = ap.constraintsNotOnExtra();
			this.constraintsOnExtra = ap.constraintsOnExtra();
			this.lv = StdLogicalVariable.getInstance(ap.extraVariable());
		}
		
		public AggParfactorBuilder constraint(Constraint c) {
			if (c.contains(lv)) {
				constraintsOnExtra.add(c);
			} else {
				constraintsNotOnExtra.add(c);
			}
			return this;
		}
		
		public AggParfactorBuilder constraints(Constraint ... c) {
			for (Constraint cons : c) {
				constraint(cons);
			}
			return this;
		}
		
		public AggParfactorBuilder constraints(Set<Constraint> c) {
			for (Constraint cons : c) {
				constraint(cons);
			}
			return this;
		}
		
		public AggParfactorBuilder factor(Factor f) throws IllegalArgumentException {
			if (f.variables().size() == 1 && f.variables().get(0).equals(p)) {
				this.f = Factor.getInstance(f);
				return this;
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		/**
		 * Sets the factor to a constant factor with the parent PRV.
		 * @return
		 */
		public AggParfactorBuilder factor() {
			f = Factor.getInstance(p);
			return this;
		}
		
		public AggParfactorBuilder values(double ... v) {
			values.clear();
			for (double d : v) {
				values.add(BigDecimal.valueOf(d));
			}
			return this;
		}
		
		public AggParfactorBuilder values(List<BigDecimal> v) {
			values.clear();
			values.addAll(v);
			return this;
		}
		
		AggParfactorBuilder child(Prv c) {
			this.c = c;
			return this;
		}
		
		@Override
		public AggParfactor build() {
			return new AggParfactor(this);
		}
		
	}
	
	
	/* ************************************************************************
	 *    Auxiliary classes
	 * ************************************************************************/
	
	/**
	 * This class encapsulates the algorithm to verify if this parfactor is 
	 * splittable on a specified substitution.
	 */
	private class Split {
		
		private final SubstitutionType substitution;
		
		private Split(LogicalVariable replaced, Term replacement) {
			// I think there should a better way to do this
			
			
			
			if (replaced.equals(extraVar)) {
				if (replacement.isConstant()) {
					Constant c = (Constant) replacement;
					substitution = new ExtraConstant(c);
				} else {
					LogicalVariable x = (LogicalVariable) replacement;
					substitution = new ExtraVariable(x);
				}
			} else {
				if (replacement.equals(extraVar)) {
					substitution = new VariableExtra(replaced);
				} else if (replacement.isConstant()) {
					Constant c = (Constant) replacement;
					substitution = new VariableConstant(replaced, c);
				} else {
					LogicalVariable x = (LogicalVariable) replacement;
					substitution = new VariableVariable(replaced, x);
				}
			}
		}
		
		/**
		 * Returns <code>true</code> if this parfactor can be split in this
		 * substitution.
		 * 
		 * @return <code>true</code> if this parfactor can be split in this
		 * substitution, <code>false</code> otherwise.
		 */
		private boolean isValid() {
			Constraint c = substitution.toInequalityConstraint();
			boolean isNotInConstraints = !constraints().contains(c);
			boolean isValid = substitution.isValid();
			return isNotInConstraints && isValid;
		}
		
		
		/**
		 * Represents substitutions of the type A/t, where A is the extra
		 * logical variable from the parent's PRV and t is a constant.
		 */
		private class ExtraConstant implements SubstitutionType {

			private final Constant constant;
			
			private ExtraConstant(Constant c) {
				this.constant = c;
			}
			
			@Override
			public boolean isValid() {
				return extraVar.population().contains(constant);
			}

			@Override
			public Constraint toInequalityConstraint() {
				Term a = StdLogicalVariable.getInstance(extraVar);
				Term t = Constant.getInstance(constant);
				return InequalityConstraint.getInstance(a, t);
			}
		}
		
		/**
		 * Represents substitutions of the type A/X, where A is the extra
		 * logical variable from the parent's PRV and X is a logical variable.
		 */
		private class ExtraVariable implements SubstitutionType {
			
			private final LogicalVariable var;
			
			private ExtraVariable(LogicalVariable x) {
				this.var = x;
			}

			@Override
			public boolean isValid() {
				List<LogicalVariable> param = parent.parameters();
				param.remove(extraVar);
				return param.contains(var);
			}

			@Override
			public Constraint toInequalityConstraint() {
				Term a = StdLogicalVariable.getInstance(extraVar);
				Term x = StdLogicalVariable.getInstance(var);
				return InequalityConstraint.getInstance(a, x);
			}
		}
		
		/**
		 * Represents substitutions of the type X/A, where A is the extra
		 * logical variable from the parent's PRV and X is a logical variable.
		 */
		private class VariableExtra implements SubstitutionType {
			
			private final LogicalVariable var;
			
			private VariableExtra(LogicalVariable x) {
				this.var = x;
			}

			@Override
			public boolean isValid() {
				List<LogicalVariable> param = parent.parameters();
				param.remove(extraVar);
				return param.contains(var);
			}

			@Override
			public Constraint toInequalityConstraint() {
				Term a = StdLogicalVariable.getInstance(extraVar);
				Term x = StdLogicalVariable.getInstance(var);
				return InequalityConstraint.getInstance(x, a);
			}
		}
		
		/**
		 * Represents substitutions of the type X/t, where X is a logical
		 * variable different from parent PRV's extra variable and
		 * t is a constant.
		 */
		private class VariableConstant implements SubstitutionType {

			private final LogicalVariable var;
			private final Constant constant;
			
			
			private VariableConstant(LogicalVariable x, Constant c) {
				this.var = x;
				this.constant = c;
			}
			
			@Override
			public boolean isValid() {
				return child.parameters().contains(var) 
						&& var.population().contains(constant);
			}

			@Override
			public Constraint toInequalityConstraint() {
				Term x = StdLogicalVariable.getInstance(var);
				Term t = Constant.getInstance(constant);
				return InequalityConstraint.getInstance(x, t);
			}
		}
		
		/**
		 * Represents substitutions of the type X/Y, where X is a logical
		 * variable different from parent PRV's extra variable and
		 * Y is a logical variable in the same condition as X.
		 */
		private class VariableVariable implements SubstitutionType {
			
			private final LogicalVariable x;
			private final LogicalVariable y;
			
			private VariableVariable(LogicalVariable x, LogicalVariable y) {
				this.x = x;
				this.y = y;
			}

			@Override
			public boolean isValid() {
				return child.parameters().contains(x) 
						&& child.parameters().contains(y);
			}

			@Override
			public Constraint toInequalityConstraint() {
				Term x1 = StdLogicalVariable.getInstance(x);
				Term y1 = StdLogicalVariable.getInstance(y);
				return InequalityConstraint.getInstance(x1, y1);
			}
		}
		
		
	}
	
	/**
	 * This class represents possible types of substitutions. There are five:
	 * A/c, A/X, X/A, X/c, X/Y, where A is the extra logical variable in 
	 * parent PRV, X and Y are logical variables different from A and c is
	 * a constant.
	 */
	private interface SubstitutionType {
		
		/**
		 * Returns <code>true</code> if this parfactor can be split in this
		 * substitution.
		 * 
		 * @return <code>true</code> if this parfactor can be split in this
		 * substitution, <code>false</code> otherwise.
		 */
		boolean isValid();
		
		/**
		 * Returns a {@link InequalityConstraint} based on terms from this
		 * substitution.
		 * 
		 * @return a {@link InequalityConstraint} based on terms from this
		 * substitution.
		 */
		Constraint toInequalityConstraint();
	}
	
	
	/**
	 * This class encapsulates the splitting algorithm
	 */
	private class Splitter {
		
		private final SplitterType splitter;
		
		private Splitter(AggregationParfactor agg, Substitution s)  {
			if (s.has(extraVar)) {
				splitter = new SplitterInvolvingExtra(agg, s);
			} else {
				splitter = new SplitterWithoutExtra(agg, s);
			}
		}
		
		private SplitResult split() {
			return splitter.split();
		}
		
		private class SplitterInvolvingExtra implements SplitterType {

			private Substitution substitution;
			private AggregationParfactor parfactorToSplit;
			private Prv auxChild;
			
			private SplitterInvolvingExtra(AggregationParfactor agg, Substitution s) {
				substitution = s;
				parfactorToSplit = agg;
				setAuxChild();
			}
			
			private void setAuxChild() {
				Prv child = parfactorToSplit.child();
				auxChild = child.rename(child.name() + "'");
			}
			
			@Override
			public SplitResult split() {
				return AggSplitResult.getInstance(result(), residue(), auxChild);
			}

			private AggregationParfactor residue() {
				Constraint c = substitution.first().toInequalityConstraint();
				return new AggParfactorBuilder(parfactorToSplit).constraint(c)
						.child(auxChild).build();
			}
			
			private Parfactor result() {
				Set<Constraint> constraints = applyToConstraints(substitution);
				List<Prv> prvs = setPrvs();
				List<BigDecimal> values = setValues(prvs);
				return new StdParfactorBuilder().constraints(constraints)
						.variables(prvs).values(values).build();
			}
			
			private List<Prv> setPrvs() {
				Prv p = parfactorToSplit.parent().apply(substitution);
				Prv c = parfactorToSplit.child();
				return Lists.listOf(p, auxChild, c);
			}
			
			private List<BigDecimal> setValues(List<Prv> prvs) {
				List<BigDecimal> values = new ArrayList<BigDecimal>();
				Factor newStructure = Factor.getInstance(prvs);
				for (Tuple<? extends RangeElement> tuple : newStructure) {
					RangeElement p = tuple.get(0);
					RangeElement cAux = tuple.get(1);
					RangeElement c = tuple.get(2);
					if (apply(operator, p, cAux).equals(c)) {
						values.add(correctedValue(p));
					} else {
						values.add(BigDecimal.ZERO);
					}
				}
				return values;
			}
			
			private BigDecimal correctedValue(RangeElement pVal) {
				Tuple<RangeElement> t = Tuple.getInstance(pVal);
				BigDecimal base = parfactorToSplit.factor().getValue(t);
				Prv p = replaceExtra();
				int rp = p.groundSetSize(constraintsNotOnExtra);
				int rc = parfactorToSplit.child().groundSetSize(constraintsNotOnExtra);
				return MathUtils.pow(base, rp, rc);
			}
			
			private Prv replaceExtra() {
				Binding b = Binding.getInstance(extraVar, extraVar.population().individualAt(0));
				Substitution s = Substitution.getInstance(b);
				Prv p = parfactorToSplit.parent().apply(s);
				return p;
			}
			
		}
		
		private class SplitterWithoutExtra implements SplitterType {

			private Substitution substitution;
			private AggregationParfactor parfactorToSplit;
			
			private SplitterWithoutExtra(AggregationParfactor agg, Substitution s) {
				substitution = s;
				parfactorToSplit = agg;
			}
			
			@Override
			public SplitResult split() {
				return StdSplitResult.getInstance(result(), residue());
			}
			
			private AggregationParfactor residue() {
				Constraint c = substitution.first().toInequalityConstraint();
				return new AggParfactorBuilder(parfactorToSplit).constraint(c).build();
			}
			
			private Parfactor result() {
				return parfactorToSplit.apply(substitution);
			}
			
		}
	}
	
	
	/**
	 * This class represents possible types of splits. There two of them:
	 * split involving parent's extra logical variable and split not involving
	 * the extra variable.
	 *
	 */
	private interface SplitterType {
		public SplitResult split();
	}
	
	
	/**
	 * This class encapsulates multiplication algorithm.
	 */
	private class Multiplier extends AggParfactorBuilder {
		private final Parfactor operand;
		
		private Multiplier(AggregationParfactor thiz, Parfactor other) {
			super(thiz);
			operand = other;
		}
		
		private Multiplier multiply() {
			super.f = super.f.multiply(operand.factor());
			return this;
		}
	}
	
	
	/**
	 * This class encapsulates sum out algorithm
	 */
	private class Eliminator {
		
		private final AggregationParfactor parfactor;
		private final StdParfactorBuilder builder;
		
		private Eliminator(AggregationParfactor ag) {
			this.parfactor = ag;
			this.builder = new StdParfactorBuilder();
		}
		
		private Parfactor eliminate() {
			Set<Constraint> constraints = parfactor.constraints();
			Prv child = parfactor.child();
			Factor factor = setFactor();
			
			Parfactor result = builder.constraints(constraints)
					.variables(child).factor(factor).build();
			
			if (childHasParameterNotInParent()) {
				LogicalVariable extraParameter = getExtraParameterFromChild();
				result = result.count(extraParameter);
			}
			
			return result;
		}
		
		private Factor setFactor() {
			Factor current = getBase();
			int domainSize = parfactor.extraVariable()
					.numberOfIndividualsSatisfying(parfactor.constraintsOnExtra());
			String binSize = Integer.toBinaryString(domainSize);
			
			List<RangeElement> childRange = parfactor.child().range();
			for (int k = 1; k < binSize.length(); k++) {
				Factor previous = Factor.getInstance(current);
				for (RangeElement x : childRange) { // for GAP, must be for (Tuple<RangeElement> tuple : previous)
					BigDecimal sum;
					if (binSize.charAt(k) == '0') {
						sum = getDoubleComposition(previous, x);
					} else {
						sum = getTripleComposition(previous, x);
					}
					Tuple<RangeElement> xTuple = Tuple.getInstance(x);
					current = current.set(xTuple, sum);
				}
			}
			
			return current;
		}
		
		private BigDecimal getDoubleComposition(Factor factor, RangeElement x) {
			BigDecimal sum = BigDecimal.ZERO;
			List<RangeElement> childRange = parfactor.child().range();
			for (RangeElement y : childRange) {
				Tuple<RangeElement> yTuple = Tuple.getInstance(y);
				for (RangeElement z : childRange) {
					Tuple<RangeElement> zTuple = Tuple.getInstance(z);
					if (apply(parfactor.operator(), y, z).equals(x)) {
						sum = sum.add(factor.getValue(yTuple).multiply(factor.getValue(zTuple)));
					}
				}
			}
			return sum;
		}
		
		private BigDecimal getTripleComposition(Factor factor, RangeElement x) {
			BigDecimal sum = BigDecimal.ZERO;
			List<RangeElement> childRange = parfactor.child().range();
			for (RangeElement y : childRange) {
				Tuple<RangeElement> yTuple = Tuple.getInstance(y);
				for (RangeElement z : childRange) {
					Tuple<RangeElement> zTuple = Tuple.getInstance(z);
					for (RangeElement w : childRange) {
						Tuple<RangeElement> wTuple = Tuple.getInstance(w);
						if (apply(parfactor.operator(), w, y, z).equals(x)) {
							BigDecimal fw = parfactor.factor().getValue(wTuple);
							BigDecimal fy = factor.getValue(yTuple);
							BigDecimal fz = factor.getValue(zTuple);
							sum = sum.add(fw.multiply(fy).multiply(fz));
						}
					}
				}
			}
			return sum;
		}
		
		private Factor getBase() {
			List<Prv> prvs = new ArrayList<Prv>(1);
			prvs.add(parfactor.child());
			Factor tempBase = Factor.getInstance(prvs);
			/*
			 * Actually i dont need all this right now. But this code is more
			 * generic, which will be useful when dealing with generalized
			 * aggregation parfactors.
			 */
			int size = parfactor.child().range().size();
			List<BigDecimal> vals = new ArrayList<BigDecimal>(size);
			for (Tuple<RangeElement> tuple : tempBase) {
				RangeElement childValue = tuple.get(0);
				if (parfactor.parent().range().contains(childValue)) {
					vals.add(parfactor.factor().getValue(tuple));
				} else {
					vals.add(BigDecimal.ZERO);
				}
			}
			return Factor.getInstance("", prvs, vals);
		}
		
		/**
		 * Returns <code>true</code> if the child PRV has exactly one extra
		 * parameter that is not present in parameters from parent PRV.
		 * 
		 * @return <code>true</code> if |param(c) \ param(p)| = 1, 
		 * <code>false</code> otherwise.
		 */
		private boolean childHasParameterNotInParent() {
			List<LogicalVariable> parentParam = parfactor.parent().parameters();
			List<LogicalVariable> childParam = parfactor.child().parameters();
			List<LogicalVariable> difference = Lists.difference(childParam, 
					parentParam);
			return (difference.size() == 1);
		}
		
		/**
		 * Returns the logical variable in the child PRV that is not present in
		 * the parent PRV.
		 * 
		 * @return The logical variable in the child PRV that is not present in
		 * the parent PRV.
		 */
		private LogicalVariable getExtraParameterFromChild() {
			List<LogicalVariable> difference = Lists.difference(
					parfactor.child().parameters(),
					parfactor.parent().parameters());
			return difference.get(0);
		}
	}
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	/**
	 * Creates an AggParfactor using the specified builder.
	 * 
	 * @param builder A {@link AggParfactorBuilder}
	 */
	private AggParfactor(AggParfactorBuilder builder) {
		Factor f;
		if (builder.f.isEmpty()) {
			f = Factor.getInstance("", builder.p, builder.values);
		} else {
			f = builder.f;
		}
		this.parent = builder.p;
		this.child = builder.c;
		this.factor = Factor.getInstance(f);
		this.operator = builder.op;
		this.constraintsNotOnExtra = builder.constraintsNotOnExtra;
		this.constraintsOnExtra = builder.constraintsOnExtra;
		this.extraVar = builder.lv;
	}

	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	
	@Override
	public Set<Constraint> constraints() {
		return Sets.union(constraintsNotOnExtra, constraintsOnExtra);
	}


	@Override
	public Set<Constraint> constraintsOnExtra() {
		return new HashSet<Constraint>(constraintsOnExtra);
	}


	@Override
	public Set<Constraint> constraintsNotOnExtra() {
		return new HashSet<Constraint>(constraintsNotOnExtra);
	}
	

	@Override
	public Factor factor() {
		return Factor.getInstance(factor);
	}

	
	@Override
	public Set<LogicalVariable> logicalVariables() {
		List<LogicalVariable> variables = Lists.union(child.parameters(), 
				parent.parameters());
		return new HashSet<LogicalVariable>(variables);
	}

	
	/**
	 * Returns a list containing the parent PRV and the child PRV, in this 
	 * order.
	 */
	@Override
	public List<Prv> prvs() {
		return Lists.listOf(parent, child);
	}
	

	@Override
	public Prv parent() {
		return StdPrv.getInstance(parent);
	}

	
	@Override
	public Prv child() {
		return StdPrv.getInstance(child);
	}
	

	/**
	 * Returns an empty list.
	 */
	@Override
	public List<Prv> context() {
		return new ArrayList<Prv>(0);
	}
	

	@Override
	public Operator<? extends RangeElement> operator() {
		return operator;
	}
	

	@Override
	public LogicalVariable extraVariable() {
		return StdLogicalVariable.getInstance(extraVar);
	}
	
	
	@Override
	public int size() {
		Set<LogicalVariable> vars = logicalVariables();
		vars.remove(extraVar);
		int size = 1;
		for (LogicalVariable lv : vars) {
			size = size * lv.numberOfIndividualsSatisfying(constraintsNotOnExtra);
		}
		return size;
	}
	

	@Override
	public Parfactor apply(Substitution s) {
		Set<Constraint> substitutedConstraints = applyToConstraints(s);
		Factor substitutedFactor = factor.apply(s);
		Prv substitutedParent = parent.apply(s);
		Prv substututedChild = child.apply(s);
		
		return new AggParfactorBuilder(substitutedParent, substututedChild, 
				operator).constraints(substitutedConstraints)
				.factor(substitutedFactor).build();
	}
	
	
	/**
	 * Returns the result of applying the specified substitution to the set
	 * of constraints from this parfactor.
	 * 
	 * @param s The substitution to apply to the constraints of this
	 * parfactor
	 * @return The result of applying the specified substitution to the set
	 * of constraints from this parfactor.
	 */
	private Set<Constraint> applyToConstraints(Substitution s) {
		Set<Constraint> substitutedConstraints = new HashSet<Constraint>(constraints().size());
		for (Constraint c : constraints()) {
			Constraint substituted = c.apply(s);
			substitutedConstraints.add(substituted);
		}
		return substitutedConstraints;
	}
	

	@Override
	public boolean contains(Prv prv) {
		return (prv.equals(parent) || prv.equals(child));
	}
	

	@Override
	public boolean isConstant() {
		boolean hasNoConstraints = constraints().isEmpty();
		boolean hasConstantFactor = factor.isConstant();
		return hasNoConstraints && hasConstantFactor;
	}

	
	/**
	 * Returns <code>false</code>.
	 * <p>
	 * A logical variable cannot be eliminated from 
	 * {@link AggregationParfactor}s.
	 * </p>
	 */
	@Override
	public boolean isCountable(LogicalVariable lv) {
		return false;
	}
	

	/**
	 * Returns <code>false</code>.
	 * <p>
	 * {@link AggregationParfactor}s do not contain {@link CountingFormula}s.
	 * </p>
	 */
	@Override
	public boolean isExpandable(Prv cf, Term t) {
		return false;
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

	
	@Override
	public boolean isSplittable(Substitution s) {
		boolean isSplittable = false;
		if (s.size() != 1) {
			isSplittable = false;
		} else {
			Binding bind = s.first();
			Split split = new Split(bind.firstTerm(), bind.secondTerm());
			isSplittable = split.isValid();
		}
		return isSplittable;
	}

	
	/**
	 * Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public Parfactor count(LogicalVariable lv) {
		throw new UnsupportedOperationException("Aggregation Parfactors are not countable");
	}
	

	/**
	 * Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public Parfactor expand(Prv cf, Term t) {
		throw new UnsupportedOperationException("Aggregation Parfactors are not expandable");
	}

	
	@Override
	public Parfactor multiply(Parfactor other) {
		// TODO check if multiplication is valid
		Multiplier result = new Multiplier(this, other);
		return result.multiply().build();
	}

		
	@Override
	public Parfactor multiplicationHelper(Parfactor other) {
		/* 
		 * I know that 'other' is a StdParfactor, because 
		 * AggregationParfactor.multiply() does not call this helper method. 
		 */
		return multiply(other);
	}

	
	@Override
	public SplitResult splitOn(Substitution s) throws IllegalArgumentException {
		Splitter result = new Splitter(this, s);
		return result.split();
	}

	@Override
	public Parfactor sumOut(Prv prv) {
		// TODO Auto-generated method stub
		
		// Example
		// RangeElement r = apply(operator, parent.range().get(0));
		
		// TODO check if prv is parent
		
		Eliminator eliminator = new Eliminator(this);
		Parfactor result = eliminator.eliminate();
		return result;
	}
	
	/*
	 * Life savior:
	 * http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ207
	 */
	
	public <T extends RangeElement> T apply(Operator<T> op, RangeElement e1, RangeElement e2) {
		T t1 = op.getTypeArgument().cast(e1);
		T t2 = op.getTypeArgument().cast(e2);
		return op.applyOn(t1, t2);
	}
	
	
	public <T extends RangeElement> T apply(Operator<T> op, RangeElement e1, RangeElement e2, RangeElement e3) {
		T t1 = op.getTypeArgument().cast(e1);
		T t2 = op.getTypeArgument().cast(e2);
		T t3 = op.getTypeArgument().cast(e3);
		return op.applyOn(t1, t2, t3);
	}
	

	@Override
	public Distribution toStdParfactors() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void accept(ParfactorVisitor visitor, Parfactor p) {
		/*
		 * As p has an unknown type, it is necessary to call its accept()
		 * method. JVM will infer its runtime type and call the appropriate
		 * method, which is one of the methods with the signature below. 
		 */
		p.accept(visitor, this);
	}
	
	
	@Override
	public void accept(ParfactorVisitor visitor, StdParfactor p) {
		/*
		 * I know this parfactor is an AggregationParfactor, and that p is a
		 * StdParfactor, thus types for visit() are defined.
		 */
		visitor.visit(this, p);
	}
	
	
	@Override
	public void accept(ParfactorVisitor visitor, AggregationParfactor p) {
		/*
		 * I know this parfactor is an AggregationParfactor, and that p is a
		 * AggregationParfactor, thus types for visit() are defined.
		 */
		visitor.visit(this, p);
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((child == null) ? 0 : child.hashCode());
		result = prime
				* result
				+ ((constraintsNotOnExtra == null) ? 0 : constraintsNotOnExtra
						.hashCode());
		result = prime
				* result
				+ ((constraintsOnExtra == null) ? 0 : constraintsOnExtra
						.hashCode());
		result = prime * result
				+ ((extraVar == null) ? 0 : extraVar.hashCode());
		result = prime * result + ((factor == null) ? 0 : factor.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
		if (!(obj instanceof AggParfactor)) {
			return false;
		}
		AggParfactor other = (AggParfactor) obj;
		if (child == null) {
			if (other.child != null) {
				return false;
			}
		} else if (!child.equals(other.child)) {
			return false;
		}
		if (constraintsNotOnExtra == null) {
			if (other.constraintsNotOnExtra != null) {
				return false;
			}
		} else if (!constraintsNotOnExtra.equals(other.constraintsNotOnExtra)) {
			return false;
		}
		if (constraintsOnExtra == null) {
			if (other.constraintsOnExtra != null) {
				return false;
			}
		} else if (!constraintsOnExtra.equals(other.constraintsOnExtra)) {
			return false;
		}
		if (extraVar == null) {
			if (other.extraVar != null) {
				return false;
			}
		} else if (!extraVar.equals(other.extraVar)) {
			return false;
		}
		if (factor == null) {
			if (other.factor != null) {
				return false;
			}
		} else if (!factor.equals(other.factor)) {
			return false;
		}
		if (operator == null) {
			if (other.operator != null) {
				return false;
			}
		} else if (!operator.equals(other.operator)) {
			return false;
		}
		if (parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!parent.equals(other.parent)) {
			return false;
		}
		return true;
	}
	
	
	@Override
	public String toString() {
		return "p = " + parent 
				+ ", c = " + child
				+ ", C_A = " + constraintsOnExtra
				+ ", C = " + constraintsNotOnExtra 
				+ "\n" + factor;	
	}

}
