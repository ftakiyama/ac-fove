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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.usp.poli.takiyama.acfove.AggParfactor.AggParfactorBuilder;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.And;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Or;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.utils.TestUtils;


public class Temp {
	
	
	@Ignore
	@Test
	public void testInferenceOnExistsNode_AggregationSumOut() {
		
		int n = 2;
		
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", n);

		Constant x1 = Constant.getInstance("x1");
		Constant x2 = Constant.getInstance("x2");
		
		Prv a_x1 = StdPrv.getBooleanInstance("a", x1);
		Prv b_x1 = StdPrv.getBooleanInstance("b", x1);
		Prv b_x2 = StdPrv.getBooleanInstance("b", x2);
		Prv e_x1 = StdPrv.getBooleanInstance("e", x1);
		Prv e_x2 = StdPrv.getBooleanInstance("e", x2);
		Prv r1_x1_y = StdPrv.getBooleanInstance("r1", x1, y);
		Prv r1_x2_y = StdPrv.getBooleanInstance("r1", x2, y);

		List<BigDecimal> fb = TestUtils.toBigDecimalList(0.1, 0.9);
		List<BigDecimal> fbr = TestUtils.toBigDecimalList(1.0, 0, 0.2, 0.8);
		List<BigDecimal> fea = TestUtils.toBigDecimalList(1.0, 0.0, 0.0, 1.0);
		
		Parfactor ga1 = new StdParfactorBuilder().variables(a_x1).build();
		Parfactor ge2 = new StdParfactorBuilder().variables(e_x2).build();
		Parfactor gb1 = new StdParfactorBuilder().variables(b_x1).values(fb).build();
		Parfactor gb2 = new StdParfactorBuilder().variables(b_x2).values(fb).build();
		Parfactor gbr1 = new StdParfactorBuilder().variables(b_x1, r1_x1_y).values(fbr).build();
		Parfactor gbr2 = new StdParfactorBuilder().variables(b_x2, r1_x2_y).values(fbr).build();
		Parfactor gea = new StdParfactorBuilder().variables(e_x1, a_x1).values(fea).build();
		Parfactor gre1 = new AggParfactorBuilder(r1_x1_y, e_x1, Or.OR).context(b_x1).build();
		Parfactor gre2 = new AggParfactorBuilder(r1_x2_y, e_x2, Or.OR).context(b_x2).build();
		
		RandomVariableSet query = RandomVariableSet.getInstance(a_x1, new HashSet<Constraint>(0));
		Marginal input = new StdMarginalBuilder().parfactors(ga1, ge2, gb1, gb2, gbr1, gbr2, gea, gre1, gre2).preservable(query).build();
		
		ACFOVE acfove = new ACFOVE(input);
		Parfactor result = acfove.run();
		
		System.out.print(result);
	}
	

	@Ignore
	@Test
	public void testInferenceOnExistsNode_AnotherAggregationSumOut() {
		
		int n = 2;
		
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", n);

		Constant x1 = Constant.getInstance("x1");
		Constant x2 = Constant.getInstance("x2");
		
		Prv a_x1 = StdPrv.getBooleanInstance("a", x1);
		Prv b_x2 = StdPrv.getBooleanInstance("b", x2);
		Prv e_x2 = StdPrv.getBooleanInstance("e", x2);
		Prv r1_x2_y = StdPrv.getBooleanInstance("r1", x2, y);

		List<BigDecimal> fa = TestUtils.toBigDecimalList(0.1360, 0.8640);
		List<BigDecimal> fb = TestUtils.toBigDecimalList(0.1, 0.9);
		List<BigDecimal> fbr = TestUtils.toBigDecimalList(1.0, 0, 0.2, 0.8);
		
		Parfactor ga1 = new StdParfactorBuilder().variables(a_x1).build();
		Parfactor ga12 = new StdParfactorBuilder().variables(a_x1).values(fa).build();
		Parfactor ge2 = new StdParfactorBuilder().variables(e_x2).build();
		Parfactor gb2 = new StdParfactorBuilder().variables(b_x2).values(fb).build();
		Parfactor gbr2 = new StdParfactorBuilder().variables(b_x2, r1_x2_y).values(fbr).build();
		Parfactor gre2 = new AggParfactorBuilder(r1_x2_y, e_x2, Or.OR).context(b_x2).build();
		
		RandomVariableSet query = RandomVariableSet.getInstance(a_x1, new HashSet<Constraint>(0));
		Marginal input = new StdMarginalBuilder().parfactors(ga1, ga12, ge2, gb2, gbr2, gre2).preservable(query).build();
		
		ACFOVE acfove = new ACFOVE(input);
		Parfactor result = acfove.run();
		
		System.out.print(result);
	}
	
	
	
	@Ignore
	@Test
	public void testInferenceOnExistsNode() {
		int n = 2;
		
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", n);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", n);

		Constant x1 = Constant.getInstance("x1");
		
		Prv a = StdPrv.getBooleanInstance("a", x);
		Prv a1 = StdPrv.getBooleanInstance("a", x1);
		Prv b = StdPrv.getBooleanInstance("b", y);
		Prv e = StdPrv.getBooleanInstance("e", x);
		Prv r = StdPrv.getBooleanInstance("r", x, y);
		Prv r1 = StdPrv.getBooleanInstance("r1", x, y);
		
		List<BigDecimal> fb = TestUtils.toBigDecimalList(0.1, 0.9);
		List<BigDecimal> fr = TestUtils.toBigDecimalList(0.2, 0.8);
		List<BigDecimal> fr1 = TestUtils.toBigDecimalList(1, 0, 1, 0, 1, 0, 0, 1);
		List<BigDecimal> fa = TestUtils.toBigDecimalList(1, 0, 0, 1);

		Parfactor gb = new StdParfactorBuilder().variables(b).values(fb).build();
		Parfactor gr = new StdParfactorBuilder().variables(r).values(fr).build();
		Parfactor gr1 = new StdParfactorBuilder().variables(b, r, r1).values(fr1).build();
		//Parfactor ge = new AggParfactorBuilder(r1, e, Or.OR).context(b).build();
		Parfactor ge = new AggParfactorBuilder(r1, e, Or.OR).build();
		Parfactor ga = new StdParfactorBuilder().variables(e, a).values(fa).build();
		
		RandomVariableSet query = RandomVariableSet.getInstance(a1, new HashSet<Constraint>(0));
		Marginal input = new StdMarginalBuilder().parfactors(gb, gr, gr1, ge, ga).preservable(query).build();
		
		ACFOVE acfove = new ACFOVE(input);
		Parfactor result = acfove.run();
		
		System.out.print(result);
	}
	
	@Ignore
	@Test
	public void testInferenceOnWellBehavedTerminology() {
		
	}
	
	@Ignore("Generates a very large parfactor, causing out of memory error")
	@Test
	public void testInferenceOnTestTerminology() {
		
		int n = 3;
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", n);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", n);
		
		Constant x1 = Constant.getInstance("x1");
		
		Prv a = StdPrv.getBooleanInstance("a", x);
		Prv b = StdPrv.getBooleanInstance("b", x);
		Prv c = StdPrv.getBooleanInstance("c", x);
		Prv d = StdPrv.getBooleanInstance("d", x);
		Prv e = StdPrv.getBooleanInstance("e", x);
		Prv f = StdPrv.getBooleanInstance("f", x);
		Prv r = StdPrv.getBooleanInstance("r", x, y);
		Prv r1 = StdPrv.getBooleanInstance("r1", x, y);
		Prv r2 = StdPrv.getBooleanInstance("r2", x, y);
		Prv c1 = StdPrv.getBooleanInstance("c", x1);
		
		List<BigDecimal> f1 = TestUtils.toBigDecimalList(0.1, 0.9);
		List<BigDecimal> f2 = TestUtils.toBigDecimalList(1.0, 0.0, 0.55, 0.45);
		List<BigDecimal> f3 = TestUtils.toBigDecimalList(1, 0, 0, 1, 0, 1, 0, 1);
		List<BigDecimal> f5 = TestUtils.toBigDecimalList(1, 0, 0, 1);
		List<BigDecimal> f7 = TestUtils.toBigDecimalList(0, 1, 1, 0, 0, 1, 0, 1);
		List<BigDecimal> f8 = TestUtils.toBigDecimalList(0.7, 0.3);
		List<BigDecimal> f9 = TestUtils.toBigDecimalList(1, 0, 1, 0, 1, 0, 0, 1);
		
		Parfactor g1 = new StdParfactorBuilder().variables(a).values(f1).build();
		Parfactor g2 = new StdParfactorBuilder().variables(a, b).values(f2).build();
		Parfactor g3 = new StdParfactorBuilder().variables(b, e, c).values(f3).build();
//		Parfactor g4 = new AggParfactorBuilder(r1, f, And.AND).context(a).build();
		Parfactor g4 = new AggParfactorBuilder(r1, f, And.AND).build();
		Parfactor g5 = new StdParfactorBuilder().variables(f, d).values(f5).build();
//		Parfactor g6 = new AggParfactorBuilder(r2, e, Or.OR).context(d).build();
		Parfactor g6 = new AggParfactorBuilder(r2, e, Or.OR).build();
		Parfactor g7 = new StdParfactorBuilder().variables(a, r, r1).values(f7).build();
		Parfactor g8 = new StdParfactorBuilder().variables(r).values(f8).build();
		Parfactor g9 = new StdParfactorBuilder().variables(d, r, r2).values(f9).build();

		// Fix this later: shatter against query should work for c(x1)
//		Set<Constraint> constraints = new HashSet<Constraint>(n - 1);
//		for (int i = 1; i < n; i++) {
//			Term individual = x.population().individualAt(i);
//			Constraint constraint = InequalityConstraint.getInstance(x, individual);
//			constraints.add(constraint);
//		}
		
		RandomVariableSet query = RandomVariableSet.getInstance(c1, new HashSet<Constraint>(0));
		Marginal input = new StdMarginalBuilder().parfactors(g1, g2, g3, g4, g5, g6, g7, g8, g9).preservable(query).build();
		

		ACFOVE acfove = new ACFOVE(input);
		Parfactor result = acfove.run();
		
		System.out.print(result);
		
//		try {
//			ACFOVE acfove = new LoggedACFOVE(input);
//			Parfactor result = acfove.run();
//		} catch (IOException exception) {
//			exception.printStackTrace();
//			System.exit(-1);
//		}
	}
	
	@Ignore
	@Test
	public void testInferenceOnExistsNodeSimplified() {
		int n = 3;
		
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", n);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", n);
		
		Prv b = StdPrv.getBooleanInstance("b", y);
		Prv e = StdPrv.getBooleanInstance("e", x);
		Prv r = StdPrv.getBooleanInstance("r", x, y);
		Prv r1 = StdPrv.getBooleanInstance("r'", x, y);
		
		List<BigDecimal> fb = TestUtils.toBigDecimalList(0.1, 0.9);
		List<BigDecimal> fr = TestUtils.toBigDecimalList(0.2, 0.8);
		List<BigDecimal> fr1 = TestUtils.toBigDecimalList(1, 0, 1, 0, 1, 0, 0, 1);
		
		Parfactor gb = new StdParfactorBuilder().variables(b).values(fb).build();
		Parfactor gr = new StdParfactorBuilder().variables(r).values(fr).build();
		Parfactor gr1 = new StdParfactorBuilder().variables(b, r, r1).values(fr1).build();
		Parfactor ge = new AggParfactorBuilder(r1, e, Or.OR).build();
		
		RandomVariableSet query = RandomVariableSet.getInstance(e, new HashSet<Constraint>(0));
		Marginal input = new StdMarginalBuilder().parfactors(gb, gr, gr1, ge).preservable(query).build();
		
		ACFOVE acfove = new ACFOVE(input);
		Parfactor result = acfove.run();
		
		System.out.print(result);
	}
	
	@Test
	@Ignore
	public void testSumOut1() {
		
		int n = 2;
		
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", n);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", n);
		
		Prv b = StdPrv.getBooleanInstance("b", y);
		Prv e = StdPrv.getBooleanInstance("e", x);
		Prv r1 = StdPrv.getBooleanInstance("r'", x, y);

		List<BigDecimal> fb = TestUtils.toBigDecimalList(0.2, 0.8);
		Parfactor gb = new StdParfactorBuilder().variables(b).values(fb).build();
		
		double pr = 0;
		List<BigDecimal> f = TestUtils.toBigDecimalList(1.0, pr, 0.0, 1 - pr);
		Parfactor ge = new AggParfactorBuilder(r1, e, Or.OR).context(b).values(f).build();
		
		Parfactor result = ge.sumOut(r1);
		result = result.multiply(gb);
		
		System.out.print(result);
	}
	
	@Test
	@Ignore
	public void testIncrement() {
		for (int i = 1; i < 10001; i = i + inc2(i)) {
			System.out.println(i);
		}
	}
	
	private int inc(int i) {
		int e = (int) Math.log10(i);
		int r = (int) Math.pow(10.0, (double) e);
		return r;
	}
	
	private int inc2(int i) {
		int r = (int) Math.pow(10.0, (int) Math.log10(i));
		return r;
	}
	
	@Test
	public void smallMath() {
		System.out.println(500*1073741824);
		//System.out.println(  ((int) Math.pow(2, 30)));
	}
}
