package br.usp.poli.takiyama.common;

import br.usp.poli.takiyama.cfove.StdParfactor;

/**
 * Visitor for Parfactors.
 * <p>
 * This visitor is used to check whether it is possible to apply some 
 * operation to a {@link Parfactor}s without the need to know
 * which types of parfactors are involved in the verification.
 * </p>
 * <p>
 * This visitor simulates double dispatching to "discover" the types of
 * double-argument methods. 
 * </p>
 * 
 * @author Felipe Takiyama
 *
 * @see <a href = "http://en.wikipedia.org/wiki/Visitor_pattern">
 * Visitor Pattern</a>
 */
public interface DoubleDispatchParfactorVisitor {
	
	/**
	 * Visits the specified {@link StdParfactor}s.
	 * 
	 * @param p The standard parfactor to visit
	 */
	public void visit(StdParfactor p);
	
	
	/**
	 * Visits the specified {@link AggregationParfactor}
	 * 
	 * @param p The aggregation parfactor to visit
	 */
	public void visit(AggregationParfactor p);
}
