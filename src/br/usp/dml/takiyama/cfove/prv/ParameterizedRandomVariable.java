package br.usp.dml.takiyama.cfove.prv;

import java.util.Set;
import java.util.Vector;


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
public class ParameterizedRandomVariable implements PrvInterface {
	private Vector<Term> parameters;
	private PredicateSymbol functor;
	
	// this class should also represent a random variable
	
	
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
			// Well, v is not recognized as being a member of 'parameters'
			// Maybe because it is a Term, which is not a final class
			int termIndex = newInstance.parameters.indexOf(v);
			newInstance.parameters.set(termIndex, s.getReplacement((LogicalVariable)v));
		}
		
		return newInstance;
	}
	
	/**
	 * Constructor.
	 * @param functor A {@link PredicateSymbol}
	 * @param parameters A {@link Vector} of {@link Term}s.
	 */
	public ParameterizedRandomVariable(PredicateSymbol functor, Vector<Term> parameters) {
		this.functor = functor;
		this.parameters = new Vector<Term>();
		for (Term term : parameters) {
			this.parameters.add(term);
		}
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
}
