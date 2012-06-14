package br.usp.dml.takiyama.cfove.prv;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Vector;

import org.junit.Test;


public class ParameterizedRandomVariableTest {
	@Test
	public void createSimplePrv() {
		System.out.println("\nTest: Create Simple Parameterized Random Variable");
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		Vector<Term> parameters = new Vector<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i));
		}
		System.out.println("Functor: " + functor.toString());
		ParameterizedRandomVariable prv = new ParameterizedRandomVariable(functor, parameters);
		
		System.out.println("PRV: " + prv.toString());
	}
	
	@Test
	public void applySubstitution() {
		System.out.println("\nTest: Apply Substitution");
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		Vector<Term> parameters = new Vector<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i)); 
		}
		ParameterizedRandomVariable prv = new ParameterizedRandomVariable(functor, parameters);
		
		System.out.println("PRV: " + prv.toString());
		
		Vector<Binding> bindings = new Vector<Binding>();
		for (int i = 0; i < 5; i++) {
			bindings.add(Binding.getInstance((LogicalVariable)parameters.get(i), new LogicalVariable("Y" + i)));
		}
		System.out.println("Bindings: " + bindings.toString());
		
		Substitution substitution = Substitution.getInstance(bindings);
		
		System.out.println("PRV after substitution: " + prv.getInstance(substitution));
	}
	
	/**
	 * Create a prv with 5 logical variables as parameters and test
	 * the method getParameters.
	 */
	@Test
	public void getParameters() {
		System.out.println("\nTest: Get Parameters");
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		Vector<Term> parameters = new Vector<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i)); 
		}
		ParameterizedRandomVariable prv = new ParameterizedRandomVariable(functor, parameters);
		
		System.out.println("PRV: " + prv.toString());
		
		Iterator<LogicalVariable> lv = prv.getParameters();
		boolean testOk = true;
		while (lv.hasNext()) {
			LogicalVariable currentVariable = lv.next();
			System.out.println("Current logical variable is: " + currentVariable.toString());
			if (!parameters.contains(currentVariable)) {
				testOk = false;
				break;
			}
		}
		assertTrue(testOk);
	}
	
	/**
	 * Create a prv with 5 logical variables as parameters and test
	 * the method getParameters after applying a substitution.
	 * Validation must be made manually 
	 */
	@Test
	public void getParametersAfterSubstitution() {
		System.out.println("\nTest: Get Parameters After Substitution");
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		Vector<Term> parameters = new Vector<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i)); 
		}
		ParameterizedRandomVariable prv = new ParameterizedRandomVariable(functor, parameters);
		System.out.println("PRV: " + prv.toString());
		
		Vector<Binding> bindings = new Vector<Binding>();
		bindings.add(Binding.getInstance((LogicalVariable)parameters.get(2), new Constant("y2")));
		Substitution substitution = Substitution.getInstance(bindings);
		System.out.println("Substitution: " + substitution.toString());
		
		Iterator<LogicalVariable> lv = prv.getInstance(substitution).getParameters();
		while (lv.hasNext()) {
			LogicalVariable currentVariable = lv.next();
			System.out.println("Current logical variable is: " + currentVariable.toString());
		}
	}
	
	/**
	 * Test equals. 
	 */
	@Test
	public void equals() {
		System.out.println("\nTest: Equals");
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		Vector<Term> parameters = new Vector<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i)); 
		}
		ParameterizedRandomVariable prv = new ParameterizedRandomVariable(functor, parameters);
		ParameterizedRandomVariable anotherPrv = new ParameterizedRandomVariable(functor, parameters);
		System.out.println("PRV 1: " + prv.toString());
		System.out.println("PRV 2: " + anotherPrv.toString());
		System.out.println("They are equal: " + prv.equals(anotherPrv));
		
		assertTrue(prv.equals(anotherPrv));	
	}
	
	/**
	 * Test equals. 
	 */
	@Test
	public void testEqualsWithIndependentlyCreatedPrvs() {
		System.out.println("\nTest: Equals with independently created PRVs");
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		Vector<Term> parameters = new Vector<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i)); 
		}
		ParameterizedRandomVariable prv = new ParameterizedRandomVariable(functor, parameters);
		
		PredicateSymbol functor2 = new PredicateSymbol("f", "true", "false");
		Vector<Term> parameters2 = new Vector<Term>();
		for (int i = 0; i < 5; i++) {
			parameters2.add(new LogicalVariable("X" + i)); 
		}
		ParameterizedRandomVariable prv2 = new ParameterizedRandomVariable(functor2, parameters2);
		
		System.out.println("PRV 1: " + prv.toString());
		System.out.println("PRV 2: " + prv2.toString());
		System.out.println("They are equal: " + prv.equals(prv2));
		
		assertTrue(prv.equals(prv2));
	}
}
