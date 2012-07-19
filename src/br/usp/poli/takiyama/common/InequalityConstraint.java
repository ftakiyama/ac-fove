package br.usp.poli.takiyama.common;

public class InequalityConstraint implements Constraint {
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof InequalityConstraint))
	    	return false;
	    // Tests if both have the same attributes
	    InequalityConstraint targetObject = (InequalityConstraint) other;
	    return true;	    		
	}
	
	@Override
	public int hashCode() { // Algorithm extracted from Bloch,J. Effective Java
		int result = 17;
		
		return result;
	}
}
