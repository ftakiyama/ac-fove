package br.usp.poli.takiyama.acfove;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Example;


public class WaterSprinkler2 {
	/**
	 * Network: water sprinkler
	 * Query: sprinkler(lot0)
	 * Evidence: wet_grass(lot0) = true
	 * Population size: 1
	 * 
	 * Even though the answer is consistent, the real probability is obtained
	 * by dividing the result by the normalizing constant. I thought AC-FOVE
	 * results did not need this kind of correction. Is it correct?
	 * 
	 * TODO Check the need for normalizing constants
	 */
	@Test
	public void querySprinklerGivenWetGrass() {
		
		// Network initialization
		int domainSize = 1;
		Example network = Example.waterSprinklerNetWork(domainSize);
		
		Parfactor gc = network.parfactor("gcloudy");
		Parfactor gr = network.parfactor("grain");
		Parfactor gs = network.parfactor("gsprinkler");
		Parfactor gw = network.parfactor("gwetgrass");
		
		// Evidence
		LogicalVariable lot = network.lv("Lot");
		Term lot0 = lot.population().individualAt(0);
		Substitution lot_lot0 = Substitution.getInstance(Binding.getInstance(lot, lot0));
		Prv wetgrass = network.prv("wet_grass ( Lot )").apply(lot_lot0);
		Parfactor evidence = new StdParfactorBuilder().variables(wetgrass).values(0.0, 1.0).build();

		// Query
		Prv sprinkler = network.prv("sprinkler ( Lot )").apply(lot_lot0);
		RandomVariableSet query = RandomVariableSet.getInstance(sprinkler, new HashSet<Constraint>(0));
		
		// Input marginal
		Marginal input = new StdMarginalBuilder(5).parfactors(gc, gr, gs, gw, evidence).preservable(query).build();

		// Runs AC-FOVE on input marginal
		ACFOVE acfove = new LoggedACFOVE(input);
		Parfactor result = acfove.run();
		
		// Calculates the correct result
		gs = gs.apply(lot_lot0);
		gw = gw.apply(lot_lot0);
		
		Factor afterSumOutWetGrass = evidence.factor().multiply(gw.factor()).sumOut(wetgrass);
		
		Prv rain = network.prv("rain ( )");
		Factor afterSumOutRain = afterSumOutWetGrass.multiply(gr.factor()).sumOut(rain);
		
		Prv cloudy = network.prv("cloudy ( )");
		Factor afterSumOutCloudy = afterSumOutRain.multiply(gc.factor()).multiply(gs.factor()).sumOut(cloudy);
		
		Parfactor expected = new StdParfactorBuilder().factor(afterSumOutCloudy).build();
		
		// Compares expected with result
		assertEquals(expected, result);
	}
}
