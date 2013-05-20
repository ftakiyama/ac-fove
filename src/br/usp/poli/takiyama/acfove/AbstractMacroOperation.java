package br.usp.poli.takiyama.acfove;

import java.util.Set;

import br.usp.poli.takiyama.common.Distribution;
import br.usp.poli.takiyama.common.RandomVariableSet;
import br.usp.poli.takiyama.common.StdDistribution;
import br.usp.poli.takiyama.prv.Prv;

public abstract class AbstractMacroOperation implements MacroOperation {
	
	Distribution dist;
	Set<? extends Prv> varsToEliminate;
	
	@Override
	public Distribution distribution() {
		return StdDistribution.of(dist);
	}
	
	@Override
	public Set<RandomVariableSet> getVariablesToEliminate() {
		throw new UnsupportedOperationException("Not implemented!");
	}

}
