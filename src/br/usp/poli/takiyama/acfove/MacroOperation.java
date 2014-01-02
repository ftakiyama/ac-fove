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
 * Represents a macro operation in (A)C-FOVE.
 * 
 * @author Felipe Takiyama
 */
public interface MacroOperation {
	
	/**
	 * Executes the macro-operation.
	 * TODO: throw exception when the operation is not possible
	 * 
	 * @return The resulting marginal after applying the macro-operation.
	 */
	public Marginal run();
	
	/**
	 * Returns this operation cost.
	 * <p>
	 * The cost of an operation is the size of parfactors it creates.
	 * </p>
	 * 
	 * @return This operation cost.
	 */
	public int cost();
	
	/**
	 * Returns the number of random variables that are eliminated if this
	 * operation is executed using {@link run}.
	 * 
	 * @return the number of random variables that are eliminated if this
	 * operation is executed
	 */
	public int numberOfRandomVariablesEliminated();
	
	@Override
	public String toString();
}
