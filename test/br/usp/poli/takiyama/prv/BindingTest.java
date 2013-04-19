package br.usp.poli.takiyama.prv;

import static org.junit.Assert.*;

import org.junit.Test;

public class BindingTest {
	
//	@Test
//	public void testGetInstanceWithLVAndConstant() {
//		LogicalVariable t1 = new LogicalVariable("MyLogicalVariable");
//		Constant t2 = new Constant("myConstant");
//		Binding b = Binding.getInstance(t1, t2);
//		assertTrue(b.getFirstTerm().isLogicalVariable() && b.getSecondTerm().isConstant());
//	}
//
//	@Test
//	public void testGetInstanceWithTwoLV() {
//		LogicalVariable t1 = new LogicalVariable("MyLogicalVariable");
//		LogicalVariable t2 = new LogicalVariable("AnotherLogicalVariable");
//		Binding b = Binding.getInstance(t1, t2);
//		assertTrue(b.isValid());
//	}
//
//	@Test
//	public void testGetInstanceWithLVAndTerm() {
//		LogicalVariable t1 = new LogicalVariable("MyLogicalVariable");
//		Term t2 = new LogicalVariable("AnotherLogicalVariable");
//		Binding b = Binding.getInstance(t1, t2);
//		assertTrue(b.isValid());
//	}

	@Test
	public void testContains() {
		/*
		 * X/Y - X
		 * X/Y - Y
		 * X/Y - W
		 * X/x1 - X
		 * X/x1 - W
		 * X/x1 - x1
		 * X/x1 - x2
		 */
		LogicalVariable x = Prvs.getLogicalVariable("X", "x", 2);
		LogicalVariable y = Prvs.getLogicalVariable("Y", "x", 2);
		LogicalVariable w = Prvs.getLogicalVariable("W", "x", 2);
		Constant x1 = Constant.getInstance("x1");
		Constant x2 = Constant.getInstance("x2");
		
		Binding b1 = Binding.getInstance(x, y);
		Binding b2 = Binding.getInstance(x, x1);
		
		assertTrue(
				b1.contains(x)
				&& b1.contains(y)
				&& !b1.contains(w)
				&& b2.contains(x)
				&& !b2.contains(w)
				&& b2.contains(x1)
				&& !b2.contains(x2));
	}
}
