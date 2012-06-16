package br.usp.dml.takiyama.ve;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.lang.ArrayIndexOutOfBoundsException;

import br.usp.dml.takiyama.trash.SetHandler;

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
	 * @param tuple The tuple to search.   
	 * @return The index of a tuple in this factor.
	 * @throws IllegalArgumentException if the tuple is empty.
	 */
	public int getTupleIndex(Tuple tuple) throws IllegalArgumentException {
		if (tuple.isEmpty()) {
			throw new IllegalArgumentException("This tuple is empty!");
		} else if (tuple.size() == 1) {
			return tuple.get(0).intValue();
		} else {		
			int lastPosition = tuple.size() - 1;
			Integer lastIndex = tuple.get(lastPosition);
			int domainSize = this.randomVariables.get(lastPosition).getDomainSize();
			return lastIndex + domainSize * getTupleIndex(tuple.subTuple(0, lastPosition));
		}
	}
	
	/**
	 * Returns a tuple given its index.
	 * @param index The index of the tuple.
	 * @return A tuple at the position specified by the parameter <b>index</b>.
	 */
	public Tuple getTuple(int index) {
		ArrayList<Integer> tuple = new ArrayList<Integer>();
		for (int j = randomVariables.size() - 1; j > 0; j--) {
			int domainSize = this.randomVariables.get(j).getDomainSize();
			tuple.add(index % domainSize);
			index = index / domainSize;	
		}
		tuple.add(index);
		Collections.reverse(tuple);
		return new Tuple(tuple);
	}
	
	@Override
	public String toString() {
		String result = this.name + "\n";
		
		if (this.randomVariables.isEmpty()) {
			return this.name + " is empty.";
		}
		
		String thinRule = "";
		String thickRule = "";
		String cellFormat = "%-10s"; 
		String valueCellFormat = "%-10s\n";
		
		// Create the rules - aesthetic
		for (int i = 0; i <= this.randomVariables.size(); i++) {
			thinRule += String.format(cellFormat, "").replace(" ", "-");
		}
		thickRule = thinRule.replace("-", "=");
		
		// Top rule
		result += thickRule + "\n";
		
		// Print the variables names
		for (RandomVariable rv : this.randomVariables) {
			result += String.format(cellFormat, rv.getName()); 
		}
		
		// Value column
		result += String.format(cellFormat + "\n", "VALUE");
		
		// Mid rule
		result += thinRule + "\n";
		
		// Print the contents
		for (int i = 0; i < this.mapping.size(); i++) {
			Tuple tuple = this.getTuple(i);
			for (int j = 0; j < tuple.size(); j++) {
				RandomVariable currentRandomVariable = this.randomVariables.get(j);
				int domainIndex = tuple.get(j);
				String domainValue = currentRandomVariable.getElementFromDomain(domainIndex);
				result += String.format(cellFormat, domainValue);
			}
			// Round the value to 6 digits
			result += String.format(valueCellFormat, this.mapping.get(i).setScale(6, BigDecimal.ROUND_HALF_DOWN));			
		}
		
		// Bottom rule
		result += thickRule + "\n";
		
		return result;
	}
	
	/**
	 * Returns the number of values in this factor, which is the same as the
	 * number of tuples in this factor.
	 * @return The number of values in this factor.
	 */
	public int size() {
		return this.mapping.size();
	}
	
	/**
	 * Returns the index of a random variable in this factor, or -1 if
	 * there is no such random variable in this factor.
	 * @param randomVariable The random variable to search for
	 * @return The index of the random variable in this factor, or -1 if
	 * this factor does not contain the random variable
	 */
	public int getRandomVariableIndex(RandomVariable randomVariable) {
		return this.randomVariables.indexOf(randomVariable);
	}
	
	/**
	 * Returns the name of this factor.
	 * @return The name of this factor.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns a copy of list of random variables in this factor.
	 * @return A copy of list of random variables in this factor.
	 */
	public ArrayList<RandomVariable> getRandomVariables() {
		return new ArrayList<RandomVariable>(this.randomVariables);
	}
	
	/**
	 * Returns the value of the tuple specified by its index.
	 * @param index The index of the tuple in this factor.
	 * @return The value of the tuple specified by its index.
	 */
	public BigDecimal getTupleValue(int index) {
		return this.mapping.get(index);
	}
	
}
