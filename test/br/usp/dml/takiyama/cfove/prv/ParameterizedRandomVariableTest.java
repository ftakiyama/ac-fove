package br.usp.dml.takiyama.cfove.prv;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Vector;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;


public class ParameterizedRandomVariableTest {
	
	/**
	 * Creates a boolean parameterized random variable with 5 logical variables
	 * (parameters).
	 * @return A boolean PRV with 5 parameters
	 */
	private ParameterizedRandomVariable getBooleanPrv() {
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		ArrayList<Term> parameters = new ArrayList<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i));
		}
		return new ParameterizedRandomVariable(functor, parameters);
	}
	
	@Test
	public void applyCompleteSubstitution() {
		System.out.println("\nTest: Apply Substitution");
		
		ParameterizedRandomVariable prv = getBooleanPrv();
		
		System.out.println("PRV: " + prv.toString());
		
		ArrayList<Binding> bindings = new ArrayList<Binding>();
		for (int i = 0; i < 5; i++) {
			bindings.add(Binding.create(new LogicalVariable("X" + i), new LogicalVariable("Y" + i)));
		}
		
		System.out.println("Substitution: " + bindings.toString());
		
		Substitution substitution = Substitution.create(bindings);
		
		System.out.println("PRV after substitution: " + prv.getInstance(substitution));
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		ArrayList<Term> parameters = new ArrayList<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("Y" + i));
		}
		ParameterizedRandomVariable correctResult = new ParameterizedRandomVariable(functor, parameters);
		
		assertTrue(prv.getInstance(substitution).equals(correctResult));
	}
	
	/**
	 * Test equals. 
	 */
	@Test
	public void testEquals() {
		System.out.println("\nTest: Equals");
		
		ParameterizedRandomVariable correctResult = getBooleanPrv();
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		ArrayList<Term> parameters = new ArrayList<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i)); 
		}
		ParameterizedRandomVariable prv = new ParameterizedRandomVariable(functor, parameters);
		
		functor = new PredicateSymbol("f", "true", "false");
		parameters = new ArrayList<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i)); 
		}
		ParameterizedRandomVariable prv2 = new ParameterizedRandomVariable(functor, parameters);
		
		System.out.println("PRV 1: " + prv.toString());
		System.out.println("Correct result: " + correctResult.toString());
		System.out.println("They are equal: " + prv.equals(correctResult));
		System.out.println("Symmetry: " + correctResult.equals(prv));
		System.out.println("Reflexivity: " + prv.equals(prv));
		System.out.println("Transitivity: " + (prv.equals(prv2) ? prv2.equals(prv) : true));
		System.out.println("Hash code: " + (prv.hashCode() == correctResult.hashCode()));
		
		assertTrue(prv.equals(correctResult)
				&& correctResult.equals(prv) 		            		  // symmetry
				&& prv.equals(prv) 				                		  // reflexivity
				&& (prv.equals(prv2) ? prv2.equals(correctResult) : true) // transitivity
				&& prv.hashCode() == correctResult.hashCode()); 		  // hash code
		
	}
	
	@Test
	public void testGetParameters() {
		System.out.println("\nTest: Get Parameters");
		
		ArrayList<Binding> bindings = new ArrayList<Binding>();
		bindings.add(Binding.create(new LogicalVariable("X1"), new Constant("y1")));
		bindings.add(Binding.create(new LogicalVariable("X4"), new Constant("y4")));
		
		System.out.println("Substitution: " + bindings.toString());
		
		Substitution substitution = Substitution.create(bindings);
		
		ParameterizedRandomVariable prv = getBooleanPrv().getInstance(substitution);

		System.out.println("After substitution: " + prv.toString());
		System.out.println("Parameters: " + prv.getParameters().toString());
		
		ImmutableSet<LogicalVariable> correctResult = ImmutableSet
			.of(new LogicalVariable("X0"),
				new LogicalVariable("X2"),
				new LogicalVariable("X3"));
		
		assertTrue(prv.getParameters().equals(correctResult));
	}
}
