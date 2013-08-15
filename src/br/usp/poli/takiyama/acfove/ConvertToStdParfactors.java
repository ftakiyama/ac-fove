package br.usp.poli.takiyama.acfove;

import br.usp.poli.takiyama.common.AggregationParfactor;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Prv;

public final class ConvertToStdParfactors implements MacroOperation {

	private final Parfactor parfactorToConvert;
	private final Marginal marginal;
	
	public ConvertToStdParfactors(Marginal m, Parfactor p) {
		this.marginal = m;
		this.parfactorToConvert = p;
	}
	
	@Override
	public Marginal run() {
		StdMarginalBuilder m = new StdMarginalBuilder();
		m.add(marginal);
		if (parfactorToConvert instanceof AggregationParfactor) {
			m.parfactors(((AggregationParfactor) parfactorToConvert).toStdParfactors());
			m.remove(parfactorToConvert);
		}
		return m.build();
	}

	/**
	 * Let g be an aggregation parfactor
	 * Returns the size of the factor component if we multiplied the parfactors
	 * that result from converting g to standard parfactors.
	 */
	@Override
	public int cost() {
		int cost = (int) Double.POSITIVE_INFINITY;
		if (parfactorToConvert instanceof AggregationParfactor) {
			AggregationParfactor ap = (AggregationParfactor) parfactorToConvert; 
			cost = ap.extraVariable().numberOfIndividualsSatisfying(ap.constraintsOnExtra());
			for (Prv var : ap.prvs()) {
				cost = cost * var.range().size();
			}
		}
		return cost;
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
		StringBuilder builder = new StringBuilder();
		builder.append("CONVERT-TO-STD-PARFACTORS");
		return builder.toString();
	}
}
