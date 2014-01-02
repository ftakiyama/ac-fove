/*******************************************************************************
 * Copyright 2014 Felipe Takiyama
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package br.usp.poli.takiyama.acfove;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import br.usp.poli.takiyama.acfove.AggParfactor.AggParfactorBuilder;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.AggregationParfactor;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Distribution;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.InputOutput;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdDistribution;
import br.usp.poli.takiyama.common.StdFactor;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Or;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.MathUtils;
import br.usp.poli.takiyama.utils.Sets;


@RunWith(Enclosed.class)
public class AggParfactorTest {

	@RunWith(Theories.class)
	public static class SplitTestUsingTheory {
		
		private static LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		private static LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);

		private static Prv p = StdPrv.getBooleanInstance("p", a, b);
		private static Prv c = StdPrv.getBooleanInstance("c", b);
		private static Prv cPrime = c.rename("c'");
		
		private static Prv v = StdPrv.getBooleanInstance("v", a);
		private static Prv u = StdPrv.getBooleanInstance("u", b);
		
		@DataPoints
		public static Parfactor[] data() {
			Constant x2 = Constant.getInstance("x2");
			Constraint a_x2 = InequalityConstraint.getInstance(a, x2);
			Constraint b_x2 = InequalityConstraint.getInstance(b, x2);
			return new Parfactor[] {
				new AggParfactorBuilder(p, c, Or.OR).build(),
				new AggParfactorBuilder(p, c, Or.OR).constraints(a_x2, b_x2).build(),
				new AggParfactorBuilder(p, c, Or.OR).context(v, u).build(),
				new AggParfactorBuilder(p, c, Or.OR).context(v, u).constraints(a_x2, b_x2).build()
			};
		}
		
		@DataPoints
		public static Substitution[] substitutions() {
			Constant x1 = Constant.getInstance("x1");
			return new Substitution[] {
				Substitution.getInstance(Binding.getInstance(b, x1)),
				Substitution.getInstance(Binding.getInstance(a, x1)),
				Substitution.getInstance(Binding.getInstance(a, b)),
				Substitution.getInstance(Binding.getInstance(b, a))
			};
		}
		
		@Theory
		public void testSplitNotOnExtra(Parfactor parfactor, Substitution s) {
			
			assumeThat(parfactor, instanceOf(AggregationParfactor.class));
			
			Term extraVar = ((AggregationParfactor) parfactor).extraVariable();
			
			assumeThat(s.has(extraVar), is(false));
			
			Parfactor result = parfactor.apply(s);
			
			Constraint c = s.first().toInequalityConstraint();
			Parfactor residue = new AggParfactorBuilder((AggregationParfactor) parfactor).constraint(c).build();
			
			SplitResult splitResult = SplitResult.getInstance(result, residue);
			
			assertThat(parfactor.splitOn(s), equalTo(splitResult));
		}
		
		@Theory
		public void testSplitInvolvingExtra(Parfactor parfactor, Substitution s) {
			
			assumeThat(parfactor, instanceOf(AggregationParfactor.class));
			
			AggregationParfactor ag = (AggregationParfactor) parfactor;
			Term extraVar = ag.extraVariable();
			
			assumeThat(s.has(extraVar), is(true));
			
			Prv newP = p.apply(s);
			Set<Constraint> constraints = Sets.apply(s, parfactor.constraints());
			List<Prv> prvs = Lists.listOf(ag.context());	
			prvs.add(0, newP);
			prvs.add(cPrime);
			prvs.add(c);
			
			// not quite right to put in a Theory
			double [] vals = new double[getSize(prvs)];
			if (ag.context().isEmpty()) {
				vals[0] = 1.0;
				vals[1] = 0.0;
				vals[2] = 0.0;
				vals[3] = 1.0;
				vals[4] = 0.0;
				vals[5] = 1.0;
				vals[6] = 0.0;
				vals[7] = 1.0;
			} else {
				vals[0] = 1.0;
				vals[1] = 0.0;
				vals[2] = 0.0;
				vals[3] = 1.0;
				vals[4] = 1.0;
				vals[5] = 0.0;
				vals[6] = 0.0;
				vals[7] = 1.0;
				vals[8] = 1.0;
				vals[9] = 0.0;
				vals[10] = 0.0;
				vals[11] = 1.0;
				vals[12] = 1.0;
				vals[13] = 0.0;
				vals[14] = 0.0;
				vals[15] = 1.0;
				vals[16] = 0.0;
				vals[17] = 1.0;
				vals[18] = 0.0;
				vals[19] = 1.0;
				vals[20] = 0.0;
				vals[21] = 1.0;
				vals[22] = 0.0;
				vals[23] = 1.0;
				vals[24] = 0.0;
				vals[25] = 1.0;
				vals[26] = 0.0;
				vals[27] = 1.0;
				vals[28] = 0.0;
				vals[29] = 1.0;
				vals[30] = 0.0;
				vals[31] = 1.0;
			}
			
			Parfactor result = new StdParfactorBuilder().constraints(constraints)
					.variables(prvs).values(vals).build();
			
			Constraint c = s.first().toInequalityConstraint();
			Parfactor residue = new AggParfactorBuilder(ag).constraint(c).child(cPrime).build();
			
			SplitResult splitResult = SplitResult.getInstance(result, residue);
			
			assertThat(parfactor.splitOn(s), equalTo(splitResult));
		}
		
		private int getSize(List<Prv> prvs) {
			int size = 1;
			for (Prv var : prvs) {
				size = size * var.range().size();
			}
			return size;
		}
	}

	
	public static class MultiplicationTest {
		
		private static LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 100);
		private static LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 100);
		private static LogicalVariable person = StdLogicalVariable.getInstance("Person", "p", 100); 
		
		private static Prv played = StdPrv.getBooleanInstance("played", person);
		private static Prv matched6 = StdPrv.getBooleanInstance("matched_6", person);
		private static Prv jackpotWon = StdPrv.getBooleanInstance("jackpot_won");
		private static Prv bigJackpot = StdPrv.getBooleanInstance("big_jackpot");
		 
		private static Prv p = StdPrv.getBooleanInstance("p", a, b);
		private static Prv c = StdPrv.getBooleanInstance("c", b);
		private static Prv v = StdPrv.getBooleanInstance("v", a);
		private static Prv u = StdPrv.getBooleanInstance("u", b);
		 
		private static Constant x1 = Constant.getInstance("x1");
		private static Constant x2 = Constant.getInstance("x2");
		private static Constant x3 = Constant.getInstance("x3");
		 
		private static Constraint a_x1 = InequalityConstraint.getInstance(a, x1);
		private static Constraint a_x2 = InequalityConstraint.getInstance(a, x2);
		private static Constraint a_b = InequalityConstraint.getInstance(a, b);
		private static Constraint b_x1 = InequalityConstraint.getInstance(b, x1);
		private static Constraint b_x3 = InequalityConstraint.getInstance(b, x3);
		 
		private static double [] f1 = {0.1234, 0.9876};
		private static Parfactor g1 = new StdParfactorBuilder()
				.constraints(a_x1, a_x2, a_b, b_x3).variables(p).values(f1).build();
        
		private static double [] f2 = {0.5425, 0.6832};
		private static Parfactor g2 = new AggParfactorBuilder(p, c, Or.OR)
				.constraints(a_x1, a_x2, a_b, b_x3).values(f2).build();
		
		/**
		 * Multiplies
		 * <p>
		 * g1 = &lang; {A&ne;x1,A&ne;x2,A&ne;B,B&ne;x3}, {p(A,B)}, F1 &rang;
		 * </p>
		 * with
		 * <p>
		 * g2 = &lang; {B&ne;x3}, p(A,B), c(B), F2, OR, {A&ne;x1,A&ne;x2,A&ne;B} &rang;
		 * </p>
		 * <p>
		 * The result is
		 * g3 = &lang; {B&ne;x3}, p(A,B), c(B), F1&odot;F2, OR, {A&ne;x1,A&ne;x2,A&ne;B} &rang;
		 * </p>
		 */
		@Test
		public void testMultiplication() {
			
			Parfactor result = g2.multiply(g1);
			
			List<BigDecimal> f3 = new ArrayList<BigDecimal>(2);
			f3.add(BigDecimal.valueOf(f1[0]).multiply(BigDecimal.valueOf(f2[0]), MathUtils.CONTEXT));
			f3.add(BigDecimal.valueOf(f1[1]).multiply(BigDecimal.valueOf(f2[1]), MathUtils.CONTEXT));
			
			Parfactor answer = new AggParfactorBuilder(p, c, Or.OR)
					.constraints(a_x1, a_x2, a_b, b_x3).values(f3).build();

			assertEquals(result, answer);
		}
		
		/**
		 * Example 3.12 from Kisynski (2010).
		 * <p>
		 * Given the set of parfactors
		 * </p>
		 * <p>
		 * &Phi; = {<br>
		 * &lang; &empty;, {played(Person)}, Fplayed &rang;,<br>
		 * &lang; &empty;, {played(Person), matched_6(Person}, Fmatched_6 &rang;,<br> 
		 * &lang; &empty;, matched_6(Person), jackpot_won(Person), 1, OR, &empty; &rang; },
		 * </p>
		 * <p>
		 * we want to calculate J<sub>ground(jackpot_won())</sub>(&Phi;).
		 * </p>
		 * <p>
		 * The partial result in the calculation is parfactor
		 * &lang; &empty;, matched_6(Person), jackpot_won(Person), F, OR, &empty; &rang;,
		 * where
		 * F = 1 &odot; &sum;<sub>played(Person)</sub>(Fplayed &odot; Fmatched_6).
		 * </p>
		 */
		@Test
		public void testTrivialMultiplication() {
			
			double [] fPlayed = {0.95, 0.05};
			
			Parfactor g1 = new StdParfactorBuilder().variables(played)
					.values(fPlayed).build();
			
			double [] fMatched = {1.0, 0.0, 0.99999993, 0.00000007};
			
			Parfactor g2 = new StdParfactorBuilder()
					.variables(played, matched6).values(fMatched).build();
			
			Parfactor g3 = new AggParfactorBuilder(matched6, jackpotWon, Or.OR)
					.build();
			
			Parfactor result = g1.multiply(g2).sumOut(played).multiply(g3);
			
			double [] fSum = {0.9999999965, 0.0000000035};
			
			Parfactor expected = new AggParfactorBuilder(matched6, jackpotWon, Or.OR)
					.values(fSum).build();
			
			assertEquals(result, expected);
		}
		
		
		/**
		 * Performs g1 * g2 and g2 * g1 and check if the results are the same.
		 * To pass the test we must have g1 * g2 = g2 * g1
		 */
		@Test
		public void testMultiplicationCommutativity() {
			Parfactor direct = g2.multiply(g1);
			Parfactor inverse = g1.multiply(g2);
			
			assertEquals(direct, inverse);
		}
		
		// TODO I can use Theory here
		@Test
		public void testMultiplicationCheck() {
			assertTrue(g1.isMultipliable(g1) && g1.isMultipliable(g2)
					&& g2.isMultipliable(g1) && !g2.isMultipliable(g2));
		}
		
		@Test
		public void testGeneralizedMultiplication() {
			
			Parfactor g4 = new AggParfactorBuilder(matched6, jackpotWon, Or.OR).context(bigJackpot).build();
			
			double [] fMatched6 = {
					0.9999999965,
					0.999999989,
					0.0000000035,
					0.000000011
			};
			Parfactor g5 = new StdParfactorBuilder().variables(matched6, bigJackpot).values(fMatched6).build();
			Parfactor product = g4.multiply(g5);
			
			Parfactor expected = new AggParfactorBuilder(matched6, jackpotWon, Or.OR).context(bigJackpot).values(fMatched6).build();
			
			assertEquals(expected, product);
		}
		
		@Test
		public void testAnotherGeneralizedMultiplication() {
			// using BigDecimal due to precision problems with double
			int size = 8;
			List<BigDecimal> fpv = new ArrayList<BigDecimal>(size);
			List<BigDecimal> fr = new ArrayList<BigDecimal>(size);
			for (int i = 0; i < size; i++) {
				fpv.add(BigDecimal.valueOf(i / 10.0));
				fr.add(fpv.get(i).multiply(fpv.get(i), MathUtils.CONTEXT));
			}
			Parfactor g1 = new StdParfactorBuilder().constraints(a_x2, b_x1).variables(p, v, u).values(fpv).build();
			Parfactor ga = new AggParfactorBuilder(p, c, Or.OR).constraints(a_x2, b_x1).context(v, u).values(fpv).build();
			Parfactor product = g1.multiply(ga);
			Parfactor expected = new AggParfactorBuilder(p, c, Or.OR).constraints(a_x2, b_x1).context(v, u).values(fr).build();
			
			assertEquals(expected, product);
		}
	}
	

	public static class SumOutTest {
		// TODO use theory
		private final static LogicalVariable person = StdLogicalVariable.getInstance("Person", "p", 5);
		private final static Prv matched6 = StdPrv.getBooleanInstance("matched_6", person);
		private final static Prv jackpotWon = StdPrv.getBooleanInstance("jackpot_won");
		private final static Prv bigJackpot = StdPrv.getBooleanInstance("big_jackpot");
		
		private final static double [] fSum = {0.9999999965, 0.0000000035};
		
		private final static Parfactor g1 = new AggParfactorBuilder(matched6, jackpotWon, Or.OR).values(fSum).build();
		
		@Test
		public void testSimpleSumOut() {
			// not elegant, but more readable
			BigDecimal [] f0 = new BigDecimal[2];
			BigDecimal [] f1 = new BigDecimal[2];
			BigDecimal [] f2 = new BigDecimal[2];
			
			f0[0] = BigDecimal.valueOf(fSum[0]);
			f0[1] = BigDecimal.valueOf(fSum[1]);
			
			f1[0] = f0[0].multiply(f0[0], MathUtils.CONTEXT);
			f1[1] = f0[0].multiply(f0[1], MathUtils.CONTEXT).add(f0[1].multiply(f0[0], MathUtils.CONTEXT), MathUtils.CONTEXT).add(f0[1].multiply(f0[1], MathUtils.CONTEXT), MathUtils.CONTEXT);
			
			f2[0] = BigDecimal.valueOf(fSum[0]).multiply(f1[0], MathUtils.CONTEXT).multiply(f1[0], MathUtils.CONTEXT);
			
			
			f2[1] = BigDecimal.valueOf(fSum[0]).multiply(f1[0], MathUtils.CONTEXT).multiply(f1[1], MathUtils.CONTEXT)
					.add(BigDecimal.valueOf(fSum[0]).multiply(f1[1], MathUtils.CONTEXT).multiply(f1[0], MathUtils.CONTEXT), MathUtils.CONTEXT)
					.add(BigDecimal.valueOf(fSum[0]).multiply(f1[1], MathUtils.CONTEXT).multiply(f1[1], MathUtils.CONTEXT), MathUtils.CONTEXT)
					.add(BigDecimal.valueOf(fSum[1]).multiply(f1[0], MathUtils.CONTEXT).multiply(f1[0], MathUtils.CONTEXT), MathUtils.CONTEXT)
					.add(BigDecimal.valueOf(fSum[1]).multiply(f1[0], MathUtils.CONTEXT).multiply(f1[1], MathUtils.CONTEXT), MathUtils.CONTEXT)
					.add(BigDecimal.valueOf(fSum[1]).multiply(f1[1], MathUtils.CONTEXT).multiply(f1[0], MathUtils.CONTEXT), MathUtils.CONTEXT)
					.add(BigDecimal.valueOf(fSum[1]).multiply(f1[1], MathUtils.CONTEXT).multiply(f1[1], MathUtils.CONTEXT), MathUtils.CONTEXT);
			
			Parfactor expected = new StdParfactorBuilder().variables(jackpotWon).values(f2).build();
			
			Parfactor result = g1.sumOut(matched6);
			
			assertEquals(expected, result);
		}
		
		@Test
		public void testGeneralizedSumOut() {
			double [] fMatched6 = {
					0.9999999965,
					0.999999989,
					0.0000000035,
					0.000000011
			};
			Parfactor g6 = new AggParfactorBuilder(matched6, jackpotWon, Or.OR).context(bigJackpot).values(fMatched6).build();
			
			Parfactor result = round(g6.sumOut(matched6), 10);
			
			// Im here!!!!! this list is not accurate
			double [] fJackpotWon = {
					0.9999999825,
					0.9999999450,
					0.0000000175,
					0.0000000550
			};
			Parfactor expected = new StdParfactorBuilder().variables(jackpotWon, bigJackpot).values(fJackpotWon).build();

			assertEquals(expected, result);
		}
		
		/**
		 * Returns the specified parfactor with values rounded to the specified
		 * scale. 
		 */
		private Parfactor round(Parfactor parfactor, int scale) {
			List<BigDecimal> rounded = new ArrayList<BigDecimal>(parfactor.factor().values());
			for (BigDecimal number : rounded) {
				int index = rounded.indexOf(number);
				rounded.set(index, number.setScale(scale, BigDecimal.ROUND_HALF_EVEN));
			}
			Factor roundedFactor = StdFactor.getInstance("", parfactor.prvs(), rounded);
			return new StdParfactorBuilder().constraints(parfactor.constraints()).factor(roundedFactor).build();
		}
	}
	
	
	@RunWith(Theories.class)
	public static class ConversionTest {
		
		// Using Integer because JUnit seems to have a bug when using int[]
		@DataPoints
		public static final Integer[] populationSize() {
			Integer[] sizes = new Integer[20];
			for (int i = 0; i < sizes.length; i++) {
				sizes[i] = i;
			}
			return sizes;
		}
		
		@Theory
		public void testSimpleConversion(Integer popSize) {
			
			// Ugly, but I could not use int directly
			int populationSize = popSize.intValue();
			
			assumeThat(populationSize, not(0));
			
			LogicalVariable person = StdLogicalVariable.getInstance("Person", "p", populationSize);
			Prv matched6 = StdPrv.getBooleanInstance("matched_6", person);
			Prv jackpotWon = StdPrv.getBooleanInstance("jackpot_won");
			Prv matched6counted = CountingFormula.getInstance(person, matched6);
			AggregationParfactor input = new AggParfactorBuilder(matched6, jackpotWon, Or.OR).build();
			
			double [] f = new double[2 * (populationSize + 1)];
			f[0] = 1.0;
			f[1] = 0.0;
			for (int i = 2; i < f.length; i++) {
				f[i] = (double)(i % 2);
			}
			
			Parfactor answer1 = new StdParfactorBuilder().variables(matched6counted, jackpotWon).values(f).build();
			Parfactor answer2 = new StdParfactorBuilder().variables(matched6).build();
			
			Distribution answer = StdDistribution.of(answer1, answer2);
			
			Distribution result = input.toStdParfactors();
			
			assertThat(result, equalTo(answer));
			
		}
		
		@Theory
		public void testSimpleGeneralizedConversion(Integer popSize) {
			
			int populationSize = popSize.intValue();
			
			assumeThat(populationSize, not(0));
			assumeThat(populationSize, not(1));
			
			LogicalVariable lva = StdLogicalVariable.getInstance("A", "x", populationSize);
			LogicalVariable lvb = StdLogicalVariable.getInstance("B", "x", populationSize);
			LogicalVariable lvc = StdLogicalVariable.getInstance("C", "x", populationSize);
			LogicalVariable lvd = StdLogicalVariable.getInstance("D", "x", populationSize);
			LogicalVariable lve = StdLogicalVariable.getInstance("E", "x", populationSize);
			
			Constant x1 = Constant.getInstance("x1");
			Constant x2 = Constant.getInstance("x2");
			
			Constraint a_x2 = InequalityConstraint.getInstance(lva, x2);
			Constraint b_x1 = InequalityConstraint.getInstance(lvb, x1);
			
			Prv p = StdPrv.getBooleanInstance("p", lva, lvb);
			Prv c = StdPrv.getBooleanInstance("c", lvb);
			Prv v = StdPrv.getBooleanInstance("v", lvc);
			Prv u = StdPrv.getBooleanInstance("u", lvd, lve);
			Prv cf = CountingFormula.getInstance(lva, p, a_x2);
			
			List<BigDecimal> fpv = new ArrayList<BigDecimal>(8);
			for (int i = 0; i < 8; i++) {
				fpv.add(BigDecimal.valueOf(i / 10.0));
			}
			
			List<BigDecimal> fr = new ArrayList<BigDecimal>(8 * populationSize);
			Lists.fill(fr, BigDecimal.ONE, 4);
			Lists.fill(fr, BigDecimal.ZERO, 4);
			for (int i = 1; i < populationSize; i++) {
				Lists.fill(fr, BigDecimal.ZERO, 4);
				Lists.fill(fr, BigDecimal.ONE, 4);
			}
			
			AggregationParfactor input = new AggParfactorBuilder(p, c, Or.OR).constraints(b_x1, a_x2).context(v, u).values(fpv).build();
			Parfactor ans1 = new StdParfactorBuilder().constraints(a_x2, b_x1).variables(p, v, u).values(fpv).build();
			Parfactor ans2 = new StdParfactorBuilder().constraints(b_x1).variables(cf, c, v, u).values(fr).build();
			
			Distribution answer = StdDistribution.of(ans1, ans2);
			Distribution result = input.toStdParfactors();
			
			assertThat(result, equalTo(answer));
		}
		
	}


	@RunWith(Parameterized.class)
	public static class SimplificationTest {
		
		private static int populationSize = 3;
		
		private static LogicalVariable a = StdLogicalVariable.getInstance("A", "x", populationSize);
		private static LogicalVariable b = StdLogicalVariable.getInstance("B", "x", populationSize);
		private static LogicalVariable e = StdLogicalVariable.getInstance("E", "x", populationSize);
		
		private static Constant x1 = Constant.getInstance("x1");
		private static Constant x2 = Constant.getInstance("x2");
		private static Constant x3 = Constant.getInstance("x3");
		
		private static Prv p = StdPrv.getBooleanInstance("p", a, b);
		private static Prv p_a_x3 = StdPrv.getBooleanInstance("p", a, x3);
		private static Prv p_x2_x3 = StdPrv.getBooleanInstance("p", x2, x3);
		private static Prv c = StdPrv.getBooleanInstance("c", b);
		private static Prv c_x3 = StdPrv.getBooleanInstance("c", x3);
		private static Prv c_b_e = StdPrv.getBooleanInstance("c", b, e);
		private static Prv c_x3_e = StdPrv.getBooleanInstance("c", x3, e);
		private static Prv h = StdPrv.getBooleanInstance("h", b);
		private static Prv h_x3 = StdPrv.getBooleanInstance("h", x3);
		
		private static Constraint a_x1 = InequalityConstraint.getInstance(a, x1);
		private static Constraint a_b = InequalityConstraint.getInstance(a, b);
		private static Constraint b_x1 = InequalityConstraint.getInstance(b, x1);
		private static Constraint b_x2 = InequalityConstraint.getInstance(b, x2);
		private static Constraint e_x1 = InequalityConstraint.getInstance(e, x1);
		
		private static List<BigDecimal> vals = new ArrayList<BigDecimal>();
		
		@Parameters
		public static Collection<Object[]> data() {
			
			InputOutput<Parfactor, Parfactor> inOut = InputOutput.getInstance();
			
			// test 1
			
			Parfactor in = new AggParfactorBuilder(p, c, Or.OR).constraints(b_x1, b_x2).build();
			Parfactor out = new AggParfactorBuilder(p_a_x3, c_x3, Or.OR).build();
			
			inOut.add(in, out);
			
			// test 2
			
			vals = Lists.listOf(
					BigDecimal.valueOf(0.2), 
					BigDecimal.valueOf(0.3));
			in = new AggParfactorBuilder(p, c, Or.OR).constraints(b_x1, b_x2, a_b, a_x1).values(vals).build();
			
			vals = Lists.listOf(
					BigDecimal.valueOf(0.2), 
					BigDecimal.ZERO, 
					BigDecimal.ZERO, 
					BigDecimal.valueOf(0.3));
			out = new StdParfactorBuilder().variables(p_x2_x3, c_x3).values(vals).build();
			
			inOut.add(in, out);
			
			// test 3
			
			vals = Lists.listOf(
					BigDecimal.valueOf(0.2), 
					BigDecimal.valueOf(0.3));
			in = new AggParfactorBuilder(p, c_b_e, Or.OR).constraints(b_x1, b_x2, a_b, a_x1, e_x1).values(vals).build();
			
			vals = Lists.listOf(
					MathUtils.pow(BigDecimal.valueOf(0.2), 1, 2), 
					BigDecimal.ZERO, 
					BigDecimal.ZERO, 
					MathUtils.pow(BigDecimal.valueOf(0.3), 1, 2));
			out = new StdParfactorBuilder().constraints(e_x1).variables(p_x2_x3, c_x3_e).values(vals).build();

			inOut.add(in, out);
			
			// test 4
			
			vals = Lists.listOf(
					BigDecimal.valueOf(0.2), 
					BigDecimal.valueOf(0.3), 
					BigDecimal.valueOf(0.5), 
					BigDecimal.valueOf(0.7));
			in = new AggParfactorBuilder(p, c, Or.OR).constraints(b_x1, b_x2).context(h).values(vals).build();

			out = new AggParfactorBuilder(p_a_x3, c_x3, Or.OR).context(h_x3).values(vals).build();
			
			inOut.add(in, out);
			
			return inOut.toCollection();
		}
		
		private Parfactor input;
		private Parfactor expected;
		
		public SimplificationTest(Parfactor input, Parfactor expected) {
			this.input = input;
			this.expected = expected;
		}
		
		@Test
		public void testSimplicationOfLogicalVariables() {
			Parfactor result = input.simplifyLogicalVariables();
			assertEquals(expected, result);
		}
	}
}
