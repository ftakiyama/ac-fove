package br.usp.poli.takiyama.common;

import br.usp.poli.takiyama.cfove.StdParfactor;

/**
 * Visitor for Parfactors.
 * <p>
 * This visitor is used to check whether it is possible to apply some 
 * operation between two {@link Parfactor}s without the need to know
 * which types of parfactors are involved in the verification.
 * </p>
 * <p>
 * This visitor simulates triple dispatching to "discover" the types of
 * double-argument methods. 
 * </p>
 * 
 * @author Felipe Takiyama
 *
 * @see <a href = "http://en.wikipedia.org/wiki/Visitor_pattern">
 * Visitor Pattern</a>
 */
public interface ParfactorVisitor {
	
	/**
	 * Visits the specified {@link StdParfactor}s.
	 * 
	 * @param p1 The first parfactor to visit
	 * @param p2 The second parfactor to visit
	 */
	public void visit(StdParfactor p1, StdParfactor p2);
	
	
	/**
	 * Visits the specified {@link AggregationParfactor} and
	 * {@link StdParfactor}.
	 * 
	 * @param p1 The aggregation parfactor to visit
	 * @param p2 The standard parfactor to visit
	 */
	public void visit(AggregationParfactor p1, StdParfactor p2);
	
	
	
	/**
	 * Visits the specified {@link AggregationParfactor}s.
	 * 
	 * @param p1 The first aggregation parfactor to visit
	 * @param p2 The second aggregation parfactor to visit
	 */
	public void visit(AggregationParfactor p1, AggregationParfactor p2);
}
