package br.usp.poli.takiyama.prv;

import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;

/**
 * Standard implementation of {@link LogicalVariable}.
 * 
 * @author Felipe Takiyama
 * 
 */
public final class StdLogicalVariable implements LogicalVariable {
	
	private final String name;
	private final Population population;
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	/**
	 * Constructor. Creates a logical variable.
	 * 
	 * @param name The name of the logical variable. The name must start with
	 * an upper case letter or underscore ("_").
	 * @param individuals The individuals that constitute the population of
	 * this logical variable
	 * @throws IllegalArgumentException If the name requirements are not met.
	 */
//	public StdLogicalVariable(String name, List<Constant> individuals) throws IllegalArgumentException {
//		// The value of a logical variable is its name
//		this.name = new String(name);
//		
//		// Population should be ordered.
//		this.population = new Population(individuals);
//		
//		// Checks if the name of the variable is valid
//		if (!name.startsWith("_") && !Character.isUpperCase(name.charAt(0))) {
//			throw new IllegalArgumentException("Exception while creating " +
//					"Logical Variable: '" + name + "' must start with " +
//					"uppercase letter or underscore.");
//		}
//	}
	
	/**
	 * Constructor. Creates a logical variable.
	 * @param name The name of the logical variable
	 * @param population The population of the logical variable. The new variable
	 * will have a copy of the population specified. 
	 * @throws IllegalArgumentException If the name of the variable does not
	 * start with a upper case letter or underscore.
	 */
	private StdLogicalVariable(String name, Population population) throws IllegalArgumentException {
		
		this.name = new String(name);
		this.population = Population.getInstance(population);
		
		if (!name.startsWith("_") && !Character.isUpperCase(name.charAt(0))) {
			throw new IllegalArgumentException("Exception while creating " +
					"Logical Variable: '" + name + "' must start with " +
					"uppercase letter or underscore.");
		}
	}
	
	/**
	 * Constructor. Creates a copy of the specified logical variable.
	 * @param lv The LogicalVariable to be copied.
	 */
	private StdLogicalVariable(LogicalVariable lv) {
		name = lv.value();
		population = lv.population();
	}
		
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/
	
	public static LogicalVariable getInstance() {
		return new StdLogicalVariable("", Population.getInstance());
	}
	
	
	public static LogicalVariable getInstance(LogicalVariable lv) {
		return new StdLogicalVariable(lv);
	}
	
	
	public static LogicalVariable getInstance(String name, Population pop) {
		return new StdLogicalVariable(name, pop);
	}
	
	
	/* ************************************************************************
	 *    Inherited methods
	 * ************************************************************************/

	@Override
	public String value() {
		return name;
	}
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	@Override
	public Population population() {
		return Population.getInstance(population);
	}
	
	
	@Override
	public Population individualsSatisfying(Set<Constraint> constraints) {
		Population pop = Population.getInstance(this.population);
		for (Constant individual : pop) {
			for (Constraint constraint : constraints) {
				Binding bind = Binding.getInstance(this, individual);
				if (constraint.isConsistentWith(bind)) {
					pop.remove(individual);
				}
			}
		}
		return pop;
	}
	
	
	@Override
	public LogicalVariable rename(String newName) {
		return new StdLogicalVariable(newName, this.population);
	}
	
	
//	public int sizeOfPopulationSatisfying(Set<Constraint> constraints) {
//		HashSet<Constraint> consistentConstraints = new HashSet<Constraint> (constraints);
//		for (Constraint constraint : consistentConstraints) {
//			if (!constraint.contains(this)) {
//				consistentConstraints.remove(constraint);
//			}
//		}
//		return (this.population.size() - consistentConstraints.size());
//	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/
	
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
	    if (!(other instanceof StdLogicalVariable))
	    	return false;
	    // Tests if both have the same attributes
	    StdLogicalVariable targetObject = (StdLogicalVariable) other;
	    return this.name.equals(targetObject.name);	    		
	}
	
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
