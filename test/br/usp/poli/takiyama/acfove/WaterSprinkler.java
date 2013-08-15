package br.usp.poli.takiyama.acfove;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.ConstantFactor;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.utils.Example;
import br.usp.poli.takiyama.utils.Lists;

/**
 * Temporary class to test the water sprinker network.
 * This test should go to ACFOVETest junit test.
 * @author ftakiyama
 *
 */
public class WaterSprinkler {
	/**
	 * Network: water sprinkler
	 * Query: wet_grass(Lot)
	 * Evidence: none
	 * Population size: n
	 */
	@Test
	public void testOnWaterSprinklerNetworkWithCountingFormula() {
		
		// Network initialization
		
		int domainSize = 30;
		Example network = Example.waterSprinklerNetWork(domainSize);
		
		Parfactor gc = network.parfactor("gcloudy");
		Parfactor gr = network.parfactor("grain");
		Parfactor gs = network.parfactor("gsprinkler");
		Parfactor gw = network.parfactor("gwetgrass");

		
		// Runs AC-FOVE on the network
		
		Prv wetgrass = network.prv("wet_grass ( Lot )");
		RandomVariableSet query = RandomVariableSet.getInstance(wetgrass, new HashSet<Constraint>(0));
		
		Marginal input = new StdMarginalBuilder(4).parfactors(gc, gr, gs, gw).preservable(query).build();

		ACFOVE acfove = new LoggedACFOVE(input);
		Parfactor result = acfove.run();
		
		
		// Calculates the correct result
		
		Prv sprinkler = network.prv("sprinkler ( Lot )");
		Parfactor afterSumOutSprinkler = gs.multiply(gw).sumOut(sprinkler);
		
		LogicalVariable lot = network.lv("Lot");
		Parfactor afterCountingLot = afterSumOutSprinkler.count(lot);

		Prv rain = network.prv("rain ( )");
		Parfactor afterSumOutRain = afterCountingLot.multiply(gr).sumOut(rain);

		Prv cloudy = network.prv("cloudy ( )");
		Parfactor afterSumOutCloudy = afterSumOutRain.multiply(gc).sumOut(cloudy);

		/*
		 * Turns out the algorithm decided to expand wet_grass when it was not
		 * necessary. This happens when domainSize < 5, where expanding Lot
		 * has a smaller cost than counting Lot.
		 * 
		 * The code below recreates parfactor afterSumOutCloudy 
		 * in a more convenient manner so I can compare answers.
		 * 
		 * It was created for domainSize = 3, but it can be adapted for any
		 * size < 5. Be aware to comment this code when changing domainSize to
		 * something greater, since the operations below are very expensive.
		 */
		
//		Marginal output = new StdMarginalBuilder(1).parfactors(afterSumOutCloudy).preservable(query).build();
//		Prv wetGrassExpanded = CountingFormula.getInstance(lot, wetgrass);
//		Parfactor expectedExpanded = new FullExpand(output, afterSumOutCloudy, wetGrassExpanded).run().iterator().next();
//		
//		List<Prv> referenceVars = Lists.listOf(
//				StdPrv.getBooleanInstance("wet_grass", lot.population().individualAt(1)),
//				StdPrv.getBooleanInstance("wet_grass", lot.population().individualAt(2)),
//				StdPrv.getBooleanInstance("wet_grass", lot.population().individualAt(0)));
//		Factor reference = ConstantFactor.getInstance(referenceVars);
		
		//Parfactor reordered = new StdParfactorBuilder().factor(expectedExpanded.factor().reorder(reference)).build();
		
		//assertThat(result, either(is(afterSumOutCloudy)).or(is(expectedExpanded)));//.or(is(reordered)));
		
		assertThat(result, is(afterSumOutCloudy));
	}
}
