package br.usp.poli.takiyama.acfove;

import static org.hamcrest.collection.IsIn.isIn;
import static org.junit.Assert.assertEquals;

import java.io.ObjectInputStream.GetField;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import br.usp.poli.takiyama.acfove.AggParfactor.AggParfactorBuilder;
import br.usp.poli.takiyama.cfove.StdParfactor;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.InputOutput;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Or;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.MathUtils;
import br.usp.poli.takiyama.utils.Sets;


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
			
			ACFOVE acfove = new ACFOVE(input);
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
	
	// This test is the same as AggregationExampleWithConversion
//	public static class ExampleComputationWithAggregation {
//
//		@Test
//		public void testExampleComputationWithAggregation() {
//			
//			int populationSize = 5;
//			
//			LogicalVariable person = StdLogicalVariable.getInstance("Person", "x", populationSize);
//			
//			Prv big_jackpot = StdPrv.getBooleanInstance("big_jackpot");
//			Prv played = StdPrv.getBooleanInstance("played", person);
//			Prv matched_6 = StdPrv.getBooleanInstance("matched_6", person);
//			Prv jackpot_won = StdPrv.getBooleanInstance("jackpot_won");
//			
//			List<BigDecimal> fBigJackpot = Utils.toBigDecimalList(0.8, 0.2);
//			List<BigDecimal> fPlayed = Utils.toBigDecimalList(0.95, 0.05, 0.85, 0.15);
//			List<BigDecimal> fMatched6 = Utils.toBigDecimalList(1.0, 0.0, 0.99999993, 0.00000007);
//			List<BigDecimal> fJackpotWon = Utils.toBigDecimalList(0.999999975, 0.000000025);
//			
//			Parfactor g1 = new StdParfactorBuilder().variables(big_jackpot).values(fBigJackpot).build();
//			Parfactor g2 = new StdParfactorBuilder().variables(big_jackpot, played).values(fPlayed).build();
//			Parfactor g3 = new StdParfactorBuilder().variables(played, matched_6).values(fMatched6).build();
//			Parfactor g4 = new AggParfactorBuilder(matched_6, jackpot_won, Or.OR).context(big_jackpot).build();
//			Parfactor g8 = new StdParfactorBuilder().variables(jackpot_won).values(fJackpotWon).build();
//			
//			RandomVariableSet query = RandomVariableSet.getInstance(jackpot_won, Sets.<Constraint>getInstance(0));
//			Marginal input = new StdMarginalBuilder(4).parfactors(g1, g2, g3, g4).preservable(query).build();
//			
//			ACFOVE acfove = new ACFOVE(input);
//			Parfactor result = acfove.run();
//			
//			Parfactor expected = g8;
//			
//			assertEquals(expected, result);
//		}
//	}
	
	
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
}