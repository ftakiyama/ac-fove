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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.utils.Example;


/**
 * The exists quantifier network problem.
 * Runs lifted elimination and compares with propositionalized
 * version.
 * It uses b(X) in the model instead of b(Y). The model is wrong, but I want
 * to verify whether introducing b(Y) causes the abnormal behavior in the
 * algorithm.
 * 
 * PS: Yes, this is the reason. Not sure why, but the cause is identified.
 */
public class Temp8 {
	
	private Example network;
	private Example propositionalized;
	private int domainSize;
	
	@Before
	public void setup() {
		domainSize = 2;
		network = Example.existsNetworkWithBX(domainSize);
		propositionalized = Example.existsNetworkPropositionalizedWithBX(domainSize);
	}
	
	@Test
	public void inferExists() {
		
		Parfactor lifted = inferExistsInLiftedManner();
		
		Map<String, Factor> propositionalized = inferExistsInPropositionalizedManner();
		
		System.out.println("Test finished");
	}
	
	private Parfactor inferExistsInLiftedManner() {
		Prv b = network.prv("b ( X )");
		Prv r = network.prv("r ( X Y )");
		Prv and = network.prv("and ( X Y )");
		
		Parfactor gb = network.parfactor("gb");
		Parfactor gr = network.parfactor("gr");
		Parfactor gand = network.parfactor("gand");
		Parfactor gexists = network.parfactor("gexists");
		
		Parfactor sumOutR = gr.multiply(gand).sumOut(r);
		Parfactor sumOutAnd = gexists.multiply(sumOutR).sumOut(and);
		Parfactor sumOutB = sumOutAnd.multiply(gb).sumOut(b);
		
		return sumOutB;
	}
	
	private Map<String, Factor> inferExistsInPropositionalizedManner() {
		
		// sum out r
		Map<String, Factor> sumOutR = new HashMap<String, Factor>();
		for (int x = 0; x < domainSize; x++) {
			for (int y = 0; y < domainSize; y++) {
				String rName = getName("r", x, y);
				Prv rPrv = propositionalized.prv(rName);
				String andName = getName("and", x, y);
				Factor rFactor = propositionalized.factor(rName);
				Factor andFactor = propositionalized.factor(andName);
				Factor result = rFactor.multiply(andFactor).sumOut(rPrv);
				sumOutR.put(andName, result);
			}
		}
		
		// sum out and
		Map<String, Factor> sumOutAnd = new HashMap<String, Factor>();
		for (int x = 0; x < domainSize; x++) {
			
			// gets factor on exists(X)
			String exists = getName("exists", "x", x);
			Factor result = propositionalized.factor(exists);
			
			// for each factor on and(X,Y), multiplies it by exists(X) and sum it out
			for (int y = 0; y < domainSize; y++) {
				String and = getName("and", x, y);
				Prv andPrv = propositionalized.prv(and);
				Factor andFactor = sumOutR.get(and);
				result = result.multiply(andFactor).sumOut(andPrv);
			}
			sumOutAnd.put(exists, result);
		}
		
		Map<String, Factor> sumOutB = new HashMap<String, Factor>();
		for (int x = 0; x < domainSize; x++) {
			String exists = getName("exists", "x", x);
			String b = getName("b", "x", x);
			Factor result = propositionalized.factor(b);
			Factor existsFactor = sumOutAnd.get(exists);
			Prv bPrv = propositionalized.prv(b);
			result = result.multiply(existsFactor).sumOut(bPrv);
			sumOutB.put(exists, result);
		}
		
		return sumOutB;
	}

	private String getName(String prvName, int i, int j) {
		return prvName + " ( x" + i + " y" + j + " )";
	}
	
	private String getName(String prvName, String x, int i) {
		return prvName + " ( " + x + i + " )";
	}
	
}
