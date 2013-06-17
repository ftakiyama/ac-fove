package br.usp.poli.takiyama.acfove;

import br.usp.poli.takiyama.common.Marginal;

/**
 * Dummy operation. It is not supposed to be executed.
 * @author Felipe Takiyama
 */
final class ImpossibleOperation implements MacroOperation {

	public static final ImpossibleOperation instance = new ImpossibleOperation();
	
	private ImpossibleOperation() {
		// cannot be instantiated.
	}
	
	/**
	 * Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public Marginal run() {
		throw new UnsupportedOperationException("This is a impossible operation!");
	}

	/**
	 * Returns {@link Double#POSITIVE_INFINITY}.
	 */
	@Override
	public int cost() {
		return ((int) Double.POSITIVE_INFINITY);
	}

	/**
	 * Returns -1.
	 */
	@Override
	public int numberOfRandomVariablesEliminated() {
		return 0;
	}

	@Override
	public String toString() {
		return "IMPOSSIBLE-OPERATION";
	}
}
