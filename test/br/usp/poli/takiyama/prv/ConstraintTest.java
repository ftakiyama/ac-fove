package br.usp.poli.takiyama.prv;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.usp.poli.takiyama.common.Builder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;


public class ConstraintTest {
	
	/**
	 * Tests if  X != Y is consistent with X/q. Must assert false.
	 */
	@Test
	public void testInequalityConsistencyXY_Xq() {
		LogicalVariable x = Builder.getLogicalVariable("X", "x", 10);
		LogicalVariable y = Builder.getLogicalVariable("Y", "x", 10);
		Constant q = x.population().individualAt(0);
		
		Constraint c = InequalityConstraint.getInstance(x, y);
		Binding b = Binding.getInstance(x, q);
		
		assertFalse(c.isConsistentWith(b));
	}
	
	
	/**
	 * Tests if  X != Y is consistent with Y/q. Must assert false.
	 */
	@Test
	public void testInequalityConsistencyXY_Yq() {
		LogicalVariable x = Builder.getLogicalVariable("X", "x", 10);
		LogicalVariable y = Builder.getLogicalVariable("Y", "x", 10);
		Constant q = x.population().individualAt(0);
		
		Constraint c = InequalityConstraint.getInstance(x, y);
		Binding b = Binding.getInstance(y, q);
		
		assertFalse(c.isConsistentWith(b));
	}
	
	
	/**
	 * Tests if  X != Y is consistent with X/W. Must assert false.
	 */
	@Test
	public void testInequalityConsistencyXY_XW() {
		LogicalVariable x = Builder.getLogicalVariable("X", "x", 10);
		LogicalVariable y = Builder.getLogicalVariable("Y", "x", 10);
		LogicalVariable w = Builder.getLogicalVariable("W", "x", 10);
		
		Constraint c = InequalityConstraint.getInstance(x, y);
		Binding b = Binding.getInstance(x, w);
		
		assertFalse(c.isConsistentWith(b));
	}
	
	
	/**
	 * Tests if  X != Y is consistent with Y/W. Must assert false.
	 */
	@Test
	public void testInequalityConsistencyXY_YW() {
		LogicalVariable x = Builder.getLogicalVariable("X", "x", 10);
		LogicalVariable y = Builder.getLogicalVariable("Y", "x", 10);
		LogicalVariable w = Builder.getLogicalVariable("W", "x", 10);
		
		Constraint c = InequalityConstraint.getInstance(x, y);
		Binding b = Binding.getInstance(y, w);
		
		assertFalse(c.isConsistentWith(b));
	}
	
	
	/**
	 * Tests if  X != t is consistent with Y/q. Must assert true.
	 */
	@Test
	public void testInequalityConsistencyXt_Yq() {
		LogicalVariable x = Builder.getLogicalVariable("X", "x", 10);
		LogicalVariable y = Builder.getLogicalVariable("Y", "x", 10);
		Constant t = Constant.getInstance("x1");
		Constant q = Constant.getInstance("x1");
				
		Constraint c = InequalityConstraint.getInstance(x, t);
		Binding b = Binding.getInstance(y, q);
		
		assertTrue(c.isConsistentWith(b));
	}
	
	
	/**
	 * Tests if  X != t is consistent with Y/W. Must assert true.
	 */
	@Test
	public void testInequalityConsistencyXt_YW() {
		LogicalVariable x = Builder.getLogicalVariable("X", "x", 10);
		LogicalVariable y = Builder.getLogicalVariable("Y", "x", 10);
		LogicalVariable w = Builder.getLogicalVariable("W", "x", 10);
		Constant t = Constant.getInstance("x1");
				
		Constraint c = InequalityConstraint.getInstance(x, t);
		Binding b = Binding.getInstance(y, w);
		
		assertTrue(c.isConsistentWith(b));
	}
	
	
	/**
	 * Tests if  X != t is consistent with X/W. Must assert false.
	 */
	@Test
	public void testInequalityConsistencyXt_XW() {
		LogicalVariable x = Builder.getLogicalVariable("X", "x", 10);
		LogicalVariable w = Builder.getLogicalVariable("W", "x", 10);
		Constant t = Constant.getInstance("x1");
				
		Constraint c = InequalityConstraint.getInstance(x, t);
		Binding b = Binding.getInstance(x, w);
		
		assertFalse(c.isConsistentWith(b));
	}
	
	
	/**
	 * Tests if  X != t is consistent with X/q when t = q. Must assert false.
	 */
	@Test
	public void testInequalityConsistencyXt_Xt() {
		LogicalVariable x = Builder.getLogicalVariable("X", "x", 10);
		Constant t = Constant.getInstance("x1");
		Constant q = Constant.getInstance("x1");
				
		Constraint c = InequalityConstraint.getInstance(x, t);
		Binding b = Binding.getInstance(x, q);
		
		assertFalse(c.isConsistentWith(b));
	}
	
	
	/**
	 * Tests if  X != t is consistent with X/q when t != q. Must assert true.
	 */
	@Test
	public void testInequalityConsistencyXt_Xq() {
		LogicalVariable x = Builder.getLogicalVariable("X", "x", 10);
		Constant t = Constant.getInstance("x1");
		Constant q = Constant.getInstance("x2");
				
		Constraint c = InequalityConstraint.getInstance(x, t);
		Binding b = Binding.getInstance(x, q);
		
		assertTrue(c.isConsistentWith(b));
	}
	
	
}
