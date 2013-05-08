package br.usp.poli.takiyama.cfove;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.FactorTest;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdSplitResult;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Operator;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RangeElement;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.MathUtils;


/**
 * Unit tests for {@link StdParfactor}s.
 * 
 * @author Felipe Takiyama
 */
public class StdParfactorTest {
	
	/**
	 * Example 2.15 from [Kisynski,2010].
	 * <p>
	 * Splits parfactor <C,V,F> on substitution {B/x1}, where C = {A &ne; B},
	 * V = {f(A, B), h(B)} and F is given by
	 * </p>
	 * <p>
	 * <table border="1">
	 * <tr><th>f(A,B)</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>0.2</td></tr>
	 * <tr><td>false</td><td>true</td><td>0.3</td></tr>
	 * <tr><td>true</td><td>false</td><td>0.5</td></tr>
	 * <tr><td>true</td><td>true</td><td>0.7</td></tr>
	 * </table>
	 * </p>
	 * <p>
	 * The result of this operation is parfactor g[B/x1], where C[B/x1] = 
	 * {A &ne; x1}, V[B/x1] = {f(A, x1), h(x1)} and F[B/x1] has the same values
	 * as F. The residue is parfactor g', where C' = {A &ne; B. B &ne; x1}, 
	 * V' = V and F' = F.
	 * </p>
	 */
	@Test
	public void testSplit() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		
		Constant x1 = Constant.getInstance("x1");
		
		Prv f = StdPrv.getBooleanInstance("f", a, b);
		Prv h = StdPrv.getBooleanInstance("h", b);
		Prv f1 = StdPrv.getBooleanInstance("f", a, x1);
		Prv h1 = StdPrv.getBooleanInstance("h", x1);
		
		Constraint ab = InequalityConstraint.getInstance(a, b);
		Constraint a0 = InequalityConstraint.getInstance(a, x1);
		Constraint b0 = InequalityConstraint.getInstance(b, x1);
		
		double [] vals = {0.2, 0.3, 0.5, 0.7};
		
		Parfactor input = new StdParfactorBuilder().constraints(ab)
				.variables(f, h).values(vals).build();
		
		Binding binding = Binding.getInstance(b, x1);
		Substitution sub = Substitution.getInstance(binding);
		SplitResult output = input.splitOn(sub);
		
		Parfactor result = new StdParfactorBuilder().constraints(a0)
				.variables(f1, h1).values(vals).build();
		
		Parfactor residue = new StdParfactorBuilder().constraints(ab, b0)
				.variables(f, h).values(vals).build();
		
		SplitResult answer = StdSplitResult.getInstance(result, residue);
		
		assertTrue(output.equals(answer));
	}
	
	
	/**
	 * Example 2.14 from Kisysnki (2010)
	 * <p>
	 * Multiplies parfactor g1 = &langle; {A&ne;B}, {f(A,B),h(B)}, F1 &rangle;
	 * with parfactor g2 = &langle; &empty;, {e(C),h(B)}, F2 &rangle;, where
	 * </p>
	 * <p>
	 * F1
	 * <table border="1">
	 * <tr><th>f(A,B)</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>&alpha;1</td></tr>
	 * <tr><td>false</td><td>true</td><td>&alpha;2</td></tr>
	 * <tr><td>true</td><td>false</td><td>&alpha;3</td></tr>
	 * <tr><td>true</td><td>true</td><td>&alpha;4</td></tr>
	 * </table>
	 * </p>
	 * <p>
	 * F2
	 * <table border="1">
	 * <tr><th>e(C)</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>green</td><td>false</td><td>&beta;1</td></tr>
	 * <tr><td>green</td><td>true</td><td>&beta;2</td></tr>
	 * <tr><td>orange</td><td>false</td><td>&beta;3</td></tr>
	 * <tr><td>orange</td><td>true</td><td>&beta;4</td></tr>
	 * <tr><td>red</td><td>false</td><td>&beta;5</td></tr>
	 * <tr><td>red</td><td>true</td><td>&beta;6</td></tr>
	 * </table>
	 * </p>
	 * <p>
	 * D(A) = D(B) = {x1,...,xn} and D(C) = {y1,...,ym}. The result of this
	 * operation is parfactor g = &langle; {A&ne;B}, {f(A,B),h(B),e(C)}, 
	 * F &rangle;, where 
	 * <p>
	 * F
	 * <table border="1">
	 * <tr><th>f(A,B)</th><th>h(B)</th><th>e(C)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>green</td>  <td>&alpha;1<sup>1/m</sup>&beta;1<sup>1/(n-1)</sup></td></tr>
	 * <tr><td>false</td><td>false</td><td>orange</td> <td>&alpha;1<sup>1/m</sup>&beta;3<sup>1/(n-1)</sup></td></tr>
	 * <tr><td>false</td><td>false</td><td>red</td>    <td>&alpha;1<sup>1/m</sup>&beta;5<sup>1/(n-1)</sup></td></tr>
	 * <tr><td>false</td><td>true</td><td>green</td>   <td>&alpha;2<sup>1/m</sup>&beta;2<sup>1/(n-1)</sup></td></tr>
	 * <tr><td>false</td><td>true</td><td>orange</td>  <td>&alpha;2<sup>1/m</sup>&beta;4<sup>1/(n-1)</sup></td></tr>
	 * <tr><td>false</td><td>true</td><td>red</td>     <td>&alpha;2<sup>1/m</sup>&beta;6<sup>1/(n-1)</sup></td></tr>
	 * <tr><td>true</td><td>false</td><td>green</td>   <td>&alpha;3<sup>1/m</sup>&beta;1<sup>1/(n-1)</sup></td></tr>
	 * <tr><td>true</td><td>false</td><td>orange</td>  <td>&alpha;3<sup>1/m</sup>&beta;3<sup>1/(n-1)</sup></td></tr>
	 * <tr><td>true</td><td>false</td><td>red</td>     <td>&alpha;3<sup>1/m</sup>&beta;5<sup>1/(n-1)</sup></td></tr>
	 * <tr><td>true</td><td>true</td><td>green</td>    <td>&alpha;4<sup>1/m</sup>&beta;2<sup>1/(n-1)</sup></td></tr>
 	 * <tr><td>true</td><td>true</td><td>orange</td>   <td>&alpha;4<sup>1/m</sup>&beta;4<sup>1/(n-1)</sup></td></tr>
 	 * <tr><td>true</td><td>true</td><td>red</td>      <td>&alpha;4<sup>1/m</sup>&beta;6<sup>1/(n-1)</sup></td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testMultiplication() {
		
		// Population size for A and B
		int n = 5;
		
		// Population size for C
		int m = 6;
		
		// Logical variables A, B and C
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", n);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", n);
		LogicalVariable c = StdLogicalVariable.getInstance("C", "y", m);
		
		// PRVs f(A,B) and h(B)
		Prv f = StdPrv.getBooleanInstance("f", a, b);
		Prv h = StdPrv.getBooleanInstance("h", b);
		
		// PRV e(C), where range(e) = {green, orange, red}
		List<RangeElement> eRange = new ArrayList<RangeElement>(
				Arrays.asList(Color.values()));
		List<Term> eParam = new ArrayList<Term>(1);
		eParam.add(c);
		Prv e = StdPrv.getInstance("e", eRange, eParam);
		
		// Constraint A != B
		Constraint ab = InequalityConstraint.getInstance(a, b);
		
		// Parfactor g1
		double [] g1vals = {1.0, 2.0, 3.0, 4.0}; 
		Parfactor g1 = new StdParfactorBuilder().constraints(ab)
				.variables(f, h).values(g1vals).build();
		
		// Parfactor g2
		double [] g2vals = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0}; 
		Parfactor g2 = new StdParfactorBuilder().variables(e, h)
				.values(g2vals).build();
		
		Parfactor product = g1.multiply(g2);
		
		// Answer
		List<BigDecimal> ansVals = new ArrayList<BigDecimal>(12);
		for (int i = 0; i < g1vals.length; i++) {
			BigDecimal alpha = BigDecimal.valueOf(g1vals[i]);
			BigDecimal alphaM = MathUtils.pow(alpha, 1, m);
			for (int j = i % 2; j < g2vals.length; j = j + 2) {
				BigDecimal beta = BigDecimal.valueOf(g2vals[j]);
				BigDecimal betaN = MathUtils.pow(beta, 1, n - 1);
				ansVals.add(alphaM.multiply(betaN));
			}
		}
		Parfactor answer = new StdParfactorBuilder().constraints(ab)
				.variables(f, h, e).values(ansVals).build();
		
		assertTrue(product.equals(answer));
	}
	
	
	/**
	 * Range for PRV e(C), specified above.
	 * @see testMultiplication (broken link)
	 */
	private enum Color implements RangeElement {
		GREEN, ORANGE, RED;

		/**
		 * Throws {@link UnsupportedOperationException}.
		 */
		public RangeElement combine(RangeElement e) {
			throw new UnsupportedOperationException("Not implemented");
		}

		/**
		 * Throws {@link UnsupportedOperationException}.
		 */
		@Override
		public RangeElement apply(Operator<? extends RangeElement> op) {
			throw new UnsupportedOperationException("Not implemented");
		}
	}
	
	
	/**
	 * Sums out #.A[f(A)]  from factor
	 * <p>
	 * <table border="1">
	 * <tr><th>#.A[f(A)]</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>(#.false = 1, #.true = 0)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 1, #.true = 0)</td><td>true</td><td>10</td></tr>
	 * <tr><td>(#.false = 0, #.true = 1)</td><td>false</td><td>100</td></tr>
	 * <tr><td>(#.false = 0, #.true = 1)</td><td>true</td><td>1000</td></tr>
	 * </table>
	 * </p>
	 * which results in factor
	 * <p>
	 * <table border="1">
	 * <tr><th>h(B)</th><th>values</th></tr>
	 * <tr><td>false</td><td>101</td></tr>
	 * <tr><td>true</td><td>1010</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testSumOutCountingFormulaWithCardinality1() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 1);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		Prv cf = CountingFormula.getInstance(a, f);
		
		double [] vals = {1.0, 10.0, 100.0, 1000.0};
		Parfactor input = new StdParfactorBuilder().variables(cf, h)
				.values(vals).build();
		
		Parfactor result = input.sumOut(cf);
		
		double [] ansVals = {101.0, 1010.0};
		Parfactor answer = new StdParfactorBuilder().variables(h)
				.values(ansVals).build();

		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * @see FactorTest#testSumOutCountingFormula()
	 */
	@Test
	public void testSumOutCountingFormulaWithCardinality2() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 2);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		Prv cf = CountingFormula.getInstance(a, f);
		
		double [] vals = {1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0};
		Parfactor input = new StdParfactorBuilder().variables(cf, h)
				.values(vals).build();
		
		Parfactor result = input.sumOut(cf);
		
		double [] ansVals = {10201.0, 102010.0};
		Parfactor answer = new StdParfactorBuilder().variables(h)
				.values(ansVals).build();

		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * @see FactorTest#testSumOutBiggerCountingFormula()
	 */
	@Test
	public void testSumOutCountingFormulaWithCardinality10() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		Prv cf = CountingFormula.getInstance(a, f);
		
		double [] vals = new double[22];
		Arrays.fill(vals, 1.0);
		Parfactor input = new StdParfactorBuilder().variables(cf, h)
				.values(vals).build();
		
		Parfactor result = input.sumOut(cf);
		
		double [] ansVals = {1024.0, 1024.0};
		Parfactor answer = new StdParfactorBuilder().variables(h)
				.values(ansVals).build();

		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Example 2.13 from Kisysnki (2010) [adapted]. Sums out 
	 * #.A:{A&ne;B}[f(A)] from factor
	 * <p>
	 * <table border="1">
	 * <tr><th>#.A:{A&ne;B}[f(A)]</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>(#.false = 0, #.true = 2)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 0, #.true = 2)</td><td>true</td><td>10</td></tr>
	 * <tr><td>(#.false = 1, #.true = 1)</td><td>false</td><td>100</td></tr>
	 * <tr><td>(#.false = 1, #.true = 1)</td><td>true</td><td>1000</td></tr>
	 * <tr><td>(#.false = 2, #.true = 0)</td><td>false</td><td>10000</td></tr>
	 * <tr><td>(#.false = 2, #.true = 0)</td><td>true</td><td>100000</td></tr>
	 * </table>
	 * </p>
	 * which results in factor
	 * <p>
	 * <table border="1">
	 * <tr><th>h(B)</th><th>values</th></tr>
	 * <tr><td>false</td><td>10201</td></tr>
	 * <tr><td>true</td><td>102010</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testSumOutCountingFormula() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 3);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		
		Constraint ab = InequalityConstraint.getInstance(a, b);
		Prv cf = CountingFormula.getInstance(a, f, ab);
		
		double [] vals = {1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0};
		Parfactor input = new StdParfactorBuilder().variables(cf, h)
				.values(vals).build();
		
		Parfactor result = input.sumOut(cf);
		
		double [] ansVals = {10201.0, 102010.0};
		Parfactor answer = new StdParfactorBuilder().variables(h)
				.values(ansVals).build();

		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Example 2.16 from Kisysnki (2010) [adapted]. Expands  
	 * #.A:{A&ne;x1}[f(A)] from parfactor 
	 * g = &langle; &empty;, {#.A:{A&ne;x1}[f(A)],h(B)}, F1 &rangle;
	 * on constant x2. We use D(A) = {x1, x2, x3}. Factor F1 is given by
	 * <p>
	 * <table border="1">
	 * <tr><th>#.A:{A&ne;x1}[f(A)]</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>(#.false = 2, #.true = 0)</td><td>false</td><td>0.2</td></tr>
	 * <tr><td>(#.false = 2, #.true = 0)</td><td>true</td><td>0.3</td></tr>
	 * <tr><td>(#.false = 1, #.true = 1)</td><td>false</td><td>0.5</td></tr>
	 * <tr><td>(#.false = 1, #.true = 1)</td><td>true</td><td>0.7</td></tr>
	 * <tr><td>(#.false = 0, #.true = 2)</td><td>false</td><td>0.11</td></tr>
	 * <tr><td>(#.false = 0, #.true = 2)</td><td>true</td><td>0.13</td></tr>
	 * </table>
	 * </p>
	 * <p>
	 * The result is parfactor 
	 * g = &langle; &empty;, {#.A:{A&ne;x1', A&ne;x2}[f(A)],h(B)}, F2 &rangle;,
	 * where F2 is
	 * </p>
	 * <p>
	 * <table border="1">
	 * <tr><th>#.A:{A&ne;x1, A&ne;x2}[f(A)]</th><th>f(x2)</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>(#.false = 1, #.true = 0)</td><td>false</td><td>false</td><td>0.2</td></tr>
	 * <tr><td>(#.false = 1, #.true = 0)</td><td>false</td><td>true</td><td>0.3</td></tr>
	 * <tr><td>(#.false = 1, #.true = 0)</td><td>true</td><td>false</td><td>0.5</td></tr>
	 * <tr><td>(#.false = 1, #.true = 0)</td><td>true</td><td>true</td><td>0.7</td></tr>
	 * <tr><td>(#.false = 0, #.true = 1)</td><td>false</td><td>false</td><td>0.5</td></tr>
	 * <tr><td>(#.false = 0, #.true = 1)</td><td>false</td><td>true</td><td>0.7</td></tr>
	 * <tr><td>(#.false = 0, #.true = 1)</td><td>true</td><td>false</td><td>0.11</td></tr>
	 * <tr><td>(#.false = 0, #.true = 1)</td><td>true</td><td>true</td><td>0.13</td></tr>
	 * </table>
	 * </p>
	 * <p>
	 * The result can also be written as 
	 * </p>
	 * <p>
	 * <table border="1">
	 * <tr><th>f(x3)</th><th>f(x2)</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>false</td><td>0.2</td></tr>
	 * <tr><td>false</td><td>false</td><td>true</td><td>0.3</td></tr>
	 * <tr><td>false</td><td>true</td><td>false</td><td>0.5</td></tr>
	 * <tr><td>false</td><td>true</td><td>true</td><td>0.7</td></tr>
	 * <tr><td>true</td><td>false</td><td>false</td><td>0.5</td></tr>
	 * <tr><td>true</td><td>false</td><td>true</td><td>0.7</td></tr>
	 * <tr><td>true</td><td>true</td><td>false</td><td>0.11</td></tr>
	 * <tr><td>true</td><td>true</td><td>true</td><td>0.13</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testExpansion() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 3);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Constant x1 = Constant.getInstance("x1");
		Constant x2 = Constant.getInstance("x2");
		Constant x3 = Constant.getInstance("x3");
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		Prv f_x2 = StdPrv.getBooleanInstance("f", x2);
		Prv f_x3 = StdPrv.getBooleanInstance("f", x3);
		
		Constraint a_x1 = InequalityConstraint.getInstance(a, x1);
		
		Prv cf = CountingFormula.getInstance(a, f, a_x1);
		
		Parfactor input = new StdParfactorBuilder().variables(cf, h)
				.values(0.2, 0.3, 0.5, 0.7, 0.11, 0.13).build();
		
		Parfactor result = input.expand(cf, x2);
		
		Parfactor answer = new StdParfactorBuilder().variables(f_x3, f_x2, h)
				.values(0.2, 0.3, 0.5, 0.7, 0.5, 0.7, 0.11, 0.13).build();

		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Counts PRV f on parfactor g = &langle; &empty;, {f(A)}, F1 &rangle;
	 * where
	 * <p>
	 * F1
	 * <table border="1">
	 * <tr><th>f(A)</th><th>values</th></tr>
	 * <tr><td>false</td><td>2</td></tr>
	 * <tr><td>true</td><td>3</td></tr>
	 * </table>
	 * </p>
	 * <p>
	 * D(A) = {x1,x2,x3}. The result of this
	 * operation is parfactor g' = &langle; &empty;, {#.A[f(A)]}, F2 &rangle;, 
	 * where 
	 * <p>
	 * F2
	 * <table border="1">
	 * <tr><th>#.A[f(A)]</th><th>values</th></tr>
	 * <tr><td>(#.false = 3, #.true = 0)</td><td>8</td></tr>
	 * <tr><td>(#.false = 2, #.true = 1)</td><td>12</td></tr>
	 * <tr><td>(#.false = 1, #.true = 2)</td><td>18</td></tr>
	 * <tr><td>(#.false = 0, #.true = 3)</td><td>27</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testCountingWithoutConstraints() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv cf = CountingFormula.getInstance(a, f);
		
		Parfactor input = new StdParfactorBuilder().variables(f)
				.values(2, 3).build();
		
		Parfactor result = input.count(a);
		
		Parfactor answer = new StdParfactorBuilder().variables(cf)
				.values(8, 12, 18, 27).build();

		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Tests exception throwing for counting.
	 * Tries to count a logical variable that does not exist in the parfactor.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCoutingException() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 3);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		
		Parfactor input = new StdParfactorBuilder().variables(f)
				.values(2, 3).build();
		
		input.count(b);
	}
	
	
	/**
	 * Counts logical variable A on parfactor 
	 * g = &langle; {A&ne;x1}, {f(A)}, F1 &rangle;
	 * where
	 * <p>
	 * F1
	 * <table border="1">
	 * <tr><th>f(A)</th><th>values</th></tr>
	 * <tr><td>false</td><td>2</td></tr>
	 * <tr><td>true</td><td>3</td></tr>
	 * </table>
	 * </p>
	 * <p>
	 * D(A) = {x1,x2,x3}. The result of this
	 * operation is parfactor g' = &langle; &empty;, {#.A[f(A)]}, F2 &rangle;, 
	 * where 
	 * <p>
	 * F2
	 * <table border="1">
	 * <tr><th>#.A[f(A)]</th><th>values</th></tr>
	 * <tr><td>(#.false = 2, #.true = 1)</td><td>4</td></tr>
	 * <tr><td>(#.false = 1, #.true = 2)</td><td>6</td></tr>
	 * <tr><td>(#.false = 0, #.true = 3)</td><td>9</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testCountingWithConstraints() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 3);
		
		Constant x1 = Constant.getInstance("x1");
		
		Constraint a_x1 = InequalityConstraint.getInstance(a, x1);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv cf = CountingFormula.getInstance(a, f, a_x1);
		
		Parfactor input = new StdParfactorBuilder().constraints(a_x1)
				.variables(f).values(2, 3).build();
		
		Parfactor result = input.count(a);
		
		Parfactor answer = new StdParfactorBuilder().variables(cf)
				.values(4, 6, 9).build();

		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Counts logical variable A on parfactor 
	 * g = &langle; &empty;, {f(A), h(B)}, F1 &rangle;
	 * where
	 * <p>
	 * F1
	 * <table border="1">
	 * <tr><th>f(A)</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>2</td></tr>
	 * <tr><td>false</td><td>true</td><td>3</td></tr>
	 * <tr><td>true</td><td>false</td><td>5</td></tr>
	 * <tr><td>true</td><td>true</td><td>7</td></tr>
	 * </table>
	 * </p>
	 * <p>
	 * D(A) = {x1,x2,x3}. The result of this
	 * operation is parfactor g' = &langle; &empty;, {#.A[f(A)],h(B)}, F2 &rangle;, 
	 * where 
	 * <p>
	 * F2
	 * <table border="1">
	 * <tr><th>#.A[f(A)]</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>(#.false = 3, #.true = 0)</td><td>false</td><td>8</td></tr>
	 * <tr><td>(#.false = 3, #.true = 0)</td><td>true</td><td>27</td></tr>
	 * <tr><td>(#.false = 2, #.true = 1)</td><td>false</td><td>20</td></tr>
	 * <tr><td>(#.false = 2, #.true = 1)</td><td>true</td><td>63</td></tr>
	 * <tr><td>(#.false = 1, #.true = 2)</td><td>false</td><td>50</td></tr>
	 * <tr><td>(#.false = 1, #.true = 2)</td><td>true</td><td>147</td></tr>
	 * <tr><td>(#.false = 0, #.true = 3)</td><td>false</td><td>125</td></tr>
	 * <tr><td>(#.false = 0, #.true = 3)</td><td>true</td><td>343</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testCountingrWithoutConstraintsAndTwoVariables() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 3);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		Prv cf = CountingFormula.getInstance(a, f);
		
		Parfactor input = new StdParfactorBuilder().variables(f, h)
				.values(2, 3, 5, 7).build();
		
		Parfactor result = input.count(a);
		
		Parfactor answer = new StdParfactorBuilder().variables(cf, h)
				.values(8, 27, 20, 63, 50, 147, 125, 343).build();
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Counts logical variable A on parfactor 
	 * g = &langle; {A&ne;B}, {f(A), h(B)}, F1 &rangle;
	 * where
	 * <p>
	 * F1
	 * <table border="1">
	 * <tr><th>f(A)</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>2</td></tr>
	 * <tr><td>false</td><td>true</td><td>3</td></tr>
	 * <tr><td>true</td><td>false</td><td>5</td></tr>
	 * <tr><td>true</td><td>true</td><td>7</td></tr>
	 * </table>
	 * </p>
	 * <p>
	 * D(A) = {x1,x2,x3}. The result of this
	 * operation is parfactor 
	 * g' = &langle; &empty;, {#.A:{A&ne;B}[f(A)],h(B)}, F2 &rangle;, 
	 * where 
	 * <p>
	 * F2
	 * <table border="1">
	 * <tr><th>#.A:{A&ne;B}[f(A)]</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>(#.false = 2, #.true = 0)</td><td>false</td><td>4</td></tr>
	 * <tr><td>(#.false = 2, #.true = 0)</td><td>true</td><td>9</td></tr>
	 * <tr><td>(#.false = 1, #.true = 1)</td><td>false</td><td>10</td></tr>
	 * <tr><td>(#.false = 1, #.true = 1)</td><td>true</td><td>21</td></tr>
	 * <tr><td>(#.false = 0, #.true = 2)</td><td>false</td><td>25</td></tr>
	 * <tr><td>(#.false = 0, #.true = 2)</td><td>true</td><td>49</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testCount() {
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 3);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 3);

		Constraint ab = InequalityConstraint.getInstance(a, b);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		Prv cf = CountingFormula.getInstance(a, f, ab);
		
		Parfactor input = new StdParfactorBuilder().constraints(ab)
				.variables(f, h).values(2, 3, 5, 7).build();
		
		Parfactor result = input.count(a);
				
		double [] vals = {4.0, 9.0, 10.0, 21.0, 25.0, 49.0};
		Parfactor answer = new StdParfactorBuilder().variables(cf, h)
				.values(vals).build();
		
		assertTrue(result.equals(answer));
	}
	
}
