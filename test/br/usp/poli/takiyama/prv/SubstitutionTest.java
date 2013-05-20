package br.usp.poli.takiyama.prv;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.utils.Sets;

/**
 * Not really a JUnit Test. One must check manually if the tests are correct.
 * TODO Make it automatic.
 * @author ftakiyama
 *
 */
@RunWith(Enclosed.class)
public class SubstitutionTest {
	
//	@Test
//	public void createSimpleSubstitution() {
//		List<Binding> bindings = new Vector<Binding>();
//		LogicalVariable t1 = new LogicalVariable("MyLogicalVariable");
//		Constant t2 = new Constant("myConstant");
//		LogicalVariable t3 = new LogicalVariable("AnotherLogicalVariable");
//		Constant t4 = new Constant("anotherConstant");
//		bindings.add(Binding.getInstance(t1, t2));
//		bindings.add(Binding.getInstance(t3, t4));
//		
//		for (LogicalVariable v : Substitution.getInstance(bindings).getLogicalVariables()) {
//			System.out.println(v.toString() + " " + Substitution.getInstance(bindings).getReplacement(v));
//		}
//	}
	
	/**
	 * Tests to verify whether a substitution is consistent with a set of
	 * constraints.
	 */
	@RunWith(Parameterized.class)
	public static class ConstraintConsistencyTest {
		private static LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		private static LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		
		private static Constant x1 = Constant.getInstance("x1");
		private static Constant x2 = Constant.getInstance("x2");

		private static Set<Constraint> noConstraint = new HashSet<Constraint>(0);
		private static Set<Constraint> constraint_A_x1 = Sets.setOf(InequalityConstraint.getInstance(a, x1));
		private static Set<Constraint> constraint_A_B = Sets.setOf(InequalityConstraint.getInstance(a, b));
		
		private static Binding a_x1 = Binding.getInstance(a, x1);
		private static Binding a_x2 = Binding.getInstance(a, x2);
		private static Binding a_b = Binding.getInstance(a, b);
		private static Binding b_x1 = Binding.getInstance(b, x1);
		private static Binding b_x2 = Binding.getInstance(b, x2);
		private static Binding b_a = Binding.getInstance(b, a);
				
		@Parameters
		public static Collection<Object[]> data() {
			return Arrays.asList(new Object[][] {
					{noConstraint,    Substitution.getInstance(), 			Boolean.TRUE},
					{noConstraint,    Substitution.getInstance(a_x1), 		Boolean.TRUE},
					{noConstraint,    Substitution.getInstance(a_b), 		Boolean.TRUE},
					{constraint_A_x1, Substitution.getInstance(a_x1), 		Boolean.FALSE},
					{constraint_A_x1, Substitution.getInstance(a_x2), 		Boolean.TRUE},
					{constraint_A_x1, Substitution.getInstance(), 			Boolean.TRUE},
					{constraint_A_B,  Substitution.getInstance(a_b), 		Boolean.FALSE},
					{constraint_A_B,  Substitution.getInstance(a_x1, b_x1), Boolean.FALSE},
					{constraint_A_B,  Substitution.getInstance(b_a), 		Boolean.FALSE},
					{constraint_A_B,  Substitution.getInstance(), 			Boolean.TRUE},
					{constraint_A_B,  Substitution.getInstance(a_x1),		Boolean.TRUE},
					{constraint_A_B,  Substitution.getInstance(b_x1), 		Boolean.TRUE},
					{constraint_A_B,  Substitution.getInstance(a_x1, b_x2), Boolean.TRUE},
			});
		}
		
		private Set<Constraint> constraints;
		private Substitution substitution;
		private Boolean expected;
		
		public ConstraintConsistencyTest(Set<Constraint> c, Substitution s, Boolean b) {
			constraints = c;
			substitution = s;
			expected = b;
		}
		
		@Test
		public void constraintConsistencyTest() {
			assertEquals(expected.booleanValue(), substitution.isConsistentWith(constraints));
		}
	}
}
