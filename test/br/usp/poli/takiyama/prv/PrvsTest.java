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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.utils.Sets;


public class PrvsTest {
	
	private LogicalVariable a;
	private List<Prv> base;
	private List<Prv> disjointWithBase;
	private List<Prv> notDisjointWithBase;
	
	@Before
	public void setup() {
		base = new ArrayList<Prv>();
		disjointWithBase = new ArrayList<Prv>();
		notDisjointWithBase = new ArrayList<Prv>();
		
		a = StdLogicalVariable.getInstance("A", "a", 3);
		base.add(StdPrv.getBooleanInstance("h", a));
		//base.add(RandomVariableSet.getInstance(StdPrv.getBooleanInstance("f", a), new HashSet<Constraint>(0)));
		disjointWithBase.add(CountingFormula.getInstance(a, StdPrv.getBooleanInstance("f", a)));
	}
	
	/**
	 * Example 2.20 from Kisynski (2010).
	 * <p>
	 * Finds the MGU for f(X1,X2) and f(x1,X4), which is {X1/x1, X2/X4}.
	 * </p>
	 */
	@Test
	public void testMgu() {
		LogicalVariable x1 = StdLogicalVariable.getInstance("X1", "x", 10);
		LogicalVariable x2 = StdLogicalVariable.getInstance("X2", "x", 10);
		LogicalVariable x4 = StdLogicalVariable.getInstance("X4", "x", 10);
		
		Constant c1 = x1.population().individualAt(1);
		
		Prv f1 = StdPrv.getBooleanInstance("f", x1, x2);
		Prv f2 = StdPrv.getBooleanInstance("f", c1, x4);
		
		Substitution result = Prvs.mgu(f1, f2);
		
		Binding x1_1 = Binding.getInstance(x1, x1.population().individualAt(1));
		Binding x2_x4 = Binding.getInstance(x2, x4);
		
		Substitution answer = Substitution.getInstance(x1_1, x2_x4);
		
		assertTrue(result.equals(answer));
	}
	
	@Test
	public void testSetIntersection() {
		for (Prv prv : base) {
			for (Prv disjoint : disjointWithBase) {
				if(!Prvs.areDisjoint(prv, disjoint)) {
					assertTrue(false);
				}
			}
		}
		assertTrue(true);
	}
	
	/**
	 * sprinkler(Lot):{Lot!=lot1} is disjoint with sprinkler(lot1)
	 */
	@Test
	public void testSetIntersection1() {
		LogicalVariable lot = StdLogicalVariable.getInstance("Lot", "lot", 10);
		Prv sprinkler = StdPrv.getBooleanInstance("sprinkler", lot);
		Term lot1 = lot.population().individualAt(0);
		Set<Constraint> constraints = Sets.setOf(InequalityConstraint.getInstance(lot, lot1));
		Prv randomVariableSet = RandomVariableSet.getInstance(sprinkler, constraints);
		Prv sprinkler1 = StdPrv.getBooleanInstance("sprinkler", lot1);
		assertTrue(Prvs.areDisjoint(randomVariableSet, sprinkler1));
	}
	
	/**
	 * sprinkler(Lot) is NOT disjoint with sprinkler(lot1)
	 */
	@Test
	public void testSetIntersection2() {
		LogicalVariable lot = StdLogicalVariable.getInstance("Lot", "lot", 10);
		Prv sprinkler = StdPrv.getBooleanInstance("sprinkler", lot);
		Term lot1 = lot.population().individualAt(0);
		Prv randomVariableSet = RandomVariableSet.getInstance(sprinkler, Sets.<Constraint>getInstance(0));
		Prv sprinkler1 = StdPrv.getBooleanInstance("sprinkler", lot1);
		assertFalse(Prvs.areDisjoint(randomVariableSet, sprinkler1));
	}
}
