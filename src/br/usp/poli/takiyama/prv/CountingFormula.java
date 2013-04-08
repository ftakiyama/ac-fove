package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;

/**
 * Definition by [Kisynski, 2010]:
 * <br>
 * A counting formula is of the form #<sub>A:C<sub>A</sub></sub>[f(...,A,...)], 
 * where:
 * <li> A is a logical variable that is bound by the # sign 
 * <li> C is a set of inequality constraints involving A 
 * <li> f(...,A,...) is a parameterized random variable. 
 * <br>
 * The value of #<sub>A:C<sub>A</sub></sub>[f(...,A,...)], given an assignment 
 * of values to random variables v, is the histogram function 
 * h<sup>v</sup> : range(f) &rightarrow; N defined by
 * <br>
 * v(#<sub>A:C<sub>A</sub></sub>[f(...,A,...)]) = h<sup>v</sup>(x) = 
 * |{a &in; (D(A):C) : v(f(...,a,...)) = x}|, 
 * <br>
 * where x is in the range of f.
 * <br>
 * <br>
 * This class extends ParameterizedRandomVarible, but I regret having done that.
 * 
 * @author ftakiyama
 *
 */
public class CountingFormula extends ParameterizedRandomVariable {
	
	private LogicalVariable boundLogicalVariable;
	private HashSet<Constraint> constraints;
	private ParameterizedRandomVariable prv; 
	private ArrayList<Histogram<String>> range;
		
	/**
	 * This class represents the elements of the range of a counting formula.
	 * Histograms are tuples composed by buckets, which in turn store the
	 * count of elements from the range of the counted parameterized random
	 * variable.
	 * 
	 * @author ftakiyama
	 *
	 * @param <T> The type of element in the range of the counted 
	 * parameterized random variable. For now, it is String.
	 */
	public class Histogram<T> {
		
		private LinkedHashMap<T, Integer> distribution;
		
		/**
		 * Constructor.
		 * <br>
		 * Creates an empty histogram.
		 * @param prvRange A list with the elements of the range of f
		 */
		Histogram(List<T> prvRange) {
			distribution = new LinkedHashMap<T, Integer>(2 * prvRange.size());
			for (int i = 0; i < prvRange.size(); i++) {
				distribution.put(prvRange.get(i), Integer.valueOf(0));
			}
		}
		
		/**
		 * Constructor.
		 * <br>
		 * Creates a copy of the specified histogram.
		 * @param histogram The histogram to copy
		 */
		Histogram(Histogram<T> histogram) {
			this.distribution = 
				new LinkedHashMap<T, Integer>(histogram.distribution);
		}
		
		/**
		 * Returns the count of the specified bucket
		 * @param rangeValue The key to the bucket
		 * @return The count of the specified bucket
		 */
		int getCount(T rangeValue) {
			return distribution.get(rangeValue);
		}
		
		/**
		 * Returns the number of buckets in this histogram, which equals to
		 * the size of range(f), where f is the parameterized random variable 
		 * being counted. 
		 * @return The number of buckets in this histogram
		 */
		int size() {
			return distribution.size();
		}
		
		/**
		 * Add the specified amount to the count of the specified range
		 * element
		 * @param rangeValue The key to the bucket 
		 * @param amount The amount to sum to the bucket
		 */
		void addCount(T rangeValue, int amount) {
			distribution.put(rangeValue, distribution.get(rangeValue) + amount);
		}
		
		/**
		 * Set the specified amount as the count for the specified bucket
		 * @param rangeValue The key to the bucket
		 * @param amount The amount to set into the bucket
		 */
		void setCount(T rangeValue, int amount) {
			distribution.put(rangeValue, amount);
		}
		
		/**
		 * Returns true if this histogram contains a bucket with the 
		 * specified count.
		 * @param count A count to check
		 * @return true If this histogram contains a bucket with the 
		 * specified count, false otherwise.
		 */
		public boolean containsValue(int count) {
			return distribution.containsValue(Integer.valueOf(count));
		}
		
		@Override
		public String toString() {
			StringBuilder histogram = new StringBuilder();
			histogram.append("( ");
			for (T key : distribution.keySet()) {
				histogram.append("#.").append(key).append("=")
						 .append(distribution.get(key)).append(", ");
			}
			histogram.deleteCharAt(histogram.lastIndexOf(","));
			histogram.append(")");
			return histogram.toString();
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (!(other instanceof Histogram<?>))
				return false;
			Histogram<?> targetObject = (Histogram<?>) other;
			return (this.distribution == null) 
						? (targetObject.distribution == null) 
						: this.distribution.equals(targetObject.distribution);
		}
		
		@Override
		public int hashCode() { 
			int result = 17;
			result = 31 + result + this.distribution.hashCode();
			return result;
		}
	}
	
	
	
	/**
	 * Private constructor. To create CountingFormulas, use the factory
	 * getInstance().
	 * 
	 * @param boundVariable The logical variable bound to this counting
	 * formula (that is, the variable being counted)
	 * @param constraints A set of constraints involving the bound logical
	 * variable
	 * @param prv The parameterized random variable associated with this 
	 * counting formula.
	 * 
	 * @throws IllegalArgumentException If the set of constraints contains
	 * a constraint not involving the bound logical variable OR if the 
	 * specified logical variable is not a parameter of the specified 
	 * parameterized random variable.
	 */
	private CountingFormula(
			LogicalVariable boundVariable, 
			Set<Constraint> constraints, 
			ParameterizedRandomVariable prv) throws IllegalArgumentException {
		super(prv); 
		this.boundLogicalVariable = boundVariable;
		this.constraints = new HashSet<Constraint>(constraints);
		this.prv = prv;
		this.range = new ArrayList<Histogram<String>>();
		
		for (Constraint c : constraints) {
			if (!c.contains(boundVariable))
				throw new IllegalArgumentException("The set of constraints" 
						+ " has constraints not involving " 
						+ boundVariable);
		}
		
		if (!prv.contains(boundVariable))
			throw new IllegalArgumentException(boundVariable 
					+ " is not a parameter of "
					+ prv);
		
		int allowedDomainSize = 
			boundLogicalVariable.individualsSatisfying(constraints).size();
		Histogram<String> histogram = new Histogram<String>(prv.getRange());
		generateHistograms(this.range, allowedDomainSize, histogram, 0);
	}
	
	/**
	 * Static factory of counting formulas.
	 * @param boundVariable The logical variable bound to this counting
	 * formula (that is, the variable being counted)
	 * @param constraints A set of constraints involving the bound logical
	 * variable
	 * @param prv The parameterized random variable associated with this 
	 * counting formula.
	 * @return A counting formula with the specified parameters.
	 */
	public static CountingFormula getInstance(
			LogicalVariable boundVariable, 
			Set<Constraint> constraints, 
			ParameterizedRandomVariable prv) {
		return new CountingFormula(boundVariable, constraints, prv);
	}
	

	
	/* ************************************************************************
	 *     PRV overridden methods
	 * ************************************************************************/
	
	/**
	 * Returns null. Execution will stop if this method is called.
	 * @param functor
	 * @param range
	 * @param parameters
	 * @return
	 */
	public static ParameterizedRandomVariable getInstance(
			String functor, 
			List<String> range, 
			List<Term> parameters) {
		System.err.println("Not implemented!");
		System.exit(-1);
		return null;
	}
	
	/**
	 * Returns null. Execution will stop if this method is called.
	 * @param functor
	 * @param parameters
	 * @return
	 */
	static ParameterizedRandomVariable getInstance(
			PredicateSymbol functor, 
			List<Term> parameters) {
		System.err.println("Not implemented!");
		System.exit(-1);
		return null;
	}
	
	/**
	 * Returns null. Execution will stop if this method is called.
	 * 
	 */
	public ParameterizedRandomVariable applySubstitution(Substitution s) {
		System.err.println("Not implemented!");
		System.exit(-1);
		return null;
	}
	
	@Override
	public ParameterizedRandomVariable applyOneSubstitution(Binding s) {
		if (s.secondTerm() instanceof Constant) { //TODO take it out
			System.out.println(
					"WARN " 
					+ s.toString()
					+ " is not a valid substitution for "
					+ this.toString());
			return this;
		}
		
		ParameterizedRandomVariable newPrv = this.prv.applyOneSubstitution(s);
		HashSet<Constraint> newConstraints = new HashSet<Constraint>();
		for (Constraint constraint : this.constraints)  {
			newConstraints.add(constraint.applySubstitution(s));
		}
		
		return new CountingFormula((StdLogicalVariable) s.secondTerm(), 
				newConstraints, newPrv);
	}
	
	@Override
	public String getElementFromRange(int index) {
		return range.get(index).toString();
	}
	
	/**
	 * Returns the range of this counting formula (set of histograms).
	 * Histograms are converted to String.
	 * @return The range of this counting formula.
	 */
	public List<String> getRange() {
		ArrayList<String> range = new ArrayList<String>(this.range.size());
		for (Histogram<String> histogram : this.range) {
			range.add(histogram.toString());
		}
		return range;
	}
	
	/**
	 * Returns the size of the range of this counting formula.
	 * @return The size of the range of this counting formula.
	 */
	public int getRangeSize() {
		return this.range.size();
	}
	
	@Override
	public boolean contains(Term t) {
		return this.getParameters().contains(t);
	}
	
	@Override
	public Set<StdLogicalVariable> getParameters() {
		Set<StdLogicalVariable> parameters = new HashSet<StdLogicalVariable>(
				this.prv.getParameters());
		parameters.remove(boundLogicalVariable);
		return parameters;
	}
	
	/**
	 * Returns the name of the parameterized random variable associated with
	 * this counting formula.
	 * @return The name of the parameterized random variable associated with
	 * this counting formula.
	 */
	public String getName() {
		return this.prv.getName();//"#."+ this.boundLogicalVariable + "[" + this.prv.getName() + "]";
	}
	
	// This method is the same of PRV, but I'm overriding it in case I change
	// inheritance to interface. At least, I should.
	@Override
	public int getGroundSetSize(Set<Constraint> constraints) {
		int size = 1;
		for (StdLogicalVariable lv : this.getParameters()) {
			size = size * lv.individualsSatisfying(constraints).size();
		}
		return size;
	}
	
	/* ************************************************************************
	 *     Methods for counting formulas
	 * ************************************************************************/
	
	/**
	 * Returns the logical variable bound to the # sign.
	 * @return the logical variable bound to the # sign.
	 */
	public LogicalVariable getBoundVariable() {
		return this.boundLogicalVariable;
	}
	
	/**
	 * Returns the parameterized random variable associated with this 
	 * counting formula.
	 * @return The parameterized random variable associated with this 
	 * counting formula.
	 */
	public ParameterizedRandomVariable getPrv() {
		return this.prv;
	}
	
	/**
	 * Returns the set of constraints of this counting formula.
	 * @return The set of constraints of this counting formula.
	 */
	public Set<Constraint> getConstraints() {
		return this.constraints;
	}
	
	/**
	 * Returns the count of a value from range(f), given the element from the
	 * range of this counting formula.
	 * 
	 * @param rangeIndex The index of the element in the range of this 
	 * counting formula.
	 * @param bucketIndex The index of the bucket in the histogram.
	 * @return The count of the specified value from range(f) for the
	 * specified element from this counting formula.
	 */
	public int getCount(int rangeIndex, int bucketIndex) {
		return range.get(rangeIndex)
					.getCount(prv.getElementFromRange(bucketIndex));
	}
	
	/**
	 * Returns the size of the range of the parameterized random 
	 * variable associated with this counting formula
	 * @return The size of the range of the parameterized random 
	 * variable associated with this counting formula
	 */
	public int getCountedVariableRangeSize() {
		return this.prv.getRangeSize();
	}
	
	@Override
	public Substitution getMgu(ParameterizedRandomVariable other) {
		if (other instanceof CountingFormula)
			return getMgu((CountingFormula) other);
		return this.prv.getMgu(other);
	}
	
	@Override
	public Substitution getMgu(CountingFormula countingFormula) {
		return this.prv.getMgu(countingFormula.prv);
	}
	
	/**
	 * Adds a constraint to this counting formula. This method returns a new
	 * instance of the counting formula.
	 * @param constraint The constraint to be added to the counting formula.
	 * @return A new counting formula equal to this one with the addition of 
	 * the constraint provided.
	 */
	public CountingFormula addConstraint(Constraint constraint) {
		HashSet<Constraint> constraints = 
				new HashSet<Constraint>(this.constraints);
		constraints.add(constraint);
		return new CountingFormula(this.boundLogicalVariable, 
				constraints, this.prv);
	}
	
	/**
	 * Adds the specified amount to the bucket of the specified histogram.
	 * <br>
	 * Returns the histogram converted to String.
	 * <br>
	 * This method does not modify this counting formula, all operations are
	 * made on copies of elements from this counting formula.
	 * 
	 * @param rangeIndex The element in the range of the counting formula to
	 * modify
	 * @param bucketIndex The bucket where addition will be made
	 * @param amount The amount to add
	 * @return The histogram with the specified amount added to the specified
	 * bucket in String format.
	 */
	public String addCount(int rangeIndex, int bucketIndex, int amount) {
		Histogram<String> histogram = 
			new Histogram<String>(this.range.get(rangeIndex));
		histogram.addCount(prv.getElementFromRange(bucketIndex), amount);
		return histogram.toString();
	}
	
	/**
	 * <p>
	 * Returns true if the counting formula can be converted to a standard
	 * parameterized random variable, false otherwise.
	 * </p>
	 * <p>
	 * A counting formula can be converted to standard parameterized random
	 * variable when the set of constraints is big enough to restrict the
	 * bound logical variable to a single individual.
	 * </p>
	 * @return True if the counting formula can be converted to a standard
	 * parameterized random variable, false otherwise.
	 */
	public boolean canBeConvertedToPrv() {
		return (boundLogicalVariable
				.individualsSatisfying(constraints).size() == 1);
	}
	
	/**
	 * Converts this counting formula to a standard parameterized random 
	 * variable when it its logical variable is constrained to a single 
	 * individual.
	 * @return The converted counting formula to a parameterized random variable.
	 */
	public ParameterizedRandomVariable convertToPrv() {
		Population loneIndividual = 
			boundLogicalVariable.individualsSatisfying(constraints);
		if (loneIndividual.size() != 1) {
			return this;
		}
		return prv.applyOneSubstitution(
				Binding.getInstance(
						boundLogicalVariable, 
						loneIndividual.iterator().next()));
	}
	
	/**
	 * Returns the range of this counting formula (set of histograms).
	 * <b>Attention!</b> This method returns a list of Histograms, but
	 * this type is only defined inside CountingFormula. Maybe I should
	 * return an iterator over the range converted to String.
	 * @return The range of this counting formula.
	 */
	public List<Histogram<String>> getCountingFormulaRange() {
		return new ArrayList<Histogram<String>>(this.range);
	}
	
	/**
	 * Generates all possible histograms for this counting formula. All 
	 * histograms are put in the specified list.
	 * <br>
	 * This function is recursive, and behaves well for histograms with
	 * short ranges (for instance, binary ranges). Performance suffers
	 * exponentially as the range grows.
	 * 
	 * @param allHistograms The set of all histograms.
	 * @param maxCount The current maximum count
	 * @param histogram The current histogram being built
	 * @param currentBucket The current bucket index
	 */
	private void generateHistograms(
			List<Histogram<String>> allHistograms, 
			int maxCount, 
			Histogram<String> histogram, 
			int currentBucket) {
		if (currentBucket == histogram.size() - 1 || maxCount == 0) {
			histogram.setCount(this.prv.getElementFromRange(currentBucket), 
					maxCount);
			allHistograms.add(new Histogram<String>(histogram));
			return;
		}
		int count = maxCount;
		while (count >= 0) {
			histogram.setCount(
					this.prv.getElementFromRange(currentBucket), count);
			generateHistograms(allHistograms, maxCount - count, histogram, 
					currentBucket + 1);
			count--;
		}
	}
	
	/**
	 * Returns the result of applying a substitution to the parameterized
	 * random variable of this counting formula.
	 * @param s The substitution to be made.
	 * @return The parameterized random variable associated with this 
	 * counting formula with the specified substitution applied.
	 */
	public ParameterizedRandomVariable applySubstitutionToPrv(Binding s) {
		return this.prv.applyOneSubstitution(s);
	}
	
	/* ************************************************************************
	 *     Object overridden methods
	 * ************************************************************************/
		
	@Override
	public String toString() {
		return "#." 
		+ this.boundLogicalVariable.toString() 
		+ ":"
		+ this.constraints.toString()
		+ " [ " 
		+ this.prv.toString()
		+ " ]"; 
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
	    	return true;
		if (!(other instanceof CountingFormula))
	    	return false;
		CountingFormula targetObject = (CountingFormula) other;
	    return ((this.boundLogicalVariable == null) 
	    			? (targetObject.boundLogicalVariable == null) 
	    			: this.boundLogicalVariable.equals(
	    					targetObject.boundLogicalVariable))
	    		&& ((this.constraints == null) 
	    				? (targetObject.constraints == null) 
	    				: this.constraints.equals(targetObject.constraints))
	    		&& ((this.prv == null) 
	    				? (targetObject.prv == null) 
	    				: this.prv.equals(targetObject.prv))
	    		&& ((this.range == null) 
	    				? (targetObject.range == null) 
	    				: this.range.equals(targetObject.range));
	}
	
	@Override
	public int hashCode() { // Algorithm extracted from Bloch,J. Effective Java
		int result = 17;
		result = 31 + result + boundLogicalVariable.hashCode();
		result = 31 + result + this.constraints.hashCode();
		result = 31 + result + this.prv.hashCode();
		result = 31 + result + this.range.hashCode();
		return result;
	}
}
