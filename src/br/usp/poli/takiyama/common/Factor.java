package br.usp.poli.takiyama.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RangeElement;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.MathUtils;

/**
 * Factors are table representations for joint distributions.
 * <p>
 * In order to save space, factors do not store all tuples, only their values.
 * Values are indexed based on the order of variables (columns) and the order
 * of the range of each variable.
 * </p>
 * @author ftakiyama
 *
 */
public class Factor implements Iterable<Tuple<RangeElement>> {
	
	private final String name;
	private final List<? extends Prv> variables;
	private final List<BigDecimal> values;
	
	private final int size;
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	/**
	 * Creates a parameterized factor.
	 * 
	 * @param name The name of this factor
	 * @param variables A ordered list of {@link Prv}.
	 * @param values A ordered list of {@link Number}, determined by the order
	 * of Prvs.
	 * @throws IllegalArgumentException If the number of values specified is not
	 * compatible with the PRVs specified.
	 */
	private Factor(String name, List<? extends Prv> variables, 
			List<BigDecimal> values) throws IllegalArgumentException {
		
		this.name = new String(name);
		this.variables = new ArrayList<Prv>(variables);
		this.values = new ArrayList<BigDecimal>(values);
		this.size = getSize(variables);
		
		if (values.size() != 0 && values.size() != size) {
			throw new IllegalArgumentException("Wrong number of values. Expected: " 
					+ size + ", received: " + values.size());
		}
	}
	
	
	/**
	 * Returns the expected size of this factor.
	 * 
	 * @return The expected size of this factor.
	 */
	private static int getSize(List<? extends Prv> variables) {
		int size = 1;
		if (variables.isEmpty()) {
			size = 0;
		}
		for (Prv prv : variables) {
			size = size * prv.range().size();
		}
		return size;
	}

	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * Returns a parameterized factor.
	 * <p>
	 * The order of <code>values</code> is dependent on the order of the list
	 * of PRVs and their ranges.
	 * </p>
	 * 
	 * @param name The name of this factor
	 * @param variables A ordered list of {@link Prv}.
	 * @param values A ordered list of {@link Number}, determined by the order
	 * of Prvs.
	 * @throws IllegalArgumentException If the number of values specified is not
	 * compatible with the PRVs specified.
	 */
	public static Factor getInstance(String name, List<? extends Prv> variables, 
			List<BigDecimal> values) throws IllegalArgumentException {
		return new Factor(name, variables, values);
	}
	
	
	/**
	 * Returns a parameterized factor with one PRV.
	 *  
	 * @param name The name of this factor
	 * @param variable A {@link Prv}.
	 * @param values A ordered list of {@link Number}
	 */
	public static Factor getInstance(String name, Prv variable, 
			List<BigDecimal> values) {
		List<Prv> variables = new ArrayList<Prv>(1);
		variables.add(variable);
		
		return Factor.getInstance(name, variables, values);
	}
	
	
	/**
	 * Returns a parameterized factor with the same variables and values as
	 * the specified factor.
	 * 
	 * @param factor The factor to "copy"
	 * @return A parameterized factor with the same variables and values as
	 * the specified factor.
	 */
	public static Factor getInstance(Factor factor) {
		return new Factor(factor.name, factor.variables, factor.values);
	}
	
	
	/**
	 * Returns a constant parameterized factor.
	 * <p>
	 * A constant factor returns the value 1 for all tuples in 
	 * the factor.
	 * </p>
	 * 
	 * @param variables A ordered list of {@link Prv}
	 * @return A constant parameterized factor with the specified PRVs.
	 */
	public static Factor getInstance(List<? extends Prv> variables) {
		int size = getSize(variables);
		List<BigDecimal> values = new ArrayList<BigDecimal>(size);
		Collections.fill(values, BigDecimal.ONE);
		
		return new Factor("1", variables, values);
	}
	
	
	/**
	 * Returns a constant factor with one PRV.
	 * <p>
	 * A constant factor returns the value 1 for all tuples in 
	 * the factor.
	 * </p>
	 * 
	 * @see #getInstance(List)
	 * @param variable A {@link Prv}
	 * @return A constant factor with the specified PRV.
	 */
	public static Factor getInstance(Prv variable) {
		List<Prv> variables = new ArrayList<Prv>(1);
		variables.add(variable);
		
		return Factor.getInstance(variables);
	}
	
	
	/**
	 * Returns an empty factor.
	 * <p>
	 * An empty factor has no values, nor variables.
	 * </p>
	 *  
	 * @see #getInstance(List)
	 * @return An empty factor.
	 */
	public static Factor getInstance() {
		List<Prv> empty = new ArrayList<Prv>(0);
		return Factor.getInstance(empty);
	}

	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	/**
	 * Returns the index of a tuple.
	 * 
	 * @param tuple The tuple to search.   
	 * @return The index of a tuple in this factor.
	 * @throws IllegalArgumentException if the tuple is empty.
	 */
	public int getIndex(Tuple<RangeElement> tuple) throws IllegalArgumentException {
		if (tuple.isEmpty()) {
			throw new IllegalArgumentException("This tuple is empty!");
		} 
		int index = 0;
		int r = 1;
		for (int i = tuple.size() - 1; i >= 0; i--) {
			index = index + r * indexOf(i, tuple);
			r = r * rangeSize(i);
		}
		return index;
	}

	
	/**
	 * Returns the index of the range element that occupies the specified
	 * position in the tuple.
	 * 
	 * @param i The position in the tuple
	 * @param tuple A tuple of {@link RangeElement}
	 * @return
	 */
	private int indexOf(int i, Tuple<RangeElement> tuple) {
		return variables.get(i).range().indexOf(tuple.get(i));
	}
	
	
	/**
	 * Returns PRV's range size occupying the specified position in this
	 * factor.
	 * 
	 * @param i PRV's index in this factor
	 * @return PRV's range size occupying the specified position in this
	 * factor.
	 */
	private int rangeSize(int i) {
		return variables.get(i).range().size();
	}
	
	
	private RangeElement rangeElementAt(int rangeIndex, int prvIndex) {
		return variables.get(prvIndex).range().get(rangeIndex);
	}
	
	
	/**
	 * Returns a tuple given its index.
	 * 
	 * @param index The index of the tuple.
	 * @return A tuple at the position specified by the parameter <b>index</b>.
	 */
	public Tuple<RangeElement> getTuple(int index) {
		List<RangeElement> values = new ArrayList<RangeElement>(variables.size());
		for (int j = variables.size() - 1; j > 0; j--) {
			int domainSize = rangeSize(j);
			values.add(rangeElementAt(index % domainSize, j));
			index = index / domainSize;	
		}
		values.add(rangeElementAt(index, 0));
		Collections.reverse(values);
		return Tuple.getInstance(values);
	}
	

	/**
	 * Returns the value of the tuple specified by its index.
	 * 
	 * @param index The index of the tuple in this factor.
	 * @return The value of the tuple specified by its index.
	 */
	public BigDecimal getValue(int index) {
		return values.get(index);
	}
	
	
	/**
	 * Returns the value of the specified tuple.
	 * 
	 * @param tuple The tuple whose value is to return
	 * @return The value of the specified tuple.
	 */
	public BigDecimal getValue(Tuple<RangeElement> tuple) {
		return getValue(getIndex(tuple));
	}
	
	
	/************  Iterator  **************************************************/
	
	/**
	 * This class is an Iterator over all tuples of this factor.
	 * <p>
	 * The code was inspired on OpenJDK's implementation of ArrayList Iterator.
	 * </p>
	 */
	private class Itr implements Iterator<Tuple<RangeElement>> {

		int nextElementToReturn;
		
		
		@Override
		public boolean hasNext() {
			return nextElementToReturn != size;
		}

		
		@Override
		public Tuple<RangeElement> next() {
			int i = nextElementToReturn;
			if (i > size) {
				throw new NoSuchElementException();
			}
			nextElementToReturn = i + 1;
			return getTuple(i);
		}

		
		/**
		 * Throws {@link UnsupportedOperationException}.
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	
	/**
	 * Returns an iterator over all tuples of this factor.
	 * <p>
	 * The tuples returned <b>depend</b> on the order of its {@link Prv}s.
	 * </p>
	 * 
	 * @return An iterator over all tuples of this factor
	 */
	public Iterator<Tuple<RangeElement>> iterator() {
		return new Itr();
	}
	
	
	/**
	 * Returns an iterator over all tuples of a parameterized factor having
	 * the specified variables.
	 * <p>
	 * The tuples returned <b>depend</b> on the order of the give 
	 * parameterized random variable list, that is, the order of parameterized
	 * random variables define the way the tuples are created.
	 * </p>
	 * 
	 * @param variables A list of parameterized random variables.
	 * @return An iterator over all tuples of a parameterized factor having
	 * the specified parameterized random variables.
	 */
	public static Iterator<Tuple<RangeElement>> iterator(List<Prv> variables) {
		return Factor.getInstance(variables).iterator();
	}
	

	/**************************************************************************/
	
	
	/**
	 * Returns the number of values in this factor, which is the same as the
	 * number of tuples in this factor.
	 * 
	 * @return The number of values in this factor.
	 */
	public int size() {
		return size;
	}
	
	
	/**
	 * Returns the name of this factor.
	 * 
	 * @return The name of this factor.
	 */
	public String name() {
		return name;
	}
	
	
	/**
	 * Returns the list of {@link Prv}s associated with this factor. This
	 * list has the same order in which Prvs were inserted when creating
	 * this factor.
	 * 
	 * @return The list of {@link Prv}s associated with this factor
	 */
	public List<Prv> variables() {
		return new ArrayList<Prv>(variables);
	}
	
	
	/**
	 * Returns the values of all tuples, in the order they were created.
	 *  
	 * @return A list containing all values of the factor, in order.
	 */
	public List<BigDecimal> values() {
		// TODO make it more flexible
		return new ArrayList<BigDecimal>(values);
	}
	
	
	/**
	 * Returns <code>true</code> if the specified {@link Term} is in this factor.
	 * 
	 * @param t The term to search for.
	 * @return <code>true</code> if the specified term specified is in this 
	 * factor, <code>false</code> otherwise.
	 */
	public boolean contains(Term t) {
		for (Prv prv : variables) {
			if (prv.contains(t)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Returns the number of occurrences of the specified {@link Term} in 
	 * {@link Prv}s from this factor.
	 * 
	 * @param t The term to search for.
	 * @return the number of occurrences of the specified {@link Term} in 
	 * {@link Prv}s from this factor.
	 */
	public int occurrences(Term t) {
		int count = 0;
		for (Prv prv : variables) {
			if (prv.contains(t)) {
				count++;
			}
		}
		return count;
	}
	
	
	/**
	 * Returns the first occurrence of a PRV in this factor having the 
	 * specified term, according to the order returned by the iterator of 
	 * variables of this factor.
	 * <p>
	 * If there is no PRV having the specified term, returns an empty 
	 * {@link StdPrv}. 
	 * </p>
	 * 
	 * @param t The term to search for
	 * @return The first occurrence of a PRV in this factor having the 
	 * specified term.
	 */
	public Prv getVariableHaving(Term t) {
		Prv result = StdPrv.getInstance();
		for (Prv prv : variables) {
			if (prv.contains(t)) {
				result = prv;
			}
		}
		return result;
	}
	
	
	/**
	 * Returns <code>true</code> if this factor is a sub-factor of the 
	 * specified factor.
	 * <p>
	 * Factor F1 is a sub-factor of factor F2 if the set of {@link Prv}s from
	 * F1 is a subset of the of Prvs from F2.
	 * </p>
	 * 
	 * @return <code>true</code> if this factor is a sub-factor of the 
	 * specified factor, <code>false</code> otherwise.
	 */
	public boolean isSubFactorOf(Factor factor) {
		// Quick check: return false if this factor is bigger
		if (factor.variables().size() < variables.size()) {
			return false;
		}
		
		// Checks if all variables in this factor exist in the specified factor
		Iterator<? extends Prv> it = variables.iterator();
		while (it.hasNext()) {
			Prv prv = it.next();
			if (!factor.variables().contains(prv)) {
				return false;
			}	
		}
		return true;
	}
	
	
	/**
	 * Returns <code>true</code> if this factor is constant.
	 * <p>
	 * A constant factor returns the value 1 for all tuples in the factor.
	 * </p>
	 * 
	 * @return <code>true</code> if this factor is constant, <code>false</code>
	 * otherwise.
	 */
	public boolean isConstant() {
		for (BigDecimal val : values) {
			if (!val.equals(BigDecimal.ONE)) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Returns <code>true</code> if this factor is empty.
	 * <p>
	 * An empty factor has no variables, nor values.
	 * </p>
	 * 
	 * @return  <code>true</code> if this factor is empty, <code>false</code>
	 * otherwise.
	 */
	public boolean isEmpty() {
		boolean noVariables = variables.isEmpty();
		boolean noValues = values.isEmpty();
		return noVariables && noValues;
	}

	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/
	
	@Override
	public int hashCode() { 
		int result = 17;
		result = 31 + result + Arrays.hashCode(variables.toArray(new Prv[variables.size()]));
		result = 31 + result + Arrays.hashCode(values.toArray(new BigDecimal[values.size()]));
		return result;
	}
	
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof Factor))
	    	return false;
	    // Tests if both have the same attributes
	    Factor targetObject = (Factor) other;
	    return ((variables == null) ? targetObject.variables == null 
	    		 					: variables.equals(targetObject.variables)) 
	    		&&
    		   ((values == null) ? targetObject.values == null 
    			   				 : values.equals(targetObject.values)); 		
	}
	
	
	@Override
	public String toString() {
		String result = this.name + "\n";
		
		if (this.variables.isEmpty()) {
			return this.name + " is empty.";
		}
		
		String thinRule = "";
		String thickRule = "";
		String cellFormat = "%-10s"; //TODO: change to something more dynamic
		String valueCellFormat = "%-10s\n";
		
		// Create the rules - aesthetic
		for (int i = 0; i <= this.variables.size(); i++) {
			thinRule += String.format(cellFormat, "").replace(" ", "-");
		}
		thickRule = thinRule.replace("-", "=");
		
		// Top rule
		result += thickRule + "\n";
		
		// Print the variables names
		for (Prv prv : variables) {
			result += String.format(cellFormat, prv.toString()); 
		}
		
		// Value column
		result += String.format(cellFormat + "\n", "VALUE");
		
		// Mid rule
		result += thinRule + "\n";
		
		// Print the contents
		for (int i = 0; i < values.size(); i++) {
			Tuple<RangeElement> tuple = getTuple(i);
			for (int j = 0; j < tuple.size(); j++) {
				result += String.format(cellFormat, tuple.get(j));
			}
			// Round the value to 6 digits
			result += String.format(valueCellFormat, values.get(i).toString());			
		}
		
		// Bottom rule
		result += thickRule + "\n";
		
		return result;
	}
	
	
	/* ************************************************************************
	 *    Setters
	 * ************************************************************************/

	/**
	 * Returns the result of applying the specified substitution to this
	 * Factor. The substitution is applied to PRVs in this Factor, but its
	 * values are not modified.
	 * @param s The substitution to apply
	 * @return The result of applying the specified substitution to this
	 * Factor
	 */
	public Factor apply(Substitution s) {
		List<Prv> substitutedVars = new ArrayList<Prv>(variables.size());
		for (Prv prv : variables) {
			Prv substituted = prv.apply(s);
			substitutedVars.add(substituted);
		}
		return Factor.getInstance(name, substitutedVars, values);
	}
	
	
	/* ************************************************************************
	 *    Multiplication, Power and Sum Out
	 * ************************************************************************/

	/**
	 * Sums out a random variable from a factor.
	 * <p>
	 * Suppose F is a factor on random variables x<sub>1</sub>,...,
	 * x<sub>i</sub>,...,x<sub>j</sub>. The summing out of random variable 
	 * x<sub>i</sub> from F, denoted as &Sigma;<sub>x<sub>i</sub></sub>F 
	 * is the factor on 
	 * random variables x<sub>1</sub>,...,x<sub>i-1</sub>, x<sub>i+1</sub>, 
	 * ..., x<sub>j</sub> such that
	 * </p>
	 * (&Sigma;<sub>x<sub>i</sub></sub> F)
	 *   (x<sub>1</sub>,...,x<sub>i-1</sub>,x<sub>i+1</sub>,...,x<sub>j</sub>) =	
	 * 	   &Sigma; <sub>y &isin; dom(x<sub>i</sub>)</sub> 
	 *     F(x<sub>1</sub>,...,x<sub>i-1</sub>,x<sub>i</sub> = y,
	 *       x<sub>i+1</sub>,...,x<sub>j</sub>).
	 * <p>
	 * If the variable to be summed out does not exist in the factor, this
	 * method returns the specified factor unmodified.
	 * </p>
	 * 
	 * @param prv The {@link Prv} to be summed out.
	 * @return A factor with the specified Prv summed out, or this factor
	 * if <code>prv</code> does not exist in this factor.
	 */
	public Factor sumOut(Prv prv) {
		
		// Checks if the random variable exists
		if (!variables.contains(prv)) {
			return this;
		}
		
		// Creates a flag for values in this factor that were already processed
		boolean[] wasVisited = new boolean[size];
		Arrays.fill(wasVisited, false);
		
		// Removes the PRV to be summed out 
		List<Prv> vars = new ArrayList<Prv>(variables);
		vars.remove(prv);
		
		// Creates the new mapping, summing out the PRV
		List<BigDecimal> vals = new ArrayList<BigDecimal>(size / prv.range().size());
		int prvIndex = variables.indexOf(prv);
		for (int i = 0; i < size; i++) {
			
			if (!wasVisited[i]) {
				
				Tuple<RangeElement> current = getTuple(i); 
				BigDecimal sum = BigDecimal.ZERO;
				
				// Builds all tuples varying only the PRV being summed out
				// and sums their values
				for (RangeElement e : prv.range()) {
					Tuple<RangeElement> next = current.set(prvIndex, e);
					BigDecimal correction = prv.getSumOutCorrection(e);
					sum = sum.add(getValue(next).multiply(correction));
					wasVisited[getIndex(next)] = true;
				}
				vals.add(sum);
			}
		}
		
		// Creates the new factor
		return getInstance(name, vars, vals);
	}
	
		
	/**
	 * Returns this factor raised by <code>p/q</code>.
	 * <p>
	 * Raising a factor to some exponent is the same as raising its values to
	 * that exponent.  
	 * </p>
	 * 
	 * @param p Exponents's numerator
	 * @param q Exponenet's denominator
	 * @return The value of this factor raised to <code>p/q</code>
	 */
	public Factor pow(int p, int q) {
		List<BigDecimal> newValues = new ArrayList<BigDecimal>();
		for (BigDecimal base : values) {
			newValues.add(MathUtils.pow(base, p, q));
		}
		return getInstance(name, variables, newValues);
	}
	
	
	/**
	 * Multiplies this factor with the specified factor.
	 * <p>
	 * Given two factors 
	 * 	   F<sub>1</sub>(x<sub>1</sub>,...,x<sub>n</sub>,
	 *                   y<sub>1</sub>,...,y<sub>j</sub>) and 
	 *     F<sub>2</sub>(y<sub>1</sub>,...,y<sub>j</sub>,
	 *                   z<sub>1</sub>,...,z<sub>k</sub>) 
	 * the resulting factor will be 
	 * F(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,...,
	 *         y<sub>j</sub>,z<sub>1</sub>,...,z<sub>k</sub>) =
	 *     F<sub>1</sub>(x<sub>1</sub>,...,x<sub>n</sub>,y<sub>1</sub>,..., 
	 *         y<sub>j</sub>) x 
	 *     F<sub>2</sub>(y<sub>1</sub>,...,y<sub>j</sub>,z<sub>1</sub>,...,
	 *         z<sub>k</sub>)
	 * </p>
	 * That is, for each assignment of values to the variables in the factors,
	 * the method multiply the values that have the same assignment for common 
	 * variables. 
	 * 
	 * @param secondFactor The second factor to be multiplied
	 * @return The multiplication of fisrtFactor by secondFactor.
	 */
	public Factor multiply(Factor factor) {
		
		String newName = name() + "*" + factor.name();
		List<Prv> union = Lists.union(variables(), factor.variables());
		List<BigDecimal> mult = new ArrayList<BigDecimal>(getSize(union));
		
		int[][] mapOfCommomVariables = getMapOfCommomVariables(this, factor);
		
		for (Tuple<RangeElement> t1 : this) {
			for (Tuple<RangeElement> t2 : factor) {
				if (haveSameSubtuple(t1, t2, mapOfCommomVariables)) {
					mult.add(getValue(t1).multiply(factor.getValue(t2)));
				}
			}
		}
				
		return getInstance(newName, union, mult);
	}
	
	
	/**
	 * Returns a mapping from indexes of variables in the first factor to the
	 * indexes of the variables that also appear in the second factor.
	 * <p>
	 * The mapping is a 2 x n matrix, where n is the number of common
	 * random variables between the first factor and the second factor.
	 * </p>
	 * <p>
	 * The set of random variables from the first factor is analyzed
	 * sequentially, and for each random variable in the set, the set from the
	 * second factor is searched for a match. Thus, the first line of the
	 * result will be in ascending order, while the second line may have
	 * an arbitrary ordering.
	 * </p>
	 * <p>
	 * For example, suppose that f1(x1,x2,x3,x4,x5) and f2(x5,x4,x1) are factors
	 * passed as parameter for this method. Then the mapping will be the
	 * following matrix:
	 * </p>
	 * <table>
	 * <tr><td>0</td><td>3</td><td>4</td></tr>
	 * <tr><td>2</td><td>1</td><td>0</td></tr>
	 * </table>
	 * <p>
	 * which means that x1 (index 0) in f1 has a match in f2 at index 2, and
	 * so on.
	 * </p>
	 * 
	 * @param f1 The first factor.
	 * @param f2 The second factor.
	 * @return A mapping of indexes from common variables between f1 and f2.
	 */
	private int[][] getMapOfCommomVariables(Factor f1, Factor f2) {
		int[][] mapping = new int[2][f1.variables().size()];
		int size = 0;
		
		for (Prv prv1 : f1.variables()) {
			if (f2.variables().contains(prv1)) {
				mapping[0][size] = f1.variables().indexOf(prv1);
				mapping[1][size] = f1.variables().indexOf(prv1);
				size++;
			}
		}
		return trim(mapping, size);
	}
	
	
	/**
	 * Trims the specified matrix to the specified size. The length of the
	 * matrix is preserved.
	 * 
	 * @param matrix The matrix to trim
	 * @param size The limit of the size of each line of the matrix.
	 * @return The specified matrix trimmed to the specified size
	 */
	private int[][] trim(int[][] matrix, int size) {
		int[][] m = new int[matrix.length][size];
		for (int j = 0; j < size; j++) {
			m[0][j] = matrix[0][j];
			m[1][j] = matrix[1][j];
		}
		return m;
	}
	
	
	/**
	 * Returns <code>true</code> if both tuples have the same sub-tuple. 
	 * The sub-tuple is defined according to a map. 
	 * 
	 * @see Factor#getMapOfCommomVariables 
	 * @param t1 The first tuple to check
	 * @param t2 The second tuple to check
	 * @param map A mapping that connects indexes representing the same PRV
	 * @return <code>true</code> if tuples have the same value for the 
	 * sub-tuple, <code>false</code> otherwise.
	 */
	private boolean haveSameSubtuple(Tuple<?> t1, Tuple<?> t2, int[][] map) {
		Tuple<?> st1 = t1.subTuple(map[0]);
		Tuple<?> st2 = t2.subTuple(map[1]);
		return st1.equals(st2);
	}
}
