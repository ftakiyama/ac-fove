package br.usp.poli.takiyama.cfove;

import java.util.TreeSet;

import br.usp.poli.takiyama.common.RandomVariable;

public class CFOVE {
	
	// Input
	private TreeSet<RandomVariable> queryRandomVariables;
	private TreeSet<SimpleParfactor> setOfParfactors;
	
	// Output
	private SimpleParfactor marginalDistribution;
	
	
	private boolean thereAreNonQueriedRandomVariables() {
		return true;
	}
	
	private void chooseMacroOperation() {
		/*
		 * for each parfactor p in set of parfactors P
		 * 	   for each PRV v in p
		 * 	       C := contraints from p
		 *         if GLOBAL-SUM-OUT(P,v,C) is possible
		 *             c := cost of the operation
		 *             n := number of variables eliminated
		 *             store (P,v,C,c,n)
		 * get (P,v,C,c) with lowest cost c and highest n
		 * 
		 *             OR
		 *             
		 * get first variable to be eliminated, and eliminate it
		 */
	}
	
	private void executeMacroOperation() {
		
	}
	
	public void execute() {
		//MacroOperations.sqhatter();
		while (thereAreNonQueriedRandomVariables()) {
			chooseMacroOperation();
			executeMacroOperation();
		}
	}
	
}
