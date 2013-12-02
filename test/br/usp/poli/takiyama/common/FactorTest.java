package br.usp.poli.takiyama.common;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.MathUtils;


public class FactorTest {

	/**
	 * Sums out f(X) from factor
	 * <p>
	 * <table border="1">
	 * <tr><th>f(X)</th><th>g(X,Y)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>0.1</td></tr>
	 * <tr><td>false</td><td>true</td><td>0.2</td></tr>
	 * <tr><td>true</td><td>false</td><td>0.3</td></tr>
	 * <tr><td>true</td><td>true</td><td>0.4</td></tr>
	 * </table>
	 * </p>
	 * which results in factor
	 * <p>
	 * <table border="1">
	 * <tr><th>g(X,Y)</th><th>values</th></tr>
	 * <tr><td>false</td><td>0.4</td></tr>
	 * <tr><td>true</td><td>0.6</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testSumOutFirstVar() {
		
		Term x = StdLogicalVariable.getInstance("X", "x", 10);
		Term y = StdLogicalVariable.getInstance("Y", "x", 10);
		
		Prv f = StdPrv.getBooleanInstance("f", x);
		Prv g = StdPrv.getBooleanInstance("g", x, y);
		
		List<Prv> vars = new ArrayList<Prv>(2);
		vars.add(f);
		vars.add(g);
		
		List<BigDecimal> vals = new ArrayList<BigDecimal>(4);
		vals.add(new BigDecimal(0.1));
		vals.add(new BigDecimal(0.2));
		vals.add(new BigDecimal(0.3));
		vals.add(new BigDecimal(0.4));
		
		Factor factor = StdFactor.getInstance("F", vars, vals);
		
		Factor result = factor.sumOut(f);
		
		List<Prv> ansVars = new ArrayList<Prv>(1);
		ansVars.add(g);
		
		List<BigDecimal> ansVals = new ArrayList<BigDecimal>(2);
		ansVals.add(new BigDecimal(0.1).add(new BigDecimal(0.3), MathUtils.CONTEXT));
		ansVals.add(new BigDecimal(0.2).add(new BigDecimal(0.4), MathUtils.CONTEXT));
		
		Factor answer = StdFactor.getInstance("F", ansVars, ansVals);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Sums out g(X,Y) from factor
	 * <p>
	 * <table border="1">
	 * <tr><th>f(X)</th><th>g(X,Y)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>0.1</td></tr>
	 * <tr><td>false</td><td>true</td><td>0.2</td></tr>
	 * <tr><td>true</td><td>false</td><td>0.3</td></tr>
	 * <tr><td>true</td><td>true</td><td>0.4</td></tr>
	 * </table>
	 * </p>
	 * which results in factor
	 * <p>
	 * <table border="1">
	 * <tr><th>f(X)</th><th>values</th></tr>
	 * <tr><td>false</td><td>0.3</td></tr>
	 * <tr><td>true</td><td>0.7</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testSumOutSecondVar() {
		
		Term x = StdLogicalVariable.getInstance("X", "x", 10);
		Term y = StdLogicalVariable.getInstance("Y", "x", 10);
		
		Prv f = StdPrv.getBooleanInstance("f", x);
		Prv g = StdPrv.getBooleanInstance("g", x, y);
		
		List<Prv> vars = new ArrayList<Prv>(2);
		vars.add(f);
		vars.add(g);
		
		List<BigDecimal> vals = new ArrayList<BigDecimal>(4);
		vals.add(new BigDecimal(0.1));
		vals.add(new BigDecimal(0.2));
		vals.add(new BigDecimal(0.3));
		vals.add(new BigDecimal(0.4));
		
		Factor factor = StdFactor.getInstance("F", vars, vals);
		
		Factor result = factor.sumOut(g);
		
		List<Prv> ansVars = new ArrayList<Prv>(1);
		ansVars.add(f);
		
		List<BigDecimal> ansVals = new ArrayList<BigDecimal>(2);
		ansVals.add(new BigDecimal(0.1).add(new BigDecimal(0.2), MathUtils.CONTEXT));
		ansVals.add(new BigDecimal(0.3).add(new BigDecimal(0.4), MathUtils.CONTEXT));
		
		Factor answer = StdFactor.getInstance("F", ansVars, ansVals);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Sums out #.A[f(A)]  from factor
	 * <p>
	 * <table border="1">
	 * <tr><th>#.A[f(A)]</th><th>h(B)</th><th>values</th></tr>
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
		
		Term a = StdLogicalVariable.getInstance("A", "x", 2);
		Term b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		Prv cf = CountingFormula.getInstance((LogicalVariable) a, f);
		
		List<Prv> vars = new ArrayList<Prv>(2);
		vars.add(cf);
		vars.add(h);
		
		List<BigDecimal> vals = new ArrayList<BigDecimal>(6);
		vals.add(new BigDecimal(1));
		vals.add(new BigDecimal(10));
		vals.add(new BigDecimal(100));
		vals.add(new BigDecimal(1000));
		vals.add(new BigDecimal(10000));
		vals.add(new BigDecimal(100000));
		
		Factor factor = StdFactor.getInstance("F", vars, vals);
		
		Factor result = factor.sumOut(cf);
		
		List<Prv> ansVars = new ArrayList<Prv>(1);
		ansVars.add(h);
		
		List<BigDecimal> ansVals = new ArrayList<BigDecimal>(2);
		ansVals.add(new BigDecimal(10201));
		ansVals.add(new BigDecimal(102010));
		
		Factor answer = StdFactor.getInstance("F", ansVars, ansVals);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Sums out #.A[f(A)]  from factor
	 * <p>
	 * <table border="1">
	 * <tr><th>#.A[f(A)]</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>(#.false = 0, #.true = 10)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 0, #.true = 10)</td><td>true</td><td>1</td></tr>
	 * <tr><td>(#.false = 1, #.true = 9)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 1, #.true = 9)</td><td>true</td><td>1</td></tr>
	 * <tr><td>(#.false = 2, #.true = 8)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 2, #.true = 8)</td><td>true</td><td>1</td></tr>
	 * <tr><td>(#.false = 3, #.true = 7)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 3, #.true = 7)</td><td>true</td><td>1</td></tr>
	 * <tr><td>(#.false = 4, #.true = 6)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 4, #.true = 6)</td><td>true</td><td>1</td></tr>
	 * <tr><td>(#.false = 5, #.true = 5)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 5, #.true = 5)</td><td>true</td><td>1</td></tr>
	 * <tr><td>(#.false = 6, #.true = 4)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 6, #.true = 4)</td><td>true</td><td>1</td></tr>
	 * <tr><td>(#.false = 7, #.true = 3)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 7, #.true = 3)</td><td>true</td><td>1</td></tr>
	 * <tr><td>(#.false = 8, #.true = 2)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 8, #.true = 2)</td><td>true</td><td>1</td></tr>
	 * <tr><td>(#.false = 9, #.true = 1)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 9, #.true = 1)</td><td>true</td><td>1</td></tr>
	 * <tr><td>(#.false = 10, #.true = 0)</td><td>false</td><td>1</td></tr>
	 * <tr><td>(#.false = 10, #.true = 0)</td><td>true</td><td>1</td></tr>
	 * </table>
	 * </p>
	 * which results in factor
	 * <p>
	 * <table border="1">
	 * <tr><th>h(B)</th><th>values</th></tr>
	 * <tr><td>false</td><td>1024</td></tr>
	 * <tr><td>true</td><td>1024</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testSumOutBiggerCountingFormula() {
		
		Term a = StdLogicalVariable.getInstance("A", "x", 10);
		Term b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		Prv cf = CountingFormula.getInstance((LogicalVariable) a, f);
		
		List<Prv> vars = new ArrayList<Prv>(2);
		vars.add(cf);
		vars.add(h);
		
		List<BigDecimal> vals = new ArrayList<BigDecimal>(22);
		for (int i = 0; i < 22; i++) {
			vals.add(new BigDecimal(1));
		}
		
		Factor factor = StdFactor.getInstance("F", vars, vals);
		
		Factor result = factor.sumOut(cf);
		
		List<Prv> ansVars = new ArrayList<Prv>(1);
		ansVars.add(h);
		
		List<BigDecimal> ansVals = new ArrayList<BigDecimal>(2);
		ansVals.add(new BigDecimal(1024));
		ansVals.add(new BigDecimal(1024));
		
		Factor answer = StdFactor.getInstance("F", ansVars, ansVals);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Raises factor
	 * <p>
	 * <table border="1">
	 * <tr><th>f(X)</th><th>g(X,Y)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>2</td></tr>
	 * <tr><td>false</td><td>true</td><td>3</td></tr>
	 * <tr><td>true</td><td>false</td><td>4</td></tr>
	 * <tr><td>true</td><td>true</td><td>0</td></tr>
	 * </table>
	 * </p>
	 * to power 2, which results in factor
	 * <p>
	 * <table border="1">
	 * <tr><th>f(X)</th><th>g(X,Y)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>4</td></tr>
	 * <tr><td>false</td><td>true</td><td>9</td></tr>
	 * <tr><td>true</td><td>false</td><td>16</td></tr>
	 * <tr><td>true</td><td>true</td><td>0</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testPowExponent2() {
		
		Term a = StdLogicalVariable.getInstance("A", "x", 10);
		Term b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		
		List<Prv> vars = new ArrayList<Prv>(2);
		vars.add(f);
		vars.add(h);
		
		List<BigDecimal> vals = new ArrayList<BigDecimal>(4);
		vals.add(new BigDecimal(2));
		vals.add(new BigDecimal(3));
		vals.add(new BigDecimal(4));
		vals.add(new BigDecimal(0));
		
		Factor factor = StdFactor.getInstance("F", vars, vals);
		
		Factor result = factor.pow(2, 1);
		
		List<Prv> ansVars = new ArrayList<Prv>(2);
		ansVars.add(f);
		ansVars.add(h);
		
		List<BigDecimal> ansVals = new ArrayList<BigDecimal>(4);
		ansVals.add(new BigDecimal(4));
		ansVals.add(new BigDecimal(9));
		ansVals.add(new BigDecimal(16));
		ansVals.add(new BigDecimal(0));
		
		Factor answer = StdFactor.getInstance("F", ansVars, ansVals);
		
		assertTrue(result.equals(answer));
	}

	
	/**
	 * Raises factor
	 * <p>
	 * <table border="1">
	 * <tr><th>f(X)</th><th>g(X,Y)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>0</td></tr>
	 * <tr><td>false</td><td>true</td><td>4</td></tr>
	 * <tr><td>true</td><td>false</td><td>9</td></tr>
	 * <tr><td>true</td><td>true</td><td>16</td></tr>
	 * </table>
	 * </p>
	 * to power 1/2 (square root), which results in factor
	 * <p>
	 * <table border="1">
	 * <tr><th>f(X)</th><th>g(X,Y)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>0</td></tr>
	 * <tr><td>false</td><td>true</td><td>2</td></tr>
	 * <tr><td>true</td><td>false</td><td>3</td></tr>
	 * <tr><td>true</td><td>true</td><td>4</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testPowSqrt() {
		
		Term a = StdLogicalVariable.getInstance("A", "x", 10);
		Term b = StdLogicalVariable.getInstance("B", "x", 3);
		
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv h = StdPrv.getBooleanInstance("h", b);
		
		List<Prv> vars = new ArrayList<Prv>(2);
		vars.add(f);
		vars.add(h);
		
		List<BigDecimal> vals = new ArrayList<BigDecimal>(4);
		vals.add(new BigDecimal(0));
		vals.add(new BigDecimal(4));
		vals.add(new BigDecimal(9));
		vals.add(new BigDecimal(16));
		
		Factor factor = StdFactor.getInstance("F", vars, vals);
		
		Factor result = factor.pow(1, 2);
		
		List<Prv> ansVars = new ArrayList<Prv>(2);
		ansVars.add(f);
		ansVars.add(h);
		
		List<BigDecimal> ansVals = new ArrayList<BigDecimal>(4);
		ansVals.add(new BigDecimal(0));
		ansVals.add(new BigDecimal(2));
		ansVals.add(new BigDecimal(3));
		ansVals.add(new BigDecimal(4));
		
		Factor answer = StdFactor.getInstance("F", ansVars, ansVals);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Multiplies factor 
	 * <p>
	 * <table border="1">
	 * <tr><th>f(X)</th><th>values</th></tr>
	 * <tr><td>false</td><td>0.1</td></tr>
	 * <tr><td>false</td><td>0.2</td></tr>
	 * </table>
	 * </p>
	 * and factor
	 * <p>
	 * <table border="1">
	 * <tr><th>f(X)</th><th>g(X,Y)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>0.1</td></tr>
	 * <tr><td>false</td><td>true</td><td>0.2</td></tr>
	 * <tr><td>true</td><td>false</td><td>0.3</td></tr>
	 * <tr><td>true</td><td>true</td><td>0.4</td></tr>
	 * </table>
	 * </p>
	 * which results in factor
	 * <p>
	 * <table border="1">
	 * <tr><th>f(X)</th><th>g(X,Y)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>0.01</td></tr>
	 * <tr><td>false</td><td>true</td><td>0.02</td></tr>
	 * <tr><td>true</td><td>false</td><td>0.06</td></tr>
	 * <tr><td>true</td><td>true</td><td>0.08</td></tr>
	 * </table>
	 * </p>
	 */
	@Test
	public void testSimpleMultiplication() {

		Term x = StdLogicalVariable.getInstance("X", "x", 10);
		Term y = StdLogicalVariable.getInstance("Y", "x", 10);
		
		Prv f = StdPrv.getBooleanInstance("f", x);
		Prv h = StdPrv.getBooleanInstance("h", x, y);
		
		List<Prv> vars1 = new ArrayList<Prv>(1);
		vars1.add(f);
		
		List<BigDecimal> vals1 = new ArrayList<BigDecimal>(2);
		vals1.add(new BigDecimal(0.1));
		vals1.add(new BigDecimal(0.2));
		
		Factor factor1 = StdFactor.getInstance("F1", vars1, vals1);
		
		List<Prv> vars2 = new ArrayList<Prv>(1);
		vars2.add(f);
		vars2.add(h);
		
		List<BigDecimal> vals2 = new ArrayList<BigDecimal>(4);
		vals2.add(new BigDecimal(0.1));
		vals2.add(new BigDecimal(0.2));
		vals2.add(new BigDecimal(0.3));
		vals2.add(new BigDecimal(0.4));

		Factor factor2 = StdFactor.getInstance("F2", vars2, vals2);
		
		Factor result = factor1.multiply(factor2);
		
		List<Prv> ansVars = new ArrayList<Prv>(2);
		ansVars.add(f);
		ansVars.add(h);
		
		List<BigDecimal> ansVals = new ArrayList<BigDecimal>(4);
		ansVals.add(new BigDecimal(0.1).multiply(new BigDecimal(0.1), MathUtils.CONTEXT));
		ansVals.add(new BigDecimal(0.1).multiply(new BigDecimal(0.2), MathUtils.CONTEXT));
		ansVals.add(new BigDecimal(0.2).multiply(new BigDecimal(0.3), MathUtils.CONTEXT));
		ansVals.add(new BigDecimal(0.2).multiply(new BigDecimal(0.4), MathUtils.CONTEXT));
		
		Factor answer = StdFactor.getInstance("F", ansVars, ansVals);
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Multiplies a factor by 1. Expected result is the factor itself.
	 */
	@Test
	public void testMultiplicationBy1() {
		Factor constant = StdFactor.getInstance();
		
		Prv f = StdPrv.getBooleanInstance("f");
		Prv h = StdPrv.getBooleanInstance("h");
		List<Prv> vars = Lists.listOf(f, h);
		
		List<BigDecimal> vals = Lists.listOf(
				BigDecimal.valueOf(1.0),
				BigDecimal.valueOf(2.0),
				BigDecimal.valueOf(3.0),
				BigDecimal.valueOf(4.0));
		Factor factor = StdFactor.getInstance("f", vars, vals);
		
		Factor result = factor.multiply(constant);
		Factor expected = factor;
		assertEquals(expected, result);
	}
}
