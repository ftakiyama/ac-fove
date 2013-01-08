package br.usp.poli.takiyama.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.cfove.SimpleParfactor;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.PRV;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

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
	private HashMap<String, Substitution> substitutionPool;
	
	// maybe I could separate factors in a separated pool.
	
	/**
	 * Constructor. Creates an empty Pool.
	 */
	public Pool() {
		variablesPool = new HashMap<String, LogicalVariable>();
		constraintsPool = new HashMap<String, Constraint>();
		prvPool = new HashMap<String, ParameterizedRandomVariable>();
		simpleParfactorPool = new HashMap<String, SimpleParfactor>();
		substitutionPool = new HashMap<String, Substitution>();
	}
	
	/**
	 * Creates an instance of LogicalVariable and puts it in the logical
	 * variables pool.
	 * Indexes from individuals starts on 0.
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
	 * PRV pool.
	 * <br>
	 * This method accepts a mixed list of parameters (constants and logical
	 * variables). Constants must be bound to a logical variable, in the 
	 * following format: "LogicalVariableName=Index", where LogicalVariableName
	 * is the name of some logical variable in the pool and Index is the
	 * index of the individual in the logical variable's population.
	 * 
	 * @param name The name of the parameterized random variable.
	 * @param parameters An array of logical variables or constants that will
	 * be the parameters of this PRV.
	 */
	private void createPrv(String name, String ... parameters) {
		if (parameters == null) 
			throw new IllegalArgumentException("Parameter list must be at" +
					" least an empty string.");
		ArrayList<Term> terms = new ArrayList<Term>();
		for (String parameter : parameters) {
			if (variablesPool.containsKey(parameter)) {
				terms.add(variablesPool.get(parameter));	
			} else if (variablesPool.containsKey(parameter.split("=")[0])) {
				terms.add(variablesPool
						.get(parameter.split("=")[0])
						.getPopulation()
						.getIndividual(Integer
								.parseInt(parameter
										.split("=")[1])));
			} else {
				throw new IllegalArgumentException("There no such logical " +
						" variable in pool: " + parameter.split("=")[0]);
			}
		}
		
		prvPool.put(name, PRV.getBooleanPrv(name, terms.toArray(new Term[terms.size()])));
	}
	
	/**
	 * Creates an instance of ParameterizedRandomVariable and puts it in the
	 * PRV pool. The variable is created using an existing variable and 
	 * applying a substitution to it. The key of the new variable will be 
	 * the old variable's name concatenated with the list of substitutions in
	 * the form [A/B, C/1, ...].
	 * <br>
	 * For instance, suppose the following substitutions will be made on
	 * variable f(A,B,C): {A/x1, B/D}. Then the new variable will be
	 * f(x1,D,C) and the key to get it from the pool is f[A/0, B/D].
	 * 
	 * @param prvName The name o the PRV where to apply the substitution
	 * @param substitutions A list of substitutions of the form X/n, where
	 * X is the name of an existing logical variable and n is a number
	 * indicating the index of the individual that will replace X.
	 */
	private void createPrvFromSubstitution(String prvName, String ... substitution) {
		createSubstitution("temporary substitution", substitution);
		Substitution sub = substitutionPool.get("temporary substitution");
		prvPool.put(prvName + Arrays.toString(substitution), prvPool.get(prvName).applySubstitution(sub));
		substitutionPool.remove("temporary substitution");
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
	 * Creates an instance of Constraint and puts it in the Constraint pool.
	 * The key to get the constraint is [firstTerm] != [secondTerm].
	 * <br>
	 * This method allows the creation of inconsistent constraints, such as
	 * X &ne; y1, where D(X) = {x1,x2,...,xn}. Note that, in this example, the
	 * individual y1 does not belong to X's population, thus the constraint
	 * will always be true.
	 * <br>
	 * The constraint must be of the form [LogicalVariable] &ne; [Constant], and
	 * [Constant] must be bound to another logical variable using the format
	 * [LogicalVariable]=[Index], where [LogicalVariable] is a valid logical
	 * variable from the pool and [Index] is a number smaller than the logical
	 * variable population. 
	 * 
	 * @param firstTerm The first term. Must be a logical variable name in the
	 * pool.
	 * @param secondTerm The second term. Must be a Constant bound to another
	 * logical variable in the pool.
	 * 
	 * @throws IllegalArgumentException if terms specified are not in the pool
	 * of variables, or the second argument is not a valid expression.
	 */
	private void createInconsistentConstraint(String firstTerm, String secondTerm) 
			throws IllegalArgumentException {
		if (!variablesPool.containsKey(firstTerm)
				|| !variablesPool.containsKey(secondTerm.split("=")[0])
				|| secondTerm.split("=").length != 2) {
			throw new IllegalArgumentException("I cannot create the " +
					"constraint.\n Either " + firstTerm + 
					" is not in the pool, or " + secondTerm.split("=") + 
					" is not in the pool, or " + secondTerm + 
					" is not a valid expression.");
		}
		constraintsPool.put(
				firstTerm + " != " + secondTerm.split("=")[1], 
				Constraint.getInstance(
						variablesPool.get(firstTerm), 
						variablesPool.get(secondTerm.split("=")[0])
									 .getPopulation()
									 .getIndividual(
											 Integer.parseInt(secondTerm.split("=")[1]))));
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
	 * @param constraints A list of constraints, separated by semicolon
	 * @param prvs A list of PRVs, separated by semicolon
	 * @param factorName The name of the factor
	 * @param values A list of factor values ordered according to the order of
	 * ths list of PRVs, separated by semicolon
	 */
	private void createSimpleParfactor(String name, String constraints, String prvs, String factorName, String values) {
		
		Set<Constraint> constraintsSet = new HashSet<Constraint>();
		if (constraints.length() > 0) {
			for (String constraint : constraints.split(";")) {
				constraintsSet.add(constraintsPool.get(constraint));
			}
		}
		
		ArrayList<ParameterizedRandomVariable> variablesSet = new ArrayList<ParameterizedRandomVariable>();
		for (String variable : prvs.split(";")) {
			variablesSet.add(prvPool.get(variable));
		}

		ArrayList<Number> factorValues = new ArrayList<Number>();
		for (String value : values.split(";")) {
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
	
	/**
	 * Creates a substitution based on a list of strings given in the form
	 * X/Y, where X must be a string starting with upper case letter and Y 
	 * can start either with a upper case letter or be a number that indicates
	 * the index of the individual in the population of the first term.
	 * @param setName The name of the substitution. Used as a key to retrieve
	 * it later.
	 * @param substitution A list of substitutions
	 */
	private void createSubstitution(String setName, String ... substitution) {
		ArrayList<Binding> bindings = new ArrayList<Binding>();
		for (String binding : substitution) {
			String firstTerm = binding.split("/")[0];
			String secondTerm = binding.split("/")[1];
			Matcher matcher = Pattern.compile("^\\d+").matcher(secondTerm); // maybe this regular expression is incorrect.
			if (matcher.find()) {
				bindings.add(
						Binding.create(
								variablesPool.get(firstTerm), 
								variablesPool.get(firstTerm)
											 .getPopulation()
											 .getIndividual(
													 Integer.parseInt(secondTerm))));
			} else {
				bindings.add(
						Binding.create(
								variablesPool.get(firstTerm), 
								variablesPool.get(secondTerm)));
				
			}
		}
		substitutionPool.put(setName, Substitution.create(bindings));
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
		createSimpleParfactor("g1", "A != B", "f;h", "F", "0.2;0.3;0.5;0.7");
		
		// Resulting parfactor
		createSimpleParfactor("g1[B/0]", "A != 0", "f[B/0];h[B/0]", "F", "0.2;0.3;0.5;0.7");
		
		// Residual parfactor
		createSimpleParfactor("g1'", "A != B;B != 0", "f;h", "F", "0.2;0.3;0.5;0.7");
		
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
		createCountingFormula("#.A:{A!=B,A!=x1}[f(A)]", "A", "f", "A != B", "A != 1"); 
		
		// Initial parfactor
		createSimpleParfactor("g1", "", "#.A:{A!=B}[f(A)];h", "F", "0.2;0.3;0.5;0.7;0.11;0.13");
		
		// Parfactor resulting from expanding g1 on term x1
		createSimpleParfactor("g1'", "", "#.A:{A!=B,A!=x1}[f(A)];f[A/1];h", "F'", "0.2;0.3;0.5;0.7;0.5;0.7;0.11;0.13");
	}
	
	/**
	 * Creates data structures for example 2.18 of Kisynski, 2010.
	 */
	public void setExample2_18() {
		
		createLogicalVariable("A", "x", 4);
		createLogicalVariable("B", "x", 4);
		createLogicalVariable("C", "x", 4);
		
		createPrv("f", variablesPool.get("A"), variablesPool.get("B"), variablesPool.get("C"));
		createPrvFromSubstitution("f", "A/3", "B/1");
		
		createConstraint("A", "0");
		createConstraint("A", "1");
		createConstraint("A", "2");
		createConstraint("A", "B");
		createConstraint("A", "C");
		createConstraint("B", "0");
		createConstraint("B", "2");
		createConstraint("B", "C");
		createConstraint("C", "1");
		createConstraint("C", "3");
		
		createSimpleParfactor(
				"g1", 
				"A != 0;A != 1;A != 2;A != B;A != C;B != 0;B != 2;B != C", 
				"f", 
				"F", 
				"0.2;0.3");
		createSimpleParfactor("g2", "C != 1;C != 3", "f[A/3, B/1]", "F", "0.2;0.3");
	}
	
	/**
	 * Creates data structures for example 2.19 of Kisynski, 2010.
	 * I've changed it slightly to make implementation easier.
	 * Instead of constraint Z != x1, I'm doing Z != y1. I've also replaced
	 * PRV f(x1,Z) with f(X,Z).
	 */
	public void setExample2_19() {
		
		// parfactor [1]
		createLogicalVariable("X", "x", 5);
		createLogicalVariable("Y", "y", 6);
		
		createPrv("f", variablesPool.get("X"), variablesPool.get("Y"));
		createPrv("q", variablesPool.get("Y"));
		
		createConstraint("X", "1");
		
		createSimpleParfactor("g1", "X != 1", "f;q", "F", "0.2;0.3;0.5;0.7");
		
		// parfactor [2]
		createLogicalVariable("X", "y", 6); 
		createLogicalVariable("Z", "y", 6);
		
		createPrv("p", variablesPool.get("X"));
		createPrv("f", variablesPool.get("X"), variablesPool.get("Z"));
		
		createConstraint("X", "Z");
		createConstraint("Z", "0");
		
		createSimpleParfactor("g2", "X != Z;Z != 0", "p;f", "F", "0.2;0.3;0.5;0.7");
		
		// parfactor [3] - changed slightly because of implementation used
		createLogicalVariable("X2", "x", 5);
		createLogicalVariable("X1", "y", 6);
		
		createPrv("f", variablesPool.get("X2"), variablesPool.get("X1"));
		createPrv("q", variablesPool.get("X1"));
		
		createConstraint("X2", "1");
		
		createSimpleParfactor("g1'", "X2 != 1", "f;q", "F", "0.2;0.3;0.5;0.7");
		
		// parfactor [4]
		createLogicalVariable("X3", "y", 6);
		createLogicalVariable("X4", "y", 6);
		
		createPrv("p", variablesPool.get("X3"));
		createPrv("f", variablesPool.get("X3"), variablesPool.get("X4"));
		
		createConstraint("X3", "X4");
		createConstraint("X4", "0");
		
		createSimpleParfactor("g2'", "X3 != X4;X4 != 0", "p;f", "F", "0.2;0.3;0.5;0.7");
		
	}
	
	/**
	 * Creates data structures for example 2.20 of Kisynski (2010).
	 */
	public void setExample2_20() {
		createLogicalVariable("X1", "y", 10);
		createLogicalVariable("X2", "x", 10);
		createLogicalVariable("X3", "y", 10);
		createLogicalVariable("X4", "y", 10);
		
		createPrv("f", variablesPool.get("X1"), variablesPool.get("X2"));
		createPrvFromSubstitution("f", "X1/1", "X2/X4");
		
		createSubstitution("answer", "X1/1", "X2/X4");
		
		// Constraints to test compatibility against the mgu
		createConstraint("X1", "1");
		createConstraint("X3", "X4");
		createConstraint("X4", "0");		
	}
	
	/**
	 * Creates data structures for example 2.21 of Kisynski (2010).
	 */
	public void setExample2_21() {
		
		// mgu
		createLogicalVariable("X1", "x", 5);
		createLogicalVariable("X2", "y", 6);
		createLogicalVariable("X3", "y", 6);
		createLogicalVariable("X4", "y", 6);
		createSubstitution("mgu", "X1/0", "X2/X4");
		
		// parfactor [3] - changed slightly because of implementation used
		createPrv("f", variablesPool.get("X1"), variablesPool.get("X2"));
		createPrv("q", variablesPool.get("X2"));
		
		createConstraint("X1", "1");
		
		createSimpleParfactor("g3", "X1 != 1", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
		// parfactor [4]
		createPrv("p", variablesPool.get("X3"));
		createPrv("f", "X2=0", "X4");
		
		createConstraint("X3", "X4");
		createConstraint("X4", "0");
		
		createSimpleParfactor("g4", "X3 != X4;X4 != 0", "p;f", "F2", "0.2;0.3;0.5;0.7");
		
		// parfactor [5]
		createPrv("f", "X1=0", "X4");
		createPrv("q", "X4");
		createSimpleParfactor("g5", "", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
		//parfactor [6]
		createPrv("f", "X1", "X2");
		createPrv("q", "X2");
		createConstraint("X1", "0");
		createConstraint("X1", "1");
		createSimpleParfactor("g6", "X1 != 0;X1 != 1", "f;q", "F1", "0.2;0.3;0.5;0.7");
	}
	
	/**
	 * Creates data structures for example 2.22 of Kisynski (2010).
	 * The example has been changed to become more consistent.
	 */
	public void setExample2_22() {
		
		createLogicalVariable("X1", "x", 5);
		createLogicalVariable("X2", "y", 6);
		createLogicalVariable("X3", "y", 6);
		createLogicalVariable("X4", "y", 6);
		
		// parfactor [4]
		createPrv("p", variablesPool.get("X3"));
		createPrv("f", "X2=1", "X4"); // f(y1,X4) and not f(x1,X4)
		createConstraint("X3", "X4");
		createConstraint("X4", "1"); // X4 != y1, and not x1
		createSimpleParfactor("g4", "X3 != X4;X4 != 1", "p;f", "F2", "0.2;0.3;0.5;0.7");
		
		// parfactor [5]
		createPrv("q", "X4");
		createSimpleParfactor("g5", "", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
		// parfactor [6]
		createPrv("f", "X1", "X2");
		createPrv("q", "X2");
		createConstraint("X1", "2");
		createSimpleParfactor("g6", "X1 != 2", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
		// parfactor [7]
		createPrv("f", "X2=1", "X4=1"); // f(y1,y1) and not f(x1,x1)
		createPrv("q", "X4=1");
		createSimpleParfactor("g7", "", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
		// parfactor [8]
		createPrv("f", "X2=1", "X4"); // f(y1,X4) and not f(x1,X4)
		createPrv("q", "X4");
		createSimpleParfactor("g8", "X4 != 1", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
	}
	
	/**
	 * Creates data structures for example 2.19 of Kisynski (2010).
	 * The intention is to run all unification related methods until we get
	 * the result obtained in example 2.22 of Kisynski (2010).
	 * The example has been changed to become more consistent.
	 */
	public void setExample2_19To2_22() {
		
		// parfactor [1]
		createLogicalVariable("X", "x", 5);
		createLogicalVariable("Y", "y", 6);
		createPrv("f", variablesPool.get("X"), variablesPool.get("Y"));
		createPrv("q", variablesPool.get("Y"));
		createConstraint("X", "2");
		createSimpleParfactor("g1", "X != 2", "f;q", "F", "0.2;0.3;0.5;0.7");
		
		// parfactor [2]
		createLogicalVariable("Z", "y", 6);
		createPrv("f", "X=1", "Z");
		createInconsistentConstraint("Z", "X=1");
		createLogicalVariable("X", "y", 6); 
		createPrv("p", variablesPool.get("X"));
		createConstraint("X", "Z");
		createSimpleParfactor("g2", "X != Z;Z != 1", "p;f", "F", "0.2;0.3;0.5;0.7");
		
		// Sets the correct result
		
		createLogicalVariable("X1", "x", 5);
		createLogicalVariable("X2", "y", 6);
		createLogicalVariable("X3", "y", 6);
		createLogicalVariable("X4", "y", 6);
		
		// parfactor [4]
		createPrv("p", variablesPool.get("X3"));
		createPrv("f", "X1=1", "X4"); 
		createConstraint("X3", "X4");
		createInconsistentConstraint("X4", "X1=1"); 
		createSimpleParfactor("g4", "X3 != X4;X4 != 1", "p;f", "F2", "0.2;0.3;0.5;0.7");
		
		// parfactor [6]
		createPrv("f", "X1", "X2");
		createPrv("q", "X2");
		createConstraint("X1", "2");
		createSimpleParfactor("g6", "X1 != 2", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
		// parfactor [7]
		createPrv("f", "X1=1", "X1=1"); 
		createPrv("q", "X1=1");
		createSimpleParfactor("g7", "", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
		// parfactor [8]
		createPrv("f", "X1=1", "X4"); 
		createPrv("q", "X4");
		createSimpleParfactor("g8", "X4 != 1", "f;q", "F1", "0.2;0.3;0.5;0.7");
	}
	
	public void setCountingTestWithoutConstraints() {
		createLogicalVariable("A", "x", 3);
		createPrv("f", "A");
		createSimpleParfactor("g1", "", "f", "F", "2;3");
		
		createCountingFormula("#.A[f]", "A", "f");
		createSimpleParfactor("g2", "", "#.A[f]", "F", "8;12;18;27");
		
	}
	
	public void setCountingTestWithConstraint() {

		createLogicalVariable("A", "x", 3);
		createPrv("f", "A");
		createConstraint("A", "0");
		createSimpleParfactor("g1", "A != 0", "f", "F", "2;3");
		
		createCountingFormula("#.A[f]", "A", "f", "A != 0");
		createSimpleParfactor("g2", "", "#.A[f]", "F", "4;6;9");
	}
	
	public void setExample2_17WithoutConstraints() {
		
		createLogicalVariable("A", "x", 3);
		createLogicalVariable("B", "x", 3);
		createPrv("f", "A");
		createPrv("h", "B");
		createSimpleParfactor("g1", "", "f;h", "F", "2;3;5;7");
		
		createCountingFormula("#.A[f]", "A", "f");
		createSimpleParfactor("g2", "", "#.A[f];h", "F", "8;27;20;63;50;147;125;343");
	}
	
	/**
	 * Creates data structures for example 2.17 of Kisynski (2010).
	 */
	public void setExample2_17() {
		
		createLogicalVariable("A", "x", 3);
		createLogicalVariable("B", "x", 3);
		createPrv("f", "A");
		createPrv("h", "B");
		createConstraint("A", "B");
		createSimpleParfactor("g1", "A != B", "f;h", "F", "2;3;5;7");
		createCountingFormula("#.A[f]", "A", "f","A != B");
		createSimpleParfactor("g2", "", "#.A[f];h", "F", "4;9;10;21;25;49");
	}
	
	/**
	 * Creates data structures for propositionalization test.
	 */
	public void setPropositionalizationTest() {
		createLogicalVariable("A", "x", 3);
		createLogicalVariable("B", "x", 3);
		createPrv("f", "A", "B");
		createPrv("h", "B");
		createConstraint("A", "B");
		createSimpleParfactor("g", "A != B", "f;h", "F", "2;3;5;7");
		
		createPrv("f", "A=0", "B");
		createConstraint("B", "0");
		createSimpleParfactor("g1", "B != 0", "f;h", "F", "2;3;5;7");
		
		createPrv("f", "A=1", "B");
		createConstraint("B", "1");
		createSimpleParfactor("g2", "B != 1", "f;h", "F", "2;3;5;7");

		createPrv("f", "A=2", "B");
		createConstraint("B", "2");
		createSimpleParfactor("g3", "B != 2", "f;h", "F", "2;3;5;7");
	}
	
	/* ************************************************************************
	 *      Exposed methods
	 * ************************************************************************/
	
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
	
	/**
	 * Returns a substitution set from the pool, or null if the set does not
	 * exist.
	 * @param name The name of the substitution.
	 * @return A substitution set from the pool, or null if the set does not
	 * exist.
	 */
	public Substitution getSubstitution(String name) {
		return substitutionPool.get(name);
	}
	
	/**
	 * Returns a parameterized random variable from the pool, or null if the
	 * parameterized random variable does not exist. 
	 * @param name The name of the parameterized random variable
	 * @return A parameterized random variable from the pool, or null if the
	 * parameterized random variable does not exist. 
	 */
	public ParameterizedRandomVariable getParameterizedRandomVariable(String name) {
		return prvPool.get(name);
	}
	
	/**
	 * Returns a constraint from the pool
	 * @param name The name of the constraint. It has the format "t != k", where
	 * t and k are terms.
	 * @return A constraint from the pool with the specified name.
	 * @throws IllegalArgumentException If the constraint with the given name
	 * does not exist in the pool.
	 */
	public Constraint getConstraint(String name) throws IllegalArgumentException {
		if (constraintsPool.containsKey(name)) {
			return constraintsPool.get(name);
		} else {
			throw new IllegalArgumentException("There is no such constraint in " +
					"the pool: " + name);
		}
	}
}
