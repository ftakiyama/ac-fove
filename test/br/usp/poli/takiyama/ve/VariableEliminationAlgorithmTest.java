package br.usp.poli.takiyama.ve;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;

import br.usp.poli.takiyama.common.RandomVariable;
import br.usp.poli.takiyama.ve.Factor;
import br.usp.poli.takiyama.ve.VariableEliminationAlgorithm;

/**
 * A set of tests for {@link VariableEliminationAlgorithm}.
 * The tests must be checked manually.
 * 
 * @author ftakiyama
 *
 */
//@Ignore("Old code")
public class VariableEliminationAlgorithmTest {
	
	/**
	 * Creates a default boolean random variable. The domain is {false, true}
	 * and the corresponding values are 0.5 and 0.5. One must take the ordering
	 * of the domain into account when creating the mapping for the factors.
	 * @param name The name of the random variable.
	 * @return A random variable with the specified name and domain {false, true}. 
	 */
	private RandomVariable getDefaultBooleanRandomVariable(String name) {
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("false");
		domain.add("true");
		
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		values.add(new BigDecimal(0.5));
		values.add(new BigDecimal(0.5));
		
		return RandomVariable.createRandomVariable(name, domain, values);
	}
	
	@Test
	public void wetGrassProblem() {
		
		String[] name = {"rain", "sprinkler", "wet_grass"};
		HashMap<String,RandomVariable> randomVariables = new HashMap<String,RandomVariable>();
		HashMap<String,Factor> factors = new HashMap<String,Factor>();
		
		for (int i = 0; i < name.length; i++) {
			randomVariables.put(name[i], getDefaultBooleanRandomVariable(name[i]));
		}
		
		ArrayList<RandomVariable> variables = new ArrayList<RandomVariable>();
		variables.add(randomVariables.get("rain"));
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.8));
		mapping.add(new BigDecimal(0.2));
		factors.put("f1", new Factor("f1", variables, mapping));
		
		variables = new ArrayList<RandomVariable>();
		variables.add(randomVariables.get("sprinkler"));
		mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.6));
		mapping.add(new BigDecimal(0.4));
		factors.put("f2", new Factor("f2", variables, mapping));
		
		variables = new ArrayList<RandomVariable>();
		variables.add(randomVariables.get("rain"));
		variables.add(randomVariables.get("sprinkler"));
		variables.add(randomVariables.get("wet_grass"));
		mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.8));
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.9));
		mapping.add(new BigDecimal(0.01));
		mapping.add(new BigDecimal(0.99));
		factors.put("f3", new Factor("f3", variables, mapping));
		
		RandomVariable[] v = new RandomVariable[randomVariables.size()];
		for (int i = 0; i < randomVariables.size(); i++) {
			v[i] = randomVariables.get(name[i]);
		}
		
		// Creates the array of factors
		Factor[] f = new Factor[factors.size()];
		for (int i = 0; i < factors.size(); i++) {
			f[i] = factors.get("f" + (i + 1));
		}
		
		RandomVariable[] o = new RandomVariable[0];
		
		RandomVariable q = randomVariables.get("wet_grass");
		
		VariableEliminationAlgorithm algorithm = new VariableEliminationAlgorithm(v, f, o, q);
		
		try {
			System.out.println(algorithm.execute());
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}
	}
	
	@Test
	public void familyOutProblem() {
		
		// Initialization
		String[] name = {"light_on", "family_out", "dog_out", "bowel_problem", "hear_bark"};
		HashMap<String,RandomVariable> randomVariables = new HashMap<String,RandomVariable>();
		HashMap<String,Factor> factors = new HashMap<String,Factor>();
		
		// Creates the random variables
		for (int i = 0; i < name.length; i++) {
			randomVariables.put(name[i], getDefaultBooleanRandomVariable(name[i]));
		}
		
		// Creates the factor to "light_on"
		ArrayList<RandomVariable> variables = new ArrayList<RandomVariable>();
		variables.add(randomVariables.get("light_on"));
		variables.add(randomVariables.get("family_out"));
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.95));
		mapping.add(new BigDecimal(0.4));
		mapping.add(new BigDecimal(0.05));
		mapping.add(new BigDecimal(0.6));
		factors.put("f[light_on]", new Factor("f[light_on]", variables, mapping));
		
		// Creates the factor to "familiy_out"
		variables.clear();
		variables.add(randomVariables.get("family_out"));
		mapping.clear();
		mapping.add(new BigDecimal(0.85));
		mapping.add(new BigDecimal(0.15));
		factors.put("f[family_out]", new Factor("f[family_out]", variables, mapping));
		
		// Creates the factor to "bowel_problem"
		variables.clear();
		variables.add(randomVariables.get("bowel_problem"));
		mapping.clear();
		mapping.add(new BigDecimal(0.99));
		mapping.add(new BigDecimal(0.01));
		factors.put("f[bowel_problem]", new Factor("f[bowel_problem]", variables, mapping));
		
		// Creates the factor to "dog_out"
		variables.clear();
		variables.add(randomVariables.get("dog_out"));
		variables.add(randomVariables.get("family_out"));
		variables.add(randomVariables.get("bowel_problem"));
		mapping.clear();
		mapping.add(new BigDecimal(0.7));
		mapping.add(new BigDecimal(0.03));
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.01));
		mapping.add(new BigDecimal(0.3));
		mapping.add(new BigDecimal(0.97));
		mapping.add(new BigDecimal(0.90));
		mapping.add(new BigDecimal(0.99));
		factors.put("f[dog_out]", new Factor("f[dog_out]", variables, mapping));
		
		// Creates the factor to "hear_bark"
		variables.clear();
		variables.add(randomVariables.get("hear_bark"));
		variables.add(randomVariables.get("dog_out"));
		mapping.clear();
		mapping.add(new BigDecimal(0.99));
		mapping.add(new BigDecimal(0.3));
		mapping.add(new BigDecimal(0.01));
		mapping.add(new BigDecimal(0.7));
		factors.put("f[hear_bark]", new Factor("f[hear_bark]", variables, mapping));
		
		// Creates the vector of random variables
		RandomVariable[] v = new RandomVariable[randomVariables.size()];
		for (int i = 0; i < randomVariables.size(); i++) {
			v[i] = randomVariables.get(name[i]);
		}
		
		// Creates the array of factors
		Factor[] f = new Factor[factors.size()];
		for (int i = 0; i < factors.size(); i++) {
			f[i] = factors.get("f[" + name[i] + "]");
		}
		
		// Creates the array of observed variables
		RandomVariable[] o = new RandomVariable[1];
		o[0] = randomVariables.get("hear_bark");
		
		// Creates the query random variable
		RandomVariable q = randomVariables.get("dog_out");
		
		// Creates a new instance of the algorithm
		VariableEliminationAlgorithm algorithm = new VariableEliminationAlgorithm(v, f, o, q);
		
		// Executes the algorithm
		try {
			System.out.println(algorithm.execute());
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}
	}
	
	@Ignore
	@Test
	public void existsNode2() {
		// Initialization
		String[] name = {"r(x1,x1)", "r(x1,x2)", "r(x2,x1)", "r(x2,x2)", "b(x1)", "b(x2)", "exists(x1)", "exists(x2)"};
		HashMap<String, RandomVariable> randomVariables = new HashMap<String,RandomVariable>();
		HashMap<String, Factor> factors = new HashMap<String,Factor>();
		
		// Creates the random variables
		for (int i = 0; i < name.length; i++) {
			randomVariables.put(name[i], getDefaultBooleanRandomVariable(name[i]));
		}
		
		// Creates the factor to "r(X,Y)"
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.8));
		
		ArrayList<RandomVariable> variables = new ArrayList<RandomVariable>();
		for (String var : name) {
			if (var.substring(0, 1).equals("r")) {
				variables.add(randomVariables.get(var));
				String factorName = "f[" + var + "]";
				factors.put(factorName, new Factor(factorName, variables, mapping));
				variables.clear();
			}
		}
		
		// Creates the factor to "b(X)"
		mapping.clear();
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.9));
		
		for (String var : name) {
			if (var.substring(0, 1).equals("b")) {
				variables.add(randomVariables.get(var));
				String factorName = "f[" + var + "]";
				factors.put(factorName, new Factor(factorName, variables, mapping));
				variables.clear();
			}
		}
		
		// Creates the factor to "b(X)"
		mapping.clear();
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		
		variables.add(randomVariables.get(name[0]));
		variables.add(randomVariables.get(name[4]));
		variables.add(randomVariables.get(name[1]));
		variables.add(randomVariables.get(name[5]));
		variables.add(randomVariables.get(name[6]));
		
		factors.put("f[exists(x1)]", new Factor("f[exists(x1)]", variables, mapping));

		variables.clear();
		

		variables.add(randomVariables.get(name[2]));
		variables.add(randomVariables.get(name[4]));
		variables.add(randomVariables.get(name[3]));
		variables.add(randomVariables.get(name[5]));
		variables.add(randomVariables.get(name[7]));
		
		factors.put("f[exists(x2)]", new Factor("f[exists(x2)]", variables, mapping));
		
		// Creates the vector of random variables
		RandomVariable[] v = new RandomVariable[randomVariables.size()];
		for (int i = 0; i < randomVariables.size(); i++) {
			v[i] = randomVariables.get(name[i]);
		}
		
		// Creates the array of factors
		Factor[] f = new Factor[factors.size()];
		for (int i = 0; i < factors.size(); i++) {
			f[i] = factors.get("f[" + name[i] + "]");
		}
		
		// Creates the array of observed variables
		RandomVariable[] o = new RandomVariable[0];
		
		// Creates the query random variable
		RandomVariable q = randomVariables.get("exists(x1)");
		
		// Creates a new instance of the algorithm
		VariableEliminationAlgorithm algorithm = new VariableEliminationAlgorithm(v, f, o, q);
		
		// Executes the algorithm
		try {
			System.out.println(algorithm.execute());
		} catch (Exception e) {
			System.err.print(e.getMessage());
			System.exit(-1);
		}
	}
	
	@Test
	public void existsNode() {
		
		int n = 2;
		HashMap<String, RandomVariable> randomVariables = new HashMap<String,RandomVariable>();
		HashMap<String, Factor> factors = new HashMap<String,Factor>();
		
		// creates random variables
		for (int i = 0; i < n; i++) {
			String b = "b(" + i + ")";
			String e = "e(" + i + ")";
			randomVariables.put(b, getDefaultBooleanRandomVariable(b));
			randomVariables.put(e, getDefaultBooleanRandomVariable(e));
			for (int j = 0; j < n; j++) {
				String r = "r(" + i + ", " + j + ")";
				randomVariables.put(r, getDefaultBooleanRandomVariable(r));
				r = "r'(" + i + ", " + j + ")";
				randomVariables.put(r, getDefaultBooleanRandomVariable(r));
			}
		}
		
		// Creates the factor to "r(X,Y)"
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.8));
		
		ArrayList<RandomVariable> variables = new ArrayList<RandomVariable>();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				String r = "r(" + i + ", " + j + ")";
				variables.add(randomVariables.get(r));
				String factorName = "f[" + r + "]";
				factors.put(factorName, new Factor(factorName, variables, mapping));
				variables.clear();
			}
		}
		
		// Creates the factor to "b(Y)"
		mapping.clear();
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.9));
		
		for (String var : randomVariables.keySet()) {
			if (var.substring(0, 1).equals("b")) {
				variables.add(randomVariables.get(var));
				String factorName = "f[" + var + "]";
				factors.put(factorName, new Factor(factorName, variables, mapping));
				variables.clear();
			}
		}
		
		// Creates the factor for "r(X,Y) b(Y) r'(X,Y)"
		mapping.clear();
		mapping.add(BigDecimal.ONE);
		mapping.add(BigDecimal.ZERO);
		mapping.add(BigDecimal.ONE);
		mapping.add(BigDecimal.ZERO);
		mapping.add(BigDecimal.ONE);
		mapping.add(BigDecimal.ZERO);
		mapping.add(BigDecimal.ZERO);
		mapping.add(BigDecimal.ONE);
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				String b = "b(" + j + ")";
				String r = "r(" + i + ", " + j + ")";
				String r1 = "r'(" + i + ", " + j + ")";
				variables.add(randomVariables.get(r));
				variables.add(randomVariables.get(b));
				variables.add(randomVariables.get(r1));
				String factorName = "f[and(" + i + ", " + j + ")]";
				factors.put(factorName, new Factor(factorName, variables, mapping));
				variables.clear();
			}
		}
		
		// Creates the factor for "r'(X,y1) ... r'(X,yn) e(X)"
		mapping.clear();
		for (int i = 0; i < (int) Math.pow(2, n); i++) {
			mapping.add(BigDecimal.ZERO);
			mapping.add(BigDecimal.ONE);
		}
		mapping.set(0, BigDecimal.ONE);
		mapping.set(1, BigDecimal.ZERO);
		
		for (int i = 0; i < n; i++) {
			String e = "e(" + i + ")";
			for (int j = 0; j < n; j++) {
				String r1 = "r'(" + i + ", " + j + ")";
				variables.add(randomVariables.get(r1));
			}
			variables.add(randomVariables.get(e));
			String factorName = "f[e(" + i + ")]";
			factors.put(factorName, new Factor(factorName, variables, mapping));
			variables.clear();
		}
		
		// Creates the vector of random variables
		RandomVariable[] v = new ArrayList<RandomVariable>(randomVariables.values()).toArray(new RandomVariable[randomVariables.size()]);
		
		// Creates the array of factors
		Factor[] f = new ArrayList<Factor>(factors.values()).toArray(new Factor[factors.size()]);
		
		// Creates the array of observed variables
		RandomVariable[] o = new RandomVariable[0];
		
		// Creates the query random variable
		RandomVariable q = randomVariables.get("e(0)");
		
		// Creates a new instance of the algorithm
		VariableEliminationAlgorithm algorithm = new VariableEliminationAlgorithm(v, f, o, q);
		
		// Executes the algorithm
		try {
			System.out.println("Welcome to this incredible test!");
			System.out.println(algorithm.execute());
		} catch (Exception e) {
			System.err.print(e.getMessage());
			System.exit(-1);
		}
	}
	
	@Test
	public void existsNodeSize1() {
		// Initialization
		String[] name = {"r(x1,x1)", "b(x1)", "exists(x1)"};
		HashMap<String, RandomVariable> randomVariables = new HashMap<String,RandomVariable>();
		HashMap<String, Factor> factors = new HashMap<String,Factor>();
		
		// Creates random variables
		for (int i = 0; i < name.length; i++) {
			randomVariables.put(name[i], getDefaultBooleanRandomVariable(name[i]));
		}
		
		// Creates the factor "r(X,Y)"
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.8));
		
		ArrayList<RandomVariable> variables = new ArrayList<RandomVariable>();
		for (String var : name) {
			if (var.substring(0, 1).equals("r")) {
				variables.add(randomVariables.get(var));
				String factorName = "f[" + var + "]";
				factors.put(factorName, new Factor(factorName, variables, mapping));
				variables.clear();
			}
		}
		
		// Creates the factor "b(X)"
		mapping.clear();
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.9));
		
		for (String var : name) {
			if (var.substring(0, 1).equals("b")) {
				variables.add(randomVariables.get(var));
				String factorName = "f[" + var + "]";
				factors.put(factorName, new Factor(factorName, variables, mapping));
				variables.clear();
			}
		}
		
		// Creates the factor "exists(X)"
		mapping.clear();
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(0.0));
		mapping.add(new BigDecimal(1.0));
		
		variables.add(randomVariables.get(name[0]));
		variables.add(randomVariables.get(name[1]));
		variables.add(randomVariables.get(name[2]));
		
		factors.put("f[exists(x1)]", new Factor("f[exists(x1)]", variables, mapping));

		// Creates the vector of random variables
		RandomVariable[] v = new RandomVariable[randomVariables.size()];
		for (int i = 0; i < randomVariables.size(); i++) {
			v[i] = randomVariables.get(name[i]);
		}
		
		// Creates the array of factors
		Factor[] f = new Factor[factors.size()];
		for (int i = 0; i < factors.size(); i++) {
			f[i] = factors.get("f[" + name[i] + "]");
		}
		
		// Creates the array of observed variables
		RandomVariable[] o = new RandomVariable[0];
		
		// Creates the query random variable
		RandomVariable q = randomVariables.get("exists(x1)");
		
		// Creates a new instance of the algorithm
		VariableEliminationAlgorithm algorithm = new VariableEliminationAlgorithm(v, f, o, q);
		
		// Executes the algorithm
		try {
			System.out.println(algorithm.execute());
		} catch (Exception e) {
			System.err.print(e.getMessage());
			System.exit(-1);
		}
	}
	
	@Test
	public void multiplication() {
		
		int n = 3;
		HashMap<String, RandomVariable> randomVariables = new HashMap<String,RandomVariable>();
		HashMap<String, Factor> factors = new HashMap<String,Factor>();
		
		// creates random variables
		for (int i = 0; i < n; i++) {
			String b = "b(" + i + ")";
			String e = "e(" + i + ")";
			randomVariables.put(b, getDefaultBooleanRandomVariable(b));
			randomVariables.put(e, getDefaultBooleanRandomVariable(e));
			for (int j = 0; j < n; j++) {
				String r = "r(" + i + ", " + j + ")";
				randomVariables.put(r, getDefaultBooleanRandomVariable(r));
				r = "r'(" + i + ", " + j + ")";
				randomVariables.put(r, getDefaultBooleanRandomVariable(r));
			}
		}
		
		// Creates the factor to "r(X,Y)"
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.8));
		
		ArrayList<RandomVariable> variables = new ArrayList<RandomVariable>();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				String r = "r(" + i + ", " + j + ")";
				variables.add(randomVariables.get(r));
				String factorName = "f[" + r + "]";
				factors.put(factorName, new Factor(factorName, variables, mapping));
				variables.clear();
			}
		}
		
		// Creates the factor to "b(Y)"
		mapping.clear();
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.9));
		
		for (String var : randomVariables.keySet()) {
			if (var.substring(0, 1).equals("b")) {
				variables.add(randomVariables.get(var));
				String factorName = "f[" + var + "]";
				factors.put(factorName, new Factor(factorName, variables, mapping));
				variables.clear();
			}
		}
		
		// Creates the factor for "r(X,Y) b(Y) r'(X,Y)"
		mapping.clear();
		mapping.add(BigDecimal.ONE);
		mapping.add(BigDecimal.ZERO);
		mapping.add(BigDecimal.ONE);
		mapping.add(BigDecimal.ZERO);
		mapping.add(BigDecimal.ONE);
		mapping.add(BigDecimal.ZERO);
		mapping.add(BigDecimal.ZERO);
		mapping.add(BigDecimal.ONE);
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				String b = "b(" + j + ")";
				String r = "r(" + i + ", " + j + ")";
				String r1 = "r'(" + i + ", " + j + ")";
				variables.add(randomVariables.get(r));
				variables.add(randomVariables.get(b));
				variables.add(randomVariables.get(r1));
				String factorName = "f[and(" + i + ", " + j + ")]";
				factors.put(factorName, new Factor(factorName, variables, mapping));
				variables.clear();
			}
		}
		
		// Creates the factor for "r'(X,y1) ... r'(X,yn) e(X)"
		mapping.clear();
		for (int i = 0; i < (int) Math.pow(2, n); i++) {
			mapping.add(BigDecimal.ZERO);
			mapping.add(BigDecimal.ONE);
		}
		mapping.set(0, BigDecimal.ONE);
		mapping.set(1, BigDecimal.ZERO);
		
		for (int i = 0; i < n; i++) {
			String e = "e(" + i + ")";
			for (int j = 0; j < n; j++) {
				String r1 = "r'(" + i + ", " + j + ")";
				variables.add(randomVariables.get(r1));
			}
			variables.add(randomVariables.get(e));
			String factorName = "f[e(" + i + ")]";
			factors.put(factorName, new Factor(factorName, variables, mapping));
			variables.clear();
		}
		
		// Creates the vector of random variables
		RandomVariable[] v = new ArrayList<RandomVariable>(randomVariables.values()).toArray(new RandomVariable[randomVariables.size()]);
		
		// Creates the array of factors
		Factor[] f = new ArrayList<Factor>(factors.values()).toArray(new Factor[factors.size()]);
		
		// Creates the array of observed variables
		RandomVariable[] o = new RandomVariable[0];
		
		// Creates the query random variable
		RandomVariable q = randomVariables.get("e(1)");
		
		// Creates a new instance of the algorithm
		VariableEliminationAlgorithm algorithm = new VariableEliminationAlgorithm(v, f, o, q);
		
		// Executes the algorithm
		try {
			System.out.println(algorithm.execute());
		} catch (Exception e) {
			System.err.print(e.getMessage());
			System.exit(-1);
		}
	}
}
