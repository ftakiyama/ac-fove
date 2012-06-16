package br.usp.dml.takiyama.ve;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class makes operations over {@link Factor}s such as sum out and 
 * multiplication.
 * @author ftakiyama
 *
 */
public class FactorOperation {
	
	/**
	 * Sums out a random variable from a factor.
	 * <br>
	 * Suppose F is a factor on random variables x<sub>1</sub>,...,
	 * x<sub>i</sub>,...,xj. The summing out of random variable x<sub>i</sub> 
	 * from F, denoted as &Sigma;<sub>x<sub>i</sub></sub>,F is the factor on 
	 * random variables x<sub>1</sub>,...,x<sub>i−1</sub>, x<sub>i+1</sub>, 
	 * ..., x<sub>j</sub> such that
	 * <br>
	 * (&Sigma;<sub>x<sub>i</sub></sub> F)
	 *   (x<sub>1</sub>,...,x<sub>i−1</sub>,x<sub>i+1</sub>,...,x<sub>j</sub>) =	
	 * 	   &Sigma; <sub>y &isin; dom(x<sub>i</sub>)</sub> 
	 *     F(x<sub>1</sub>,...,x<sub>i−1</sub>,x<sub>i</sub> = y,
	 *       x<sub>i+1</sub>,...,x<sub>j</sub>).
	 * <br>
	 * If the variable to be summed out does not exist in the factor, this
	 * method returns the specified factor unmodified.
	 * @param factor The factor where the operation takes place.
	 * @param randomVariable The random variable to be summed out.
	 * @return A factor with the specified random variable summed out, or
	 * <code>factor</code> if <code>randomVariable</code> does not exist.
	 */
	public static Factor sumOut(Factor factor, RandomVariable randomVariable) {
		
		// Checks if the random variable exists
		if (factor.getRandomVariableIndex(randomVariable) == -1) {
			return factor;
		}
		
		// Creates a flag for the mappings in the factor that were already processed
		int[] marks = new int[factor.size()];
		Arrays.fill(marks, 0);
		
		// Removes the random variable
		ArrayList<RandomVariable> newRandomVariables = factor.getRandomVariables();
		newRandomVariables.remove(randomVariable);
		
		// Creates the new mapping, summing out the random variable
		ArrayList<BigDecimal> newMapping = new ArrayList<BigDecimal>();
		for (int factorCursor = 0; factorCursor < factor.size(); factorCursor++) {
			if (marks[factorCursor] == 0) {
				Tuple currentTuple = factor.getTuple(factorCursor); 
				BigDecimal sum = new BigDecimal(0);
				int tupleIndex;
				for (int domainCursor = 0; domainCursor < randomVariable.getDomainSize(); domainCursor++) {
					tupleIndex = factor.getTupleIndex(currentTuple.getModifiedTuple(factor.getRandomVariableIndex(randomVariable), domainCursor));
					marks[tupleIndex] = 1;
					sum = sum.add(factor.getTupleValue(tupleIndex));
				}
				newMapping.add(sum);
			}
		}
		
		// Creates the new factor
		return new Factor(factor.getName(), newRandomVariables, newMapping);
	}
	
	/**
	 * Multiplies 2 factors and returns the resulting factor.
	 * <br>
	 * Given two factors 
	 * 	   F<sub>1</sub>(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,...,y<sub>j</sub>) and 
	 *     F<sub>2</sub>(y<sub>1</sub>,...,y<sub>j</sub>,z<sub>1</sub>,...,z<sub>k</sub>) 
	 * the resulting parfactor will be 
	 * F(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,...,
	 *         y<sub>j</sub>,z<sub>1</sub>,...,z<sub>k</sub>) =
	 *     F<sub>1</sub>(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,..., 
	 *         y<sub>j</sub>) x 
	 *     F<sub>2</sub>(y<sub>1</sub>,...,y<sub>j</sub>,z<sub>1</sub>,...,
	 *         z<sub>k</sub>)
	 * <br>
	 * That is, for each assignment of values to the variables in the parfactors,
	 * we multiply the values that have the same assignment for the common 
	 * variables. 
	 * @param firstFactor The first factor to be multiplied
	 * @param secondFactor The second factor to be multiplied
	 * @return The multiplication of fisrtFactor by secondFactor.
	 */
	public static Factor multiply(Factor firstFactor, Factor secondFactor) {
		/*
		 * We have to take into account that factors depend on the order of 
		 * their variables.
		 * 
		 * get indexes of tuples in v2 that appear in v1 -> create a mapping v1<->v2
		 * FOR EACH value v1 IN firstFactor DO
		 *     FOR EACH value v2 IN secondFactor DO
		 *         IF v1 and v2 have a sub-tuple in common THEN
		 *             v_new := v1*v2
		 *             Add v_new to new mapping m
		 * 
		 * 
		 * have a sub-tuple in common
		 * 
		 *                
		 */
		return null;
	}
	
}
