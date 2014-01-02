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

import java.util.Iterator;
import java.util.Set;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Prvs;
import br.usp.poli.takiyama.prv.RandomVariableSet;

public final class FinalMultiplication implements MacroOperation {

	private final Marginal marginal;
	
	public FinalMultiplication(Marginal m) {
		marginal = m;
	}
	
	@Override
	public Marginal run() {
		Parfactor product = new StdParfactorBuilder().build();
		
		// Multiplies all parfactors in the marginal
		for (Parfactor candidate : marginal) {
			product = product.multiply(candidate);
		}
		Marginal result = new StdMarginalBuilder(1).parfactors(product).build();
		
		return result;
	}

	/**
	 * Returns the size of the first factor returned by marginal iterator.
	 * This operation should be called when all parfactors have the same size.
	 */
	@Override
	public int cost() {
		int cost = (int) Double.POSITIVE_INFINITY;
		if (marginalHasOnlyPreservable()) {
			Iterator<Parfactor> it = marginal.iterator();
			if (it.hasNext()) {
				cost = it.next().factor().size();
			}
		}
		return cost;
	}
	
	private boolean marginalHasOnlyPreservable() {
		for (Parfactor p : marginal) {
			for (Prv prv : p.prvs()) {
				RandomVariableSet rvs = RandomVariableSet.getInstance(prv, p.constraints());
				if (Prvs.areDisjoint(rvs, marginal.preservable())) {
					return false;
				}
			}
		}
		return true;
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
		return "FINAL-MULTIPLICATION";
	}
}
