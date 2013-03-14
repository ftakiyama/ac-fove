package br.usp.poli.takiyama.acfove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.acfove.operator.BooleanOperator;
import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.cfove.SimpleParfactor;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.Tuple;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

public class GeneralizedAggregationParfactor implements Parfactor {

	private final ParameterizedRandomVariable parent;
	private final ParameterizedRandomVariable child;
	
	private final ArrayList<ParameterizedRandomVariable> contextVariables;
	
	private final ParameterizedFactor factor;
	
	private final BooleanOperator operator; // should think of something more generic
	 
	private final HashSet<Constraint> constraintsOnExtraVariable; 
	private final HashSet<Constraint> otherConstraints;
	
	private final LogicalVariable extraVariable;
	
	/**
	 * Auxiliary class to build generalized aggregation parfactors using
	 * the Builder pattern.
	 */
	public static class Builder {
		// required parameters
		private final ParameterizedRandomVariable p;
		private final ParameterizedRandomVariable c;
		private final ArrayList<ParameterizedRandomVariable> cv;
		private final BooleanOperator op;
		private final LogicalVariable lv;
		
		// optional parameters
		private ParameterizedFactor f; 
		private HashSet<Constraint> constraintsOnExtra; 
		private HashSet<Constraint> otherConstraints;
		
		public Builder (
				ParameterizedRandomVariable p,
				ParameterizedRandomVariable c,
				BooleanOperator op,
				List<ParameterizedRandomVariable> cv) 
				throws IllegalArgumentException {
			
			this.p = ParameterizedRandomVariable.getInstance(p);
			this.c = ParameterizedRandomVariable.getInstance(c);
			this.op = op;
			this.cv = new ArrayList<ParameterizedRandomVariable>(cv);
			List<ParameterizedRandomVariable> fvars = new ArrayList<ParameterizedRandomVariable>(cv);
			fvars.add(0, p);
			this.f = ParameterizedFactor.getConstantInstance(fvars);
			this.constraintsOnExtra = new HashSet<Constraint>();
			this.otherConstraints = new HashSet<Constraint>();
			
			HashSet<LogicalVariable> vars = new HashSet<LogicalVariable>(p.getParameters());
			vars.removeAll(c.getParameters());
			if (vars.size() == 1) {
				this.lv = new LogicalVariable(vars.iterator().next());
			} else {
				throw new IllegalArgumentException(p 
						+ " does not have 1 extra logical variable: "
						+ vars + " " + c);
			}
		}
		
		public Builder(GeneralizedAggregationParfactor ap) {
			this.p = ParameterizedRandomVariable.getInstance(ap.parent);
			this.c = ParameterizedRandomVariable.getInstance(ap.child);
			this.cv = new ArrayList<ParameterizedRandomVariable>(ap.contextVariables);
			this.op = ap.operator;
			this.f = ParameterizedFactor.getInstance(ap.factor);
			this.constraintsOnExtra = new HashSet<Constraint>(ap.constraintsOnExtraVariable);
			this.otherConstraints = new HashSet<Constraint>(ap.otherConstraints);
			this.lv = new LogicalVariable(ap.extraVariable);
		}
		
		public Builder addConstraint(Constraint c) {
			if (c.contains(lv)) {
				this.constraintsOnExtra.add(c);
			} else {
				this.otherConstraints.add(c);
			}
			return this;
		}
		
		public Builder addConstraints(Set<Constraint> c) {
			for (Constraint constraint : c) {
				this.addConstraint(constraint);
			}
			return this;
		}
		
		public Builder factor(ParameterizedFactor f) {
			this.f = ParameterizedFactor.getInstance(f);
			return this;
		}
		
		public GeneralizedAggregationParfactor build() throws IllegalArgumentException {
			if (!isValid()) {
				throw new IllegalArgumentException(cv + "is not a valid set.");
			}
			return new GeneralizedAggregationParfactor(this);
		}

		/**
		 * Returns true if the set of context variables is consistent.
		 * <br>
		 * Let V be the set of context PRVs, C the set of constraints not 
		 * involving the extra parent logical variable and C<sub>A</sub> the set
		 * of constraints involving the extra parent logical variable.
		 * The set V is consistent if:
		 * <li> &forall; fi,fj &in; V, 
		 * (ground(fi):C&cup;C<sub>A</sub>) &cap; (ground(fj):C&cup;C<sub>A</sub>) = &empty;
		 * <li> &forall; fi &in; V, 
		 * (ground(fi):C&cup;C<sub>A</sub>) &cap; (ground(p):C&cup;C<sub>A</sub>) = &empty;
		 * <li> &forall; fi &in; V, 
		 * (ground(fi):C&cup;C<sub>A</sub>) &cap; (ground(c):C) = &empty;
		 * <br>
		 * <br>
		 * That is, each PRV from {p,c}&cup;V represent disjoint sets of
		 * random variables.
		 * 
		 * @return true if the set of context variables is consistent, false
		 * otherwise
		 */
		private boolean isValid() {
			// must use unification
			// TODO Implement this
			return true;
		}
	}
	
	/**
	 * Creates a generalized aggregation parfactor.
	 * @param builder A {@link GeneralizedAggregationParfactor} Builder.
	 */
	private GeneralizedAggregationParfactor(Builder builder) {
		this.parent = builder.p;
		this.child = builder.c;
		this.contextVariables = builder.cv;
		this.factor = builder.f;
		this.operator = builder.op;
		this.constraintsOnExtraVariable = builder.constraintsOnExtra;
		this.otherConstraints = builder.otherConstraints;
		this.extraVariable = builder.lv;
	}
	
	@Override
	public boolean contains(ParameterizedRandomVariable variable) {
		boolean isParent = variable.equals(parent);
		boolean isChild = variable.equals(child);
		boolean isContextVariable = contextVariables.contains(variable);
		return (isParent || isChild || isContextVariable);
	}

	@Override
	public ParameterizedFactor getFactor() {
		return ParameterizedFactor.getInstance(factor);
	}

	@Override
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public ParameterizedRandomVariable getChildVariable() {
		return ParameterizedRandomVariable.getInstance(child);
	}

	@Override
	public Set<Constraint> getConstraints() {
		Set<Constraint> allConstraints = new HashSet<Constraint>(constraintsOnExtraVariable);
		allConstraints.addAll(otherConstraints);
		return allConstraints;
	}

	@Override
	public Set<LogicalVariable> getLogicalVariables() {
		Set<LogicalVariable> allVariables = new HashSet<LogicalVariable>();
		allVariables.addAll(parent.getParameters());
		allVariables.addAll(child.getParameters());
		for (ParameterizedRandomVariable prv : contextVariables) {
			allVariables.addAll(prv.getParameters());
		}
		return allVariables;
	}

	@Override
	public int size() {
		Set<LogicalVariable> vars = this.getLogicalVariables();
		vars.remove(extraVariable);
		int numFactors = 1;
		for (LogicalVariable lv : vars) {
			numFactors = numFactors * lv.getSizeOfPopulationSatisfying(otherConstraints);
		}
		
		return numFactors;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public List<Parfactor> split(Binding s) {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public Parfactor count(LogicalVariable lv) {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public Set<Parfactor> propositionalize(LogicalVariable lv) {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public Parfactor expand(CountingFormula countingFormula, Term term) {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public Parfactor fullExpand(CountingFormula countingFormula) {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public Parfactor multiply(Parfactor parfactor) {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public Parfactor sumOut(ParameterizedRandomVariable prv) {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public List<Parfactor> splitOnConstraints(Set<Constraint> constraints) {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public Parfactor restoreLogicalVariableNames() {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public Parfactor replaceLogicalVariablesConstrainedToSingleConstant() {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public Parfactor renameLogicalVariables() {
		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public List<Parfactor> splitOnMgu(Substitution mgu) {

		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public Set<Parfactor> unify(Parfactor parfactor) {


		throw new UnsupportedOperationException("Not implemented!");
	}

	
	/**
	 * Converts this aggregation parfactor into standard parfactors.
	 * <br>
	 * <br>
	 * The conversion can be made only if the set of all constraints in
	 * this aggregation parfactor is in the normal form. One must check this
	 * condition before calling this method.
	 * <br>
	 * <br>
	 * The conversion results in two parfactors, one of them involving a 
	 * counting formula. This method returns a list of the resulting parfactors
	 * in the following order: the first involves counting formulas and
	 * the second does not.
	 * <br>
	 * <b>The result is a list so I can retrieve the parfactors I want later,
	 * although I'm not sure if this will be necessary</b>
	 * 
	 * @return The result of converting this aggregation parfactor into 
	 * standard parfactors.
	 */
	public List<Parfactor> convertToParfactor() {
		
		CountingFormula cf = CountingFormula.getInstance(extraVariable, 
				constraintsOnExtraVariable, parent);
		
		List<ParameterizedRandomVariable> vars = 
				new ArrayList<ParameterizedRandomVariable>(2);
		vars.add(cf);
		vars.add(child);
		vars.addAll(contextVariables);
		
		Iterator<Tuple> it = ParameterizedFactor.getIteratorOverTuples(vars);
		List<Number> values = new ArrayList<Number>();
		while (it.hasNext()) {
			Tuple current = it.next();
			if (aggregationIsConsistent(current, cf)) {
				values.add(1.0);
			} else {
				values.add(0.0);
			}
		}
		
		// first parfactor
		ParameterizedFactor factor = ParameterizedFactor.getInstance("", vars, values);
		Parfactor parfactor = SimpleParfactor.getInstance(this.otherConstraints, factor);
		factor = null; // object won't be used further
		
		List<Parfactor> result = new ArrayList<Parfactor>(2);
		result.add(parfactor);
		
		// second parfactor
		parfactor = SimpleParfactor.getInstance(getConstraints(), this.factor);
		result.add(parfactor);
		
		return result;
	}
	
	/**
	 * Calculates r = &otimes;(x &in; range(p), &otimes;(i = 1, H(v(cf), x), x))
	 * and returns true if r = v(c).
	 * <br>
	 * The function v is an assignment of values to PRVs. 
	 * @param t The tuple that represents the assignment of values
	 * @param cf The counting formula being analyzed
	 * @return True if the aggregation of parent nodes for the given 
	 * assignment of values is consistent with the child node value in 
	 * the tuple
	 */
	private boolean aggregationIsConsistent(Tuple t, CountingFormula cf) {
		
		int cfRangeIndex =  t.get(0);
		Set<Boolean> s = new HashSet<Boolean>(2 * this.parent.getRangeSize());

		for (String e : this.parent.getRange()) {
			int bucketIndex = this.parent.getRange().indexOf(e);
			int count = cf.getCount(cfRangeIndex, bucketIndex);
			
			if (count > 0) {
				Boolean eToBool = Boolean.valueOf(e);
				Boolean intermediate = this.operator.applyOn(eToBool, count); // <-- "returns the specified element" is wrong
				
				s.add(intermediate);
			}
		}
		
		Boolean b = this.operator.applyOn(s).booleanValue();
		int childRangeIndex = t.get(1);
		String childRangeElem = this.child.getElementFromRange(childRangeIndex);
		
		return (b.compareTo(Boolean.valueOf(childRangeElem)) == 0);
	}
	
}
