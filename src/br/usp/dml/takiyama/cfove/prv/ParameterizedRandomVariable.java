package br.usp.dml.takiyama.cfove.prv;

import java.util.Iterator;
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
	
	/**
	 * Returns an iterator over the set of logical variables that appear in
	 * this parameterized random variable (PRV).
	 * @return An iterator over the set of logical variables of this PRV. 
	 */
	public Iterator<LogicalVariable> getParameters() {
		Vector<LogicalVariable> parameters = new Vector<LogicalVariable>();
		for (Term t : this.parameters) {
			if (t.isLogicalVariable()) {
				parameters.add((LogicalVariable) t);
			}
		}
		return parameters.iterator();
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
		//TODO: Implement this.
		// I think I'll need to create a Cartesian product of sets...
		// I see... out of memory space!
		return null;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object.getClass().getName().endsWith("ParameterizedRandomVariable")) {
			ParameterizedRandomVariable compared = (ParameterizedRandomVariable)object;
			boolean sameFunctor = this.functor.getName().equals(compared.functor.getName());
			boolean sameParameters = this.parameters.equals(compared.parameters);
			
			// I am here. Must test if the vectors of parameters are the same.
			for (int i = 0; i < this.parameters.size(); i++) {
				System.out.println(this.parameters.get(i).equals(compared.parameters.get(i)));
			}	
			
			if (sameFunctor) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
