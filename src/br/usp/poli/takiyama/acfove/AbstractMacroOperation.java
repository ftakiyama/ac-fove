package br.usp.poli.takiyama.acfove;

import br.usp.poli.takiyama.common.Marginal;

public abstract class AbstractMacroOperation implements MacroOperation {
	
	Marginal marginal;
	
	@Override
	public Marginal marginal() {
		return marginal;
	}
}
