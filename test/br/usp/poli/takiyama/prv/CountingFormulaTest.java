package br.usp.poli.takiyama.prv;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;


public class CountingFormulaTest {
	
	/**
	 * Tests substitution A/x1 on #.A[f(A)].
	 * Result is #.A[f(A)].
	 */
	@Test
	public void testSubstitutionAx1_simpleCf() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv cf = CountingFormula.getInstance(a, f); 
		
		Constant x1 = a.population().individualAt(0);
		Substitution s = Substitution.getInstance(Binding.getInstance(a, x1));
		
		Prv result = cf.apply(s);
		
		Prv answer = CountingFormula.getInstance(a, f);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Tests substitution A/B on #.A[f(A)].
	 * Result is #.B[f(B)].
	 */
	@Test
	public void testSubstitutionAB_simpleCf() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv cf = CountingFormula.getInstance(a, f); 
		
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		Substitution s = Substitution.getInstance(Binding.getInstance(a, b));
		
		Prv result = cf.apply(s);
		
		f = StdPrv.getBooleanInstance("f", b);
		Prv answer = CountingFormula.getInstance(b, f);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Tests substitution B/x1 on #.A[f(A)].
	 * Result is #.A[f(A)].
	 */
	@Test
	public void testSubstitutionBx1_simpleCf() {

		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a);
		Prv cf = CountingFormula.getInstance(a, f); 
		
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		Constant x = Constant.getInstance("x1");
		Substitution s = Substitution.getInstance(Binding.getInstance(b, x));
		
		Prv result = cf.apply(s);
		
		Prv answer = CountingFormula.getInstance(a, f);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Tests substitution A/x1 on #.A:{A!=x1}[f(A)].
	 * Result is #.A:{A!=x1}[f(A)].
	 */
	@Test
	public void testSubstitutionAx1_constrainedCf() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a);
		Constant x1 = Constant.getInstance("x1");
		Constraint c = InequalityConstraint.getInstance(a, x1);
		Prv cf = CountingFormula.getInstance(a, f, c); 
		
		Substitution s = Substitution.getInstance(Binding.getInstance(a, x1));
		
		Prv result = cf.apply(s);
		
		Prv answer = CountingFormula.getInstance(a, f, c);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Tests substitution A/B on #.A:{A!=x1}[f(A)].
	 * Result is #.B:{B!=x1}[f(B)].
	 */
	@Test
	public void testSubstitutionAB_constrainedCf() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a);
		Constant x1 = Constant.getInstance("x1");
		Constraint c = InequalityConstraint.getInstance(a, x1);
		Prv cf = CountingFormula.getInstance(a, f, c); 
		
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		Substitution s = Substitution.getInstance(Binding.getInstance(a, b));
		
		Prv result = cf.apply(s);
		
		f = StdPrv.getBooleanInstance("f", b);
		Constraint cb = InequalityConstraint.getInstance(b, x1);
		Prv answer = CountingFormula.getInstance(b, f, cb);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Tests substitution B/x1 on #.A:{A!=x1}[f(A)].
	 * Result is #.A:{A!=x1}[f(A)].
	 */
	@Test
	public void testSubstitutionBx1_constrainedCf() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a);
		Constant x1 = Constant.getInstance("x1");
		Constraint c = InequalityConstraint.getInstance(a, x1);
		Prv cf = CountingFormula.getInstance(a, f, c); 
		
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		Substitution s = Substitution.getInstance(Binding.getInstance(b, x1));
		
		Prv result = cf.apply(s);
		
		Prv answer = CountingFormula.getInstance(a, f, c);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Tests substitution A/x1 on #.A:{A!=x1}[f(A,B)].
	 * Result is #.A:{A!=x1}[f(A,B)].
	 */
	@Test
	public void testSubstitutionAx1_complexCf() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a, b);
		Constant x1 = Constant.getInstance("x1");
		Constraint c = InequalityConstraint.getInstance(a, x1);
		Prv cf = CountingFormula.getInstance(a, f, c); 
		
		Substitution s = Substitution.getInstance(Binding.getInstance(a, x1));

		Prv result = cf.apply(s);
		
		Prv answer = CountingFormula.getInstance(a, f, c);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Tests substitution A/B on #.A:{A!=x1}[f(A,B)].
	 * Result is #.B:{B!=x1}[f(B,B)].
	 */
	@Test
	public void testSubstitutionAB_complexCf() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a, b);
		Constant x1 = Constant.getInstance("x1");
		Constraint c = InequalityConstraint.getInstance(a, x1);
		Prv cf = CountingFormula.getInstance(a, f, c); 
		
		Substitution s = Substitution.getInstance(Binding.getInstance(a, b));

		Prv result = cf.apply(s);
		
		f = StdPrv.getBooleanInstance("f", b, b);
		c = InequalityConstraint.getInstance(b, x1);
		Prv answer = CountingFormula.getInstance(b, f, c);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Tests substitution A/B, B/C on #.A:{A!=x1}[f(A,B)].
	 * Result is #.B:{B!=x1}[f(B,C)] or #.C:{C!=x1}[f(C,C)],
	 * depending on the order that the substitution is made.
	 */
	@Test
	public void testSubstitutionAB_BC_complexCf() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a, b);
		Constant x1 = Constant.getInstance("x1");
		Constraint c = InequalityConstraint.getInstance(a, x1);
		Prv cf = CountingFormula.getInstance(a, f, c); 
		
		LogicalVariable d = StdLogicalVariable.getInstance("C", "x", 10);
		Substitution s = Substitution.getInstance(Binding.getInstance(a, b), 
				Binding.getInstance(b, d));

		Prv result = cf.apply(s);
		
		f = StdPrv.getBooleanInstance("f", b, d);
		c = InequalityConstraint.getInstance(b, x1);
		Prv answer1 = CountingFormula.getInstance(b, f, c);
		
		f = StdPrv.getBooleanInstance("f", d, d);
		c = InequalityConstraint.getInstance(d, x1);
		Prv answer2 = CountingFormula.getInstance(d, f, c);
		
		assertTrue(result.equals(answer1) || result.equals(answer2));
	}
	
	
	/**
	 * Tests substitution B/x1 on #.A:{A!=x1}[f(A,B)].
	 * Result is #.A:{A!=x1}[f(A,x1)].
	 */
	@Test
	public void testSubstitutionBx1_complexCf() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a, b);
		Constant x1 = Constant.getInstance("x1");
		Constraint c = InequalityConstraint.getInstance(a, x1);
		Prv cf = CountingFormula.getInstance(a, f, c); 
		
		Substitution s = Substitution.getInstance(Binding.getInstance(b, x1));

		Prv result = cf.apply(s);
		
		f = StdPrv.getBooleanInstance("f", a, x1);
		Prv answer = CountingFormula.getInstance(a, f, c);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Tests substitution A/B, B/x1 on #.A:{A!=x1}[f(A,B)].
	 * Result is #.A:{A!=x1}[f(A,x1)] or #.B:{B!=x1}[f(B,B)],
	 * depending on the order that the substitution is made.
	 */
	@Test
	public void testSubstitutionAB_Bx1_complexCf() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		Prv f = StdPrv.getBooleanInstance("f", a, b);
		Constant x1 = Constant.getInstance("x1");
		Constraint c = InequalityConstraint.getInstance(a, x1);
		Prv cf = CountingFormula.getInstance(a, f, c); 
		
		Substitution s = Substitution.getInstance(Binding.getInstance(a, b), 
				Binding.getInstance(b, x1));

		Prv result = cf.apply(s);
		
		f = StdPrv.getBooleanInstance("f", a, x1);
		Prv answer1 = CountingFormula.getInstance(a, f, c);
		
		f = StdPrv.getBooleanInstance("f", b, b);
		c = InequalityConstraint.getInstance(b, x1);
		Prv answer2 = CountingFormula.getInstance(b, f, c);
		
		assertTrue(result.equals(answer1) || result.equals(answer2));
	}
	
	
	// Test counting formula constructor
//	@Test
//	public void testCountingFormulaCreation() {
//		String name = "A";
//		List<Constant> individuals = new ArrayList<Constant>(5);
//		individuals.add(Constant.getInstance("a1"));
//		individuals.add(Constant.getInstance("a2"));
//		individuals.add(Constant.getInstance("a3"));
//		individuals.add(Constant.getInstance("a4"));
//		individuals.add(Constant.getInstance("a5"));
//		Population pop = Population.getInstance(individuals);
//		LogicalVariable v = StdLogicalVariable.getInstance(name, pop);
//		Set<Constraint> constraints = new HashSet<Constraint>(2);
//		constraints.add(InequalityConstraint.getInstance(v, Constant.getInstance("a1")));
//		constraints.add(InequalityConstraint.getInstance(v, Constant.getInstance("a2")));
//		
//		name = "B";
//		LogicalVariable b = StdLogicalVariable.getInstance(name, pop);
//		List<Term> terms = new ArrayList<Term>(2);
//		terms.add(v);
//		terms.add(b);
//		
//		List<RangeElement> range = new ArrayList<RangeElement>();
//		range.add(Bool.valueOf(false));
//		range.add(Bool.valueOf(true));
//		
//		StdPrv prv = StdPrv.getInstance("f", range, terms);
//		
//		CountingFormula cf = new CountingFormula(v, constraints, prv);
//		
//		System.out.println(cf);
//	}
	
	@Test
	public void testIndividualsSatisfyingConstraints() {
		
		int numIndividuals = 5;
		List<Constant> individuals = new ArrayList<Constant>(numIndividuals);
		for (int i = 1; i <= numIndividuals; i++) {
			individuals.add(Constant.getInstance("a" + i));
		}
		
		Population pop = Population.getInstance(individuals);
		
		String name = "A";
		
		LogicalVariable v = StdLogicalVariable.getInstance(name, pop);
		
		Set<Constraint> constraints = new HashSet<Constraint>(2);
		constraints.add(InequalityConstraint.getInstance(v, Constant.getInstance("a1")));
		constraints.add(InequalityConstraint.getInstance(v, Constant.getInstance("a2")));
		
		Population p = v.individualsSatisfying(constraints);
		
		assertTrue(p.size() == 3);
	}

}
