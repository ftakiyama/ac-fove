package br.usp.dml.takiyama.cfove.prv;

/**
 * A logical variable is a word starting with an upper-case letter or the 
 * underscore. [Poole, 2010]
 * @author ftakiyama
 *
 */
public final class LogicalVariable extends Term {
	
	// I am not really sure if the logical variable must have a domain
	// private Population domain;
	
	/**
	 * Constructor. Creates a logical variable.
	 * @param name The name of the logical variable. The name must start with
	 * an upper case letter or underscore ("_").
	 * @throws IllegalArgumentException If the name requirements are not met.
	 */
	public LogicalVariable(String name) throws IllegalArgumentException {
		// The value of a logical variable is its name
		super(name, false);
		
		// The population is empty
		// this.domain = new Population();
		
		// Checks if the name of the variable is valid
		if (!name.startsWith("_") && !Character.isUpperCase(name.charAt(0))) {
			throw new IllegalArgumentException("Exception while creating " +
					"Logical Variable: '" + name + "' must start with " +
					"uppercase letter or underscore.");
		}
	}
	
}
