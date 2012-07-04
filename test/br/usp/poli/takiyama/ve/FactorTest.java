package br.usp.poli.takiyama.ve;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.junit.Before;

import br.usp.poli.takiyama.ve.Factor;
import br.usp.poli.takiyama.ve.RandomVariable;
import br.usp.poli.takiyama.ve.Tuple;

/**
 * A set of tests to check the methods of {@link Factor}.
 * @author ftakiyama
 *
 */
public class FactorTest {
	
	private ArrayList<RandomVariable> randomVariables;
	private Factor factor;
	
	@Before
	public void initialSetup() {
		randomVariables = new ArrayList<RandomVariable>();
		
		String name = "rv1";
		ArrayList<String> domain = new ArrayList<String>();
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		domain.add("blue");
		domain.add("green");
		domain.add("red");
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));
		
		randomVariables.add(RandomVariable.createRandomVariable(name, domain, values));
		
		name = "rv2";
		domain.clear();
		domain.add("true");
		domain.add("false");
		values.clear();
		values.add(new BigDecimal(0.02));
		values.add(new BigDecimal(0.98));
		
		randomVariables.add(RandomVariable.createRandomVariable(name, domain, values));		
	
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.3));
		mapping.add(new BigDecimal(0.4));
		mapping.add(new BigDecimal(0.5));
		mapping.add(new BigDecimal(0.6));
		
		try {
			factor = new Factor("MyFactor", randomVariables, mapping);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		} catch (Exception e) {
			System.err.println("Unexpected error when creating the Factor.\n" + e);
			System.exit(-1);
		}
	}
	
	@Test
	public void getTuple() {
		ArrayList<Integer> tuple = new ArrayList<Integer>();
		tuple.add(0); tuple.add(0);
		Tuple t0 = new Tuple(tuple);
		
		tuple.clear();
		tuple.add(0); tuple.add(1);
		Tuple t1 = new Tuple(tuple);

		tuple.clear();
		tuple.add(1); tuple.add(0);
		Tuple t2 = new Tuple(tuple);

		tuple.clear();
		tuple.add(1); tuple.add(1);
		Tuple t3 = new Tuple(tuple);
		
		tuple.clear();
		tuple.add(2); tuple.add(0);
		Tuple t4 = new Tuple(tuple);
		
		tuple.clear();
		tuple.add(2); tuple.add(1);
		Tuple t5 = new Tuple(tuple);
		
		assertTrue(factor.getTuple(0).equals(t0) && factor.getTuple(1).equals(t1)
				&& factor.getTuple(2).equals(t2) && factor.getTuple(3).equals(t3)
				&& factor.getTuple(4).equals(t4) && factor.getTuple(5).equals(t5));
		
	}
	
	@Test
	public void getTupleIndex() {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		boolean allTestsOk = true;
		
		indexes.add(0);
		indexes.add(0);
		if (factor.getTupleIndex(new Tuple(indexes)) != 0) allTestsOk = false;
		
		indexes.clear();
		indexes.add(0);
		indexes.add(1);
		if (factor.getTupleIndex(new Tuple(indexes)) != 1) allTestsOk = false;

		indexes.clear();
		indexes.add(1);
		indexes.add(0);
		if (factor.getTupleIndex(new Tuple(indexes)) != 2) allTestsOk = false;

		indexes.clear();
		indexes.add(1);
		indexes.add(1);
		if (factor.getTupleIndex(new Tuple(indexes)) != 3) allTestsOk = false;

		indexes.clear();
		indexes.add(2);
		indexes.add(0);
		if (factor.getTupleIndex(new Tuple(indexes)) != 4) allTestsOk = false;

		indexes.clear();
		indexes.add(2);
		indexes.add(1);
		if (factor.getTupleIndex(new Tuple(indexes)) != 5) allTestsOk = false;

		assertTrue(allTestsOk);
	}
	
	@Test
	public void getRandomVariableIndex() {
		String name = "rv1";
		ArrayList<String> domain = new ArrayList<String>();
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		domain.add("blue");
		domain.add("green");
		domain.add("red");
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));
		
		RandomVariable r1 = RandomVariable.createRandomVariable(name, domain, values);
		
		name = "rv2";
		domain.clear();
		domain.add("true");
		domain.add("false");
		values.clear();
		values.add(new BigDecimal(0.02));
		values.add(new BigDecimal(0.98));
		
		RandomVariable r2 = RandomVariable.createRandomVariable(name, domain, values);
		
		assertTrue(factor.getRandomVariableIndex(r1) == 0 &&
				factor.getRandomVariableIndex(r2) == 1);
	}
	
	public void getRandomVariableIndex2() throws ArrayIndexOutOfBoundsException {
		HashMap<String,RandomVariable> randomVariables = new HashMap<String,RandomVariable>();
		
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("true");
		domain.add("false");
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		values.add(new BigDecimal(0.05));
		values.add(new BigDecimal(0.10));
		randomVariables.put("x1", RandomVariable.createRandomVariable("x1", domain, values));
		randomVariables.put("x2", RandomVariable.createRandomVariable("x2", domain, values));
		
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		v.add(randomVariables.get("x1"));
		v.add(randomVariables.get("x2"));
		
		ArrayList<BigDecimal> m = new ArrayList<BigDecimal>();
		m.add(new BigDecimal(0.1));
		m.add(new BigDecimal(0.2));
		m.add(new BigDecimal(0.3));
		m.add(new BigDecimal(0.4));
		
		Factor f = new Factor("f1", v, m);
		
		assertTrue(f.getRandomVariableIndex(randomVariables.get("x1")) == 0 &&
				f.getRandomVariableIndex(randomVariables.get("x2")) == 1);
	}
	
	@Test
	public void testEquals() throws ArrayIndexOutOfBoundsException {
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		
		String name = "rv1";
		ArrayList<String> domain = new ArrayList<String>();
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		domain.add("blue");
		domain.add("green");
		domain.add("red");
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));
		
		v.add(RandomVariable.createRandomVariable(name, domain, values));
		
		name = "rv2";
		domain.clear();
		domain.add("true");
		domain.add("false");
		values.clear();
		values.add(new BigDecimal(0.02));
		values.add(new BigDecimal(0.98));
		
		v.add(RandomVariable.createRandomVariable(name, domain, values));		
	
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.3));
		mapping.add(new BigDecimal(0.4));
		mapping.add(new BigDecimal(0.5));
		mapping.add(new BigDecimal(0.6));
		
		Factor f = new Factor("MyFactor", v, mapping);
		
		assertTrue(factor.equals(f));
	}
	
	@Test
	public void testEqualsWithDifferentFactors() throws ArrayIndexOutOfBoundsException {
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		
		String name = "rv1";
		ArrayList<String> domain = new ArrayList<String>();
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		domain.add("blue");  
		domain.add("green");
		domain.add("red");
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));
		
		v.add(RandomVariable.createRandomVariable(name, domain, values));
		
		name = "rv2";
		domain.clear();
		domain.add("true");
		domain.add("false");
		values.clear();
		values.add(new BigDecimal(0.02));
		values.add(new BigDecimal(0.98));
		
		v.add(RandomVariable.createRandomVariable(name, domain, values));		
	
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.11));
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.3));
		mapping.add(new BigDecimal(0.4));
		mapping.add(new BigDecimal(0.5));
		mapping.add(new BigDecimal(0.6));
		
		Factor f = new Factor("MyFactor", v, mapping);
		
		assertFalse(factor.equals(f));
	}
	
	@Test
	public void testEqualsPrecision() throws ArrayIndexOutOfBoundsException {
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		
		String name = "rv1";
		ArrayList<String> domain = new ArrayList<String>();
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		domain.add("blue");
		domain.add("green");
		domain.add("red");
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));
		
		v.add(RandomVariable.createRandomVariable(name, domain, values));
		
		name = "rv2";
		domain.clear();
		domain.add("true");
		domain.add("false");
		values.clear();
		values.add(new BigDecimal(0.02));
		values.add(new BigDecimal(0.98));
		
		v.add(RandomVariable.createRandomVariable(name, domain, values));		
	
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.1000000000000001)); // put one more zero and  the factors will be considered equal
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.3));
		mapping.add(new BigDecimal(0.4));
		mapping.add(new BigDecimal(0.5));
		mapping.add(new BigDecimal(0.6));
		
		Factor f = new Factor("MyFactor", v, mapping);
		
		assertFalse(factor.equals(f));
	}
	
	@Test
	public void testSubFactorWithEqualFactors() {
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		
		String name = "rv1";
		ArrayList<String> domain = new ArrayList<String>();
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		domain.add("blue");  
		domain.add("green");
		domain.add("red");
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));
		
		v.add(RandomVariable.createRandomVariable(name, domain, values));
		
		name = "rv2";
		domain.clear();
		domain.add("true");
		domain.add("false");
		values.clear();
		values.add(new BigDecimal(0.02));
		values.add(new BigDecimal(0.98));
		
		v.add(RandomVariable.createRandomVariable(name, domain, values));		
	
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.3));
		mapping.add(new BigDecimal(0.4));
		mapping.add(new BigDecimal(0.5));
		mapping.add(new BigDecimal(0.6));
		
		Factor f = new Factor("MyFactor", v, mapping);
		
		assertTrue(factor.isSubFactorOf(f));
	}
	
	@Test
	public void testSubFactorWithEmptyFactor() {
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		
		Factor f = new Factor("MyFactor", v, mapping);
		
		assertTrue(f.isSubFactorOf(factor));
	}
	
	@Test
	public void testSubFactorWithSubFactor() {
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		
		String name = "rv1";
		ArrayList<String> domain = new ArrayList<String>();
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		domain.add("blue");  
		domain.add("green");
		domain.add("red");
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));
		
		v.add(RandomVariable.createRandomVariable(name, domain, values));
		
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.2));
		mapping.add(new BigDecimal(0.3));
		
		Factor f = new Factor("MyFactor", v, mapping);
		
		assertTrue(f.isSubFactorOf(factor));
	}
	
	@Test
	public void testSubFactorWithNonSubFactor() {
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		
		String name = "rv3";
		ArrayList<String> domain = new ArrayList<String>();
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		domain.add("blue");  
		domain.add("green");
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.456));
		
		v.add(RandomVariable.createRandomVariable(name, domain, values));
		
		ArrayList<BigDecimal> mapping = new ArrayList<BigDecimal>();
		mapping.add(new BigDecimal(0.1));
		mapping.add(new BigDecimal(0.3));
		
		Factor f = new Factor("MyFactor", v, mapping);
		
		assertFalse(f.isSubFactorOf(factor));
	}
}
