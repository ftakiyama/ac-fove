/**
 * 
 */
package br.usp.dml.takiyama.cfove;

import java.util.Iterator;
import java.util.Vector;

import br.usp.dml.takiyama.trash.SetHandler;

/**
 * This class manipulates factors. It contains operations over {@link Factor}s
 * such as sum out and multiplication.
 * @author ftakiyama
 *
 */
public class FactorHandler {
	
	/**
	 * Sum out a variable from a factor. I think an explanation would fit...
	 * @param factor The factor from which the variable will be summed out
	 * @param variable The variable to be summed out
	 * @return A factor with the variable summed out
	 */
	public static Factor sumOut(Factor factor, RandomVariable variable) {
		// Stores the index of the variable to be eliminated
		int variableIndex = factor.getVariableIndex(variable);
		
		// Removes the variable to be summed out from the factor
		factor.removeVariable(variable);
		
		// The new factor after summing out the variable
		Factor newFactor = new Factor(factor.getVariables());
		
		// Calculates all combinations of values for the remaining variables
		// TODO Change this later to use google's implementation
		Vector<Tuple> allTuples = new Vector<Tuple>();
		allTuples = SetHandler.cartesianProduct(factor.getVariables());
		
		while (!allTuples.isEmpty()) {
			// Creates the tuples that can be combined and sum their values
			double sum = 0;
			for (int i = 0; i < variable.getDomainSize(); i++) {
				Tuple tuple = new Tuple(allTuples.firstElement(), variableIndex, variable.getElement(i));
				sum += factor.getValue(tuple);
			}
			
			// Add the new tuple and its value to the new Factor 
			newFactor.addAssignment(allTuples.remove(0), sum);			
		}
		
		return newFactor;
	}
	
	/**
	 * Multiplies 2 factors and returns the resulting factor.
	 * Given two factors F1(x1, ..., xn, y1, ..., yj) and 
	 * F2(y1, ..., yj, z1, ..., zk) the resulting parfactor will be
	 * F(x1, ..., xn, y1, ..., yj, z1, ..., zk) =
	 *     F1(x1, ..., xn, y1, ..., yj) x F2(y1, ..., yj, z1, ..., zk)
	 * That is, for each assignment of values to the variables in the parfactors,
	 * we multiply the values that have the same assignment for the common 
	 * variables. 
	 * @param firstFactor The first factor to be multiplied
	 * @param secondFactor The second factor to be multiplied
	 * @return The multiplication of fisrtFactor by secondFactor.
	 */
	public static Factor multiply(Factor firstFactor, Factor secondFactor) {
		// Creates a new empty factor with the union of variables from
		// the first factor and the second factor
		Vector<Integer> indexesOfTheSecondSet = new Vector<Integer>(); 
		Factor product = new Factor(unionVar(firstFactor.getVariables(), secondFactor.getVariables(), indexesOfTheSecondSet));
		
		// Calculates all combinations of values for the variables of the new factor
		Vector<Tuple> allTuples = new Vector<Tuple>();
		allTuples = SetHandler.cartesianProduct(product.getVariables());
		
		while(!allTuples.isEmpty()) {
			// Breaks the tuple in two sub-tuples, each corresponding to their
			// respective factor
			/*
			 * Example: Say we have F1(x1, x2) and F2(x2, x3). Then the product
			 * will be F(x1, x2, x3). Suppose all variables are binary. Then,
			 * (0, 1, 1) is a valid tuple for F. The sub-tuples will be
			 * (0, 1) for F1 and (1, 1) for F2.
			 */
			int cutPoint = firstFactor.getVariablesAmount();
			Tuple subTuple1 = new Tuple(allTuples.firstElement(), 0, cutPoint);
			Tuple subTuple2 = new Tuple(allTuples.firstElement(), indexesOfTheSecondSet);
			
			// Multiplies the values of each sub-tuple and add the result to
			// the product factor
			double prod = firstFactor.getValue(subTuple1) * secondFactor.getValue(subTuple2);
			product.addAssignment(allTuples.remove(0), prod);
		}
		
		return product;
	}
	
	/**
	 * Returns the union of two sets of random variables.
	 * The elements of the first set will be placed at the beginning of the
	 * resulting vector.  
	 * This funcion also puts the indexes of the elements in the union that
	 * also belong to the second set.
	 * @param set1 The first set
	 * @param set2 The second set
	 * @param indexes A vector where the indexes of the elements in the union
	 * that also belong to the second set will be stored. If not needed,
	 * can be set to null 
	 * @return set1 union set2
	 */
	private static Vector<RandomVariable> unionVar(Vector<RandomVariable> set1, Vector<RandomVariable> set2, Vector<Integer> indexes) {
		// Copies set1 to result
		@SuppressWarnings("unchecked")
		Vector<RandomVariable> result = (Vector<RandomVariable>) set1.clone();
		
		// Iterates over the elements of set2
		// If the element doesn't exist in set1, then add it to set1
		Iterator<RandomVariable> it = set2.iterator();
		while (it.hasNext()) {
			RandomVariable v = it.next();
			if (!result.contains(v)) {
				if (indexes != null) indexes.add(result.size());
				result.add(v);
			} else {
				if (indexes != null) indexes.add(result.indexOf(v));
			}
		}
		return result;
	}
	
	/**
	 * Returns the intersection of two sets of random variables.
	 * The intersection is built searching the first set for elements that
	 * also appear in the second set. Thus, the order of the intersection
	 * is determined by the order of the variables in the first set.
	 * This is important because this order is used in the algorithm used
	 * by FactorHandler.multiply().
	 * @param set1 The first set
	 * @param set2 The second set
	 * @return set1 intersect set2
	 */
	private static Vector<RandomVariable> intersectVar(Vector<RandomVariable> set1, Vector<RandomVariable> set2) {
		Vector<RandomVariable> result = new Vector<RandomVariable>();
		Iterator<RandomVariable> it = set1.iterator();
		while (it.hasNext()) {
			RandomVariable v = it.next();
			if (set2.contains(v)) {
				result.add(v);
			}
		}
		return result;
	}
	
	
	/**
	 * Returns the difference between two sets of random variables
	 * @param set1 The first set
	 * @param set2 The second set
	 * @return set1 - set1
	 */
	private static Vector<RandomVariable> minusVar(Vector<RandomVariable> set1, Vector<RandomVariable> set2) {
		@SuppressWarnings("unchecked")
		Vector<RandomVariable> result = (Vector<RandomVariable>) set1.clone();
		Iterator<RandomVariable> it = set2.iterator();
		while (it.hasNext()) {
			RandomVariable v = it.next();
			if (result.contains(v)) {
				result.remove(v);
			}
		}
		return result;
	}
	
	public Factor multiply(Vector<Factor> factors) {
		
		return null;
	}
	
	/**
	 * Tests the set operations implemented in this class. 
	 * These tests are for debug purposes only, and don't have influence in
	 * the program itself. 
	 */
	public static void testSetOperations() {		
		System.out.println("Test: set operations");
		
		// All variables will be binary
		Vector<String> domain = new Vector<String>();
		domain.add("true");
		domain.add("false");
		
		// Variables
		Vector<RandomVariable> rv = new Vector<RandomVariable>();
		for (int i = 0; i < 10; i++) {
			RandomVariable x = new RandomVariable("x" + i, domain);
			rv.add(x);
		}
		
		System.out.println("Variables created.");
		
		// Sets of variables
		Vector<RandomVariable> v1 = new Vector<RandomVariable>();
		Vector<RandomVariable> v2 = new Vector<RandomVariable>();
		Vector<RandomVariable> v3 = new Vector<RandomVariable>();
		Vector<RandomVariable> v4 = new Vector<RandomVariable>();
		Vector<RandomVariable> v5 = new Vector<RandomVariable>();
		
		// v1 = {x0, x1, x2}
		// v2 = {x0, x1, x2}
		// v3 = {x3}
		// v4 = {x2, x3, x4}
		// v5 = { }
		
		v1.add(rv.elementAt(0));
		v1.add(rv.elementAt(1));
		v1.add(rv.elementAt(2));
		
		v2.add(rv.elementAt(0));
		v2.add(rv.elementAt(1));
		v2.add(rv.elementAt(2));
		
		v3.add(rv.elementAt(3));
		
		v4.add(rv.elementAt(2));
		v4.add(rv.elementAt(3));
		v4.add(rv.elementAt(4));
		
		System.out.println("= Union =");
		
		// union - test1
		System.out.print("v1 U v2: ");
		printSet(unionVar(v1, v2, null));
		
		// union - test 2
		System.out.print("v1 U v3: ");
		printSet(unionVar(v1, v3, null));
		
		// union - test 3
		System.out.print("v1 U v4: ");
		printSet(unionVar(v1, v4, null));
		
		// union - test 4
		System.out.print("v1 U v5: ");
		printSet(unionVar(v1, v5, null));
		
		System.out.println("= Intersection =");
		
		// intersection - test 1
		System.out.print("v1 ^ v1: ");
		printSet(intersectVar(v1, v1));
		
		// intersection - test 2
		System.out.print("v1 ^ v2: ");
		printSet(intersectVar(v1, v2));
		
		// intersection - test 3
		System.out.print("v1 ^ v3: ");
		printSet(intersectVar(v1, v3));
		
		// intersection - test 4
		System.out.print("v1 ^ v4: ");
		printSet(intersectVar(v1, v4));
		
		// intersection - test 5
		System.out.print("v1 ^ v5: ");
		printSet(intersectVar(v1, v5));
		
		
		System.out.println("= Difference =");
		
		// diff - test 1
		System.out.print("v1 - v2: ");
		printSet(minusVar(v1, v2));
		
		// diff - test 2
		System.out.print("v1 - v3: ");
		printSet(minusVar(v1, v3));
		
		// diff - test 4
		System.out.print("v1 - v4: ");
		printSet(minusVar(v1, v4));
		
		// diff - test 5
		System.out.print("v1 - v5: ");
		printSet(minusVar(v1, v5));
		
		// diff - test 6
		System.out.print("v2 - v1: ");
		printSet(minusVar(v2, v1));
		
		// diff - test 7
		System.out.print("v3 - v1: ");
		printSet(minusVar(v3, v1));
		
		// diff - test 8
		System.out.print("v4 - v1: ");
		printSet(minusVar(v4, v1));
		
		// diff - test 9
		System.out.print("v5 - v1: ");
		printSet(minusVar(v5, v1));
	}
	
	/**
	 * Prints a set of random variables
	 * @param v The set of random variables
	 */
	private static void printSet(Vector<RandomVariable> v) {
		Iterator<RandomVariable> it = v.iterator();
		System.out.print("{");
		while (it.hasNext()) {
			System.out.print(it.next().getName() + " ");
		}
		System.out.println("}");
	}
	
	
}
