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
package br.usp.poli.takiyama.common;

import java.util.Set;

import br.usp.poli.takiyama.prv.LogicalVariable;

/**
 * Implements decorator pattern for parfactors.
 * @author Felipe Takiyama
 */
public interface ParfactorDecorator extends Parfactor {
	
	/**
	 * Returns the set of all logical variables in this parfactor, including
	 * bound variables from counting formulas.
	 */
	@Override
	public Set<LogicalVariable> logicalVariables();
}
