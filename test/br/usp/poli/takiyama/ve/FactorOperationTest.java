package br.usp.poli.takiyama.ve;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.RandomVariable;
import br.usp.poli.takiyama.ve.Factor;
import br.usp.poli.takiyama.ve.FactorOperation;

/**
 * A set of tests to check the methods of {@link FactorOperation}.
 * @author ftakiyama
 *
 */
public class FactorOperationTest {
	
	private HashMap<String,RandomVariable> randomVariables;
	private HashMap<String,Factor> factors;
	
	@Before
	public void initialSetup() throws ArrayIndexOutOfBoundsException {
		
		initializeAttributes();
		
		addBooleanRandomVariable("x1");
		addBooleanRandomVariable("x2");
		addBooleanRandomVariable("x3");
		addBooleanRandomVariable("x4");
		
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x2"));
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(new BigDecimal(0.1));
		m.add(new BigDecimal(0.2));
		m.add(new BigDecimal(0.3));
		m.add(new BigDecimal(0.4));
		
		factors.put("f1", new Factor("f1", v, m));
		
		
		v.clear();
		v.add(randomVariables.get("x3"));
		v.add(randomVariables.get("x4"));
		
		m.clear();
		m.add(new BigDecimal(0.80));
		m.add(new BigDecimal(0.20));
		m.add(new BigDecimal(0.30));
		m.add(new BigDecimal(0.60));

		factors.put("f2", new Factor("f2", v, m));
		
		v.clear();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x3"));
		
		m.clear();
		m.add(new BigDecimal(2.0));
		m.add(new BigDecimal(4.0));
		m.add(new BigDecimal(6.0));
		m.add(new BigDecimal(8.0));

		factors.put("f3", new Factor("f3", v, m));
		
		v.clear();
		v.add(randomVariables.get("x1"));
		
		m.clear();
		m.add(new BigDecimal(0.1));
		m.add(new BigDecimal(0.2));

		factors.put("f4", new Factor("f4", v, m));

		v.clear();
		v.add(randomVariables.get("x1"));
		
		m.clear();
		m.add(new BigDecimal(0));
		m.add(new BigDecimal(1));

		factors.put("f5", new Factor("f5", v, m));
	}
	
	private void initializeAttributes() {
		this.factors = new HashMap<String,Factor>();
		this.randomVariables = new HashMap<String,RandomVariable>();
	}
	
	private void addBooleanRandomVariable(String name) {
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("true");
		domain.add("false");
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		values.add(new BigDecimal(0.05));
		values.add(new BigDecimal(0.10));
		randomVariables.put(name, RandomVariable.createRandomVariable(name, domain, values));		
	}
	
	@Test
	public void basicTestSumOut() {
		System.out.println("============== TEST BASIC SUM OUT ===============");
		System.out.println("BEFORE:\n" + factors.get("f1").toString());
		
		// Sums out x1
		System.out.println("SUM OUT x1:\n" + 
				FactorOperation.sumOut(factors.get("f1"), randomVariables.get("x1")));
		
		// Sums out x2
		System.out.println("SUM OUT x2:\n" + 
				FactorOperation.sumOut(factors.get("f1"), randomVariables.get("x2")));
				
	}
	
	
	@Test
	public void basicTestMultiplication() {
		
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x2"));
		v.add(randomVariables.get("x3"));
		v.add(randomVariables.get("x4"));
		
		Factor f1 = factors.get("f1");
		Factor f2 = factors.get("f2");
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(f1.getTupleValue(0).multiply(f2.getTupleValue(0)));
		m.add(f1.getTupleValue(0).multiply(f2.getTupleValue(1)));
		m.add(f1.getTupleValue(0).multiply(f2.getTupleValue(2)));
		m.add(f1.getTupleValue(0).multiply(f2.getTupleValue(3)));
		m.add(f1.getTupleValue(1).multiply(f2.getTupleValue(0)));
		m.add(f1.getTupleValue(1).multiply(f2.getTupleValue(1)));
		m.add(f1.getTupleValue(1).multiply(f2.getTupleValue(2)));
		m.add(f1.getTupleValue(1).multiply(f2.getTupleValue(3)));
		m.add(f1.getTupleValue(2).multiply(f2.getTupleValue(0)));
		m.add(f1.getTupleValue(2).multiply(f2.getTupleValue(1)));
		m.add(f1.getTupleValue(2).multiply(f2.getTupleValue(2)));
		m.add(f1.getTupleValue(2).multiply(f2.getTupleValue(3)));
		m.add(f1.getTupleValue(3).multiply(f2.getTupleValue(0)));
		m.add(f1.getTupleValue(3).multiply(f2.getTupleValue(1)));
		m.add(f1.getTupleValue(3).multiply(f2.getTupleValue(2)));
		m.add(f1.getTupleValue(3).multiply(f2.getTupleValue(3)));
		
		Factor correctResult = new Factor("f1 * f2", v, m);
		Factor result = FactorOperation.multiply(factors.get("f1"), factors.get("f2"));
		
		System.out.println("============== TEST BASIC MULTIPLICATION ===============");
		System.out.println("BEFORE:\n" + factors.get("f1").toString() + "\n" + factors.get("f2").toString());
		System.out.println("AFTER:\n" + result);
		System.out.println("CORRECT:\n" + correctResult);
		
		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testSelfProduct() {
		// Creates the factor with the correct result
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x2"));
		
		Factor f1 = factors.get("f1");
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(f1.getTupleValue(0).multiply(f1.getTupleValue(0)));
		m.add(f1.getTupleValue(1).multiply(f1.getTupleValue(1)));
		m.add(f1.getTupleValue(2).multiply(f1.getTupleValue(2)));
		m.add(f1.getTupleValue(3).multiply(f1.getTupleValue(3)));
		
		Factor correctResult = new Factor("f1 * f1", v, m);
		Factor result = FactorOperation.multiply(factors.get("f1"), factors.get("f1")); 
		
		System.out.println("============== TEST SELF PRODUCT ===============");
		System.out.println("BEFORE:\n" + factors.get("f1").toString());
		System.out.println("AFTER:\n" + result);

		assertTrue(result.equals(correctResult));		
	}
	
	@Test
	public void testMultiplicationWithCommonVariables() {
		// Creates the factor with the correct result
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x2"));
		v.add(randomVariables.get("x3"));
		
		Factor f1 = factors.get("f1");
		Factor f3 = factors.get("f3");
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(f1.getTupleValue(0).multiply(f3.getTupleValue(0)));
		m.add(f1.getTupleValue(0).multiply(f3.getTupleValue(1)));
		m.add(f1.getTupleValue(1).multiply(f3.getTupleValue(0)));
		m.add(f1.getTupleValue(1).multiply(f3.getTupleValue(1)));
		m.add(f1.getTupleValue(2).multiply(f3.getTupleValue(2)));
		m.add(f1.getTupleValue(2).multiply(f3.getTupleValue(3)));
		m.add(f1.getTupleValue(3).multiply(f3.getTupleValue(2)));
		m.add(f1.getTupleValue(3).multiply(f3.getTupleValue(3)));
		
		Factor correctResult = new Factor("f1 * f3", v, m);
		Factor result = FactorOperation.multiply(factors.get("f1"), factors.get("f3"));
		
		System.out.println("============== TEST MULTIPLICATION WITH COMMON VARIABLES ===============");
		System.out.println("BEFORE:\n" + factors.get("f1").toString() + "\n" + factors.get("f3").toString());
		System.out.println("f1 x f3:\n" + result);
		
		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testSumOutAfterMultiplication() {
		
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x2"));
		v.add(randomVariables.get("x3"));
		
		Factor f1 = factors.get("f1");
		Factor f3 = factors.get("f3");
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(f1.getTupleValue(0).multiply(f3.getTupleValue(0)));
		m.add(f1.getTupleValue(0).multiply(f3.getTupleValue(1)));
		m.add(f1.getTupleValue(1).multiply(f3.getTupleValue(0)));
		m.add(f1.getTupleValue(1).multiply(f3.getTupleValue(1)));
		m.add(f1.getTupleValue(2).multiply(f3.getTupleValue(2)));
		m.add(f1.getTupleValue(2).multiply(f3.getTupleValue(3)));
		m.add(f1.getTupleValue(3).multiply(f3.getTupleValue(2)));
		m.add(f1.getTupleValue(3).multiply(f3.getTupleValue(3)));
		
		ArrayList<BigDecimal> mSum = new ArrayList<BigDecimal>();
		mSum.add(m.get(0).add(m.get(4)));
		mSum.add(m.get(1).add(m.get(5)));
		mSum.add(m.get(2).add(m.get(6)));
		mSum.add(m.get(3).add(m.get(7)));
		
		Factor correctResult = new Factor("sumOut(x1, f1 * f3)", v, mSum);
		Factor result = FactorOperation.sumOut(FactorOperation.multiply(factors.get("f1"), factors.get("f3")), randomVariables.get("x1"));
		
		System.out.println("============== TEST SUM OUT AFTER MULTIPLICATION ===============");
		System.out.println("BEFORE:\n" + factors.get("f1").toString() + "\n" + factors.get("f3").toString());
		System.out.println("AFTER:\n" + result);
		
		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testProductWithNoFactor() {
		Factor correctResult = new Factor("", new ArrayList<RandomVariable>(), new ArrayList<BigDecimal>());
		Factor result = FactorOperation.product((Factor[]) null);

		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testProductWithOneFactor() {
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x2"));
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(new BigDecimal(0.1));
		m.add(new BigDecimal(0.2));
		m.add(new BigDecimal(0.3));
		m.add(new BigDecimal(0.4));
		
		Factor correctResult = new Factor("f1", v, m);
		Factor result = FactorOperation.product(factors.get("f1"));

		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testProductWithTwoFactors() {
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x2"));
		v.add(randomVariables.get("x3"));
		v.add(randomVariables.get("x4"));
		
		Factor f1 = factors.get("f1");
		Factor f2 = factors.get("f2");
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(f1.getTupleValue(0).multiply(f2.getTupleValue(0)));
		m.add(f1.getTupleValue(0).multiply(f2.getTupleValue(1)));
		m.add(f1.getTupleValue(0).multiply(f2.getTupleValue(2)));
		m.add(f1.getTupleValue(0).multiply(f2.getTupleValue(3)));
		m.add(f1.getTupleValue(1).multiply(f2.getTupleValue(0)));
		m.add(f1.getTupleValue(1).multiply(f2.getTupleValue(1)));
		m.add(f1.getTupleValue(1).multiply(f2.getTupleValue(2)));
		m.add(f1.getTupleValue(1).multiply(f2.getTupleValue(3)));
		m.add(f1.getTupleValue(2).multiply(f2.getTupleValue(0)));
		m.add(f1.getTupleValue(2).multiply(f2.getTupleValue(1)));
		m.add(f1.getTupleValue(2).multiply(f2.getTupleValue(2)));
		m.add(f1.getTupleValue(2).multiply(f2.getTupleValue(3)));
		m.add(f1.getTupleValue(3).multiply(f2.getTupleValue(0)));
		m.add(f1.getTupleValue(3).multiply(f2.getTupleValue(1)));
		m.add(f1.getTupleValue(3).multiply(f2.getTupleValue(2)));
		m.add(f1.getTupleValue(3).multiply(f2.getTupleValue(3)));
		
		Factor correctResult = new Factor("f1 * f2", v, m);
		Factor result = FactorOperation.product(factors.get("f1"), factors.get("f2"));
		
		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testProductWithThreeFactors() {
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x2"));
		
		Factor f1 = factors.get("f1");
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(f1.getTupleValue(0).multiply(f1.getTupleValue(0)).multiply(f1.getTupleValue(0)));
		m.add(f1.getTupleValue(1).multiply(f1.getTupleValue(1)).multiply(f1.getTupleValue(1)));
		m.add(f1.getTupleValue(2).multiply(f1.getTupleValue(2)).multiply(f1.getTupleValue(2)));
		m.add(f1.getTupleValue(3).multiply(f1.getTupleValue(3)).multiply(f1.getTupleValue(3)));
		
		Factor correctResult = new Factor("f1 * f1 * f1", v, m);
		Factor result = FactorOperation.product(factors.get("f1"), factors.get("f1"), factors.get("f1")); 
		
		assertTrue(result.equals(correctResult));		
	}
	
	
	@Test
	public void testBasicDivision() {
		
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x2"));
		
		Factor f1 = factors.get("f1");
		Factor f2 = factors.get("f4");
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(f1.getTupleValue(0).divide(f2.getTupleValue(0), FactorOperation.PRECISION, RoundingMode.HALF_UP));
		m.add(f1.getTupleValue(1).divide(f2.getTupleValue(0), FactorOperation.PRECISION, RoundingMode.HALF_UP));
		m.add(f1.getTupleValue(2).divide(f2.getTupleValue(1), FactorOperation.PRECISION, RoundingMode.HALF_UP));
		m.add(f1.getTupleValue(3).divide(f2.getTupleValue(1), FactorOperation.PRECISION, RoundingMode.HALF_UP));
		
		Factor correctResult = new Factor("f1", v, m);
		Factor result = FactorOperation.divide(factors.get("f1"), factors.get("f4"));
		
		System.out.println("============= TEST SIMPLE DIVISION ==============");
		System.out.println("BEFORE:\n" + factors.get("f1").toString() + "\n" + factors.get("f4").toString());
		System.out.println("AFTER:\n" + result);
		System.out.println("CORRECT:\n" + correctResult);
		
		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testSelfDivision() {
		// Creates the factor with the correct result
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x2"));
		
		Factor f1 = factors.get("f1");
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(f1.getTupleValue(0).divide(f1.getTupleValue(0), FactorOperation.PRECISION, RoundingMode.HALF_UP));
		m.add(f1.getTupleValue(1).divide(f1.getTupleValue(1), FactorOperation.PRECISION, RoundingMode.HALF_UP));
		m.add(f1.getTupleValue(2).divide(f1.getTupleValue(2), FactorOperation.PRECISION, RoundingMode.HALF_UP));
		m.add(f1.getTupleValue(3).divide(f1.getTupleValue(3), FactorOperation.PRECISION, RoundingMode.HALF_UP));
		
		Factor correctResult = new Factor("f1", v, m);
		Factor result = FactorOperation.divide(factors.get("f1"), factors.get("f1")); 
		
		System.out.println("============== TEST SELF DIVISION ===============");
		System.out.println("BEFORE:\n" + factors.get("f1").toString());
		System.out.println("AFTER:\n" + result);
		System.out.println("CORRECT:\n" + correctResult);

		assertTrue(result.equals(correctResult));		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDivisionException() {
		FactorOperation.divide(factors.get("f1"), factors.get("f3"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDivisionByZero() {
		FactorOperation.divide(factors.get("f1"), factors.get("f5"));
	}
}
