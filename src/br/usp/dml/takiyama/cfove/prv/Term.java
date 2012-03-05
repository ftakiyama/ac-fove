package br.usp.dml.takiyama.cfove.prv;

/**
 * A term is either a variable or a constant.
 * For example X, kim, cs422, mome, or Raths can be terms. [Poole, 2010] 
 * @author ftakiyama
 *
 */
abstract class Term {
	private final String value;
	private final boolean isConstant;
	
	Term (String value, boolean isConstant) {
		this.value = value;
		this.isConstant = isConstant;
	}
	
	public String getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
	
	/**
	 * Determines if this term is a {@link Constant}.
	 * @return True if this term is a constant, false otherwise.
	 */
	public boolean isConstant() {
		return this.isConstant;
	}
	
	/**
	 * Determines if this term is a {@link LogicalVariable}.
	 * @return True if this term is a logical variable, false otherwise.
	 */
	public boolean isLogicalVariable() {
		return !this.isConstant;
	}
	
	/**
	 * Converts this term to a constant. If this term cannot be converted, it
	 * will throw an IllegalArgumentException. 
	 * @return This term converted to a {@link Constant}
	 * @throws IllegalArgumentException If this term cannot be converted to 
	 * a constant (i.e., its name is invalid for a constant)
	 */
	/*
	public Constant toConstant() throws IllegalArgumentException {
		return new Constant(this.value);
	}
	*/
}
