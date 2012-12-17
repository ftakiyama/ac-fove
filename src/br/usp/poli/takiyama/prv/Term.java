package br.usp.poli.takiyama.prv;

/**
 * A term is either a variable or a constant.
 * For example X, kim, cs422, mome, or Raths can be terms. [Poole, 2010] 
 * @author ftakiyama
 *
 */
public interface Term {
	public String getValue();
	
	// The methods below are poor design...
	// I am assuming that no one is ever going to use this interface other
	// than to implement Logical Variables or Constants.
	// Using this hypothesis, I wouldn't even need to create two methods, since
	// both are mutually exclusive.
	// TODO: consider creating a better desing for future versions
	
	/**
	 * Checks if the term is a LogicalVariable.
	 * @return True if the term is a logical variable, false otherwise.
	 */
	public boolean isLogicalVariable();
	
	/**
	 * Checks if the term is a Constant.
	 * @return True if the term is a Constant, false otherwise.
	 */
	public boolean isConstant();
}
