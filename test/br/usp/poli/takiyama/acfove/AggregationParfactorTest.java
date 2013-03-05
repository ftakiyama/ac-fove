package br.usp.poli.takiyama.acfove;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.Pool;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

/**
 * Unit tests for Aggregation parfactors.
 * @author ftakiyama
 *
 */
public class AggregationParfactorTest {

	private Pool objects;
	
	@Before
	public void setUp() {
		objects = new Pool();
	}
	
	/**
	 * Example 3.9 of Kisysnki (2010)
	 */
	@Test
	public void conversionToParfactorWithCountingFormula() {
		
		objects.setExample3_9(10);
		
		AggregationParfactor ag = objects.getAggParfactor("ag");
		
		List<Parfactor> result = ag.convertToParfactor();
		
		List<Parfactor> answer = new ArrayList<Parfactor>(2);
		answer.add(objects.getSimpleParfactor("g1"));
		answer.add(objects.getSimpleParfactor("g2"));
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; &empty;, p(A,B), c(B), 1, OR, &empty; &rang; 
	 * on substitution {B/x1}.
	 * <br>
	 * <br>
	 * Result should be
	 * J( { &lang; &empty;, p(A,x1), c(x1), 1, OR, &empty; &rang;,      
	 * 		&lang; {B &ne; x1}, p(A,B), c(B), 1, OR, &empty; &rang; } ).
	 */
	@Test
	public void testSimpleSplit() {
		
		objects.setSplitAggParfactorTest();
		
		AggregationParfactor ag = objects.getAggParfactor("ag1");
		Binding substitution = objects.getBinding("B/1");
		List<Parfactor> result = ag.split(substitution);
		
		List<Parfactor> answer = objects.getParfactorList("m1");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; &empty;, p(A,B), c(B), 1, OR, &empty; &rang; 
	 * on substitution {A/x1}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; &empty;, p(A,B), c'(B), 1, OR, {A&ne;x1} &rang;,      
	 * 		&lang; &empty;, { p(x1,B), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test
	public void testSplitOnExtraWithConstant() {
		
		objects.setSplitAggParfactorTest();

		AggregationParfactor ag = objects.getAggParfactor("ag1");
		Binding substitution = objects.getBinding("A/1");
		List<Parfactor> result = ag.split(substitution);
		
		List<Parfactor> answer = objects.getParfactorList("m2");  
				
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; &empty;, p(A,B), c(B), 1, OR, &empty; &rang; 
	 * on substitution {A/B}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; &empty;, p(A,B), c'(B), 1, OR, {A&ne;B} &rang;,      
	 * 		&lang; &empty;, { p(B,B), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test 
	public void testSplitOnExtraWithVariable() {
		
		objects.setSplitAggParfactorTest();

		AggregationParfactor ag = objects.getAggParfactor("ag1");
		Binding substitution = objects.getBinding("A/B");
		List<Parfactor> result = ag.split(substitution);
		
		List<Parfactor> answer = objects.getParfactorList("m3");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; &empty;, p(A,B), c(B), 1, OR, &empty; &rang; 
	 * on substitution {B/A}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; &empty;, p(A,B), c'(B), 1, OR, {A&ne;B} &rang;,      
	 * 		&lang; &empty;, { p(A,A), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test 
	public void testSplitWithExtra() {
		
		objects.setSplitAggParfactorTest();

		AggregationParfactor ag = objects.getAggParfactor("ag1");
		Binding substitution = objects.getBinding("B/A");
		List<Parfactor> result = ag.split(substitution);
		
		List<Parfactor> answer = objects.getParfactorList("m4");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; {B&ne;x2}, p(A,B), c(B), 1, OR, {A&ne;x2} &rang; 
	 * on substitution {B/x1}.
	 * <br>
	 * <br>
	 * Result should be
	 * J( { &lang; &empty;, p(A,x1), c(x1), 1, OR, {A&ne;x2} &rang;,      
	 * 		&lang; {B&ne;x1,B&ne;x2}, p(A,B), c(B), 1, OR, {A&ne;x2} &rang; } ).
	 */
	@Test
	public void testSimpleSplitConstrainedParfactor() {
		
		objects.setSplitAggParfactorTest();

		AggregationParfactor ag = objects.getAggParfactor("ag2");
		Binding substitution = objects.getBinding("B/1");
		List<Parfactor> result = ag.split(substitution);
		
		List<Parfactor> answer = objects.getParfactorList("m5");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; {B&ne;x2}, p(A,B), c(B), 1, OR, {A&ne;x2} &rang; 
	 * on substitution {A/x1}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; {B&ne;x2}, p(A,B), c'(B), 1, OR, {A&ne;x1,A&ne;x2} &rang;,      
	 * 		&lang; {B&ne;x2}, { p(x1,B), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test
	public void testSplitConstrainedParfactorOnExtraWithConstant() {
		
		objects.setSplitAggParfactorTest();

		AggregationParfactor ag = objects.getAggParfactor("ag2");
		Binding substitution = objects.getBinding("A/1");
		List<Parfactor> result = ag.split(substitution);
		
		List<Parfactor> answer = objects.getParfactorList("m6");
		
		assertTrue(result.equals(answer));
		
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; {B&ne;x2}, p(A,B), c(B), 1, OR, {A&ne;x2} &rang; 
	 * on substitution {A/B}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; {B&ne;x2}, p(A,B), c'(B), 1, OR, {A&ne;B,A&ne;x2} &rang;,      
	 * 		&lang; {B&ne;x2}, { p(B,B), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test 
	public void testSplitConstrainedParfactorOnExtraWithVariable() {
		
		objects.setSplitAggParfactorTest();

		AggregationParfactor ag = objects.getAggParfactor("ag2");
		Binding substitution = objects.getBinding("A/B");
		List<Parfactor> result = ag.split(substitution);
		
		List<Parfactor> answer = objects.getParfactorList("m7");
		
		assertTrue(result.equals(answer));
		
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; {B&ne;x2}, p(A,B), c(B), 1, OR, {A&ne;x2} &rang; 
	 * on substitution {B/A}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; {B&ne;x2}, p(A,B), c'(B), 1, OR, {A&ne;B,A&ne;x2} &rang;,      
	 * 		&lang; {A&ne;x2}, { p(A,A), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test 
	public void testSplitConstrainedParfactorWithExtra() {
		
		objects.setSplitAggParfactorTest();

		AggregationParfactor ag = objects.getAggParfactor("ag2");
		Binding substitution = objects.getBinding("B/A");
		List<Parfactor> result = ag.split(substitution);
		
		List<Parfactor> answer = objects.getParfactorList("m8");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Example 3.12 of Kisynski (2010).
	 * <br>
	 * Given the set of parfactors
	 * <br>
	 * &Phi; = {<br>
	 * &lang; &empty;, {played(Person)}, Fplayed &rang;,<br>
	 * &lang; &empty;, {played(Person), matched_6(Person}, Fmatched_6 &rang;,<br> 
	 * &lang; &empty;, matched_6(Person), jackpot_won(Person), 1, OR, &empty; &rang; },
	 * <br>
	 * <br>
	 * we want to calculate J<sub>ground(jackpot_won())</sub>(&Phi;).
	 * <br>
	 * <br>
	 * The partial result in the calculation is parfactor
	 * &lang; &empty;, matched_6(Person), jackpot_won(Person), F, OR, &empty; &rang;,
	 * where
	 * F = 1 &odot; &sum;<sub>played(Person)</sub>(Fplayed &odot; Fmatched_6).
	 */
	@Test
	public void testTrivialMultiplication() {
		
		objects.setMultiplicationAggParfactor();
		
		Parfactor g1 = objects.getSimpleParfactor("g1");
		Parfactor g2 = objects.getSimpleParfactor("g2");
		Parfactor g3 = objects.getAggParfactor("g3");
		Parfactor temp = g1.multiply(g2);
		ParameterizedRandomVariable played = 
				objects.getParameterizedRandomVariable("played");
		temp = temp.sumOut(played);
		Parfactor result = g3.multiply(temp);
		Parfactor answer = objects.getAggParfactor("g4");
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Multiplies
	 * <br>
	 * h1 = &lang; {A&ne;x1,A&ne;x2,A&ne;B,B&ne;x3}, {p(A,B)}, F1 &rang;
	 * <br>
	 * with
	 * <br>
	 * h2 = &lang; {B&ne;x3}, p(A,B), c(B), F2, OR, {A&ne;x1,A&ne;x2,A&ne;B} &rang;
	 * <br>
	 * <br>
	 * The result is
	 * h3 = &lang; {B&ne;x3}, p(A,B), c(B), F1&odot;F2, OR, {A&ne;x1,A&ne;x2,A&ne;B} &rang;
	 */
	@Test
	public void testMultiplication() {

		objects.setMultiplicationAggParfactor();
		
		Parfactor g1 = objects.getSimpleParfactor("h1");
		Parfactor g2 = objects.getAggParfactor("h2");
		Parfactor result = g2.multiply(g1);
		
		Parfactor answer = objects.getAggParfactor("h3");

		assertTrue(result.equals(answer));
	}
	
	/**
	 * Tests reflexivity.
	 * <br>
	 * Given two parfactors g1 and g2, g1 &odot; g2 = g2 &odot; g1.
	 */
	@Test
	public void testMultiplicationReflexivity() {
		
		objects.setMultiplicationAggParfactor();
		
		Parfactor g1 = objects.getSimpleParfactor("h1");
		Parfactor g2 = objects.getAggParfactor("h2");
		Parfactor result = g2.multiply(g1);
		Parfactor sameResult = g1.multiply(g2); 
		
		assertTrue(result.equals(sameResult) 
				&& sameResult.equals(result));
	}
}

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
