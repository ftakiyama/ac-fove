package br.usp.poli.takiyama.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.cfove.SimpleParfactor;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.PRV;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;

/**
 * This class generates structures for testing purposes. 
 * It contains all logical variables, constraints, parameterized random 
 * variables and parfactors used throughout the unit tests.
 * 
 * @author ftakiyama
 *
 */
public class Pool {
	
	private HashMap<String, LogicalVariable> variablesPool;
	private HashMap<String, Constraint> constraintsPool;
	private HashMap<String, ParameterizedRandomVariable> prvPool;
	private HashMap<String, SimpleParfactor> simpleParfactorPool;
	
	// maybe I could separate factors in a separated pool.
	
	/**
	 * Constructor. Creates an empty Pool.
	 */
	public Pool() {
		variablesPool = new HashMap<String, LogicalVariable>();
		constraintsPool = new HashMap<String, Constraint>();
		prvPool = new HashMap<String, ParameterizedRandomVariable>();
		simpleParfactorPool = new HashMap<String, SimpleParfactor>();
	}
	
	/**
	 * Creates an instance of LogicalVariable and puts it in the logical
	 * variables pool.
	 * @param name The name of the logical variable
	 * @param prefix The prefix used to name individuals from population
	 * @param populationSize The size of the population
	 */
	private void createLogicalVariable(String name, String prefix, int populationSize) {
		variablesPool.put(name, PRV.getLogicalVariable(name, prefix, populationSize));
	}
	
	/**
	 * Creates an instance of ParameterizedRandomVariable and puts it in the
	 * PRV pool. 
	 * @param name The name of the parameterized random variable.
	 * @param variables An array of names of logical variables from this PRV.
	 */
	private void createPrv(String name, LogicalVariable ... variables) {
		prvPool.put(name, PRV.getBooleanPrv(name, variables));
	}
	
	/**
	 * Creates an instance of ParameterizedRandomVariable and puts it in the
	 * PRV pool. The variable is created using an existing variable and 
	 * applying a substitution to it.
	 * 
	 * @param prvName The name o the PRV where to apply the substitution
	 * @param substitutions A list of substitutions of the form X/n, where
	 * X is the name of an existing logical variable and n is a number
	 * indicating the index of the individual that will replace X.
	 */
	private void createPrvFromSubstitution(String prvName, String ... substitutions) {
		ArrayList<Binding> bindings = new ArrayList<Binding>();
		for (String substitution : substitutions) {
			String firstTerm = substitution.split("/")[0];
			String secondTerm = substitution.split("/")[1];
			bindings.add(
					Binding.create(
							variablesPool.get(firstTerm), 
							variablesPool.get(firstTerm)
										 .getPopulation()
										 .getIndividual(
												 Integer.parseInt(secondTerm))));
		}
		Substitution substitution = Substitution.create(bindings);
		prvPool.put(prvName + Arrays.toString(substitutions), prvPool.get(prvName).applySubstitution(substitution));
	}
	
	/**
	 * Creates an instance of Constraint and puts it in the Constraint pool.
	 * The key to get the constraint is [firstTerm] != [secondTerm]
	 * @param firstTerm The first term. Must be a logical variable name.
	 * @param secondTerm The second term. Can be a logical variable name or
	 * an individual, indicated by the number of its index.
	 */
	private void createConstraint(String firstTerm, String secondTerm) {
		if (Character.isUpperCase(secondTerm.charAt(0))) {
			constraintsPool.put(
					firstTerm + " != " + secondTerm, 
					Constraint.getInstance(
							variablesPool.get(firstTerm), 
							variablesPool.get(secondTerm)));
		} else {
			constraintsPool.put(
					firstTerm + " != " + secondTerm, 
					Constraint.getInstance(
							variablesPool.get(firstTerm), 
							variablesPool.get(firstTerm)
										 .getPopulation()
										 .getIndividual(
												 Integer.parseInt(secondTerm))));
		}
	}
	
	/**
	 * Creates an instance of CountingFormula and puts it in the PRV pool.
	 * To create the instance, it is necessary to have previously created
	 * the bound logical variable, the PRV and the constraints and have added
	 * them to their pools.
	 * @param name The name of the counting formula
	 * @param boundLogicalVariableName The bound logical variable name
	 * @param prvName The PRV name
	 * @param constraintNames A list of constraint names
	 */
	private void createCountingFormula(
			String name, 
			String boundLogicalVariableName, 
			String prvName, 
			String ... constraintNames) {
		
		HashSet<Constraint> constraints = new HashSet<Constraint>();
		for (String constraint : constraintNames) {
			constraints.add(this.constraintsPool.get(constraint));
		}
		prvPool.put(
				name, 
				CountingFormula.getInstance(
						this.variablesPool.get(boundLogicalVariableName), 
						constraints, 
						this.prvPool.get(prvName)));
	}
	
	/**
	 * Creates an instance of SimpleParfactor and puts it in the simple parfactor
	 * pool.
	 * @param name The name of the parfactor.
	 * @param constraints A list of constraints, separated by comma
	 * @param prvs A list of PRVs, separated by comma
	 * @param factorName The name of the factor
	 * @param values A list of factor values ordered according to the order of
	 * ths list of PRVs
	 */
	private void createSimpleParfactor(String name, String constraints, String prvs, String factorName, String values) {
		
		Set<Constraint> constraintsSet = new HashSet<Constraint>();
		if (constraints.length() > 0) {
			for (String constraint : constraints.split(",")) {
				constraintsSet.add(constraintsPool.get(constraint));
			}
		}
		
		ArrayList<ParameterizedRandomVariable> variablesSet = new ArrayList<ParameterizedRandomVariable>();
		for (String variable : prvs.split(",")) {
			variablesSet.add(prvPool.get(variable));
		}

		ArrayList<Number> factorValues = new ArrayList<Number>();
		for (String value : values.split(",")) {
			factorValues.add(Double.valueOf(value));
		}
		
		simpleParfactorPool.put(
				name, 
				SimpleParfactor.getInstance(
						constraintsSet, 
						ParameterizedFactor.getInstance(
								factorName, 
								variablesSet, 
								factorValues)));
	}
	
	
	
	/* ************************************************************************
	 *      Setup pool
	 * ************************************************************************/
	
	/**
	 * Creates data structures for example 2.15 of [Kisynski, 2010]
	 */
	public void setExample2_15() {
		
		createLogicalVariable("A", "x", 10);
		createLogicalVariable("B", "x", 10);
		
		createPrv("f", variablesPool.get("A"), variablesPool.get("B"));
		createPrv("h", variablesPool.get("B"));
		
		createPrvFromSubstitution("f", "B/0");
		createPrvFromSubstitution("h", "B/0");
		
		createConstraint("A", "B");
		createConstraint("A", "0");
		createConstraint("B", "0"); 
		
		// Initial parfactor
		createSimpleParfactor("g1", "A != B", "f,h", "F", "0.2,0.3,0.5,0.7");
		
		// Resulting parfactor
		createSimpleParfactor("g1[B/0]", "A != 0", "f[B/0],h[B/0]", "F", "0.2,0.3,0.5,0.7");
		
		// Residual parfactor
		createSimpleParfactor("g1'", "A != B,B != 0", "f,h", "F", "0.2,0.3,0.5,0.7");
		
	}
	
	/**
	 * Creates data structures for example 2.16 of [Kisynski, 2010]
	 */
	public void setExample2_16() {
		
		createLogicalVariable("A", "x", 3);
		createLogicalVariable("B", "x", 3);
		
		createPrv("f", variablesPool.get("A"));
		createPrv("h", variablesPool.get("B"));
		
		createPrvFromSubstitution("f", "A/1");
		
		createConstraint("A", "B");
		createConstraint("A", "1");
		
		createCountingFormula("#.A:{A!=B}[f(A)]", "A", "f", "A != B");
		createCountingFormula("#.A:{A!=B;A!=x1}[f(A)]", "A", "f", "A != B", "A != 1"); // << be careful! use semicolon to separate constraints in the name of counting formula
		
		// Initial parfactor
		createSimpleParfactor("g1", "", "#.A:{A!=B}[f(A)],h", "F", "0.2,0.3,0.5,0.7,0.11,0.13");
		
		// Parfactor resulting from expanding g1 on term x1
		createSimpleParfactor("g1'", "", "#.A:{A!=B;A!=x1}[f(A)],f[A/1],h", "F'", "0.2,0.3,0.5,0.7,0.5,0.7,0.11,0.13");
	}
	
	/**
	 * Returns a simple parfactor from the pool, or null if the parfactor does
	 * not exist.
	 * @param name The name of the parfactor
	 * @return A simple parfactor from the pool, or null if the parfactor does
	 * not exist.
	 */
	public SimpleParfactor getSimpleParfactor(String name) {
		return simpleParfactorPool.get(name);
	}
	
	/**
	 * Returns a logical variable from the pool, or null if the variable does
	 * not exist.
	 * @param name The name of the logical variable
	 * @return A logical variable from the pool, or null if the variable does
	 * not exist.
	 */
	public LogicalVariable getLogicalVariable(String name) {
		return variablesPool.get(name);
	}
	
	/**
	 * Returns a counting formula from the pool, or null if the counting
	 * formula does not exist. One must be sure about the name of the object
	 * being retrieved, since I do not check whether the returned object is
	 * a counting formula or a standard parameterized random variable.
	 * @param name The name of the counting formula
	 * @return A counting formula from the pool, or null if the formula does
	 * not exist.
	 */
	public CountingFormula getCountingFormula(String name) {
		return (CountingFormula) prvPool.get(name);
	}
}
