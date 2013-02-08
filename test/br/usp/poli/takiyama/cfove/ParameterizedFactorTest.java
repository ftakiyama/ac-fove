package br.usp.poli.takiyama.cfove;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Tuple;
import br.usp.poli.takiyama.prv.PRV;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

public class ParameterizedFactorTest {
	
	private HashMap<String, ParameterizedRandomVariable> variables;
	private HashMap<String, ParameterizedFactor> factors;
	
	@Before
	public void initialSetup() {
		
		// Pool of PRVs
		variables = new HashMap<String, ParameterizedRandomVariable>();
		for (int i = 0; i < 3; i++) {
			String name = "f" + i;
			variables.put(name,
						  PRV.getBooleanPrvWithOneParameter(name, i));
		}
		for (int i = 0; i < 3; i++) {
			String name = "g" + i;
			variables.put(name,
						  PRV.getBooleanPrvWithTwoParameters(name, i, i + 1));
		}
		for (int i = 0; i < 3; i++) {
			String name = "h" + i;
			variables.put(name,
						  PRV.getBooleanPrvWithThreeParameters(name, i, i + 1, i + 2));
		}
		
		// Test factors
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
	}
	
	@Test
	public void testParameterizedRandomVariableIndex() {
		System.out.println("\nTest: getParameterizedRandomVariableIndex()");
		
		ParameterizedFactor factor = factors.get("factor3");
		assertTrue(factor.getParameterizedRandomVariableIndex(variables.get("f0")) == 0 &&
				   factor.getParameterizedRandomVariableIndex(variables.get("g0")) == 1 &&
				   factor.getParameterizedRandomVariableIndex(variables.get("h0")) == 2);
		
		System.out.println("Index of f0: " + factor.getParameterizedRandomVariableIndex(variables.get("f0")));
		System.out.println("Index of g0: " + factor.getParameterizedRandomVariableIndex(variables.get("g0")));
		System.out.println("Index of h0: " + factor.getParameterizedRandomVariableIndex(variables.get("h0")));
		
	}
	
	@Test
	public void testTupleObtainment() {
		System.out.println("\nTest: getTuple()");
		
		ParameterizedFactor factor = factors.get("factor3");
		
		ArrayList<Integer> tuple = new ArrayList<Integer>();
		tuple.add(0); tuple.add(0); tuple.add(0);
		Tuple t0 = new Tuple(tuple);
		
		tuple.clear();
		tuple.add(0); tuple.add(0); tuple.add(1);
		Tuple t1 = new Tuple(tuple);

		tuple.clear();
		tuple.add(0); tuple.add(1); tuple.add(0);
		Tuple t2 = new Tuple(tuple);

		tuple.clear();
		tuple.add(0); tuple.add(1); tuple.add(1);
		Tuple t3 = new Tuple(tuple);
		
		tuple.clear();
		tuple.add(1); tuple.add(0); tuple.add(0);
		Tuple t4 = new Tuple(tuple);
		
		tuple.clear();
		tuple.add(1); tuple.add(0); tuple.add(1);
		Tuple t5 = new Tuple(tuple);
		
		tuple.clear();
		tuple.add(1); tuple.add(1); tuple.add(0);
		Tuple t6 = new Tuple(tuple);
		
		tuple.clear();
		tuple.add(1); tuple.add(1); tuple.add(1);
		Tuple t7 = new Tuple(tuple);
		
		assertTrue(factor.getTuple(0).equals(t0) && 
				   factor.getTuple(1).equals(t1) && 
				   factor.getTuple(2).equals(t2) && 
				   factor.getTuple(3).equals(t3) && 
				   factor.getTuple(4).equals(t4) && 
				   factor.getTuple(5).equals(t5) && 
				   factor.getTuple(6).equals(t6) && 
				   factor.getTuple(7).equals(t7));
		
		for (int i = 0; i < factor.size(); i++) {
			System.out.println("Tuple " + i + ":" + factor.getTuple(i));
		}
		
	}
	
	@Test
	public void testTupleIndex() {
		System.out.println("\nTest: getTupleIndex()");
		
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		prvs.add(variables.get("f1"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(0.1);
		mapping.add(0.2);
		mapping.add(0.3);
		mapping.add(0.4);
		ParameterizedFactor factor = ParameterizedFactor.getInstance("factor", prvs, mapping);
		
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		boolean allTestsOk = true;
		
		indexes.add(0);
		indexes.add(0);
		System.out.println("Index of tuple " + indexes + ": " + factor.getTupleIndex(new Tuple(indexes)));
		if (factor.getTupleIndex(new Tuple(indexes)) != 0) allTestsOk = false;
		
		indexes.clear();
		indexes.add(0);
		indexes.add(1);
		System.out.println("Index of tuple " + indexes + ": " + factor.getTupleIndex(new Tuple(indexes)));
		if (factor.getTupleIndex(new Tuple(indexes)) != 1) allTestsOk = false;

		indexes.clear();
		indexes.add(1);
		indexes.add(0);
		System.out.println("Index of tuple " + indexes + ": " + factor.getTupleIndex(new Tuple(indexes)));
		if (factor.getTupleIndex(new Tuple(indexes)) != 2) allTestsOk = false;

		indexes.clear();
		indexes.add(1);
		indexes.add(1);
		System.out.println("Index of tuple " + indexes + ": " + factor.getTupleIndex(new Tuple(indexes)));
		if (factor.getTupleIndex(new Tuple(indexes)) != 3) allTestsOk = false;

		assertTrue(allTestsOk);
		
	}
	
	@Test
	public void testEquals() {
		System.out.println("\nTest: equals()");
		String name = "factor3";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		prvs.add(variables.get("g0"));
		prvs.add(variables.get("h0"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(0.1);
		mapping.add(0.2);
		mapping.add(0.3);
		mapping.add(0.4);
		mapping.add(0.5);
		mapping.add(0.6);
		mapping.add(0.7);
		mapping.add(0.8);
		ParameterizedFactor correctResult = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		name = "factor3";
		prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		prvs.add(variables.get("g0"));
		prvs.add(variables.get("h0"));
		mapping = new ArrayList<Number>();
		mapping.add(0.1);
		mapping.add(0.2);
		mapping.add(0.3);
		mapping.add(0.4);
		mapping.add(0.5);
		mapping.add(0.6);
		mapping.add(0.7);
		mapping.add(0.8);
		ParameterizedFactor transition = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		ParameterizedFactor factor = factors.get("factor3");
		
		assertTrue(factor.equals(correctResult) &&
				   correctResult.equals(factor) &&
				   factor.equals(factor) &&
				   ((factor.equals(transition)) ? transition.equals(correctResult) : true) &&
				   factor.hashCode() == correctResult.hashCode());
		
		System.out.println("       Equals: " + factor.equals(correctResult));
		System.out.println("     Symmetry: " + correctResult.equals(factor));
		System.out.println("Reflexitivity: " + factor.equals(factor));
		System.out.println(" Transitivity: " + ((factor.equals(transition)) ? transition.equals(correctResult) : true));
		System.out.println("    Hash code: " + (factor.hashCode() == correctResult.hashCode()));
		
	}
	
	@Test
	public void testPrecisionOfEquals() {
		System.out.println("\nTest: precision of equals()");
		String name = "factor1";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(0.1000000000000001);
		mapping.add(0.2);
		ParameterizedFactor correctResult = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		name = "factor1";
		prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		mapping = new ArrayList<Number>();
		mapping.add(0.1000000000000001);
		mapping.add(0.2);
		ParameterizedFactor transition = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		ParameterizedFactor factor = factors.get("factor1");
		
		System.out.println("       Equals: " + factor.equals(correctResult));
		System.out.println("     Symmetry: " + correctResult.equals(factor));
		System.out.println("Reflexitivity: " + factor.equals(factor));
		System.out.println(" Transitivity: " + ((factor.equals(transition)) ? transition.equals(correctResult) : true));
		System.out.println("    Hash code: " + (factor.hashCode() == correctResult.hashCode()));
		
		assertFalse(factor.equals(correctResult) &&
				    correctResult.equals(factor) &&
				    factor.equals(factor) &&
				    ((factor.equals(transition)) ? transition.equals(correctResult) : true) &&
				    factor.hashCode() == correctResult.hashCode());
	}
	
	@Test
	public void testEqualsWithDifferentFactors() {
		System.out.println("\nTest: equals() with different objects");
		String name = "factor2";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(0.1);
		mapping.add(0.2);
		ParameterizedFactor differentName = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		name = "factor1";
		prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f1"));
		mapping = new ArrayList<Number>();
		mapping.add(0.1);
		mapping.add(0.2);
		ParameterizedFactor differentVariable = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		name = "factor1";
		prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		mapping = new ArrayList<Number>();
		mapping.add(1);
		mapping.add(2);
		ParameterizedFactor differentMapping = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		
		ParameterizedFactor factor = factors.get("factor1");
		
		System.out.println("Different variable: " + factor.equals(differentVariable));
		System.out.println(" Different mapping: " + factor.equals(differentMapping));
		
		assertTrue(!factor.equals(differentVariable) &&
				   !factor.equals(differentMapping));
	}
	
	@Test
	public void testSubFactorWithEmptyFactor() {
		String name = "factor2";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		ArrayList<Number> mapping = new ArrayList<Number>();
		
		ParameterizedFactor emptyFactor = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		ParameterizedFactor factor = factors.get("factor2");
		
		assertTrue(emptyFactor.isSubFactorOf(factor));
	}
	
	@Test
	public void testSubFactorWithEqualFactors() {
		String name = "factor2";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		prvs.add(variables.get("g0"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(0.1);
		mapping.add(0.2);
		mapping.add(0.3);
		mapping.add(0.4);
		
		ParameterizedFactor clone = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		ParameterizedFactor factor = factors.get("factor2");
		
		assertTrue(clone.isSubFactorOf(factor));
	}
	
	@Test
	public void testSubFactorWithNonSubFactor() {
		String name = "factor1";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("g0"));
		prvs.add(variables.get("h0"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(0.1);
		mapping.add(0.2);
		mapping.add(0.3);
		mapping.add(0.4);
		
		ParameterizedFactor falseSubFactor = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		ParameterizedFactor factor = factors.get("factor2");
		
		assertFalse(falseSubFactor.isSubFactorOf(factor));
	}
	
	@Test
	public void testSubFactorWithSubFactor() {
		String name = "subfactor";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("g0"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(1);
		mapping.add(2);
		
		ParameterizedFactor subFactor = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		ParameterizedFactor factor = factors.get("factor2");
		
		assertTrue(subFactor.isSubFactorOf(factor));
	}
	
	
	/***************************************************************************
	 * SUM OUT
	 **************************************************************************/
	
	@Test
	public void testSumOutFirstVariable() {
		System.out.println("\nTest: Sum out f0 from factor2");
		
		String name = "factor2";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("g0"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(0.1 + 0.3);
		mapping.add(0.2 + 0.4);
		ParameterizedFactor correctResult = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		ParameterizedFactor factor = factors.get("factor2");
		System.out.println("Factor to sum out: " + factor.toString());
		factor = factor.sumOut(variables.get("f0"));
		
		System.out.println("After summing out: " + factor.toString());
		System.out.println("Correct result: " + correctResult.toString());
		
		assertTrue(factor.equals(correctResult));
	}
	
	@Test
	public void testSumOutSecondVariable() {
		System.out.println("\nTest: Sum out g0 from factor2");
		
		String name = "factor2";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(0.1 + 0.2);
		mapping.add(0.3 + 0.4);
		ParameterizedFactor correctResult = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		ParameterizedFactor factor = factors.get("factor2");
		System.out.println("Factor to sum out: " + factor.toString());
		factor = factor.sumOut(variables.get("g0"));
		
		System.out.println("After summing out: " + factor.toString());
		System.out.println("Correct result: " + correctResult.toString());
		
		assertTrue(factor.equals(correctResult));
	}
	
	@Test
	public void testBasicMultiplication() {
		System.out.println("\nTest: factor1 x factor2");
		
		String name = "factor1";
		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
		prvs.add(variables.get("f0"));
		prvs.add(variables.get("g0"));
		ArrayList<Number> mapping = new ArrayList<Number>();
		mapping.add(0.1 * 0.1);
		mapping.add(0.1 * 0.2);
		mapping.add(0.2 * 0.3);
		mapping.add(0.2 * 0.4);
		ParameterizedFactor correctResult = ParameterizedFactor.getInstance(name, prvs, mapping);
		
		ParameterizedFactor factor = factors.get("factor1").multiply(factors.get("factor2"));
		
		System.out.println("After multiplication: " + factor.toString());
		System.out.println("Correct result: " + correctResult.toString());
		
		assertTrue(factor.equals(correctResult));
	}
	
}
