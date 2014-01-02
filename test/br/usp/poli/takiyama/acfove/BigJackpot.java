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
package br.usp.poli.takiyama.acfove;

import java.util.Set;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.utils.Example;
import br.usp.poli.takiyama.utils.Sets;


public class BigJackpot {
	
	private Marginal bigJackpot;
	
	@Before
	public void setup() {
		Example network = Example.bigJackpotNetworkNoContext(10);
		Prv matched6 = network.prv("matched_6 ( Person )");
		
		LogicalVariable person = network.lv("Person");
		Constant john = Constant.getInstance("x1");
		
		Constraint notJohn = InequalityConstraint.getInstance(person, john);
		
		Set<Constraint> constraints = Sets.setOf(notJohn);
		
		RandomVariableSet query = RandomVariableSet.getInstance(matched6, constraints);
		
		bigJackpot = network.getMarginal(query);
	}
	
	@Test
	public void queryMatched6() {
		ACFOVE acfove = new LoggedACFOVE(bigJackpot, Level.ALL);
		acfove.run();
	}
}
