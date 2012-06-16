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
	 * 
	 * @param factor The factor where the operation takes place.
	 * @param randomVariable The random variable to be summed out.
	 * @return A factor with the specified random variable summed out.
	 */
	public static Factor sumOut(Factor factor, RandomVariable randomVariable) {
		
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
	
	public static Factor multiply(Factor firstFactor, Factor secondFactor) {
		return null;
	}
	
}
