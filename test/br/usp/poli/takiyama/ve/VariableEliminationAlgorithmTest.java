package br.usp.poli.takiyama.ve;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

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
	
	
}
