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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.StdFactor;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.utils.Example;
import br.usp.poli.takiyama.utils.MathUtils;



/**
 * The exists quantifier network problem.
 * Runs propositionalized inference for exists(x0).
 * Dont use for populations greater than 2. 8O
 */
public class Temp9 {
	private Example propositionalized;
	private int domainSize;
	
	@Before
	public void setup() {
		domainSize = 3;
		propositionalized = Example.existsNetworkPropositionalized(domainSize);
	}
	
	@Test
	public void inferExists() {
		
		Factor result = inferExistsInPropositionalizedManner();
		
		assertEquals(expected(), result);
	}
	
	private Factor inferExistsInPropositionalizedManner() {
		
		Factor product = StdFactor.getInstance();
		for (Factor factor : propositionalized.allFactors()) {
			product = product.multiply(factor);
		}
		
		Factor result = product;
		for (Prv rv : propositionalized.allPrvs()) {
			if (!rv.toString().equals("exists ( x0 )")) {
				result = result.sumOut(rv);
			}
		}
		
		return result;
	}
	
	private Factor expected() {
		
		Factor factorOnB = propositionalized.factor("b ( y0 )");
		BigDecimal bFalse = factorOnB.getValue(0);
		BigDecimal bTrue  = factorOnB.getValue(1); 
		
		Factor factorOnR = propositionalized.factor("r ( x0 y0 )");
		BigDecimal rFalse = factorOnR.getValue(0);
		
		BigDecimal eFalse = bFalse.add(rFalse.multiply(bTrue, MathUtils.CONTEXT)).pow(domainSize);
		BigDecimal eTrue  = BigDecimal.ONE.subtract(eFalse);
		
		List<BigDecimal> expectedValues = new ArrayList<BigDecimal>(2);
		expectedValues.add(eFalse);
		expectedValues.add(eTrue);
		
		Prv exists = propositionalized.prv("exists ( x0 )");
		
		Factor expectedFactor = StdFactor.getInstance("", exists, expectedValues);
		
		return expectedFactor;
	}
}
