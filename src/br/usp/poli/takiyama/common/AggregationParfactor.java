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

import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Operator;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RangeElement;

public interface AggregationParfactor extends Parfactor {
	
	/**
	 * Returns the parent PRV of this aggregation parfactor
	 * 
	 * @return the parent PRV of this aggregation parfactor
	 */
	public Prv parent();
	
	
	/**
	 * Returns the child PRV of this aggregation parfactor
	 * 
	 * @return the child PRV of this aggregation parfactor
	 */
	public Prv child();
	
	
	/**
	 * Returns the list of context PRV in this aggregation parfactor.
	 * @return the list of context PRV in this aggregation parfactor.
	 */
	public List<Prv> context();
	
	
	/**
	 * Returns the result of converting this aggregation parfactor to 
	 * {@link StdParfactor}s.
	 * 
	 * @return A {@link Distribution} containing the result of converting 
	 * this aggregation parfactor to {@link StdParfactor}s.
	 */
	public Distribution toStdParfactors();


	/**
	 * Returns the set of constraints on the extra logical variable.
	 * 
	 * @return The set of constraints on the extra logical variable.
	 */
	public Set<Constraint> constraintsOnExtra();
	
	
	/**
	 * Returns the set of constraints that do not involve the extra 
	 * logical variable.
	 * 
	 * @return The set of constraints not on the extra logical variable.
	 */
	public Set<Constraint> constraintsNotOnExtra();
	

	/**
	 * Returns the operator that defines the type of aggregation in this 
	 * parfactor.
	 * @return The operator that defines the type of aggregation in this 
	 * parfactor.
	 */
	Operator<? extends RangeElement> operator();


	/**
	 * Returns the extra logical variable in parent PRV.
	 * 
	 * @return The extra logical variable in parent PRV.
	 */
	public LogicalVariable extraVariable();
	
	
	@Override
	public int hashCode();
	
	@Override
	public boolean equals(Object o);
	
	@Override
	public String toString();


	

	
}
