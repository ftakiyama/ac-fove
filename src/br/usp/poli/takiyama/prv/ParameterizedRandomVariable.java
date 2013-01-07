package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.RandomVariable;

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
	private final ArrayList<Term> parameters; // <- I must ASSURE that this is ordered in a predictable way
	private final PredicateSymbol functor;
	
	// this class should be able to create random variables
	// ground instance = random variable
	
	// TODO Erase deprecated methods
	
	/*
	 * What should this class do?
	 * - get parameters
	 * - get ground instances
	 */
	
	/**
	 * Private constructor.
	 * @param functor A {@link PredicateSymbol}
	 * @param parameters A {@link Vector} of {@link Term}s.
	 */
	private ParameterizedRandomVariable(PredicateSymbol functor, List<Term> parameters) {
		this.functor = new PredicateSymbol(functor);
		this.parameters = new ArrayList<Term>(parameters);
	}
	
	/**
	 * Constructor. Created so I can use it in CountingFormula.
	 * @param prv A parameterized random variable
	 */
	protected ParameterizedRandomVariable(ParameterizedRandomVariable prv) {
		this.functor = prv.functor;
		this.parameters = prv.parameters;
	}
	
	/**
	 * Static factory of parameterized random variables (PRVs).
	 * @param functor The name of the PRV
	 * @param range The range of the PRV
	 * @param parameters The parameters of the PRV
	 * @return An instance of PRV.
	 */
	public static ParameterizedRandomVariable getInstance(
			String functor, 
			List<String> range, 
			List<Term> parameters) {
		return new ParameterizedRandomVariable(
				new PredicateSymbol(functor, range.toArray(new String[range.size()])), 
				parameters);
	}
	
	/**
	 * Static factory of parameterized random variables (PRVs).
	 * Visible only for classes within the package.
	 * I don't know if this will be useful.
	 * @param functor
	 * @param parameters
	 * @return
	 */
	static ParameterizedRandomVariable getInstance(
			PredicateSymbol functor, 
			List<Term> parameters) {
		return new ParameterizedRandomVariable(functor, parameters);
	}
	
	/**
	 * Returns an empty parameterized random variable. It has a nameless 
	 * functor and an empty set of parameters.
	 * @return 
	 */
	public static ParameterizedRandomVariable getEmptyInstance() {
		return new ParameterizedRandomVariable(
				new PredicateSymbol(""), 
				new ArrayList<Term>());
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
	public ParameterizedRandomVariable applySubstitution(Substitution s) {
		ParameterizedRandomVariable newInstance = 
			new ParameterizedRandomVariable(this.functor, this.parameters);
		Set<LogicalVariable> toBeReplaced = s.getLogicalVariables();
		
		for (Term v : toBeReplaced) {
			int termIndex = newInstance.parameters.indexOf(v);
			if (termIndex != -1) {
				newInstance.parameters.set(termIndex, s.getReplacement((LogicalVariable)v));
			}
		}
		
		return newInstance;
	}
	
	/**
	 * Convenience method to apply one single substitution to this parameterized
	 * random variable.
	 * @param s A binding that will be applied to this Parameterized Random
	 * Variable.
	 * @return An instance of this Parameterized Random Variable. Note that
	 * instance does not have the same meaning as 'instance' in Java.
	 * @see #applySubstitution(Substitution)
	 */
	public ParameterizedRandomVariable applyOneSubstitution(Binding s) {
		if (this.parameters.indexOf(s.getFirstTerm()) == -1) {
			return this;
		}
		ParameterizedRandomVariable newInstance =
			new ParameterizedRandomVariable(this.functor, this.parameters);
		newInstance.parameters.set(newInstance.parameters.indexOf(s.getFirstTerm()), s.getSecondTerm());
		return newInstance;
	}
	
	/**
	 * Returns a set over the logical variables that appear in
	 * this parameterized random variable (PRV).
	 * @return A set over the logical variables of this PRV. 
	 */
	public Set<LogicalVariable> getParameters() {
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
	
	public String getName() {
		return this.functor.getName();
	}
	
	/**
	 * This class represent equations of the form ti = tj, where ti and tj are
	 * parameters of a parameterized random variable.
	 * <br>
	 * This class is used in the algorithm to find the MGU between two 
	 * parameterized random variables. 
	 * <br>
	 * This class is mutable since it is intended to change of state.
	 * 
	 * @author ftakiyama
	 *
	 */
    private class Equation {
		private Term firstTerm;
		private Term secondTerm;
		
		/**
		 * Constructor. Creates a new Equation.
		 * @param t1 The first Term
		 * @param t2 The second Term
		 */
		public Equation(Term t1, Term t2) {
			this.firstTerm = t1;
			this.secondTerm = t2;
		}
		
		/**
		 * Applies a substitution to this Equation. Given an equation ti = tj
		 * and a simple substitution {tk/t}, this method replaces all the terms
		 * equal to tk with t. For instance, if ti = tk, then the equation
		 * becomes t = tj.
		 * @param substitution A simple substitution (Binding) to be applied
		 * to this equation. 
		 */
		public void applySubstitution(Binding substitution) {
			if (firstTerm.equals(substitution.getFirstTerm())) {
				this.firstTerm = substitution.getSecondTerm();
			} else if (secondTerm.equals(substitution.getFirstTerm())) {
				this.secondTerm = substitution.getSecondTerm();
			} 
		}
		
		/**
		 * Returns true if the first term is a logical variable, false otherwise.
		 * @return true if the first term is a logical variable, false otherwise.
		 */
		public boolean firstTermIsLogicalVariable() {
			return firstTerm.isLogicalVariable();
		}
		
		/**
		 * Returns true if the second term is a logical variable, false otherwise.
		 * @return true if the second term is a logical variable, false otherwise.
		 */
		public boolean secondTermIsLogicalVariable() {
			return secondTerm.isLogicalVariable();
		}
		
		/**
		 * Returns true if the first term is equal to the second term, false
		 * otherwise.
		 * @return True if the first term is equal to the second term, false
		 * otherwise.
		 */
		public boolean haveIdenticalTerms() {
			return firstTerm.equals(secondTerm);
		}
		
		/**
		 * Converts this Equation to a Binding.
		 * That is, if this equation is ti = tj, then returns the correspondinf
		 * Binding ti/tj.
		 * @return The Binding that corresponds to this Equation
		 * @throws IllegalArgumentException if the first term is not a logical
		 * variable, thus making it impossible to convert.
		 */
		public Binding toBinding() throws IllegalArgumentException {
			if (firstTermIsLogicalVariable()) {
				return Binding.create((LogicalVariable) firstTerm, secondTerm);
			} else {
				throw new IllegalArgumentException("The Equation " 
						+ this.toString()
						+ " could not be converted to a Binding object. The" 
						+ " first term must be a LogicalVariable.");
			}
		}
		
		@Override
		public String toString() {
			return firstTerm.toString() + " = " + secondTerm.toString();
		}
		
	}
	
	/**
	 * Returns the Most General Unifier (MGU) between this parameterized
	 * random variable and the specified random variable.
	 * Both variables must have the same name and have the same number of
	 * parameters. In case these conditions are not met, this method throws
	 * an IllegalArgumentException.
	 * <br>
	 * If the PRVs do not unify, throws an IllegalArgumentException.
	 * <br>
	 * The algorithm used was adapted by Kisynski (2010) from Sterling and
	 * Shapiro (1994).
	 * 
	 * @param other The parameterized random variable from which the MGU will
	 * be obtained.
	 * @return The MGU as a Substitution.
	 * @throws IllegalArgumentException If the PRVs do not have the same name
	 * or do not have the same number of parameters or do not unify.
	 */
	public Substitution getMgu(ParameterizedRandomVariable other) throws IllegalArgumentException {
		if (this.parameters.size() != other.parameters.size()
				|| this.getName() != other.getName()) {
			throw new IllegalArgumentException();
		}
		
		HashSet<Equation> temporaryMgu = new HashSet<Equation>();
		Stack<Equation> buffer = new Stack<Equation>();
		for (int i = 0; i < this.parameters.size(); i++) {
			buffer.push(new Equation(this.parameters.get(i), other.parameters.get(i)));
		}
		ArrayList<Binding> mgu = new ArrayList<Binding>();
		
		while (!buffer.isEmpty()) {
			Equation equation = buffer.pop();
			if (equation.haveIdenticalTerms()) {
				// do nothing
			} else if (equation.firstTermIsLogicalVariable()) {
				Binding substitution = Binding
					.create((LogicalVariable) equation.firstTerm, 
							equation.secondTerm);
				for (Equation e : buffer) {
					e.applySubstitution(substitution);
				}
				for (Equation s : temporaryMgu) {
					s.applySubstitution(substitution);
				}
				// add {ti/tj} to mgu
				//temporaryMgu.add(equation);
				mgu.add(substitution);
			} else if (equation.secondTermIsLogicalVariable()) {
				Binding substitution = Binding
					.create((LogicalVariable) equation.secondTerm,
							equation.firstTerm);
				for (Equation e : buffer) {
					e.applySubstitution(substitution);
				}
				for (Equation s : temporaryMgu) {
					s.applySubstitution(substitution);
				}
				// add {tj/ti} to mgu
				mgu.add(substitution);
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		return Substitution.create(new ArrayList<Binding>(mgu));
		
	}
	
	/**
	 * Returns true if the Term specified is present in this parameterized
	 * random variable
	 * @param t The term to search for.
	 * @return True if this parameterized random variable contains the 
	 * term specified.
	 */
	public boolean contains(Term t) {
		return this.parameters.contains(t);
	}
	
	/**
	 * @deprecated
	 * Returns a random variables that this parameterized random variable 
	 * represents. 
	 * <br> 
	 * A parameterized random variable f(t1,...,tk) represents a set of random 
	 * variables, one random variable for each ground substitution to all 
	 * logical variables in param(f(t1,...,tk)) [Kisynski, 2010].
	 * <br>
	 * Each random variable is implicitly indexed according to the ordering
	 * of logical variables and to the ordering of the population of each
	 * logical variable. 
	 * 
	 * @param index The index of the random variable to return.
	 * 
	 * @return The random variable indexed by the specified <code>index</code>.
	 * 
	 * @throws IllegalStateException If there are constants in this 
	 * parameterized random variable.
	 * @throws IllegalArgumentException If there is no ground instance indexed 
	 * by <code>index</code>. 
	 */
	public RandomVariable getGroundInstance(int index) 
			throws 	IllegalStateException, 
					IllegalArgumentException {
		ArrayList<Integer> termsIndexes = new ArrayList<Integer>();
		
		int copyIndex = index; // this is not nice
		
		if (index < 0) {
			throw new IllegalArgumentException("Index " + index + " is not " +
					"valid. It must be a non-negative value.");
		}
		
		// Get the individual for each logical variable
		// The individual is represented by its index in the population	
		for (Term t : this.parameters) {
			if (t instanceof Constant) {
				throw new IllegalStateException("For now, I can only return ground " +
						"instances of parameterized random variables whose " +
						"terms are all logical variables. I found " +
						t.getValue() + ", which is a constant.");
			}
			termsIndexes.add(index % ((LogicalVariable) t).getPopulation().size());
			index = index / ((LogicalVariable) t).getPopulation().size();
		}
		
		// Checks if index is within the allowed bounds - ugly
//		int maxIndex = 1;
//		for (Term t : this.parameters) {
//			maxIndex *= ((LogicalVariable) t).getPopulation().size();
//		}
		if (copyIndex >= getNumberOfGroundInstances()) {
			throw new IllegalArgumentException("Index " + copyIndex + " is not "
					+ "valid. Maximum valid value is " 
					+ (getNumberOfGroundInstances() - 1) + ".");
		}
		
		return createRandomVariableFromConstantIndexes(termsIndexes);	
	}
	
	/**
	 * @deprecated
	 * Creates a random variable with the specified indexes. I think I should
	 * explain this better...
	 * @param termsIndexes The indexes that define the random variable.
	 * @return A random variable specified by the indexes.
	 */
	private RandomVariable createRandomVariableFromConstantIndexes(List<Integer> termsIndexes) {
		//System.out.println(termsIndexes);
		StringBuilder name = new StringBuilder(this.functor.getName() + " ( ");
		for (int index = 0; index < termsIndexes.size(); index++) { // long way down to convert indexes to constant values...
			name
				.append(((LogicalVariable) parameters.get(index))
					.getPopulation()
						.getIndividual(termsIndexes
							.get(index))
								.getValue())
				.append(" ");
		}
		name.append(")");
		return RandomVariable.createRandomVariable(
				name.toString(), 
				functor.getRange()); 
	}
	
	
	/**
	 * @deprecated
	 * Returns a random variable that this parameterized random variable 
	 * represents satisfying a set of constraints.
	 * <br>
	 * Each random variable is implicitly indexed according to the ordering
	 * of logical variables and to the ordering of the population of each
	 * logical variable. 
	 * 
	 * @param index The index of the random variable to return.
	 * @param constraints A set of constraints that the ground instance must
	 * follow
	 * 
	 * @return The random variable indexed by the specified <code>index</code>.
	 * 
	 * @throws IllegalStateException If there are constants in this 
	 * parameterized random variable.
	 * @throws IllegalArgumentException If there is no ground instance indexed 
	 * by <code>index</code>. 
	 */
	private RandomVariable getGroundInstanceSatisfyingConstraints(
			int index, 
			Set<Constraint> constraints)  
			throws 	IllegalStateException, 
					IllegalArgumentException {
		
		// TODO: satisfy constraints 
		
		return getGroundInstance(index);
	}
	
	/**
	 * @deprecated
	 * Returns the set of ground instances of the parameterized random variable 
	 * that satisfy a set of {@link Constraint}s.
	 * @param constraints A set of constraints that the ground instances 
	 * must follow
	 * @return The set of ground instances that satisfy the constraints in
	 * <code>constraints</code>
	 */
	public Set<RandomVariable> getGroundInstancesSatisfying(Set<Constraint> constraints) {
		HashSet<RandomVariable> groundInstances = new HashSet<RandomVariable>();
		for (int i = 0; i < getNumberOfGroundInstances(); i++) {
			groundInstances.add(getGroundInstanceSatisfyingConstraints(i, constraints));
		}
		return groundInstances;
	}
	
	/**
	 * @deprecated
	 * Returns the number of ground instances of the parameterized random 
	 * variable.
	 * TODO Should I make it an attribute? It is pretty inefficient implementation.
	 * TODO The implementation do not take into account mixed PRVs.
	 * @return The number of ground instances of the parameterized random 
	 * variable.
	 */
	public int getNumberOfGroundInstances() {
		int maxIndex = 1;
		for (Term t : this.parameters) {
			maxIndex *= ((LogicalVariable) t).getPopulation().size();
		}
		return maxIndex;
	}
	
	/**
	 * Returns the size of the range of this parameterized random variable.
	 * @return The size of the range of this parameterized random variable.
	 */
	public int getRangeSize() {
		return this.functor.getRange().size();
	}
	
	/**
	 * Returns an element from the range given its index.
	 * @param index The index of the element to search for.
	 * @return The element in the range at the position specified.
	 */
	public String getElementFromRange(int index) {
		return this.functor.getRange().get(index);
	}
	
	/**
	 * @deprecated
	 * <b>WARNING</b>: I have created this method in order to use factors with 
	 * parameterized random variables. I mask a PRV into a RV using the
	 * functor. A more appropriate implementation would extend the class
	 * Factor to accommodate PRVs instead of RVs. Developers are lazy
	 * sometimes... 
	 * <br>
	 * <br>
	 * This method returns an "abstract" representation of this parameterized
	 * random variable in the form of a random variable. In fact, they represent
	 * different things, but there is an analogy:
	 * <li>"Functor" of PRV is analogous to the "name" of RV
	 * <li>"Range" of PRV is analogous to the "domain" of RV
	 * <br>
	 * <br>
	 * I use this analogy to camouflage PRVs from the point of view of Factors.
	 * This method should be used to create factors for Parfactors and to
	 * sum out a variable from a factor that belongs to a parfactor.
	 * <br>
	 * <b>ONCE AGAIN</b>: correct this ASAP!  
	 * @return A {@link RandomVariable} that "represents" this parameterized
	 * random variable.
	 */
	public RandomVariable getRandomVariableRepresetantion() {
		return RandomVariable.createRandomVariable(this.functor.getName(), this.functor.getRange());
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
