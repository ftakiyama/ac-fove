package br.usp.poli.takiyama.acfove;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import br.usp.poli.takiyama.cfove.StdParfactor;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Prvs;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.utils.Sets;

final class GlobalSumOut implements MacroOperation {

	// The marginal where elimination will take place
	private final Marginal marginal;
	
	// The set of random variables to eliminate
	private final RandomVariableSet eliminables;
	
	/*
	 * These variables are redundant. If cost is INFINITY, then this operation
	 * is automatically impossible.
	 */
	private int cost;
	private boolean isPossible;
	
	private static int infinity = (int) Double.POSITIVE_INFINITY;
	
	public GlobalSumOut(Marginal marginal, RandomVariableSet eliminables) {
		this.marginal = marginal;
		this.eliminables = eliminables;
		calculateFeasibility();
	}
	
	/*
	 * Calculates the feasibility of this operation. This operation is possible
	 * if all parfactors involving the variables being eliminated can be 
	 * multiplied and those variables can be summed out from the product.
	 */
	private void calculateFeasibility() {
		setCost(infinity);
		
		RandomVariableSet elim = RandomVariableSet.getInstance(eliminables.prv().getCanonicalForm(), eliminables.constraints());
		if (Prvs.areDisjoint(elim, marginal.preservable())) {
			
			Queue<Parfactor> queue = new LinkedList<Parfactor>(marginal.distribution().toSet());
			Parfactor result = new StdParfactorBuilder().build();
			for (Parfactor candidate : queue) {
				if (containsEliminable(candidate)) {
					if (result.isMultipliable(candidate)) {
						result = result.multiply(candidate);
					} else {
						// contains eliminables but cannot be multiplied: be sure
						// to shatter before!
						return;
					}
				}
			}
			
			if (result.isEliminable(eliminables)) {
				int f = result.factor().size();
				int v = eliminables.range().size();
				setCost(f / v); 
			}
		}
	}
	
	/*
	 * Updates cost and isPossible. Helps to keep consistency.
	 */
	private void setCost(int c) {
		if (c < infinity) {
			this.cost = c;
			this.isPossible = true;
		} else {
			this.cost = infinity;
			this.isPossible = false;
		}
	}

	/**
	 * Returns true if the specified parfactor contains the set of variables
	 * to eliminate.
	 */
	private boolean containsEliminable(Parfactor candidate) {
		/*
		 * In theory, marginal is shattered, so candidate either contains
		 * eliminables or not.
		 */
		
		RandomVariableSet elim = RandomVariableSet.getInstance(eliminables.prv().getCanonicalForm(), Sets.union(eliminables.prv().constraints(), eliminables.constraints()));
		List<Prv> variables = candidate.prvs();
		for (Prv prv : variables) {
			RandomVariableSet rvs = RandomVariableSet.getInstance(prv.getCanonicalForm(), candidate.constraints());
			if (rvs.equals(elim)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Marginal run() {
		if (isPossible) {
			Parfactor result = new StdParfactorBuilder().build();
			
			StdMarginalBuilder marginalResult = new StdMarginalBuilder();
			marginalResult.add(marginal);
			
			// Multiplies all parfactors that involve the eliminable PRV
			for (Parfactor candidate : marginal) {
				if (containsEliminable(candidate)) {
					result = result.multiply(candidate);
					marginalResult.remove(candidate);
				}
			}
			
			// Sums out the eliminable if possible - actually it should be possible at this point
			//if (Sets.setOf(eliminables.prv().parameters()).equals(result.logicalVariables())) {
			try {
				result = result.sumOut(eliminables.prv());
			} catch (IllegalArgumentException e) {
				result = new StdParfactor.StdParfactorBuilder().build();
			}
			//}
			
			// Adds the result to marginal result if not constant (constant 
			// parfactors are irrelevant)
			if (!result.isConstant()) {
				marginalResult.add(result);
			}
			
			return marginalResult.build();
			
		} else {
			return marginal;
		}
	}
	
	@Override
	public int cost() {
		return cost;
	}
	
	@Override
	public int numberOfRandomVariablesEliminated() {
		// cost = infinity means this operation is impossible, thus no vars 
		// can be eliminated.
		if (cost() == infinity) {
			return 0;
		} else {
			return eliminables.prv().groundSetSize(eliminables.constraints());
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GLOBAL-SUM-OUT").append(" ").append(eliminables);
		return builder.toString();
	}
}
