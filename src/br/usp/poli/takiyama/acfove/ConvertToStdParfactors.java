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

import br.usp.poli.takiyama.common.AggregationParfactor;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Prv;

public final class ConvertToStdParfactors implements MacroOperation {

	private final Parfactor parfactorToConvert;
	private final Marginal marginal;
	
	public ConvertToStdParfactors(Marginal m, Parfactor p) {
		this.marginal = m;
		this.parfactorToConvert = p;
	}
	
	@Override
	public Marginal run() {
		StdMarginalBuilder m = new StdMarginalBuilder();
		m.add(marginal);
		if (parfactorToConvert instanceof AggregationParfactor) {
			m.parfactors(((AggregationParfactor) parfactorToConvert).toStdParfactors());
			m.remove(parfactorToConvert);
		}
		return m.build();
	}

	/**
	 * Let g be an aggregation parfactor
	 * Returns the size of the factor component if we multiplied the parfactors
	 * that result from converting g to standard parfactors.
	 */
	@Override
	public int cost() {
		int cost = (int) Double.POSITIVE_INFINITY;
		if (parfactorToConvert instanceof AggregationParfactor) {
			AggregationParfactor ap = (AggregationParfactor) parfactorToConvert; 
			cost = ap.extraVariable().numberOfIndividualsSatisfying(ap.constraintsOnExtra());
			for (Prv var : ap.prvs()) {
				cost = cost * var.range().size();
			}
		}
		return cost;
	}

	/**
	 * Returns 0.
	 */
	@Override
	public int numberOfRandomVariablesEliminated() {
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CONVERT-TO-STD-PARFACTORS");
		return builder.toString();
	}
}
