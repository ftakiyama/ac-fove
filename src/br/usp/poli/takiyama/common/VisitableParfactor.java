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
 * Parfactors that implement this interface are considered 'visitable' by
 * {@link ParfactorVisitor}.
 * <p>
 * This interface provides auxiliary methods to implement Visitor Pattern 
 * using triple dispatch to avoid the use of <code>instanceof</code>.
 * </p>
 * 
 * @author Felipe Takiyama
 * 
 * @see ParfactorVisitor
 *
 */
public interface VisitableParfactor {
	
	/**
	 * Accepts the specified visitor.
	 * 
	 * @param visitor The visitor of this parfactor
	 * @param p A parfactor whose type is yet to be discovered.
	 */
	public void accept(ParfactorVisitor visitor, Parfactor p);
	
	
	/**
	 * Accepts the specified visitor.
	 * 
	 * @param visitor The visitor of this parfactor
	 * @param p The other parfactor to be visited by the visitor
	 */
	public void accept(ParfactorVisitor visitor, StdParfactor p); 
	
	
	/**
	 * Accepts the specified visitor.
	 * 
	 * @param visitor The visitor of this parfactor
	 * @param p The other parfactor to be visited by the visitor
	 */
	public void accept(ParfactorVisitor visitor, AggregationParfactor p);
	
}
