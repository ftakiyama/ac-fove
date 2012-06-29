package br.usp.dml.takiyama.cfove.prv;

/**
 * A logical variable is a word starting with an upper-case letter or the 
 * underscore. [Poole, 2010]
 * @author ftakiyama
 *
 */
final class LogicalVariable implements Term {
	
	// I am not really sure if the logical variable must have a domain
	// private Population domain;
	private final String name;
	
	/**
	 * Constructor. Creates a logical variable.
	 * @param name The name of the logical variable. The name must start with
	 * an upper case letter or underscore ("_").
	 * @throws IllegalArgumentException If the name requirements are not met.
	 */
	LogicalVariable(String name) throws IllegalArgumentException {
		// The value of a logical variable is its name
		this.name = new String(name);
		
		// Checks if the name of the variable is valid
		if (!name.startsWith("_") && !Character.isUpperCase(name.charAt(0))) {
			throw new IllegalArgumentException("Exception while creating " +
					"Logical Variable: '" + name + "' must start with " +
					"uppercase letter or underscore.");
		}
	}
	
	/**
	 * Returns the value of this logical variable.
	 * @return The value of this logical variable.
	 */
	public String getValue() {
		return new String(name);
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
