package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.cfove.prv.PRV;
import br.usp.poli.takiyama.cfove.prv.ParameterizedRandomVariable;


public class OperationsTest {
	
	private HashMap<String, ParameterizedRandomVariable> variables;
	private HashMap<String, ParameterizedFactor> factors;
	private HashMap<String, Parfactor> parfactors;
	
	
	@Before
	public void initialSetup() {
		// Pool of PRVs
		variables = new HashMap<String, ParameterizedRandomVariable>();
		for (int i = 0; i < 3; i++) {
			String name = "f" + i;
			variables.put(name,
						  PRV.getBooleanPrvWithOneParameter(name, i + 1));
		}
		for (int i = 0; i < 3; i++) {
			String name = "g" + i;
			variables.put(name,
						  PRV.getBooleanPrvWithTwoParameters(name, i + 1, i + 2));
		}
		for (int i = 0; i < 3; i++) {
			String name = "h" + i;
			variables.put(name,
						  PRV.getBooleanPrvWithThreeParameters(name, i + 1, i + 2, i + 3));
		}
		
		// Logical variable for the wine ontology
//		String name = "wine";
//		variables.put(name, PRV.getBooleanPrvWithOneParameter(name, 10));
		
		// Pool of factors
		factors = new HashMap<String, ParameterizedFactor>(); 
		
		String name = "factor1";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(0.1);
		mapping.add(0.2);
		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
		
		name = "factor2";
		prvs.add(variables.get("g0"));
		mapping.add(0.3);
		mapping.add(0.4);
		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
		
		name = "factor3";
		prvs.add(variables.get("h0"));
		mapping.add(0.5);
		mapping.add(0.6);
		mapping.add(0.7);
		mapping.add(0.8);
		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
		
		name = "factor4";
		prvs.clear();
		prvs.add(variables.get("f0"));
		mapping.clear();
		mapping.add(0.3);
		mapping.add(0.4);
		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
		
		name = "factor5";
		prvs.clear();
		prvs.add(variables.get("f2"));
		mapping.clear();
		mapping.add(0.3);
		mapping.add(0.4);
		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
		
		name = "empty_factor";
		prvs.clear();
		mapping.clear();
		mapping.add(1);
		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
		
		// Pool of parfactors
		parfactors = new HashMap<String, Parfactor>(); 
		parfactors.put("parfactor1", Parfactor.getInstanceWithoutConstraints(factors.get("factor1")));
		parfactors.put("parfactor2", Parfactor.getInstanceWithoutConstraints(factors.get("factor2")));
		parfactors.put("parfactor3", Parfactor.getInstanceWithoutConstraints(factors.get("factor3")));
		parfactors.put("parfactor4", Parfactor.getInstanceWithoutConstraints(factors.get("factor4")));
		parfactors.put("parfactor5", Parfactor.getInstanceWithoutConstraints(factors.get("factor5")));
		parfactors.put("empty_parfactor", Parfactor.getInstanceWithoutConstraints(factors.get("empty_factor")));
	}
	
	@Test
	public void testBasicLiftedElimination() {
		System.out.println("\nTest: Basic Lifted Elimination");
		
		ArrayList<Parfactor> setOfParfactors = new ArrayList<Parfactor>();
		setOfParfactors.add(parfactors.get("parfactor2"));
		
		System.out.println("Before lifted elimination: \n" + setOfParfactors);
		
		System.out.println("After lifted elimination: \n" + Operations
				.liftedElimination(setOfParfactors, 
								   parfactors.get("parfactor2"), 
								   variables.get("g0")));		
	}
	
	@Test
	public void testBasicMultiplication() {
		System.out.println("\nTest: Basic Multiplication");
		
		ArrayList<Parfactor> setOfParfactors = new ArrayList<Parfactor>();
		setOfParfactors.add(parfactors.get("parfactor1"));
		setOfParfactors.add(parfactors.get("parfactor4"));
		
		System.out.println("Before multiplication: \n" + setOfParfactors);
		
		System.out.println("After multiplication: \n" + Operations
				.multiplication(setOfParfactors, 
								parfactors.get("parfactor1"), 
								parfactors.get("parfactor4")));
	}
}
