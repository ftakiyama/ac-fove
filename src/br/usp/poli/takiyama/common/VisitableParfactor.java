package br.usp.poli.takiyama.common;

import br.usp.poli.takiyama.cfove.StdParfactor;

/**
 * Parfactors that implement this interface are considered 'visitable' by
 * {@link ParfactorVisitor}.
 * <p>
 * This interface provides auxiliary methods to implement Visitor Pattern 
 * using triple dispatch to avoid the use of <code>instanceof</code>.
 * </p>
 * 
 * @author Felipe Takiyama
 * 
 * @see ParfactorVisitor
 *
 */
public interface VisitableParfactor {
	
	/**
	 * Accepts the specified visitor.
	 * 
	 * @param visitor The visitor of this parfactor
	 * @param p A parfactor whose type is yet to be discovered.
	 */
	public void accept(ParfactorVisitor visitor, Parfactor p);
	
	
	/**
	 * Accepts the specified visitor.
	 * 
	 * @param visitor The visitor of this parfactor
	 * @param p The other parfactor to be visited by the visitor
	 */
	public void accept(ParfactorVisitor visitor, StdParfactor p); 
	
	
	/**
	 * Accepts the specified visitor.
	 * 
	 * @param visitor The visitor of this parfactor
	 * @param p The other parfactor to be visited by the visitor
	 */
	public void accept(ParfactorVisitor visitor, AggregationParfactor p);
	
}
