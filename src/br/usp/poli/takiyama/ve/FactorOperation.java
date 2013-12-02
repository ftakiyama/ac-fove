package br.usp.poli.takiyama.ve;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import br.usp.poli.takiyama.common.RandomVariable;
import br.usp.poli.takiyama.common.IntTuple;
import br.usp.poli.takiyama.utils.MathUtils;


/**
 * This class makes operations over {@link Factor}s such as sum out and 
 * multiplication.
 * @author ftakiyama
 *
 */
public class FactorOperation {
	
	public static final int PRECISION = 8; 
	
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
				IntTuple currentTuple = factor.getTuple(factorCursor); 
				BigDecimal sum = new BigDecimal(0);
				int tupleIndex;
				int currentRandomVariableIndex = factor.getRandomVariableIndex(randomVariable);
				for (int domainCursor = 0; domainCursor < randomVariable.getDomainSize(); domainCursor++) {
					IntTuple nextTuple = currentTuple.getModifiedTuple(currentRandomVariableIndex, domainCursor);
					tupleIndex = factor.getTupleIndex(nextTuple);
					marks[tupleIndex] = 1;
					sum = sum.add(factor.getTupleValue(tupleIndex), MathUtils.CONTEXT);
				}
				newMapping.add(sum);
			}
		}
		
		// Creates the new factor
		return new Factor("sumOut(" 
				+ randomVariable.getName() 
				+ ", " 
				+ factor.getName() 
				+ ")", 
				newRandomVariables, 
				newMapping);
	}
	
	/**
	 * Multiplies 2 factors and returns the resulting factor.
	 * <br>
	 * Given two factors 
	 * 	   F<sub>1</sub>(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,...,y<sub>j</sub>) and 
	 *     F<sub>2</sub>(y<sub>1</sub>,...,y<sub>j</sub>,z<sub>1</sub>,...,z<sub>k</sub>) 
	 * the resulting factor will be 
	 * F(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,...,
	 *         y<sub>j</sub>,z<sub>1</sub>,...,z<sub>k</sub>) =
	 *     F<sub>1</sub>(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,..., 
	 *         y<sub>j</sub>) x 
	 *     F<sub>2</sub>(y<sub>1</sub>,...,y<sub>j</sub>,z<sub>1</sub>,...,
	 *         z<sub>k</sub>)
	 * <br>
	 * That is, for each assignment of values to the variables in the factors,
	 * the method multiply the values that have the same assignment for the common 
	 * variables. 
	 * @param firstFactor The first factor to be multiplied
	 * @param secondFactor The second factor to be multiplied
	 * @return The multiplication of fisrtFactor by secondFactor.
	 */
	public static Factor multiply(Factor firstFactor, Factor secondFactor) {
		/*
		 * get indexes of tuples in v2 that appear in v1 -> create a mapping v1<->v2
		 * FOR EACH value v1 IN firstFactor DO
		 *     FOR EACH value v2 IN secondFactor DO
		 *         IF v1 and v2 have a sub-tuple in common THEN
		 *             v_new := v1*v2
		 *             Add v_new to new mapping m
		 *                
		 */

		ArrayList<RandomVariable> newVariables = union(firstFactor, secondFactor);
		String newName = firstFactor.getName() + " * " + secondFactor.getName();
		ArrayList<BigDecimal> newMapping = new ArrayList<BigDecimal>();
		
		int[][] commonVariablesMapping = getCommonVariablesMapping(firstFactor, secondFactor);
		
		for (int i = 0; i < firstFactor.size(); i++) {
			IntTuple t1 = firstFactor.getTuple(i);
			for(int j = 0; j < secondFactor.size(); j++) {
				IntTuple t2 = secondFactor.getTuple(j);
				if (haveSameSubtuple(t1, t2, commonVariablesMapping)) {
					newMapping.add(firstFactor.getTupleValue(i).multiply(secondFactor.getTupleValue(j), MathUtils.CONTEXT));
				}
			}
		}
		
		return new Factor(newName, newVariables, newMapping);
	}
	
	/**
	 * Returns a mapping from indexes of variables in the first factor to the
	 * indexes of the variables that also appear in the second factor.
	 * <br>
	 * The mapping is a 2 x n matrix, where n is the number of common
	 * random variables between the first factor and the second factor.
	 * <br>
	 * The set of random variables from the first factor is analyzed
	 * sequentially, and for each random variable in the set, the set from the
	 * second factor is searched for a match. Thus, the first line of the
	 * result will be in ascending order, while the second line may have
	 * an arbitrary ordering.
	 * <br>
	 * For example, suppose that f1(x1,x2,x3,x4,x5) and f2(x5,x4,x1) are factors
	 * passed as parameter for this method. Then the mapping will be the
	 * following matrix:
	 * <br>
	 * 0 3 4<br>
	 * 2 1 0
	 * <br>
	 * which means that x1 (index 0) in f1 has a match in f2 at index 2, and
	 * so on.
	 * 
	 * @param f1 The first factor.
	 * @param f2 The second factor.
	 * @return A mapping of indexes from common variables between f1 and f2.
	 */
	private static int[][] getCommonVariablesMapping(Factor f1, Factor f2) {
		ArrayList<RandomVariable> rv2 = f2.getRandomVariables();
		Iterator<RandomVariable> it1 =  f1.getRandomVariables().iterator();
		int[][] mapping = new int[2][f1.getRandomVariables().size()];
		int i = 0;
		
		while (it1.hasNext()) {
			RandomVariable v1 = it1.next();
			if (rv2.contains(v1)) {
				mapping[0][i] = f1.getRandomVariableIndex(v1);
				mapping[1][i] = f2.getRandomVariableIndex(v1);
				i++;
			}
		}
		
		// trim the matrix - this is horrible
		int[][] newMapping = new int[2][i];
		for (int j = 0; j < i; j++) {
			newMapping[0][j] = mapping[0][j];
			newMapping[1][j] = mapping[1][j];
		}
		
		return newMapping;
	}
	
	/**
	 * Returns true if both tuples have the same sub-tuple. The sub-tuple is
	 * defined according to a map. 
	 * @see FactorOperation#getCommonVariablesMapping 
	 * @param t1 The first tuple
	 * @param t2 The second tuple
	 * @param mapping A mapping that connects indexes that represent the same
	 * random variable.
	 * @return True if the tuples have the same value for the sub-tuple, false
	 * otherwise.
	 */
	private static boolean haveSameSubtuple(IntTuple t1, IntTuple t2, int[][] mapping) {
		IntTuple commonSubTuple1 = t1.subTuple(mapping[0]);
		IntTuple commonSubTuple2 = t2.subTuple(mapping[1]);
		if (commonSubTuple1.equals(commonSubTuple2)) { 
			return true;
		}
		return false;
	}
	
	
	/**
	 * Returns the union of two sets of random variables from factors.
	 * The elements of the first set will be placed at the beginning of the
	 * resulting array.  
	 * @param f1 The first factor
	 * @param f2 The second factor
	 * @return The union of random variables from the first factor and the
	 * random variables from the second factor.
	 */
	private static ArrayList<RandomVariable> union(Factor f1, Factor f2) { // <- i dont need the factors, only the random variable sets
		ArrayList<RandomVariable> result = f1.getRandomVariables();
		Iterator<RandomVariable> it = f2.getRandomVariables().iterator();
		
		while (it.hasNext()) {
			RandomVariable v = it.next();
			if (!result.contains(v)) {
				result.add(v);
			}
		}
		return result;		
	}
	
	/**
	 * Multiplies a sequence of factors in the order provided.
	 * <br>
	 * If no factors are provided, returns an empty factor.
	 * @param factors A sequence of factors to be multiplied.
	 * @return The factor resulting from the multiplication of all specified 
	 * factors, or an empty factor if no factor is specified.
	 */
	public static Factor product(Factor... factors) {
		if (factors == null) {
			return new Factor("", 
					new ArrayList<RandomVariable>(), 
					new ArrayList<BigDecimal>());
		}
		if (factors.length == 1) {
			return factors[0];
		}
		Factor result = multiply(factors[0], factors[1]);
		for (int i = 2; i < factors.length; i++) {
			result = multiply(result, factors[i]);
		}
		return result;
	}
	
	
	/**
	 * Divides factors by a sub-factor and returns the resulting factor.
	 * <br>
	 * Given two factors 
	 * 	   F<sub>1</sub>(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,...,y<sub>j</sub>) and 
	 *     F<sub>2</sub>(y<sub>1</sub>,...,y<sub>j</sub>) 
	 * the resulting factor will be 
	 * F(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,...,y<sub>j</sub>) =
	 *     F<sub>1</sub>(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,..., 
	 *         y<sub>j</sub>) / 
	 *     F<sub>2</sub>(y<sub>1</sub>,...,y<sub>j</sub>)
	 * <br>
	 * That is, for each assignment of values to the variables in the factors,
	 * the method divides the values that have the same assignment for the 
	 * common variables. 
	 * @param dividend The dividend factor
	 * @param divisor The divisor factor. Must be a sub-factor of the dividend 
	 * and must not contain zero values.
	 * @return The division of <code>dividend</code> by <code>divisor</code>.
	 * @throws IllegalArgumentException If <code>divisor</code> is not a
	 * sub-factor of <code>dividend</code>, or if <code>divisor</code> has
	 * a tuple whose value is 0.
	 */
	public static Factor divide(Factor dividend, Factor divisor) 
			throws IllegalArgumentException {
		
		// Checks if divisor is sub-factor of the dividend
		if (!divisor.isSubFactorOf(dividend))
			throw new IllegalArgumentException("The divisor is not a " +
					"sub-factor of the dividend.");
		
		// Checks if divisor contains zero -- this is a weak check!
		for (int i = 0; i < divisor.size(); i++) {
			if (divisor.getTupleValue(i).compareTo(BigDecimal.ZERO) == 0)
				throw new IllegalArgumentException("The divisor contains a" +
						"tuple whose value is 0.");
		}
		
		// The algorithm is similar to the multiplication 
		
		ArrayList<BigDecimal> newMapping = new ArrayList<BigDecimal>();
		
		int[][] commonVariablesMapping = getCommonVariablesMapping(dividend, divisor);
		
		for (int i = 0; i < dividend.size(); i++) {
			IntTuple t1 = dividend.getTuple(i);
			for(int j = 0; j < divisor.size(); j++) {
				IntTuple t2 = divisor.getTuple(j);
				if (haveSameSubtuple(t1, t2, commonVariablesMapping)) {
					newMapping
						.add(dividend
							.getTupleValue(i)
							.divide(divisor
								.getTupleValue(j), 
								PRECISION, 
								RoundingMode.HALF_UP));
				}
			}
		}
		return new Factor(dividend.getName(), dividend.getRandomVariables(), newMapping);
	}
	
	
}
