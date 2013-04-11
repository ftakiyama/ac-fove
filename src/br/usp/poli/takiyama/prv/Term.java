package br.usp.poli.takiyama.prv;

/**
 * A term is either a variable or a constant.
 * For example X, kim, cs422, mome, or Raths can be terms. [Poole, 2010] 
 * @author ftakiyama
 *
 */
public interface Term {
	
	/**
	 * Returns the String representation of the value of term.
	 * @return The String representation of the value of term.
	 */
	public String value();
	
	
	/*
	 * The two methods below, isVariable() and isConstant(), are *not*
	 * good OO practice. But:
	 * - I am probably the only one that will ever edit this code
	 * - This code is unlikely to be extended in the near future
	 * - A Term is either a variable or a constant by definition
	 */
	
	/**
	 * Returns <code>true</code> if this term is a {@link LogicalVariable}.
	 * 
	 * @return <code>true</code> if this term is a {@link LogicalVariable},
	 * <code>false</code> otherwise.
	 */
	public boolean isVariable();
	
	
	/**
	 * Returns <code>true</code> if this term is a {@link Constant}.
	 * 
	 * @return <code>true</code> if this term is a {@link Constant},
	 * <code>false</code> otherwise.
	 */
	public boolean isConstant();
	
	
	@Override	
	public boolean equals(Object o);	
	
	@Override	
	public int hashCode();
	
	@Override	
	public String toString();
}
