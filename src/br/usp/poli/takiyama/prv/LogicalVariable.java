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

import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;

/**
 * A logical variable is a word starting with an upper-case letter or the 
 * underscore. [Poole, 2010]
 * 
 * @author Felipe Takiyama
 *
 */
public interface LogicalVariable extends Term {
	
	
	/**
	 * Returns <code>true</code> if the population associated with this 
	 * logical variable contains the specified individual.
	 * @param c The individual to look for.
	 * @return <code>true</code> if the specified individual is in the
	 * population associated with this logical variable, <code>false</code>
	 * otherwise.
	 */
	//public boolean contains(Constant c);
	
		
	/**
	 * Returns all individuals of the population of this logical variable that
	 * satisfy the specified set of constraints of the form X != t.
	 * (t must be a constant).
	 * <p>
	 * Note that, if t is a logical variable, then there is no way of finding
	 * out which value to constrain in the population. Binary constraints are
	 * silently ignored during account. 
	 * </p>
	 * <p>
	 * This method does not check whether the constraints are consistent, that
	 * is, if the second term is a constant representing an individual from 
	 * this logical variable population.
	 * </p>
	 * 
	 * @param constraints A set of constraints that restricts the individuals
	 * of the population from this logical variable
	 * @return A set containing all individuals satisfying the specified set
	 * of constraints.
	 */
	public Population individualsSatisfying(Set<Constraint> constraints);
	
	
	/**
	 * Returns the population associated with this logical variable.
	 * @return The population associated with this logical variable.
	 */
	public Population population();
	
	
	/**
	 * Removes the specified individual from this logical variable.
	 * @param individual The individual to be removed
	 * @return A copy of this logical variable with the specified 
	 * individual removed.
	 */
	//public LogicalVariable remove(Constant individual);
	
	
	/**
	 * Renames this logical variable. 
	 * A new instance of LogicalVariable will be created.
	 * @param newName The new name
	 */
	public LogicalVariable rename(String name);
	
	
	/**
	 * Returns the excluded set for this Logical Variable, that is, the set of
	 * terms t such that (X &ne; t) &in; C (specified set of constraints).
	 *  
	 * @param constraints A set of {@link InequalityConstraint}
	 * @return The excluded set for this logical variable
	 */
	public Set<Term> excludedSet(Set<Constraint> constraints);
	
	
	/**
	 * Returns the number of individuals satisfying the specified set of
	 * constraints.
	 * 
	 * @param constraints A set of {@link InequalityConstraint}
	 * @return the number of individuals satisfying the specified set of
	 * constraints.
	 */
	public int numberOfIndividualsSatisfying(Set<Constraint> constraints);
	
	
	/**
	 * Returns <code>true</code> if this logical variable has no name and its
	 * population is empty.
	 * 
	 * @return <code>true</code> if this is an empty logical variable, 
	 * <code>false</code> otherwise
	 */
	public boolean isEmpty();
	
	
	@Override
	public String toString();
	

	@Override
	public int hashCode();
	

	@Override
	public boolean equals(Object o);
}
