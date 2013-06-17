package br.usp.poli.takiyama.acfove;

import br.usp.poli.takiyama.common.AggregationParfactor;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.RandomVariableSet;

public final class ConvertToStdParfactors implements MacroOperation {

	private final Marginal marginal;
	
	public ConvertToStdParfactors(Marginal marginal) {
		this.marginal = marginal;
	}
	
	@Override
	public Marginal run() {
		StdMarginalBuilder m = new StdMarginalBuilder(marginal.size());
		for (Parfactor p : this.marginal) {
			// TODO A visitor would be more suitable
			if (p instanceof AggregationParfactor) {
				m.parfactors(((AggregationParfactor) p).toStdParfactors());
			} else {
				m.add(p);
			}
		}
		RandomVariableSet query = marginal.preservable();
		return m.preservable(query).build();
	}

	@Override
	public int cost() {
		/*
		 * TODO: at first it doesnt matter the cost.
		 * When I put this as a macro operation to evaluate, I must 
		 * calculate it properly.
		 */
		return (int) Double.POSITIVE_INFINITY;
	}

	/**
	 * Returns 0.
	 */
	@Override
	public int numberOfRandomVariablesEliminated() {
		return 0;
	}

}
