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
package br.usp.poli.takiyama.ve;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.usp.poli.takiyama.common.RandomVariable;

/**
 * A set of tests to check the methods of {@link RandomVariable}.
 * @author ftakiyama
 */
@Ignore("Old code")
public class RandomVariableTest {
	
	private RandomVariable randomVariable;
	
	@Before
	public void initialSetup() {
		String name = "MyRandomVariable";
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("blue");
		domain.add("green");
		domain.add("red");
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));		
		
		randomVariable = RandomVariable.createRandomVariable(name, domain, values);
	}
	
	@Test
	public void createSimpleRandomVariable() {
		initialSetup();
		System.out.print(randomVariable.toString());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwConstructorException() {
		String name = "MyRandomVariable";
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("blue");
		domain.add("green");
		domain.add("red");
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		values.add(new BigDecimal(0.2));
		values.add(new BigDecimal(0.11111));
		
		randomVariable = RandomVariable.createRandomVariable(name, domain, values);
	}
	
	@Test
	public void testEqualsMethod() {
		String name = "MyRandomVariable";
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("blue");
		domain.add("green");
		domain.add("red");
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		values.add(new BigDecimal(0.20));
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));		
		
		RandomVariable rv1 = RandomVariable.createRandomVariable(name, domain, values);
		RandomVariable rv2 = RandomVariable.createRandomVariable(name, domain, values);
		
		assertTrue(randomVariable.equals(rv1)
				&& rv1.equals(randomVariable) 							 // symmetry
				&& randomVariable.equals(randomVariable) 				 // reflexivity
				&& (rv1.equals(rv2) ? rv2.equals(randomVariable) : true) // transitivity
				&& randomVariable.hashCode() == rv1.hashCode());
	}
	
	@Test
	public void testEqualsMethodForDifferentRandomVariables() {
		String name = "MyRandomVariable";
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("blue");
		domain.add("green");
		domain.add("red");
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		values.add(new BigDecimal(0.2000000000000001)); // <-- here is the difference
		values.add(new BigDecimal(0.11111));
		values.add(new BigDecimal(0.456));		
		
		RandomVariable rv1 = RandomVariable.createRandomVariable(name, domain, values);
		RandomVariable rv2 = RandomVariable.createRandomVariable(name, domain, values);
		
		assertFalse(randomVariable.equals(rv1)
				&& rv1.equals(randomVariable) 							 // symmetry
				&& randomVariable.equals(randomVariable) 				 // reflexivity
				&& (rv1.equals(rv2) ? rv2.equals(randomVariable) : true) // transitivity
				&& randomVariable.hashCode() == rv1.hashCode());
	}
}
