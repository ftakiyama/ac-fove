package br.usp.dml.takiyama.ve;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;


public class VariableEliminationAlgorithmTest {
	
	private String[] name = {"rain", "sprinkler", "wet_grass"};
	private HashMap<String,RandomVariable> randomVariables;
	private HashMap<String,Factor> factors;
	
	private void initializeAttributes() {
		this.randomVariables = new HashMap<String,RandomVariable>();
		this.factors = new HashMap<String,Factor>();
	}
	
	private RandomVariable getDefaultBooleanRandomVariable(String name) {
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("false");
		domain.add("true");
		
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		values.add(new BigDecimal(0.5));
		values.add(new BigDecimal(0.5));
		
		return RandomVariable.createRandomVariable(name, domain, values);
	}
	
	@Before
	public void initialSetup() {
		
		initializeAttributes();
		
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
	}
	
	@Test
	public void testAlgorithm() {
		
		// ugly
		RandomVariable[] v = new RandomVariable[name.length];
		v[0] = randomVariables.get("rain");
		v[1] = randomVariables.get("sprinkler");
		v[2] = randomVariables.get("wet_grass");
		
		Factor[] f = new Factor[3];
		f[0] = factors.get("f1");
		f[1] = factors.get("f2");
		f[2] = factors.get("f3");
		
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
	public void testCharniakExample() {
		//TODO
	}
	
	
}
