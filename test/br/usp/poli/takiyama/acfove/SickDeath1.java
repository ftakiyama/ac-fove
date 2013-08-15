package br.usp.poli.takiyama.acfove;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.utils.Example;


public class SickDeath1 {
	/**
	 * Network: sick and death (Braz 2005)
	 * Query: someDeath
	 * Evidence: none
	 * Population size: 10
	 * 
	 */
	@Test
	public void querySomeDeath() {
		
		// Network initialization
		int domainSize = 10;
		Example network = Example.sickDeathNetwork(domainSize);
		
		Parfactor ge = network.parfactor("gepidemic");
		Parfactor gs = network.parfactor("gsick");
		Parfactor gd = network.parfactor("gdeath");
		Parfactor gsd = network.parfactor("gsomedeath");

		// Query
		Prv someDeath = network.prv("someDeath ( )");
		RandomVariableSet query = RandomVariableSet.getInstance(someDeath, new HashSet<Constraint>(0));
		
		// Input marginal
		Marginal input = new StdMarginalBuilder(5).parfactors(ge, gs, gd, gsd).preservable(query).build();

		// Runs AC-FOVE on input marginal
		ACFOVE acfove = new LoggedACFOVE(input);
		Parfactor result = acfove.run();
		
		// Calculates the correct result
		
		// Sum out sick
		Prv sick = network.prv("sick ( Person )");
		Parfactor afterSumOutSick = gs.multiply(gd).sumOut(sick);
		
		// Sum out death
		Prv death = network.prv("death ( Person )");
		Parfactor afterSumOutDeath = afterSumOutSick.multiply(gsd).sumOut(death);
		
		// Sum out epidemic
		Prv epidemic = network.prv("epidemic ( )");
		Parfactor afterSumOutEpidemic = afterSumOutDeath.multiply(ge).sumOut(epidemic);
		
		Parfactor expected = afterSumOutEpidemic;
		
		// Compares expected with result
		assertEquals(expected, result);
	}
}
