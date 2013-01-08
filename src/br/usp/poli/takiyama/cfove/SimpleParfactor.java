package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.usp.poli.takiyama.common.Constraint;
//import br.usp.poli.takiyama.common.Constraints;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.Parfactors;
import br.usp.poli.takiyama.common.RandomVariable;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.LogicalVariableNameGenerator;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Population;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Sets;

/**
 * Parfactors, also known as Parametric Factors, represent the joint distribution 
 * of a set of parameterized random variables.
 * @author ftakiyama
 *
 */
public final class SimpleParfactor implements Parfactor {
	
	private final HashSet<Constraint> constraints;
	//private final ArrayList<ParameterizedRandomVariable> variables;
	private final ParameterizedFactor factor;
	
	// this class should be able to return factors.
	
	/**
	 * Constructor.
	 * @param constraints A set of inequality constraints on logical variables
	 * @param variables A set of parameterized random variables
	 * @param factor A factor from the Cartesian product of ranges of 
	 * parameterized random variables in <code>variables</code> to the reals.
	 * @throws IllegalArgumentException
	 */
	private SimpleParfactor(
			Set<Constraint> constraints, 
			//List<ParameterizedRandomVariable> variables, 
			ParameterizedFactor factor) 
			throws IllegalArgumentException {
		
		this.constraints = new HashSet<Constraint>(constraints);
		//this.variables = new ArrayList<ParameterizedRandomVariable>(variables);
		this.factor = factor;
		
		// check if the factor is consistent with the list of variables
		
		// check if the factor uses counting formulas; if so, check if it is in normal form
		
//		if (false) {
//			throw new IllegalArgumentException("Constraints not in normal form.");
//		}
	}
	

	/**
	 * @deprecated
	 * Returns an instance of Parfactor.
	 * @param constraints A list of constraints
	 * @param factor A factor on parameterized random variables
	 * @return The parfactor corresponding to arguments specified.
	 * @throws IllegalArgumentException If the counting formulas in the factor
	 * are not in normal form.
	 */
	public static SimpleParfactor getInstance(
			List<Constraint> constraints, 
			//List<ParameterizedRandomVariable> variables, 
			ParameterizedFactor factor) 
			throws IllegalArgumentException {
		return new SimpleParfactor(new HashSet<Constraint>(constraints), factor);
	}
	
	/**
	 * Returns an instance of Parfactor.
	 * @param constraints A set of constraints
	 * @param factor A factor on parameterized random variables
	 * @return The parfactor corresponding to arguments specified.
	 * @throws IllegalArgumentException If the counting formulas in the factor
	 * are not in normal form.
	 */
	public static SimpleParfactor getInstance(
			Set<Constraint> constraints, 
			ParameterizedFactor factor) 
			throws IllegalArgumentException {
		return new SimpleParfactor(constraints, factor);
	}
	
	/**
	 * @deprecated
	 * For tests purposes.
	 * Returns a factor without constraints.
	 * @param factor
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SimpleParfactor getInstanceWithoutConstraints(ParameterizedFactor factor) 
			throws IllegalArgumentException {
		
		return new SimpleParfactor(new HashSet<Constraint>(), 
							 factor);
	}
	
	/**
	 * Returns a constant parfactor, the neutral factor for multiplications. 
	 * That is, any parfactor multiplied by the parfactor returned by this 
	 * method does not change.
	 * <br>
	 * The constant parfactor contains only one value, 1. There are no
	 * parameterized random variables or constraints in this parfactor. 
	 *  
	 * @return The constant parfactor.
	 */
	public static SimpleParfactor getConstantInstance() {
		
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(Double.valueOf("1.0"));
		
		return new SimpleParfactor(new HashSet<Constraint>(), 
				 			 ParameterizedFactor.getInstance(
				 					 "1", 
				 					 new ArrayList<ParameterizedRandomVariable>(), 
				 					 mapping));
	}
	
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables() {
		return this.factor.getParameterizedRandomVariables();
	}
	
	public Set<Constraint> getConstraints() {
		return new HashSet<Constraint>(this.constraints);
	}
	
	
	public ParameterizedFactor getFactor() {
		return this.factor;
	}
	
	/**
	 * Returns the number of factors this parfactor represents.
	 * @return The number of factors this parfactor represents.
	 */
	public int size() {
		// TODO: check constraints
		
		/*
		 * size := 1
		 * for each PRV prv in variables
		 *     for each parameter p in prv
		 *         if p has not been read
		 *             size := size * p.population
		 *             mark p as read
		 * return size
		 */
		
		int size = 1;
		HashSet<LogicalVariable> read = new HashSet<LogicalVariable>(); // is it the best thing to do? 
		for (ParameterizedRandomVariable v : factor.getParameterizedRandomVariables()) {
			for (LogicalVariable lv : v.getParameters()) {
				if (!read.contains(lv)) {
					size = size * lv.getPopulation().size();
					read.add(lv);
				}
			}
		}
		
		if (read.isEmpty()) 
			size = 0;
		
		return size;
	}
	
	public List<LogicalVariable> getLogicalVariables() {
		HashSet<LogicalVariable> logicalVariables = new HashSet<LogicalVariable>();
		for (ParameterizedRandomVariable prv : factor.getParameterizedRandomVariables()) {
			logicalVariables.addAll(prv.getParameters());
		}
		return new ArrayList<LogicalVariable>(logicalVariables);
	}

	@Override
	public boolean contains(ParameterizedRandomVariable variable) {
		return this.factor.getParameterizedRandomVariables().contains(variable);
	}

	@Override
	public ParameterizedRandomVariable getChildVariable() {
		return null;
	}
	
	@Override
	public boolean isConstant() {
		return (this.constraints.isEmpty()
				&& this.factor.getParameterizedRandomVariables().isEmpty()
				&& this.factor.getAllValues().size() == 1
				&& this.factor.getAllValues().get(0).doubleValue() == 1);
	}
	
	/*
	 ***************************************************************************
	 *
	 * OPERATIONS
	 * 
	 ***************************************************************************
	 */
	
	
	public Set<SimpleParfactor> sumOut(
			Set<SimpleParfactor> setOfParfactors, 
			ParameterizedRandomVariable variable) {
		
		HashSet<SimpleParfactor> parfactors = new HashSet<SimpleParfactor>(setOfParfactors);
		
		if (allPreConditionsForLiftedEliminationAreOk(setOfParfactors, variable)
				&& checkFirstConditionForLiftedElimination(setOfParfactors, variable)
				&& checkSecondConditionForLiftedElimination(variable)) {
				
			ParameterizedFactor newFactor = getFactor().sumOut(variable);
			
			parfactors.remove(this);
			
			SimpleParfactor newParfactor = SimpleParfactor.getInstance(
					getConstraints(), 
					newFactor);
			
			double size1 = (double) this.size();
			double size2 = (double) newParfactor.size();
			double exponent = size1 / size2;
			
			newParfactor = SimpleParfactor.getInstance(
					getConstraints(), 
					newFactor.pow(exponent));
			
			parfactors.add(newParfactor);
			
			return parfactors; 
		} 
		
		return parfactors;
		
	}
	
	
	private boolean allPreConditionsForLiftedEliminationAreOk(
			Set<SimpleParfactor> setOfParfactors, 
			ParameterizedRandomVariable variable) {
		
		/*
		 * TODO: check if parfactor is in normal form
		 * TODO: check if variable is not a counting formula  
		 */
		
		return setOfParfactors.contains(this);
	}
	
	/**
	 * Returns true if no other parfactor in <code>setOfParfactors</code> 
	 * includes parameterized random variables that represent random variables 
	 * represented by <code>variable</code>.
	 * @param setOfParfactors A set of parfactors
	 * @param parfactor A normal form parfactor from <code>setOfParfactors</code>
	 * @param variable A parameterized random variable from <code>parfactor</code> 
	 * @return True if no other parfactor in <code>setOfParfactors</code> 
	 * includes parameterized random variables that represent random variables 
	 * represented by <code>variable</code>, false otherwise.
	 */
	private boolean checkFirstConditionForLiftedElimination(
			Set<SimpleParfactor> setOfParfactors, 
			ParameterizedRandomVariable variable) {
		
		Set<SimpleParfactor> setOfParfactorsWithoutTarget = 
				new HashSet<SimpleParfactor>(setOfParfactors);
		
		setOfParfactorsWithoutTarget.remove(this);
		
		for (SimpleParfactor currentParfactor : setOfParfactorsWithoutTarget) {
			
			Set<RandomVariable> targetGroundInstances = variable
				.getGroundInstancesSatisfying(getConstraints());
			
			for (ParameterizedRandomVariable prv : currentParfactor.getParameterizedRandomVariables()) {
				
				Set<RandomVariable> currentGroundInstances = prv
					.getGroundInstancesSatisfying(currentParfactor
						.getConstraints());
				
				currentGroundInstances.retainAll(targetGroundInstances);
				
				if (!currentGroundInstances.isEmpty()) return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the set of logical variables in <code>variable</code> is a 
	 * superset of the union of logical variables in other parameterized 
	 * random variables from <code>parfactor</code>.
	 * @param parfactor The parfactor to check
	 * @param variable The variable to check
	 * @return True if the set of logical variables in <code>variable</code> is a 
	 * superset of the union of logical variables in other parameterized 
	 * random variables from <code>parfactor</code>, false otherwise.
	 */
	private boolean checkSecondConditionForLiftedElimination(
			ParameterizedRandomVariable variable) {
		
		ArrayList<ParameterizedRandomVariable> setOfParameterizedRandomVariablesWithoutTarget = 
			new ArrayList<ParameterizedRandomVariable>(getParameterizedRandomVariables());
	
		setOfParameterizedRandomVariablesWithoutTarget.remove(variable);	
		
		HashSet<LogicalVariable> allLogicalVariables = new HashSet<LogicalVariable>();
		
		for (ParameterizedRandomVariable prv : setOfParameterizedRandomVariablesWithoutTarget) {
			allLogicalVariables = new HashSet<LogicalVariable>(
					Sets.union(prv.getParameters(), allLogicalVariables)); // not good
		}
		
		return variable.getParameters().containsAll(allLogicalVariables);		
	}
	
	
	/* ************************************************************************
	 *    MULTIPLICATION
	 * ************************************************************************/
	
	public Set<SimpleParfactor> multiply(
			Set<SimpleParfactor> setOfParfactors, 
			SimpleParfactor parfactor) {
		/*
		 * if conditions are met
		 *     calculate g = <Ci U Cj, Vi U Vj, Fi x Fj>
		 *     ri := |gi| / |g|
		 *     rj := |gj| / |g|
		 *     remove gi and gj from set of parfactors
		 *     calculate g' = <Ci U Cj, Vi U Vj, Fi^ri x Fj^rj>
		 *     insert g' in the set of parfactors
		 * return set of parfactors    
		 */
		
		HashSet<SimpleParfactor> newSetOfParfactors = new HashSet<SimpleParfactor>(setOfParfactors);
		if (this.canBeMultipliedBy(parfactor)) {
			SimpleParfactor p = (SimpleParfactor) parfactor;
			Set<Constraint> union = p.getConstraints();
			union.addAll(this.getConstraints());
			SimpleParfactor g = getInstance(
					union,
					this.getFactor().multiply(parfactor.getFactor()));
			
			double firstExponent = ((double) this.size()) / g.size();
			double secondExponent = ((double) p.size()) / g.size();
			
			newSetOfParfactors.remove(this);
			newSetOfParfactors.remove(p);
			
			SimpleParfactor product = SimpleParfactor.getInstance(
					union,
					this.getFactor().pow(firstExponent).multiply(p.getFactor().pow(secondExponent)));
			
			newSetOfParfactors.add(product);
		}
		return newSetOfParfactors;
	}
	
	// TODO: check when parfactor is an agg parfactor...
	public boolean canBeMultipliedBy(SimpleParfactor parfactor) {
		if (!(parfactor instanceof SimpleParfactor)) {
			return parfactor.canBeMultipliedBy(this);
		}
		SimpleParfactor p = (SimpleParfactor) parfactor;
		
		return checkFirstConditionForMultiplication(p) &&
			   checkSecondConditionForMultiplication(p);
	}
	
	private boolean checkFirstConditionForMultiplication(
			SimpleParfactor parfactor) {

		/* First condition: sets of random variables represented by
		 * parameterized random variables from each parfactor are identical 
		 * or disjoint 
		 */
		for (ParameterizedRandomVariable v1 : this.getParameterizedRandomVariables()) {
			for (ParameterizedRandomVariable v2 : parfactor.getParameterizedRandomVariables()) {
				
				Set<RandomVariable> groundInstancesFromThis = v1.getGroundInstancesSatisfying(this.getConstraints());
				Set<RandomVariable> groundInstancesFromOther = v2.getGroundInstancesSatisfying(parfactor.getConstraints());
				
				// intersection
				groundInstancesFromThis.retainAll(groundInstancesFromOther);
				
				if (!groundInstancesFromThis.isEmpty() &&
					!v1.getGroundInstancesSatisfying(
							this.getConstraints())
					.equals(
					v2.getGroundInstancesSatisfying(
							parfactor.getConstraints()))) {
					
					return false; // this is horrible and unreadable
				}
			}
		}
		
		return true;
	}
	
	private boolean checkSecondConditionForMultiplication(
			SimpleParfactor parfactor) {
		
		ArrayList<LogicalVariable> logicalVariablesFromFirstParfactor = 
			new ArrayList<LogicalVariable>(this.getLogicalVariables());
		
		for (ParameterizedRandomVariable v1 : this.getParameterizedRandomVariables()) {
			for (ParameterizedRandomVariable v2 : parfactor.getParameterizedRandomVariables()) {
				if (v1.getGroundInstancesSatisfying(
							this.getConstraints())
					.equals(
					v2.getGroundInstancesSatisfying(
							parfactor.getConstraints()))) {
					
					logicalVariablesFromFirstParfactor.removeAll(v1.getParameters());
					
					if (!v1.getParameters().equals(v2.getParameters()))
						return false;
				}
			}			
		}
		
		if (Sets.intersection(logicalVariablesFromFirstParfactor,
							  new ArrayList<LogicalVariable>(parfactor.getLogicalVariables()))
				.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	/* ************************************************************************
	 *    Split
	 * ************************************************************************/
	
	
	/**
	 * Let g<sub>i</sub> = &lt; C<sub>i</sub>, V<sub>i</sub>, F<sub>i</sub> &gt; 
	 * be a parfactor and X a logical variable present in it.
	 * Let {X/t} be a substitution such that:
	 * <li> t is term that is not in any constraints of g<sub>i</sub>;
	 * <li> t is a constant such that t &in; D(x) <b>or</b> t is logical
	 * variable present in g<sub>i</sub> such that D(t) = D(X).
	 * <br>
	 * <br>
	 * This method returns the result of splitting g<sub>i</sub> on substitution
	 * {X/t}. It thus returns two parfactors:
	 * <li> g<sub>i</sub>[X/t], the parfactor g<sub>i</sub> after
	 * applying substitution {X/t};
	 * <li> g<sub>i</sub>' = &lt; C<sub>i</sub> U {X &ne; t}, V<sub>i</sub>,
	 * F<sub>i</sub> &gt;, the residual parfactor.
	 * <br>
	 * @param substitution The substitution upon which the parfactor is split.
	 * @return A list of parfactors with two parfactors, as specified above.
	 * The first parfactor is the residual parfactor, and the second is the
	 * result from the split. If the conditions for splitting are not met, 
	 * returns an EMPTY list.
	 */
	public List<Parfactor> split(Binding substitution) {
		List<Parfactor> result = new ArrayList<Parfactor>();
		if (firstTermIsPresent(substitution) 
				&& secondTermIsNotInConstraints(substitution)
				&& (secondTermIsConstantBelongingToFirstTermPopulation(substitution) 
						|| (secondTermIsPresent(substitution) 
								&& secondTermHasSameDomainOfFirstTerm(substitution)))) {
			result.add(this.addConstraint(Constraint.getInequalityConstraintFromBinding(substitution)));
			result.add(this.applySubstitution(substitution));
		}
		return result;
	}
	
	/**
	 * Checks if the first term of the given substitution belongs to the set
	 * of logical variables of this factor 
	 * @param substitution The substitution from which the first term will
	 * be searched for.
	 * @return True if the first term of the substitution is present in this
	 * parfactor, false otherwise.
	 */
	private boolean firstTermIsPresent(Binding substitution) {		
		return getLogicalVariables().contains(substitution.getFirstTerm());
	}
	
	/**
	 * Checks if the second term of the given substitution is absent from the
	 * set of constraints in this parfactor.
	 * @param substitution The substitution from which the second term will
	 * be searched for.
	 * @return True if the second term of the given substitution is not in 
	 * any constraint from this parfactor, false otherwise.
	 */
	private boolean secondTermIsNotInConstraints(Binding substitution) {
		for (Constraint constraint : this.constraints) {
			if (constraint.contains(substitution.getSecondTerm())) {  
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the second term of the given substitution is a constant
	 * belonging to first term's population.
	 * @param substitution The substitution on which the terms will be checked.
	 * @return True if the second term is a constant belonging to first term's
	 * population, false otherwise.
	 */
	private boolean secondTermIsConstantBelongingToFirstTermPopulation(Binding substitution) {
		if (substitution.getSecondTerm() instanceof LogicalVariable) { // ugly
			return false;
		}
		return substitution
					.getFirstTerm()
					.getPopulation()
					.contains((Constant) substitution.getSecondTerm());
	}
	
	/**
	 * Checks if the second term of the given substitution belongs to the set
	 * of logical variables of this factor 
	 * @param substitution The substitution from which the second term will
	 * be searched for.
	 * @return True if the second term of the substitution is present in this
	 * parfactor, false otherwise.
	 */
	private boolean secondTermIsPresent(Binding substitution) {
		return getLogicalVariables().contains(substitution.getSecondTerm());
	}
	
	/**
	 * Checks if the second term has the same domain of the first term.
	 * @param substitution The substitution on which the terms will be checked.
	 * @return True if the second term has the same domain of the first term,
	 * false otherwise.
	 */
	private boolean secondTermHasSameDomainOfFirstTerm(Binding substitution) {
		return substitution
					.getFirstTerm()
					.getPopulation()
					.equals(
							((LogicalVariable) substitution.getSecondTerm()) // I can cast here because I know it is a logical variable
															  .getPopulation());
	}
	
	/**
	 * Returns this parfactor with an additional constraint.
	 * @param constraint
	 * @return
	 */
	private SimpleParfactor addConstraint(Constraint constraint) {
		HashSet<Constraint> residualConstraints = new HashSet<Constraint>(this.constraints);
		residualConstraints.add(constraint);
		return new SimpleParfactor(residualConstraints, this.factor);
	}
	
	/**
	 * Apply a substitution on this parfactor, replacing the matching occurrences
	 * in all constraints and variables from the factor.
	 * @param substitution The substitution to apply. Note it is not a set,
	 * but one single substitution of the form X/t, where X is a LogicalVariable
	 * and t is either a LogicalVariable or a Constant.
	 * @return A new parfactor that is identical to this one with the
	 * substitution applied.
	 */
	private SimpleParfactor applySubstitution(Binding substitution) {
		HashSet<Constraint> substitutedConstraints = new HashSet<Constraint>();
		for (Constraint constraint : this.constraints) {
			Constraint result = constraint.applySubstitution(substitution);
			if (result != null) { // there must be something more elegant
				substitutedConstraints.add(constraint.applySubstitution(substitution));
			}
		}
		
		ArrayList<ParameterizedRandomVariable> substitutedVariables = new ArrayList<ParameterizedRandomVariable>();
		for (ParameterizedRandomVariable prv : this.factor.getParameterizedRandomVariables()) {
			substitutedVariables.add(prv.applyOneSubstitution(substitution));
		}
		
		// TODO: correct the model: List<Number> is not a super-type of List<Double>
		ArrayList<Number> values = new ArrayList<Number>();
		for (Double d : this.factor.getAllValues()) {
			values.add(d);
		}
		
		return new SimpleParfactor(substitutedConstraints, ParameterizedFactor.getInstance(this.factor.getName(), substitutedVariables, values));
		
	}
	
	/**************************************************************************/
	
	
	/* ************************************************************************
	 *    Propositionalization
	 * ************************************************************************/
	
	/**
	 * Propositionalizes this factor on the specified logical variable.
	 * <br>
	 * Given a parfactor g = <C,V,F> and a logical variable X, this operation
	 * returns a set of parfactors that are the result of splitting g for all
	 * substitutions {X/c} that satisfy c &in; D(X):C.
	 * 
	 * @param logicalVariable The logical variable to propositionalize
	 * @return This parfactor propositionalized on the specified logical
	 * variable.
	 */
	public Set<Parfactor> propositionalize(LogicalVariable logicalVariable) {
		Set<Parfactor> result = new HashSet<Parfactor>();
		Parfactor parfactorToSplit = this;
		for (Constant individual : logicalVariable.getIndividualsSatisfying(this.constraints)) {
			List<Parfactor> splitResult = parfactorToSplit.split(Binding.create(logicalVariable, individual));
			result.add(splitResult.get(1));
			parfactorToSplit = splitResult.get(0);
		}
		
		// I'm not sure if this is necessary
		Parfactor residue = parfactorToSplit.replaceLogicalVariablesConstrainedToSingleConstant();
		if (!residue.isConstant()) {
			result.add(residue);
		}
		return result;
	}
	
	/**************************************************************************/
	
	
	/* ************************************************************************
	 *    Expansion
	 * ************************************************************************/
	
	// TODO: hey! what if n = 2?
	public Parfactor expand(CountingFormula countingFormula, Term term) {
		if (this.isInNormalForm()
				&& belongsToParfactor(countingFormula) 
				&& termIsNotInConstraintsFromCountingFormula(countingFormula, term) 
				&& termAppearsInConstraintsForAllLogicalVariablesInCountingFormula(countingFormula, term)) {

			// Creates the new set of parameterized random variables:
			// V' = V \ {#_A:CA [f(...,A,...)]} U {f(...,t,...), #_A:(CA U {A != t}) [f(...,A,...)]}
			
			ArrayList<ParameterizedRandomVariable> newVariables = this.factor.getParameterizedRandomVariables();
			
			CountingFormula newCountingFormula = 
				countingFormula.addConstraint(
					Constraint.getInstance(
						countingFormula.getBoundVariable(), 
						term
					)
				);
			
			newVariables.set(newVariables.indexOf(countingFormula), newCountingFormula);
			newVariables.add(
				newVariables.indexOf(
					newCountingFormula) + 1, 
					newCountingFormula.applyOneSubstitution(
						Binding.create(
							newCountingFormula.getBoundVariable(), 
							term
						)
					)
				);
			
			
			// Creates the new Factor
			ArrayList<Number> newValues = new ArrayList<Number>();
			
			// maybe there is a smarter way to do this
			newValues.add(this.factor.getTupleValue(0));
			newValues.add(this.factor.getTupleValue(1));
			for (int i = 2; i < this.factor.getAllValues().size() - 2; i = i + 2) {
				newValues.add(this.factor.getAllValues().get(i));
				newValues.add(this.factor.getAllValues().get(i + 1));
				newValues.add(this.factor.getAllValues().get(i));
				newValues.add(this.factor.getAllValues().get(i + 1));
			}
			newValues.add(this.factor.getTupleValue(this.factor.getAllValues().size() - 2));
			newValues.add(this.factor.getTupleValue(this.factor.getAllValues().size() - 1));

			return SimpleParfactor.getInstance(this.constraints, ParameterizedFactor.getInstance(this.factor.getName(), newVariables, newValues));
		} else {
			return this;
		}
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
		// i dont know if this will be useful, so the code is ugly
		for (Constraint constraint : this.constraints) {
			if (constraint.getSecondTerm() instanceof LogicalVariable) {
				Set<Term> firstSet = getExcludedSet(constraint.getFirstTerm());
				firstSet.remove(constraint.getSecondTerm());
				Set<Term> secondSet = getExcludedSet((LogicalVariable) constraint.getSecondTerm());
				secondSet.remove(constraint.getFirstTerm());
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
		for (Constraint constraint : this.constraints) {
			if (constraint.contains(x)) {
				excludedSet.add(constraint.getSecondTerm()); // i do not verify if the other term is in fact the second term of the constraint...
			}
		}
		return excludedSet;
	}
	
	/**
	 * Checks if the given counting formula belongs to the set of parameterized
	 * random variables of this parfactor.
	 * @param countingFormula The counting formula to verify
	 * @return True if the counting formula is in this parfactor, false 
	 * otherwise.
	 */
	private boolean belongsToParfactor(CountingFormula countingFormula) {
		return this.factor.getParameterizedRandomVariables().contains(countingFormula);
	}
	
	/**
	 * Checks if the term t does not appear in any constraint from the counting
	 * formula, that is, given a counting formula 
	 * #<sub>A:C<sub>A</sub></sub>[f(...,A,...)]
	 * and a term t, checks if 
	 * t &notin; &epsilon;<sub>A</sub><sup>C<sub>A</sub></sup>
	 * @param countingFormula A counting formula
	 * @param term The term to check
	 * @return True if t &notin; &epsilon;<sub>A</sub><sup>C<sub>A</sub></sup>,
	 * false otherwise.
	 */
	private boolean termIsNotInConstraintsFromCountingFormula(CountingFormula countingFormula, Term term) {
		for (Constraint c : countingFormula.getConstraints()) {
			if (c.contains(term)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if t &in; &epsilon;<sub>Y</sub><sup>C<sub>i</sub></sup> for each
	 * logical variable Y &in; &epsilon;<sub>A</sub><sup>C<sub>A</sub></sup>.
	 * In other words, for each logical variable Y that appears in constraints 
	 * C<sub>A</sub>, checks if the constraint Y &ne; t appears in C<sub>i</sub>.
	 * If there is a logical variable Y that does not satisfy it, returns false.
	 * @param countingFormula A counting formula 
	 * #<sub>A:C<sub>A</sub></sub>[f(...,A,...)]
	 * @param term The term to check
	 * @return True if t &in; &epsilon;<sub>Y</sub><sup>C<sub>i</sub></sup> for 
	 * each logical variable Y &in; &epsilon;<sub>A</sub><sup>C<sub>A</sub></sup>,
	 * false otherwise.
	 */
	private boolean termAppearsInConstraintsForAllLogicalVariablesInCountingFormula(CountingFormula countingFormula, Term term) {
		for (Constraint countingFormulaConstraint : countingFormula.getConstraints()) {
			boolean foundMatch = false;
			for (Constraint parfactorConstraint : this.constraints) {
				if (parfactorConstraint.hasCommonTerm(countingFormulaConstraint)) {
					foundMatch = true;
					break;
				}
			}
			if (!foundMatch && this.constraints.size() > 0) {
				return false;
			}
		}	
		return true;
	}
	
	/**************************************************************************/
	
	
	/* ************************************************************************
	 *    Unification
	 * ************************************************************************/
	
	/**
	 * TODO: should be private.
	 * Returns a parfactor replacing all LogicalVariables from this parfactor 
	 * that are constrained to one single Constant with this Constant. 
	 * <br>
	 * If this parfactor represents 0 factors, returns the constant parfactor
	 * (neutral element in multiplications).
	 * <br>
	 * If this parfactor does not contain any LogicalVariables under the
	 * conditions above, this method returns this parfactor unmodified.
	 * <br>
	 * <br>
	 * Example: suppose X is a logical variable with population {x1,x2} and
	 * that the set of constraints C is given by {X &ne; x1}. Then X is 
	 * constrained to one single constant: x2. All occurrences of X are replaced
	 * with x2.
	 * <br>
	 * <br>
	 * The algorithm used is based on the work of Mackworth (1977) and
	 * Kisysnki (2010).
	 */
	public Parfactor replaceLogicalVariablesConstrainedToSingleConstant() {
		
		LinkedList<LogicalVariable> queue = new LinkedList<LogicalVariable>();
		for (Constraint constraint : this.constraints) {
			queue.offer(constraint.getFirstTerm());
			if (constraint.secondTermIsLogicalVariable()) {
				queue.offer((LogicalVariable) constraint.getSecondTerm());
			}
		}
		
		SimpleParfactor newParfactor = this;
		
		while (!queue.isEmpty()) {
			
			LogicalVariable logicalVariable = queue.poll();
			Population populationSatisfyingConstraints = logicalVariable.getPopulation();
			HashSet<Constraint> newConstraints = new HashSet<Constraint>(newParfactor.getConstraints()); //safe copy
			
			for (Constraint constraint : newParfactor.getConstraints()) {
				if (constraint.getFirstTerm().equals(logicalVariable) 
						&& constraint.secondTermIsConstant()) {
					populationSatisfyingConstraints.removeIndividual((Constant) constraint.getSecondTerm());
				}
			}
			
			if (populationSatisfyingConstraints.size() == 0) {
				return SimpleParfactor.getConstantInstance();
			} else if (populationSatisfyingConstraints.size() == 1) {
				for (Constraint constraint : newConstraints) {
					if (constraint.getFirstTerm().equals(logicalVariable)) {
						if (constraint.secondTermIsConstant()) {
							newParfactor.constraints.remove(constraint);
						} else {
							queue.offer((LogicalVariable) constraint.getFirstTerm());
						}
					}
				}
				Binding substitution = Binding.create(logicalVariable, populationSatisfyingConstraints.getIndividual(0));
				newParfactor = newParfactor.applySubstitution(substitution);
			} else {
				//do nothing
			}
		}
		return newParfactor;                            
	}
	
	/**
	 * Renames all logical variables in this parfactor so that no name 
	 * conflict occurs. Returns the modified parfactor.
	 * @TODO make it privates
	 * @return This parfactor with all its logical variables renamed.
	 */
	public Parfactor renameLogicalVariables() {
		// Using hash set because I don't need repetition
		HashSet<LogicalVariable> logicalVariables = new HashSet<LogicalVariable>();
		for (ParameterizedRandomVariable prv : this.factor.getParameterizedRandomVariables()) {
			for (LogicalVariable lv : prv.getParameters()) {
				logicalVariables.add(lv);
			}
		}
		
		SimpleParfactor newParfactor = this;
		for (LogicalVariable lv : logicalVariables) {
			newParfactor = newParfactor.applySubstitution(
					Binding.create(
							lv, 
							LogicalVariableNameGenerator.rename(lv) 
					)
			);
		}
		return newParfactor;
	}
	
	
	/**
	 * When the MGU is consistent with a set of inequality constraints,
	 * parameterized random variables represent non-disjoint and possibly
	 * non-identical sets of random variables. To make then identical, we 
	 * split the parfactor involved on the MGU.
	 * <br>
	 * This method splits this parfactor in all substitutions present in
	 * the MGU.
	 * 
	 * @param mgu The Most General Unifier to split this parfactor.
	 * @return A list of parfactors. The first element is the parfactor
	 * obtained by spliting this parfactor on the MGU. The remaining elements
	 * are the residual parfactors from the split.
	 */
	public List<Parfactor> splitOnMgu(Substitution mgu) throws ArrayIndexOutOfBoundsException {
		SimpleParfactor result = this;
		ArrayList<Parfactor> residualParfactors = new ArrayList<Parfactor>();
		Iterator<LogicalVariable> mguIterator = mgu.getSubstitutedIterator();
		
		while (mguIterator.hasNext()) {
			LogicalVariable replaced = mguIterator.next();
			if (result.factor.contains(replaced)) {
				Binding binding =  Binding.create(replaced, mgu.getReplacement(replaced));
				Term replacement = mgu.getReplacement(replaced);
				if (replacement.isConstant() || result.factor.contains((LogicalVariable) replacement)) {
					List<Parfactor> resultSplit = result.split(binding);
					if (resultSplit.size() == 2) { 
						result = (SimpleParfactor) resultSplit.get(1);
						residualParfactors.add(resultSplit.get(0));
					} else {
						System.out.print("Splitting resulted in " + 
								resultSplit.size() + " parfactors.");
					}
				} else {
					result = result.applySubstitution(binding);
				}
			}
		}
		residualParfactors.add(0, result);
		return residualParfactors;
	}
	
	/**
	 * Splits a parfactor on a set of constraints.
	 * <br>
	 * This method is necessary to process parfactors that contain constraints
	 * and that are under the process of unification. This method is used
	 * on the last step to make two parameterized random variables represent
	 * the same set of random variables.
	 * 
	 * @param constraints The constraints to split this parfactor on.
	 * @return A list of parfactors. The first parfactor is the result of
	 * splitting this parfactor on the set of specified constraints. The
	 * remaining parfactors are the by-product parfactors.
	 */
	public List<Parfactor> splitOnConstraints(Set<Constraint> constraints) {
		SimpleParfactor residue = this;
		ArrayList<Parfactor> byProductParfactors = new ArrayList<Parfactor>();
		for (Constraint constraint : constraints) {
			if (!this.constraints.contains(constraint)
					&& residue.factor.contains(constraint.getFirstTerm())
					&& (constraint.secondTermIsConstant() 
							|| residue.factor.contains((LogicalVariable) constraint.getSecondTerm()))) {
				List<Parfactor> resultSplit = residue.split(constraint.toBinding());
				if (resultSplit.size() == 2) { 
					residue = (SimpleParfactor) resultSplit.get(0);
					byProductParfactors.add(resultSplit.get(1));
				}
			}
		}
		byProductParfactors.add(0, residue);
		return byProductParfactors;
	}
	
	/**
	 * Returns a set of parfactors in which for every pair of parameterized 
	 * random variables from parfactors of this set, the set of random
	 * variables represented by them are either identical or disjoint.
	 * <br>
	 * This set is generated using unification, as described by Kisynski (2010).
	 * This method is used in SHATTER macro operation to guarantee that all
	 * parameterized random variables from parfactors of a set represent
	 * identical or disjoint sets of random variables.
	 * 
	 * @param parfactor The parfactor to unify with.
	 * @return A set of shattered parfactors.
	 */
	public Set<Parfactor> unify(Parfactor parfactor) {
		Parfactor g1 = this.replaceLogicalVariablesConstrainedToSingleConstant();
		Parfactor g2 = parfactor.replaceLogicalVariablesConstrainedToSingleConstant();
		g1 = g1.renameLogicalVariables();
		g2 = g2.renameLogicalVariables();
		
		Set<Parfactor> unifiedSet = new HashSet<Parfactor>();
		unifiedSet.add(g1);
		unifiedSet.add(g2);
		boolean updatedSet = true;
		while (updatedSet) {
			updatedSet = false;
			for (ParameterizedRandomVariable firstVariable : g1.getParameterizedRandomVariables()) {
				for (ParameterizedRandomVariable secondVariable : g2.getParameterizedRandomVariables()) {
					Substitution mgu = null;
					try {
						mgu = firstVariable.getMgu(secondVariable);
					} catch (IllegalArgumentException e) {
						// firstVariable and secondVariable represent disjoint sets
						continue;
					}
					if (mgu.isEmpty()) {
						// firstVariable and secondVariable represent the same set
					} else {
						Set<Constraint> allConstraints = g1.getConstraints();
						allConstraints.addAll(g2.getConstraints());
						if (Parfactors.checkMguAgainstConstraints(mgu, allConstraints)) {
							List<Parfactor> firstSplitOnMgu = g1.splitOnMgu(mgu);
							List<Parfactor> secondSplitOnMgu = g2.splitOnMgu(mgu);
							Parfactor firstResult = firstSplitOnMgu.remove(0);
							Parfactor secondResult = secondSplitOnMgu.remove(0);
							List<Parfactor> firstSplitOnConstraints = firstResult.splitOnConstraints(secondResult.getConstraints());
							List<Parfactor> secondSplitOnConstraints = secondResult.splitOnConstraints(firstResult.getConstraints());
							unifiedSet.remove(g1);
							unifiedSet.remove(g2);
							unifiedSet.addAll(firstSplitOnMgu);
							unifiedSet.addAll(secondSplitOnMgu);
							unifiedSet.addAll(firstSplitOnConstraints);
							unifiedSet.addAll(secondSplitOnConstraints);
							updatedSet = true;
							break;
						} else {
							// firstVariable and secondVariable represent disjoint sets
						}
					}
				}
			}
			if (updatedSet) {
				break;
			}
		}
		
		return unifiedSet;
	}
	
	/**************************************************************************/
	
	
	/* ************************************************************************
	 *    Counting
	 * ************************************************************************/
	
	public Parfactor count(LogicalVariable logicalVariable) 
			throws IllegalArgumentException {
		
		if (canBeCounted(logicalVariable)) {
			
			// get constratints in A
			HashSet<Constraint> constraintsOnCounted = new HashSet<Constraint>();
			for (Constraint constraint : this.constraints) {
				if (constraint.contains(logicalVariable)) {
					constraintsOnCounted.add(constraint);
				}
			}
			
			// get prv in A
			ParameterizedRandomVariable prv = this.factor.getVariableToCount(logicalVariable);
			
			// create counting formula
			CountingFormula countingFormula = 
				CountingFormula
				.getInstance(logicalVariable, constraintsOnCounted, prv);
			
			// create new set of constraitns
			HashSet<Constraint> newConstraints = new HashSet<Constraint>(this.constraints);
			newConstraints.removeAll(constraintsOnCounted);
			
			// create new set of prvs
			ArrayList<ParameterizedRandomVariable> newVariables =
				this.factor.getParameterizedRandomVariables();
			newVariables.set(newVariables.indexOf(prv), countingFormula);
			
			// create new factor
			ArrayList<Number> newValues = new ArrayList<Number>();
			for (int cfIndex = 0; cfIndex < countingFormula.getRangeSize(); cfIndex++) {
				ArrayList<Integer> processedIndexes = new ArrayList<Integer>();
				for (int fIndex = 0; fIndex < this.factor.size() && !processedIndexes.contains(fIndex); fIndex++) { 
					double newValue = 1.0;
					for (int prvIndex = 0; prvIndex < prv.getRangeSize(); prvIndex++) {
						int tupleIndex = this.factor.getTupleValueOnVariable(prv, prvIndex, fIndex); // the tuple being processed
						newValue *= Math
							.pow(this.factor.getTupleValue(tupleIndex),
								countingFormula.getCount(cfIndex, prvIndex));
						processedIndexes.add(tupleIndex);
					}
					newValues.add(newValue);
				}
			}
			
			return new SimpleParfactor(
					newConstraints, 
					ParameterizedFactor.getInstance(
							this.factor.getName(),
							newVariables, 
							newValues));
			
		} else {
			throw new IllegalArgumentException("Logical variable " 
					+ logicalVariable 
					+ " does not satisfy conditions to counted.");
		}
	}
	
	/**
	 * Returns true if the specified logical variable can be counted in this
	 * parfactor, false otherwise.
	 * <br>
	 * A logical variable is 'countable' when it occurs free in only one 
	 * parameterized random variable in the parfactor.
	 * @param logicalVariable The candidate logical variable to be counted.
	 * @return True if the logical variable is countable, false otherwise.
	 */
	public boolean canBeCounted(LogicalVariable logicalVariable) {
		return this.factor.isUnique(logicalVariable);
	}
	
	/**************************************************************************/
	
	@Override
	public String toString() {
		return "\n<\n" + constraints + ",\n" + factor.getParameterizedRandomVariables() + ",\n" + factor + ">\n";
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof SimpleParfactor))
	    	return false;
	    // Tests if both have the same attributes
	    SimpleParfactor targetObject = (SimpleParfactor) other;
	    return ((this.constraints == null) ? 
	    		 targetObject.constraints == null : 
		    		 this.constraints.equals(targetObject.constraints)) &&
    		   ((this.factor == null) ? 
    		     targetObject.factor == null : 
    		     this.factor.equals(targetObject.factor));	    		
	}
	
	@Override
	public int hashCode() { // Algorithm extracted from Bloch,J. Effective Java
		int result = 17;
		result = 31 + result + constraints.hashCode();
		result = 31 + result + factor.hashCode();
		return result;
	}

}
