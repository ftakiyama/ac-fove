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

import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Term;

/**
 * This class represents constraints of the form X ? Y, where
 * X and Y are a Terms and '?' is either '=' or '&ne;'. 
 * <p>
 * Constraints with two constants, like t &ne; q, are invalid. If a method
 * detects a invalid constraint, it returns null.
 * </p>
 *  
 * @author Felipe Takiyama
 *
 */
abstract class AbstractConstraint implements Constraint {
	
	Term firstTerm;
	Term secondTerm;
	
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	@Override
	public Term firstTerm() {
		return this.firstTerm;
	}

	
	@Override
	public Term secondTerm() {
		return this.secondTerm;
	}
	
	
	/* ************************************************************************
	 *    Inherited methods
	 * ************************************************************************/
		
	@Override
	public boolean contains(Term term) {
		return (firstTerm.equals(term) || secondTerm.equals(term));
	}
	
	
	@Override
	public boolean hasCommonTerm(Constraint constraint) {
		return (this.firstTerm().equals(constraint.firstTerm())) 
			|| (this.firstTerm().equals(constraint.secondTerm()))
			|| (this.secondTerm().equals(constraint.firstTerm()))
			|| (this.secondTerm().equals(constraint.secondTerm()));
	}
	
	
	@Override
	public Binding toBinding() throws IllegalStateException {
		return getBinding(firstTerm, secondTerm);
	}
	
	
	@Override
	public Binding toInverseBinding() throws IllegalStateException {
		return getBinding(secondTerm, firstTerm);
	}
	
	
	/**
	 * Creates a {@link Binding} with the specified terms.
	 * @param t1 The first term (the one being replaced)
	 * @param t2 The second term (the replacement)
	 * @return The binding t1/t2
	 * @throws IllegalStateException If t1 is not a {@link LogicalVariable}
	 */
	private Binding getBinding(Term t1, Term t2) throws IllegalStateException {
		if (t1.isVariable()) {
			LogicalVariable lv = (LogicalVariable) t1;
			return Binding.getInstance(lv, t2);
		} else {
			throw new IllegalStateException();
		}
	}
	
	
	@Override
	public boolean isUnary() {
		return ((firstTerm.isVariable() && secondTerm.isConstant()) 
				|| (firstTerm.isConstant() && secondTerm.isVariable())); 
	}
	
	
	@Override
	public Set<LogicalVariable> logicalVariables() {
		Set<LogicalVariable> logicalVariables = new HashSet<LogicalVariable>(2);
		if (firstTerm.isVariable()) {
			logicalVariables.add((LogicalVariable) firstTerm);
		}
		if (secondTerm.isVariable()) {
			logicalVariables.add((LogicalVariable) secondTerm);
		}
		return logicalVariables;
	}
}
