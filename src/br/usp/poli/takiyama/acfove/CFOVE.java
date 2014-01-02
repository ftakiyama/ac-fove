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

import java.util.logging.Level;

import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;

public class CFOVE extends ACFOVE {

	public CFOVE(Marginal parfactors, Level logLevel) {
		super(removeAggregation(parfactors), logLevel);
	}

	public CFOVE(Marginal parfactors) {
		super(removeAggregation(parfactors));
	}

	/**
	 * Returns the specified network with aggregation parfactors converted to
	 * standard parfactors. The resulting network can be used with the C-FOVE
	 * algorithm. 
	 * 
	 * @param network The network where aggregation parfactors will be converted
	 * @return The specified network with aggregation parfactors converted to
	 * standard parfactors. 
	 */
	private static Marginal removeAggregation(Marginal marginal) {
		for (Parfactor parfactor : marginal) {
			if (parfactor instanceof AggParfactor) {
				MacroOperation convert = new ConvertToStdParfactors(marginal, parfactor);
				marginal = convert.run();
			}
		}
		return marginal;
	}
}
