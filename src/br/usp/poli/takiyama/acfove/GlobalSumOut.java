package br.usp.poli.takiyama.acfove;

import java.util.ArrayList;
import java.util.Set;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.utils.Sets;

final class GlobalSumOut implements MacroOperation {

	private final Marginal marginal;
	
	private final RandomVariableSet eliminables;
		
	public GlobalSumOut(Marginal marginal, RandomVariableSet eliminables) {
		this.marginal = marginal;
		this.eliminables = eliminables;
	}
	
	@Override
	public Marginal run() {
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
		
		// Sums out the eliminable if possible
		if (Sets.setOf(eliminables.prv().parameters()).equals(result.logicalVariables())) {
			result = result.sumOut(eliminables.prv());
		}
		
		// Adds the result to marginal result if not constant (constant 
		// parfactors are irrelevant)
		if (!result.isConstant()) {
			marginalResult.add(result);
		}
		
		return marginalResult.build();
	}

	private boolean containsEliminable(Parfactor candidate) {
		return (candidate.contains(eliminables.prv()) 
				&& candidate.constraints().containsAll(eliminables.constraints()));
	}
	
	@Override
	public int cost() {
		Set<Prv> vars = Sets.getInstance(16);
		Set<Constraint> constraints = Sets.getInstance(64);
		
		for (Parfactor candidate : marginal) {
			if (containsEliminable(candidate)) {
				vars.addAll(candidate.prvs()); 
				constraints.addAll(candidate.constraints());
			}
		}
		
		int cost = (int) Double.POSITIVE_INFINITY;
		if (eliminableHasAllLogicalVariablesFrom(vars)) {
			int f = Factor.getInstance(new ArrayList<Prv>(vars)).size();
			int v = eliminables.range().size();
			cost = f / v; 
		}
		
		return cost;
	}

	private boolean eliminableHasAllLogicalVariablesFrom(Set<Prv> prvs) {
		Set<LogicalVariable> lvs = Sets.getInstance(0);
		for (Prv v : prvs) {
			lvs.addAll(v.parameters());
		}
		return eliminables.prv().parameters().containsAll(lvs);
	}
	
	@Override
	public int numberOfRandomVariablesEliminated() {
		return eliminables.prv().groundSetSize(eliminables.constraints());
	}

}
