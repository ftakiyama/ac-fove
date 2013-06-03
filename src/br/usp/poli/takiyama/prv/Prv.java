package br.usp.poli.takiyama.prv;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;

/**
 * PRV stands for Parameterized Random Variable, a structure that represents 
 * sets of random variables.
 * 
 * @author Felipe Takiyama
 *
 */
public interface Prv extends Replaceable<Prv> {

	/**
	 * Returns the set of constraints associated with this PRV. If there are
	 * no constraints associated with this PRV, returns an empty set.
	 * 
	 * @return the set of constraints associated with this PRV.
	 */
	public Set<Constraint> constraints();
	
	
	/**
	 * Returns the name of this PRV (the string representation of the functor)
	 * 
	 * @return the name of this PRV 
	 */
	public String name();
	
	
	/**
	 * Returns a list containing the logical variables that appear in this
	 * PRV. The order of variables is the same as from when the PRV was 
	 * created.
	 * 
	 * @return A list of the logical variables of this PRV.
	 */
	public List<LogicalVariable> parameters();
	
	
	/**
	 * Returns a list containing the terms that appear in this
	 * PRV. The order of terms is the same as from when the PRV was 
	 * created.
	 * 
	 * @return A list of the terms of this PRV.
	 */
	public List<Term> terms();
	

	/**
	 * Returns the size of the range of this parameterized random variable.
	 * 
	 * @return The size of the range of this parameterized random variable.
	 */
	public List<RangeElement> range();
	
	
	/**
	 * Returns the bound {@link LogicalVariable} to this PRV.
	 * 
	 * @return the bound LogicalVariable to this PRV.
	 */
	public LogicalVariable boundVariable();
	
	
	/**
	 * Returns the number of ground instances satisfying the specified set of
	 * constraints.
	 * 
	 * @param constraints A set of constraints
	 * @return The number of ground instances satisfying the specified set of
	 * constraints.
	 */
	public int groundSetSize(Set<Constraint> constraints);
	
	
	/**
	 * Returns <code>true</code> if the {@link Term} specified is present in 
	 * this parameterized random variable
	 * 
	 * @param t The term to search for.
	 * @return <code>true</code> if this parameterized random variable 
	 * contains the term specified, <code>false</code> otherwise.
	 */
	public boolean contains(Term t);
	
	
	/**
	 * Returns an Instance of this Parameterized Random Variable by applying
	 * a substitution.
	 * <p>
	 * The application of a substitution &theta; ={X1/ti1....,Xl/til} to a 
	 * parameterized random variable f(t1,...,tk), written 
	 * f(t1,...,tk)[&theta;], 
	 * is a parameterized random variable that is the original parameterized 
	 * random variable f(t1,...,tk) with every occurrence of Xj in f(t1,...,tk) 
	 * replaced by the corresponding tij. 
	 * </p>
	 * <p>
	 * The parameterized random variable f(t1,...,tk)[&theta;] is called an 
	 * Instance of f(t1,...,tk) [Kisynski, 2010]. Note that
	 * Instance does not have the same meaning as 'instance' in OOP.
	 * </p>
	 * 
	 * @param s A substitution that will be applied to this Parameterized
	 * Random Variable.
	 * @return An Instance of this Parameterized Random Variable. 
	 */
	public Prv apply(Substitution s);
	
	
	/**
	 * Returns a new Parameterized Random Variable with same range and 
	 * parameters as this one, but with the specified name.
	 * 
	 * @param name The new name of this PRV.
	 * @return This PRV renamed. A new instance is created.
	 */
	public Prv rename(String name);
	
	
	/**
	 * Returns the correction factor used in lifted elimination.
	 *  
	 * @param e The range element in this PRV to be processed.
	 * @return The correction factor used in lifted elimination
	 */
	public BigDecimal getSumOutCorrection(RangeElement e);
	
	
	/**
	 * Returns <code>true</code> if this PRV represents the same set of random
	 * variables as the specified set.
	 * 
	 * @param s A set of random variables.
	 * @return <code>true</code> if this PRV represents the same set of random
	 * variables as the specified set, <code>false</code> otherwise.
	 */
	public boolean isEquivalentTo(RandomVariableSet s);
	
	
	public Prv getCanonicalForm();
	
	
	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();
	
	@Override
	public String toString();
}
