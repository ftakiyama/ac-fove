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
package br.usp.poli.takiyama.prv;

/**
 * @deprecated
 * Individuals are things in the world that is being described (e.g., a 
 * particular house or a particular booking may be individuals) [Poole, 2010]
 * 
 * @author ftakiyama
 *
 */
final class Individual {
	private final String name;
	
	/**
	 * Static factory. Use this method if you want a new Individual.
	 * @param name The name of the individual
	 * @return An individual whose name is 'name'
	 */
	static Individual getInstance(String name) {
		return new Individual(name);
	}
	
	/**
	 * Constructor. Creates a new individual. 
	 * @param name The name of the individual
	 */
	private Individual(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of this individual.
	 * @return The name of this individual.
	 */
	public String getName() {
		return this.name;
	}
}
