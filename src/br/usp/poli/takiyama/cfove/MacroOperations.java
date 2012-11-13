//package br.usp.poli.takiyama.cfove;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Set;
//
//import br.usp.poli.takiyama.acfove.AggregationParfactor;
//import br.usp.poli.takiyama.common.Constraint;
//import br.usp.poli.takiyama.prv.LogicalVariable;
//import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
//
//public final class MacroOperations {
//	
//	// Enforces non-instantiability
//	private MacroOperations() { }
//	
//	
//	/**
//	 * Given a set of parfactors &Phi;, a parameterized random variable f(...) and a 
//	 * set of constraints C, the macro-operation multiplies all parfactors from 
//	 * &Phi; containing parameterized random variables that represent 
//	 * ground(f(...)) : C and eliminates random variables ground(f(...)) : C 
//	 * from the product; the macro-operation is only applicable if the product 
//	 * satisfies the pre-conditions for Lifted Elimination. This method does
//	 * not verify those pre-conditions, they must be checked separately using
//	 * the appropriate method from this class.
//	 * <br>
//	 * <br>
//	 * Multiplication operations can always be performed because of initial shattering. 
//	 * <br>
//	 * <br>
//	 * This is the only macro-operation that eliminates random variables from J(&Phi;).
//	 * <br>
//	 * <br>
//	 * 
//	 * @param setOfParfactors The set of parfactors from which the variable
//	 * will be eliminated
//	 * @param variable The parameterized random variable to eliminate
//	 * @param setOfConstraints A set of constraints the given variable must
//	 * obey
//	 */
//	public static Set<SimpleParfactor> globalSumOut(Set<SimpleParfactor> setOfParfactors, 
//			ParameterizedRandomVariable variable, 
//			Set<Constraint> setOfConstraints) {
//		
//		/*
//		 * Input:  set of parfactors Phi
//		 * 	       parameterized random variable f
//		 *         set of constraints C
//		 * Output: set of parfactors Phi with f summed out
//		 * 
//		 * product := unary parfactor
//		 * for each parfactor p in Phi
//		 *     if p contains PRV v such that ground(v):C = ground(f):C
//		 *         product := product * p
//		 *         remove p from Phi
//		 * if product satisfies pre-condition for elimination
//		 *     result := eliminate f from product
//		 * return result
//		 */
//		
//		HashSet<SimpleParfactor> newSetOfParfactors = new HashSet<SimpleParfactor>(setOfParfactors);
//		SimpleParfactor product = SimpleParfactor.getConstantInstance(); //TODO: it would be better to do the first multiplication outside the loop
//		for (SimpleParfactor p : setOfParfactors) {
//			if (p.getParameterizedRandomVariables().contains(variable)) {
//				newSetOfParfactors = new HashSet<SimpleParfactor>(p.multiply(newSetOfParfactors, product));
//				HashSet<SimpleParfactor> copyOfNewSet = new HashSet<SimpleParfactor>(newSetOfParfactors);
//				copyOfNewSet.removeAll(setOfParfactors);
//				product = copyOfNewSet.iterator().hasNext() ? copyOfNewSet.iterator().next() : null;
//				
//				if (product == null) {
//					throw new NullPointerException("The new set is empty!: " + copyOfNewSet);
//				}
//			}
//		}
//		newSetOfParfactors = new HashSet<SimpleParfactor>(product.sumOut(newSetOfParfactors, variable));
//		
//		return newSetOfParfactors;
//	}
//	
//	/**
//	 * Checks if it is possible to apply the method  
//	 * {@link MacroOperations#globalSumOut(Set, ParameterizedRandomVariable, Set)}
//	 * to eliminate a parameterized random variable from a set of parfactors.
//	 * <br>
//	 * This method must be used before the aforementioned method to check the
//	 * validity of the operation. One may get unexpected results if the
//	 * verification is not made.
//	 * @param setOfParfactors The set of parfactors from which the variable
//	 * will be eliminated
//	 * @param variable The parameterized random variable to eliminate
//	 * @param setOfConstraints A set of constraints the given variable must
//	 * obey
//	 * @return True if GLOBAL-SUM-OUT is possible for the given parameters, 
//	 * false otherwise 
//	 */
//	public static boolean conditionsForGlobalSumOutAreMet(
//			Set<SimpleParfactor> setOfParfactors, 
//			ParameterizedRandomVariable variable, 
//			Set<Constraint> setOfConstraints) {
//		// remember that multiplication may be over aggregations or simple
//		// how can i check the sum out condition without doing the multiplication?
//		
//		/*
//		 * for each parfactor p in set of parfactors P
//		 *     if p contains PRV v
//		 *         add p to pool
//		 * all_vars := empty set
//		 * for each parfactor p in pool
//		 *     if p can be multiplied by next parfactor np in pool
//		 *         all_vars := PRVs from p union PRVs from np union all_vars
//		 *     else
//		 *         all_vars := empty set
//		 *         break
//		 * if all_vars = empty set
//		 *     return false
//		 * if sumOutIsPossible(all_vars, v)
//		 *     return true
//		 * return false
//		 * 
//		 */
//		
//		AggregationParfactor agg = null;
//		ArrayList<SimpleParfactor> candidateParfactors = new ArrayList<SimpleParfactor>();
//		ParameterizedRandomVariable childVariable = null;
//		
//		for (SimpleParfactor parfactor : setOfParfactors) {
//			if (parfactor.contains(variable)) {
//				if (parfactor instanceof AggregationParfactor) {
//					agg = (AggregationParfactor) parfactor; // what if there are more aggregation parfactors in the set?
//					childVariable = agg.getChildVariable();
//				} else {
//					candidateParfactors.add(parfactor);	
//				}
//			}
//		}
//		
//		// check if multiplication is possible
//		// it only makes sense when there is an Aggregation Parfactor in the set
//		// TODO: this is utterly ugly
//		if (agg != null) {
//			for (SimpleParfactor parfactor : candidateParfactors) {
//				if (!agg.canBeMultipliedBy(parfactor)) {
//					return false;
//				}
//			}
//			candidateParfactors.add(agg);
//		}
//		
//		HashSet<ParameterizedRandomVariable> variablesFromMultiplication = 
//			new HashSet<ParameterizedRandomVariable>(candidateParfactors.get(0).getParameterizedRandomVariables());
//				
//		for (int i = 0; i < candidateParfactors.size() - 1; i++) {
//			if (candidateParfactors.get(i).canBeMultipliedBy(candidateParfactors.get(i + 1))) {
//				variablesFromMultiplication.addAll(candidateParfactors.get(i + 1).getParameterizedRandomVariables());
//			} else {
//				return false;
//			}
//		}
//		
//		if (sumOutIsPossible(variablesFromMultiplication, variable, childVariable)) {
//			return true;
//		}
//		return false;
//		
//	}
//	
//	/**
//	 * Returns true if it is possible to sum out the <code>variableToSumOut</code>
//	 * given the all parameterized random variables in the factor from the
//	 * parfactor and its child variable, if any.
//	 * <br>
//	 * The <code>childVariable</code> must be provided separately when the
//	 * parfactor is actually an aggregation parfactor, otherwise it must be set
//	 * to <code>null</code>.
//	 * 
//	 * @param variablesFromMultiplication The variables present in the factor
//	 * @param variableToSumOut The parameterized random variable to be summed
//	 * out
//	 * @param childVariable The child parameterized random variable from the
//	 * parfactor, set to null if the is no such variable.
//	 * @return True if summing out the given variable is possible, false
//	 * otherwise.
//	 */
//	private static boolean sumOutIsPossible(
//			Set<ParameterizedRandomVariable> variablesFromMultiplication, 
//			ParameterizedRandomVariable variableToSumOut,
//			ParameterizedRandomVariable childVariable) {
//		
//		if (childVariable != null) { // Product is an aggregation parfactor
//			HashSet<LogicalVariable> parentParameters = new HashSet<LogicalVariable>(variableToSumOut.getParameters());
//			parentParameters.removeAll(childVariable.getParameters());
//			return (parentParameters.size() == 1);
//		} else { // Product is simple parfactor
//			variablesFromMultiplication.remove(variableToSumOut);
//			HashSet<LogicalVariable> allLogicalVariables = new HashSet<LogicalVariable>();
//			for (ParameterizedRandomVariable variable : variablesFromMultiplication) {
//				allLogicalVariables.addAll(variable.getParameters());
//			}
//			return variableToSumOut.getParameters().containsAll(allLogicalVariables);
//		}
//	}
//}
