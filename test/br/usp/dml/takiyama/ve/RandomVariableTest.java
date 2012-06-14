package br.usp.dml.takiyama.ve;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Test;

import br.usp.dml.takiyama.ve.RandomVariable;

public class RandomVariableTest {
	
	@Test
	public void createSimpleRandomVariable() {
		String name = "MyRandomVariable";
		ArrayList<String> domain = new ArrayList<String>();
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		domain.add("blue");
		domain.add("green");
		domain.add("red");
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));
		
		System.out.print(RandomVariable.createRandomVariable(name, domain, values).toString());
	}
}
