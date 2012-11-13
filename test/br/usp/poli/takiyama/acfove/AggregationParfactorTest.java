//package br.usp.poli.takiyama.acfove;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import br.usp.poli.takiyama.common.Constraint;
//import br.usp.poli.takiyama.cfove.ParameterizedFactor;
//import br.usp.poli.takiyama.cfove.SimpleParfactor;
//import br.usp.poli.takiyama.cfove.SimpleParfactor;
//import br.usp.poli.takiyama.prv.PRV;
//import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
//
//public class AggregationParfactorTest {
//	
//	private HashMap<String, ParameterizedRandomVariable> variables;
//	private HashMap<String, ParameterizedFactor> factors;
//	private HashMap<String, SimpleParfactor> parfactors;
//	private HashMap<String, AggregationParfactor> aggParfactors;
//	
//	@Before
//	public void initialSetup() {
//		variables = new HashMap<String, ParameterizedRandomVariable>();
//		factors = new HashMap<String, ParameterizedFactor>();
//		parfactors = new HashMap<String, SimpleParfactor>();
//		aggParfactors = new HashMap<String, AggregationParfactor>();
//		
//		variables.put("p", PRV.getBooleanPrvWithOneParameter("p", 10));
//		variables.put("c", PRV.getBooleanPrvWithoutParameter("c"));
//		variables.put("matched_6", PRV.getBooleanPrvWithOneParameter("matched_6", 5));
//		variables.put("jackpot_won", PRV.getBooleanPrvWithoutParameter("jackpot_won"));
//		
//		
//		// Creates a simple parfactor
//		String name = "Parent";
//		ArrayList<ParameterizedRandomVariable> v = new ArrayList<ParameterizedRandomVariable>();
//		v.add(variables.get("p"));
//		ArrayList<Number> m = new ArrayList<Number>();
//		m.add(Double.valueOf("0.2"));
//		m.add(Double.valueOf("0.8"));
//		factors.put(name, ParameterizedFactor.getInstance(name, v, m));
//		parfactors.put(name, SimpleParfactor.getInstanceWithoutConstraints(factors.get(name)));
//		
//		// Creates another simple parfactor
//		name = "Cousin";
//		v.clear();
//		v.add(variables.get("c"));
//		m.clear();
//		m.add(Double.valueOf("0.2"));
//		m.add(Double.valueOf("0.8"));
//		factors.put(name, ParameterizedFactor.getInstance(name, v, m));
//		parfactors.put(name, SimpleParfactor.getInstanceWithoutConstraints(factors.get(name)));
//		
//		
//		// Creates a simple aggregation parfactor
//		name = "Child";
//		v.clear();
//		v.add(variables.get("c"));
//		m.clear();
//		m.add(Double.valueOf("0.2"));
//		m.add(Double.valueOf("0.8"));
//		factors.put(name, ParameterizedFactor.getInstance(name, v, m));
//		HashSet<Constraint> emptySet = new HashSet<Constraint>();
//		Or op = Or.getInstance();
//		aggParfactors.put("agg1", AggregationParfactor.getInstance(emptySet, 
//				variables.get("p"), 
//				variables.get("c"), 
//				factors.get("Parent"), 
//				op, 
//				emptySet));	
//		
//		
//		// Creates a simple aggregation parfactor Fsum
//		name = "Fsum";
//		v.clear();
//		v.add(variables.get("matched_6"));
//		m.clear();
//		m.add(Double.valueOf("0.9999999965"));
//		m.add(Double.valueOf("0.0000000035"));
//		factors.put(name, ParameterizedFactor.getInstance(name, v, m));
//		aggParfactors.put("agg2", AggregationParfactor.getInstance(emptySet, 
//				variables.get("matched_6"), 
//				variables.get("jackpot_won"), 
//				factors.get("Fsum"), 
//				op, 
//				emptySet));	
//		
//	}
//	
//	@Test
//	public void testSimpleMultiplication() {
//		System.out.println("\nTest: Simple Multiplication");
//		HashSet<SimpleParfactor> setOfParfactors = new HashSet<SimpleParfactor>();
//		setOfParfactors.add(aggParfactors.get("agg1"));
//		System.out.println(aggParfactors.get("agg1").multiply(setOfParfactors, parfactors.get("Parent")));
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void testMultiplicationConditions() {
//		HashSet<SimpleParfactor> setOfParfactors = new HashSet<SimpleParfactor>();
//		setOfParfactors.add(aggParfactors.get("agg1"));
//		aggParfactors.get("agg1").multiply(setOfParfactors, parfactors.get("Cousin"));
//	}
//	
////	@Test
////	public void testFactorCalculationForSumOut() {
////		System.out.println("\nTest (Manual): Factor calculation for sum out");
////		int[] m = {1, 0, 1};  
////		System.out.println(aggParfactors.get("agg2").getFactorValue("true", 2, m));
////	}
//	
////	@Test
////	public void testIntToBinConversion() {
////		System.out.println("\nTest (Manual): Factor calculation for sum out");
////		for (int i = 0; i < 20; i++) {
////			System.out.println(i + " = " + printArray(aggParfactors.get("agg2").getBinaryRepresentationOf(i)));
////		}
////	}
////	
////	private String printArray(Integer[] list) {
////		String result = "";
////		for (Integer x : list) {
////			result += x.toString() + " ";
////		}
////		return result;
////	}
//	
//	@Test
//	public void testSumOut() {
//		System.out.println("\nTest (Manual): Sum out");
//		HashSet<SimpleParfactor> setOfParfactors = new HashSet<SimpleParfactor>();
//		setOfParfactors.add(aggParfactors.get("agg1"));
//		setOfParfactors.add(aggParfactors.get("agg2"));
//		System.out.println(aggParfactors.get("agg2").sumOut(setOfParfactors, variables.get("matched_6")));
//	}
//	
//}
