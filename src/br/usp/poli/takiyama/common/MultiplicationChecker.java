package br.usp.poli.takiyama.common;

import br.usp.poli.takiyama.cfove.StdParfactor;

/**
 * Encapsulates the algorithm to check conditions for multiplication between
 * {@link Parfactor}s.
 * <p>
 * Currently, it supports {@link StdParfactor} and {@link AggregationParfactor}.
 * If new types of parfactors are added, the interface needs to be remodeled.
 * </p>
 * 
 * @author Felipe Takiyama
 *
 */
public final class MultiplicationChecker implements ParfactorVisitor {

	private boolean areMultipliable;
	
	/**
	 * Constructor.
	 */
	public MultiplicationChecker() {
		areMultipliable = false;
	}
	
	
	@Override
	public void visit(StdParfactor p1, StdParfactor p2) {
		// TODO Need to use unification, nevertheless, I'll have a set of shattered parfactors,
		// so multiplication is always possible between std parfactors.
		areMultipliable = true;
	}
	

	/**
	 * Checks whether the specified {@link AggregationParfactor} can be
	 * multiplied by the specified {@link StdParfactor}. This is possible if
	 * <li> They both have the same set of constraints
	 * <li> The specified standard parfactor has the same PRVs as 
	 * the specified aggregation parfactor, excluding the child PRV.
	 */
	@Override
	public void visit(AggregationParfactor agg, StdParfactor std) {
		boolean sameConstraints = agg.constraints().equals(std.constraints());
		boolean factorOnParent = (std.factor().variables().size() == 1)
				&& (std.contains(agg.parent()));
		areMultipliable = sameConstraints && factorOnParent;
	}

	
	/**
	 * Returns <code>false</code>, because an 
	 * {@link AggregationParfactor} cannot 
	 * multiply another {@link AggregationParfactor}
	 */
	@Override
	public void visit(AggregationParfactor agg1, AggregationParfactor agg2) {
		areMultipliable = false;
	}
	
	
	/**
	 * Returns the status of a multiplication check. If no visit were made,
	 * returns <code>false</code>.
	 * 
	 * @return The status of a multiplication check or <code>false</code> if
	 * no visits were made.
	 */
	public boolean areMultipliable() {
		return areMultipliable;
	}

}
