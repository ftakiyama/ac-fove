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
	
	// I don't know if this method is correct/good
	public void applySubstitution(Substitution s) {
		// substitution is a class?
		// can I change the vector of terms directly?
		Set<LogicalVariable> toBeReplaced = s.getLogicalVariables();
		for (LogicalVariable v : toBeReplaced) {
			int termIndex = parameters.indexOf(v);
			parameters.set(termIndex, s.getReplacement(v));
		}
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
	 * @return An Instance of this Parameterized Random Variable.
	 */
	public static ParameterizedRandomVariable getInstance(Substitution s) {
		return null;
	}
	
	/**
	private ParameterizedRandomVariable(PredicateSymbol name, Vector<Term> parameter) {
		return null;
	}
	*/
}
