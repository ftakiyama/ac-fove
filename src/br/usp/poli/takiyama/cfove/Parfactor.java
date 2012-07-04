package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.List;

import br.usp.poli.takiyama.cfove.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.ve.Factor;

/**
 * Parfactors, also known as Parametric Factors, represent the joint distribution 
 * of a set of parameterized random variables.
 * @author ftakiyama
 *
 */
public final class Parfactor {
	
	private final ArrayList<Constraint> constraints;
	private final ArrayList<ParameterizedRandomVariable> variables;
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
			List<ParameterizedRandomVariable> variables, 
			Factor factor) 
			throws IllegalArgumentException {
		
		this.constraints = new ArrayList<Constraint>(constraints);
		this.variables = new ArrayList<ParameterizedRandomVariable>(variables);
		this.factor = factor.copy();
		
		// check if the factor is consistent with the list of variables
		
		// check if the factor uses counting formulas; if so, check if it is in normal form
		
//		if (false) {
//			throw new IllegalArgumentException("Constraints not in normal form.");
//		}
	}
	
	public static Parfactor getInstance(
			List<Constraint> constraints, 
			List<ParameterizedRandomVariable> variables, 
			Factor factor) 
			throws IllegalArgumentException {
		return new Parfactor(constraints, variables, factor);
	}
	
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables() {
		return new ArrayList<ParameterizedRandomVariable>(this.variables);
	}
	
	public List<Constraint> getConstraints() {
		return new ArrayList<Constraint>(this.constraints);
	}
	
	public Factor getFactor() {
		return this.factor.copy();
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
		
		return 0;
	}
}
