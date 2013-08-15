package br.usp.poli.takiyama.acfove;

import java.util.Iterator;
import java.util.Set;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Prvs;
import br.usp.poli.takiyama.prv.RandomVariableSet;

public final class FinalMultiplication implements MacroOperation {

	private final Marginal marginal;
	
	public FinalMultiplication(Marginal m) {
		marginal = m;
	}
	
	@Override
	public Marginal run() {
		Parfactor product = new StdParfactorBuilder().build();
		
		// Multiplies all parfactors in the marginal
		for (Parfactor candidate : marginal) {
			product = product.multiply(candidate);
		}
		Marginal result = new StdMarginalBuilder(1).parfactors(product).build();
		
		return result;
	}

	/**
	 * Returns the size of the first factor returned by marginal iterator.
	 * This operation should be called when all parfactors have the same size.
	 */
	@Override
	public int cost() {
		int cost = (int) Double.POSITIVE_INFINITY;
		if (marginalHasOnlyPreservable()) {
			Iterator<Parfactor> it = marginal.iterator();
			if (it.hasNext()) {
				cost = it.next().factor().size();
			}
		}
		return cost;
	}
	
	private boolean marginalHasOnlyPreservable() {
		for (Parfactor p : marginal) {
			for (Prv prv : p.prvs()) {
				RandomVariableSet rvs = RandomVariableSet.getInstance(prv, p.constraints());
				if (Prvs.areDisjoint(rvs, marginal.preservable())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns 0.
	 */
	@Override
	public int numberOfRandomVariablesEliminated() {
		return 0;
	}

	@Override
	public String toString() {
		return "FINAL-MULTIPLICATION";
	}
}
