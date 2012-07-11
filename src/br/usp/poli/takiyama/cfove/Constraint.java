package br.usp.poli.takiyama.cfove;

public class Constraint {
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof Constraint))
	    	return false;
	    // Tests if both have the same attributes
	    Constraint targetObject = (Constraint) other;
	    return true;	    		
	}
	
	@Override
	public int hashCode() { // Algorithm extracted from Bloch,J. Effective Java
		int result = 17;
		
		return result;
	}
}
