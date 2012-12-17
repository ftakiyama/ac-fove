package br.usp.poli.takiyama.prv;

import java.util.HashSet;
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
 * @author ftakiyama
 *
 */
public class CountingFormula extends ParameterizedRandomVariable {
	
	private LogicalVariable boundLogicalVariable;
	private HashSet<Constraint> constraints;
	private ParameterizedRandomVariable prv; // do i need this field? > yes, in case i need prv separately
	
	private int rangeSize = -1;
	
	/**
	 * Private constructor. To create CountingFormulas, use the factory
	 * getInstance().
	 * @param boundVariable
	 * @param constraints
	 * @param prv
	 */
	private CountingFormula(LogicalVariable boundVariable, Set<Constraint> constraints, ParameterizedRandomVariable prv) {
		super(prv); 
		this.boundLogicalVariable = boundVariable;
		this.constraints = new HashSet<Constraint>(constraints);
		this.prv = prv;
	}
	
	/**
	 * Static factory of counting formulas.
	 * @param boundVariable
	 * @param constraints
	 * @param prv
	 * @return A counting formula with the specified parameters.
	 */
	public static CountingFormula getInstance(LogicalVariable boundVariable, Set<Constraint> constraints, ParameterizedRandomVariable prv) {
		return new CountingFormula(boundVariable, constraints, prv);
	}
	
	/**
	 * Returns null.
	 * @param functor
	 * @param range
	 * @param parameters
	 * @return
	 */
	public static ParameterizedRandomVariable getInstance(
			String functor, 
			List<String> range, 
			List<Term> parameters) {
		return null;
	}
	
	/**
	 * Returns null.
	 * @param functor
	 * @param parameters
	 * @return
	 */
	static ParameterizedRandomVariable getInstance(
			PredicateSymbol functor, 
			List<Term> parameters) {
		return null;
	}
	
	public ParameterizedRandomVariable applySubstitution(Substitution s) {
		return null;
	}
	
	/**
	 * Returns the size of the range of this counting formula.
	 * This method works only for counting formulas whose parameterized 
	 * random variable has boolean range. An exception is throw if the condition
	 * is not met.
	 * 
	 * @throws IllegalStateException When the PRV is not boolean.
	 */
	public int getRangeSize() throws IllegalStateException {
		if (this.prv.getRangeSize() != 2) {
			throw new IllegalStateException("The counting formula " + 
					this.toString() + 
					" has a non boolean PRV. The range size of the PRV is " + 
					this.prv.getRangeSize());
		}
		
		if (rangeSize == -1) {
			rangeSize = this.boundLogicalVariable.getPopulation().size() + 1;
			for (Constraint constraint : this.constraints) {
				if (constraint.getSecondTerm() instanceof LogicalVariable
						&& ((LogicalVariable) constraint.getSecondTerm()).getPopulation().equals(boundLogicalVariable.getPopulation())) {
					rangeSize--;
				} else if (constraint.getSecondTerm() instanceof Constant
						&& boundLogicalVariable.getPopulation().contains((Constant) constraint.getSecondTerm())) {
					rangeSize--;
				}
			}
		}
		return rangeSize;
	}
	
	public String getElementFromRange(int index) {
		return "(#." 
			+ this.prv.getElementFromRange(0) 
			+ " = " 
			+ (this.getRangeSize() - index) 
			+ ", #." 
			+ this.prv.getElementFromRange(1) 
			+ " = " 
			+ index 
			+ ")";
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
	 * Adds a constraint to this counting formula. This method returns a new
	 * instance of the counting formula.
	 * @param constraint The constraint to be added to the counting formula.
	 * @return A new counting formula equal to this one with the addition of 
	 * the constraint provided.
	 */
	public CountingFormula addConstraint(Constraint constraint) {
		HashSet<Constraint> constraints = new HashSet<Constraint>(this.constraints);
		constraints.add(constraint);
		return new CountingFormula(this.boundLogicalVariable, constraints, this.prv);
	}
	
	/**
	 * Returns the set of constraints of this counting formula.
	 * @return The set of constraints of this counting formula.
	 */
	public Set<Constraint> getConstraints() {
		return this.constraints;
	}
	
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
	    return ((this.boundLogicalVariable == null) ? (targetObject.boundLogicalVariable == null) : this.boundLogicalVariable.equals(targetObject.boundLogicalVariable))
	    		&& ((this.constraints == null) ? (targetObject.constraints == null) : this.constraints.equals(targetObject.constraints))
	    		&& ((this.prv == null) ? (targetObject.prv == null) : this.prv.equals(targetObject.prv));
	}
	
	@Override
	public int hashCode() { // Algorithm extracted from Bloch,J. Effective Java
		int result = 17;
		result = 31 + result + boundLogicalVariable.hashCode();
		result = 31 + result + this.constraints.hashCode();
		result = 31 + result + this.prv.hashCode();
		return result;
	}
}
