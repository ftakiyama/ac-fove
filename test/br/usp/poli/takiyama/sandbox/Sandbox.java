package br.usp.poli.takiyama.sandbox;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.Sets;
import br.usp.poli.takiyama.utils.TestUtils.FactorBuilder;


@RunWith(Enclosed.class)
public class Sandbox {
	
	@RunWith(Parameterized.class)
	public static class Test1 {
		@Parameters(name = "{index}: {0} * {0} = {1}")
		public static Collection<Object[]> data() {
			Object data[][] = new Object[][] { 
					{1, 1},  
					{2, 4},  
					{3, 9} 
					};
			return Arrays.asList(data);
		}
		
		private int a;
		private int b;
		
		public Test1(int a, int b) {
			this.a = a;
			this.b = b;
		}
		
		@Test
		public void test() {
			assertEquals(a*a, b);
		}
	}
	
	@RunWith(Parameterized.class)
	public static class Test2 {
		@Parameters(name = "{index}: {0} multi {0} = {1}")
		public static Collection<Object[]> data() {
			Object data[][] = new Object[][] { 
					{1, 1},  
					{2, 4},  
					{3, 9} 
					};
			return Arrays.asList(data);
		}
		
		private int a;
		private int b;
		
		public Test2(int a, int b) {
			this.a = a;
			this.b = b;
		}
		
		@Test
		public void test() {
			assertEquals(a*a, b);
		}
	}
	
	public static class ListTest {
		
		@Test
		public void testEqualityOfEmptyLists() {
			List<BigDecimal> list1 = new ArrayList<BigDecimal>(0);
			List<BigDecimal> list2 = new ArrayList<BigDecimal>(0);
			assertTrue(Lists.areEqual(list1, list2));
		}
		
		@Test
		public void testEquality() {
			List<BigDecimal> list1 = new ArrayList<BigDecimal>(1);
			List<BigDecimal> list2 = new ArrayList<BigDecimal>(1);
			
			list1.add(BigDecimal.ONE);
			list2.add(BigDecimal.valueOf(1));
			
			assertTrue(Lists.areEqual(list1, list2));
		}
		
		@Test
		public void testEqualityTwoElements() {
			List<BigDecimal> list1 = new ArrayList<BigDecimal>(2);
			List<BigDecimal> list2 = new ArrayList<BigDecimal>(2);
			
			list1.add(BigDecimal.ONE);
			list1.add(BigDecimal.ONE);
			list2.add(BigDecimal.valueOf(1));
			list2.add(BigDecimal.valueOf(1.0));
			
			assertTrue(Lists.areEqual(list1, list2));
		}
		
		@Test
		public void testSubstitution() {
			LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
			LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
			
			Constant x1 = Constant.getInstance("x1");
			Substitution s = Substitution.getInstance(Binding.getInstance(b, x1));
			
			Constraint c = InequalityConstraint.getInstance(a, b);
			List<Constraint> list = Lists.listOf(c);
			
			Constraint ans = InequalityConstraint.getInstance(a, x1);
			List<Constraint> expected = Lists.listOf(ans);
			
			assertEquals(expected, Lists.apply(s, list));
		}
	}
	
	public static class NumberTest {
		@Test
		public void testBigDecimalScale() {
			BigDecimal n = BigDecimal.valueOf(0.2345);
			BigDecimal m = n.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			int x;
		}
	}

	@Ignore("Old code")
	public static class SetTest {
		@Test
		public void testEmptySet() {
			Set<String> setWithInitialCapacity = new HashSet<String>(0);
			Set<String> setWithoutInitialCapacity = new HashSet<String>();
			
			assertFalse(setWithInitialCapacity.contains(null));
			assertFalse(setWithoutInitialCapacity.contains(null));
		}
		
		private static class Person {
			private String name;
			private Person(String name) {
				this.name = name;
			}
			private void setName(String name) {
				this.name = name;
			}
			public String toString() {
				return name;
			}
		}
		
		@Test
		public void testSetReference() {
			Person person = new Person("John");
			Set<Person> s1 = Sets.setOf(person);
			Set<Person> s2 = new HashSet<Person>(s1);
			System.out.println("Before");
			System.out.println(s1);
			System.out.println(s2);
			person.setName("James");
			System.out.println("After");
			System.out.println(s1);
			System.out.println(s2);
		}
	}

	@Ignore("Always fails")
	public static class AssertTest {
		@Test
		public void testDirect() {
			assertEquals(1, 2);
			assertEquals(1, 1);
		}
		
		@Test
		public void testInverse() {
			assertEquals(1, 1);
			assertEquals(1, 2);
		}
	}

	@Ignore("Old code")
	public static class MutabilityTest {
		@Test
		public void accessAndModifyValueInHashMap() {
			// Creating a map whose value is a mutable object
			HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();
			// Creating the value
			Set<String> value = new HashSet<String>();
			value.add("a");
			value.add("b");
			// Putting the value into the map
			map.put("key", value);
			// Retrieving the value and modifying it
			map.get("key").add("c");
			System.out.println(map);
		}
	}

	public static class ApacheVelocityTest {
		@Test
		public void printListToHtml() {
			// inicializando o velocity  
	        VelocityEngine ve = new VelocityEngine();  
	        ve.init();  
	  
	        // criando o contexto que liga o java ao template  
	        VelocityContext context = new VelocityContext();  
	  
	        // escolhendo o template  
	        Template t = ve.getTemplate("test_template.vm");  
	      
	        // variavel que sera acessada no template:  
	        ArrayList<String> list = new ArrayList<String>();  
	        list.add("Item 1");  
	        list.add("Item 2");  
	        list.add("Item 3");  
	        list.add("Item 4");  
	        list.add("Item 5");  
	  
	        // aqui! damos a variavel list para  
	        // o contexto!  
	        context.put("list", list);   
	        try {
		        BufferedWriter writer = new BufferedWriter(new FileWriter("test_template.html"));
		  
		        // mistura o contexto com o template  
		        t.merge(context, writer);  
		        writer.close();
	  
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
		}
	}

	public static class ExistsNodeCalculation {
		
		// the size of the population, in practical terms
		private final int n = 3;
		
		// probability of r(x, y)
		private BigDecimal pr = BigDecimal.valueOf(0.8);
		private BigDecimal pr_1 = BigDecimal.valueOf(0.2);
		
		// probability of b(x)
		private BigDecimal pb = BigDecimal.valueOf(0.9);
		private BigDecimal pb_1 = BigDecimal.valueOf(0.1);
		
		private class TrueTuple implements Iterable<String> {
			@Override
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					
					int current = -1;
					
					@Override
					public boolean hasNext() {
						String currentWord = Integer.toString(current, 4);
						return (!currentWord.matches("3{" + n + "}") && n != 0);
					}

					@Override
					public String next() {
						current++;
						String result = Integer.toString(current, 4);
						int leadingZeroes = n - result.length();
						char [] zeroes = new char[leadingZeroes];
						Arrays.fill(zeroes, '0');
						result = result + String.copyValueOf(zeroes);
						return result;
					}

					@Override
					public void remove() { }
				};
			}			
		}
		
		/**
		 * Calculates P(A) where A = &exist;r(X,Y).B(X), with
		 * P(r) = 0.8 and P(B) = 0.9 and size of the population = n. Tested
		 * for n = 1 and n = 2.
		 * P(A) -> 1 for n -> infinity
		 */
		@Ignore("To not interfere in other tests")
		@Test
		public void getProbability() {
			BigDecimal sum = BigDecimal.ZERO;
			TrueTuple tuples = new TrueTuple();
			for (String bin : tuples) {
				BigDecimal product = getProbabilityFrom(bin); 
				//all = all.add(product);
				if (bin.contains("3")) {
					sum = sum.add(product);
				}
			}
			System.out.println("P(exists(r.b(x1)) = " + sum);
		}
		
		private BigDecimal getProbabilityFrom(String word) {
			BigDecimal product = BigDecimal.ONE;
			for (int i = 0; i < word.length(); i++) {
				int asInt = Integer.valueOf(word.substring(i, i + 1));
				int f = (asInt % 2 != 0) ? 1 : 0;
				int e = (asInt > 1) ? 1 : 0;
				product = product.multiply(pr.pow(e).multiply(pr_1.pow(1 - e)))
								 .multiply(pb.pow(f).multiply(pb_1.pow(1 - f)));
			}
			return product;
		}
	}

	public static class FactorBuilderTest {

		@Ignore
		@Test
		public void testPrvParsing() {
			String prv = "1()";
			FactorBuilder b = new FactorBuilder();
			b.vars(prv);
		}
		
		@Ignore
		@Test
		public void testBuild() {
			FactorBuilder b = new FactorBuilder();
			b.vars("b(y1)").vals(0.1, 0.9);
			System.out.print(b.build());
		}
		
		@Test
		public void testMultiplication() {
			FactorBuilder b = new FactorBuilder();
			b.vars("b(y1)").vals(0.1, 0.9);
			Factor b1 = b.build();
			
			b.clear();
			b.vars("b(y2)").vals(0.1, 0.9);
			Factor b2 = b.build();
			
			b.clear();
			b.vars("b(y1)", "and(x1, y1)").vals(1, 0, 0.2, 0.8);
			Factor band11 = b.build();
			
			b.clear();
			b.vars("b(y1)", "and(x2, y1)").vals(1, 0, 0.2, 0.8);
			Factor band21 = b.build();
			
			b.clear();
			b.vars("b(y2)", "and(x1, y2)").vals(1, 0, 0.2, 0.8);
			Factor band12 = b.build();

			b.clear();
			b.vars("b(y2)", "and(x2, y2)").vals(1, 0, 0.2, 0.8);
			Factor band22 = b.build();
			
			b.clear();
			b.vars("and(x1, y1)", "and(x1, y2)", "exists(x1)").vals(1, 0, 0, 1, 0, 1, 0, 1);
			Factor e1 = b.build();

			b.clear();
			b.vars("and(x2, y1)", "and(x2, y2)", "exists(x2)").vals(1, 0, 0, 1, 0, 1, 0, 1);
			Factor e2 = b.build();

			Factor r = b1.multiply(b2).multiply(band11).multiply(band12).multiply(band21).multiply(band22).multiply(e1).multiply(e2);
			//r = r.sumOut(b1.variables().get(0));
			//r = r.sumOut(b2.variables().get(0));
			r = r.sumOut(e1.variables().get(0));
			r = r.sumOut(e1.variables().get(1));
			r = r.sumOut(e2.variables().get(0));
			r = r.sumOut(e2.variables().get(1));
			//r = r.sumOut(e1.variables().get(2));
			
			System.out.print(r);
		}
		
		@Ignore
		@Test
		public void test() {
			for (int i = 1; i < 10; i++) {
				double b1 = 0.2;
				double b2 = 0.8;
				double r1 = 0.1;
				double x1 = b1 + Math.pow(r1, i) * b2;
				double x2 = Math.pow(b1 + r1 * b2, i);
				System.out.println(x1 + "\t" + x2);
			}
		}
		
		@Test
		public void test1() {
			
			double [] fb = {0.1, 0.9};
			double [] fr = {0.2, 0.8};
			
			FactorBuilder b = new FactorBuilder();
			double c = fb[1] * Math.pow(fr[0], 2);
			b.vars("exists(x1)", "b(y1)").vals(fb[0], c, fb[1], 1 - c);
			Factor eb11 = b.build();
			
			b.clear();
			b.vars("exists(x1)", "b(y2)").vals(fb[0], c, fb[1], 1 - c);
			Factor eb12 = b.build();

			b.clear();
			b.vars("exists(x2)", "b(y1)").vals(fb[0], c, fb[1], 1 - c);
			Factor eb21 = b.build();

			b.clear();
			b.vars("exists(x2)", "b(y2)").vals(fb[0], c, fb[1], 1 - c);
			Factor eb22 = b.build();
			
			Factor r = eb11.multiply(eb12).multiply(eb21).multiply(eb22);
			
			Factor ref = new FactorBuilder().vars("b(y1)", "b(y2)", "exists(x1)", "exists(x2)").build();
			r = r.reorder(ref);
			
			System.out.print(r);
		}
		
		@Test
		public void test2() {
			double b1 = 0.2;
			double b2 = 0.8;
			double r1 = 0.5;
			double r2 = 0.5;
			double r3 = 0.1;
			double r4 = 0.9;
			double x1 = b1*r1+b2*r3;
			double x2 = b1*r2+b2*r4;
			System.out.println(x1 + "\t" + x2);
		}
	}
}