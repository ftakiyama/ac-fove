package br.usp.poli.takiyama.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.usp.poli.takiyama.acfove.AggregationParfactor;
import br.usp.poli.takiyama.acfove.AggregationParfactor.Builder;
import br.usp.poli.takiyama.acfove.GeneralizedAggregationParfactor;
import br.usp.poli.takiyama.acfove.operator.BooleanOperator;
import br.usp.poli.takiyama.acfove.operator.Or;
import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.cfove.SimpleParfactor;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.OldCountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.PRVs;
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
	private HashMap<String, RandomVariableSet> randomVariableSetPool;
	private HashMap<String, AggregationParfactor> aggParfactorPool;
	private HashMap<String, Binding> bindingPool;
	private HashMap<String, List<Parfactor>> parfactorListPool;
	private HashMap<String, GeneralizedAggregationParfactor> genAggParfactorPool;
	
	// maybe I could separate factors in a separated pool.
	
	/**
	 * Constructor. Creates an empty Pool.
	 */
	public Pool() {
		this.variablesPool = new HashMap<String, LogicalVariable>();
		this.constraintsPool = new HashMap<String, Constraint>();
		this.prvPool = new HashMap<String, ParameterizedRandomVariable>();
		this.simpleParfactorPool = new HashMap<String, SimpleParfactor>();
		this.substitutionPool = new HashMap<String, Substitution>();
		this.randomVariableSetPool = new HashMap<String, RandomVariableSet>();
		this.aggParfactorPool = new HashMap<String, AggregationParfactor>();
		this.bindingPool = new HashMap<String, Binding>();
		this.parfactorListPool = new HashMap<String, List<Parfactor>>();
		this.genAggParfactorPool = new HashMap<String, GeneralizedAggregationParfactor>();
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
		variablesPool.put(name, PRVs.getLogicalVariable(name, prefix, populationSize));
	}
	
	/**
	 * Creates an instance of ParameterizedRandomVariable and puts it in the
	 * PRV pool. 
	 * @param name The name of the parameterized random variable.
	 * @param variables An array of names of logical variables from this PRV.
	 */
	private void createPrv(String name, LogicalVariable ... variables) {
		prvPool.put(name, PRVs.getBooleanPrv(name, variables));
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
						.population()
						.individualAt(Integer
								.parseInt(parameter
										.split("=")[1])));
			} else if (parameter == "") {
				// PRV without parameters
			} else {
				throw new IllegalArgumentException("There no such logical " +
						" variable in pool: " + parameter.split("=")[0]);
			}
		}
		
		prvPool.put(name, PRVs.getBooleanPrv(name, terms.toArray(new Term[terms.size()])));
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
					InequalityConstraint.getInstance(
							variablesPool.get(firstTerm), 
							variablesPool.get(secondTerm)));
		} else {
			constraintsPool.put(
					firstTerm + " != " + secondTerm, 
					InequalityConstraint.getInstance(
							variablesPool.get(firstTerm), 
							variablesPool.get(firstTerm)
										 .population()
										 .individualAt(
												 Integer.parseInt(secondTerm))));
		}
	}
	
	/**
	 * @deprecated
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
				InequalityConstraint.getInstance(
						variablesPool.get(firstTerm), 
						variablesPool.get(secondTerm.split("=")[0])
									 .population()
									 .individualAt(
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
				OldCountingFormula.getInstance(
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
	 * @throws IllegalArgumentException If it cannot find a constraint or 
	 * parameterized logical variable in the pool.
	 */
	private void createSimpleParfactor(
			String name, 
			String constraints, 
			String prvs, 
			String factorName, 
			String values) 
			throws IllegalArgumentException {
		
		Set<Constraint> constraintsSet = new HashSet<Constraint>();
		if (constraints.length() > 0) {
			for (String constraint : constraints.split(";")) {
				if (constraintsPool.containsKey(constraint))
					constraintsSet.add(constraintsPool.get(constraint));
				else
					throw new IllegalArgumentException("Could not find "
							+ "constraint " + constraint);
			}
		}
		
		ArrayList<ParameterizedRandomVariable> variablesSet = 
			new ArrayList<ParameterizedRandomVariable>();
		for (String variable : prvs.split(";")) {
			if (prvPool.containsKey(variable))
				variablesSet.add(prvPool.get(variable));
			else
				throw new IllegalArgumentException("Could not find PRV " 
						+ variable);
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
						Binding.getInstance(
								variablesPool.get(firstTerm), 
								variablesPool.get(firstTerm)
											 .population()
											 .individualAt(
													 Integer.parseInt(secondTerm))));
			} else {
				bindings.add(
						Binding.getInstance(
								variablesPool.get(firstTerm), 
								variablesPool.get(secondTerm)));
				
			}
		}
		substitutionPool.put(setName, Substitution.getInstance(bindings));
	}
	
	/**
	 * Creates a random variable set based on a parameterized random variable
	 * and a set of constraints.
	 * @param name The name of the set. Used as a key to retrieve the instance
	 * created later
	 * @param prv The name of the PRV in PRV pool
	 * @param constraints A list of constraint names in the constraint pool
	 */
	private void createRandomVariableSet (
			String name, String prv, String ... constraints) {
		HashSet<Constraint> c = new HashSet<Constraint>(constraints.length);
		for (String constraintName : constraints) {
			if (constraintsPool.containsKey(constraintName)) {
				c.add(constraintsPool.get(constraintName));
			} else {
				throw new IllegalArgumentException("No such constraint in pool: "
						+ constraintName);
			}
		}
		
		if (!prvPool.containsKey(prv))
			throw new IllegalArgumentException("No such PRV in pool: " + prv);
		
		randomVariableSetPool.put(
				name, 
				RandomVariableSet.getInstance(
						prvPool.get(prv), 
						c));
	}
	
	/**
	 * Creates an instance of aggregation parfactor and puts it in the pool of
	 * aggregation parfactors.
	 * @param name The key to retrieve the created parfactor later
	 * @param parent The name of the parent PRV
	 * @param child The name of the child PRV
	 * @param constraints A list of constraint names separated by semicolon
	 * @param factorName The name of the factor
	 * @param values The values of the factor separated by semicolon
	 * @param operator A {@link BinaryOperator}
	 * @throws IllegalArgumentException If it cannot find a constraint, the
	 * parent PRV or the child PRV.
	 */
	private void createAggParfactor(
			String name,
			String parent,
			String child,
			String constraints,
			String factorName,
			String values,
			BooleanOperator operator) 
			throws IllegalArgumentException {
		
		Set<Constraint> constraintsSet = new HashSet<Constraint>();
		if (constraints.length() > 0) {
			for (String constraint : constraints.split(";")) {
				if (constraintsPool.containsKey(constraint)) {
					constraintsSet.add(constraintsPool.get(constraint));
				} else {
					throw new IllegalArgumentException("Could not find "
							+ "constraint " + constraint);
				}
			}
		}
		
		ParameterizedRandomVariable p = null;
		ParameterizedRandomVariable c = null;
		if (prvPool.containsKey(parent) && prvPool.containsKey(child)) {
			p = prvPool.get(parent);
			c = prvPool.get(child);
		} else {
			throw new IllegalArgumentException("Could not find PRV " 
					+ parent
					+ " or "
					+ child);
		}
		
		ArrayList<Number> factorValues = new ArrayList<Number>();
		for (String value : values.split(";")) {
			factorValues.add(Double.valueOf(value));
		}
		
		List<ParameterizedRandomVariable> plist = new ArrayList<ParameterizedRandomVariable>(1);
		plist.add(p);
		ParameterizedFactor f = ParameterizedFactor.getInstance(name, plist, factorValues);
		
		Builder builder = new AggregationParfactor.Builder(p, c, operator);
		builder.addConstraints(constraintsSet);
		builder.factor(f);
		AggregationParfactor ag = builder.build();
		
		aggParfactorPool.put(name, ag);
	}
	
	/**
	 * 
	 * Creates an instance of aggregation parfactor and puts it in the pool of
	 * aggregation parfactors.
	 * <br>
	 * The instance created has the identity factor 1.
	 * @param name The key to retrieve the created parfactor later
	 * @param parent The name of the parent PRV
	 * @param child The name of the child PRV
	 * @param operator A {@link BinaryOperator}
	 * @param constraints A list of constraint names separated by semicolon
	 * @throws IllegalArgumentException If it cannot find a constraint, the
	 * parent PRV or the child PRV.
	 */
	private void createAggParfactor(
			String name,
			String parent,
			String child,
			String constraints,
			BooleanOperator operator) 
			throws IllegalArgumentException {
		String factorName = "1";
		StringBuilder v = new StringBuilder();
		if (prvPool.containsKey(parent)) {
			for (int i = 0; i < prvPool.get(parent).getRangeSize(); i++) {
				v.append("1.0;");
			}
		} else {
			throw new IllegalArgumentException("Could not find PRV " + parent);
		}
		v.deleteCharAt(v.lastIndexOf(";"));
		createAggParfactor(name, parent, child, constraints, factorName, v.toString(), operator);
	}
	
	/**
	 * Creates an instance of generalized aggregation parfactor and puts it in 
	 * the pool of
	 * generalized aggregation parfactors.
	 * @param name The key to retrieve the created parfactor later
	 * @param parent The name of the parent PRV
	 * @param child The name of the child PRV
	 * @param context A list of context parameterized random variables
	 * @param constraints A list of constraint names separated by semicolon
	 * @param factorName The name of the factor
	 * @param values The values of the factor separated by semicolon
	 * @param operator A {@link BinaryOperator}
	 * @throws IllegalArgumentException If it cannot find a constraint, the
	 * parent PRV or the child PRV.
	 */
	private void createGenAggParfactor(
			String name,
			String parent,
			String child,
			String context,
			String constraints,
			String factorName,
			String values,
			BooleanOperator operator) 
			throws IllegalArgumentException {
		
		Set<Constraint> constraintsSet = new HashSet<Constraint>();
		if (constraints.length() > 0) {
			for (String constraint : constraints.split(";")) {
				if (constraintsPool.containsKey(constraint)) {
					constraintsSet.add(constraintsPool.get(constraint));
				} else {
					throw new IllegalArgumentException("Could not find "
							+ "constraint " + constraint);
				}
			}
		}
		
		ParameterizedRandomVariable p = null;
		ParameterizedRandomVariable c = null;
		if (prvPool.containsKey(parent) && prvPool.containsKey(child)) {
			p = prvPool.get(parent);
			c = prvPool.get(child);
		} else {
			throw new IllegalArgumentException("Could not find PRV " 
					+ parent
					+ " or "
					+ child);
		}
		
		ArrayList<Number> factorValues = new ArrayList<Number>();
		for (String value : values.split(";")) {
			factorValues.add(Double.valueOf(value));
		}
		
		List<ParameterizedRandomVariable> plist = new ArrayList<ParameterizedRandomVariable>(1);
		plist.add(p);
		if (context.length() > 0) {
			for (String contextPrv : context.split(";")) {
				if (prvPool.containsKey(contextPrv)) {
					plist.add(prvPool.get(contextPrv));
				} else {
					throw new IllegalArgumentException("Could not find "
							+ " context PRV " + contextPrv);
				}
			}
		}
		ParameterizedFactor f = ParameterizedFactor.getInstance(name, plist, factorValues);
		plist.remove(p);
		GeneralizedAggregationParfactor.Builder builder = new GeneralizedAggregationParfactor.Builder(p, c, operator, plist);
		builder.addConstraints(constraintsSet);
		builder.factor(f);
		GeneralizedAggregationParfactor ag = builder.build();
		
		genAggParfactorPool.put(name, ag);
	}
	
	/**
	 * 
	 * Creates an instance of generalized aggregation parfactor and puts it in 
	 * the pool of
	 * generalized aggregation parfactors.
	 * <br>
	 * The instance created has the identity factor 1.
	 * @param name The key to retrieve the created parfactor later
	 * @param parent The name of the parent PRV
	 * @param child The name of the child PRV
	 * @param context A list of context PRVs
	 * @param operator A {@link BinaryOperator}
	 * @param constraints A list of constraint names separated by semicolon
	 * @throws IllegalArgumentException If it cannot find a constraint, the
	 * parent PRV or the child PRV.
	 */
	private void createGenAggParfactor(
			String name,
			String parent,
			String child,
			String context,
			String constraints,
			BooleanOperator operator) 
			throws IllegalArgumentException {
		String factorName = "1";
		StringBuilder v = new StringBuilder();
		if (prvPool.containsKey(parent)) {
			int factorSize = prvPool.get(parent).getRangeSize();
			if (context.length() > 0) {
				for (String contextPrv : context.split(";")) {
					if (prvPool.containsKey(contextPrv)) {
						factorSize *= prvPool.get(contextPrv).getRangeSize();
					} else {
						throw new IllegalArgumentException("Could not find PRV " + contextPrv);
					}
				}
			}
			for (int i = 0; i < factorSize; i++) {
				v.append("1.0;");
			}
		} else {
			throw new IllegalArgumentException("Could not find PRV " + parent);
		}
		v.deleteCharAt(v.lastIndexOf(";"));
		createGenAggParfactor(name, parent, child, context, constraints, factorName, v.toString(), operator);
	}
	
	/**
	 * Creates an instance of {@link Binding} and puts it in the binding
	 * pool. 
	 * <br>
	 * The first term must exist in the PRV pool, and the second term must
	 * be a valid index for the first term population. Otherwise, an
	 * {@link IllegalArgumentException} is thrown.
	 * 
	 * @param name The key to retrieve this binding later
	 * @param firstTerm A logical variable
	 * @param secondTerm The index of the individual in first term population.
	 * @throws IllegalArgumentException If the first term does not exist in
	 * the pool or if the second term is an invalid index.
	 */
	private void createBinding(String name, String firstTerm, int secondTerm) 
			throws IllegalArgumentException {
		
		if (!variablesPool.containsKey(firstTerm)) {
			throw new IllegalArgumentException("No such PRV: " + firstTerm);
		}
		
		LogicalVariable lv = variablesPool.get(firstTerm);
		
		if (lv.population().size() <= secondTerm
				|| secondTerm < 0) {
			throw new IllegalArgumentException("Not a valid individual: " 
					+ secondTerm
					+ " because "
					+ firstTerm
					+ " has a population of size "
					+ lv.population().size());
		}
		
		Term c = lv.population().individualAt(secondTerm);
		Binding b = Binding.getInstance(lv, c);
		
		bindingPool.put(name, b);
	}
	
	/**
	 * Creates an instance of {@link Binding} and puts it in the binding
	 * pool. 
	 * <br>
	 * The first term  and the second term must exist in the PRV pool. 
	 * Otherwise, an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param name The key to retrieve this binding later
	 * @param firstTerm A logical variable
	 * @param secondTerm A logical variable
	 * @throws IllegalArgumentException If the first term or the second term
	 * do not exist in the pool.
	 */
	private void createBinding(String name, String firstTerm, String secondTerm) 
			throws IllegalArgumentException {
		
		if (!variablesPool.containsKey(firstTerm)) {
			throw new IllegalArgumentException("No such PRV: " + firstTerm);
		}
		if (!variablesPool.containsKey(secondTerm)) {
			throw new IllegalArgumentException("No such PRV: " + secondTerm);
		}
		
		LogicalVariable lv = variablesPool.get(firstTerm);
		Term t = variablesPool.get(secondTerm);
		Binding b = Binding.getInstance(lv, t);
		
		bindingPool.put(name, b);
	}
	
	/**
	 * Creates a list of parfactors. The specified parfactors must be in
	 * the pool.
	 * @param p1 The name of the first parfactor to add.
	 * @param parfactors The name of the remaining parfactors to add.
	 */
	private void createParfactorList(String name, String ... parfactors) {
		ArrayList<Parfactor> pList = new ArrayList<Parfactor>();
		
		if (parfactors == null || parfactors.length == 0) {
			throw new IllegalArgumentException("Empty list of parfactors!");
		}
		for (String p : parfactors) {
			if (aggParfactorPool.containsKey(p)) {
				pList.add(getAggParfactor(p));
			} else if (genAggParfactorPool.containsKey(p)) {
				pList.add(getGenAggParfactor(p));
			} else if (simpleParfactorPool.containsKey(p)) {
				pList.add(getSimpleParfactor(p));
			} else {
				throw new IllegalArgumentException("No such parfactor: " + p);
			}
		}
		this.parfactorListPool.put(name, pList);
	}
	
	/**
	 * Converts an array of doubles to a string that obeys the convention
	 * to initialize parfactors.
	 * @param v An array of doubles.
	 * @return The specified array converted to string
	 */
	private String toString(double[] v) {
		String vs = Arrays.toString(v);
		vs = vs.substring(1, vs.length() - 1);
		vs = vs.replaceAll(", ", ";");
		return vs;
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
	 * <br>
	 * Changed constraint A &ne; B to A &ne; x0 to keep the normal form.
	 */
	public void setExample2_16() {
		
		createLogicalVariable("A", "x", 3);
		createLogicalVariable("B", "x", 3);
		
		createPrv("f", variablesPool.get("A"));
		createPrv("h", variablesPool.get("B"));
		
		createPrvFromSubstitution("f", "A/1");
		createPrvFromSubstitution("f", "A/2");
		
		createConstraint("A", "0");
		createConstraint("A", "1");
		
		createCountingFormula("#.A:{A!=x0}[f(A)]", "A", "f", "A != 0");
		//createCountingFormula("#.A:{A!=x0,A!=x1}[f(A)]", "A", "f", "A != 0", "A != 1"); 
		
		// Initial parfactor
		createSimpleParfactor("g1", "", "#.A:{A!=x0}[f(A)];h", "F", "0.2;0.3;0.5;0.7;0.11;0.13");
		
		// Parfactor resulting from expanding g1 on term x1
		createSimpleParfactor("g1'", "", "f[A/2];f[A/1];h", "F'", "0.2;0.3;0.5;0.7;0.5;0.7;0.11;0.13");
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
	 * The example has been changed to become more consistent:
	 * <br>
	 * <br>
	 * &Phi;<sub>0</sub> = {
	 * < { X &ne; x2 }, { f(X, Y), q(Y) }, F1 >, [1] <br>
	 * < { X &ne; x1, Z &ne; y1 }, { p(X), f(x1, Z) }, F2 > } [2] <br>
	 * <br>
	 * The result will be:
	 * <br>
	 * <br>
	 * &Phi;<sub>3</sub> = {
	 * < { X3 &ne; x1, X4 &ne; y1 }, { p(X3), f(x1, X4) }, F2 >, [3] <br>
	 * < { X1 &ne; x1, X1 &ne; x2 }, { f(X1, X2), q(X2) }, F1 >, [4] <br>
	 * < {  }, { f(x1, y1), q(y1) }, F1 >, [5] <br>
	 * < { X4 &ne; y1 }, { f(x1, X4), q(X4) }, F1 >, [6] <br>
	 * <br>
	 * Parfactor [4] can also be <br>
	 * < { X1 &ne; x1, X1 &ne; x2 }, { f(X1, X4), q(X4) }, F1 >, [4'] <br>
	 * depending on the order that the split is made.
	 * <br>
	 * <br>
	 * The objective is to make f(X, Y) in [1] and f(x1, Z) in [2] 
	 * represent the same set of random variables.
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
		createPrv("p", variablesPool.get("X"));
		createConstraint("Z", "1");
		createConstraint("X", "1");
		createSimpleParfactor("g2", "X != 1;Z != 1", "p;f", "F", "0.2;0.3;0.5;0.7");
		
		// Sets the correct result
		
		createLogicalVariable("X2", "x", 5);
		createLogicalVariable("X1", "y", 6); // changed names so the test coincides with name generator
		createLogicalVariable("X3", "x", 5);
		createLogicalVariable("X4", "y", 6);
		
		// parfactor [3]
		createConstraint("X", "1");
		createConstraint("Z", "1");
		createPrv("p", "X");
		createPrv("f", "X=1", "Z");
		createSimpleParfactor("g3", "X != 1;Z != 1", "p;f", "F2", "0.2;0.3;0.5;0.7");
		
		// parfactor [4]
		createConstraint("X", "1");
		createConstraint("X", "2");
		createPrv("f", "X", "Z");
		createPrv("q", "Z");
		createSimpleParfactor("g4", "X != 1;X != 2", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
		// parfactor [5]
		createPrv("f", "X=1", "Y=1");
		createPrv("q", "Y=1");
		createSimpleParfactor("g5", "", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
		// parfactor [6]
		createConstraint("Z", "1");
		createPrv("f", "X=1", "Z");
		createPrv("q", "Z");
		createSimpleParfactor("g6", "Z != 1", "f;q", "F1", "0.2;0.3;0.5;0.7");
		
	}
	
	/**
	 * Creates data structures to test unification between two counting
	 * formulas.
	 * <br>
	 * The input is
	 * <br>
	 * &Phi;<sub>0</sub> = {<br>
	 * < { }, { #<sub>A:{A &ne; x1}</sub>[f(A)], h(A) }, F1 >, [1] <br>
	 * < { }, { #<sub>A:{A &ne; x2}</sub>[f(A)] }, F2 > } [2] <br>
	 * <br>
	 * Where D(A) = {x1,x2,x3}.
	 * The result is
	 * <br>
	 * &Phi;<sub>1</sub> = {<br>
	 * < { }, { f(x0), f(x1) }, F2' >, [3] <br>
	 * < { A &ne; x2 }, { f(x0), f(x2), h(A) }, F1' > [4] <br>
	 * < { }, { f(x0), f(x2), h(x2) }, F1' > } [5] <br>
	 * 
	 */
	public void setUnificationTestWithTwoCountingFormulas() {
		createLogicalVariable("A", "x", 3);
		createConstraint("A", "1");
		createPrv("f", "A");
		createCountingFormula("#.A[f]", "A", "f", "A != 1");
		createPrv("h", "A");
		createSimpleParfactor("g1", "", "#.A[f];h", "F1", "2;3;5;7;11;13");
		
		createConstraint("A", "2");
		createCountingFormula("#.A[f]", "A", "f", "A != 2");
		createSimpleParfactor("g2", "", "#.A[f]", "F2", "2;3;5");

		createPrvFromSubstitution("f", "A/0");
		createPrvFromSubstitution("f", "A/1");
		createPrvFromSubstitution("f", "A/2");
		createPrvFromSubstitution("h", "A/2");

		// parfactor [3]
		createSimpleParfactor("g3", "", "f[A/0];f[A/1]", "F2", "2;3;3;5");
		
		// parfactor [4]
		createConstraint("A", "2");
		createSimpleParfactor("g4", "A != 2", "f[A/0];f[A/2];h", "F1", "2;3;5;7;5;7;11;13");
		
		// parfactor [5]
		createSimpleParfactor("g5", "", "f[A/0];f[A/2];h[A/2]", "F1", "2;3;5;7;5;7;11;13");
	}

	/**
	 * Creates data structures to test unification between two counting
	 * formulas in a bigger population.
	 * <br>
	 * The input is
	 * <br>
	 * &Phi;<sub>0</sub> = {<br>
	 * < { }, { #<sub>A:{A &ne; x1}</sub>[f(A)], h(A) }, F1 >, [1] <br>
	 * < { }, { #<sub>A:{A &ne; x2}</sub>[f(A)] }, F2 > } [2] <br>
	 * <br>
	 * Where D(A) = {x1,x2,x3,x4}.
	 * The result is
	 * <br>
	 * &Phi;<sub>1</sub> = {<br>
	 * < { }, { #<sub>A:{A &ne; x1, A &ne; x2}</sub>[f(A)], f(x1) }, F2' >, [3] <br>
	 * < { A &ne; x2 }, { #<sub>A:{A &ne; x1, A &ne; x2}</sub>[f(A)], f(x2), h(A) }, F1' > [4] <br>
	 * < { }, { #<sub>A:{A &ne; x1, A &ne; x2}</sub>[f(A)], f(x2), h(x2) }, F1' > } [5] <br>
	 * 
	 */
	public void setUnificationTestWithTwoCountingFormulasBigPopulation() {
		createLogicalVariable("A", "x", 4);
		createConstraint("A", "1");
		createPrv("f", "A");
		createCountingFormula("#.A[f]", "A", "f", "A != 1");
		createPrv("h", "A");
		createSimpleParfactor("g1", "", "#.A[f];h", "F1", "2;3;5;7;11;13;17;23");
		
		createConstraint("A", "2");
		createCountingFormula("#.A[f]", "A", "f", "A != 2");
		createSimpleParfactor("g2", "", "#.A[f]", "F2", "2;3;5;7");

		createConstraint("A", "1");
		createConstraint("A", "2");
		createPrv("f", "A");
		createCountingFormula("#.A[f]", "A", "f", "A != 1", "A != 2");
		createPrv("f", "A=1");
		createSimpleParfactor("g3", "", "#.A[f];f", "F2", "2;3;3;5;5;7");
		
		createPrv("f", "A=2");
		createPrv("h", "A");
		createSimpleParfactor("g4", "A != 2", "#.A[f];f;h", "F1", "2;3;5;7;5;7;11;13;11;13;17;23");
		
		createPrv("h", "A=2");
		createSimpleParfactor("g5", "", "#.A[f];f;h", "F1", "2;3;5;7;5;7;11;13;11;13;17;23");
	}
	
	/**
	 * Creates data structures to test unification between a counting formula
	 * and a standard parameterized random variable.
	 * <br>
	 * The input is
	 * <br>
	 * &Phi;<sub>0</sub> = {<br>
	 * < { }, { #<sub>A:{A &ne; x1}</sub>[f(A)], h(A) }, F1 >, [1] <br>
	 * < { }, { f(A) }, F2 > } [2] <br>
	 * <br>
	 * The result is
	 * <br>
	 * &Phi;<sub>1</sub> = {<br>
	 * < { }, { #<sub>X2:{X2 &ne; x1}</sub>[f(X2)], h(X2) }, F1 >, [3] <br>
	 * < { }, { f(x1) }, F2 >, [4] <br>
	 * < { X2 &ne; x1 }, { f(X2) }, F2 > } [5] <br>
	 * 
	 */
	public void setUnificationTestWithCountingFormulaAndPrv() {
		createLogicalVariable("A", "x", 3);
		createConstraint("A", "1");
		createPrv("f", "A");
		createPrv("h", "A");
		createCountingFormula("#.A[f]", "A", "f", "A != 1");
		createSimpleParfactor("g1", "", "#.A[f];h", "F1", "2;3;5;7;11;13");
		createSimpleParfactor("g2", "", "f", "F2", "2;3");
		
		createConstraint("A", "1");
		createPrv("f", "A");
		createPrv("h", "A");
		createCountingFormula("#.A[f]", "A", "f", "A != 1");
		createSimpleParfactor("g3", "", "#.A[f];h", "F1", "2;3;5;7;11;13");
		createPrv("f", "A=1");
		createSimpleParfactor("g4", "", "f", "F2", "2;3");
		createPrv("f", "A");
		createSimpleParfactor("g5", "A != 1", "f", "F2", "2;3");		
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
	
	public void setSumOutCountingFormulaWithCardinality1Test() {
		createLogicalVariable("A", "x", 1);
		createLogicalVariable("B", "x", 3);
		createPrv("f", "A");
		createPrv("h", "B");
		createCountingFormula("#.A[f]", "A", "f");
		createSimpleParfactor("g", "", "#.A[f];h", "F", "1;10;100;1000");
		createSimpleParfactor("g_answer", "", "h", "F", "101;1010");
	}
	
	public void setSumOutCountingFormulaWithCardinality2Test() {
		createLogicalVariable("A", "x", 2);
		createLogicalVariable("B", "x", 3);
		createPrv("f", "A");
		createPrv("h", "B");
		createCountingFormula("#.A[f]", "A", "f");
		createSimpleParfactor("g", "", "#.A[f];h", "F", "1;10;100;1000;10000;100000");
		createSimpleParfactor("g_answer", "", "h", "F", "10201;102010");
	}
	
	public void setSumOutCountingFormulaWithCardinality10Test() {
		createLogicalVariable("A", "x", 10);
		createLogicalVariable("B", "x", 3);
		createPrv("f", "A");
		createPrv("h", "B");
		createCountingFormula("#.A[f]", "A", "f");
		createSimpleParfactor("g", "", "#.A[f];h", "F", "1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1");
		createSimpleParfactor("g_answer", "", "h", "F", "1024;1024");
	}
	
	/**
	 * Creates data structures for example 2.13 of Kisynski (2010).
	 */
	public void setExample2_13() {
		createLogicalVariable("A", "x", 3);
		createLogicalVariable("B", "x", 3);
		createPrv("f", "A");
		createPrv("h", "B");
		createConstraint("A", "B");
		createCountingFormula("#.A[f]", "A", "f", "A != B");
		createSimpleParfactor("g", "", "#.A[f];h", "F", "1;10;100;1000;10000;100000");
		createSimpleParfactor("g_answer", "", "h", "F", "10201;102010");
	}
	
	
	/**
	 * Creates data structures for example in section 2.5.2.7 of Kisynski (2010).
	 * Only sets &Phi; and &Phi;<sub>1</sub> are put in the pool.
	 */
	public void setExample2_5_2_7forShattering() {
		createLogicalVariable("Lot", "lot", 15);
		
		// parfactor [01]
		createPrv("rain", "");
		createSimpleParfactor("g1", "", "rain", "F1", "0.8;0.2");
				
		// parfactor [02]
		createPrv("sprinkler", "Lot");
		createSimpleParfactor("g2", "", "sprinkler", "F2", "0.6;0.4");
		
		// parfactor [03]
		createPrv("wet_grass", "Lot");
		createSimpleParfactor("g3", "", "rain;sprinkler;wet_grass", "F3", "1.0;0.0;0.2;0.8;0.1;0.9;0.01;0.99");
		
		// parfactor [04]
		createPrv("wet_grass", "Lot=1");
		createSimpleParfactor("g4", "", "wet_grass", "F4", "0;1");
		
		// parfactor [05]
		createPrv("sprinkler", "Lot=1");
		createSimpleParfactor("g5", "", "rain;sprinkler;wet_grass", "F3", "1.0;0.0;0.2;0.8;0.1;0.9;0.01;0.99");
				
		// parfactor [06]
		createPrv("sprinkler", "Lot");
		createPrv("wet_grass", "Lot");
		createConstraint("Lot", "1");
		createSimpleParfactor("g6", "Lot != 1", "rain;sprinkler;wet_grass", "F3", "1.0;0.0;0.2;0.8;0.1;0.9;0.01;0.99");
		
		// parfactor [07]
		createPrv("sprinkler", "Lot=1");
		createSimpleParfactor("g7", "", "sprinkler", "F2", "0.6;0.4");
				
		// parfactor [08]
		createPrv("sprinkler", "Lot");
		createSimpleParfactor("g8", "Lot != 1", "sprinkler", "F2", "0.6;0.4");		
	}
	
	/**
	 * Creates data structures for example in section 2.5.2.7 of Kisynski (2010).
	 * Only sets &Phi;<sub>4</sub> and &Phi;<sub>5</sub> are put in the pool.
	 * <br>
	 * Parfactors [09] and [12] have been changed to use integer numbers, 
	 * otherwise comparison between doubles would complicate the test.
	 * Precision is not the objective of this test.
	 * <br>
	 * The population of logical variable Lot is set to 16 individuals. Factor
	 * F7 will have 32 rows.
	 * 
	 */
	public void setExample2_5_2_7forCountingConvert() {
		
		createLogicalVariable("Lot", "lot", 16);
		
		// parfactor [01]
		createPrv("rain", "");
		createSimpleParfactor("g1", "", "rain", "F1", "0.8;0.2");
		
		// parfactor [09]
		createPrv("wet_grass", "Lot");
		createConstraint("Lot", "1");
		createSimpleParfactor("g9", "Lot != 1", "rain;wet_grass", "F5", "2;3;5;7");
		
		// parfactor [11]
		createSimpleParfactor("g11", "", "rain", "F6", "0.32;0.936");
		
		// parfactor [12]
		createCountingFormula("#.Lot[wet_grass]", "Lot", "wet_grass", "Lot != 1");
		createSimpleParfactor("g12", "", "rain;#.Lot[wet_grass]", "F7", "32768;49152;73728;110592;165888;248832;373248;559872;839808;1259712;1889568;2834352;4251528;6377292;9565938;14348907;30517578125;42724609375;59814453125;83740234375;117236328125;164130859375;229783203125;321696484375;450375078125;630525109375;882735153125;1235829214375;1730160900125;2422225260175;3391115364245;4747561509943");
		
	}
	
	public void setPropositionalizationMacroTest() {
		
		createLogicalVariable("Lot", "lot", 3);
		
		// parfactor [01]
		createPrv("rain", "");
		createSimpleParfactor("g1", "", "rain", "F1", "0.8;0.2");
				
		// parfactor [02]
		createPrv("sprinkler", "Lot");
		createSimpleParfactor("g2", "", "sprinkler", "F2", "0.6;0.4");
		
		// parfactor [03]
		createPrv("wet_grass", "Lot");
		createSimpleParfactor("g3", "", "rain;sprinkler;wet_grass", "F3", "1.0;0.0;0.2;0.8;0.1;0.9;0.01;0.99");
		
		// parfactor [04]
		createPrv("wet_grass", "Lot=1");
		createSimpleParfactor("g4", "", "wet_grass", "F4", "0;1");
		
		// parfactor [02] after propositionalization
		createPrv("sprinkler", "Lot=0");
		createSimpleParfactor("g2.0", "", "sprinkler", "F2", "0.6;0.4");
		createPrv("sprinkler", "Lot=1");
		createSimpleParfactor("g2.1", "", "sprinkler", "F2", "0.6;0.4");
		createPrv("sprinkler", "Lot=2");
		createSimpleParfactor("g2.2", "", "sprinkler", "F2", "0.6;0.4");
		
		// parfactor [03] after propositinalization
		createPrv("sprinkler", "Lot=0");
		createPrv("wet_grass", "Lot=0");
		createSimpleParfactor("g3.0", "", "rain;sprinkler;wet_grass", "F3", "1.0;0.0;0.2;0.8;0.1;0.9;0.01;0.99");
		createPrv("sprinkler", "Lot=1");
		createPrv("wet_grass", "Lot=1");
		createSimpleParfactor("g3.1", "", "rain;sprinkler;wet_grass", "F3", "1.0;0.0;0.2;0.8;0.1;0.9;0.01;0.99");
		createPrv("sprinkler", "Lot=2");
		createPrv("wet_grass", "Lot=2");
		createSimpleParfactor("g3.2", "", "rain;sprinkler;wet_grass", "F3", "1.0;0.0;0.2;0.8;0.1;0.9;0.01;0.99");
	}
	
	public void setFullExpandTest() {
		
		createLogicalVariable("A", "x", 3);
		createLogicalVariable("B", "x", 3);
		
		createPrv("f", "A");
		createSimpleParfactor("g1", "", "f", "F1", "0.4;0.6");
		createPrv("h", "B");
		createConstraint("A", "1");
		createCountingFormula("#.A[f]", "A", "f", "A != 1");
		createSimpleParfactor("g2", "", "#.A[f];h", "F2", "1;2;3;4;5;6");
		
		createPrv("f", "A=0");
		createSimpleParfactor("g1.0", "", "f", "F1", "0.4;0.6");
		createPrv("f", "A=1");
		createSimpleParfactor("g1.1", "", "f", "F1", "0.4;0.6");
		createPrv("f", "A=2");
		createSimpleParfactor("g1.2", "", "f", "F1", "0.4;0.6");
		
		createPrv("f", "A");
		createPrvFromSubstitution("f", "A/0");
		createPrvFromSubstitution("f", "A/2");
		createSimpleParfactor("g3", "", "f[A/0];f[A/2];h", "F3", "1;2;3;4;3;4;5;6");
		
		
	}
	
	public void setGlobalSumOutTest() {
		
		createLogicalVariable("Lot", "lot", 10);
		
		createPrv("rain", "");
		createPrv("sprinkler", "Lot");
		createPrv("wet_grass", "Lot");
		createPrv("another_rain", "");
		createPrvFromSubstitution("sprinkler", "Lot/1");
		createPrvFromSubstitution("wet_grass", "Lot/1");
		
		createConstraint("Lot", "1");
		
		createSimpleParfactor("g1", "", "rain", "F1", "1;2");
		createSimpleParfactor("g4", "", "wet_grass[Lot/1]", "F4", "1;2");
		createSimpleParfactor("g5", "", "rain;sprinkler[Lot/1];wet_grass[Lot/1]", "F3", "1;2;3;4;5;6;7;8");
		createSimpleParfactor("g6", "Lot != 1", "rain;sprinkler;wet_grass", "F3", "1;2;3;4;5;6;7;8");
		createSimpleParfactor("g7", "", "sprinkler[Lot/1]", "F2", "2;3");
		createSimpleParfactor("g8", "Lot != 1", "sprinkler", "F2", "2;3");
		
		createSimpleParfactor("g9", "Lot != 1", "rain;wet_grass", "F9", "11;16;31;36");
		createSimpleParfactor("g10", "", "rain;wet_grass[Lot/1]", "F9", "11;16;31;36");
		
		createSimpleParfactor("g11", "", "rain", "F6", "3;5");
		createSimpleParfactor("g12", "", "rain;another_rain", "F7", "2;3;5;7");
		
		createSimpleParfactor("g13", "", "another_rain", "F8", "56;79");
	}
	
	/**
	 * Creates all parfactors from section 2.5.2.7 of Kisynski (2010).
	 * @param populationSize The size of the population of logival variable
	 * 'Lot'.
	 */
	public void setExample2_5_2_7(int populationSize) {
		
		createLogicalVariable("Lot", "lot", populationSize);
		
		createPrv("rain", "");
		createPrv("sprinkler", "Lot");
		createPrv("wet_grass", "Lot");
		createPrvFromSubstitution("sprinkler", "Lot/1");
		createPrvFromSubstitution("wet_grass", "Lot/1");
		
		createConstraint("Lot", "1");
		
		createCountingFormula("#.Lot[wet_grass]", "Lot", "wet_grass", "Lot != 1");
		
		createRandomVariableSet("wg:lot", "wet_grass", "Lot != 1");
		
		// factor components
		
		double[] f1 = {0.8, 0.2};
		double[] f2 = {0.6, 0.4};
		double[] f3 = {1.0, 0.0, 0.2, 0.8, 0.1, 0.9, 0.01, 0.99};
		double[] f4 = {0.0, 1.0};
		
		double[] f2xf3 = new double[8];
		for (int i = 0; i < f2xf3.length; i++) {
			f2xf3[i] = f2[(i / 2) % 2] * f3[i];
		}
		
		double[] f5 = new double[4];
		f5[0] = f2xf3[0] + f2xf3[2];
		f5[1] = f2xf3[1] + f2xf3[3];
		f5[2] = f2xf3[4] + f2xf3[6];
		f5[3] = f2xf3[5] + f2xf3[7];
		
		double[] f4xf5 = new double[4];
		for (int i = 0; i < f4xf5.length; i++) {
			f4xf5[i] = f4[i % 2] * f5[i];
		}
		
		double[] f6 = new double[2];
		f6[0] = f4xf5[0] + f4xf5[1];
		f6[1] = f4xf5[2] + f4xf5[3];

		int n = populationSize;
		double[] f7 = new double[2 * n];
		for (int i = 0; i < n; i++) {
			f7[i] = Math.pow(f5[0], n - 1 - i) * Math.pow(f5[1], i);
			f7[n + i] = Math.pow(f5[2], n - 1 - i) * Math.pow(f5[3], i);
		}
		
		double[] f1xf6xf7 = new double[2 * n];
		for (int i = 0; i < 2 * n; i++) {
			f1xf6xf7[i] = f1[i / n] * f6[i / n] * f7[i];
		}

		double[] f8 = new double[n];
		for (int i = 0; i < n; i++) {
			f8[i] = f1xf6xf7[i] + f1xf6xf7[n + i];
		}
		
		// parfactors
		
		createSimpleParfactor("g1", "", "rain", "F1", toString(f1));
		createSimpleParfactor("g2", "", "sprinkler", "F2", toString(f2));
		createSimpleParfactor("g3", "", "rain;sprinkler;wet_grass", "F3", toString(f3));
		createSimpleParfactor("g4", "", "wet_grass[Lot/1]", "F4", toString(f4));
		createSimpleParfactor("g5", "", "rain;sprinkler[Lot/1];wet_grass[Lot/1]", "F3", toString(f3));
		createSimpleParfactor("g6", "Lot != 1", "rain;sprinkler;wet_grass", "F3", toString(f3));
		createSimpleParfactor("g7", "", "sprinkler[Lot/1]", "F2", toString(f2));
		createSimpleParfactor("g8", "Lot != 1", "sprinkler", "F2", toString(f2));
		createSimpleParfactor("g9", "Lot != 1", "rain;wet_grass", "F5", toString(f5));
		createSimpleParfactor("g10", "", "rain;wet_grass[Lot/1]", "F5", toString(f5));
		createSimpleParfactor("g11", "", "rain", "F6", toString(f6));
		createSimpleParfactor("g12", "", "rain;#.Lot[wet_grass]", "F7", toString(f7));
		createSimpleParfactor("g13", "", "#.Lot[wet_grass]", "F8", toString(f8));
	}
	
	/**
	 * Conversion to parfactors.
	 * <br>
	 * Creates parfactors used in example 3.9 of Kisynski (2010)
	 * @param populationSize  The size of the population of logical variable
	 * Person
	 */
	public void setExample3_9(int populationSize) {
		createLogicalVariable("Person", "p", populationSize);
		createPrv("matched_6", "Person");
		createPrv("jackpot_won", "");
		createCountingFormula("#.Person[matched_6]", "Person", "matched_6");
		
		double [] f = new double[2 * (populationSize + 1)];
		f[0] = 1.0;
		f[1] = 0.0;
		for (int i = 2; i < f.length; i++) {
			f[i] = (double)(i % 2);
		}
		
		createAggParfactor("ag", "matched_6", "jackpot_won", "", Or.OR);

		createSimpleParfactor("g1", "", "#.Person[matched_6];jackpot_won", "F#", toString(f));
		createSimpleParfactor("g2", "", "matched_6", "1", "1.0;1.0");
	}
	
	/**
	 * Creates all data structures to perform split tests on aggregation
	 * parfactors.
	 */
	public void setSplitAggParfactorTest() {
		createLogicalVariable("A", "x", 10);
		createLogicalVariable("B", "x", 10);
		createPrv("p", "A", "B");
		createPrv("c", "B");
		createPrv("c'", "B");
		createPrvFromSubstitution("p", "A/B");
		createPrvFromSubstitution("p", "B/A");
		createPrvFromSubstitution("p", "A/1");
		createPrvFromSubstitution("p", "B/1");
		createPrvFromSubstitution("c", "B/1");
		createBinding("B/1", "B", 1);
		createBinding("A/1", "A", 1);
		createBinding("B/A", "B", "A");
		createBinding("A/B", "A", "B");
		createConstraint("A", "1");
		createConstraint("A", "2");
		createConstraint("B", "1");
		createConstraint("B", "2");
		createConstraint("A", "B");
		createConstraint("B", "A");
		createAggParfactor("ag1", "p", "c", "", Or.OR);
		createAggParfactor("ag2", "p", "c", "A != 2;B != 2", Or.OR);
		
		createAggParfactor("g1", "p[B/1]", "c[B/1]", "", Or.OR);
		createAggParfactor("g2", "p", "c", "B != 1", Or.OR);
		createParfactorList("m1", "g1", "g2");
		
		createAggParfactor("g3", "p", "c'", "A != 1", Or.OR);
		
		double [] f = {1.0, 
					   0.0,
					   0.0,
					   1.0,
					   0.0,
					   1.0,
					   0.0,
					   1.0};
		
		createSimpleParfactor("g4", "", "p[A/1];c';c", "Fc", toString(f));
		createParfactorList("m2", "g3", "g4");
		
		createAggParfactor("g5", "p", "c'", "A != B", Or.OR);
		createSimpleParfactor("g6", "", "p[A/B];c';c", "Fc", toString(f));
		createParfactorList("m3", "g5", "g6");
		
		createAggParfactor("g7", "p", "c'", "A != B", Or.OR);
		createSimpleParfactor("g8", "", "p[B/A];c';c", "Fc", toString(f));
		createParfactorList("m4", "g7", "g8");
		
		createAggParfactor("g9",  "p[B/1]", "c[B/1]", "A != 2", Or.OR);
		createAggParfactor("g10", "p", "c", "B != 1;B != 2;A != 2", Or.OR);
		createParfactorList("m5", "g9", "g10");
				
		createAggParfactor("g11", "p", "c'", "A != 1;A != 2;B != 2", Or.OR);
		createSimpleParfactor("g12", "B != 2", "p[A/1];c';c", "Fc", toString(f));
		createParfactorList("m6", "g11", "g12");
				
		createAggParfactor("g13", "p", "c'", "A != B;A != 2;B != 2", Or.OR);
		createSimpleParfactor("g14", "B != 2", "p[A/B];c';c", "Fc", toString(f));
		createParfactorList("m7", "g13", "g14");
		
		createAggParfactor("g15", "p", "c'", "B != A;A != 2;B != 2", Or.OR);
		createSimpleParfactor("g16", "A != 2", "p[B/A];c';c", "Fc", toString(f));
		createParfactorList("m8", "g15", "g16");
	}
	
	public void setMultiplicationAggParfactor() {
		createLogicalVariable("Person", "p", 100);
		createPrv("played", "Person");
		createPrv("matched_6", "Person");
		createPrv("jackpot_won", "");
		
		double [] fPlayed = {
				0.95,
				0.05
		};
		createSimpleParfactor("g1", "", "played", "Fplayed", toString(fPlayed));
		
		double [] fMatched = {
				1.0,
				0.0,
				0.99999993,
				0.00000007
		};
		createSimpleParfactor("g2", "", "played;matched_6", "Fmatched", toString(fMatched));
		
		createAggParfactor("g3", "matched_6", "jackpot_won", "", Or.OR);
		
		double [] fSum = {
				0.9999999965,
				0.0000000035
		};
		createAggParfactor("g4", "matched_6", "jackpot_won", "", "Fsum", toString(fSum), Or.OR);
		
		// second test
		createLogicalVariable("A", "x", 100);
		createLogicalVariable("B", "x", 100);
		createPrv("p", "A", "B");
		createPrv("c", "B");
		createConstraint("A", "1");
		createConstraint("A", "2");
		createConstraint("A", "B");
		createConstraint("B", "3");
		
		double [] f1 = {0.1234, 0.9876};
		createSimpleParfactor("h1", "A != 1;A != 2;A != B;B != 3", "p", "F1", toString(f1));
				
		double [] f2 = {0.5425, 0.6832};
		createAggParfactor("h2", "p", "c", "A != 1;A != 2;A != B;B != 3", "F2", toString(f2), Or.OR);
		
		double [] f3 = new double[2];
		f3[0] = f1[0] * f2[0];
		f3[1] = f1[1] * f2[1];
		createAggParfactor("h3", "p", "c", "A != 1;A != 2;A != B;B != 3", "F3", toString(f3), Or.OR);
	}
	
	public void setSumOutAggParfactorTest() {
		
		createLogicalVariable("Person", "p", 5);
		createPrv("matched_6", "Person");
		createPrv("jackpot_won", "");
		double [] fSum = {
				0.9999999965,
				0.0000000035
		};
		createAggParfactor("g1", "matched_6", "jackpot_won", "", "Fsum", toString(fSum), Or.OR);
		
		// not elegant, but more readable
		double [] f0 = new double[2];
		double [] f1 = new double[2];
		double [] f2 = new double[2];
		
		f0[0] = fSum[0];
		f0[1] = fSum[1];
		
		f1[0] = f0[0] * f0[0];
		f1[1] = f0[0]*f0[1] + f0[1]*f0[0] + f0[1]*f0[1];
		
		f2[0] = fSum[0] * f1[0] * f1[0];
		f2[1] = fSum[0] * f1[0] * f1[1]
		      + fSum[0] * f1[1] * f1[0]
			  + fSum[0] * f1[1] * f1[1]
		      + fSum[1] * f1[0] * f1[0]
		      + fSum[1] * f1[0] * f1[1]
		      + fSum[1] * f1[1] * f1[0]
   		      + fSum[1] * f1[1] * f1[1];                       
		createSimpleParfactor("g2", "", "jackpot_won", "F2", toString(f2));                   
		                
	}
	
	/* ************************************************************************
	 *      Generealized Aggregation Parfactors
	 * ************************************************************************/
	
	public void setGenAggParfactorConversionTest(int populationSize) {
		createLogicalVariable("Person", "p", populationSize);
		createPrv("matched_6", "Person");
		createPrv("jackpot_won", "");
		createCountingFormula("#.Person[matched_6]", "Person", "matched_6");
		
		double [] f = new double[2 * (populationSize + 1)];
		f[0] = 1.0;
		f[1] = 0.0;
		for (int i = 2; i < f.length; i++) {
			f[i] = (double)(i % 2);
		}
		
		createGenAggParfactor("g1", "matched_6", "jackpot_won", "", "", Or.OR);
		createSimpleParfactor("g2", "", "matched_6", "1", "1.0;1.0");
		createSimpleParfactor("g3", "", "#.Person[matched_6];jackpot_won", "F#", toString(f));
		
		createLogicalVariable("A", "x", populationSize);
		createLogicalVariable("B", "x", populationSize);
		createLogicalVariable("C", "x", populationSize);
		createLogicalVariable("D", "x", populationSize);
		createLogicalVariable("E", "x", populationSize);
		createPrv("p", "A", "B");
		createPrv("c", "B");
		createPrv("v", "C");
		createPrv("u", "D", "E");
		createConstraint("A", "2");
		createConstraint("B", "1");
		// createGenAggParfactor(name, parent, child, (List) context, constraints, operator, fac name, factor values)
		
		double [] fpv = {0.1, 0.2, 0.3 ,0.4, 0.5, 0.6, 0.7, 0.8};
		createGenAggParfactor("g4", "p", "c", "v;u", "B != 1;A != 2", "Fpv", toString(fpv), Or.OR);
		createSimpleParfactor("g5", "A != 2;B != 1", "p;v;u", "Fpv", toString(fpv));
		
		createCountingFormula("#.A[p]", "A", "p", "A != 2");
		double [] f1 = new double[8 * populationSize];
		Arrays.fill(f1, 0, 4, 1);
		Arrays.fill(f1, 4, 8, 0);
		for (int i = 1; i < populationSize; i++) {
			Arrays.fill(f1, 8 * i, 8 * i + 4, 0);
			Arrays.fill(f1, 8 * i + 4, 8 * (i + 1), 1);
		}
		createSimpleParfactor("g6", "B != 1", "#.A[p];c;v;u", "F#", toString(f1));
	}
	
	public void setGenAggParfactorSplitTest() {
		createLogicalVariable("A", "x", 10);
		createLogicalVariable("B", "x", 10);
		createPrv("p", "A", "B");
		createPrv("c", "B");
		createPrv("c'", "B");
		createPrv("v", "A");
		createPrv("u", "B");
		createPrvFromSubstitution("p", "A/B");
		createPrvFromSubstitution("p", "B/A");
		createPrvFromSubstitution("p", "A/1");
		createPrvFromSubstitution("p", "B/1");
		createPrvFromSubstitution("c", "B/1");
		createPrvFromSubstitution("u", "B/1");
		createPrvFromSubstitution("u", "B/A");
		createPrvFromSubstitution("v", "A/1");
		createPrvFromSubstitution("v", "A/B");
		createBinding("B/1", "B", 1);
		createBinding("A/1", "A", 1);
		createBinding("B/A", "B", "A");
		createBinding("A/B", "A", "B");
		createConstraint("A", "1");
		createConstraint("A", "2");
		createConstraint("B", "1");
		createConstraint("B", "2");
		createConstraint("A", "B");
		createConstraint("B", "A");
		
		createGenAggParfactor("ag1", "p", "c", "v;u", "", Or.OR);
		createGenAggParfactor("ag2", "p", "c", "v;u", "A != 2;B != 2", Or.OR);
		
		createGenAggParfactor("g1", "p[B/1]", "c[B/1]", "v;u[B/1]","", Or.OR);
		createGenAggParfactor("g2", "p", "c", "v;u", "B != 1", Or.OR);
		createParfactorList("m1", "g1", "g2");
		
		createGenAggParfactor("g3", "p", "c'", "v;u", "A != 1", Or.OR);
		
		double [] f = {1.0, 0.0,
					   0.0, 1.0,
					   1.0, 0.0,
					   0.0, 1.0,
					   1.0, 0.0,
					   0.0, 1.0,
					   1.0, 0.0,
					   0.0, 1.0,
					   0.0, 1.0,
					   0.0, 1.0,
					   0.0, 1.0,
					   0.0, 1.0,
					   0.0, 1.0,
					   0.0, 1.0,
					   0.0, 1.0,
					   0.0, 1.0};
		
		createSimpleParfactor("g4", "", "p[A/1];v[A/1];u;c';c", "Fc", toString(f));
		createParfactorList("m2", "g3", "g4");
		
		createGenAggParfactor("g5", "p", "c'", "v;u", "A != B", Or.OR);
		createSimpleParfactor("g6", "", "p[A/B];v[A/B];u;c';c", "Fc", toString(f));
		createParfactorList("m3", "g5", "g6");
		
		createGenAggParfactor("g7", "p", "c'", "v;u", "A != B", Or.OR);
		createSimpleParfactor("g8", "", "p[B/A];v;u[B/A];c';c", "Fc", toString(f));
		createParfactorList("m4", "g7", "g8");
		
		createGenAggParfactor("g9",  "p[B/1]", "c[B/1]", "v;u[B/1]", "A != 2", Or.OR);
		createGenAggParfactor("g10", "p", "c", "v;u", "B != 1;B != 2;A != 2", Or.OR);
		createParfactorList("m5", "g9", "g10");
				
		createGenAggParfactor("g11", "p", "c'", "v;u", "A != 1;A != 2;B != 2", Or.OR);
		createSimpleParfactor("g12", "B != 2", "p[A/1];v[A/1];u;c';c", "Fc", toString(f));
		createParfactorList("m6", "g11", "g12");
				
		createGenAggParfactor("g13", "p", "c'", "v;u", "A != B;A != 2;B != 2", Or.OR);
		createSimpleParfactor("g14", "B != 2", "p[A/B];v[A/B];u;c';c", "Fc", toString(f));
		createParfactorList("m7", "g13", "g14");
		
		createGenAggParfactor("g15", "p", "c'", "v;u", "B != A;A != 2;B != 2", Or.OR);
		createSimpleParfactor("g16", "A != 2", "p[B/A];v;u[B/A];c';c", "Fc", toString(f));
		createParfactorList("m8", "g15", "g16");
	}
	
	public void setGenAggParfactorMultiplicationTest() {
		// first test
		createLogicalVariable("Person", "p", 100);
		createPrv("matched_6", "Person");
		createPrv("jackpot_won", "");
		createPrv("big_jackpot", "");
		
		createGenAggParfactor("g4", "matched_6", "jackpot_won", "big_jackpot","", Or.OR);
		double [] fMatched6 = {
				0.9999999965,
				0.999999989,
				0.0000000035,
				0.000000011
		};
		createSimpleParfactor("g5", "", "matched_6;big_jackpot", "Fmatched6", toString(fMatched6));
		createGenAggParfactor("g6", "matched_6", "jackpot_won", "big_jackpot","", "Fmatched6", toString(fMatched6), Or.OR);
		
		// second test
		createLogicalVariable("A", "x", 100);
		createLogicalVariable("B", "x", 100);
		createPrv("p", "A", "B");
		createPrv("c", "B");
		createPrv("v", "A");
		createPrv("u", "B");
		createConstraint("A", "2");
		createConstraint("B", "1");
		
		double [] fpv = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8};
		createSimpleParfactor("g1", "A != 2;B != 1", "p;v;u", "F1", toString(fpv));
		createGenAggParfactor("ga", "p", "c", "v;u", "A != 2;B != 1", "Fpv", toString(fpv), Or.OR);
		
		double [] fr = new double[8];
		for (int i = 0; i < fr.length; i++) {
			fr[i] = fpv[i] * fpv[i];
		}
		createGenAggParfactor("gar", "p", "c", "v;u", "A != 2;B != 1", "Fpv'", toString(fr), Or.OR);
	}
	
	public void setGenAggParfactorSumOutTest() {

		createLogicalVariable("Person", "p", 5);
		createPrv("matched_6", "Person");
		createPrv("jackpot_won", "");
		createPrv("big_jackpot", "");
		
		double [] fMatched6 = {
				0.9999999965,
				0.999999989,
				0.0000000035,
				0.000000011
		};
		createGenAggParfactor("g6", "matched_6", "jackpot_won", "big_jackpot", "", "Fmatched6'", toString(fMatched6), Or.OR);
		
		double [] fJackpotWon = {
				0.9999999825,
				0.9999999450,
				0.0000000175,
				0.0000000550
		};
		createSimpleParfactor("g7", "", "jackpot_won;big_jackpot", "Fjackpot_won", toString(fJackpotWon));
	}
	
	public void setGenAggUnificationTest() {
		
		createLogicalVariable("A", "x", 10);
		createLogicalVariable("B", "x", 10);
		createLogicalVariable("E", "x", 10);
		createLogicalVariable("F", "x", 10);
		
		createPrv("p", "A", "B");
		createPrv("c", "B");
		createPrv("c'", "B");
		createPrv("u", "");
		createPrv("v", "F");
		createPrv("w", "B");
		
		createPrvFromSubstitution("p", "B/1");
		createPrvFromSubstitution("p", "B/E");
		createPrvFromSubstitution("p", "A/1", "B/3");
		createPrvFromSubstitution("p", "A/1", "B/E");
		createPrvFromSubstitution("p", "A/F", "B/E");
		createPrvFromSubstitution("c", "B/1");
		createPrvFromSubstitution("c", "B/3");
		createPrvFromSubstitution("c", "B/E");
		createPrvFromSubstitution("c'", "B/1");
		createPrvFromSubstitution("c'", "B/3");
		createPrvFromSubstitution("c'", "B/E");

		createConstraint("A", "1");
		createConstraint("A", "2");
		createConstraint("A", "4");
		createConstraint("B", "1");
		createConstraint("B", "2");
		createConstraint("E", "1");
		createConstraint("E", "3");
		createConstraint("F", "3");
		
		String name;
		String parent;
		String child;
		String constraints;
		String contextVars;
		String vars;
		String factorName;
		String factorValues;
		
		// test 1 - input
		
		name 			= "g.1.in.1";
		parent 			= "p";
		child 			= "c";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.1.in.2";
		constraints 	= "";
		vars 			= "p[B/1]";
		factorName 	 	= "1";
		factorValues 	= "1.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		// test 1 - expected result
		
		name 			= "g.1.out.1";
		parent 			= "p[B/1]";
		child 			= "c[B/1]";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.1.out.2";
		parent 			= "p";
		child 			= "c";
		constraints 	= "B != 1";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.1.out.3";
		constraints 	= "";
		vars 			= "p[B/1]";
		factorName 	 	= "1";
		factorValues 	= "1.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		
		// test 2 - input
		
		name 			= "g.2.in.1";
		parent 			= "p";
		child 			= "c";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.2.in.2";
		constraints 	= "";
		vars 			= "p[A/1, B/E]";
		factorName 	 	= "1";
		factorValues 	= "1.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		// test 2 - expected result
		
		name 			= "g.2.out.1";
		parent 			= "p";
		child 			= "c'";
		constraints 	= "A != 1";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.2.out.2";
		constraints 	= "";
		vars 			= "p[A/1, B/E];c'[B/E];c[B/E]";
		factorName 	 	= "Fc";
		factorValues 	= "1.0;0.0;0.0;1.0;0.0;1.0;0.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		name 			= "g.2.out.3";
		constraints 	= "";
		vars 			= "p[A/1, B/E]";
		factorName 	 	= "1";
		factorValues 	= "1.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		
		// test 3 - input
		
		name 			= "g.3.in.1";
		parent 			= "p";
		child 			= "c";
		constraints 	= "B != 2;A != 4";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.3.in.2";
		constraints 	= "E != 3";
		vars 			= "p[A/1, B/E]";
		factorName 	 	= "1";
		factorValues 	= "1.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		// test 3 - expected result
		
		name 			= "g.3.out.1";
		parent 			= "p";
		child 			= "c'";
		constraints 	= "B != 2;A != 4;A != 1";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.3.out.2";
		constraints 	= "E != 3";
		vars 			= "p[A/1, B/E]";
		factorName 	 	= "1";
		factorValues 	= "1.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		name 			= "g.3.out.3";
		constraints 	= "";
		vars 			= "p[A/1, B/3];c'[B/3];c[B/3]";
		factorName 	 	= "Fc";
		factorValues 	= "1.0;0.0;0.0;1.0;0.0;1.0;0.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		name 			= "g.3.out.4";
		constraints 	= "E != 3";
		vars 			= "p[A/1, B/E];c'[B/E];c[B/E]";
		factorName 	 	= "Fc";
		factorValues 	= "1.0;0.0;0.0;1.0;0.0;1.0;0.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		
		// test 4 - input
		
		name 			= "g.4.in.1";
		parent 			= "p";
		child 			= "c";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.4.in.2";
		parent 			= "p[A/F, B/E]";
		child 			= "c[B/E]";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		// test 4 - expected result
		// g.4.out.1 == g.4.out.2 
		// g.4.out.3 == g.4.out.4
		// result is 1,2 or 3,4
		
		name 			= "g.4.out.1";
		parent 			= "p";
		child 			= "c";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.4.out.2";
		parent 			= "p";
		child 			= "c";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.4.out.3";
		parent 			= "p[A/F, B/E]";
		child 			= "c[B/E]";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.4.out.4";
		parent 			= "p[A/F, B/E]";
		child 			= "c[B/E]";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		
		// test 5 - input
		
		name 			= "g.5.in.1";
		parent 			= "p";
		child 			= "c";
		constraints 	= "";
		contextVars 	= "u";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.5.in.2";
		parent 			= "p[A/F, B/E]";
		child 			= "c[B/E]";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		// test 5 - expected result
		// result is 1,2 or 3,4
		
		name 			= "g.5.out.1";
		parent 			= "p";
		child 			= "c";
		constraints 	= "";
		contextVars 	= "u";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.5.out.2";
		parent 			= "p";
		child 			= "c";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.5.out.3";
		parent 			= "p[A/F, B/E]";
		child 			= "c[B/E]";
		constraints 	= "";
		contextVars 	= "u";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.5.out.4";
		parent 			= "p[A/F, B/E]";
		child 			= "c[B/E]";
		constraints 	= "";
		contextVars 	= "";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		
		// test 6 - input
		
		name 			= "g.6.in.1";
		parent 			= "p[B/1]";
		child 			= "c[B/1]";
		constraints 	= "";
		contextVars 	= "u";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.6.in.2";
		parent 			= "p[B/E]";
		child 			= "c[B/E]";
		constraints 	= "";
		contextVars 	= "v";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		// test 6 - expected result
		
		name 			= "g.6.out.1";
		parent 			= "p[B/1]";
		child 			= "c[B/1]";
		constraints 	= "";
		contextVars 	= "u";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.6.out.2";
		parent 			= "p[B/1]";
		child 			= "c[B/1]";
		constraints 	= "";
		contextVars 	= "v";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.6.out.3";
		parent 			= "p[B/E]";
		child 			= "c[B/E]";
		constraints 	= "E != 1";
		contextVars 	= "v";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		

		// test 7 - input
		
		name 			= "g.7.in.1";
		parent 			= "p[B/1]";
		child 			= "c[B/1]";
		constraints 	= "B != 1;A != 2";
		contextVars 	= "w";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.7.in.2";
		parent 			= "p[B/E]";
		child 			= "c[B/E]";
		constraints 	= "F != 3;A != 4";
		contextVars 	= "v";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		// test 7 - expected result
		
		name 			= "g.7.out.1";
		parent 			= "p[B/1]";
		child 			= "c'[B/1]";
		constraints 	= "F != 3;A != 2;A != 2";
		contextVars 	= "v";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.7.out.2";
		constraints 	= "F != 3";
		vars 			= "p[B/1];v;c'[B/1];c[B/1]";
		factorName 	 	= "Fc";
		factorValues 	= "1.0;0.0;0.0;1.0;1.0;0.0;0.0;1.0;0.0;1.0;0.0;1.0;0.0;1.0;0.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		name 			= "g.7.out.3";
		parent 			= "p[B/1]";
		child 			= "c'[B/1]";
		constraints 	= "B != 1;A != 2;A != 4";
		contextVars 	= "w";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
		name 			= "g.7.out.4";
		constraints 	= "B != 1";
		vars 			= "p[B/1];w;c'[B/1];c[B/1]";
		factorName 	 	= "Fc'";
		factorValues 	= "1.0;0.0;0.0;1.0;1.0;0.0;0.0;1.0;0.0;1.0;0.0;1.0;0.0;1.0;0.0;1.0";
		createSimpleParfactor(name, constraints, vars, factorName, factorValues);
		
		name 			= "g.7.out.5";
		parent 			= "p[B/E]";
		child 			= "c[B/E]";
		constraints 	= "F != 3;E != 1;A != 4";
		contextVars 	= "v";
		createGenAggParfactor(name, parent, child, contextVars, constraints, Or.OR);
		
	}
	
	/**
	 * Creates parfactors to test the Shattering macro operation using the new
	 * structure.
	 */
	public void setStdParfactorShatteringTest() {
		createLogicalVariable("X", "x", 10);
//		createStdPrv("f", "X");
//		createStdPrv("h", "X");
//		createStdPrvFromSubstitution("f", "X/1");
//		createStdPrvFromSubstitution("f", "X/2");
//		createStdPrvFromSubstitution("h", "X/1");
//		createStdPrvFromSubstitution("h", "X/2");
//		createInequalityConstraint("X", "1");
//		createInequalityConstraint("X", "2");
//		
//		// test 1 - input
//		createStdParfactor("g.1.in.1", "", "f;h[X/1]", "F1", "1;2;3;4");
//		createStdParfactor("g.1.in.2", "", "f[X/2];h", "F2", "1;2;3;4");
//		
//		// test 1 - expected result
//		createStdParfactor("g.1.out.3", "", "f[X/2];h[X/1]", "F1", "1;2;3;4");
//		createStdParfactor("g.1.out.4", "X != 2", "f;h[X/1]", "F1", "1;2;3;4");
//		createStdParfactor("g.1.out.5", "", "f[X/2];h[X/1]", "F2", "1;2;3;4");
//		createStdParfactor("g.1.out.7", "X != 1", "f[X/2];h", "F2", "1;2;3;4");
//		
//		// test 2 - input
//		createStdParfactor("g.2.in.1", "", "f;h", "F1", "1;2;3;4");
//		createStdParfactor("g.2.in.2", "", "f[X/1];h[X/2]", "F2", "5;6;7;8");
//		
//		// test 2 - expected result
//		createStdParfactor("g.2.out.3", "", "f[X/1];h[X/1]", "F1", "1;2;3;4");
//		createStdParfactor("g.2.out.4", "X != 1;X != 2", "f;h", "F1", "1;2;3;4");
//		createStdParfactor("g.2.out.5", "", "f[X/2];h[X/2]", "F1", "1;2;3;4");
//		createStdParfactor("g.2.out.7", "", "f[X/1];h[X/2]", "F2", "5;6;7;8");
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
	public OldCountingFormula getCountingFormula(String name) {
		return (OldCountingFormula) prvPool.get(name);
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
	
	/**
	 * Returns a set of the specified simple parfactors.
	 * @param name A list of simple parfactor names
	 * @return A set with the specified simple parfactors
	 * @throws IllegalArgumentException If a specified parfactor is not
	 * in the pool
	 */
	public Set<Parfactor> getSimpleParfactorSet(String ... name) 
			throws IllegalArgumentException {
		HashSet<Parfactor> parfactors = new HashSet<Parfactor>(name.length);
		for (String parfactor : name) {
			if (simpleParfactorPool.containsKey(parfactor)) 
				parfactors.add(simpleParfactorPool.get(parfactor));
			else
				throw new IllegalArgumentException("There is no such" 
						+ " parfactor in the pool: '" + parfactor + "'");
		}
		return parfactors;
	}
	
	/**
	 * Returns a random variable set from the pool
	 * @param name The name of the random variable set. 
	 * @return A random variable set from the pool with the specified name.
	 * @throws IllegalArgumentException If the set with the given name
	 * does not exist in the pool.
	 */
	public RandomVariableSet getRandomVariableSet(String name) throws IllegalArgumentException {
		if (randomVariableSetPool.containsKey(name)) {
			return randomVariableSetPool.get(name);
		} else {
			throw new IllegalArgumentException("There is no such RV set in " +
					"the pool: " + name);
		}
	}
	
	/**
	 * Returns an aggregation parfactor from the pool.
	 * @param name The name of the aggregation parfactor
	 * @return An aggregation parfactor from the pool.
	 * @throws IllegalArgumentException If the parfactor with the given name
	 * does not exist in the pool.
	 */
	public AggregationParfactor getAggParfactor(String name) throws IllegalArgumentException {
		if (aggParfactorPool.containsKey(name)) {
			return aggParfactorPool.get(name);
		} else {
			throw new IllegalArgumentException("There is no such Aggregation"
					+ " parfactor in the pool: " 
					+ name);
		}
	}
		
	/**
	 * Returns a binding from the pool.
	 * @param name The name of the binding.
	 * @return A binding from the pool.
	 * @throws IllegalArgumentException If the binding with the specified name
	 * does not exist in the pool.
	 */
	public Binding getBinding(String name) throws IllegalArgumentException {
		if (bindingPool.containsKey(name)) {
			return bindingPool.get(name);
		} else {
			throw new IllegalArgumentException("There is no such Binding"
					+ " in the pool: " 
					+ name);
		}
	}
	
	/**
	 * Returns a list of parfactors from the pool.
	 * @param name The name of the list of parfactors.
	 * @return A list of parfactors from the pool
	 * @throws IllegalArgumentException If the list does not exist
	 */
	public List<Parfactor> getParfactorList(String name) throws IllegalArgumentException {
		if (parfactorListPool.containsKey(name)) {
			return parfactorListPool.get(name);
		} else {
			throw new IllegalArgumentException("There is no such list of"
					+ " parfactor in the pool: " 
					+ name);
		}
	}
	
	/**
	 * Returns a Generalized Aggregation Parfactor from the pool
	 * @param name The name of the GAP.
	 * @return A Generalized Aggregation Parfactor from the pool
	 * @throws IllegalArgumentException If the GAP with the specified name 
	 * does not exist in the pool.
	 */
	public GeneralizedAggregationParfactor getGenAggParfactor (String name) 
			throws IllegalArgumentException {
		if (genAggParfactorPool.containsKey(name)) {
			return genAggParfactorPool.get(name);
		} else {
			throw new IllegalArgumentException("There is no such Generalized" 
					+ " Aggregation"
					+ " parfactor in the pool: " 
					+ name);
		}
	}
}
