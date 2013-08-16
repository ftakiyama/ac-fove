package br.usp.poli.takiyama.acfove;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import br.usp.poli.takiyama.common.AggregationParfactor;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Distribution;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.utils.Example;


public class CompetingWorkshops {
	/**
	 * Network: competing workshops (Milch 2008)
	 * Query: success
	 * Evidence: none
	 * Population size: 10 workshops, 1000 people
	 * 
	 */
	@Test
	public void querySomeDeath() {
		
		// Network initialization
		int numberOfPeople = 10;
		int numberOfWorkshops = 10;
		Example network = Example.competingWorkshopsNetwork(numberOfWorkshops, numberOfPeople);
		
		Parfactor gh = network.parfactor("ghot");
		Parfactor ga = network.parfactor("gattends");
		Parfactor gs = network.parfactor("gsuccess");

		// Query
		Prv success = network.prv("success ( )");
		RandomVariableSet query = RandomVariableSet.getInstance(success, new HashSet<Constraint>(0));
		
		// Input marginal
		Marginal input = new StdMarginalBuilder(5).parfactors(gh, ga, gs).preservable(query).build();

		// Runs AC-FOVE on input marginal
		ACFOVE acfove = new LoggedACFOVE(input);
		Parfactor result = acfove.run();
		
		// Calculates the correct result
		
		// Sum out hot
		Prv hot = network.prv("hot ( Workshop )");
		Parfactor afterSumOutHot = gh.multiply(ga).sumOut(hot);
		
		// Converts aggregation parfactor to standard parfactors
		Distribution converted = ((AggregationParfactor) gs).toStdParfactors();
		
		// Gets the converted parfactor that contains the counting formula
		Prv attends = network.prv("attends ( Person )");
		for (Parfactor p : converted) {
			if (!p.contains(attends)) {
				gs = p;
			}
		}
		
		// Sum out attends
		LogicalVariable person = network.lv("Person");
		attends = CountingFormula.getInstance(person, attends);
		Parfactor afterSumOutAttends = afterSumOutHot.multiply(gs).sumOut(attends);
		
		Parfactor expected = afterSumOutAttends;
		
		// Compares expected with result
		assertEquals(expected, result);
	}
}
