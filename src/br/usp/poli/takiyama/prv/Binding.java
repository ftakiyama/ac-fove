package br.usp.poli.takiyama.prv;

/**
 * A binding is an ordered pair of terms (t1, t2). The first term (t1) must be a 
 * {@link LogicalVariable} and the second term (t2) may be a {@link Constant}
 * or another {@link LogicalVariable}.
 * We say that t2 replaces t1.
 * 
 * @author Felipe Takiyama
 *
 */
public class Binding {
	
	private final LogicalVariable firstTerm;
	private final Term secondTerm;
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	/**
	 * Creates a binding composed by the specified terms.
	 * 
	 * @param t1 The logical variable being replaced
	 * @param t2 The replacement
	 */
	private Binding(LogicalVariable t1, Term t2) {
		firstTerm = t1;
		secondTerm = t2;
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/
	
	/**
	 * Returns a binding with the specified terms. 
	 * 
	 * @param t1 The logical variable being replaced
	 * @param t2 The replacement
	 * @return The binding t1/t2
	 */
	public static Binding getInstance(LogicalVariable t1, Term t2) {
		return new Binding(t1, t2);
	}

	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	/**
	 * Returns the first term of this binding.
	 * @return The first term of this binding.
	 */
	public LogicalVariable firstTerm() {
		return firstTerm;
	}

	
	/**
	 * Returns the second term of this binding.
	 * @return The second term of this binding.
	 */
	public Term secondTerm() {
		return secondTerm;
	}
	
	
	/**
	 * Returns <code>true</code> if this binding contains the specified term.
	 * 
	 * @param t The term to search for.
	 * @return <code>true</code>  if this binding contains the specified term, 
	 * <code>false</code> otherwise.
	 */
	public boolean contains(Term t) {
		return t.equals(firstTerm) || t.equals(secondTerm);
	}
	
	
	/**
	 * Returns <code>true</code> if:
	 * <li> The second term is a {@link Constant} and it belongs to first
	 * term's population <b>or</b>
	 * <li> The second term is a {@link LogicalVariable} and both terms have
	 * the same population
	 * 
	 * @return <code>true</code> if second term is in first term's population 
	 * or if second term and first term have the same population, 
	 * <code>false</code> otherwise.
	 */
	public boolean isValid() {
		boolean isValid = false;
		if (secondTerm.isConstant()) {
			Constant c = (Constant) secondTerm;
			isValid = firstTerm.population().contains(c);
		} else {
			LogicalVariable lv = (LogicalVariable) secondTerm;
			isValid = firstTerm.population().equals(lv.population());
		}
		return isValid;
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

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
		return firstTerm.toString() + "/" + secondTerm.toString();
	}
}
