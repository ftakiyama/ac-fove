package br.usp.dml.takiyama.cfove.prv;

/**
 * A binding is an ordered pair of terms (t1, t2). The first term (t1) must be a 
 * {@link LogicalVariable} and the second term (t2) may be a {@link Constant}
 * or another {@link LogicalVariable}.
 * We say that t2 replaces t1.
 * 
 * @author ftakiyama
 *
 */
public final class Binding {
	private final LogicalVariable firstTerm;
	private final Term secondTerm;
	
	/**
	 * Returns a binding.
	 * @param t1 A {@link LogicalVariable}
	 * @param t2 A {@link Term}
	 * @return A binding where the first term is t1 and the second term is t2
	 */
	public static Binding create(LogicalVariable t1, Term t2) {
		return new Binding(t1, t2);
	}
	
	/**
	 * Constructor. Creates a new binding.
	 * To get an instance of this class, use the instance factory getInstance(). 
	 * @param t1 A {@link LogicalVariable}
	 * @param t2 A {@link Term}
	 */
	private Binding(LogicalVariable t1, Term t2) {
		this.firstTerm = t1;
		this.secondTerm = t2;
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
	
	/**
	 * Checks if this binding is valid. The first term must be a 
	 * {@link LogicalVariable} and the second term may be a {@link Constant}
	 * or another {@link LogicalVariable}.
	 * @return True if this binding is valid, false otherwise.
	 */
//	public boolean isValid() {
//		return (firstTerm.isLogicalVariable() 
//				&& (secondTerm.isLogicalVariable() || secondTerm.isConstant()));
//	}
	
	@Override
	public String toString() {
		return "( " + firstTerm.toString() + " " + secondTerm.toString() + " )";
	}
}
