package br.usp.dml.takiyama.cfove.prv;

import java.util.Vector;

import org.junit.Test;


public class ParameterizedRandomVariableTest {
	@Test
	public void createSimplePrv() {
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		Vector<Term> parameters = new Vector<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i));
		}
		System.out.println(functor.toString());
		ParameterizedRandomVariable prv = new ParameterizedRandomVariable(functor, parameters);
		
		System.out.println(prv.toString());
	}
	
	@Test
	public void applySubstitution() {
		PredicateSymbol functor = new PredicateSymbol("f", "true", "false");
		Vector<Term> parameters = new Vector<Term>();
		for (int i = 0; i < 5; i++) {
			parameters.add(new LogicalVariable("X" + i)); // <- indeed. I create a X term here
		}
		ParameterizedRandomVariable prv = new ParameterizedRandomVariable(functor, parameters);
		
		Vector<Binding> bindings = new Vector<Binding>();
		for (int i = 0; i < 5; i++) {
			bindings.add(Binding.getInstance((LogicalVariable)parameters.get(i), new LogicalVariable("Y" + i)));
			//bindings.add(Binding.getInstance(new LogicalVariable("X" + i), new LogicalVariable("Y" + i))); // <- and another with the same name here.
		}
		System.out.println(bindings.toString());
		
		Substitution substitution = Substitution.getInstance(bindings);
		System.out.println(substitution.toString());
		
		System.out.println(prv.getInstance(substitution));
	}
	
}
