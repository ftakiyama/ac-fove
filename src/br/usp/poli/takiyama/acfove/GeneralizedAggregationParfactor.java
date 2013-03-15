package br.usp.poli.takiyama.acfove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import br.usp.poli.takiyama.acfove.AggregationParfactor.Builder;
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
	
	/**
	 * Applies the specified substitution to this aggregation parfactor.
	 * <br>
	 * The substitution is applied on the set of constraints (both), the
	 * parent PRV, the child PRV, the context variables and the factor.
	 * @param s The substitution to apply
	 * @return This parfactor with the specified substitution applied.
	 */
	private GeneralizedAggregationParfactor applySubstitution(Binding s) {
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
		
		List<ParameterizedRandomVariable> cv = new ArrayList<ParameterizedRandomVariable>(contextVariables.size());
		for (ParameterizedRandomVariable prv : contextVariables) {
			cv.add(prv.applyOneSubstitution(s));
		}
		
		ParameterizedRandomVariable p = parent.applyOneSubstitution(s);
		ParameterizedRandomVariable c = child.applyOneSubstitution(s);
		ParameterizedFactor f = factor.applySubstitution(s);
		
		Builder builder = new Builder(p, c, this.operator, cv);
		builder.addConstraints(allConstraints);
		builder.factor(f);
		
		return builder.build();
	}

	/* ************************************************************************
	 * 
	 *   Enabling methods
	 * 
	 * ***********************************************************************/
	
	
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
	 * using the method {@link GeneralizedAggregationParfactor#canBeSplit(Binding)}.
	 * <br>
	 * It is not recommended to call this method directly. 
	 * {@link GeneralizedAggregationParfactor#split(Binding)} should be used instead.
	 * <br>
	 * <br>
	 * Splitting g = &lang; C, p, c, V, Fpv, &otimes;, C<sub>A</sub> &rang;
	 * on substitution {X/t} (given that it meets conditions above) results in 
	 * two parfactors:
	 * <li> g[X/t], that is, g with all occurrences of X replaced with t 
	 * <li> &lang; C U {X &ne; t}, p, c, V, Fpv, &otimes;, C<sub>A</sub> &rang;,
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
	 * 
	 * @param c The constraint to add.
	 * @return This parfactor with the specified constraint added. A new
	 * instance is created.
	 */
	private GeneralizedAggregationParfactor addConstraint(Constraint c) {
		Builder builder = new Builder(this.parent, this.child, this.operator, this.contextVariables);
		builder.addConstraints(this.constraintsOnExtraVariable);
		builder.addConstraints(this.otherConstraints);
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
	 * using the method {@link GeneralizedAggregationParfactor#canBeSplit(Binding)}.
	 * <br>
	 * It is not recommended to call this method directly. 
	 * {@link GeneralizedAggregationParfactor#split(Binding)} should be used instead.
	 * <br>
	 * <br>
	 * Splitting g = &lang; C, p, c, V, Fp, &otimes;, C<sub>A</sub> &rang;
	 * on substitution {A/t} (which mets conditions above) results in two
	 * parfactors:
	 * <li> &lang; C, p, c', V, Fp, &otimes;, C<sub>A</sub> U {A &ne; t} &rang;
	 * <li> &lang; C U C<sub>A</sub>[A/t], {p[A/t], V[A/t], c', c} , Fc &rang;
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
		
		Builder builder = new Builder(this.parent, cAux, this.operator, this.contextVariables);
		builder.addConstraints(this.constraintsOnExtraVariable);
		builder.addConstraints(this.otherConstraints);
		builder.addConstraint(constraintFromBinding);
		builder.factor(this.factor);
		GeneralizedAggregationParfactor residue = builder.build();
		
		// Builds the simple parfactor
		// It is relatively simple, but the code below doesn't reflect that
		// TODO make it simpler
		
		Set<Constraint> constraints = new HashSet<Constraint>(this.otherConstraints);
		for (Constraint c : this.constraintsOnExtraVariable) {
			Constraint nc = c.applySubstitution(s);
			if (nc != null) {
				constraints.add(nc);
			}
		}
		
		List<ParameterizedRandomVariable> prvs = 
				new ArrayList<ParameterizedRandomVariable>(3 + contextVariables.size());
		prvs.add(this.parent.applyOneSubstitution(s));
		for (ParameterizedRandomVariable prv : contextVariables) {
			ParameterizedRandomVariable sprv = prv.applyOneSubstitution(s);
			prvs.add(sprv);
		}
		prvs.add(cAux);
		prvs.add(this.child);

		List<Number> mapping = new ArrayList<Number>();
		Iterator<Tuple> it = ParameterizedFactor.getIteratorOverTuples(prvs);
		while (it.hasNext()) {
			Tuple currentTuple = it.next();
			
			int rangeIndexOfP = currentTuple.get(0);
			int rangeIndexOfCAux = currentTuple.get(prvs.size() - 2);
			int rangeIndexOfC = currentTuple.get(prvs.size() - 1);
			
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
	 * using the method {@link GeneralizedAggregationParfactor#canBeSplit(Binding)}.
	 * <br>
	 * It is not recommended to call this method directly. 
	 * {@link GeneralizedAggregationParfactor#split(Binding)} should be used instead.
	 * <br>
	 * <br>
	 * Splitting g = &lang; C, p, c, V, Fp, &otimes;, C<sub>A</sub> &rang;
	 * on substitution {X/A} (given that it meets conditions above) results in two
	 * parfactors:
	 * <li> &lang; C, p, c', V, Fp, &otimes;, C<sub>A</sub> U {A &ne; t} &rang;
	 * <li> &lang; C[A/t] U C<sub>A</sub>, {p[X/A], V[X/A], c', c} , Fc &rang;
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
		 */
		
		// Builds the aggregation parfactor
		
		Constraint constraintFromBinding = Constraint.getInequalityConstraintFromBinding(s);
		ParameterizedRandomVariable cAux = this.child.rename(this.child.getName() + "'");
		
		Builder builder = new Builder(this.parent, cAux, this.operator, this.contextVariables);
		builder.addConstraints(this.constraintsOnExtraVariable);
		builder.addConstraints(this.otherConstraints);
		builder.addConstraint(constraintFromBinding);
		builder.factor(this.factor);
		GeneralizedAggregationParfactor residue = builder.build();
		
		// Builds the simple parfactor
		
		Set<Constraint> constraints = new HashSet<Constraint>(this.constraintsOnExtraVariable);
		for (Constraint c : this.otherConstraints) {
			Constraint nc = c.applySubstitution(s);
			if (nc != null) {
				constraints.add(nc);
			}
		}
		
		List<ParameterizedRandomVariable> prvs = 
			new ArrayList<ParameterizedRandomVariable>(3 + contextVariables.size());
		prvs.add(this.parent.applyOneSubstitution(s));
		for (ParameterizedRandomVariable prv : contextVariables) {
			ParameterizedRandomVariable sprv = prv.applyOneSubstitution(s);
			prvs.add(sprv);
		}
		prvs.add(cAux);
		prvs.add(this.child);

		List<Number> mapping = new ArrayList<Number>();
		Iterator<Tuple> it = ParameterizedFactor.getIteratorOverTuples(prvs);
		while (it.hasNext()) {
			Tuple currentTuple = it.next();
			
			int rangeIndexOfP = currentTuple.get(0);
			int rangeIndexOfCAux = currentTuple.get(prvs.size() - 2);
			int rangeIndexOfC = currentTuple.get(prvs.size() - 1);
			
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
	 * <li> The specified parfactor has only the parent PRV and context PRVs
	 * <li> The constraints of the specified parfactor are equal to the union
	 * of all constraints in this aggregation parfactor.
	 * 
	 * @param p The candidate parfactor to multiply this one
	 * @return True if this parfactor can be multiplied by the specified
	 * parfactor, false otherwise.
	 */
	public boolean canMultiply(Parfactor p) {
		if (p instanceof GeneralizedAggregationParfactor
				|| p instanceof AggregationParfactor) {
			return false;
		}
		
		boolean compatibleConstraints = p.getConstraints().equals(this.getConstraints());
		
		List<ParameterizedRandomVariable> prvs = p.getParameterizedRandomVariables();
		List<ParameterizedRandomVariable> thisPrvs = factor.getParameterizedRandomVariables();
		boolean parfactorOnParentAndContext = (prvs.size() == thisPrvs.size()) 
												&& prvs.containsAll(thisPrvs);
		
		return compatibleConstraints && parfactorOnParentAndContext;
	}
	
	/**
	 * Sets the factor in this aggregation parfactor.
	 * @param f The new parameterized factor
	 * @return This aggregation parfactor with the specified parameterized
	 * factor.
	 */
	private GeneralizedAggregationParfactor setFactor(ParameterizedFactor f) {
		Builder builder = new Builder(this);
		builder.factor(f);
		return builder.build();
	}
	
	
	/* ************************************************************************
	 *    Sum out
	 * ************************************************************************/
	
	@Override
	public Parfactor sumOut(ParameterizedRandomVariable prv) {
		
		ParameterizedFactor f = calculateSumOutFactor();
		Parfactor result;
		if (childHasExtra()) {
			result = countChildVariable(f);
		} else {
			Set<Constraint> c = new HashSet<Constraint>(this.otherConstraints);
			result = SimpleParfactor.getInstance(c, f);
		}
		
		return result;
	}
	
	
	private ParameterizedFactor calculateSumOutFactor() {
		
		List<ParameterizedRandomVariable> prvs = 
				new ArrayList<ParameterizedRandomVariable>(contextVariables);
		prvs.add(0, child);
		Iterator<Tuple> it = ParameterizedFactor.getIteratorOverTuples(prvs);
		
		ParameterizedFactor currentFactor = calculateSumOutBase(it, prvs);
		
		int domainSize = this.extraVariable.getSizeOfPopulationSatisfying(constraintsOnExtraVariable);
		String bin = Integer.toBinaryString(domainSize);
		
		for (int k = 1; k < bin.length(); k++) {
			it = ParameterizedFactor.getIteratorOverTuples(prvs);
			if (bin.charAt(k) == '0') {
				currentFactor = calculateSumOutFk0(it, currentFactor);
			} else {
				currentFactor = calculateSumOutFk1(it, currentFactor);
			}
		}
		
		return currentFactor;
	}
	
	private ParameterizedFactor calculateSumOutBase(Iterator<Tuple> it, List<ParameterizedRandomVariable> prvs) {

		List<Number> base = new ArrayList<Number>();

		while (it.hasNext()) {
			Tuple t = it.next();
			String childRangeValue = child.getElementFromRange(t.get(0));
			if (this.parent.getRange().contains(childRangeValue)) {
				int pindex = parent.getRange().indexOf(childRangeValue);
				Tuple t1 = t.getModifiedTuple(0, pindex);
				base.add(factor.getValue(t1));
			} else {
				base.add(0.0);
			}
		}
		
		return ParameterizedFactor.getInstance("", prvs, base);
	}
	
	private ParameterizedFactor calculateSumOutFk0(Iterator<Tuple> it, ParameterizedFactor previousFactor) {
		
		List<Number> fValues = new ArrayList<Number>();
		
		while (it.hasNext()) {
			Tuple t = it.next();
			String x = child.getElementFromRange(t.get(0));
			Boolean x1 = Boolean.valueOf(x);
			double sum = 0.0;
			for (String y : this.child.getRange()) {
				Boolean y1 = Boolean.valueOf(y);
				int yindex = this.child.getRange().indexOf(y);
				for (String z : this.child.getRange()) {
					Boolean z1 = Boolean.valueOf(z);
					int zindex = this.child.getRange().indexOf(z);
					if (this.operator.applyOn(y1, z1) == x1) {
						Tuple ty = t.getModifiedTuple(0, yindex);
						Tuple tz = t.getModifiedTuple(0, zindex);
						sum = sum 
							+ previousFactor.getValue(ty)
								* previousFactor.getValue(tz);
					}
				}
			}
			fValues.add(sum);
		}
		
		List<ParameterizedRandomVariable> prvs = previousFactor.getParameterizedRandomVariables();
		return ParameterizedFactor.getInstance("", prvs, fValues);
	}
	
	private ParameterizedFactor calculateSumOutFk1(Iterator<Tuple> it, ParameterizedFactor previousFactor) {
		List<Number> fValues = new ArrayList<Number>();
		
		while (it.hasNext()) {
			Tuple t = it.next();
			String x = child.getElementFromRange(t.get(0));
			Boolean x1 = Boolean.valueOf(x);
			double sum = 0.0;
			for (String y : this.child.getRange()) {
				Boolean y1 = Boolean.valueOf(y);
				int yindex = this.child.getRange().indexOf(y);
				for (String z : this.child.getRange()) {
					Boolean z1 = Boolean.valueOf(z);
					int zindex = this.child.getRange().indexOf(z);
					for (String w : this.child.getRange()) {
						Boolean w1 = Boolean.valueOf(w);
						int windex = this.parent.getRange().indexOf(w);
						if (this.operator.applyOn(w1, y1, z1) == x1) {
							Tuple tw = t.getModifiedTuple(0, windex);
							Tuple ty = t.getModifiedTuple(0, yindex);
							Tuple tz = t.getModifiedTuple(0, zindex);
							sum = sum 
								+ factor.getValue(tw)
									* previousFactor.getValue(ty)
									* previousFactor.getValue(tz);
						}
					}
				}
			}
			fValues.add(sum);
		}
		
		List<ParameterizedRandomVariable> prvs = previousFactor.getParameterizedRandomVariables();
		return ParameterizedFactor.getInstance("", prvs, fValues);
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
		result = prime
				* result
				+ ((contextVariables == null) ? 0 : contextVariables.hashCode());
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GeneralizedAggregationParfactor)) {
			return false;
		}
		GeneralizedAggregationParfactor other = (GeneralizedAggregationParfactor) obj;
		if (child == null) {
			if (other.child != null) {
				return false;
			}
		} else if (!child.equals(other.child)) {
			return false;
		}
		if (constraintsOnExtraVariable == null) {
			if (other.constraintsOnExtraVariable != null) {
				return false;
			}
		} else if (!constraintsOnExtraVariable
				.equals(other.constraintsOnExtraVariable)) {
			return false;
		}
		if (contextVariables == null) {
			if (other.contextVariables != null) {
				return false;
			}
		} else if (!contextVariables.equals(other.contextVariables)) {
			return false;
		}
		if (extraVariable == null) {
			if (other.extraVariable != null) {
				return false;
			}
		} else if (!extraVariable.equals(other.extraVariable)) {
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
		if (otherConstraints == null) {
			if (other.otherConstraints != null) {
				return false;
			}
		} else if (!otherConstraints.equals(other.otherConstraints)) {
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "p = " + parent 
				+ ", c = " + child
				+ ", V = " + contextVariables
				+ ", C_A = " + constraintsOnExtraVariable
				+ ", C = " + otherConstraints 
				+ "\n" + factor;
	}
}
