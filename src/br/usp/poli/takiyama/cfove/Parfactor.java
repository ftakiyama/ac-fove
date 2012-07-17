package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import br.usp.poli.takiyama.cfove.prv.LogicalVariable;
import br.usp.poli.takiyama.cfove.prv.ParameterizedRandomVariable;

/**
 * Parfactors, also known as Parametric Factors, represent the joint distribution 
 * of a set of parameterized random variables.
 * @author ftakiyama
 *
 */
public final class Parfactor implements ParametricFactor {
	
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
	private Parfactor(
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
	
	public static Parfactor getInstance(
			List<Constraint> constraints, 
			//List<ParameterizedRandomVariable> variables, 
			ParameterizedFactor factor) 
			throws IllegalArgumentException {
		return new Parfactor(constraints, factor);
	}
	
	/**
	 * For tests purposes.
	 * Returns a factor without constraints.
	 * @param factor
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Parfactor getInstanceWithoutConstraints(ParameterizedFactor factor) 
			throws IllegalArgumentException {
		
		return new Parfactor(new ArrayList<Constraint>(), 
							 factor);
	}
	
	
	public static Parfactor getConstantInstance() {
		
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(Double.valueOf("1.0"));
		
		return new Parfactor(new ArrayList<Constraint>(), 
				 			 ParameterizedFactor.getInstance(
				 					 "1", 
				 					 new ArrayList<ParameterizedRandomVariable>(), 
				 					 mapping));
	}
	
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables() {
		return this.factor.getParameterizedRandomVariables();
	}
	
	public List<Constraint> getConstraints() {
		return new ArrayList<Constraint>(this.constraints);
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
	public String toString() {
		return "\n<\n" + constraints + ",\n" + factor.getParameterizedRandomVariables() + ",\n" + factor + ">\n";
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof Parfactor))
	    	return false;
	    // Tests if both have the same attributes
	    Parfactor targetObject = (Parfactor) other;
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
