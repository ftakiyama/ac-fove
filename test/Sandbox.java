

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import br.usp.poli.takiyama.acfove.operator.And;
import br.usp.poli.takiyama.common.Parfactors;
import br.usp.poli.takiyama.common.Pool;
import br.usp.poli.takiyama.common.ParfactorI;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Bool;
import br.usp.poli.takiyama.prv.OldCountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariableNameGenerator;
import br.usp.poli.takiyama.prv.Operator;
import br.usp.poli.takiyama.prv.Or;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.RangeElement;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.MathUtils;


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
	}
	
}
