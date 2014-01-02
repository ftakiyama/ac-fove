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

import br.usp.poli.takiyama.cfove.StdParfactor;

/**
 * Visitor for Parfactors.
 * <p>
 * This visitor is used to check whether it is possible to apply some 
 * operation to a {@link Parfactor}s without the need to know
 * which types of parfactors are involved in the verification.
 * </p>
 * <p>
 * This visitor simulates double dispatching to "discover" the types of
 * double-argument methods. 
 * </p>
 * 
 * @author Felipe Takiyama
 *
 * @see <a href = "http://en.wikipedia.org/wiki/Visitor_pattern">
 * Visitor Pattern</a>
 */
public interface DoubleDispatchParfactorVisitor {
	
	/**
	 * Visits the specified {@link StdParfactor}s.
	 * 
	 * @param p The standard parfactor to visit
	 */
	public void visit(StdParfactor p);
	
	
	/**
	 * Visits the specified {@link AggregationParfactor}
	 * 
	 * @param p The aggregation parfactor to visit
	 */
	public void visit(AggregationParfactor p);
}
