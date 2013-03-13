package br.usp.poli.takiyama.acfove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import br.usp.poli.takiyama.acfove.operator.BooleanOperator;
import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.cfove.SimpleParfactor;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.Tuple;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.CountingFormula.Histogram;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.LogicalVariableNameGenerator;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;


public class AggregationParfactor implements Parfactor {

	private final ParameterizedRandomVariable parent;
	private final ParameterizedRandomVariable child;
	
	private final ParameterizedFactor factor;
	
	private final BooleanOperator operator; // should think of something more generic
	 
	private final HashSet<Constraint> constraintsOnExtraVariable; 
	private final HashSet<Constraint> otherConstraints;
	
	private final LogicalVariable extraVariable;
	
	// Builder pattern
	public static class Builder {
		// required parameters
		private final ParameterizedRandomVariable p;
		private final ParameterizedRandomVariable c;
		private final BooleanOperator op;
		private final LogicalVariable lv;
		
		// optional parameters
		private ParameterizedFactor f; 
		private HashSet<Constraint> constraintsOnExtra; 
		private HashSet<Constraint> otherConstraints;
		
		public Builder (
				ParameterizedRandomVariable p,
				ParameterizedRandomVariable c,
				BooleanOperator op) 
				throws IllegalArgumentException {
			
			this.p = ParameterizedRandomVariable.getInstance(p);
			this.c = ParameterizedRandomVariable.getInstance(c);
			this.op = op;
			this.f = ParameterizedFactor.getConstantInstance(p);
			this.constraintsOnExtra = new HashSet<Constraint>();
			this.otherConstraints = new HashSet<Constraint>();
			
			HashSet<LogicalVariable> vars = 
					new HashSet<LogicalVariable>(p.getParameters());
			vars.removeAll(c.getParameters());
			if (vars.size() == 1) {
				this.lv = new LogicalVariable(vars.iterator().next());
			} else {
				throw new IllegalArgumentException(p 
						+ " does not have 1 extra logical variable: "
						+ vars + " " + c);
			}
		}
		
		public Builder(AggregationParfactor ap) {
			this.p = ParameterizedRandomVariable.getInstance(ap.parent);
			this.c = ParameterizedRandomVariable.getInstance(ap.child);
			this.op = ap.operator;
			this.f = ParameterizedFactor.getInstance(ap.factor);
			this.constraintsOnExtra = new HashSet<Constraint>(ap.constraintsOnExtraVariable);
			this.otherConstraints = new HashSet<Constraint>(ap.otherConstraints);
			this.lv = new LogicalVariable(ap.extraVariable);
		}
		
		public Builder addConstraintsOnExtra(Set<Constraint> c) {
			this.constraintsOnExtra.addAll(c);
			return this;
		}
		
		public Builder addOtherConstraints(Set<Constraint> c) {
			this.otherConstraints.addAll(c);
			return this;
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
		
		public AggregationParfactor build() {
			return new AggregationParfactor(this);
		}
	}
	
	private AggregationParfactor(Builder builder) {
		this.parent = builder.p;
		this.child = builder.c;
		this.factor = builder.f;
		this.operator = builder.op;
		this.constraintsOnExtraVariable = builder.constraintsOnExtra;
		this.otherConstraints = builder.otherConstraints;
		this.extraVariable = builder.lv;
	}
	
	@Override
	public boolean contains(ParameterizedRandomVariable variable) {
		return (variable.equals(this.child) || variable.equals(this.parent));
	}

	@Override
	public ParameterizedFactor getFactor() {
		return ParameterizedFactor.getInstance(factor);
	}

	@Override
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables() {
		List<ParameterizedRandomVariable> vars = 
			new ArrayList<ParameterizedRandomVariable>();
		vars.add(child);
		vars.add(parent);
		return vars;
	}

	@Override
	public ParameterizedRandomVariable getChildVariable() {
		return ParameterizedRandomVariable.getInstance(child);
	}

	/**
	 * Returns the set of all constraints of this parfactors: the ones that
	 * involve the extra logical variable in the parent node and the ones that
	 * do not involve this variable.
	 * @return All constraints in the parfactor
	 */
	public Set<Constraint> getConstraints() {
		Set<Constraint> constraints = new HashSet<Constraint>();
		constraints.addAll(constraintsOnExtraVariable);
		constraints.addAll(otherConstraints);
		return constraints;
	}

	@Override
	public Set<LogicalVariable> getLogicalVariables() {
		Set<LogicalVariable> vars = new HashSet<LogicalVariable>();
		vars.addAll(child.getParameters());
		vars.addAll(parent.getParameters());
		return vars;
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
	public Parfactor restoreLogicalVariableNames() {
		AggregationParfactor ap = this;
		for (LogicalVariable lv : this.getLogicalVariables()) {
			Binding s = Binding.create(lv, LogicalVariableNameGenerator.restore(lv));
			ap = applySubstitution(s);
		}
		return ap;
	}
		
	/**
	 * Applies the specified substitution to this aggregation parfactor.
	 * <br>
	 * The substitution is applied on the set of constraints (both), the
	 * parent PRV, the child PRV and the factor.
	 * @param s The substitution to apply
	 * @return This parfactor with the specified substitution applied.
	 */
	private AggregationParfactor applySubstitution(Binding s) {
		Set<Constraint> allConstraints = 
				new HashSet<Constraint>(
						constraintsOnExtraVariable.size() 
						+ otherConstraints.size());
		
		for (Constraint c : constraintsOnExtraVariable) {
			Constraint nc = c.applySubstitution(s);
			if (nc != null) {
				allConstraints.add(nc);
			}
		}
		for (Constraint c : otherConstraints) {
			Constraint nc = c.applySubstitution(s);
			if (nc != null) {
				allConstraints.add(c);
			}
		}
		ParameterizedRandomVariable p = parent.applyOneSubstitution(s);
		ParameterizedRandomVariable c = child.applyOneSubstitution(s);
		ParameterizedFactor f = factor.applySubstitution(s);
		
		Builder builder = new Builder(p, c, this.operator);
		builder.addConstraints(allConstraints);
		builder.factor(f);
		
		return builder.build();
	}
	
	/* ************************************************************************
	 *    Split
	 * ************************************************************************/

	/**
	 * The format of the substitution.
	 * <li> X/t
	 * <li> A/t
	 * <li> X/A
	 * <br>
	 * where X is a logical variable, t is a term and A is the parent extra
	 * variable.
	 */
	private enum SubstitutionType {
		X_t, A_t, X_A
	}
	
	@Override
	public List<Parfactor> split(Binding s) {
		List<Parfactor> splitParfactors = new ArrayList<Parfactor>(2);
		switch (getSubstitutionType(s)) {
		case X_t:
			splitParfactors = splitOnSubstitutionXt(s);
			break;
		case A_t:
			splitParfactors = splitOnSubstitutionAt(s);
			break;
		case X_A:
			splitParfactors = splitOnSubstitutionXA(s);
			break;
		default:
			throw new IllegalArgumentException("Unrecognizable substitution: " 
					+ s);
		}
		return splitParfactors;
	}
	
	/**
	 * Returns true if this parfactor can be split on the specified 
	 * substitution.
	 * @param s The substitution that determines the split
	 * @return True if this parfactor can be split on the specified
	 * substitution, false otherwise.
	 */
	public boolean canBeSplit(Binding s) { // ugly implementation
		SubstitutionType substitutionType = getSubstitutionType(s);
		boolean isConsistent = false;
		LogicalVariable a, x;
		switch(substitutionType) {
		case A_t:
			a = s.getFirstTerm();
			if (s.getSecondTerm().isConstant()) {
				Constant t = (Constant) s.getSecondTerm();
				isConsistent = a.getPopulation().contains(t);
			} else {
				LogicalVariable t = (LogicalVariable) s.getSecondTerm();
				Set<LogicalVariable> param = parent.getParameters();
				param.remove(a);
				isConsistent = param.contains(t);
			}
			break;
		case X_A:
			a = (LogicalVariable) s.getSecondTerm();
			Set<LogicalVariable> param = parent.getParameters();
			param.remove(a);
			x = s.getFirstTerm();
			isConsistent = param.contains(x);
			break;
		case X_t:
			x = s.getFirstTerm();
			if (s.getSecondTerm().isConstant()) {
				Constant t = (Constant) s.getSecondTerm();
				isConsistent = child.getParameters().contains(x)
								&& x.getPopulation().contains(t);
			} else {
				LogicalVariable t = (LogicalVariable) s.getSecondTerm();
				isConsistent = child.getParameters().contains(x)
								&& child.getParameters().contains(t);
			}
			break;
		}
		Constraint c = Constraint.getInequalityConstraintFromBinding(s); 
		boolean isNotInConstraints = !isInConstraints(c);
		return isNotInConstraints && isConsistent;
	}

	/**
	 * Returns true if the specified constraint is in any of the constraints
	 * sets of this aggregation parfactor.
	 * @param c The constraint to search for
	 * @return True if the specified constraint is in any of the constraints
	 * sets of this aggregation parfactor, false otherwise.
	 */
	private boolean isInConstraints(Constraint c) {
		return (constraintsOnExtraVariable.contains(c)
				|| otherConstraints.contains(c));
	}
	
	/**
	 * Returns the format of the specified substitution.
	 * @param s The substitution to analyze
	 * @return The format of the specified substitution as a {@link SubstitutionType}.
	 */
	private SubstitutionType getSubstitutionType(Binding s) {
		LogicalVariable firstTerm = s.getFirstTerm();
		Term secondTerm = s.getSecondTerm();
		if (firstTerm.equals(extraVariable)) {
			return SubstitutionType.A_t;
		}
		if (secondTerm.isLogicalVariable()) {
			if (((LogicalVariable) secondTerm).equals(extraVariable)) {
				return SubstitutionType.X_A;
			}
		}
		return SubstitutionType.X_t;
	}
	
	/**
	 * Splits this parfactor on the specified substitution. The following
	 * conditiond must be met:
	 * <li> The substitution must be of the form X/t, where X is a logical
	 * variable and t is a term
	 * <li> X is not the extra logical variable in the parent PRV
	 * <li> t is not the extra logical variable in the parent PRV
	 * <li> (X &ne; t) &notin; C
	 * <li> X &in; param(c)
	 * <li> t &in; D(X) or t &in; param (c)
	 * <br>
	 * This method does not verify the conditions above. They must be checked
	 * using the method {@link AggregationParfactor#canBeSplit(Binding)}.
	 * <br>
	 * It is not recommended to call this method directly. 
	 * {@link AggregationParfactor#split(Binding)} should be used instead.
	 * <br>
	 * <br>
	 * Splitting g = &lang; C, p, c, Fp, &otimes;, C<sub>A</sub> &rang;
	 * on substitution {X/t} (which mets conditions above) results in two
	 * parfactors:
	 * <li> g[X/t], that is, g with all occurrences of X replaced with t 
	 * <li> &lang; C U {X &ne; t}, p, c, Fp, &otimes;, C<sub>A</sub> &rang;,
	 * the residual parfactor.
	 * <br>
	 * This method returns the two parfactors above in the order listed.
	 * 
	 * @param s The substitution on which we split this parfactor. It must
	 * obey the conditions above.
	 * @return A list where the first position is occupied by g[X/t] and the
	 * second position is the residual parfactor.
	 */
	private List<Parfactor> splitOnSubstitutionXt(Binding s) {
		Parfactor split = this.applySubstitution(s);
		Constraint constraint = Constraint.getInequalityConstraintFromBinding(s);
		Parfactor residue = this.addConstraint(constraint);
		List<Parfactor> result = new ArrayList<Parfactor>(2);
		result.add(split);
		result.add(residue);
		return result;
	}
	
	/**
	 * Adds the specified constraint to this parfactor.
	 * @param c The constraint to add.
	 * @return This parfactor with the specified constraint added. A new
	 * instance is created.
	 */
	private AggregationParfactor addConstraint(Constraint c) {
		Builder builder = new Builder(this.parent, this.child, this.operator);
		builder.addConstraintsOnExtra(this.constraintsOnExtraVariable);
		builder.addOtherConstraints(this.otherConstraints);
		builder.addConstraint(c);
		builder.factor(this.factor);
		return builder.build();
	}
	
	/**
	 * Splits this parfactor on the specified substitution. The following
	 * conditiond must be met:
	 * <li> The substitution must be of the form A/t, where A is the parent's
	 * extra logical variable and t is a term
	 * <li> t is not the extra logical variable in the parent PRV
	 * <li> (A &ne; t) &notin; C<sub>A</sub>
	 * <li> t &in; D(A) or t &in; param(p)\{A}
	 * <br>
	 * This method does not verify the conditions above. They must be checked
	 * using the method {@link AggregationParfactor#canBeSplit(Binding)}.
	 * <br>
	 * It is not recommended to call this method directly. 
	 * {@link AggregationParfactor#split(Binding)} should be used instead.
	 * <br>
	 * <br>
	 * Splitting g = &lang; C, p, c, Fp, &otimes;, C<sub>A</sub> &rang;
	 * on substitution {A/t} (which mets conditions above) results in two
	 * parfactors:
	 * <li> &lang; C, p, c', Fp, &otimes;, C<sub>A</sub> U {A &ne; t} &rang;
	 * <li> &lang; C U C<sub>A</sub>[A/t], {p[A/t], c', c} , Fc &rang;
	 * <br>
	 * where c' is an auxiliary PRV that is equal to c and Fc is a factor
	 * defined in Kisynski (2010).
	 * <br>
	 * This method returns the two parfactors above in the order listed. One
	 * should be aware to sum out c', since it is introduced by this operation.
	 * 
	 * @param s The substitution on which we split this parfactor. It must
	 * obey the conditions above.
	 * @return A list where the first position is occupied by the aggregation
	 * parfactor and the
	 * second position is the simple parfactor.
	 */
	private List<Parfactor> splitOnSubstitutionAt(Binding s) {
		
		// Builds the aggregation parfactor
		
		Constraint constraintFromBinding = Constraint.getInequalityConstraintFromBinding(s);
		ParameterizedRandomVariable cAux = this.child.rename(this.child.getName() + "'");
		
		Builder builder = new Builder(this.parent, cAux, this.operator);
		builder.addConstraintsOnExtra(this.constraintsOnExtraVariable);
		builder.addOtherConstraints(this.otherConstraints);
		builder.addConstraint(constraintFromBinding);
		builder.factor(this.factor);
		AggregationParfactor residue = builder.build();
		
		// Builds the simple parfactor
		// It is relatively simple, but the code below is very verbose and cluttered
		// TODO make it simpler
		
		Set<Constraint> constraints = new HashSet<Constraint>(this.otherConstraints);
		for (Constraint c : this.constraintsOnExtraVariable) {
			Constraint nc = c.applySubstitution(s);
			if (nc != null) {
				constraints.add(nc);
			}
		}
		
		List<ParameterizedRandomVariable> prvs = 
				new ArrayList<ParameterizedRandomVariable>(3);
		prvs.add(this.parent.applyOneSubstitution(s));
		prvs.add(cAux);
		prvs.add(this.child);

		List<Number> mapping = new ArrayList<Number>();
		Iterator<Tuple> it = ParameterizedFactor.getIteratorOverTuples(prvs);
		while (it.hasNext()) {
			Tuple currentTuple = it.next();
			
			int rangeIndexOfP = currentTuple.get(0);
			int rangeIndexOfCAux = currentTuple.get(1);
			int rangeIndexOfC = currentTuple.get(2);
			
			String valueOfP = this.parent.getElementFromRange(rangeIndexOfP);
			String valueOfCAux = cAux.getElementFromRange(rangeIndexOfCAux);
			String valueOfC = this.child.getElementFromRange(rangeIndexOfC);
			
			Boolean p = Boolean.valueOf(valueOfP); // ugly
			Boolean c = Boolean.valueOf(valueOfC); 
			Boolean caux = Boolean.valueOf(valueOfCAux);
			
			if (this.operator.applyOn(p, caux).equals(c)) {
				double correction = getCorrectionFraction();
				ArrayList<Integer> tupleValue = new ArrayList<Integer>(1);
				tupleValue.add(rangeIndexOfP);
				Tuple t = new Tuple(tupleValue);
				double v = this.factor.getTupleValue(this.factor.getTupleIndex(t));
				mapping.add(Math.pow(v, correction));
			} else {
				mapping.add(0.0);
			}
		}
		ParameterizedFactor fc = ParameterizedFactor.getInstance("fc", prvs, mapping);
		SimpleParfactor split = SimpleParfactor.getInstance(constraints, fc);
		
		List<Parfactor> result = new ArrayList<Parfactor>(2);
		result.add(residue);
		result.add(split);
		return result;
	}
	
	/**
	 * Splits this parfactor on the specified substitution. The following
	 * conditiond must be met:
	 * <li> The substitution must be of the form X/A, where A is the parent's
	 * extra logical variable and X is a logical variable
	 * <li> X is not the extra logical variable in the parent PRV
	 * <li> (X &ne; A) &notin; C<sub>A</sub>
	 * <li> X &in; param(p)\{A}
	 * <br>
	 * This method does not verify the conditions above. They must be checked
	 * using the method {@link AggregationParfactor#canBeSplit(Binding)}.
	 * <br>
	 * It is not recommended to call this method directly. 
	 * {@link AggregationParfactor#split(Binding)} should be used instead.
	 * <br>
	 * <br>
	 * Splitting g = &lang; C, p, c, Fp, &otimes;, C<sub>A</sub> &rang;
	 * on substitution {X/A} (which mets conditions above) results in two
	 * parfactors:
	 * <li> &lang; C, p, c', Fp, &otimes;, C<sub>A</sub> U {A &ne; t} &rang;
	 * <li> &lang; C[A/t] U C<sub>A</sub>, {p[X/A], c', c} , Fc &rang;
	 * <br>
	 * where c' is an auxiliary PRV that is equal to c and Fc is a factor
	 * defined in Kisynski (2010).
	 * <br>
	 * This method returns the two parfactors above in the order listed. One
	 * should be aware to sum out c', since it is introduced by this operation.
	 * 
	 * @param s The substitution on which we split this parfactor. It must
	 * obey the conditions above.
	 * @return A list where the first position is occupied by the aggregation
	 * parfactor and the
	 * second position is the simple parfactor.
	 */
	private List<Parfactor> splitOnSubstitutionXA(Binding s) {
		
		/* This method is actually the same as splitOnSubstitutionAt.
		 * The only difference is on the set of constraints on the simple
		 * parfactor, instead of using C_A I use C.
		 * Crap piece of code.
		 */
		
		// Builds the aggregation parfactor
		
		Constraint constraintFromBinding = Constraint.getInequalityConstraintFromBinding(s);
		ParameterizedRandomVariable cAux = this.child.rename(this.child.getName() + "'");
		
		Builder builder = new Builder(this.parent, cAux, this.operator);
		builder.addConstraintsOnExtra(this.constraintsOnExtraVariable);
		builder.addOtherConstraints(this.otherConstraints);
		builder.addConstraint(constraintFromBinding);
		builder.factor(this.factor);
		AggregationParfactor residue = builder.build();
		
		// Builds the simple parfactor
		
		Set<Constraint> constraints = new HashSet<Constraint>(this.constraintsOnExtraVariable);
		for (Constraint c : this.otherConstraints) {
			Constraint nc = c.applySubstitution(s);
			if (nc != null) {
				constraints.add(nc);
			}
		}
		
		List<ParameterizedRandomVariable> prvs = 
				new ArrayList<ParameterizedRandomVariable>(3);
		prvs.add(this.parent.applyOneSubstitution(s));
		prvs.add(cAux);
		prvs.add(this.child);

		List<Number> mapping = new ArrayList<Number>();
		Iterator<Tuple> it = ParameterizedFactor.getIteratorOverTuples(prvs);
		while (it.hasNext()) {
			Tuple currentTuple = it.next();
			
			int rangeIndexOfP = currentTuple.get(0);
			int rangeIndexOfCAux = currentTuple.get(1);
			int rangeIndexOfC = currentTuple.get(2);
			
			String valueOfP = this.parent.getElementFromRange(rangeIndexOfP);
			String valueOfCAux = cAux.getElementFromRange(rangeIndexOfCAux);
			String valueOfC = this.child.getElementFromRange(rangeIndexOfC);
			
			Boolean p = Boolean.valueOf(valueOfP); // ugly
			Boolean c = Boolean.valueOf(valueOfC); 
			Boolean caux = Boolean.valueOf(valueOfCAux);
			
			if (this.operator.applyOn(p, caux).equals(c)) {
				double correction = getCorrectionFraction();
				ArrayList<Integer> tupleValue = new ArrayList<Integer>(1);
				tupleValue.add(rangeIndexOfP);
				Tuple t = new Tuple(tupleValue);
				double v = this.factor.getTupleValue(this.factor.getTupleIndex(t));
				mapping.add(Math.pow(v, correction));
			} else {
				mapping.add(0.0);
			}
		}
		ParameterizedFactor fc = ParameterizedFactor.getInstance("fc", prvs, mapping);
		SimpleParfactor split = SimpleParfactor.getInstance(constraints, fc);
		
		List<Parfactor> result = new ArrayList<Parfactor>(2);
		result.add(residue);
		result.add(split);
		return result;
	}

	/**************************************************************************/
	
	
	/* ************************************************************************
	 *    Multiplication
	 * ************************************************************************/
	
	@Override
	public Parfactor multiply(Parfactor parfactor) throws IllegalArgumentException {
		if (!canMultiply(parfactor)) {
			throw new IllegalArgumentException(this 
					+ "\n cannot be multiplied by \n" 
					+ parfactor);
		}
		
		ParameterizedFactor first = this.getFactor();
		ParameterizedFactor second = parfactor.getFactor();
		ParameterizedFactor result = first.multiply(second);
		
		return setFactor(result);
	}
	
	/**
	 * Returns true if this parfactor can be multiplied by the specified
	 * parfactor, false otherwise.
	 * <br>
	 * This parfactor can be multiplied by the specified parfactor if:
	 * <li> The specified parfactor is a simple parfactor
	 * <li> The specified parfactor has only the parent PRV
	 * <li> The constraints of the specified parfactor are equal to the union
	 * of all constraints in this aggregation parfactor.
	 * 
	 * @param p The candidate parfactor to multiply this one
	 * @return True if this parfactor can be multiplied by the specified
	 * parfactor, false otherwise.
	 */
	public boolean canMultiply(Parfactor p) {
		if (p instanceof AggregationParfactor) {
			return false;
		}
		boolean compatibleConstraints = p.getConstraints().equals(this.getConstraints());
		List<ParameterizedRandomVariable> prvs = p.getParameterizedRandomVariables();
		boolean parfactorOnParent = (prvs.size() == 1) && prvs.contains(this.parent);
		
		return compatibleConstraints && parfactorOnParent;
	}
	
	/**
	 * Sets the factor in this aggregation parfactor.
	 * @param f The new parameterized factor
	 * @return This aggregation parfactor with the specified parameterized
	 * factor.
	 */
	private AggregationParfactor setFactor(ParameterizedFactor f) {
		Builder builder = new Builder(this);
		builder.factor(f);
		return builder.build();
	}
	
	/**************************************************************************/
	
	
	/* ************************************************************************
	 *    Sum out
	 * ************************************************************************/
	
	@Override
	public Parfactor sumOut(ParameterizedRandomVariable prv) {
		
		Set<Constraint> c = new HashSet<Constraint>(this.otherConstraints);
		ParameterizedFactor f = calculateSumOutFactor();
		Parfactor result;
		if (childHasExtra()) {
			result = countChildVariable(f);
		} else {
			result = SimpleParfactor.getInstance(c, f);
		}
		
		return result;
	}
	
	/**
	 * Calculates the factor Fm. (cannot think right now)
	 * @return
	 */
	private ParameterizedFactor calculateSumOutFactor() {
		
		// base
		List<Number> currentFactor = new ArrayList<Number>(child.getRangeSize());
		for (String x : child.getRange()) {
			if (this.parent.getRange().contains(x)) {
				int index = parent.getRange().indexOf(x); // only works if p is the only PRV in Fp
				currentFactor.add(this.factor.getTupleValue(index));
			} else {
				currentFactor.add(0.0);
			}
		}
		
		// Fk
		int domainSize = this.extraVariable.getSizeOfPopulationSatisfying(constraintsOnExtraVariable);
		String bin = Integer.toBinaryString(domainSize);
		for (int k = 1; k < bin.length(); k++) {
			List<Number> previousFactor = new ArrayList<Number>(currentFactor);
			if (bin.charAt(k) == '0') {
				for (String x : this.child.getRange()) {
					double sum = 0.0;
					Boolean x1 = Boolean.valueOf(x);
					for (String y : this.child.getRange()) {
						Boolean y1 = Boolean.valueOf(y);
						int yindex = this.child.getRange().indexOf(y);
						for (String z : this.child.getRange()) {
							Boolean z1 = Boolean.valueOf(z);
							int zindex = this.child.getRange().indexOf(z);
							if (this.operator.applyOn(y1, z1) == x1) {
								sum = sum 
									+ previousFactor.get(yindex).doubleValue() 
										* previousFactor.get(zindex).doubleValue();
							}
						}	
					}
					int xindex = this.child.getRange().indexOf(x);
					currentFactor.set(xindex, sum);
				}
			} else {
				for (String x : this.child.getRange()) {
					double sum = 0.0;
					Boolean x1 = Boolean.valueOf(x);
					for (String y : this.child.getRange()) {
						Boolean y1 = Boolean.valueOf(y);
						int yindex = this.child.getRange().indexOf(y);
						for (String z : this.child.getRange()) {
							Boolean z1 = Boolean.valueOf(z);
							int zindex = this.child.getRange().indexOf(z);
							for (String w : this.child.getRange()) {
								Boolean w1 = Boolean.valueOf(w);
								if (this.operator.applyOn(w1, y1, z1) == x1) {
									int index = parent.getRange().indexOf(w); // only works if p is the only PRV in Fp
									sum = sum 
										+ this.factor.getTupleValue(index)
											* previousFactor.get(yindex).doubleValue() 
											* previousFactor.get(zindex).doubleValue();
								}
							}
						}	
					}
					int xindex = this.child.getRange().indexOf(x);
					currentFactor.set(xindex, sum);
				}
			}
		}
		
		List<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>(1);
		prvs.add(this.child);
		ParameterizedFactor f = ParameterizedFactor.getInstance("", prvs, currentFactor);
		return f;
	}
	
	/**
	 * Counts the child variable resulting from calculateSumOutFactor().
	 * 
	 * @param f The parameterized factor containing the logical variable 
	 * to be counted
	 * @return The result of counting the variable from the specified factor.
	 */
	private Parfactor countChildVariable(ParameterizedFactor f) {
		
		// build constraints on C_E
		LogicalVariable extra = getExtraInChild();
		Set<Constraint> constraintsOnExtra = getConstraintsContaining(extra);
		Set<Constraint> constraints = new HashSet<Constraint>(this.otherConstraints);
		constraints.removeAll(constraintsOnExtra);
				
		// Build counting formula
		CountingFormula cf = CountingFormula.getInstance(extra, constraintsOnExtra, child);
		
		List<Number> mapping = new ArrayList<Number>();
		for (CountingFormula.Histogram<String> h : cf.getCountingFormulaRange()) {
			int extraPopulationSize = extra.getSizeOfPopulationSatisfying(constraintsOnExtra);
			if (h.containsValue(extraPopulationSize)) {
				int index = cf.getCountingFormulaRange().indexOf(h);
				mapping.add(f.getTupleValue(index));
			} else {
				mapping.add(0.0);
			}
		}
		
		String name = f.getName();
		List<ParameterizedRandomVariable> variables = f.getParameterizedRandomVariables();
		ParameterizedFactor nf = ParameterizedFactor.getInstance(name, variables, mapping);
		Parfactor result = SimpleParfactor.getInstance(constraints, nf);
		return result;
	}
	
	/**
	 * Returns true if the child PRV is parameterized by a logical variable
	 * not present in the parent PRV.
	 * <br>
	 * In other words, if |param(c) \ param(p)| = 1, returns true. 
	 * 
	 * @return True if the child PRV is parameterized by a logical variable
	 * not present in the parent PRV, false otherwise.
	 */
	private boolean childHasExtra() {
		Set<LogicalVariable> paramParent = parent.getParameters();
		Set<LogicalVariable> paramChild = new HashSet<LogicalVariable>(child.getParameters());
		paramChild.removeAll(paramParent);
		return (paramChild.size() == 1);
	}
	
	/**
	 * Returns the logical variable in the child PRV that is not present in
	 * the parent PRV.
	 * @return The logical variable in the child PRV that is not present in
	 * the parent PRV.
	 * @throws NoSuchElementException If there is no extra variable in the child.
	 */
	private LogicalVariable getExtraInChild() throws NoSuchElementException {
		Set<LogicalVariable> paramParent = parent.getParameters();
		Set<LogicalVariable> paramChild = child.getParameters();
		paramChild.removeAll(paramParent);
		Iterator<LogicalVariable> it = paramChild.iterator();
		if (!it.hasNext()) {
			throw new NoSuchElementException("Child does not have extra LV.");
		}
		return paramChild.iterator().next();
	}
	
	/**
	 * Returns the set of constraints in this aggregation parfactor that 
	 * contain the specified logical variable.
	 * @param lv The logical variable to look for in constraints.
	 * @return The set of constraints that involve the specified logical
	 * variable.
	 */
	private Set<Constraint> getConstraintsContaining(LogicalVariable lv) {
		Set<Constraint> constraints = new HashSet<Constraint>();
		for (Constraint c : getConstraints()) {
			if (c.contains(lv)) {
				constraints.add(c);
			}
		}
		return constraints;
	}
	
	/**
	 * Returns true if the specified parameterized random variable can be 
	 * summed out from this aggregation parfactor.
	 * <br>
	 * <br>
	 * To sum out a PRV v(...) from this parfactor, the following must be true:
	 * <li> v(...) is the parent PRV, that is, v(...) = p(...A...)
	 * <li> The set of all constraints from this parfactor must be in the
	 * normal form.
	 * <li> param(c(...)) = param(p(...A....))\{A} <b>or</b>
	 *      param(c(...E...))\{E} = param(p(...A....))\{A}
	 * 
	 * @param prv The ParameterizedRandomVariable to sum out.
	 * @return True if the specified PRV can be summed out from this parfactor,
	 * false otherwise.
	 */
	public boolean canSumOut(ParameterizedRandomVariable prv) {
		Set<LogicalVariable> paramC = new HashSet<LogicalVariable>(this.child.getParameters());
		Set<LogicalVariable> paramP = new HashSet<LogicalVariable>(this.parent.getParameters());
		paramP.remove(extraVariable);
		boolean compatibleParams = (paramC.equals(paramP)) 
								   || (paramC.size() - 1 == paramP.size());
		boolean isParent = prv.equals(this.parent);
		
		return isInNormalForm() && compatibleParams && isParent;
	}
	
	/**
	 * Checks if this parfactor is in normal form.
	 * <br>
	 * A parfactor is in normal form if for each inequality constraint 
	 * (X &ne; Y) &in; C we have &epsilon;<sub>X</sub><sup>C</sup>\{Y} = 
	 *  &epsilon;<sub>Y</sub><sup>C</sup>\{X}. X and Y are logical variables.
	 * @return
	 */
	private boolean isInNormalForm() {
		for (Constraint c : this.getConstraints()) {
			if (c.getSecondTerm() instanceof LogicalVariable) {
				Set<Term> firstSet = getExcludedSet(c.getFirstTerm());
				firstSet.remove(c.getSecondTerm());
				Set<Term> secondSet = getExcludedSet((LogicalVariable) c.getSecondTerm());
				secondSet.remove(c.getFirstTerm());
				if (! firstSet.equals(secondSet)) {
					return false;
				}
			}
 		}
		return true;
	}
	
	/**
	 * Returns the excluded set for logical variable X, that is, the set of
	 * terms t such that (X &ne; t) &in; C.
	 * @param x The logical variable.
	 * @return The excluded set for logical variable X.
	 */
	private Set<Term> getExcludedSet(LogicalVariable x) {
		HashSet<Term> excludedSet = new HashSet<Term>();
		for (Constraint constraint : this.getConstraints()) {
			if (constraint.contains(x)) {
				excludedSet.add(constraint.getSecondTerm()); // i do not verify if the other term is in fact the second term of the constraint...
			}
		}
		return excludedSet;
	}
	
	/**************************************************************************/
	
	
	@Override
	public Parfactor count(LogicalVariable lv) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	@Override
	public Set<Parfactor> propositionalize(LogicalVariable lv) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	@Override
	public Parfactor expand(CountingFormula countingFormula, Term term) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	@Override
	public Parfactor fullExpand(CountingFormula countingFormula) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	@Override
	public Parfactor replaceLogicalVariablesConstrainedToSingleConstant() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	@Override
	public Parfactor renameLogicalVariables() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	@Override
	public List<Parfactor> splitOnMgu(Substitution mgu) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	@Override
	public List<Parfactor> splitOnConstraints(Set<Constraint> constraints) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	@Override
	public Set<Parfactor> unify(Parfactor parfactor) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method!");
	}
	
	
	/* ************************************************************************
	 * 
	 *   Class specific methods
	 * 
	 * ***********************************************************************/
	
	
	/* ====================================================================== */
	/*   Conversion to standard parfactors                                    */
	/* ====================================================================== */
	
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
	 * in the following order: the first does involves counting formulas and
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

	/**
	 * Returns the correction factor used in split operations.
	 * <br>
	 * The correction factor is given by rp / rc, where
	 * <li> rp = |ground(p(...,a,...)):C|, a &in; D(A):C<sub>A</sub>
	 * <li> rc = |ground(c(...)):C| 
	 * @return The correction factor used in split operations.
	 */
	private double getCorrectionFraction() {
		Binding s = Binding.create(extraVariable, extraVariable.getPopulation().getIndividual(0));
		ParameterizedRandomVariable p = this.parent.applyOneSubstitution(s);
		int rp = p.getGroundSetSize(otherConstraints);
		int rc = this.child.getGroundSetSize(otherConstraints);
		
		return ((double) rp) / rc;
	}
	
	/* ====================================================================== */
	/*   toString, hashCode and equals                                        */
	/* ====================================================================== */
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((child == null) ? 0 : child.hashCode());
		result = prime
				* result
				+ ((constraintsOnExtraVariable == null) ? 0
						: constraintsOnExtraVariable.hashCode());
		result = prime * result
				+ ((extraVariable == null) ? 0 : extraVariable.hashCode());
		result = prime * result + ((factor == null) ? 0 : factor.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime
				* result
				+ ((otherConstraints == null) ? 0 : otherConstraints.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AggregationParfactor other = (AggregationParfactor) obj;
		if (child == null) {
			if (other.child != null)
				return false;
		} else if (!child.equals(other.child))
			return false;
		if (constraintsOnExtraVariable == null) {
			if (other.constraintsOnExtraVariable != null)
				return false;
		} else if (!constraintsOnExtraVariable
				.equals(other.constraintsOnExtraVariable))
			return false;
		if (extraVariable == null) {
			if (other.extraVariable != null)
				return false;
		} else if (!extraVariable.equals(other.extraVariable))
			return false;
		if (factor == null) {
			if (other.factor != null)
				return false;
		} else if (!factor.equals(other.factor))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (otherConstraints == null) {
			if (other.otherConstraints != null)
				return false;
		} else if (!otherConstraints.equals(other.otherConstraints))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "p = " + parent 
				+ ", c = " + child
				+ ", C_A = " + constraintsOnExtraVariable
				+ ", C = " + otherConstraints 
				+ "\n" + factor;
	}
	
}

//package br.usp.poli.takiyama.acfove;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import br.usp.poli.takiyama.common.Constraint;
//import br.usp.poli.takiyama.cfove.ParameterizedFactor;
//import br.usp.poli.takiyama.cfove.SimpleParfactor;
//import br.usp.poli.takiyama.cfove.SimpleParfactor;
//import br.usp.poli.takiyama.prv.LogicalVariable;
//import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
//import br.usp.poli.takiyama.utils.Sets;
//
///**
// * An aggregation parfactor [Kisynski, 2010] is a hextuple &lang; C, 
// * p(...,A,...), c(...), F<sub>p</sub>, &otimes; , C<sub>A</sub> &rang;, 
// * where
// * <br>
// * <li> p(...,A,...) and c(...) are parameterized random variables 
// * <li> the range of p is a subset of the range of c 
// * <li> A is the only logical variable in p(...,A,...) that is not in c(...) 
// * <li> C is a set of inequality constraints not involving A 
// * <li> F<sub>p</sub> is a factor from the range of p to real numbers 
// * <li> &otimes; is a commutative and associative deterministic binary operator 
// * over the range of c 
// * <li> C<sub>A</sub> is a set of inequality constraints involving A, 
// * such that (D(A) : C<sub>A</sub>) &ne; &empty;.
// * @author ftakiyama
// *
// */
//public class AggregationParfactor implements SimpleParfactor {
//	
//	private ParameterizedRandomVariable parent;
//	private ParameterizedRandomVariable child;
//	
//	private ParameterizedFactor parentFactor;
//	
//	private Operator operator;
//	 
//	private HashSet<Constraint> constraintsInA; // I must guarantee that Constraint has a good hashCode() method
//	private HashSet<Constraint> constraintsNotInA;
//	
//	private LogicalVariable extraVariableInParent;
//	
//	private String name;
//	
//	private AggregationParfactor(
//			String name,
//			Set<Constraint> constraintsInA,
//			ParameterizedRandomVariable parent,
//			ParameterizedRandomVariable child,
//			ParameterizedFactor factor,
//			Operator operator,
//			Set<Constraint> constraintsNotInA) {
//		
//		this.name = name;
//		this.parent = parent;
//		this.child = child;
//		this.parentFactor = factor;
//		this.operator = operator;
//		this.constraintsInA = new HashSet<Constraint>(constraintsInA);
//		this.constraintsNotInA = new HashSet<Constraint>(constraintsNotInA);
//		Set<LogicalVariable> parameters = new HashSet<LogicalVariable>(parent.getParameters());
//		parameters.removeAll(child.getParameters());
//		LogicalVariable[] extraParameter = parameters.toArray(new LogicalVariable[1]);
//		this.extraVariableInParent = extraParameter[0];
//	}
//	
//	public static AggregationParfactor getInstance(
//			Set<Constraint> constraintsInA,
//			ParameterizedRandomVariable parent,
//			ParameterizedRandomVariable child,
//			ParameterizedFactor factor,
//			Operator operator,
//			Set<Constraint> constraintsNotInA) {
//		
//		return new AggregationParfactor("", constraintsInA, parent, child, factor, operator, constraintsNotInA);
//	}
//	
//	public static AggregationParfactor getInstance(
//			String name,
//			Set<Constraint> constraintsInA,
//			ParameterizedRandomVariable parent,
//			ParameterizedRandomVariable child,
//			ParameterizedFactor factor,
//			Operator operator,
//			Set<Constraint> constraintsNotInA) {
//		
//		return new AggregationParfactor(name, constraintsInA, parent, child, factor, operator, constraintsNotInA);
//	}
//	
//	public static AggregationParfactor getInstanceWithoutConstraints(
//			ParameterizedRandomVariable parent,
//			ParameterizedRandomVariable child,
//			ParameterizedFactor factor,
//			Operator operator) {
//
//		return new AggregationParfactor("", new HashSet<Constraint>(), parent, child, factor, operator, new HashSet<Constraint>());
//	}
//	
//	
//	public boolean contains(ParameterizedRandomVariable variable) {
//		return variable.equals(parent) || variable.equals(child);
//	}
//	
//	public ParameterizedFactor getFactor() {
//		return this.parentFactor;
//	}
//	
//	@Override
//	public List<ParameterizedRandomVariable> getParameterizedRandomVariables() {
//		return this.parentFactor.getParameterizedRandomVariables();
//	}
//	
//	@Override
//	public ParameterizedRandomVariable getChildVariable() {
//		return this.child;
//	}
//
//	@Override
//	public Set<Constraint> getConstraints() {
//		return this.constraintsInA; // TODO correct this
//	}
//	
//	
//	/*
//	 ***************************************************************************
//	 *
//	 * OPERATIONS
//	 * 
//	 ***************************************************************************
//	 */
//	
//	/**
//	 * Multiplies the aggregation parfactor by specified parfactor.
//	 * <br>
//	 * Let g<sub>A</sub> = 
//	 * &lang; C, p(...,A,...), c(...), F<sub>p</sub>, &otimes;, C<sub>A</sub> &rang; 
//	 * be an aggregation parfactor from &Phi; and g<sub>1</sub> = 
//	 * &lang; C &cup; C<sub>A</sub>, {p(...,A,...)}, F<sub>1</sub> &rang; 
//	 * be a parfactor from &Phi;. 
//	 * <br>
//	 * Then g<sub>A</sub> x g<sub>1</sub> =  
//	 * &lang; C, p(...,A,...), c(...), F<sub>p</sub> x F<sub>1</sub>, 
//	 * &otimes;, C<sub>A</sub> &rang;.
//	 * <br>
//	 * <br>
//	 * The specified parfactor must follow some rules:
//	 * <li> The set of constraints must be the union of the set of constraints
//	 * on A and the set of constraints not in A
//	 * <li> The only parameterized random variable must be equal to the parent 
//	 * variable in the aggregation parfactor
//	 * 
//	 * @param parfactor The parfactor to multiply for.
//	 * @return The product of the aggregation parfactor and the specified
//	 * parfactor.
//	 * @throws IllegalArgumentException If the parfactor provided does not
//	 * obey the constraints listed above.
//	 */
//	public Set<SimpleParfactor> multiply(Set<SimpleParfactor> setOfParfactors, SimpleParfactor parfactor) 
//			throws IllegalArgumentException {
//		
//		if (this.canBeMultipliedBy(parfactor)) {
//			
//			setOfParfactors.remove(parfactor);
//			setOfParfactors.add(
//					getInstance(this.constraintsInA,
//								this.parent,
//								this.child,
//								this.parentFactor.multiply(parfactor.getFactor()),
//								this.operator,
//								this.constraintsNotInA));
//			
//			return setOfParfactors;
//			
//		} else {
//			throw new IllegalArgumentException("Cannot multiply because " +
//					"pre-requisites are not met. " + this.toString() + "\n\n" + parfactor.toString());
//		}
//	}
//	
//	public boolean canBeMultipliedBy(SimpleParfactor parfactor) {
//		if (!(parfactor instanceof SimpleParfactor)) {
//			return false;
//		}
//		SimpleParfactor p = (SimpleParfactor) parfactor;
//		
//		return Sets.union(this.constraintsInA, 
//				   		  this.constraintsNotInA)
//				   .equals(new HashSet<Constraint>(p.getConstraints())) &&
//			   p.getParameterizedRandomVariables().size() == 1 &&
//			   p.getParameterizedRandomVariables().get(0).equals(this.parent);
//	}
//
//	
//	
//	public Set<SimpleParfactor> sumOut(Set<SimpleParfactor> setOfParfactors, ParameterizedRandomVariable variable) {
//		
//		/*
//		 * INPUT: set of parfactors and aggregation parfactors Phi
//		 *        aggregation parfactor gA from Phi
//		 * OUTPUT: Phi \ {gA} U <C, {c}, Fm>
//		 * 
//		 * if conditions to sum out are met
//		 *     m := floor( log2( |D(A):CA| ) )
//		 *     bm....b0 := binary representation of |D(A):CA|
//		 *     Fm := calculateF(m)
//		 *     g := <C, {c}, Fm>
//		 *     return Phi \ {gA} U g
//		 * 
//		 * procedure calculateF(String x, int k, bit[] binRepresentation) 
//		 *     if k = 0
//		 *         if x in range(p)
//		 *             return Fp(x)
//		 *         else
//		 *             return 0
//		 *     else
//		 *         sum := 0 
//		 *         if b_(m-k) = 0
//		 *         	   for each y in range(c)
//		 *                 for each z in range(c)
//		 *                     if (y OPERATOR z == x)
//		 *                         sum += calculateF(y, k-1, binRep) * calculateF(z, k-1, binRep)  
//		 *             return sum
//		 *         else
//		 *             for each w in range(c)
//		 *                 for each y in range(c)
//		 *                     for each z in range(c)
//		 *                         if (w OPERATOR y OPERATOR z == x)
//		 *                             sum += Fp(w) * calculateF(y, k-1, binRep) * calculateF(z, k-1, binRep)  
//		 *             return sum
//		 * 
//		 */
//			
//		if (conditionsToSumOutAreMet(setOfParfactors)) {
//			Integer[] sizeOfDomainAsBinary = getBinaryRepresentationOf(extraVariableInParent.getIndividualsSatisfying(constraintsInA).size());
//			
//			ArrayList<ParameterizedRandomVariable> variables = new ArrayList<ParameterizedRandomVariable>();
//			variables.add(child);
//			
//			ArrayList<Number> mapping = new ArrayList<Number>();
//			for (int i = 0; i < child.getRangeSize(); i++) {
//				mapping.add(getFactorValue(child.getElementFromRange(i), 
//										   sizeOfDomainAsBinary.length - 1, 
//										   sizeOfDomainAsBinary));
//			}
//			
//			setOfParfactors.remove(this);
//			setOfParfactors.add(
//					SimpleParfactor.getInstance(constraintsNotInA, 
//							              ParameterizedFactor.getInstance(this.name, 
//							            		                          variables, 
//							            		                          mapping)));
//			
//		}
//		
//		return setOfParfactors;
//	}
//	
//	//TODO: implement this
//	private boolean conditionsToSumOutAreMet(Set<SimpleParfactor> setOfParfactors) {
//		// checks if C U CA is in normal form
//		
//		// checks if param(c) = param(p) \ A
//
//		// checks if that no other parfactor or aggregation parfactor in Phi 
//		// involves parameterized random variables that represent random 
//		// variables from ground(p(...,A,...)).
//		return true;
//	}
//	
//	/**
//	 * Returns the binary representation of a positive integer as an array
//	 * of Integers.
//	 * @param number The number to be converted
//	 * @return The binary representation of a positive integer
//	 */
//	private Integer[] getBinaryRepresentationOf(int number) { //TODO: rewrite this ugly thing
//		ArrayList<Integer> binaryRepresentation = new ArrayList<Integer>();
//		binaryRepresentation.add(number % 2);
//		number = number / 2;
//		while (number != 0) {
//			binaryRepresentation.add(number % 2);
//			number = number / 2;
//		}
//		Collections.reverse(binaryRepresentation);
//		return binaryRepresentation.toArray(new Integer[binaryRepresentation.size()]);
//	}
//	
//	/**
//	 * Reference: [Kisynski, 2010], page 89.
//	 * <br>
//	 * Calculates the resulting factor value when summing out the aggregation
//	 * parfactor.
//	 * <br>
//	 * TODO: this method is recursive. Make it non-recursive. 
//	 * @param x A value in range(c)
//	 * @param k The index of the factor to calculate
//	 * @param binaryRepresentation The binary representation of |D(A):C<sub>A</sub>|
//	 * @return The value of F<sub>k</sub>(x).
//	 */
//	private Number getFactorValue(String x, int k, Integer[] binaryRepresentation) { //TODO: rewrite this ugly thing TODO: cache results
//		if (k == 0) {
//			for (int i = 0; i < parent.getRangeSize(); i++) {
//				if (x.equals(parent.getElementFromRange(i))) {
//					return parentFactor.getTupleValue(i);
//				}
//			}
//			return 0;
//		} else {
//			Double sum = Double.valueOf("0");
//			if (binaryRepresentation[binaryRepresentation.length - k - 1] == 0) {
//				for (int y = 0; y < child.getRangeSize(); y++) {
//					for (int z = 0; z < child.getRangeSize(); z++) {
//						if (operator.applyOn(child.getElementFromRange(y), child.getElementFromRange(z)).equals(x)) {
//							double fy = getFactorValue(child.getElementFromRange(y), k - 1, binaryRepresentation).doubleValue();
//							double fz = getFactorValue(child.getElementFromRange(z), k - 1, binaryRepresentation).doubleValue(); 
//							sum +=  fy * fz; 
//								   
//						}
//					}
//				}
//				return sum;
//			} else {
//				for (int w = 0; w < child.getRangeSize(); w++) {
//					for (int y = 0; y < child.getRangeSize(); y++) {
//						for (int z = 0; z < child.getRangeSize(); z++) {
//							if (operator.applyOn(child.getElementFromRange(w), child.getElementFromRange(y), child.getElementFromRange(z)).equals(x)) {
//								sum += parentFactor.getTupleValue(w) *
//									   getFactorValue(child.getElementFromRange(y), k - 1, binaryRepresentation).doubleValue() *
//									   getFactorValue(child.getElementFromRange(z), k - 1, binaryRepresentation).doubleValue();
//							}
//						}
//					}
//				}
//				return sum;
//				
//			}
//		}
//	}
//	
//	@Override
//	public String toString() {
//		return "\n<\n" + 
//			   constraintsNotInA.toString() + ",\n" +
//			   parent.toString() + ",\n" +
//			   child.toString() + ",\n" +
//			   parentFactor.toString() +
//			   operator.toString() + ",\n" +
//			   constraintsInA.toString() + ",\n" +
//			   ">\n";
//	}
//
//	
//}
