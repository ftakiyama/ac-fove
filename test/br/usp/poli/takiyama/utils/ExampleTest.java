package br.usp.poli.takiyama.utils;

import java.util.HashSet;

import org.junit.Test;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;


public class ExampleTest {
	
	private final int limit = 3;
	
	@Test
	public void testPropositionalization() {
		
		for (int domainSize = 1; domainSize < limit; domainSize++) {
			
			Example network = Example.competingWorkshopsNetwork(domainSize, domainSize);
			
			// Query
			Prv someDeath = network.prv("success ( )");
			RandomVariableSet query = RandomVariableSet.getInstance(someDeath, new HashSet<Constraint>(0));
			
			// Input marginal
			Marginal input = network.getMarginal(query);

			//System.out.println(network.propositionalizeAll(input));
			
		}
	}
	
	@Test
	public void testPropositionalization2() {
				
		Example network = Example.waterSprinklerNetWork(limit);
		
		// Query
		Prv someDeath = network.prv("rain ( )");
		RandomVariableSet query = RandomVariableSet.getInstance(someDeath, new HashSet<Constraint>(0));
		
		// Input marginal
		Marginal input = network.getMarginal(query);

		//System.out.println(network.propositionalizeAll(input));

	}
	
	@Test
	public void conversionTest() {
		Example network = Example.competingWorkshopsNetwork(limit, limit);
		
		// Query
		Prv someDeath = network.prv("success ( )");
		RandomVariableSet query = RandomVariableSet.getInstance(someDeath, new HashSet<Constraint>(0));
		
		// Input marginal
		Marginal input = network.getMarginal(query);
		
		System.out.println(network.removeAggregation(input));
	}
}
