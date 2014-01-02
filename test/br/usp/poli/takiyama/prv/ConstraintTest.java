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
package br.usp.poli.takiyama.prv;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;


public class ConstraintTest {
	
	/**
	 * Tests if  X != Y is consistent with X/q. Must assert false.
	 */
	@Test
	public void testInequalityConsistencyXY_Xq() {
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", 10);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", 10);
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
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", 10);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", 10);
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
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", 10);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", 10);
		LogicalVariable w = StdLogicalVariable.getInstance("W", "x", 10);
		
		Constraint c = InequalityConstraint.getInstance(x, y);
		Binding b = Binding.getInstance(x, w);
		
		assertFalse(c.isConsistentWith(b));
	}
	
	
	/**
	 * Tests if  X != Y is consistent with Y/W. Must assert false.
	 */
	@Test
	public void testInequalityConsistencyXY_YW() {
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", 10);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", 10);
		LogicalVariable w = StdLogicalVariable.getInstance("W", "x", 10);
		
		Constraint c = InequalityConstraint.getInstance(x, y);
		Binding b = Binding.getInstance(y, w);
		
		assertFalse(c.isConsistentWith(b));
	}
	
	
	/**
	 * Tests if  X != t is consistent with Y/q. Must assert true.
	 */
	@Test
	public void testInequalityConsistencyXt_Yq() {
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", 10);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", 10);
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
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", 10);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "x", 10);
		LogicalVariable w = StdLogicalVariable.getInstance("W", "x", 10);
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
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", 10);
		LogicalVariable w = StdLogicalVariable.getInstance("W", "x", 10);
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
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", 10);
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
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", 10);
		Constant t = Constant.getInstance("x1");
		Constant q = Constant.getInstance("x2");
				
		Constraint c = InequalityConstraint.getInstance(x, t);
		Binding b = Binding.getInstance(x, q);
		
		assertTrue(c.isConsistentWith(b));
	}
	
	
}
