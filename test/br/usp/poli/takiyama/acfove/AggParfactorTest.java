package br.usp.poli.takiyama.acfove;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.*;
import org.junit.runner.RunWith;

import br.usp.poli.takiyama.acfove.AggParfactor.AggParfactorBuilder;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.AggregationParfactor;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Distribution;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdDistribution;
import br.usp.poli.takiyama.common.StdSplitResult;
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


@RunWith(Enclosed.class)
public class AggParfactorTest {

	@RunWith(Theories.class)
	public static class SplitTestUsingTheory {
		
		private static LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		private static LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);

		private static Prv p = StdPrv.getBooleanInstance("p", a, b);
		private static Prv c = StdPrv.getBooleanInstance("c", b);
		private static Prv cPrime = c.rename("c'");
		
		@DataPoints
		public static Parfactor[] data() {
			return new Parfactor[] {
				new AggParfactorBuilder(p, c, Or.OR).factor().build()
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
			
			SplitResult splitResult = StdSplitResult.getInstance(result, residue);
			
			assertThat(parfactor.splitOn(s), equalTo(splitResult));
		}
		
		@Theory
		public void testSplitInvolvingExtra(Parfactor parfactor, Substitution s) {
			
			assumeThat(parfactor, instanceOf(AggregationParfactor.class));
			
			Term extraVar = ((AggregationParfactor) parfactor).extraVariable();
			
			assumeThat(s.has(extraVar), is(true));
			
			Prv newP = p.apply(s);
			
			// not quite right to put in a Theory
			double [] vals = {1, 0, 0, 1, 0, 1, 0, 1};
			Parfactor result = new StdParfactorBuilder().variables(newP, cPrime, c).values(vals).build();
			
			Constraint c = s.first().toInequalityConstraint();
			Parfactor residue = new AggParfactorBuilder((AggregationParfactor) parfactor).constraint(c).child(cPrime).build();
			
			SplitResult splitResult = AggSplitResult.getInstance(result, residue, cPrime);
			
			assertThat(parfactor.splitOn(s), equalTo(splitResult));
		}
	}

	
	public static class MultiplicationTest {
		
		private static LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 100);
		private static LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 100);
         
		private static Prv p = StdPrv.getBooleanInstance("p", a, b);
		private static Prv c = StdPrv.getBooleanInstance("c", b);
		 
		private static Constant x1 = Constant.getInstance("x1");
		private static Constant x2 = Constant.getInstance("x2");
		private static Constant x3 = Constant.getInstance("x3");
		 
		private static Constraint a_x1 = InequalityConstraint.getInstance(a, x1);
		private static Constraint a_x2 = InequalityConstraint.getInstance(a, x2);
		private static Constraint a_b = InequalityConstraint.getInstance(a, b);
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
			f3.add(BigDecimal.valueOf(f1[0]).multiply(BigDecimal.valueOf(f2[0])));
			f3.add(BigDecimal.valueOf(f1[1]).multiply(BigDecimal.valueOf(f2[1])));
			
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
		@Ignore("Enable this test when summing out is working")
		@Test
		public void testTrivialMultiplication() {
			
			LogicalVariable person = StdLogicalVariable.getInstance("Person", "p", 100);
			
			Prv played = StdPrv.getBooleanInstance("played", person);
			Prv matched6 = StdPrv.getBooleanInstance("matched_6", person);
			Prv jackpotWon = StdPrv.getBooleanInstance("jackpot_won");
			
			double [] fPlayed = {0.95, 0.05};
			
			Parfactor g1 = new StdParfactorBuilder().variables(played)
					.values(fPlayed).build();
			
			double [] fMatched = {1.0, 0.0, 0.99999993, 0.00000007};
			
			Parfactor g2 = new StdParfactorBuilder()
					.variables(played, matched6).values(fMatched).build();
			
			Parfactor g3 = new AggParfactorBuilder(matched6, jackpotWon, Or.OR)
					.factor().build();
			
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
	}
	

	public static class SumOutTest {
		// TODO use theory
		private final static LogicalVariable person = StdLogicalVariable.getInstance("Person", "p", 5);
		private final static Prv matched6 = StdPrv.getBooleanInstance("matched_6", person);
		private final static Prv jackpotWon = StdPrv.getBooleanInstance("jackpot_won");

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
			
			f1[0] = f0[0].multiply(f0[0]);
			f1[1] = f0[0].multiply(f0[1]).add(f0[1].multiply(f0[0])).add(f0[1].multiply(f0[1]));
			
			f2[0] = BigDecimal.valueOf(fSum[0]).multiply(f1[0]).multiply(f1[0]);
			
			
			f2[1] = BigDecimal.valueOf(fSum[0]).multiply(f1[0]).multiply(f1[1])
					.add(BigDecimal.valueOf(fSum[0]).multiply(f1[1]).multiply(f1[0]))
					.add(BigDecimal.valueOf(fSum[0]).multiply(f1[1]).multiply(f1[1]))
					.add(BigDecimal.valueOf(fSum[1]).multiply(f1[0]).multiply(f1[0]))
					.add(BigDecimal.valueOf(fSum[1]).multiply(f1[0]).multiply(f1[1]))
					.add(BigDecimal.valueOf(fSum[1]).multiply(f1[1]).multiply(f1[0]))
					.add(BigDecimal.valueOf(fSum[1]).multiply(f1[1]).multiply(f1[1]));
			
			Parfactor expected = new StdParfactorBuilder().variables(jackpotWon).values(f2).build();
			
			Parfactor result = g1.sumOut(matched6);
			
			assertEquals(expected, result);
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
			AggregationParfactor input = new AggParfactorBuilder(matched6, jackpotWon, Or.OR).factor().build();
			
			double [] f = new double[2 * (populationSize + 1)];
			f[0] = 1.0;
			f[1] = 0.0;
			for (int i = 2; i < f.length; i++) {
				f[i] = (double)(i % 2);
			}
			
			Parfactor answer1 = new StdParfactorBuilder().variables(matched6counted, jackpotWon).values(f).build();
			Parfactor answer2 = new StdParfactorBuilder().variables(matched6).factor().build();
			
			Distribution answer = StdDistribution.of(answer1, answer2);
			
			Distribution result = input.toStdParfactors();
			
			answer.equals(result);
			assertThat(result, equalTo(answer));
			
		}
		
	}
}
