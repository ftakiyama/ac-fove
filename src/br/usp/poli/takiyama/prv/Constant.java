package br.usp.poli.takiyama.prv;


/**
 * A constant is a word that starts with a lower-case letter. [Poole, 2010]
 * @author ftakiyama
 *
 */
final class Constant implements Term {
	
	private final String value;
	
	/**
	 * Creates a constant. 
	 * @param value The value of the constant. It must start with a lower-case
	 * letter.
	 * @throws IllegalArgumentException If <code>value</code> does not start 
	 * with a lower-case letter.
	 */
	Constant(String value) throws IllegalArgumentException {
		this.value = new String(value);
		
		if (Character.isUpperCase(value.charAt(0))) {
			throw new IllegalArgumentException("Exception while creating " +
					"Constant: '" + value + "' must start with " +
					"lowercase letter.");
		}
	}
	
	/**
	 * Returns the value of this constant.
	 * @return The value of this constant.
	 */
	public String getValue() {
		return new String(value);
	}
	
	@Override
	public String toString() {
		return this.value;
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof Constant))
	    	return false;
	    // Tests if both have the same attributes
	    Constant targetObject = (Constant) other;
	    return this.value.equals(targetObject.value);	    		
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
}
