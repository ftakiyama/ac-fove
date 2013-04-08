package br.usp.poli.takiyama.acfove;

import java.util.Set;

import br.usp.poli.takiyama.common.Distribution;
import br.usp.poli.takiyama.common.RandomVariableSet;

/**
 * Represents a macro operation in (A)C-FOVE.
 * 
 * @author Felipe Takiyama
 */
public interface MacroOperation {
	
	/**
	 * Returns the distribution associated with this macro-operation.
	 * @return The distribution associated with this macro-operation.
	 */
	public Distribution distribution();
	
	/**
	 * Returns the set of variables to eliminate after performing the
	 * macro operation.
	 * @return The set of variables to eliminate
	 */
	public Set<RandomVariableSet> getVariablesToEliminate();
	
	/**
	 * Executes the macro-operation.
	 */
	public void run();
	
	@Override
	public String toString();
}
