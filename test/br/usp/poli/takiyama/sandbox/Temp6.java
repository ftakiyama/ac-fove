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
package br.usp.poli.takiyama.sandbox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.utils.Example;


public class Temp6 {
	@Test
	public void existsNetworkCountBeforeMultiplication() {
		Example network = Example.existsNetwork(2);
		
		LogicalVariable x = network.lv("X");

		Prv b = network.prv("b");
		Prv r = network.prv("r");
		Prv and = network.prv("and");
		
		Parfactor gb = network.parfactor("gb");
		Parfactor gr = network.parfactor("gr");
		Parfactor gand = network.parfactor("gand");
		Parfactor gexists = network.parfactor("gexists");
		
		Parfactor sumOutR = gr.multiply(gand).sumOut(r);
		Parfactor sumOutAnd = gexists.multiply(sumOutR).sumOut(and);
		Parfactor countX = sumOutAnd.count(x);
		Parfactor sumOutB = countX.multiply(gb).sumOut(b);
		
		Parfactor expected = network.expected();
		
		assertEquals(expected, sumOutB);
	}
}
