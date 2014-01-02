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
package br.usp.poli.takiyama.utils;

import java.util.HashSet;

import org.junit.Test;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;


public class ExampleTest {
	
	private final int limit = 3;
	
	@Test
	public void testPropositionalization() {
		
		for (int domainSize = 1; domainSize < limit; domainSize++) {
			
			Example network = Example.competingWorkshopsNetwork(domainSize, domainSize);
			
			// Query
			Prv someDeath = network.prv("success ( )");
			RandomVariableSet query = RandomVariableSet.getInstance(someDeath, new HashSet<Constraint>(0));
			
			// Input marginal
			Marginal input = network.getMarginal(query);

			//System.out.println(network.propositionalizeAll(input));
			
		}
	}
	
	@Test
	public void testPropositionalization2() {
				
		Example network = Example.waterSprinklerNetWork(limit);
		
		// Query
		Prv someDeath = network.prv("rain ( )");
		RandomVariableSet query = RandomVariableSet.getInstance(someDeath, new HashSet<Constraint>(0));
		
		// Input marginal
		Marginal input = network.getMarginal(query);

		//System.out.println(network.propositionalizeAll(input));

	}
	
	@Test
	public void conversionTest() {
		Example network = Example.competingWorkshopsNetwork(limit, limit);
		
		// Query
		Prv someDeath = network.prv("success ( )");
		RandomVariableSet query = RandomVariableSet.getInstance(someDeath, new HashSet<Constraint>(0));
		
		// Input marginal
		Marginal input = network.getMarginal(query);
		
		System.out.println(network.removeAggregation(input));
	}
}
