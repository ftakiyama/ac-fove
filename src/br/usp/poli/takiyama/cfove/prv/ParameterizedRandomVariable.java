package br.usp.poli.takiyama.cfove.prv;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import br.usp.poli.takiyama.cfove.Constraint;
import br.usp.poli.takiyama.ve.RandomVariable;

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
			newInstance.parameters.set(termIndex, s.getReplacement((LogicalVariable)v));
		}
		
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
			List<Constraint> constraints)  
			throws 	IllegalStateException, 
					IllegalArgumentException {
		
		// TODO: satisfy constraints 
		
		return getGroundInstance(index);
	}
	
	/**
	 * Returns the set of ground instances of the parameterized random variable 
	 * that satisfy a set of {@link Constraint}s.
	 * @param constraints A set of constraints that the ground instances 
	 * must follow
	 * @return The set of ground instances that satisfy the constraints in
	 * <code>constraints</code>
	 */
	public List<RandomVariable> getGroundInstancesSatisfying(List<Constraint> constraints) {
		ArrayList<RandomVariable> groundInstances = new ArrayList<RandomVariable>();
		for (int i = 0; i < getNumberOfGroundInstances(); i++) {
			groundInstances.add(getGroundInstanceSatisfyingConstraints(i, constraints));
		}
		return groundInstances;
	}
	
	/**
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
