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

/**
 * Dummy operation. It is not supposed to be executed.
 * @author Felipe Takiyama
 */
final class ImpossibleOperation implements MacroOperation {

	public static final ImpossibleOperation instance = new ImpossibleOperation();
	
	private ImpossibleOperation() {
		// cannot be instantiated.
	}
	
	/**
	 * Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public Marginal run() {
		throw new UnsupportedOperationException("This is a impossible operation!");
	}

	/**
	 * Returns {@link Double#POSITIVE_INFINITY}.
	 */
	@Override
	public int cost() {
		return ((int) Double.POSITIVE_INFINITY);
	}

	/**
	 * Returns -1.
	 */
	@Override
	public int numberOfRandomVariablesEliminated() {
		return 0;
	}

	@Override
	public String toString() {
		return "IMPOSSIBLE-OPERATION";
	}
}
