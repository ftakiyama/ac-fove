package br.usp.poli.takiyama.acfove;

import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.utils.MathUtils;

/**
 * This operation eliminates a free {@link LogicalVariable} from a  
 * {@link Parfactor}. This is done using the Counting operation.
 */
public final class CountingConvert implements MacroOperation {

	// Marginal that contains the parfactor being counted
	private final Marginal marginal;
	
	// Parfactor being counted
	private final Parfactor countableParfactor;
	
	// Logical variable being counted
	private final LogicalVariable freeVariable;
	
	// PRV that contains the free variable
	private Prv prvToCount;
	
	public CountingConvert(Marginal marginal, Parfactor countable, LogicalVariable freeVariable) {
		this.marginal = marginal;
		this.countableParfactor = countable;
		this.freeVariable = freeVariable;
		this.prvToCount = countable.factor().getVariableHaving(freeVariable);
	}
	
	@Override
	public Marginal run() {
		
		StdMarginalBuilder resultBuilder = new StdMarginalBuilder();
		resultBuilder.add(marginal).remove(countableParfactor);
		
		Parfactor counted = countableParfactor.count(freeVariable);
		resultBuilder.add(counted);
		
		return resultBuilder.build();
	}

	/**
	 * Returns the counted factor size, which is given by
	 * <p>
	 * |F| / |range(f(...A...))| * 
	 * combination(|D(A):C| + |range(f(...A...))| - 1, |range(f(...A...))| - 1)
	 * </p>
	 * where
	 * <li> |F| is factor component size from parfactor being counted
	 * <li> |range(f(...A...))| is the size of the counted PRV range
	 * <li> |D(A):C| is the number of free logical variable individuals 
	 * satisfying constraints from the parfactor being counted
	 */
	@Override
	public int cost() {
		if (countableParfactor.isCountable(freeVariable)) {
			int f = countableParfactor.factor().size();
			int r = prvToCount.range().size();
			int h = getNumberOfHistograms();
			return f / r * h;
		} else {
			return (int) Double.POSITIVE_INFINITY;
		}
	}

	/**
	 * Returns the number of histograms created when converting the  
	 * standard parameterized random variable to a counting formula.
	 */
	private int getNumberOfHistograms() {
		int domain = freeVariable.numberOfIndividualsSatisfying(countableParfactor.constraints());
		int range = prvToCount.range().size() - 1;
		int numberOfHistograms = MathUtils.combination(domain + range, range).intValue();
		return numberOfHistograms;
	}

	/**
	 * Returns zero.
	 */
	@Override
	public int numberOfRandomVariablesEliminated() {
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("COUNTING-CONVERT").append("\n")
				.append(countableParfactor).append("\n")
				.append(freeVariable);
		return builder.toString();
	}
}
