/**
 * 
 */
package ve;

import java.util.Vector;

/**
 * This class manipulates factors. It contains operations over {@link Factor}s
 * such as sum out and multiplication.
 * @author ftakiyama
 *
 */
public class FactorHandler {
	
	/**
	 * Sum out a variable from a factor. I think an explanation would fit...
	 * @param factor The factor to be manipulated
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
	
	public Factor multiply(Factor firstFactor, Factor secondFactor) {
		
		return null;
	}
	
	public Factor multiply(Vector<Factor> factors) {
		
		return null;
	}
}
