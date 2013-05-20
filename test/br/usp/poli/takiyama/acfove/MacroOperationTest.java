package br.usp.poli.takiyama.acfove;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import br.usp.poli.takiyama.cfove.StdParfactor;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Distribution;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdDistribution;
import br.usp.poli.takiyama.common.UnconstrainedMarginal;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;

@RunWith(Enclosed.class)
public class MacroOperationTest {

	public static class ShatterTest {
		
		@Test
		public void testShatter() {
			LogicalVariable lot = StdLogicalVariable.getInstance("Lot", "lot", 15);
			Constant lot1 = Constant.getInstance("lot1");
			
			Prv rain = StdPrv.getBooleanInstance("rain");
			Prv sprinkler = StdPrv.getBooleanInstance("sprinkler", lot);
			Prv wet_grass = StdPrv.getBooleanInstance("wet_grass", lot);
			Prv wet_grass_lot1 = StdPrv.getBooleanInstance("wet_grass", lot1);
			Prv sprinkler_lot1 = StdPrv.getBooleanInstance("sprinkler", lot1);
			
			Constraint lot_lot1 = InequalityConstraint.getInstance(lot, lot1);
			
			double [] f1 = {0.8, 0.2};
			double [] f2 = {0.6, 0.4};
			double [] f3 = {1.0, 0.0, 0.2, 0.8, 0.1, 0.9, 0.01, 0.99};
			double [] f4 = {0, 1};
			
			Parfactor g1 = new StdParfactorBuilder().variables(rain).values(f1).build();
			Parfactor g2 = new StdParfactorBuilder().variables(sprinkler).values(f2).build();
			Parfactor g3 = new StdParfactorBuilder().variables(rain, sprinkler, wet_grass).values(f3).build();
			Parfactor g4 = new StdParfactorBuilder().variables(wet_grass_lot1).values(f4).build();
			Parfactor g5 = new StdParfactorBuilder().variables(rain, sprinkler_lot1, wet_grass_lot1).values(f3).build();
			Parfactor g6 = new StdParfactorBuilder().variables(rain, sprinkler, wet_grass).constraints(lot_lot1).values(f3).build();
			Parfactor g7 = new StdParfactorBuilder().variables(sprinkler_lot1).values(f2).build();
			Parfactor g8 = new StdParfactorBuilder().variables(sprinkler).constraints(lot_lot1).values(f2).build();
			
			Distribution initial = StdDistribution.of(g1, g2, g3, g4);
			Marginal<Prv> marginal = UnconstrainedMarginal.getInstance();
			marginal = marginal.addAll(initial);
			
			MacroOperation shatter = new Shatter(marginal);
			shatter.run();
			
			Distribution result = shatter.distribution();
			Distribution expected = StdDistribution.of(g1, g4, g5, g6, g7, g8);
			
			assertEquals(expected, result);
		}
	}
}
