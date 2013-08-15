package br.usp.poli.takiyama.acfove;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Example;


public class WaterSprinkler3 {
	/**
	 * Network: Water Sprinkler
	 * Query: sprinkler(lot0)
	 * Evidence: wet_grass(lot0) = true
	 * Population size: 10
	 */
	@Test
	public void querySprinklerGivenWetGrass() {
		
		// Network initialization
		int domainSize = 100;
		Example network = Example.waterSprinklerNetWork(domainSize);
		
		Parfactor gc = network.parfactor("gcloudy");
		Parfactor gr = network.parfactor("grain");
		Parfactor gs = network.parfactor("gsprinkler");
		Parfactor gw = network.parfactor("gwetgrass");
		
		// Evidence
		LogicalVariable lot = network.lv("Lot");
		Term lot0 = lot.population().individualAt(0);
		Substitution lot_lot0 = Substitution.getInstance(Binding.getInstance(lot, lot0));
		Prv wetgrass_lot0 = network.prv("wet_grass ( Lot )").apply(lot_lot0);
		Parfactor evidence = new StdParfactorBuilder().variables(wetgrass_lot0).values(0.0, 1.0).build();

		// Query
		Prv sprinkler_lot0 = network.prv("sprinkler ( Lot )").apply(lot_lot0);
		RandomVariableSet query = RandomVariableSet.getInstance(sprinkler_lot0, new HashSet<Constraint>(0));
		
		// Input marginal
		Marginal input = new StdMarginalBuilder(5).parfactors(gc, gr, gs, gw, evidence).preservable(query).build();

		// Runs AC-FOVE on input marginal
		ACFOVE acfove = new LoggedACFOVE(input);
		Parfactor result = acfove.run();
		
		// Calculates the correct result
		
		// Splits the marginal on the evidence and the query
		Parfactor g1 = gc;
		Parfactor g2 = gr;
		SplitResult splitSprinkler = gs.splitOn(lot_lot0);
		Parfactor g3 = splitSprinkler.residue().iterator().next();
		Parfactor g3_0 = splitSprinkler.result();
		SplitResult splitWetGrass = gw.splitOn(lot_lot0);
		Parfactor g4 = splitWetGrass.residue().iterator().next();
		Parfactor g4_0 = splitWetGrass.result();
		Parfactor g5 = evidence;
		
		// sum out wet_grass(Lot):{Lot!=lot0}
		Prv wetgrass = network.prv("wet_grass ( Lot )");
		Parfactor afterSumOutWetGrass = g4.sumOut(wetgrass);

		// sum out sprinkler(Lot):{Lot!=lot0}
		Prv sprinkler = network.prv("sprinkler ( Lot )");
		Parfactor afterSumOutSprinkler = afterSumOutWetGrass.multiply(g3).sumOut(sprinkler);
		
		// sum out wet_grass(lot0)
		Parfactor afterSumOutWetGrassLot0 = g5.multiply(g4_0).sumOut(wetgrass_lot0);
		
		// sum out rain()
		Prv rain = network.prv("rain ( )");
		Parfactor afterSumOutRain = afterSumOutWetGrassLot0.multiply(afterSumOutSprinkler).multiply(g2).sumOut(rain);
		
		// sum out cloudy()
		Prv cloudy = network.prv("cloudy ( )");
		Parfactor afterSumOutCloudy = afterSumOutRain.multiply(g1).multiply(g3_0).sumOut(cloudy);
		
		/*
		 * The parfactor afterSumOutCloudy contains the constraint Lot != lot1,
		 * which does not make sense in the parfactor since it does not contain
		 * the logical variable Lot. 
		 * This a quick fix to recreate the parfactor without this constraint.
		 */
//		Parfactor expected = new StdParfactorBuilder().factor(afterSumOutCloudy.factor()).build();
		
		// Compares expected with result
		assertEquals(afterSumOutCloudy, result);
	}
}
