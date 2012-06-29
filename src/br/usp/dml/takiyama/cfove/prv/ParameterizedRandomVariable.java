package br.usp.dml.takiyama.cfove.prv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.common.collect.ImmutableSet;


/**
 * A parameterized random variable is either a logical atom or a term. 
 * That is, it is of the form p(t1,...,tn), where each ti is a logical variable 
 * or a constant and p is a functor. Each functor has a set of values called 
 * the range of the functor [Kisynski, 2010]. The parameterized random variable 
 * is said to be parameterized by the logical variables that appear in it.
 * [Poole, 2010]
 * @author ftakiyama
 *
 */
public class ParameterizedRandomVariable {
	private final ArrayList<Term> parameters;
	private final PredicateSymbol functor;
	
	// this class should be able to create random variables
	
	/*
	 * What should this class do?
	 * - get parameters
	 * - get ground instances
	 */
	
	/**
	 * Constructor.
	 * @param functor A {@link PredicateSymbol}
	 * @param parameters A {@link Vector} of {@link Term}s.
	 */
	public ParameterizedRandomVariable(PredicateSymbol functor, List<Term> parameters) {
		this.functor = new PredicateSymbol(functor);
		this.parameters = new ArrayList<Term>(parameters);
	}
	
	/**
	 * Returns an Instance of this Parameterized Random Variable by applying
	 * a substitution.
	 * The application of a substitution θ ={X1/ti1....,Xl/til} to a 
	 * parameterized random variable f(t1,...,tk), written f(t1,...,tk)[θ], 
	 * is a parameterized random variable that is the original parameterized 
	 * random variable f(t1,...,tk) with every occurrence of Xj in f(t1,...,tk) 
	 * replaced by the corresponding tij. The parameterized random variable 
	 * f(t1,...,tk)[θ] is called an Instance of f(t1,...,tk). [Kisynski, 2010]
	 * 
	 * @param s A substitution that will be applied to this Parameterized
	 * Random Variable.
	 * @return An Instance of this Parameterized Random Variable. Note that
	 * Instance does not have the same meaning as 'instance' in OOP (see
	 * explanation above).
	 */
	public ParameterizedRandomVariable getInstance(Substitution s) {
		ParameterizedRandomVariable newInstance = 
			new ParameterizedRandomVariable(this.functor, this.parameters);
		Set<LogicalVariable> toBeReplaced = s.getLogicalVariables();
		
		for (Term v : toBeReplaced) {
			int termIndex = newInstance.parameters.indexOf(v);
			newInstance.parameters.set(termIndex, s.getReplacement((LogicalVariable)v));
		}
		
		return newInstance;
	}
	
	
	
	/**
	 * Returns an immutable set over the set of logical variables that appear in
	 * this parameterized random variable (PRV).
	 * @return An immutable set over the set of logical variables of this PRV. 
	 */
	public ImmutableSet<LogicalVariable> getParameters() {
		ArrayList<LogicalVariable> parameters = new ArrayList<LogicalVariable>();
		for (Term t : this.parameters) {
			/*
			 * There should be a better way to do that.
			 * But since I know that Term can only be either a Constant or a
			 * LogicalVariable, I believe that this is not a big problem.
			 */
			if (t instanceof LogicalVariable) {
				parameters.add((LogicalVariable) t);
			}
		}
		return ImmutableSet.copyOf(parameters.iterator());
	}
	
	/**
	 * Returns an iterator over the set of random variables that this
	 * parameterized random variable represents.<br> 
	 * A parameterized random variable f(t1,...,tk) represents a set of random 
	 * variables, one random variable for each ground substitution to all 
	 * logical variables in param(f(t1,...,tk)) [Kisynski, 2010].
	 * 
	 * @return
	 */
	public Iterator<ParameterizedRandomVariable> getGroundInstances() {
		// Apply the same strategy used to implement factors in ve.
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(this.functor.getName() + " ( ");
		for (Term term : this.parameters) {
			result.append(term).append(" ");
		}
		result.append(")");
		return result.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof ParameterizedRandomVariable))
	    	return false;
	    // Tests if both have the same attributes
	    ParameterizedRandomVariable targetObject = (ParameterizedRandomVariable) other;
	    return ((this.functor == null) ? (targetObject.functor == null) : this.functor.equals(targetObject.functor))
	    		&& ((this.parameters == null) ? (targetObject.parameters == null) : this.parameters.equals(targetObject.parameters));
	}
	
	@Override
	public int hashCode() {
		return functor.hashCode() + parameters.hashCode();
	}
}
