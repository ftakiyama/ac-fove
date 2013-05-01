package br.usp.poli.takiyama.acfove;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.usp.poli.takiyama.acfove.AggParfactor.AggParfactorBuilder;
import br.usp.poli.takiyama.cfove.StdParfactor;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Or;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;


public class AggParfactorTest {
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
		
		double [] fSum = {0.9999999965, 0.0000000035};
		
		Parfactor g3 = new AggParfactorBuilder(matched6, jackpotWon, Or.OR)
				.values(fSum).build();
		
//		objects.setMultiplicationAggParfactor();
//		
//		Parfactor g1 = objects.getSimpleParfactor("g1");
//		Parfactor g2 = objects.getSimpleParfactor("g2");
//		Parfactor g3 = objects.getAggParfactor("g3");
//		Parfactor temp = g1.multiply(g2);
//		ParameterizedRandomVariable played = 
//				objects.getParameterizedRandomVariable("played");
//		temp = temp.sumOut(played);
//		Parfactor result = g3.multiply(temp);
//		
//		Parfactor answer = objects.getAggParfactor("g4");
//		
//		assertTrue(result.equals(answer));
	}
	
	
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
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 100);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 100);
		
		Prv p = StdPrv.getBooleanInstance("p", a, b);
		Prv c = StdPrv.getBooleanInstance("c", b);
		
		Constant x1 = Constant.getInstance("x1");
		Constant x2 = Constant.getInstance("x2");
		Constant x3 = Constant.getInstance("x3");
		
		Constraint a_x1 = InequalityConstraint.getInstance(a, x1);
		Constraint a_x2 = InequalityConstraint.getInstance(a, x2);
		Constraint a_b = InequalityConstraint.getInstance(a, b);
		Constraint b_x3 = InequalityConstraint.getInstance(b, x3);
		
		double [] f1 = {0.1234, 0.9876};
		
		Parfactor g1 = new StdParfactorBuilder()
				.constraints(a_x1, a_x2, a_b, b_x3).variables(p).values(f1).build();

		double [] f2 = {0.5425, 0.6832};
		
		Parfactor g2 = new AggParfactorBuilder(p, c, Or.OR)
				.constraints(a_x1, a_x2, a_b, b_x3).values(f2).build();
		
		Parfactor result = g2.multiply(g1);
		
		List<BigDecimal> f3 = new ArrayList<BigDecimal>(2);
		f3.add(BigDecimal.valueOf(f1[0]).multiply(BigDecimal.valueOf(f2[0])));
		f3.add(BigDecimal.valueOf(f1[1]).multiply(BigDecimal.valueOf(f2[1])));
		
		Parfactor answer = new AggParfactorBuilder(p, c, Or.OR)
				.constraints(a_x1, a_x2, a_b, b_x3).values(f3).build();

		assertTrue(result.equals(answer));
		
	}
	
	@Test
	public void testMultiplicationCommutativity() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 100);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 100);
		
		Prv p = StdPrv.getBooleanInstance("p", a, b);
		Prv c = StdPrv.getBooleanInstance("c", b);
		
		Constant x1 = Constant.getInstance("x1");
		Constant x2 = Constant.getInstance("x2");
		Constant x3 = Constant.getInstance("x3");
		
		Constraint a_x1 = InequalityConstraint.getInstance(a, x1);
		Constraint a_x2 = InequalityConstraint.getInstance(a, x2);
		Constraint a_b = InequalityConstraint.getInstance(a, b);
		Constraint b_x3 = InequalityConstraint.getInstance(b, x3);
		
		double [] f1 = {0.1234, 0.9876};
		
		Parfactor g1 = new StdParfactorBuilder()
				.constraints(a_x1, a_x2, a_b, b_x3).variables(p).values(f1).build();

		double [] f2 = {0.5425, 0.6832};
		
		Parfactor g2 = new AggParfactorBuilder(p, c, Or.OR)
				.constraints(a_x1, a_x2, a_b, b_x3).values(f2).build();
		
		Parfactor direct = g2.multiply(g1);
		Parfactor inverse = g1.multiply(g2);
		
		assertTrue(direct.equals(inverse));
	}
}
