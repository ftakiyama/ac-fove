package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.List;

import br.usp.poli.takiyama.cfove.prv.ParameterizedRandomVariable;

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
	public static List<Parfactor> globalSumOut(List<Parfactor> setOfParfactors, 
			ParameterizedRandomVariable variable, 
			List<Constraint> setOfConstraints) {
		
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
		
		ArrayList<Parfactor> newSetOfParfactors = new ArrayList<Parfactor>(setOfParfactors);
		Parfactor product = Parfactor.getConstantInstance();
		for (Parfactor p : setOfParfactors) {
			if (p.getParameterizedRandomVariables().contains(variable)) {
				newSetOfParfactors = new ArrayList<Parfactor>(Operations.multiplication(newSetOfParfactors, product, p));
				product = newSetOfParfactors.get(newSetOfParfactors.size() - 1); //ugly
			}
		}
		newSetOfParfactors = new ArrayList<Parfactor>(Operations.liftedElimination(newSetOfParfactors, product, variable));
		
		return newSetOfParfactors;
	}
}
