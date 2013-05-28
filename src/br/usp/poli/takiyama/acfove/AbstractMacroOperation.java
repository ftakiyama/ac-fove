package br.usp.poli.takiyama.acfove;

import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.prv.Prv;

public abstract class AbstractMacroOperation implements MacroOperation {
	
	Marginal<Prv> marginal;
	
	@Override
	public Marginal<Prv> marginal() {
		return marginal;
	}
}
