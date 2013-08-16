package br.usp.poli.takiyama.acfove;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import br.usp.poli.takiyama.acfove.AggParfactor.AggParfactorBuilder;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.InputOutput;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdFactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.common.Tuple;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Bool;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Or;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.RangeElement;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.samples.WaterSprinklerNetwork;
import br.usp.poli.takiyama.utils.Example;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.MathUtils;
import br.usp.poli.takiyama.utils.Sets;
import br.usp.poli.takiyama.utils.TestUtils;


@RunWith(Enclosed.class)
public class ACFOVETest {
	
	private static class Utils {
		private static List<BigDecimal> toBigDecimalList(double ... list) {
			List<BigDecimal> result = new ArrayList<BigDecimal>(list.length);
			for (int i = 0; i < list.length; i++) {
				result.add(BigDecimal.valueOf(list[i]));
			}
			return result;
		}
	}
	
	@Ignore("Early debug sessions >> fourth step is not correct")
	@RunWith(Parameterized.class)
	public static class StepByStepExampleComputation {
		
		private static List<BigDecimal> toBigDecimalList(double ... list) {
			List<BigDecimal> result = new ArrayList<BigDecimal>(list.length);
			for (int i = 0; i < list.length; i++) {
				result.add(BigDecimal.valueOf(list[i]));
			}
			return result;
		}
		
		@Parameters
		public static Collection<Object[]> data() {
			int populationSize = 10;
			
			LogicalVariable lot = StdLogicalVariable.getInstance("Lot", "lot", populationSize);
			
			Constant lot1 = Constant.getInstance("lot1");
		
			Constraint lot_lot1 = InequalityConstraint.getInstance(lot, lot1);
			
			Prv rain = StdPrv.getBooleanInstance("rain");
			Prv sprinkler = StdPrv.getBooleanInstance("sprinkler", lot);
			Prv sprinkler_lot1 = StdPrv.getBooleanInstance("sprinkler", lot1);
			Prv wet_grass = StdPrv.getBooleanInstance("wet_grass", lot);
			Prv wet_grass_lot1 = StdPrv.getBooleanInstance("wet_grass", lot1);
			Prv formula = CountingFormula.getInstance(lot, wet_grass, lot_lot1);
			
			List<BigDecimal> f1 = toBigDecimalList(0.8, 0.2);
			List<BigDecimal> f2 = toBigDecimalList(0.6, 0.4);
			List<BigDecimal> f3 = toBigDecimalList(1.0, 0.0, 0.2, 0.8, 0.1, 0.9, 0.01, 0.99);
			List<BigDecimal> f4 = toBigDecimalList(0.0, 1.0);
			
			List<BigDecimal> f2xf3 = new ArrayList<BigDecimal>(8);
			for (int i = 0; i < 8; i++) {
				f2xf3.add(f2.get((i / 2) % 2).multiply(f3.get(i)));
			}
			
			List<BigDecimal> f5 = new ArrayList<BigDecimal>(4);
			f5.add(f2xf3.get(0).add(f2xf3.get(2)));
			f5.add(f2xf3.get(1).add(f2xf3.get(3)));
			f5.add(f2xf3.get(4).add(f2xf3.get(6)));
			f5.add(f2xf3.get(5).add(f2xf3.get(7)));
			
			List<BigDecimal> f4xf5 = new ArrayList<BigDecimal>(4);
			for (int i = 0; i < 4; i++) {
				f4xf5.add(f4.get(i % 2).multiply(f5.get(i)));
			}
			
			List<BigDecimal> f6 = new ArrayList<BigDecimal>(2);
			f6.add(f4xf5.get(0).add(f4xf5.get(1)));
			f6.add(f4xf5.get(2).add(f4xf5.get(3)));
			
			int n = populationSize;
			List<BigDecimal> f7 = new ArrayList<BigDecimal>(2 * n);
			for (int i = 0; i < n; i++) {
				f7.add(MathUtils.pow(f5.get(0), n - i - 1, 1).multiply(MathUtils.pow(f5.get(1), i, 1)));
			}
			for (int i = 0; i < n; i++) {
				f7.add(MathUtils.pow(f5.get(2), n - i - 1, 1).multiply(MathUtils.pow(f5.get(3), i, 1)));
			}
			
			List<BigDecimal> f1xf6xf7 = new ArrayList<BigDecimal>(2 * n);
			for (int i = 0; i < 2 * n; i++) {
				f1xf6xf7.add(f1.get(i / n).multiply(f6.get(i / n).multiply(f7.get(i))));
			}
			
			List<BigDecimal> f8 = new ArrayList<BigDecimal>(n);
			for (int i = 0; i < n; i++) {
				f8.add(f1xf6xf7.get(i).add(f1xf6xf7.get(n + i)));
			}
			
			/**
			 * Alternative path
			 * 
			 * When processing set Phi 2, we can either eliminate 
			 * wet_grass(lot1) or sprinkler(lot1).
			 * The order in which it is done does not affect final result,
			 * but affects these individual tests. Kisynski opted to eliminate
			 * sprinkler(lot1) first.
			 * Below I assemble the case when wet_grass(lot1) is eliminated
			 * first. This changes set Phi 3, after that the intermediate
			 * results remain the same.
			 */
			
			List<BigDecimal> f3xf4 = new ArrayList<BigDecimal>(8);
			for (int i = 0; i < 8; i++) {
				f3xf4.add(f4.get(i % 2).multiply(f3.get(i)));
			}
			
			f3xf4.add(f4.get(0).multiply(f3.get(0)));
			f3xf4.add(f4.get(0).multiply(f3.get(1)));
			f3xf4.add(f4.get(1).multiply(f3.get(2)));
			f3xf4.add(f4.get(1).multiply(f3.get(3)));
			f3xf4.add(f4.get(0).multiply(f3.get(4)));
			f3xf4.add(f4.get(0).multiply(f3.get(5)));
			f3xf4.add(f4.get(1).multiply(f3.get(6)));
			f3xf4.add(f4.get(1).multiply(f3.get(7)));
			
			List<BigDecimal> f5alt = new ArrayList<BigDecimal>(4);
			f5alt.add(f3xf4.get(0).add(f3xf4.get(1)));
			f5alt.add(f3xf4.get(2).add(f3xf4.get(3)));
			f5alt.add(f3xf4.get(4).add(f3xf4.get(5)));
			f5alt.add(f3xf4.get(6).add(f3xf4.get(7)));
						
			Parfactor g1 = new StdParfactorBuilder().variables(rain).values(f1).build();
			Parfactor g2 = new StdParfactorBuilder().variables(sprinkler).values(f2).build();
			Parfactor g3 = new StdParfactorBuilder().variables(rain, sprinkler, wet_grass).values(f3).build();
			Parfactor g4 = new StdParfactorBuilder().variables(wet_grass_lot1).values(f4).build();
			Parfactor g5 = new StdParfactorBuilder().variables(rain, sprinkler_lot1, wet_grass_lot1).values(f3).build();
			Parfactor g6 = new StdParfactorBuilder().variables(rain, sprinkler, wet_grass).values(f3).constraints(lot_lot1).build();
			Parfactor g7 = new StdParfactorBuilder().variables(sprinkler_lot1).values(f2).build();
			Parfactor g8 = new StdParfactorBuilder().variables(sprinkler).values(f2).constraints(lot_lot1).build();
			Parfactor g9 = new StdParfactorBuilder().variables(rain, wet_grass).values(f5).constraints(lot_lot1).build();
			Parfactor g10 = new StdParfactorBuilder().variables(rain, wet_grass_lot1).values(f5).build();
			Parfactor g10alt = new StdParfactorBuilder().variables(rain, sprinkler_lot1).values(f5alt).build();
			Parfactor g11 = new StdParfactorBuilder().variables(rain).values(f6).build();
			Parfactor g12 = new StdParfactorBuilder().variables(rain, formula).values(f7).build();
			Parfactor g13 = new StdParfactorBuilder().variables(formula).values(f8).build();
			
			Set<Constraint> constraints = Sets.setOf(lot_lot1);
			RandomVariableSet query = RandomVariableSet.getInstance(wet_grass, constraints);
			
			InputOutput<Marginal, Marginal> inOut = InputOutput.getInstance();
			Marginal input, output;
			
			// first step
//			input = new StdMarginalBuilder(4).parfactors(g1, g2, g3, g4).preservable(query).build();
//			output = new StdMarginalBuilder(6).parfactors(g1, g4, g5, g6, g7, g8).preservable(query).build();
//			inOut.add(input, output);
			
			// second step - sums out ground(sprinkler(Lot)): {Lot != lot1} from g8
			input = new StdMarginalBuilder(6).parfactors(g1, g4, g5, g6, g7, g8).preservable(query).build();
			output = new StdMarginalBuilder(5).parfactors(g1, g4, g5, g7, g9).preservable(query).build();
			inOut.add(input, output);
			
			// third step - sums out ground(sprinkler(lot1) from g5 x g7
			input = new StdMarginalBuilder(6).parfactors(g1, g4, g5, g7, g9).preservable(query).build();
			// turns out my algorithm chose the alternative path....
			output = new StdMarginalBuilder(4).parfactors(g1, g4, g9, g10).preservable(query).build();
//			output = new StdMarginalBuilder(4).parfactors(g1, g7, g9, g10alt).preservable(query).build();
			inOut.add(input, output);

			// fourth step
			input = new StdMarginalBuilder(4).parfactors(g1, g4, g9, g10).preservable(query).build();
			output = new StdMarginalBuilder(3).parfactors(g1, g9, g11).preservable(query).build();
			inOut.add(input, output);
			
			// fifth step
			input = new StdMarginalBuilder(3).parfactors(g1, g9, g11).preservable(query).build();
			output = new StdMarginalBuilder(3).parfactors(g1, g11, g12).preservable(query).build();
			inOut.add(input, output);
			
			// sixth step
			input = new StdMarginalBuilder(3).parfactors(g1, g11, g12).preservable(query).build();
			output = new StdMarginalBuilder(1).parfactors(g13).preservable(query).build();
			inOut.add(input, output);
			
			// all steps 
//			input = new StdMarginalBuilder(4).parfactors(g1, g2, g3, g4).preservable(query).build();
//			output = new StdMarginalBuilder(1).parfactors(g13).preservable(query).build();
//			inOut.add(input, output);
			
			return inOut.toCollection();
		}
		
		private Marginal input;
		private Marginal expected;
		
		public StepByStepExampleComputation(Marginal input, Marginal expected) {
			this.input = input;
			this.expected = expected;
		}
		
		@Test
		public void testStep() {
			ACFOVE acfove = new ACFOVE(input);
			Marginal result = acfove.runStep();
			assertEquals(expected, result);
		}
	}
	
	public static class ExampleComputation {

		private static List<BigDecimal> toBigDecimalList(double ... list) {
			List<BigDecimal> result = new ArrayList<BigDecimal>(list.length);
			for (int i = 0; i < list.length; i++) {
				result.add(BigDecimal.valueOf(list[i]));
			}
			return result;
		}
		

		// TODO: remove repeted code
		@Ignore(" while testing smaller features")
		@Test
		public void testExampleComputation() {
			int populationSize = 10;
			
			LogicalVariable lot = StdLogicalVariable.getInstance("Lot", "lot", populationSize);
			
			Constant lot1 = Constant.getInstance("lot1");
		
			Constraint lot_lot1 = InequalityConstraint.getInstance(lot, lot1);
			
			Prv rain = StdPrv.getBooleanInstance("rain");
			Prv sprinkler = StdPrv.getBooleanInstance("sprinkler", lot);
			Prv wet_grass = StdPrv.getBooleanInstance("wet_grass", lot);
			Prv wet_grass_lot1 = StdPrv.getBooleanInstance("wet_grass", lot1);
			Prv formula = CountingFormula.getInstance(lot, wet_grass, lot_lot1);
			
			List<BigDecimal> f1 = toBigDecimalList(0.8, 0.2);
			List<BigDecimal> f2 = toBigDecimalList(0.6, 0.4);
			List<BigDecimal> f3 = toBigDecimalList(1.0, 0.0, 0.2, 0.8, 0.1, 0.9, 0.01, 0.99);
			List<BigDecimal> f4 = toBigDecimalList(0.0, 1.0);
			
			List<BigDecimal> f2xf3 = new ArrayList<BigDecimal>(8);
			for (int i = 0; i < 8; i++) {
				f2xf3.add(f2.get((i / 2) % 2).multiply(f3.get(i)));
			}
			
			List<BigDecimal> f5 = new ArrayList<BigDecimal>(4);
			f5.add(f2xf3.get(0).add(f2xf3.get(2)));
			f5.add(f2xf3.get(1).add(f2xf3.get(3)));
			f5.add(f2xf3.get(4).add(f2xf3.get(6)));
			f5.add(f2xf3.get(5).add(f2xf3.get(7)));
			
			List<BigDecimal> f4xf5 = new ArrayList<BigDecimal>(4);
			for (int i = 0; i < 4; i++) {
				f4xf5.add(f4.get(i % 2).multiply(f5.get(i)));
			}
			
			List<BigDecimal> f6 = new ArrayList<BigDecimal>(2);
			f6.add(f4xf5.get(0).add(f4xf5.get(1)));
			f6.add(f4xf5.get(2).add(f4xf5.get(3)));
			
			int n = populationSize;
			List<BigDecimal> f7 = new ArrayList<BigDecimal>(2 * n);
			for (int i = 0; i < n; i++) {
				f7.add(MathUtils.pow(f5.get(0), n - i - 1, 1).multiply(MathUtils.pow(f5.get(1), i, 1)));
			}
			for (int i = 0; i < n; i++) {
				f7.add(MathUtils.pow(f5.get(2), n - i - 1, 1).multiply(MathUtils.pow(f5.get(3), i, 1)));
			}
			
			List<BigDecimal> f1xf6xf7 = new ArrayList<BigDecimal>(2 * n);
			for (int i = 0; i < 2 * n; i++) {
				f1xf6xf7.add(f1.get(i / n).multiply(f6.get(i / n).multiply(f7.get(i))));
			}
			
			List<BigDecimal> f8 = new ArrayList<BigDecimal>(n);
			for (int i = 0; i < n; i++) {
				f8.add(f1xf6xf7.get(i).add(f1xf6xf7.get(n + i)));
			}
						
			Parfactor g1 = new StdParfactorBuilder().variables(rain).values(f1).build();
			Parfactor g2 = new StdParfactorBuilder().variables(sprinkler).values(f2).build();
			Parfactor g3 = new StdParfactorBuilder().variables(rain, sprinkler, wet_grass).values(f3).build();
			Parfactor g4 = new StdParfactorBuilder().variables(wet_grass_lot1).values(f4).build();
			Parfactor g13 = new StdParfactorBuilder().variables(formula).values(f8).build();
			
			Set<Constraint> constraints = Sets.setOf(lot_lot1);
			RandomVariableSet query = RandomVariableSet.getInstance(wet_grass, constraints);
			
			Marginal input = new StdMarginalBuilder(4).parfactors(g1, g2, g3, g4).preservable(query).build();
			
			ACFOVE acfove = new LoggedACFOVE(input);
			Parfactor result = acfove.run();
			
			Parfactor expected = g13;
			
			assertEquals(expected, result);
			
		}
	}

	@RunWith(Parameterized.class)
	public static class StepByStepExampleComputationWithAggregation {
		@Parameters
		public static Collection<Object[]> data() {
			
			int populationSize = 5;
			
			LogicalVariable person = StdLogicalVariable.getInstance("Person", "x", populationSize);
			
			Prv big_jackpot = StdPrv.getBooleanInstance("big_jackpot");
			Prv played = StdPrv.getBooleanInstance("played", person);
			Prv matched_6 = StdPrv.getBooleanInstance("matched_6", person);
			Prv jackpot_won = StdPrv.getBooleanInstance("jackpot_won");
			
			List<BigDecimal> fBigJackpot = Utils.toBigDecimalList(0.8, 0.2);
			List<BigDecimal> fPlayed = Utils.toBigDecimalList(0.95, 0.05, 0.85, 0.15);
			List<BigDecimal> fMatched6 = Utils.toBigDecimalList(1.0, 0.0, 0.99999993, 0.00000007);
			List<BigDecimal> fJackpotWonPrime = Utils.toBigDecimalList(0.999999975, 0.000000025);
			
			List<BigDecimal> fPlayedxfMatched6 = new ArrayList<BigDecimal>(8);
			for (int i = 0; i < 8; i++) {
				fPlayedxfMatched6.add(fPlayed.get(i / 2).multiply(fMatched6.get(i % 4)));
			}
			
			List<BigDecimal> fMatched6Prime = new ArrayList<BigDecimal>(4);
			fMatched6Prime.add(fPlayedxfMatched6.get(0).add(fPlayedxfMatched6.get(2)));
			fMatched6Prime.add(fPlayedxfMatched6.get(1).add(fPlayedxfMatched6.get(3)));
			fMatched6Prime.add(fPlayedxfMatched6.get(4).add(fPlayedxfMatched6.get(6)));
			fMatched6Prime.add(fPlayedxfMatched6.get(5).add(fPlayedxfMatched6.get(7)));
			
			double [] fJackpotWon = {
					0.9999999825,
					0.9999999450,
					0.0000000175,
					0.0000000550
			};
			
			Parfactor g1 = new StdParfactorBuilder().variables(big_jackpot).values(fBigJackpot).build();
			Parfactor g2 = new StdParfactorBuilder().variables(big_jackpot, played).values(fPlayed).build();
			Parfactor g3 = new StdParfactorBuilder().variables(played, matched_6).values(fMatched6).build();
			Parfactor g4 = new AggParfactorBuilder(matched_6, jackpot_won, Or.OR).context(big_jackpot).build();
			Parfactor g5 = new StdParfactorBuilder().variables(big_jackpot, matched_6).values(fMatched6Prime).build();
			Parfactor g7 = new StdParfactorBuilder().variables(big_jackpot, jackpot_won).values(fJackpotWon).build();
			Parfactor g8 = new StdParfactorBuilder().variables(jackpot_won).values(fJackpotWonPrime).build();
			
			RandomVariableSet query = RandomVariableSet.getInstance(played, Sets.<Constraint>getInstance(0));
			
			InputOutput<Marginal, Marginal> inOut = InputOutput.getInstance();
			Marginal input, output;
			
			// first step - multiplies g2 by g3 and sums out played(Person)
			input = new StdMarginalBuilder(4).parfactors(g1, g2, g3, g4).preservable(query).build();
			output = new StdMarginalBuilder(3).parfactors(g1, g4, g5).preservable(query).build();
			inOut.add(input, output);
			
			// second step - multiplies g4 by g5 and sums out matched_6(Person)
			input = new StdMarginalBuilder(3).parfactors(g1, g4, g5).preservable(query).build();
			output = new StdMarginalBuilder(2).parfactors(g1, g7).preservable(query).build();
			inOut.add(input, output);
			
			// third step - sums out big_jackpot()
			input = new StdMarginalBuilder(2).parfactors(g1, g7).preservable(query).build();
			output = new StdMarginalBuilder(1).parfactors(g8).preservable(query).build();
			inOut.add(input, output);
			
			return inOut.toCollection();
		}
		
		private Marginal input;
		private Marginal expected;
		
		public StepByStepExampleComputationWithAggregation(Marginal input, Marginal expected) {
			this.input = input;
			this.expected = expected;
		}
		
		@Test
		@Ignore("Activate when aggregation parfactors are no longer converted in the beginning of the algorithm")
		public void testStep() {
			ACFOVE acfove = new ACFOVE(input);
			Marginal result = acfove.runStep();
			assertEquals(expected, result);
		}
	}
	
	/**
	 * Tests for AC-FOVE algorithm using example 3.14 of Kisynski (2010).
	 * These tests assume that all aggregation parfactors are converted to
	 * standard parfactors in the beginning of the algorithm. This is a simpler,
	 * less efficient approach.
	 */
	@RunWith(Parameterized.class)
	public static class AggregationExampleWithConversion {
		
		@Parameters
		public static Collection<Object[]> data() {
			
			int populationSize = 4;
			InputOutput<Marginal, Parfactor> inOut = InputOutput.getInstance();
			
			for (int n = 1; n <= populationSize; n++) {
			
				LogicalVariable person = StdLogicalVariable.getInstance("Person", "x", n);
				
				Prv big_jackpot = StdPrv.getBooleanInstance("big_jackpot");
				Prv played = StdPrv.getBooleanInstance("played", person);
				Prv matched_6 = StdPrv.getBooleanInstance("matched_6", person);
				Prv jackpot_won = StdPrv.getBooleanInstance("jackpot_won");
				
				List<BigDecimal> fBigJackpot = Utils.toBigDecimalList(0.8, 0.2);
				List<BigDecimal> fPlayed = Utils.toBigDecimalList(
						0.95, 
						0.05, 
						0.85, 
						0.15);
				List<BigDecimal> fMatched6 = Utils.toBigDecimalList(
						1.0, 
						0.0, 
						0.99999993, 
						0.00000007);
				List<BigDecimal> temp = Utils.toBigDecimalList(
						0.9999999965, 
						0.0000000035, 
						0.9999999895, 
						0.0000000105);
				
				// 0.8 * 0.9999999965^n + 0.2 * 0.9999999895^n
				BigDecimal r0 = fBigJackpot.get(0).multiply(MathUtils.pow(temp.get(0), n, 1)).add(fBigJackpot.get(1).multiply(MathUtils.pow(temp.get(2), n, 1)));
				
				// expression is too complicated to put in one line >8O
				//BigDecimal r1 = fBigJackpot.get(0).multiply(getSum(temp.get(0), temp.get(1), n)).add(fBigJackpot.get(1).multiply(getSum(temp.get(2), temp.get(3), n)));
				
				// Hey, this is much easier:
				BigDecimal r1 = BigDecimal.ONE.subtract(r0);
				
				List<BigDecimal> fResult = Lists.listOf(r0, r1);
				
				Parfactor g1 = new StdParfactorBuilder().variables(big_jackpot).values(fBigJackpot).build();
				Parfactor g2 = new StdParfactorBuilder().variables(big_jackpot, played).values(fPlayed).build();
				Parfactor g3 = new StdParfactorBuilder().variables(played, matched_6).values(fMatched6).build();
				Parfactor g4 = new AggParfactorBuilder(matched_6, jackpot_won, Or.OR).context(big_jackpot).build();
				
				RandomVariableSet query = RandomVariableSet.getInstance(jackpot_won, Sets.<Constraint>getInstance(0));
				Marginal input = new StdMarginalBuilder(4).parfactors(g1, g2, g3, g4).preservable(query).build();
				
				Parfactor expected = new StdParfactorBuilder().variables(jackpot_won).values(fResult).build();
				
				inOut.add(input, expected);
			}
			
			return inOut.toCollection();
		}
		
		/**
		 * Returns 
		 * &sum;<sub>n</sub>a<sub>0</sub><sup>n-i</sup>.a<sub>1</sub><sup>i</sup>
		 */
		private static BigDecimal getSum(BigDecimal a0, BigDecimal a1, int n) {
			BigDecimal result = BigDecimal.ZERO;
			for (int i = 1; i <= n; i++) {
				result = result.add(MathUtils.pow(a0, n - i, 1).multiply(MathUtils.pow(a1, i, 1)));
			}
			return result;
		}
		
		private Marginal input;
		private Parfactor expected;
		
		public AggregationExampleWithConversion(Marginal input, Parfactor expected) {
			this.input = input;
			this.expected = expected;
		}
		
		@Test
		public void testAggregationExampleWithConversion() {
			ACFOVE acfove = new ACFOVE(input);
			Parfactor result = acfove.run();
			assertEquals(expected, result);
		}
	}

	public static class CorrectnessTest {

		@Ignore
		@Test
		public void testNodeWithCommonParent() {
			
			int domainSize = 3;
			
			LogicalVariable x = StdLogicalVariable.getInstance("X", "x", domainSize);
			
			Constant x1 = Constant.getInstance("x1");
			
			Prv b = StdPrv.getBooleanInstance("b");
			Prv r = StdPrv.getBooleanInstance("r", x);
			Prv r11 = StdPrv.getBooleanInstance("r", x1);
			
			List<BigDecimal> f1 = Utils.toBigDecimalList(0.2, 0.8);
			List<BigDecimal> f2 = Utils.toBigDecimalList(1.0, 0.0, 0.1, 0.9);
			
			Parfactor g1 = new StdParfactorBuilder().variables(b).values(f1).build();
			Parfactor g2 = new StdParfactorBuilder().variables(b, r).values(f2).build();
			
			RandomVariableSet query = RandomVariableSet.getInstance(b, Sets.<Constraint>getInstance(0));
			Marginal input = new StdMarginalBuilder(2).parfactors(g1, g2).preservable(query).build();
			
			Marginal groundedInput = propositionalizeAll(input);
			
//			ACFOVE ve = new ACFOVE(groundedInput);
//			Parfactor groundedResult = ve.run();
//			System.out.println(groundedResult);
			
			ACFOVE acfove = new LoggedACFOVE(input);
			Parfactor result = acfove.run();
			System.out.println(result);
			
		}
		
		@Ignore("First need to know how to eliminate variable from parfactor of type (a(X), b(Y))")
		@Test
		public void testNodesWithCommonParent() {
			
			int domainSize = 3;
			
			LogicalVariable x = StdLogicalVariable.getInstance("X", "x", domainSize);
			LogicalVariable y = StdLogicalVariable.getInstance("Y", "y", domainSize);
			
			Constant x1 = Constant.getInstance("x1");
			Constant y1 = Constant.getInstance("y1");
			
			Prv b = StdPrv.getBooleanInstance("b", y);
			Prv r = StdPrv.getBooleanInstance("r", x, y);
			Prv r11 = StdPrv.getBooleanInstance("r", x1, y1);
			
			List<BigDecimal> f1 = Utils.toBigDecimalList(0.2, 0.8);
			List<BigDecimal> f2 = Utils.toBigDecimalList(1.0, 0.0, 0.1, 0.9);
			
			Parfactor g1 = new StdParfactorBuilder().variables(b).values(f1).build();
			Parfactor g2 = new StdParfactorBuilder().variables(b, r).values(f2).build();
			
			RandomVariableSet query = RandomVariableSet.getInstance(r11, Sets.<Constraint>getInstance(0));
			Marginal input = new StdMarginalBuilder(2).parfactors(g1, g2).preservable(query).build();
			
			Marginal groundedInput = propositionalizeAll(input);
			
			System.out.println(g1.multiply(g2));
			
			ACFOVE ve = new ACFOVE(groundedInput);
			Parfactor groundedResult = ve.run();
			System.out.println(groundedResult);
			
			ACFOVE acfove = new LoggedACFOVE(input);
			Parfactor result = acfove.run();
			System.out.println(result);
			
		}
		
		private Marginal propositionalizeAll(Marginal m) {
			Marginal result = m;
			for (Parfactor p : m) {
				for (LogicalVariable v : p.logicalVariables()) {
					result = new Propositionalize(result, p, v).run();
				}
			}
			return result;
		}
		
		
		@Ignore("Test with Kisynski example first")
		@Test
		public void testExistsNodeManually() {
			
			int domainSize = 2;
			
			LogicalVariable x = StdLogicalVariable.getInstance("X", "x", domainSize);
			LogicalVariable y = StdLogicalVariable.getInstance("Y", "y", domainSize);
			
			Constant x1 = Constant.getInstance("x1");
			Constant y1 = Constant.getInstance("y1");
			
			Prv b = StdPrv.getBooleanInstance("b", y);
			Prv by = CountingFormula.getInstance(y, b);
			Prv r = StdPrv.getBooleanInstance("r", x, y);
			Prv a = StdPrv.getBooleanInstance("and", x, y);
			Prv e = StdPrv.getBooleanInstance("exists", x);
			Prv eaux = StdPrv.getBooleanInstance("exists_aux", x);
			Prv ex = CountingFormula.getInstance(x, e);
			
			List<BigDecimal> fb = Utils.toBigDecimalList(0.1, 0.9);
			List<BigDecimal> fr = Utils.toBigDecimalList(0.2, 0.8);
			List<BigDecimal> fand = Utils.toBigDecimalList(1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0);
			List<BigDecimal> f6 = Utils.toBigDecimalList(1.0, 1.0, 1.0, 0.0);
			List<BigDecimal> f7 = Utils.toBigDecimalList(1.0, 0.0, -1.0, 1.0);
			
			Parfactor g1 = new StdParfactorBuilder().variables(b).values(fb).build();
			Parfactor g2 = new StdParfactorBuilder().variables(r).values(fr).build();
			Parfactor g3 = new StdParfactorBuilder().variables(r, b, a).values(fand).build();
			Parfactor g4 = new AggParfactorBuilder(a, e, Or.OR).context(b).build();
						
			Parfactor g5 = g2.multiply(g3).sumOut(r);
			Parfactor g6 = g5.multiply(g1);
			Parfactor g7 = g6.multiply(g4).sumOut(a);
			Parfactor g8 = g7.count(x);
			Parfactor g9 = g8.sumOut(b);

			// Trying to use special agg parfactor conversion
//			Parfactor g5 = g2.multiply(g3).sumOut(r);
//			Parfactor g6 = new StdParfactorBuilder().variables(a, eaux).values(f6).build();
//			Parfactor g7 = new StdParfactorBuilder().variables(e, eaux).values(f7).build();
//			Parfactor g8 = g5.multiply(g6).sumOut(a);
//			Parfactor g9 = g1.multiply(g8).count(y);
//			Parfactor g10 = g9.multiply(g7).sumOut(eaux);
//			Parfactor g11 = g10.count(x).sumOut(by);
			
			
			
			System.out.println("Welcome to this incredible test!");
			System.out.println(getCorrectResultOfExistsNodeManually(domainSize));
			System.out.println(g9);
			
		}
		
		private Factor getCorrectResultOfExistsNodeManually(int n) {
			
			// List of constants
			List<Constant> x = new ArrayList<Constant>(n);
			List<Constant> y = new ArrayList<Constant>(n);
			for (int i = 0; i < n; i++) {
				x.add(Constant.getInstance("x" + i));
				y.add(Constant.getInstance("y" + i));
			}
			
			// Creates random variables
			List<Prv> b = new ArrayList<Prv>(n);
			List<Prv> r = new ArrayList<Prv>(n * n);
			List<Prv> a = new ArrayList<Prv>(n * n);
			List<Prv> e = new ArrayList<Prv>(n);
			for (int i = 0; i < n; i++) {
				b.add(StdPrv.getBooleanInstance("b", y.get(i)));
				e.add(StdPrv.getBooleanInstance("exists", x.get(i)));
				for (int j = 0; j < n; j++) {
					r.add(StdPrv.getBooleanInstance("r", x.get(i), y.get(j)));
					a.add(StdPrv.getBooleanInstance("and", x.get(i), y.get(j)));
				}
			}
			
			// Creates factors on b(Y)
			List<BigDecimal> vb = Utils.toBigDecimalList(0.1, 0.9);
			List<Factor> fb = new ArrayList<Factor>(n);
			for (int i = 0; i < n; i++) {
				int index = i;
				List<Prv> rvs = Lists.listOf(b.get(index));
				fb.add(StdFactor.getInstance("", rvs, vb));
			}
			
			// Creates factors on r(X,Y)
			List<BigDecimal> vr = Utils.toBigDecimalList(0.2, 0.8);
			List<Factor> fr = new ArrayList<Factor>(n);
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					int index = i * n + j;
					List<Prv> rvs = Lists.listOf(r.get(index));
					fr.add(StdFactor.getInstance("", rvs, vr));
				}
			}
			
			// Creates factors on r(X,Y), b(Y), and(X,Y)
			List<BigDecimal> vand = Utils.toBigDecimalList(1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0);
			List<Factor> fa = new ArrayList<Factor>(n * n);
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					List<Prv> rvs = Lists.listOf(b.get(j), r.get(i * n + j),
							a.get(i * n + j));
					fa.add(StdFactor.getInstance("", rvs, vand));
				}
			}
			
			// Creates factors on and(X,y1), ..., and(X,yn), exists(X)
			int vexistsSize = (int) Math.pow(2, n);
			List<BigDecimal> vexists = new ArrayList<BigDecimal>(vexistsSize); 
			for (int i = 0; i < vexistsSize; i++) {
				vexists.add(BigDecimal.ZERO);
				vexists.add(BigDecimal.ONE);
			}
			vexists.set(0, BigDecimal.ONE);
			vexists.set(1, BigDecimal.ZERO);
			List<Factor> fe = new ArrayList<Factor>(n);
			for (int i = 0; i < n; i++) {
				List<Prv> rvs = new ArrayList<Prv>(n + 1);
				for (int j = 0; j < n; j++) {
					rvs.add(a.get(i * n + j));
				}
				rvs.add(e.get(i));
				fe.add(StdFactor.getInstance("", rvs, vexists));
			}
			
			// The stupidest way to solve it
//			Factor result = StdFactor.getInstance();
//			for (Factor f : fb) {
//				result = result.multiply(f);
//			}
//			for (Factor f : fr) {
//				result = result.multiply(f);
//			}
//			for (Factor f : fa) {
//				result = result.multiply(f);
//			}
//			for (Factor f : fe) {
//				result = result.multiply(f);
//			}
//			for (Prv v : result.variables()) {
//				if (!e.contains(v)) {
//					result = result.sumOut(v);
//				}
//			}
			
			// Step by step
			// Eliminate r(X,Y)
			List<Factor> afterEliminating_r = new ArrayList<Factor>(n * n);
			for (int i = 0; i < n * n; i++) {
				afterEliminating_r.add(fr.get(i).multiply(fa.get(i)).sumOut(r.get(i)));
			}
			// Eliminate and(X,Y)
			List<Factor> afterEliminating_and = new ArrayList<Factor>(n * n);
			for (int i = 0; i < n; i++) {
				Factor product = fe.get(i);
				for (Prv v : fe.get(i).variables()) {
					for (Factor f : afterEliminating_r) {
						if (f.variables().contains(v)) {
							product = product.multiply(f);
						}
					}
				}
				for (Prv and : a) {
					if (product.variables().contains(and)) {
						product = product.sumOut(and);
					}
				}
				afterEliminating_and.add(product);
			}
			
			// Eliminate b(Y) -- need to multiply all remaining factors ...
			Factor product = StdFactor.getInstance();
			for (Factor f : afterEliminating_and) {
				product = product.multiply(f);
			}
			for (Factor f : fb) {
				product = product.multiply(f);
			}
			// and eliminate each b(yi)
			Factor result = product;
			for (Prv by : b) {
				result = result.sumOut(by);
			}
			result = result.sumOut(e.get(0));
			
			return result;
		}
		
		@Ignore("Wrong: this is the case to use Aggregation parfactors")
		@Test
		public void testFullyConnectedNetwork() throws IOException {
			
			int domainSize = 3;
			
			LogicalVariable x = StdLogicalVariable.getInstance("X", "x", domainSize);
			LogicalVariable y = StdLogicalVariable.getInstance("Y", "y", domainSize);
			
			Constant x1 = Constant.getInstance("x1");
			Constant x2 = Constant.getInstance("x2");
			Constant y1 = Constant.getInstance("y1");

			Prv e = StdPrv.getBooleanInstance("e", x);
			Prv b = StdPrv.getBooleanInstance("b", y);
			Prv e1 = StdPrv.getBooleanInstance("e", x1);
			
			List<BigDecimal> f1 = Utils.toBigDecimalList(0.2, 0.8);
			double r1n = Math.pow(0.1, domainSize);
			List<BigDecimal> f2 = Utils.toBigDecimalList(1.0, 0.0, r1n, 1.0 - r1n);
			
			Parfactor g1 = new StdParfactorBuilder().variables(b).values(f1).build();
			Parfactor g2 = new StdParfactorBuilder().variables(b, e).values(f2).build();
			
			RandomVariableSet query = RandomVariableSet.getInstance(e1, Sets.<Constraint>getInstance(0));
			Marginal input = new StdMarginalBuilder(2).parfactors(g1, g2).preservable(query).build();
			
			Marginal groundedInput = propositionalizeAll(input);
			
			Parfactor product = new StdParfactorBuilder().build();
			for (Parfactor p : groundedInput) {
				product = product.multiply(p);
			}
						
			ACFOVE ve = new LoggedACFOVE(groundedInput);
			Parfactor groundedResult = ve.run();
			System.out.println("Hi: " + groundedResult);
		}
		
		@Ignore("Testing manually")
		@Test
		public void testInferenceOnExistsNodeSimplified() throws IOException {
			int n = 1;
			
			LogicalVariable x = StdLogicalVariable.getInstance("X", "x", n);
			LogicalVariable y = StdLogicalVariable.getInstance("Y", "y", n);
			
			Constant x1 = Constant.getInstance("x1");
			
			Prv b = StdPrv.getBooleanInstance("b", y);
			Prv e = StdPrv.getBooleanInstance("e", x);
			Prv r = StdPrv.getBooleanInstance("r", x, y);
			Prv r1 = StdPrv.getBooleanInstance("and", x, y);
			Prv e1 = StdPrv.getBooleanInstance("e", x1);
			
			List<BigDecimal> fb = TestUtils.toBigDecimalList(0.1, 0.9);
			List<BigDecimal> fr = TestUtils.toBigDecimalList(0.2, 0.8);
			List<BigDecimal> fr1 = TestUtils.toBigDecimalList(1, 0, 1, 0, 1, 0, 0, 1);
			
			Parfactor gb = new StdParfactorBuilder().variables(b).values(fb).build();
			Parfactor gr = new StdParfactorBuilder().variables(r).values(fr).build();
			Parfactor gr1 = new StdParfactorBuilder().variables(b, r, r1).values(fr1).build();
			Parfactor ge = new AggParfactorBuilder(r1, e, Or.OR).build();
			
			RandomVariableSet query = RandomVariableSet.getInstance(e1, new HashSet<Constraint>(0));
			Marginal input = new StdMarginalBuilder().parfactors(gb, gr, gr1, ge).preservable(query).build();
			
			input = propositionalizeAll(input);
			
			ACFOVE ve = new LoggedACFOVE(input);
			Parfactor groundedResult = ve.run();
			System.out.println("Hi again: " + groundedResult);
		}
		
		
		@Test
		public void testBigJackpotInference() {
			
			int populationSize = 4;
			
			LogicalVariable person = StdLogicalVariable.getInstance("Person", "x", populationSize);
			
			Prv big_jackpot = StdPrv.getBooleanInstance("big_jackpot");
			Prv played = StdPrv.getBooleanInstance("played", person);
			Prv matched_6 = StdPrv.getBooleanInstance("matched_6", person);
			Prv jackpot_won = StdPrv.getBooleanInstance("jackpot_won");
			
			List<BigDecimal> fBigJackpot = Utils.toBigDecimalList(0.8, 0.2);
			List<BigDecimal> fPlayed = Utils.toBigDecimalList(0.95, 0.05, 0.85, 0.15);
			List<BigDecimal> fMatched6 = Utils.toBigDecimalList(1.0, 0.0, 0.99999993, 0.00000007);
			
			Parfactor g1 = new StdParfactorBuilder().variables(big_jackpot).values(fBigJackpot).build();
			Parfactor g2 = new StdParfactorBuilder().variables(big_jackpot, played).values(fPlayed).build();
			Parfactor g3 = new StdParfactorBuilder().variables(played, matched_6).values(fMatched6).build();
			Parfactor g4 = new AggParfactorBuilder(matched_6, jackpot_won, Or.OR).context(big_jackpot).build();
			
			Parfactor g2xg3 = g2.multiply(g3);
			Parfactor afterEliminatingPlayed = g2xg3.sumOut(played);
			Parfactor g4xg5 = g4.multiply(afterEliminatingPlayed);
			Parfactor afterEliminatingMatched6 = g4xg5.sumOut(matched_6);
			Parfactor g6xg1 = g1.multiply(afterEliminatingMatched6);
			Parfactor afterEliminatingBigJackpot = g6xg1.sumOut(big_jackpot);

			Factor r = getCorrectResultOfBigJackpotInference(populationSize);
			Parfactor expected = new StdParfactorBuilder().factor(r).build();
			
			assertEquals(expected, afterEliminatingBigJackpot);
			
		}
		
		// propositionalizes the model above and  calculates P(jackpot_won)
		private Factor getCorrectResultOfBigJackpotInference(int n) {
			
			List<Prv> matched_6 = new ArrayList<Prv>(n);
			for (int i = 0; i < n; i++) {
				Constant p = Constant.getInstance("x" + i);
				matched_6.add(StdPrv.getBooleanInstance("matched_6", p));
			}
			
			Prv jackpot_won = StdPrv.getBooleanInstance("jackpot_won");
			
			// Creates factor on matched_6(x0) ... matched_6(xn) jackpot_won()
			List<Prv> vars = Lists.listOf(matched_6);
			vars.add(jackpot_won);
			List<BigDecimal> fagg = new ArrayList<BigDecimal>();
			for (int i = 0; i < (int) Math.pow(2, n); i++) {
				fagg.add(BigDecimal.ZERO);
				fagg.add(BigDecimal.ONE);
			}
			fagg.set(0, BigDecimal.ONE);
			fagg.set(1, BigDecimal.ZERO);
			
			Factor jw = StdFactor.getInstance("", vars, fagg);
			
			// Creates factor on big_jackpot() matched_6(X)
			Prv big_jackpot = StdPrv.getBooleanInstance("big_jackpot");
			List<BigDecimal> f5vals = TestUtils.toBigDecimalList(0.9999999965, 0.0000000035, 0.9999999895, 0.0000000105);
			List<Factor> f5 = new ArrayList<Factor>(n);
			for (Prv m6 : matched_6) {
				List<Prv> rvs = new ArrayList<Prv>(2);
				rvs.add(big_jackpot);
				rvs.add(m6);
				f5.add(StdFactor.getInstance("", rvs, f5vals));
			}

			// Creates factor big_jackpot()
			List<BigDecimal> fbj = TestUtils.toBigDecimalList(0.8, 0.2);
			Factor bj = StdFactor.getInstance("", big_jackpot, fbj);
			
			// multiplies all factors
			Factor product = jw;
			for (Factor f : f5) {
				product = product.multiply(f);
			}
			
			// eliminates matched_6(x)
			Factor result = product;
			for (Prv m6 : matched_6) {
				result = result.sumOut(m6);
			}
			
			// Multiplies by big_jackpot
			result = result.multiply(bj);
			
			// eliminates big_jackpot
			result = result.sumOut(big_jackpot);
			
			return result;
		}
		
		
	}

	/**
	 * Some tests involving the lifted version of the water sprinkler network.
	 * Data structures used in test cases from this class are created by
	 * {@link WaterSprinklerNetwork} class.
	 */
	public static class WaterSprinklerNetworkTest {
		
		/**
		 * Network: water sprinkler
		 * Query: wet_grass(Lot)
		 * Evidence: none
		 * Population size: n >= 5
		 * 
		 * For n < 5, the algorithm propositionalizes the logical variable Lot
		 * because it is cheaper than counting it. In the end, the result will
		 * be propositionalized. This test assumes the answer is lifted so it
		 * is easier to compare with the expected result. Last time I checked
		 * the answer is correct for n < 5 too.
		 */
		@Test
		public void queryWetGrass() {
			int domainSize = 5;
			WaterSprinklerNetwork wsn = new WaterSprinklerNetwork(domainSize);
			wsn.setQuery(wsn.wetGrass);
			Marginal input = wsn.getMarginal();
			
			// Runs AC-FOVE on input marginal
			ACFOVE acfove = new LoggedACFOVE(input);
			Parfactor result = acfove.run();
			Parfactor expected = getResultWetGrass(wsn);
			
			// Compares expected with result
			assertEquals(expected, result);
		}
		
		private Parfactor getResultWetGrass(WaterSprinklerNetwork wsn) {
			
			Parfactor afterSumOutSprinkler = wsn.sprinklerParfactor
					.multiply(wsn.wetGrassParfactor).sumOut(wsn.sprinkler);
			
			Parfactor afterCountingLot = afterSumOutSprinkler.count(wsn.lot);

			Parfactor afterSumOutRain = afterCountingLot
					.multiply(wsn.rainParfactor).sumOut(wsn.rain);

			Parfactor afterSumOutCloudy = afterSumOutRain
					.multiply(wsn.cloudyParfactor).sumOut(wsn.cloudy);
			
			return afterSumOutCloudy;
		}
		
		/**
		 * Network: water sprinkler
		 * Query: rain()
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
		public void queryRainGivenWetGrassWithOneLot() {
			int domainSize = 1;
			WaterSprinklerNetwork wsn = new WaterSprinklerNetwork(domainSize);
			wsn.setEvidence(wsn.wetGrass, 0); 
			wsn.setQuery(wsn.rain);
			Marginal input = wsn.getMarginal();
			
			// Runs AC-FOVE on input marginal
			ACFOVE acfove = new LoggedACFOVE(input);
			Parfactor result = acfove.run();
			Parfactor expected = getResultRainGivenWetGrassWithOneLot(wsn);
			
			// Compares expected with result
			assertEquals(expected, result);
		}
		
		private Parfactor getResultRainGivenWetGrassWithOneLot(WaterSprinklerNetwork wsn) {
			
			// Sum out wet_grass(lot0)
			Prv wetGrass = wsn.wetGrass.apply(wsn.getLot(0));
			Parfactor wetGrassParfactor = wsn.wetGrassParfactor.apply(wsn.getLot(0));
			Factor afterSumOutWetGrass = wsn.evidenceParfactor.factor()
					.multiply(wetGrassParfactor.factor()).sumOut(wetGrass);

			// Sum out sprinkler(lot0)
			Prv sprinkler = wsn.sprinkler.apply(wsn.getLot(0));
			Parfactor sprinklerParfactor = wsn.sprinklerParfactor.apply(wsn.getLot(0));
			Factor afterSumOutSprinkler = afterSumOutWetGrass
					.multiply(sprinklerParfactor.factor()).sumOut(sprinkler);
			
			// Sum out cloudy
			Factor afterSumOutCloudy = afterSumOutSprinkler
					.multiply(wsn.cloudyParfactor.factor())
					.multiply(wsn.rainParfactor.factor()).sumOut(wsn.cloudy);
			
			Parfactor expected = new StdParfactorBuilder().factor(afterSumOutCloudy).build();
			
			return expected;
		}
		
		
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
		public void querySprinklerGivenWetGrassWithOneLot() {
			int domainSize = 1;
			WaterSprinklerNetwork wsn = new WaterSprinklerNetwork(domainSize);
			wsn.setEvidence(wsn.wetGrass, 0); 
			wsn.setQuery(wsn.sprinkler.apply(wsn.getLot(0)));
			Marginal input = wsn.getMarginal();
			
			// Runs AC-FOVE on input marginal
			ACFOVE acfove = new LoggedACFOVE(input);
			Parfactor result = acfove.run();
			Parfactor expected = getResultSprinklerGivenWetGrassWithOneLot(wsn);
			
			// Compares expected with result
			assertEquals(expected, result);
		}
		
		private Parfactor getResultSprinklerGivenWetGrassWithOneLot(WaterSprinklerNetwork wsn) {
			
			// Sum out wet_grass(lot0)
			Prv wetGrass = wsn.wetGrass.apply(wsn.getLot(0));
			Parfactor wetGrassParfactor = wsn.wetGrassParfactor.apply(wsn.getLot(0));
			Factor afterSumOutWetGrass = wsn.evidenceParfactor.factor()
					.multiply(wetGrassParfactor.factor()).sumOut(wetGrass);

			// Sum out rain
			Factor afterSumOutRain = afterSumOutWetGrass
					.multiply(wsn.rainParfactor.factor()).sumOut(wsn.rain);
			
			// Sum out cloudy
			Factor afterSumOutCloudy = afterSumOutRain
					.multiply(wsn.cloudyParfactor.factor())
					.multiply(wsn.sprinklerParfactor.apply(wsn.getLot(0)).factor())
					.sumOut(wsn.cloudy);
			
			Parfactor expected = new StdParfactorBuilder().factor(afterSumOutCloudy).build();
			
			return expected;
		}

		/**
		 * Network: Water Sprinkler
		 * Query: rain()
		 * Evidence: wet_grass(lot0) = true
		 * Population size: 100
		 */
		@Test
		public void queryRainGivenWetGrassWithManyLots() {
			int domainSize = 100;
			WaterSprinklerNetwork wsn = new WaterSprinklerNetwork(domainSize);
			wsn.setEvidence(wsn.wetGrass, 0); 
			wsn.setQuery(wsn.rain);
			Marginal input = wsn.getMarginal();
			
			// Runs AC-FOVE on input marginal
			ACFOVE acfove = new LoggedACFOVE(input);
			Parfactor result = acfove.run();
			Parfactor expected = getResultyRainGivenWetGrassWithManyLots(wsn);
			
			// Compares expected with result
			assertEquals(expected, result);
		}
		
		private Parfactor getResultyRainGivenWetGrassWithManyLots(WaterSprinklerNetwork wsn) {
			
			// Splits the marginal on the evidence and the query
			Parfactor g1 = wsn.cloudyParfactor;
			Parfactor g2 = wsn.rainParfactor;
			SplitResult splitSprinkler = wsn.sprinklerParfactor.splitOn(wsn.getLot(0));
			Parfactor g3 = splitSprinkler.residue().iterator().next();
			Parfactor g3_0 = splitSprinkler.result();
			SplitResult splitWetGrass = wsn.wetGrassParfactor.splitOn(wsn.getLot(0));
			Parfactor g4 = splitWetGrass.residue().iterator().next();
			Parfactor g4_0 = splitWetGrass.result();
			Parfactor g5 = wsn.evidenceParfactor;
			
			// sum out wet_grass(Lot):{Lot!=lot0}
			Parfactor afterSumOutWetGrass = g4.sumOut(wsn.wetGrass);
			
			// sum out sprinkler(Lot):{Lot!=lot0}
			Parfactor afterSumOutSprinkler = afterSumOutWetGrass.multiply(g3).sumOut(wsn.sprinkler);
			
			// sum out wet_grass(lot0)
			Prv wetgrass_lot0 = wsn.wetGrass.apply(wsn.getLot(0));
			Parfactor afterSumOutWetGrassLot0 = g5.multiply(g4_0).sumOut(wetgrass_lot0);
			
			// sum out sprinkler(lot0)
			Prv sprinkler_lot0 = wsn.sprinkler.apply(wsn.getLot(0));
			Parfactor afterSumOutSprinklerLot0 = afterSumOutWetGrassLot0.multiply(g3_0).sumOut(sprinkler_lot0);
			
			// sum out cloudy()
			Parfactor afterSumOutCloudy = afterSumOutSprinkler
					.multiply(afterSumOutSprinklerLot0).multiply(g1).multiply(g2)
					.sumOut(wsn.cloudy);
			
			return afterSumOutCloudy;
		}
		
		
		/**
		 * Network: Water Sprinkler
		 * Query: sprinkler(lot0)
		 * Evidence: wet_grass(lot0) = true
		 * Population size: 10
		 */
		@Test
		public void querySprinklerGivenWetGrassWithManyLots() {

			int domainSize = 100;
			WaterSprinklerNetwork wsn = new WaterSprinklerNetwork(domainSize);
			wsn.setEvidence(wsn.wetGrass, 0); 
			wsn.setQuery(wsn.sprinkler.apply(wsn.getLot(0)));
			Marginal input = wsn.getMarginal();
			
			// Runs AC-FOVE on input marginal
			ACFOVE acfove = new LoggedACFOVE(input);
			Parfactor result = acfove.run();
			Parfactor expected = getResultSprinklerGivenWetGrassWithManyLots(wsn);
			
			// Compares expected with result
			assertEquals(expected, result);
		}
		
		private Parfactor getResultSprinklerGivenWetGrassWithManyLots(WaterSprinklerNetwork wsn) {
			
			// Splits the marginal on the evidence and the query
			Parfactor g1 = wsn.cloudyParfactor;
			Parfactor g2 = wsn.rainParfactor;
			SplitResult splitSprinkler = wsn.sprinklerParfactor.splitOn(wsn.getLot(0));
			Parfactor g3 = splitSprinkler.residue().iterator().next();
			Parfactor g3_0 = splitSprinkler.result();
			SplitResult splitWetGrass = wsn.wetGrassParfactor.splitOn(wsn.getLot(0));
			Parfactor g4 = splitWetGrass.residue().iterator().next();
			Parfactor g4_0 = splitWetGrass.result();
			Parfactor g5 = wsn.evidenceParfactor;
			
			// sum out wet_grass(Lot):{Lot!=lot0}
			Parfactor afterSumOutWetGrass = g4.sumOut(wsn.wetGrass);

			// sum out sprinkler(Lot):{Lot!=lot0}
			Parfactor afterSumOutSprinkler = afterSumOutWetGrass.multiply(g3).sumOut(wsn.sprinkler);
			
			// sum out wet_grass(lot0)
			Prv wetgrass_lot0 = wsn.wetGrass.apply(wsn.getLot(0));
			Parfactor afterSumOutWetGrassLot0 = g5.multiply(g4_0).sumOut(wetgrass_lot0);
			
			// sum out rain()
			Parfactor afterSumOutRain = afterSumOutWetGrassLot0
					.multiply(afterSumOutSprinkler).multiply(g2).sumOut(wsn.rain);
			
			// sum out cloudy()
			Parfactor afterSumOutCloudy = afterSumOutRain.multiply(g1).multiply(g3_0).sumOut(wsn.cloudy);
			
			return afterSumOutCloudy;
		}
	}
}