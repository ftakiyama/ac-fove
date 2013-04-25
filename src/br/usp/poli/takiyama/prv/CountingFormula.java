package br.usp.poli.takiyama.prv;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.utils.MathUtils;

/**
 * Definition by [Kisynski, 2010]:
 * <p>
 * A counting formula is of the form #<sub>A:C<sub>A</sub></sub>[f(...,A,...)], 
 * where:
 * <li> A is a logical variable that is bound by the # sign 
 * <li> C is a set of inequality constraints involving A 
 * <li> f(...,A,...) is a parameterized random variable. 
 * </p>
 * <p>
 * The value of #<sub>A:C<sub>A</sub></sub>[f(...,A,...)], given an assignment 
 * of values to random variables v, is the histogram function 
 * h<sup>v</sup> : range(f) &rightarrow; N defined by
 * v(#<sub>A:C<sub>A</sub></sub>[f(...,A,...)]) = h<sup>v</sup>(x) = 
 * |{a &in; (D(A):C) : v(f(...,a,...)) = x}|, 
 * where x is in the range of f.
 * </p>
 * 
 * @author Felipe Takiyama
 *
 */
public class CountingFormula implements Prv {

	private final LogicalVariable bound;
	private final Set<Constraint> constraints;
	private final Prv prv; 
	private final List<Histogram<? extends RangeElement>> range;
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	/**
	 * Creates a counting formula
	 * 
	 * @param boundVariable The logical variable bound to this counting
	 * formula (that is, the variable being counted)
	 * @param constraints A set of constraints involving the bound logical
	 * variable
	 * @param prv The parameterized random variable associated with this 
	 * counting formula.
	 * 
	 * @throws IllegalArgumentException If the set of constraints contains
	 * a constraint not involving the bound logical variable <b>or</b> if the 
	 * specified logical variable is not a parameter of the specified 
	 * parameterized random variable.
	 */
	private CountingFormula(LogicalVariable bound, Set<Constraint> constraints, 
			Prv prv) throws IllegalArgumentException {
		
		this.prv = StdPrv.getInstance(prv);
		this.bound = StdLogicalVariable.getInstance(bound);
		this.constraints = new HashSet<Constraint>(constraints);
		this.range = new ArrayList<Histogram<? extends RangeElement>>(); 
		
		if (!prv.contains(bound)) {
			throw new IllegalArgumentException();
		}
		
		for (Constraint c : constraints) {
			if (!c.contains(bound)) {
				throw new IllegalArgumentException();
			}
		}
		
		int allowedDomainSize = this.bound.numberOfIndividualsSatisfying(constraints);
		Histogram<RangeElement> histogram = new Histogram<RangeElement>(prv.range());
		generateHistograms(this.range, allowedDomainSize, histogram, 0);
	}
	
	
	/**
	 * Generates all possible histograms for this counting formula. All 
	 * histograms are put in the specified list.
	 * <p>
	 * This function is recursive, and behaves well for histograms with
	 * short ranges (for instance, binary ranges). Performance suffers
	 * exponentially as the range grows.
	 * </p>
	 * <p>
	 * Initially, 
	 * <li><code>allHistograms</code> must be an empty list, which will
	 * contain all histograms at the end of the execution of this function.
	 * <li><code>maxCount</code> is the maximum count a bucket can hold, and must
	 * be calculated beforehand considering bound logical variable population
	 * size and constraints involving this logical variable. 
	 * <li><code>histogram</code> is an empty histogram.
	 * <li><code>currentBucket</code> is 0.
	 * </p>
	 * <br>
	 * 
	 * @param allHistograms The set of all histograms.
	 * @param maxCount The current maximum count
	 * @param histogram The current histogram being built
	 * @param currentBucket The current bucket index
	 */
	private void generateHistograms(List<Histogram<? extends RangeElement>> allHistograms, 
			int maxCount, Histogram<RangeElement> histogram, int currentBucket) {
		
		if (currentBucket == histogram.size() - 1 || maxCount == 0) {
			histogram.setCount(prv.range().get(currentBucket), maxCount);
			allHistograms.add(new Histogram<RangeElement>(histogram));
			return;
		}
		int count = maxCount;
		while (count >= 0) {
			histogram.setCount(prv.range().get(currentBucket), count);
			generateHistograms(allHistograms, maxCount - count, histogram, 
					currentBucket + 1);
			count--;
		}
	}
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/
	
	/**
	 * Returns a counting formula.
	 * 
	 * @param boundVariable The logical variable bound to this counting
	 * formula (that is, the variable being counted)
	 * @param constraints A set of constraints involving the bound logical
	 * variable
	 * @param prv The parameterized random variable associated with this 
	 * counting formula.
	 * @return A counting formula
	 * 
	 * @throws IllegalArgumentException If the set of constraints contains
	 * a constraint not involving the bound logical variable <b>or</b> if the 
	 * specified logical variable is not a parameter of the specified 
	 * parameterized random variable.
	 */
	public static CountingFormula getInstance(LogicalVariable bound, Prv prv, 
			Set<Constraint> constraints) throws IllegalArgumentException {
		
		return new CountingFormula(bound, constraints, prv);
	}
	
	
	/**
	 * Returns a counting formula without constraints.
	 * 
	 * @param boundVariable The logical variable bound to this counting
	 * formula (that is, the variable being counted)
	 * @param prv The parameterized random variable associated with this 
	 * counting formula.
	 * @return A counting formula
	 * 
	 * @throws IllegalArgumentException If the 
	 * specified logical variable is not a parameter of the specified 
	 * parameterized random variable.
	 */
	public static CountingFormula getInstance(LogicalVariable bound, Prv prv) 
			throws IllegalArgumentException {
		
		Set<Constraint> constraints = new HashSet<Constraint>(0);
		return new CountingFormula(bound, constraints, prv);
	}
	
	
	/**
	 * Returns a counting formula with one constraint.
	 * 
	 * @param boundVariable The logical variable bound to this counting
	 * formula (that is, the variable being counted)
	 * @param prv The parameterized random variable associated with this 
	 * counting formula.
	 * @param constraint A constraint involving the bound logical
	 * variable
	 * @return A counting formula
	 * 
	 * @throws IllegalArgumentException If the 
	 * specified logical variable is not a parameter of the specified 
	 * parameterized random variable <b>or</b> if the constraint does not
	 * involve the bound logical variable
	 */
	public static CountingFormula getInstance(LogicalVariable bound, Prv prv,
			Constraint constraint) throws IllegalArgumentException {
		
		Set<Constraint> constraints = new HashSet<Constraint>(1);
		constraints.add(constraint);
		return new CountingFormula(bound, constraints, prv);
	}
	
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/
	
	@Override
	public Set<Constraint> constraints() {
		return new HashSet<Constraint>(constraints);
	}
	

	/**
	 * Returns the name of the PRV associated with this counting formula.
	 */
	@Override
	public String name() {
		return prv.name();
	}
	

	@Override
	public List<LogicalVariable> parameters() {
		List<LogicalVariable> param = new ArrayList<LogicalVariable>(prv.parameters());
		param.remove(bound);
		return param;
	}
	
	
	@Override
	public List<Term> terms() {
		List<Term> terms = new ArrayList<Term>(prv.terms());
		terms.remove(bound);
		return terms;
	}

	
	@Override
	public LogicalVariable boundVariable() {
		return StdLogicalVariable.getInstance(bound);
	}
	
	
	@Override
	public int groundSetSize(Set<Constraint> constraints) {
		int size = 1;
		for (LogicalVariable v : parameters()) {
			size = size * v.individualsSatisfying(constraints).size();
		}
		return size;
	}
	

	@Override
	public List<RangeElement> range() {
		return new ArrayList<RangeElement>(range);
	}
	
	
	/**
	 * Returns the size of the range of the PRV associated with this 
	 * counting formula.
	 * 
	 * @return the size of the range of the PRV associated with this 
	 * counting formula.
	 */
	public int prvRangeSize() {
		return prv.range().size();
	}

	
	@Override
	public boolean contains(Term t) {
		return parameters().contains(t);
	}

	
	/**
	 * Returns the count of a value from range(f), given the histogram from the
	 * range of this counting formula.
	 * 
	 * @param hIndex The index of the histogram of this counting formula
	 * @param e The index of the bucket in the histogram.
	 * @return The count of the specified value from range(f) for the
	 * specified element from this counting formula.
	 */
	@SuppressWarnings("unchecked")
	public int getCount(int hIndex, RangeElement e) {
		/*
		 * I know that the range is composed of Histogram<RangeElement>, so it
		 * is safe to cast.
		 */
		return ((Histogram<RangeElement>) range.get(hIndex)).getCount(e);
	}
	
	
	/**
	 * Returns <code>true</code> if the counting formula can be converted to a 
	 * standard parameterized random variable, <code>false</code> otherwise.
	 * <p>
	 * A counting formula can be converted to standard parameterized random
	 * variable when the set of constraints is big enough to restrict the
	 * bound logical variable to a single individual.
	 * </p>
	 * 
	 * @return <code>true</code> if the counting formula can be converted to a 
	 * standard parameterized random variable, <code>false</code> otherwise.
	 */
	public boolean isStdPrv() {
		return (bound.individualsSatisfying(constraints).size() == 1);
	}

	
	/**
	 * Returns the value of the multinomial defined by the values in buckets 
	 * from specified histogram.
	 */
	@Override
	public BigDecimal getSumOutCorrection(RangeElement e) {
		@SuppressWarnings("unchecked")
		Histogram<RangeElement> h = (Histogram<RangeElement>) e;
		return new BigDecimal(MathUtils.multinomial(h.toMultinomial()));
	}
	
	
	/* ************************************************************************
	 *    Setters
	 * ************************************************************************/

	/**
	 * Adds a constraint to this counting formula. This method returns a new
	 * instance of the counting formula.
	 * 
	 * @param constraint The constraint to be added to the counting formula.
	 * @return A new counting formula equal to this one with the addition of 
	 * the constraint specified.
	 */
	public CountingFormula add(Constraint constraint) {
		HashSet<Constraint> constraints = 
				new HashSet<Constraint>(this.constraints);
		constraints.add(constraint);
		return CountingFormula.getInstance(bound, prv, constraints);
	}
	
	
	@Override
	public Prv apply(Substitution s) {
		Prv substituted = StdPrv.getInstance(prv);
		Set<Constraint> constraints = new HashSet<Constraint>(this.constraints);
		LogicalVariable boundLv = StdLogicalVariable.getInstance(bound);
		
		for (Iterator<LogicalVariable> it = s.getSubstitutedIterator(); it.hasNext(); ) {
			LogicalVariable toReplace = it.next();
			Term replacement = s.getReplacement(toReplace);
			
			// Cannot substitute bound logical variable with constant
			if ((!boundLv.equals(toReplace)) || replacement.isVariable()) {
				Substitution sub = Substitution.getInstance(Binding.getInstance(toReplace, replacement));
				substituted = substituted.apply(sub);
				constraints = apply(sub, constraints);
				if (boundLv.equals(toReplace)) {
					boundLv = StdLogicalVariable.getInstance((LogicalVariable) replacement);
				}
			}
		}
		return CountingFormula.getInstance(boundLv, (StdPrv) substituted, constraints);
	}


	/**
	 * Returns the result of applying a substitution to the parameterized
	 * random variable of this counting formula.
	 * 
	 * @param s The substitution to be made.
	 * @return The parameterized random variable associated with this 
	 * counting formula with the specified substitution applied.
	 */
	public Prv applyToPrv(Substitution s) {
		return prv.apply(s);
	}
	
	
	/**
	 * Returns the set of constraints that result from applying the
	 * specified substitution to the specified set of constraints.
	 * @param s
	 * @param constraints
	 * @return
	 */
	private Set<Constraint> apply(Substitution s, Set<Constraint> constraints) {
		Set<Constraint> substituted = new HashSet<Constraint>(constraints.size());
		for (Constraint c : constraints) {
			try {
				Constraint newConstraint = c.apply(s);
				substituted.add(newConstraint);
			} catch (IllegalArgumentException e) {
				// Illegal constraint does not get added
			}
		}
		return substituted;
	}
	
	
	/**
	 * Adds the specified amount to the bucket of the specified histogram.
	 * <p>
	 * This method does not modify this counting formula, all operations are
	 * made on copies of elements from this counting formula.
	 * </p>
	 * 
	 * @param hIndex The index of the histogram in the range of this counting 
	 * formula
	 * @param e The bucket where addition will be made
	 * @param n The amount to add
	 * @return The histogram with the specified amount added to the specified
	 * bucket.
	 */
	public RangeElement increaseCount(int hIndex, RangeElement e, int n) {
		Histogram<RangeElement> hist = new Histogram<RangeElement>(range.get(hIndex));
		hist.addCount(e, n);
		return hist;
	}
	
	
	@Override
	public Prv rename(String name) {
		throw new UnsupportedOperationException("Not implemented!");
	}
	

	/**
	 * Converts this counting formula to a standard parameterized random 
	 * variable when its bound logical variable is constrained to a single 
	 * individual.
	 * <p>
	 * If conversion is not possible, returns this counting formula.
	 * </p>
	 * 
	 * @return This counting formula converted to a standard parameterized 
	 * random variable.
	 */
	public Prv toStdPrv() {
		Population constrainedIndividuals = bound.individualsSatisfying(constraints);
		Prv result;
		if (constrainedIndividuals.size() != 1) {
			result = this;
		} else {
			Constant loneIndividual = constrainedIndividuals.iterator().next();
			Binding b = Binding.getInstance(bound, loneIndividual);
			Substitution s = Substitution.getInstance(b);
			result = prv.apply(s);
		}
		return result;
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	@Override
	public String toString() {
		return "#." 
		+ bound.toString() 
		+ ":"
		+ constraints.toString()
		+ " [ " 
		+ prv.toString()
		+ " ]"; 
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bound == null) ? 0 : bound.hashCode());
		result = prime * result
				+ ((constraints == null) ? 0 : constraints.hashCode());
		result = prime * result + ((prv == null) ? 0 : prv.hashCode());
		result = prime * result + ((range == null) ? 0 : range.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CountingFormula)) {
			return false;
		}
		CountingFormula other = (CountingFormula) obj;
		if (bound == null) {
			if (other.bound != null) {
				return false;
			}
		} else if (!bound.equals(other.bound)) {
			return false;
		}
		if (constraints == null) {
			if (other.constraints != null) {
				return false;
			}
		} else if (!constraints.equals(other.constraints)) {
			return false;
		}
		if (prv == null) {
			if (other.prv != null) {
				return false;
			}
		} else if (!prv.equals(other.prv)) {
			return false;
		}
		if (range == null) {
			if (other.range != null) {
				return false;
			}
		} else if (!range.equals(other.range)) {
			return false;
		}
		return true;
	}
}
