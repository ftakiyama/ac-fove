package br.usp.poli.takiyama.acfove;

import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

public final class ShatterOnQuery implements MacroOperation {

	private Marginal marginal;
	
	public ShatterOnQuery(Marginal marginal) {
		this.marginal = new StdMarginalBuilder().add(marginal).build();
	}
	
	@Override
	public Marginal run() {
		StdMarginalBuilder builder = new StdMarginalBuilder();
		for (Parfactor parfactor : marginal) {
			RandomVariableSet query = marginal.preservable();
			if (parfactor.contains(query.prv())) {
				Set<Constraint> constraints = query.constraints();
				builder.add(split(parfactor, constraints));
			} else {
				builder.add(parfactor);
			}
		}
		builder.preservable(marginal.preservable());
		return builder.build();
	}

	/**
	 * Returns the result of splitting the specified parfactor on the 
	 * specified constraints.
	 */
	private Marginal split(Parfactor parfactor, Set<Constraint> constraints) {
		Parfactor residue = parfactor;
		StdMarginalBuilder byProduct = new StdMarginalBuilder();
		for (Constraint constraint : constraints) {
			
			// Need to check both terms from constraint
			residue = expand(residue, constraint.firstTerm());
			residue = expand(residue, constraint.secondTerm());
			
			Substitution constraintAsSub;
			try {
				constraintAsSub = Substitution.getInstance(constraint.toBinding());
			} catch (IllegalStateException e) {
				constraintAsSub = Substitution.getInstance(constraint.toInverseBinding());
			}
			if (residue.isSplittable(constraintAsSub)) {
				SplitResult split = residue.splitOn(constraintAsSub);
				residue = split.residue().iterator().next();
				byProduct.parfactors(split.result());
			} 
		}
		return byProduct.add(residue).build();
	}
	
	/**
	 * Returns the result of expanding all counting formulas from the 
	 * specified parfactor on the specified term. Expansion is made only if
	 * conditions for expansion are met.
	 */
	private Parfactor expand(Parfactor parfactor, Term term) {
		List<Prv> variables = parfactor.prvs();
		for (Prv prv : variables) {
			if (parfactor.isExpandable(prv, term)) {
				parfactor = parfactor.expand(prv, term);
			}
		}
		return parfactor;
	}
	
	@Override
	public int cost() {
		return (int) Double.POSITIVE_INFINITY;
	}

	@Override
	public int numberOfRandomVariablesEliminated() {
		return 0;
	}

}
