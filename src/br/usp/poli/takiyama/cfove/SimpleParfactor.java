package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.RandomVariable;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.utils.Sets;

/**
 * Parfactors, also known as Parametric Factors, represent the joint distribution 
 * of a set of parameterized random variables.
 * @author ftakiyama
 *
 */
public final class SimpleParfactor implements Parfactor {
	
	private final ArrayList<Constraint> constraints;
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
			List<Constraint> constraints, 
			//List<ParameterizedRandomVariable> variables, 
			ParameterizedFactor factor) 
			throws IllegalArgumentException {
		
		this.constraints = new ArrayList<Constraint>(constraints);
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
		return new SimpleParfactor(constraints, factor);
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
			//List<ParameterizedRandomVariable> variables, 
			ParameterizedFactor factor) 
			throws IllegalArgumentException {
		return new SimpleParfactor(new ArrayList<Constraint>(constraints), factor);
	}
	
	/**
	 * For tests purposes.
	 * Returns a factor without constraints.
	 * @param factor
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SimpleParfactor getInstanceWithoutConstraints(ParameterizedFactor factor) 
			throws IllegalArgumentException {
		
		return new SimpleParfactor(new ArrayList<Constraint>(), 
							 factor);
	}
	
	
	public static SimpleParfactor getConstantInstance() {
		
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(Double.valueOf("1.0"));
		
		return new SimpleParfactor(new ArrayList<Constraint>(), 
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

	/*
	 ***************************************************************************
	 *
	 * OPERATIONS
	 * 
	 ***************************************************************************
	 */
	
	
	public Set<Parfactor> sumOut(
			Set<Parfactor> setOfParfactors, 
			ParameterizedRandomVariable variable) {
		
		HashSet<Parfactor> parfactors = new HashSet<Parfactor>(setOfParfactors);
		
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
			Set<Parfactor> setOfParfactors, 
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
			Set<Parfactor> setOfParfactors, 
			ParameterizedRandomVariable variable) {
		
		Set<Parfactor> setOfParfactorsWithoutTarget = 
				new HashSet<Parfactor>(setOfParfactors);
		
		setOfParfactorsWithoutTarget.remove(this);
		
		for (Parfactor currentParfactor : setOfParfactorsWithoutTarget) {
			
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
	
	public Set<Parfactor> multiply(
			Set<Parfactor> setOfParfactors, 
			Parfactor parfactor) {
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
		
		HashSet<Parfactor> newSetOfParfactors = new HashSet<Parfactor>(setOfParfactors);
		if (conditionsForMultiplicationAreSatisfied(parfactor)) {
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
	
	private boolean conditionsForMultiplicationAreSatisfied( 
			Parfactor parfactor) {
		if (!(parfactor instanceof SimpleParfactor)) {
			return false;
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
		result = 31 + result + Arrays.hashCode(constraints.toArray(new Constraint[constraints.size()]));
		result = 31 + result + factor.hashCode();
		return result;
	}

}
