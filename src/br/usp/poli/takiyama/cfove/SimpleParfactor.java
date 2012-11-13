package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;
//import br.usp.poli.takiyama.common.Constraints;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.RandomVariable;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;
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
	 * @return A set of parfactors with two parfactors, as specified above.
	 */
	public Set<Parfactor> split(Binding substitution) {
		// TODO
		// substitution is actually a binding
		Set<Parfactor> result = new HashSet<Parfactor>();
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
	 * @param substitution
	 * @return
	 */
	private SimpleParfactor applySubstitution(Binding substitution) {
		HashSet<Constraint> substitutedConstraints = new HashSet<Constraint>();
		for (Constraint constraint : this.constraints) {
			substitutedConstraints.add(constraint.applySubstitution(substitution));
		}
		
		ArrayList<ParameterizedRandomVariable> substitutedVariables = new ArrayList<ParameterizedRandomVariable>();
		ArrayList<Binding> bindings = new ArrayList<Binding>();
		bindings.add(substitution);
		for (ParameterizedRandomVariable prv : this.factor.getParameterizedRandomVariables()) {
			substitutedVariables.add(prv.applySubstitution(Substitution.create(bindings)));
		}
		
		// TODO: correct the model: List<Number> is not a super-type of List<Double>
		ArrayList<Number> values = new ArrayList<Number>();
		for (Double d : this.factor.getAllValues()) {
			values.add(d);
		}
		
		return new SimpleParfactor(substitutedConstraints, ParameterizedFactor.getInstance(this.factor.getName(), substitutedVariables, values));
		
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
