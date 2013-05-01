package br.usp.poli.takiyama.acfove;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.AggregationParfactor;
import br.usp.poli.takiyama.common.Builder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Distribution;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
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
import br.usp.poli.takiyama.utils.Sets;

public class AggParfactor implements AggregationParfactor {

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
		private final Prv c;
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
				f = Factor.getInstance(f);
				return this;
			} else {
				throw new IllegalArgumentException();
			}
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
		// TODO need to think how to do this. Maybe apply the same technique used to multiply (invert operands and call auxiliary method)
		throw new UnsupportedOperationException("Not implemented!");
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parfactor sumOut(Prv prv) {
		// TODO Auto-generated method stub
		RangeElement r = apply(operator, parent.range().get(0));
		return null;
	}
	
	/*
	 * Life savior:
	 * http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ207
	 */
	
	// helper method
	private <T extends RangeElement> T apply(Operator<T> op, RangeElement e1) {
		return op.apply(op.getTypeArgument().cast(e1), 2);
	}

	

	@Override
	public Distribution toStdParfactors() {
		// TODO Auto-generated method stub
		return null;
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
