package br.usp.poli.takiyama.acfove;

import java.util.Set;

import br.usp.poli.takiyama.common.Distribution;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.RandomVariableSet;
import br.usp.poli.takiyama.prv.Prv;

/**
 * Represents a macro operation in (A)C-FOVE.
 * 
 * @author Felipe Takiyama
 */
public interface MacroOperation {
	
//	@Deprecated
//	/**
//	 * Returns the distribution associated with this macro-operation.
//	 * @return The distribution associated with this macro-operation.
//	 */
//	public Distribution distribution();
//	
//	@Deprecated
//	/**
//	 * Returns the set of variables to eliminate after performing the
//	 * macro operation.
//	 * @return The set of variables to eliminate
//	 */
//	public Set<RandomVariableSet> getVariablesToEliminate();
	
	/**
	 * Returns the marginal.
	 */
	public Marginal<Prv> marginal();
	
	/**
	 * Executes the macro-operation.
	 */
	public void run();
	
	@Override
	public String toString();
}
