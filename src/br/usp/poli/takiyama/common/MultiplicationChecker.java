package br.usp.poli.takiyama.common;

import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.cfove.StdParfactor;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Prvs;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.utils.Sets;

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
	
	
	/**
	 * Checks whether the specified {@link StdParfactor} can be
	 * multiplied by another {@link StdParfactor}. This is possible if
	 * all parameterized random variables f(...) &in; Vi and f'(...) &in; Vj such 
	 * that ground(f(...)) : Ci = ground(f'(...)) : Cj, f(...) and f'(...) are 
	 * identically parameterized by logical variables and the set of other 
	 * logical variables present in parfactor gi is disjoint with the set of 
	 * logical variables present in parfactor gj.
	 * 
	 */
	@Override
	public void visit(StdParfactor p1, StdParfactor p2) {
		
		/*
		 * This code only verifies the first condition.
		 * The other condition, concerning disjoint logical variables, is not
		 * verified. I think I have misinterpreted this rule...
		 */
		areMultipliable = true;
		for (Prv prv1 : p1.prvs()) {
			for (Prv prv2 : p2.prvs()) {
				RandomVariableSet rvs1 = RandomVariableSet.getInstance(prv1, Sets.union(prv1.constraints(), p1.constraints()));
				RandomVariableSet rvs2 = RandomVariableSet.getInstance(prv2, Sets.union(prv2.constraints(), p2.constraints()));
				if (!Prvs.areDisjoint(rvs1, rvs2) && !rvs1.equals(rvs2)) {
					areMultipliable = false;
					break;
				}
			}
		}
		
		
		
		// TODO Need to use unification, nevertheless, I'll have a set of shattered parfactors,
		// so multiplication is always possible between std parfactors.
		
		// hehe - it is not
//		areMultipliable = true;
//		Set<LogicalVariable> lv1 = p1.logicalVariables();
//		Set<LogicalVariable> lv2 = p2.logicalVariables();
//		for (Prv prv1 : p1.prvs()) {
//			for (Prv prv2 : p2.prvs()) {
//				if (sameName(prv1, prv2)) {
//					boolean sameParameters = sameParameters(prv1, prv2);
//					// I think the disjoint lv thing is not correct
//					//boolean disjointRemainingLogicalVariables = disjointRemainingLogicalVariables(lv1, prv1, lv2, prv2);
//					if (!sameParameters /*|| !disjointRemainingLogicalVariables*/) {
//						areMultipliable = false;
//						break;
//					}
//				}
//			}
//		}
	}
	
	private boolean sameName(Prv v1, Prv v2) {
		return (v1.name().equals(v2.name()));
	}
	
	private boolean sameParameters(Prv v1, Prv v2) {
		return (v1.parameters().equals(v2.parameters()));
	}
	
	private boolean disjointRemainingLogicalVariables(Set<LogicalVariable> lv1, 
			Prv prv1, Set<LogicalVariable> lv2, Prv prv2) {
		Set<LogicalVariable> remaining1 = Sets.difference(lv1, prv1.parameters());
		Set<LogicalVariable> remaining2 = Sets.difference(lv2, prv2.parameters());
		Set<LogicalVariable> intersection = Sets.intersection(remaining1, remaining2);
		return intersection.isEmpty();
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
		boolean stdIsEmpty = std.factor().isEmpty();
		boolean sameConstraints = agg.constraints().equals(std.constraints());
		List<Prv> stdPrvs = std.factor().variables();
		List<Prv> aggPrvs = agg.factor().variables();
		boolean samePrvsInFactor = stdPrvs.containsAll(aggPrvs) && aggPrvs.containsAll(stdPrvs);
		areMultipliable = (stdIsEmpty || (sameConstraints && samePrvsInFactor));
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
