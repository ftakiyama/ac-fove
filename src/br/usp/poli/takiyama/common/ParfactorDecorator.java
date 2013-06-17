package br.usp.poli.takiyama.common;

import java.util.Set;

import br.usp.poli.takiyama.prv.LogicalVariable;

/**
 * Implements decorator pattern for parfactors.
 * @author Felipe Takiyama
 */
public interface ParfactorDecorator extends Parfactor {
	
	/**
	 * Returns the set of all logical variables in this parfactor, including
	 * bound variables from counting formulas.
	 */
	@Override
	public Set<LogicalVariable> logicalVariables();
}
