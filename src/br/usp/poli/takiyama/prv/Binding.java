package br.usp.poli.takiyama.prv;

/**
 * A binding is an ordered pair of terms (t1, t2). The first term (t1) must be a 
 * {@link LogicalVariable} and the second term (t2) may be a {@link Constant}
 * or another {@link LogicalVariable}.
 * We say that t2 replaces t1.
 * 
 * @author ftakiyama
 *
 */
public class Binding {
	
	private final LogicalVariable firstTerm;
	private final Term secondTerm;
	
	/**
	 * Constructor.
	 * @param t1
	 * @param t2
	 */
	private Binding(LogicalVariable t1, Term t2) {
		this.firstTerm = t1;
		this.secondTerm = t2;
	}
	
	/**
	 * Static factory. 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static Binding create(LogicalVariable t1, Term t2) {
		return new Binding(t1, t2);
	}
	
	/**
	 * Returns the first term of this binding.
	 * @return The first term of this binding.
	 */
	public LogicalVariable getFirstTerm() {
		return this.firstTerm;
	}

	/**
	 * Returns the second term of this binding.
	 * @return The second term of this binding.
	 */
	public Term getSecondTerm() {
		return this.secondTerm;
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof Binding))
	    	return false;
	    // Tests if both have the same attributes
	    Binding targetObject = (Binding) other;
	    return (this.firstTerm == null) ? (targetObject.firstTerm == null) : (this.firstTerm.equals(targetObject.firstTerm))
	    		&& (this.secondTerm == null) ? (targetObject.secondTerm == null) : (this.secondTerm.equals(targetObject.secondTerm));
	}
	
	@Override
	public int hashCode() { // Algorithm extracted from Bloch,J. Effective Java
		int result = 17;
		result = 31 + result + firstTerm.hashCode();
		result = 31 + result + secondTerm.hashCode(); 
		return result;
	}
	
	@Override
	public String toString() {
		return "( " + firstTerm.toString() + " " + secondTerm.toString() + " )";
	}
}
