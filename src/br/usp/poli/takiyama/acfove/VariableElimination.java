package br.usp.poli.takiyama.acfove;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;

public class VariableElimination extends ACFOVE {

	public VariableElimination(Marginal parfactors, Level logLevel) {
		super(propositionalizeAll(parfactors), logLevel);
	}

	public VariableElimination(Marginal parfactors) {
		super(propositionalizeAll(parfactors));
	}

	
	/**
	 * Returns the specified network completely propositionalized. The resulting
	 * network can be used with VE algorithms.
	 * <p>
	 * The resulting network will not have logical variables and factors (space
	 * constraint).
	 * </p>
	 * 
	 * @param network The network to propositionalize.
	 * @return the specified network completely propositionalized.
	 */
	private static Marginal propositionalizeAll(Marginal marginal) {
		
		Set<LogicalVariable> logicalVariables = new HashSet<LogicalVariable>();
		
		// get all logical variables and builds list of parfactors
		for (Parfactor parfactor : marginal) {
			logicalVariables.addAll(parfactor.logicalVariables());
		}
		
		// Auxiliary set of parfactors
		Set<Parfactor> parfactors = marginal.distribution().toSet();
		
		for (LogicalVariable lv : logicalVariables) {
			// propositionalizes all parfactors in the set containing the current logical variable
			for (Parfactor parfactor : parfactors) {
				if (parfactor.logicalVariables().contains(lv)) {
					MacroOperation propositionalize = new Propositionalize(marginal, parfactor, lv);
					marginal = propositionalize.run();
				}
			}
			// updates the set of parfactors
			parfactors = marginal.distribution().toSet();
		}
		
		// oops... forgot to propositionalize the query
		
		return marginal;
	}
	
	
	@Override
	void chooseMacroOperation() {
		for (Parfactor p : this.result()) {
			for (Prv prv : p.prvs()) {
				evaluateGlobalSumOut(prv);
			}
		}
	}
}
