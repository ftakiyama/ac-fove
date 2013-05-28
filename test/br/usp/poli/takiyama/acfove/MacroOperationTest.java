package br.usp.poli.takiyama.acfove;

import static org.hamcrest.collection.IsIn.isIn;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import br.usp.poli.takiyama.acfove.AggParfactor.AggParfactorBuilder;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Or;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;

@RunWith(Enclosed.class)
public class MacroOperationTest {

	/**
	 * Tests unification/shattering between aggregation parfactors and
	 * standard parfactors.
	 */
	@RunWith(Parameterized.class)
	public static class UnificationTest {
		
		@Parameters
		public static Collection<Object[]> data() {
			
			LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
			LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
			LogicalVariable e = StdLogicalVariable.getInstance("E", "x", 10);
			LogicalVariable f = StdLogicalVariable.getInstance("F", "x", 10);
			
			Constant x1 = Constant.getInstance("x1");
			Constant x2 = Constant.getInstance("x2");
			Constant x3 = Constant.getInstance("x3");
			Constant x4 = Constant.getInstance("x4");
			
			Prv p_a_b = StdPrv.getBooleanInstance("p", a, b);
			Prv c_b = StdPrv.getBooleanInstance("c", b);
			Prv cPrime = StdPrv.getBooleanInstance("c'", b);
			Prv u = StdPrv.getBooleanInstance("u");
			Prv v_f = StdPrv.getBooleanInstance("v", f);
			Prv w_b = StdPrv.getBooleanInstance("w", b);
			
			Prv p_a_x1 = StdPrv.getBooleanInstance("p", a, x1);
			Prv p_a_x3 = StdPrv.getBooleanInstance("p", a, x3);
			Prv p_a_e = StdPrv.getBooleanInstance("p", a, e);
			Prv p_x1_x2 = StdPrv.getBooleanInstance("p", x1, x2);
			Prv p_x1_x3 = StdPrv.getBooleanInstance("p", x1, x3);
			Prv p_x1_e = StdPrv.getBooleanInstance("p", x1, e);
			Prv p_x1_b = StdPrv.getBooleanInstance("p", x1, b);
			Prv p_x2_x1 = StdPrv.getBooleanInstance("p", x2, x1);
			Prv p_x4_x1 = StdPrv.getBooleanInstance("p", x4, x1);
			Prv p_f_e = StdPrv.getBooleanInstance("p", f, e);
			Prv c_x1 = StdPrv.getBooleanInstance("c", x1);
			Prv c_x3 = StdPrv.getBooleanInstance("c", x3);
			Prv c_e = StdPrv.getBooleanInstance("c", e);
			Prv cPrime_x1 = StdPrv.getBooleanInstance("c'", x1);
			Prv cPrime_x3 = StdPrv.getBooleanInstance("c'", x3);
			Prv cPrime_e = StdPrv.getBooleanInstance("c'", e);
			
			Constraint a_x1 = InequalityConstraint.getInstance(a, x1);
			Constraint a_x2 = InequalityConstraint.getInstance(a, x2);
			Constraint a_x4 = InequalityConstraint.getInstance(a, x4);
			Constraint b_x1 = InequalityConstraint.getInstance(b, x1);
			Constraint b_x2 = InequalityConstraint.getInstance(b, x2);
			Constraint e_x1 = InequalityConstraint.getInstance(e, x1);
			Constraint e_x2 = InequalityConstraint.getInstance(e, x2);
			Constraint e_x3 = InequalityConstraint.getInstance(e, x3);
			Constraint f_x3 = InequalityConstraint.getInstance(f, x3);
			
			Parfactor g_1_in_1 = new AggParfactorBuilder(p_a_b, c_b, Or.OR).build();
			Parfactor g_1_in_2 = new StdParfactorBuilder().variables(p_a_x1).build();
			Parfactor g_1_out_1 = new AggParfactorBuilder(p_a_x1, c_x1, Or.OR).build();
			Parfactor g_1_out_2 = new AggParfactorBuilder(p_a_b, c_b, Or.OR).constraint(b_x1).build();
			Parfactor g_1_out_3 = new StdParfactorBuilder().variables(p_a_x1).build();
			
			Marginal<Prv> in1 = new StdMarginalBuilder().parfactors(g_1_in_1, g_1_in_2).build();
			Marginal<Prv> out1 = new StdMarginalBuilder().parfactors(g_1_out_1, g_1_out_2, g_1_out_3).build();
			
			Parfactor g_2_in_1 = new AggParfactorBuilder(p_a_b, c_b, Or.OR).build();
			Parfactor g_2_in_2 = new StdParfactorBuilder().variables(p_x1_e).build();
			Parfactor g_2_out_1 = new AggParfactorBuilder(p_a_b, cPrime, Or.OR).constraint(a_x1).build();
			Parfactor g_2_out_2 = new StdParfactorBuilder().variables(p_x1_e, cPrime_e, c_e).values(new double[] {1, 0, 0, 1, 0, 1, 0, 1}).build();
			Parfactor g_2_out_3 = new StdParfactorBuilder().variables(p_x1_e).build();
			Parfactor g_2_out_4 = new StdParfactorBuilder().variables(p_x1_b, cPrime, c_b).values(new double[] {1, 0, 0, 1, 0, 1, 0, 1}).build();
			Parfactor g_2_out_5 = new StdParfactorBuilder().variables(p_x1_b).build();
			
			Marginal<Prv> in2 = new StdMarginalBuilder().parfactors(g_2_in_1, g_2_in_2).build();
			Marginal<Prv> out2_1 = new StdMarginalBuilder().parfactors(g_2_out_1, g_2_out_2, g_2_out_3).eliminables(cPrime).build();
			Marginal<Prv> out2_2 = new StdMarginalBuilder().parfactors(g_2_out_1, g_2_out_4, g_2_out_5).eliminables(cPrime).build();
						
			Parfactor g_3_in_1 = new AggParfactorBuilder(p_a_b, c_b, Or.OR).constraints(b_x2, a_x4).build();
			Parfactor g_3_in_2 = new StdParfactorBuilder().variables(p_x1_e).constraints(e_x3).build();
			
			List<Parfactor> out = new ArrayList<Parfactor>();
			out.add(new AggParfactorBuilder(p_a_e, cPrime_e, Or.OR).constraints(e_x2, e_x3, a_x1, a_x4).build());
			out.add(new AggParfactorBuilder(p_a_x3, cPrime_x3, Or.OR).constraints(a_x1, a_x4).build());
			out.add(new StdParfactorBuilder().variables(p_x1_x3, cPrime_x3, c_x3).values(new double[] {1, 0, 0, 1, 0, 1, 0, 1}).build());
			out.add(new StdParfactorBuilder().variables(p_x1_e, cPrime_e, c_e).constraints(e_x2, e_x3).values(new double[] {1, 0, 0, 1, 0, 1, 0, 1}).build());
			out.add(new StdParfactorBuilder().variables(p_x1_x2).build());
			out.add(new StdParfactorBuilder().variables(p_x1_e).constraints(e_x3, e_x2).build());
									
			Marginal<Prv> in3 = new StdMarginalBuilder().parfactors(g_3_in_1, g_3_in_2).build();
			Marginal<Prv> out3_2 = new StdMarginalBuilder().parfactors(out).eliminables(cPrime_e).build();
			
			Parfactor g_4_in_1 = new AggParfactorBuilder(p_a_b, c_b, Or.OR).build();
			Parfactor g_4_in_2 = new AggParfactorBuilder(p_f_e, c_e, Or.OR).build();
			// expected result is g1,g2 or g3,g4 where:
			// - g.4.out.1 == g.4.out.2 
			// - g.4.out.3 == g.4.out.4
			Parfactor g_4_out_1 = new AggParfactorBuilder(p_a_b, c_b, Or.OR).build();
			Parfactor g_4_out_2 = new AggParfactorBuilder(p_a_b, c_b, Or.OR).build();
			Parfactor g_4_out_3 = new AggParfactorBuilder(p_f_e, c_e, Or.OR).build();
			Parfactor g_4_out_4 = new AggParfactorBuilder(p_f_e, c_e, Or.OR).build();
			
			Marginal<Prv> in4 = new StdMarginalBuilder().parfactors(g_4_in_1, g_4_in_2).build();
			Marginal<Prv> out4_1 = new StdMarginalBuilder().parfactors(g_4_out_1, g_4_out_2).build();
			Marginal<Prv> out4_2 = new StdMarginalBuilder().parfactors(g_4_out_3, g_4_out_4).build();
			
			Parfactor g_5_in_1 = new AggParfactorBuilder(p_a_b, c_b, Or.OR).context(u).build();
			Parfactor g_5_in_2 = new AggParfactorBuilder(p_f_e, c_e, Or.OR).build();
			// expected result is g1,g2 or g3,g4
			Parfactor g_5_out_1 = new AggParfactorBuilder(p_a_b, c_b, Or.OR).context(u).build();
			Parfactor g_5_out_2 = new AggParfactorBuilder(p_a_b, c_b, Or.OR).build();
			Parfactor g_5_out_3 = new AggParfactorBuilder(p_f_e, c_e, Or.OR).context(u).build();
			Parfactor g_5_out_4 = new AggParfactorBuilder(p_f_e, c_e, Or.OR).build();
			
			Marginal<Prv> in5 = new StdMarginalBuilder().parfactors(g_5_in_1, g_5_in_2).build();
			Marginal<Prv> out5_1 = new StdMarginalBuilder().parfactors(g_5_out_1, g_5_out_2).build();
			Marginal<Prv> out5_2 = new StdMarginalBuilder().parfactors(g_5_out_3, g_5_out_4).build();
			
			Parfactor g_6_in_1 = new AggParfactorBuilder(p_a_x1, c_x1, Or.OR).context(u).build();
			Parfactor g_6_in_2 = new AggParfactorBuilder(p_a_e, c_e, Or.OR).context(v_f).build();
			Parfactor g_6_out_1 = new AggParfactorBuilder(p_a_x1, c_x1, Or.OR).context(u).build();
			Parfactor g_6_out_2 = new AggParfactorBuilder(p_a_x1, c_x1, Or.OR).context(v_f).build();
			Parfactor g_6_out_3 = new AggParfactorBuilder(p_a_e, c_e, Or.OR).context(v_f).constraint(e_x1).build();
			
			Marginal<Prv> in6 = new StdMarginalBuilder().parfactors(g_6_in_1, g_6_in_2).build();
			Marginal<Prv> out6 = new StdMarginalBuilder().parfactors(g_6_out_1, g_6_out_2, g_6_out_3).build();
			
			Parfactor g_7_in_1 = new AggParfactorBuilder(p_a_x1, c_x1, Or.OR).context(w_b).constraints(b_x1, a_x2).build();
			Parfactor g_7_in_2 = new AggParfactorBuilder(p_a_e, c_e, Or.OR).context(v_f).constraints(f_x3, a_x4).build();
			Parfactor g_7_out_1 = new AggParfactorBuilder(p_a_x1, cPrime_x1, Or.OR).context(v_f).constraints(f_x3, a_x2, a_x4).build();
			Parfactor g_7_out_2 = new StdParfactorBuilder().variables(p_x2_x1, v_f, cPrime_x1, c_x1).constraints(f_x3).values(new double[] {1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1}).build();
			Parfactor g_7_out_3 = new AggParfactorBuilder(p_a_x1, cPrime_x1, Or.OR).context(w_b).constraints(b_x1, a_x2, a_x4).build();
			Parfactor g_7_out_4 = new StdParfactorBuilder().variables(p_x4_x1, w_b, cPrime_x1, c_x1).constraints(b_x1).values(new double[] {1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1}).build();
			Parfactor g_7_out_5 = new AggParfactorBuilder(p_a_e, c_e, Or.OR).context(v_f).constraints(f_x3, a_x4, e_x1).build();
						
			Marginal<Prv> in7 = new StdMarginalBuilder().parfactors(g_7_in_1, g_7_in_2).build();
			Marginal<Prv> out7 = new StdMarginalBuilder().parfactors(g_7_out_1, g_7_out_2, g_7_out_3, g_7_out_4, g_7_out_5).eliminables(cPrime_x1).build();
			
			return Arrays.asList(new Object[][] {
					{in1, new Object[] {out1}},
					{in2, new Object[] {out2_1, out2_2}},
					{in3, new Object[] {out3_2}},
					{in4, new Object[] {out4_1, out4_2}},
					{in5, new Object[] {out5_1, out5_2}},
					{in6, new Object[] {out6}},
					{in7, new Object[] {out7}},
			});
		}
		
		private Marginal<Prv> input;
		private List<Object> expected;
		
		public UnificationTest(Marginal<Prv> in, Object ... out) {
			input = in;
			expected = Arrays.asList(out);
		}
				
		@Test
		public void testUnification() {
			MacroOperation shatter = new Shatter(input);
			shatter.run();
			org.junit.Assert.assertThat(shatter.marginal(), isIn(expected));
		}
		
	}
	
	/**
	 * Example 2.19 to 2.22 from Kisynki[2010] - unification of standard
	 * parfactors.
	 */
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
			
			Marginal<Prv> marginal = new StdMarginalBuilder().parfactors(g1, g2, g3, g4).build();
			
			MacroOperation shatter = new Shatter(marginal);
			shatter.run();
			
			Marginal<Prv> result = shatter.marginal();
			Marginal<Prv> expected = new StdMarginalBuilder().parfactors(g1, g4, g5, g6, g7, g8).build();
			
			assertEquals(expected, result);
		}
	}
}