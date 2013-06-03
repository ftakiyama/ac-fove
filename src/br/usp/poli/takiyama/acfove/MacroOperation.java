package br.usp.poli.takiyama.acfove;

import br.usp.poli.takiyama.common.Marginal;

/**
 * Represents a macro operation in (A)C-FOVE.
 * 
 * @author Felipe Takiyama
 */
public interface MacroOperation {
	
	/**
	 * Executes the macro-operation.
	 * 
	 * @return The resulting marginal after applying the macro-operation.
	 */
	public Marginal run();
	
	/**
	 * Returns this operation cost.
	 * <p>
	 * The cost of an operation is the size of parfactors it creates.
	 * </p>
	 * 
	 * @return This operation cost.
	 */
	public int cost();
	
	/**
	 * Returns the number of random variables that are eliminated if this
	 * operation is executed using {@link run}.
	 * 
	 * @return the number of random variables that are eliminated if this
	 * operation is executed
	 */
	public int numberOfRandomVariablesEliminated();
	
	@Override
	public String toString();
}
