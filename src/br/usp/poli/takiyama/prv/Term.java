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
	
	@Override	
	public boolean equals(Object o);	
	
	@Override	
	public int hashCode();
	
	@Override	
	public String toString();
}
