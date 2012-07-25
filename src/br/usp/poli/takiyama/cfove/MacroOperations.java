package br.usp.poli.takiyama.cfove;

import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

public final class MacroOperations {
	
	// Enforces non-instantiability
	private MacroOperations() { }
	
	
	/**
	 * Given a set of parfactors &Phi;, a parameterized random variable f(...) and a 
	 * set of constraints C, the macro-operation multiplies all parfactors from 
	 * &Phi; containing parameterized random variables that represent 
	 * ground(f(...)) : C and eliminates random variables ground(f(...)) : C 
	 * from the product; the macro-operation is only applicable if the product 
	 * satisfies the pre-conditions for Lifted Elimination.
	 * <br>
	 * Multiplication operations can always be performed because of initial shattering. 
	 * <br>
	 * This is the only macro-operation that eliminates random variables from J(&Phi;).
	 * @param setOfParfactors
	 * @param variable
	 * @param setOfConstraints
	 */
	public static Set<Parfactor> globalSumOut(Set<Parfactor> setOfParfactors, 
			ParameterizedRandomVariable variable, 
			Set<Constraint> setOfConstraints) {
		
		/*
		 * Input:  set of parfactors Phi
		 * 	       parameterized random variable f
		 *         set of constraints C
		 * Output: set of parfactors Phi with f summed out
		 * 
		 * product := unary parfactor
		 * for each parfactor p in Phi
		 *     if p contains PRV v such that ground(v):C = ground(f):C
		 *         product := product * p
		 *         remove p from Phi
		 * if product satisfies pre-condition for elimination
		 *     result := eliminate f from product
		 * return result
		 */
		
		HashSet<Parfactor> newSetOfParfactors = new HashSet<Parfactor>(setOfParfactors);
		Parfactor product = SimpleParfactor.getConstantInstance(); //TODO: it would be better to do the first multiplication outside the loop
		for (Parfactor p : setOfParfactors) {
			if (p.getParameterizedRandomVariables().contains(variable)) {
				newSetOfParfactors = new HashSet<Parfactor>(p.multiply(newSetOfParfactors, product));
				HashSet<Parfactor> copyOfNewSet = new HashSet<Parfactor>(newSetOfParfactors);
				copyOfNewSet.removeAll(setOfParfactors);
				product = copyOfNewSet.iterator().hasNext() ? copyOfNewSet.iterator().next() : null;
				
				if (product == null) {
					throw new NullPointerException("The new set is empty!: " + copyOfNewSet);
				}
			}
		}
		newSetOfParfactors = new HashSet<Parfactor>(product.sumOut(newSetOfParfactors, variable));
		
		return newSetOfParfactors;
	}
	
	
	public static boolean conditionsForGlobalSumOutAreMet(Set<Parfactor> setOfParfactors, 
			ParameterizedRandomVariable variable, 
			Set<Constraint> setOfConstraints) {
		// remember that multiplication may be over aggregations or simple
		// how can i check the sum out condition without doing the multiplication?
		return true;
	}
}
