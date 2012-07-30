package br.usp.poli.takiyama.acfove;

import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

public class ACFOVE {
	
	// Input
	private HashSet<Parfactor> setOfParfactors;
	private ParameterizedRandomVariable query; // change this later
	
	// Output
	//private Parfactor marginal;
	
	// Auxiliary state variables
	private HashSet<ParameterizedRandomVariable> nonQueried;
	//private Parfactor currentParfactor;
	
	// The evaluator
	private Evaluator evaluator;
	
	public ACFOVE(Set<Parfactor> setOfParfactors, ParameterizedRandomVariable query) {
		this.setOfParfactors = new HashSet<Parfactor>(setOfParfactors);
		this.query = query;
		
		this.nonQueried = new HashSet<ParameterizedRandomVariable>();
		for (Parfactor parfactor : setOfParfactors) {
			this.nonQueried.addAll(parfactor.getParameterizedRandomVariables());
		}
		this.nonQueried.remove(query);
		
		this.evaluator = new Evaluator();
	}
	
	private boolean thereAreNonQueriedRandomVariables() {
		return !this.nonQueried.isEmpty();
	}
	
	private void chooseMacroOperation() {
		// TODO: incomplete implementation
		for (Parfactor parfactor : setOfParfactors) {
			for (ParameterizedRandomVariable variable : parfactor.getParameterizedRandomVariables()) {
				if (!variable.equals(query)) {
					evaluator.checkCostOfGlobalSumOut(setOfParfactors, variable, parfactor.getConstraints());
				}
			}
		}
		// variable to eliminate chosen - this works now, but what after?
		this.nonQueried.remove(evaluator.getVariable()); // maybe i should encapsulate set management
		System.out.println(evaluator.toString());
	}
	
	private void executeMacroOperation() {
		this.setOfParfactors = new HashSet<Parfactor>(evaluator.executeCurrentOperation());
	}
	
	public Parfactor execute() {
		//MacroOperations.shatter();
		while (thereAreNonQueriedRandomVariables()) {
			chooseMacroOperation();
			executeMacroOperation();
		}
		return setOfParfactors.iterator().next(); // TODO: change this later
	}
}
