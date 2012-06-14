package br.usp.dml.takiyama.ve;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.Before;

import br.usp.dml.takiyama.ve.Factor;

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
			//System.out.println(factor.toString());
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
		System.out.println(factor.getTuple(0));
		System.out.println(factor.getTuple(1));
		System.out.println(factor.getTuple(2));
		System.out.println(factor.getTuple(3));
		System.out.println(factor.getTuple(4));
		System.out.println(factor.getTuple(5));
	}
	
	@Test
	public void getTupleIndex() {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		boolean allTestsOk = true;
		
		indexes.add(0);
		indexes.add(0);
		if (factor.getTupleIndex(indexes) != 0) allTestsOk = false;
		
		indexes.clear();
		indexes.add(0);
		indexes.add(1);
		if (factor.getTupleIndex(indexes) != 1) allTestsOk = false;

		indexes.clear();
		indexes.add(1);
		indexes.add(0);
		if (factor.getTupleIndex(indexes) != 2) allTestsOk = false;

		indexes.clear();
		indexes.add(1);
		indexes.add(1);
		if (factor.getTupleIndex(indexes) != 3) allTestsOk = false;

		indexes.clear();
		indexes.add(2);
		indexes.add(0);
		if (factor.getTupleIndex(indexes) != 4) allTestsOk = false;

		indexes.clear();
		indexes.add(2);
		indexes.add(1);
		if (factor.getTupleIndex(indexes) != 5) allTestsOk = false;

		assertTrue(allTestsOk);
	}
	 
}
