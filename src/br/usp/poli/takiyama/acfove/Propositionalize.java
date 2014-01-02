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

import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Population;
import br.usp.poli.takiyama.prv.Substitution;


/**
 * This operation executes a split on a parfactor for every 
 * constant in the population of a free logical variable that appears in this
 * parfactor.
 * <p>
 * Given a set of parfactors, it is always possible to propositionalize it on a 
 * free logical variable.
 * </p>
 * <p>
 * After all the splits, the {@link Shatter} macro operation is invoked to 
 * guarantee that all parameterized random variables represent equal or
 * disjoint sets of random variables.
 * </p>
 */
public final class Propositionalize implements MacroOperation {

	private Marginal marginal;
	
	private final Parfactor propositionalizable;
	private final LogicalVariable freeVariable;
	
	public Propositionalize(Marginal marginal, Parfactor propositionalizable, LogicalVariable freeVariable) {
		this.marginal = marginal;
		this.propositionalizable = propositionalizable;
		this.freeVariable = freeVariable;
	}
	
	@Override
	public Marginal run() {

		Parfactor splittable = propositionalizable;
		
		StdMarginalBuilder resultBuilder = new StdMarginalBuilder();
		resultBuilder.add(marginal).remove(propositionalizable);
		
		Population population = getIndividuals();
		for (Constant individual : population) {
			Substitution sub = Substitution.getInstance(
					Binding.getInstance(freeVariable, individual));
			if (splittable.isSplittable(sub)) {
				SplitResult splitResult = splittable.splitOn(sub);
				resultBuilder.add(splitResult.result());
				if (splitResult.residue().size() == 1) {
					splittable = splitResult.residue().iterator().next();
				} else {
					throw new IllegalStateException("Split result has more than 1 residue!");
				}
			} else {
				resultBuilder.add(splittable);
			}
		}
		
		// Shatters the new marginal and returns the result
		Marginal result = new Shatter(resultBuilder.build()).run();
		return result;
	}
	
	/**
	 * Returns free logical variable population that satisfies constraints 
	 * from the parfactor being propositionalized.
	 */
	private Population getIndividuals() {
		return freeVariable.individualsSatisfying(propositionalizable.constraints());
	}

	/*
	 * The cost of propositionalization is given by the following expression:
	 * <p>
	 * |F| x |D(X):C|
	 * </p>
	 * <p>
	 * where
	 * </p>
	 * <li> |F| is factor component size from parfactor being propositionalized 
	 * <li> |D(X):C| is the number of free logical variable individuals 
	 * satisfying constraints from the parfactor being propositionalized
	 * <p>
	 * Propositionalization actually creates many factors with the same size
	 * as the original parfactor. Thus, the cost would have to be the size of
	 * the parfactor being propositionalized. Nevertheless, this macro-operation
	 * is called when no other operation is possible, because it 'destroys'
	 * lifted elimination. The above expression makes this operation more
	 * expensive.
	 * </p>
	 */
	
	/**
	 * Returns infinity - 1. This operation is only feasible when no other
	 * operation is possible.
	 */
	@Override
	public int cost() {
		return ((int) Double.POSITIVE_INFINITY) - 1;
	}

	/**
	 * Returns zero.
	 */
	@Override
	public int numberOfRandomVariablesEliminated() {
		return 0;
	}

	@Override
	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("PROPOSITIONALIZE").append("\n")
//				.append(propositionalizable).append("\n")
//				.append(freeVariable);
//		return builder.toString();
		return "";
	}
}
