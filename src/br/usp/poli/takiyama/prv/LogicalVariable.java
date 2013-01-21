package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;

/**
 * A logical variable is a word starting with an upper-case letter or the 
 * underscore. [Poole, 2010]
 * @author ftakiyama
 *
 */
public final class LogicalVariable implements Term {
	
	private final String name;
	private final Population population;
	
	/**
	 * Constructor. Creates a logical variable.
	 * @param name The name of the logical variable. The name must start with
	 * an upper case letter or underscore ("_").
	 * @param individuals The individuals that constitute the population of
	 * this logical variable
	 * @throws IllegalArgumentException If the name requirements are not met.
	 */
	public LogicalVariable(String name, ArrayList<Constant> individuals) throws IllegalArgumentException {
		// The value of a logical variable is its name
		this.name = new String(name);
		
		// Population should be ordered.
		this.population = new Population(individuals);
		
		// Checks if the name of the variable is valid
		if (!name.startsWith("_") && !Character.isUpperCase(name.charAt(0))) {
			throw new IllegalArgumentException("Exception while creating " +
					"Logical Variable: '" + name + "' must start with " +
					"uppercase letter or underscore.");
		}
	}
	
	/**
	 * Constructor. Creates a logical variable.
	 * @param name The name of the logical variable
	 * @param population The population of the logical variable. The new variable
	 * will have a copy of the population specified. 
	 * @throws IllegalArgumentException If the name of the variable does not
	 * start with a upper case letter or underscore.
	 */
	private LogicalVariable(String name, Population population) throws IllegalArgumentException {
		this.name = new String(name);
		this.population = Population.copyOf(population);
		if (!name.startsWith("_") && !Character.isUpperCase(name.charAt(0))) {
			throw new IllegalArgumentException("Exception while creating " +
					"Logical Variable: '" + name + "' must start with " +
					"uppercase letter or underscore.");
		}
	}
	
	/**
	 * Returns the name of this logical variable.
	 * @return The name of this logical variable.
	 */
	public String getValue() {
		return new String(name);
	}
	
	/**
	 * Returns a copy of the population of this logical variable.
	 * @return A copy of the population of this logical variable.
	 */
	public Population getPopulation() {
		return Population.copyOf(population);
	}
	
	/**
	 * Returns all individuals of the population of this logical variable that
	 * satisfy the specified set of constraints of the form X != t.
	 * <br>
	 * t must be a constant.
	 * <br>
	 * Note that, if t is a logical variable, then there is no way of finding
	 * out which value to constrain in the population. 
	 * <br>
	 * This method does not check whether the constraints are consistent, that
	 * is, if the second term is a constant representing an individual from 
	 * this logical variable population.
	 * @param constraints A set of constraints that restricts the individuals
	 * of the population from this logical variable
	 * @return A set containing all individuals satisfying the specified set
	 * of constraints.
	 */
	public Set<Constant> getIndividualsSatisfying(Set<Constraint> constraints) {
		Population population = Population.copyOf(this.population);
		for (Constraint constraint : constraints) {
			if (constraint.getFirstTerm().equals(this) 
					&& constraint.secondTermIsConstant()) {
				population.removeIndividual((Constant) constraint.getSecondTerm());
			}
		}
		return population.toSet();
	}
	
	/**
	 * Returns the size of the population of this logical variable that
	 * satisfies the given set of constraints.
	 * <br>
	 * This method only checks if this logical variable is part of the
	 * constraint. Other validations, such as consistency of individuals and
	 * populations, must be done in the specified set of constraints before
	 * calling this method.
	 * @param constraints A set of consistent constraints.
	 * @return The size of the population of this logical variable that
	 * satisfies the given set of constraints.
	 */
	public int getSizeOfPopulationSatisfying(Set<Constraint> constraints) {
		HashSet<Constraint> consistentConstraints = new HashSet<Constraint> (constraints);
		for (Constraint constraint : consistentConstraints) {
			if (!constraint.contains(this)) {
				consistentConstraints.remove(constraint);
			}
		}
		return (this.population.size() - consistentConstraints.size());
	}
	
	/**
	 * Renames this logical variable. 
	 * A new instance of LogicalVariable will be created.
	 * @param newName The new name
	 */
	LogicalVariable rename(String newName) {
		return new LogicalVariable(newName, this.population);
	}
	
	@Override
	public boolean isLogicalVariable() {
		return true;
	}
	
	@Override
	public boolean isConstant() {
		return false;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof LogicalVariable))
	    	return false;
	    // Tests if both have the same attributes
	    LogicalVariable targetObject = (LogicalVariable) other;
	    return this.name.equals(targetObject.name);	    		
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}