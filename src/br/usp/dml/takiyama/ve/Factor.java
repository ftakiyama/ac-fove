package br.usp.dml.takiyama.ve;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.ArrayIndexOutOfBoundsException;

/**
 * Let dom(x) denote the domain of random variable x. 
 * A factor on random variables x1,x2,...,xn is a representation 
 * of a function from dom(x1) x dom(x2) x ... x dom(xn) into the 
 * real numbers. [Kisynski,2010].<br>
 * This factor uses an efficient implementation, but in order to
 * do that, we require the following:<br>
 * <li> Random variables belonging to this factor are ordered
 * <li> The domain of each random variable is ordered
 * <li> The order does not change
 * <br>
 * Thus, we sacrifice encapsulation to benefit efficiency.
 * <br>
 * Factors are immutable objects, so if one wants to modify it, a new factor
 * should be created.
 * 
 * @author ftakiyama
 *
 */
public final class Factor {
	private final String name;
	private final ArrayList<RandomVariable> randomVariables;
	private final ArrayList<BigDecimal> mapping;
	
	/**
	 * Constructor. Creates a new factor with name 'name'
	 * and values listed in 'mapping'. 
	 * @param name The name of the factor.
	 * @param randomVariables A list of the random variables of this factor.
	 * @param mapping The values of the factor.
	 * @throws ArrayIndexOutOfBoundsException if there is an inconsistency
	 * between the size of the factor and the set of random variables.
	 */
	public Factor(String name, ArrayList<RandomVariable> randomVariables,
			ArrayList<BigDecimal> mapping) throws ArrayIndexOutOfBoundsException {
		this.name = name;
		this.randomVariables = new ArrayList<RandomVariable>(randomVariables);
		this.mapping = new ArrayList<BigDecimal>(mapping);
		
		int factorSize = 1;
		for (RandomVariable rv : randomVariables) {
			factorSize *= rv.getDomainSize();
		}
		if (factorSize != mapping.size()) {
			throw new ArrayIndexOutOfBoundsException("The mapping does " +
					"not have the required number of values. Expected: " + 
					factorSize + " received: " + mapping.size());
		}
	}
	
	/**
	 * Returns the index of a tuple.
	 * @param indexes The tuple. It must be an ArrayList of Integers, where
	 * each position j corresponds to the index of the element in the domain
	 * of the j-th random variable from this factor.  
	 * @return The index of a tuple.
	 * @throws IllegalArgumentException
	 */
	public int getTupleIndex(ArrayList<Integer> indexes) throws IllegalArgumentException {
		if (indexes == null || indexes.isEmpty()) {
			throw new IllegalArgumentException("This tuple is empty!");
		} else if (indexes.size() == 1) {
			return indexes.get(0);
		} else {		
			int lastPosition = indexes.size() - 1;
			Integer lastIndex = indexes.remove(lastPosition);
			int domainSize = this.randomVariables.get(lastPosition).getDomainSize();
			return lastIndex + domainSize * getTupleIndex(indexes);
		}
	}
	
	/**
	 * Returns a tuple given its index.
	 * @param index The index of the tuple.
	 * @return A tuple at the position specified by the parameter <b>index</b>.
	 */
	public ArrayList<String> getTuple(int index) {
		ArrayList<String> tuple = new ArrayList<String>();
		for (int j = randomVariables.size() - 1; j > 0; j--) {
			int domainSize = this.randomVariables.get(j).getDomainSize();
			int ij = index % domainSize;
			tuple.add(this.randomVariables.get(j).getElementFromDomain(ij));
			index = index / domainSize;	
		}
		tuple.add(this.randomVariables.get(0).getElementFromDomain(index));
		Collections.reverse(tuple);
		return tuple;
	}
	
	@Override
	public String toString() {
		/* this method should be rewritten to organize the data in better way.
		 * it lists the attributes of this factor, but it is quite raw.
		 */
		String result = this.name;
		result += "\n" + randomVariables.toString();
		result += "\n" + mapping.toString();
		return result;
	}
	
}
