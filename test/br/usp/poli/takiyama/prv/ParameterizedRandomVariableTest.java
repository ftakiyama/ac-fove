package br.usp.poli.takiyama.prv;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import br.usp.poli.takiyama.common.RandomVariable;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.PredicateSymbol;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

import com.google.common.collect.ImmutableSet;


public class ParameterizedRandomVariableTest {
	
	/**
	 * Creates a boolean parameterized random variable with 3 logical variables
	 * (parameters). Don't change this, otherwise you will have to re-write
	 * all the tests.
	 * @return A boolean PRV with 3 parameters
	 */
	private ParameterizedRandomVariable getBooleanPrv() {
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		ArrayList<Constant> individualsForX1 = new ArrayList<Constant>();
		ArrayList<Constant> individualsForX2 = new ArrayList<Constant>();
		ArrayList<Constant> individualsForX3 = new ArrayList<Constant>();
		
		individualsForX1.add(new Constant("a1"));
		
		individualsForX2.add(new Constant("b1"));
		individualsForX2.add(new Constant("b2"));
		
		individualsForX3.add(new Constant("c1"));
		individualsForX3.add(new Constant("c2"));
		individualsForX3.add(new Constant("c3"));
		
		ArrayList<Term> parameters = new ArrayList<Term>();
		parameters.add(new LogicalVariable("X1", individualsForX1));
		parameters.add(new LogicalVariable("X2", individualsForX2));
		parameters.add(new LogicalVariable("X3", individualsForX3));
		
		return ParameterizedRandomVariable.getInstance(functor, parameters);
	}
	
	@Test
	public void applyCompleteSubstitution() {
		System.out.println("\nTest: Apply Substitution");
		
		ArrayList<Constant> individualsForX1 = new ArrayList<Constant>();
		ArrayList<Constant> individualsForX2 = new ArrayList<Constant>();
		ArrayList<Constant> individualsForX3 = new ArrayList<Constant>();
		
		individualsForX1.add(new Constant("a1"));
		
		individualsForX2.add(new Constant("b1"));
		individualsForX2.add(new Constant("b2"));
		
		individualsForX3.add(new Constant("c1"));
		individualsForX3.add(new Constant("c2"));
		individualsForX3.add(new Constant("c3"));
		
		ParameterizedRandomVariable prv = getBooleanPrv();
		
		System.out.println("PRV: " + prv.toString());
		
		ArrayList<Binding> bindings = new ArrayList<Binding>();
		bindings.add(Binding.create(new LogicalVariable("X1", individualsForX1), new LogicalVariable("Y1", individualsForX1)));
		bindings.add(Binding.create(new LogicalVariable("X2", individualsForX2), new LogicalVariable("Y2", individualsForX2)));
		bindings.add(Binding.create(new LogicalVariable("X3", individualsForX3), new LogicalVariable("Y3", individualsForX3)));
		
		System.out.println("Substitution: " + bindings.toString());
		
		Substitution substitution = Substitution.create(bindings);
		
		System.out.println("PRV after substitution: " + prv.applySubstitution(substitution));
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		ArrayList<Term> parameters = new ArrayList<Term>();
		parameters.add(new LogicalVariable("Y1", individualsForX1));
		parameters.add(new LogicalVariable("Y2", individualsForX2));
		parameters.add(new LogicalVariable("Y3", individualsForX3));
		
		ParameterizedRandomVariable correctResult = ParameterizedRandomVariable.getInstance(functor, parameters);
		
		assertTrue(prv.applySubstitution(substitution).equals(correctResult));
	}
	
	/**
	 * Test equals. 
	 */
	@Test
	public void testEquals() {
		System.out.println("\nTest: Equals");
		
		ArrayList<Constant> individualsForX1 = new ArrayList<Constant>();
		ArrayList<Constant> individualsForX2 = new ArrayList<Constant>();
		ArrayList<Constant> individualsForX3 = new ArrayList<Constant>();
		
		individualsForX1.add(new Constant("a1"));
		
		individualsForX2.add(new Constant("b1"));
		individualsForX2.add(new Constant("b2"));
		
		individualsForX3.add(new Constant("c1"));
		individualsForX3.add(new Constant("c2"));
		individualsForX3.add(new Constant("c3"));
		
		ParameterizedRandomVariable correctResult = getBooleanPrv();
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		ArrayList<Term> parameters = new ArrayList<Term>();

		parameters.add(new LogicalVariable("X1", individualsForX1));
		parameters.add(new LogicalVariable("X2", individualsForX2));
		parameters.add(new LogicalVariable("X3", individualsForX3));
		
		ParameterizedRandomVariable prv = ParameterizedRandomVariable.getInstance(functor, parameters);
		
		functor = new PredicateSymbol("f", "true", "false");
		parameters = new ArrayList<Term>();
		parameters.add(new LogicalVariable("X1", individualsForX1));
		parameters.add(new LogicalVariable("X2", individualsForX2));
		parameters.add(new LogicalVariable("X3", individualsForX3));
		
		ParameterizedRandomVariable prv2 = ParameterizedRandomVariable.getInstance(functor, parameters);
		
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
		
		ArrayList<Constant> individualsForX1 = new ArrayList<Constant>();
		ArrayList<Constant> individualsForX2 = new ArrayList<Constant>();
		ArrayList<Constant> individualsForX3 = new ArrayList<Constant>();
		
		individualsForX1.add(new Constant("a1"));
		
		individualsForX2.add(new Constant("b1"));
		individualsForX2.add(new Constant("b2"));
		
		individualsForX3.add(new Constant("c1"));
		individualsForX3.add(new Constant("c2"));
		individualsForX3.add(new Constant("c3"));
		

		ArrayList<Binding> bindings = new ArrayList<Binding>();
		bindings.add(Binding.create(new LogicalVariable("X1", individualsForX1), new Constant("y1")));
		bindings.add(Binding.create(new LogicalVariable("X3", individualsForX3), new Constant("y4")));
		
		System.out.println("Substitution: " + bindings.toString());
		
		Substitution substitution = Substitution.create(bindings);
		
		ParameterizedRandomVariable prv = getBooleanPrv().applySubstitution(substitution);

		System.out.println("After substitution: " + prv.toString());
		System.out.println("Parameters: " + prv.getParameters().toString());
		
		ImmutableSet<LogicalVariable> correctResult = ImmutableSet
			.of(new LogicalVariable("X2", individualsForX2));
		
		assertTrue(prv.getParameters().equals(correctResult));
	}
	
	@Test
	public void testGetGroundInstance() throws Exception {
		System.out.println("\nTest: Get Ground Instances");
		
		ParameterizedRandomVariable prv = getBooleanPrv();
		
		for (int i = 0; i < 6; i++) {
			System.out.println(prv.getGroundInstance(i));
		}
		
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("true");
		domain.add("false");
		assertTrue(prv
			.getGroundInstance(0)
				.equals(RandomVariable
					.createRandomVariable("f ( a1 b1 c1 )", domain))
			&& prv
				.getGroundInstance(1)
					.equals(RandomVariable
						.createRandomVariable("f ( a1 b2 c1 )", domain))
			&& prv
				.getGroundInstance(2)
					.equals(RandomVariable
						.createRandomVariable("f ( a1 b1 c2 )", domain))
			&& prv
				.getGroundInstance(3)
					.equals(RandomVariable
						.createRandomVariable("f ( a1 b2 c2 )", domain))
			&& prv
				.getGroundInstance(4)
					.equals(RandomVariable
						.createRandomVariable("f ( a1 b1 c3 )", domain))
			&& prv
				.getGroundInstance(5)
					.equals(RandomVariable
						.createRandomVariable("f ( a1 b2 c3 )", domain)));
	}
	
	@Test(expected = IllegalStateException.class)
	public void throwGetGroundInstanceIllegalStateException() {
		
		ArrayList<Constant> individualsForX1 = new ArrayList<Constant>();
		ArrayList<Constant> individualsForX2 = new ArrayList<Constant>();
		ArrayList<Constant> individualsForX3 = new ArrayList<Constant>();
		
		individualsForX1.add(new Constant("a1"));
		
		individualsForX2.add(new Constant("b1"));
		individualsForX2.add(new Constant("b2"));
		
		individualsForX3.add(new Constant("c1"));
		individualsForX3.add(new Constant("c2"));
		individualsForX3.add(new Constant("c3"));
		
		ArrayList<Binding> bindings = new ArrayList<Binding>();
		bindings.add(Binding.create(new LogicalVariable("X1", individualsForX1), new Constant("y1")));
		bindings.add(Binding.create(new LogicalVariable("X3", individualsForX3), new Constant("y3")));
				
		Substitution substitution = Substitution.create(bindings);
		
		ParameterizedRandomVariable prv = getBooleanPrv().applySubstitution(substitution);
		
		prv.getGroundInstance(0);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwGetGroundInstanceIllegalArgumentException() {
		getBooleanPrv().getGroundInstance(6);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getGroundInstanceWithNegativeIndex() {
		getBooleanPrv().getGroundInstance(-1);
	}
	
	@Test
	public void testGetGroundInstanceWithOneLogicalVariable() {
		System.out.println("\nTest: Get Ground Instances");
		
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		ArrayList<Constant> individualsForX1 = new ArrayList<Constant>();
		
		individualsForX1.add(new Constant("a1"));
		individualsForX1.add(new Constant("a2"));
		individualsForX1.add(new Constant("a3"));
		individualsForX1.add(new Constant("a4"));
		
		ArrayList<Term> parameters = new ArrayList<Term>();
		parameters.add(new LogicalVariable("X1", individualsForX1));
		
		ParameterizedRandomVariable prv = ParameterizedRandomVariable.getInstance(functor, parameters);
		
		for (int i = 0; i < 4; i++) {
			System.out.println(prv.getGroundInstance(i));
		}
		
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("true");
		domain.add("false");
		
		assertTrue(prv
			.getGroundInstance(0)
				.equals(RandomVariable
					.createRandomVariable("f ( a1 )", domain))
			&& prv
				.getGroundInstance(1)
					.equals(RandomVariable
						.createRandomVariable("f ( a2 )", domain))
			&& prv
				.getGroundInstance(2)
					.equals(RandomVariable
						.createRandomVariable("f ( a3 )", domain))
			&& prv
				.getGroundInstance(3)
					.equals(RandomVariable
						.createRandomVariable("f ( a4 )", domain))
		);
	}
}
