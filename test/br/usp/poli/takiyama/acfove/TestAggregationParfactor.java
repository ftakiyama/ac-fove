package br.usp.poli.takiyama.acfove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.cfove.Constraint;
import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.cfove.Parfactor;
import br.usp.poli.takiyama.cfove.prv.PRV;
import br.usp.poli.takiyama.cfove.prv.ParameterizedRandomVariable;

public class TestAggregationParfactor {
	
	private HashMap<String, ParameterizedRandomVariable> variables;
	private HashMap<String, ParameterizedFactor> factors;
	private HashMap<String, Parfactor> parfactors;
	private HashMap<String, AggregationParfactor> aggParfactors;
	
	@Before
	public void initialSetup() {
		variables = new HashMap<String, ParameterizedRandomVariable>();
		factors = new HashMap<String, ParameterizedFactor>();
		parfactors = new HashMap<String, Parfactor>();
		aggParfactors = new HashMap<String, AggregationParfactor>();
		
		variables.put("p", PRV.getBooleanPrvWithOneParameter("p", 10));
		variables.put("c", PRV.getBooleanPrvWithoutParameter("c"));
		
		
		// Creates a simple parfactor
		String name = "Parent";
		ArrayList<ParameterizedRandomVariable> v = new ArrayList<ParameterizedRandomVariable>();
		v.add(variables.get("p"));
		ArrayList<Number> m = new ArrayList<Number>();
		m.add(Double.valueOf("0.2"));
		m.add(Double.valueOf("0.8"));
		factors.put(name, ParameterizedFactor.getInstance(name, v, m));
		parfactors.put(name, Parfactor.getInstanceWithoutConstraints(factors.get(name)));
		
		// Creates a simple aggregation parfactor
		name = "Child";
		v.clear();
		v.add(variables.get("c"));
		m.clear();
		m.add(Double.valueOf("0.2"));
		m.add(Double.valueOf("0.8"));
		factors.put(name, ParameterizedFactor.getInstance(name, v, m));
		HashSet<Constraint> emptySet = new HashSet<Constraint>();
		Operator op = new Operator();
		aggParfactors.put("agg1", AggregationParfactor.getInstance(emptySet, 
				variables.get("p"), 
				variables.get("c"), 
				factors.get("Parent"), 
				op, 
				emptySet));	
	}
	
	@Test
	public void testSimpleMultiplication() {
		System.out.println("\nTest: Simple Multiplication");
		System.out.println(aggParfactors.get("agg1").multiply(parfactors.get("Parent")));
		
	}
}
