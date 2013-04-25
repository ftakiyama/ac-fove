package br.usp.poli.takiyama.cfove;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdSplitResult;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
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
	
	private enum Color implements RangeElement {
		GREEN, ORANGE, RED;
	}
	
}
