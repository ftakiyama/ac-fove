package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.usp.poli.takiyama.common.Tuple;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

public final class ParameterizedFactor {
	private final String name;
	private final ArrayList<ParameterizedRandomVariable> variables;
	private final ArrayList<Double> mapping;
	
	/**
	 * Private constructor. Creates a new factor on parameterized random variables.
	 * @param name The name of the factor.
	 * @param variables A list of the parameterized random variables of this factor.
	 * @param mapping The values of the factor.
	 * @throws IllegalArgumentException if there is an inconsistency
	 * between the size of the factor and the set of parameterized random variables.
	 */
//	private ParameterizedFactor(
//			String name, 
//			List<ParameterizedRandomVariable> variables,
//			List<Number> mapping) 
//			throws IllegalArgumentException {
//		
//		this.name = name;
//		this.variables = new ArrayList<ParameterizedRandomVariable>(variables);
//		
//		// TODO How to convert a list of Numbers to a list of Doubles?
//		ArrayList<Double> temp = new ArrayList<Double>();
//		for (Number n : mapping) {
//			temp.add(n.doubleValue());
//		}
//		this.mapping = new ArrayList<Double>(temp);
//		
//		// Ugly, but necessary
//		int factorSize = 1;
//		for (ParameterizedRandomVariable prv : variables) {
//			factorSize *= prv.getRangeSize();
//		}
//		if (mapping.size() != 0 && factorSize != mapping.size()) {
//			throw new IllegalArgumentException("The mapping does " +
//					"not have the required number of values. Expected: " + 
//					factorSize + " received: " + mapping.size());
//		}
//		
//		
//	}
	
	
	private ParameterizedFactor(
			String name, 
			List<ParameterizedRandomVariable> variables,
			List<Double> mapping) 
			throws IllegalArgumentException {
		
		this.name = name;
		this.variables = new ArrayList<ParameterizedRandomVariable>(variables);
		this.mapping = new ArrayList<Double>(mapping);
		
		// Ugly, but necessary
		int factorSize = 1;
		for (ParameterizedRandomVariable prv : variables) {
			factorSize *= prv.getRangeSize();
		}
		if (mapping.size() != 0 && factorSize != mapping.size()) {
			throw new IllegalArgumentException("The mapping does " +
					"not have the required number of values. Expected: " + 
					factorSize + " received: " + mapping.size());
		}
		
		
	}
	
	/**
	 * Static factory to get a new instance of Parameterized Factors.
	 * @param name The name of the factor.
	 * @param variables A list of the parameterized random variables of this factor.
	 * @param mapping The values of the factor.
	 * @return An instance of ParameterizedFactor.
	 * @throws IllegalArgumentException if there is an inconsistency
	 * between the size of the factor and the set of parameterized random variables.
	 */
	public static ParameterizedFactor getInstance(
			String name, 
			List<ParameterizedRandomVariable> variables,
			List<Number> mapping) 
			throws IllegalArgumentException {
		ArrayList<Double> temp = new ArrayList<Double>();
		for (Number n : mapping) {
			temp.add(n.doubleValue());
		}
		return new ParameterizedFactor(name, variables, new ArrayList<Double>(temp));
	}
	
	public static ParameterizedFactor getInstance(ParameterizedFactor factor) {
		return new ParameterizedFactor(factor.name, factor.variables, factor.mapping);
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
			int domainSize = this.variables.get(lastPosition).getRangeSize();
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
		for (int j = variables.size() - 1; j > 0; j--) {
			int domainSize = this.variables.get(j).getRangeSize();
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
		
		if (this.variables.isEmpty()) {
			return this.name + " is empty.";
		}
		
		String thinRule = "";
		String thickRule = "";
		String cellFormat = "%-10s"; 
		String valueCellFormat = "%-10s\n";
		
		// Create the rules - aesthetic
		for (int i = 0; i <= this.variables.size(); i++) {
			thinRule += String.format(cellFormat, "").replace(" ", "-");
		}
		thickRule = thinRule.replace("-", "=");
		
		// Top rule
		result += thickRule + "\n";
		
		// Print the variables names
		for (ParameterizedRandomVariable prv : this.variables) {
			result += String.format(cellFormat, prv.getName()); 
		}
		
		// Value column
		result += String.format(cellFormat + "\n", "VALUE");
		
		// Mid rule
		result += thinRule + "\n";
		
		// Print the contents
		for (int i = 0; i < this.mapping.size(); i++) {
			Tuple tuple = this.getTuple(i);
			for (int j = 0; j < tuple.size(); j++) {
				ParameterizedRandomVariable currentRandomVariable = this.variables.get(j);
				int domainIndex = tuple.get(j);
				String domainValue = currentRandomVariable.getElementFromRange(domainIndex);
				result += String.format(cellFormat, domainValue);
			}
			// Round the value to 6 digits
			result += String.format(valueCellFormat, this.mapping.get(i).toString());			
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
	public int getParameterizedRandomVariableIndex(ParameterizedRandomVariable randomVariable) {
		return this.variables.indexOf(randomVariable);
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
	public ArrayList<ParameterizedRandomVariable> getParameterizedRandomVariables() {
		return new ArrayList<ParameterizedRandomVariable>(this.variables);
	}
	
	/**
	 * Returns the value of the tuple specified by its index.
	 * @param index The index of the tuple in this factor.
	 * @return The value of the tuple specified by its index.
	 */
	public double getTupleValue(int index) {
		return this.mapping.get(index);
	}
	
	/**
	 * Returns true if the factor is a sub-factor of the specified factor.
	 * @return True if the factor is a sub-factor of the specified factor.
	 */
	public boolean isSubFactorOf(ParameterizedFactor factor) {
		// Quick check
		if (factor.variables.size() < this.variables.size())
			return false;
		
		Iterator<ParameterizedRandomVariable> it = this.variables.iterator();
		while (it.hasNext())
			if (!factor.variables.contains(it.next()))
				return false;
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof ParameterizedFactor))
	    	return false;
	    // Tests if both have the same attributes
	    ParameterizedFactor targetObject = (ParameterizedFactor) other;
	    return this.name.equals(targetObject.name) &&
	    	   ((this.variables == null) ? 
	    		 targetObject.variables == null : 
	    		 this.variables.equals(targetObject.variables)) &&
    		   ((this.mapping == null) ? 
    		     targetObject.mapping == null : 
    		     this.mapping.equals(targetObject.mapping));	    		
	}
	
	@Override
	public int hashCode() { // Algorithm extracted from Bloch,J. Effective Java
		int result = 17;
		result = 31 + result + name.hashCode();
		result = 31 + result + Arrays.hashCode(variables.toArray(new ParameterizedRandomVariable[variables.size()]));
		result = 31 + result + Arrays.hashCode(mapping.toArray(new Double[mapping.size()]));
		return result;
	}
	
	
	/*
	 * STATIC METHODS
	 */
	
	/**
	 * TODO: rewrite this thing
	 * Sums out a random variable from a factor.
	 * <br>
	 * Suppose F is a factor on random variables x<sub>1</sub>,...,
	 * x<sub>i</sub>,...,xj. The summing out of random variable x<sub>i</sub> 
	 * from F, denoted as &Sigma;<sub>x<sub>i</sub></sub>,F is the factor on 
	 * random variables x<sub>1</sub>,...,x<sub>i-1</sub>, x<sub>i+1</sub>, 
	 * ..., x<sub>j</sub> such that
	 * <br>
	 * (&Sigma;<sub>x<sub>i</sub></sub> F)
	 *   (x<sub>1</sub>,...,x<sub>i-1</sub>,x<sub>i+1</sub>,...,x<sub>j</sub>) =	
	 * 	   &Sigma; <sub>y &isin; dom(x<sub>i</sub>)</sub> 
	 *     F(x<sub>1</sub>,...,x<sub>i-1</sub>,x<sub>i</sub> = y,
	 *       x<sub>i+1</sub>,...,x<sub>j</sub>).
	 * <br>
	 * If the variable to be summed out does not exist in the factor, this
	 * method returns the specified factor unmodified.
	 * @param factor The factor where the operation takes place.
	 * @param randomVariable The random variable to be summed out.
	 * @return A factor with the specified random variable summed out, or
	 * <code>factor</code> if <code>randomVariable</code> does not exist.
	 */
	public ParameterizedFactor sumOut(ParameterizedRandomVariable randomVariable) {
		
		// Checks if the random variable exists
		if (getParameterizedRandomVariableIndex(randomVariable) == -1) {
			return this;
		}
		
		// Creates a flag for the mappings in the factor that were already processed
		int[] marks = new int[this.size()];
		Arrays.fill(marks, 0);
		
		// Removes the random variable
		ArrayList<ParameterizedRandomVariable> newRandomVariables = this.getParameterizedRandomVariables();
		newRandomVariables.remove(randomVariable);
		
		// Creates the new mapping, summing out the random variable
		ArrayList<Number> newMapping = new ArrayList<Number>();
		for (int factorCursor = 0; factorCursor < this.size(); factorCursor++) {
			if (marks[factorCursor] == 0) {
				Tuple currentTuple = this.getTuple(factorCursor); 
				Double sum = new Double(0);
				int tupleIndex;
				int currentRandomVariableIndex = this.getParameterizedRandomVariableIndex(randomVariable);
				for (int domainCursor = 0; domainCursor < randomVariable.getRangeSize(); domainCursor++) {
					Tuple nextTuple = currentTuple.getModifiedTuple(currentRandomVariableIndex, domainCursor);
					tupleIndex = this.getTupleIndex(nextTuple);
					marks[tupleIndex] = 1;
					sum = Double.valueOf(Double.valueOf(sum) + this.getTupleValue(tupleIndex));
				}
				newMapping.add(sum);
			}
		}
		
		// Creates the new factor
		return getInstance(this.getName(), newRandomVariables, newMapping);
	}
	
	/**
	 * TODO: rewrite this thing.
	 * Returns a factor raised by the value specified.
	 * <br>
	 * Raising a factor to some exponent is the same as raising its values to
	 * that exponent.  
	 * <br>
	 * The current implementation makes totally worthless the use of BigDecimal.
	 * There is no method that raises a BigDecimal to another BigDecimal, so 
	 * the algorithm must be implemented. Currently, the method is converting
	 * BigDecimal to double so I can use {@link Math#pow}.
	 * @param factor The "base"
	 * @param exponent The exponent
	 * @return The value of <code>factor<code><sup>exponent</sup>
	 */
	public ParameterizedFactor pow(double exponent) {
		ArrayList<Number> newMapping = new ArrayList<Number>();
		
		for (Double base : mapping) {
			newMapping.add(Math.pow(base, exponent));
		}
		
		return getInstance(this.name, this.variables, newMapping);
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
	public ParameterizedFactor multiply(ParameterizedFactor secondFactor) {
		
		ArrayList<ParameterizedRandomVariable> newVariables = 
			new ArrayList<ParameterizedRandomVariable>(
					union(this.getParameterizedRandomVariables(), 
						  secondFactor.getParameterizedRandomVariables()));
		String newName = this.getName() + " * " + secondFactor.getName();
		ArrayList<Number> newMapping = new ArrayList<Number>();
		
		int[][] commonVariablesMapping = getCommonVariablesMapping(this, secondFactor);
		
		for (int i = 0; i < this.size(); i++) {
			Tuple t1 = this.getTuple(i);
			for(int j = 0; j < secondFactor.size(); j++) {
				Tuple t2 = secondFactor.getTuple(j);
				if (haveSameSubtuple(t1, t2, commonVariablesMapping)) {
					newMapping.add(this.getTupleValue(i) * secondFactor.getTupleValue(j));
				}
			}
		}
		
		return getInstance(newName, newVariables, newMapping);
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
	private int[][] getCommonVariablesMapping(ParameterizedFactor f1, ParameterizedFactor f2) {
		ArrayList<ParameterizedRandomVariable> rv2 = f2.getParameterizedRandomVariables();
		Iterator<ParameterizedRandomVariable> it1 =  f1.getParameterizedRandomVariables().iterator();
		int[][] mapping = new int[2][f1.getParameterizedRandomVariables().size()];
		int i = 0;
		
		while (it1.hasNext()) {
			ParameterizedRandomVariable v1 = it1.next();
			if (rv2.contains(v1)) {
				mapping[0][i] = f1.getParameterizedRandomVariableIndex(v1);
				mapping[1][i] = f2.getParameterizedRandomVariableIndex(v1);
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
	private boolean haveSameSubtuple(Tuple t1, Tuple t2, int[][] mapping) {
		Tuple commonSubTuple1 = t1.subTuple(mapping[0]);
		Tuple commonSubTuple2 = t2.subTuple(mapping[1]);
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
	private static List<ParameterizedRandomVariable> union(
			List<ParameterizedRandomVariable> prv1, 
			List<ParameterizedRandomVariable> prv2) {
		
		ArrayList<ParameterizedRandomVariable> result = new ArrayList<ParameterizedRandomVariable>(prv1);
		Iterator<ParameterizedRandomVariable> it = prv2.iterator();
		
		while (it.hasNext()) {
			ParameterizedRandomVariable v = it.next();
			if (!result.contains(v)) {
				result.add(v);
			}
		}
		return result;		
	}
}
