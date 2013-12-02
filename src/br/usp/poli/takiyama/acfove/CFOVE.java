package br.usp.poli.takiyama.acfove;

import java.util.logging.Level;

import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;

public class CFOVE extends ACFOVE {

	public CFOVE(Marginal parfactors, Level logLevel) {
		super(removeAggregation(parfactors), logLevel);
	}

	public CFOVE(Marginal parfactors) {
		super(removeAggregation(parfactors));
	}

	/**
	 * Returns the specified network with aggregation parfactors converted to
	 * standard parfactors. The resulting network can be used with the C-FOVE
	 * algorithm. 
	 * 
	 * @param network The network where aggregation parfactors will be converted
	 * @return The specified network with aggregation parfactors converted to
	 * standard parfactors. 
	 */
	private static Marginal removeAggregation(Marginal marginal) {
		for (Parfactor parfactor : marginal) {
			if (parfactor instanceof AggParfactor) {
				MacroOperation convert = new ConvertToStdParfactors(marginal, parfactor);
				marginal = convert.run();
			}
		}
		return marginal;
	}
}
