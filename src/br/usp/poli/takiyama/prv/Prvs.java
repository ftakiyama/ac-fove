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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.EqualityConstraint;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.Sets;

/**
 * Operations for {@link Prv}.
 * 
 * @author Felipe Takiyama
 *
 */
public final class Prvs {
	

	public static Substitution mgu(Prv prv1, Prv prv2) throws IllegalArgumentException {
		
		if (!areUnifiable(prv1, prv2)) {
			throw new IllegalArgumentException();
		}
		
		Stack<Constraint> buffer = pushEquations(prv1, prv2);
		List<Binding> mgu = new ArrayList<Binding>();
		
		while (!buffer.isEmpty()) {
			EqualityConstraint equation = (EqualityConstraint) buffer.pop();
			if (hasIdenticalTerms(equation)) {
				// do nothing
			} else if (equation.firstTerm().isVariable()) {
				Binding b = equation.toBinding();
				buffer = apply(b, buffer);
				mgu.add(b);
			} else if (equation.secondTerm().isVariable()) {
				Binding b = equation.toInverseBinding();
				buffer = apply(b, buffer);
				mgu.add(b);
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		Substitution result = Substitution.getInstance(mgu);
		return result;
	}
	
	private static boolean areUnifiable(Prv prv1, Prv prv2) {
		boolean sameFunctor = prv1.name().equals(prv2.name());
		boolean sameNumberOfParam = (prv1.terms().size() == prv2.terms().size());
		return sameFunctor && sameNumberOfParam;
	}
	
	private static Stack<Constraint> pushEquations(Prv prv1, Prv prv2) {
		Stack<Constraint> result = new Stack<Constraint>();
		for (int i = 0; i < prv1.terms().size(); i++) {
			Term t1 = prv1.terms().get(i);
			Term t2 = prv2.terms().get(i);
			result.push(EqualityConstraint.getInstance(t1, t2));
		}
		return result;
	}
	
	private static boolean hasIdenticalTerms(Constraint c) {
		return c.firstTerm().equals(c.secondTerm());
	}
		
	private static Stack<Constraint> apply(Binding b, Stack<Constraint> buffer) {
		Substitution s = Substitution.getInstance(b);
		for (Constraint e : buffer) {
			e = (EqualityConstraint) e.apply(s);
		}
		return buffer;
	}
	
	/**
	 * Returns <code>true</code> if the specified PRVs represent disjoint sets
	 * of random variables.
	 * @param prv1
	 * @param prv2
	 * @return
	 */
	public static boolean areDisjoint(Prv prv1, Prv prv2) {
		
		/*
		 * This algorithm is similar to the one used in Shatter.
		 * 
		 * rename all logical variables
		 * get the MGU between these two variables
		 * if MGU is empty OR error:
		 *     return true
		 * else
		 *     if MGU is not consistent with constraints
		 *     	   return false
		 *     else
		 *     	   return true;
		 */
		
		// Renames all logical variables in PRVs
		boolean areDisjoint = false;
		List<LogicalVariable> allVariables = Lists.union(
				prv1.getCanonicalForm().parameters(), 
				prv2.getCanonicalForm().parameters());
		Prv renamed1 = prv1.apply(NameGenerator.rename(allVariables));
		Prv renamed2 = prv2.apply(NameGenerator.rename(allVariables));
		
		try {
			Substitution mgu = Prvs.mgu(renamed1.getCanonicalForm(), renamed2.getCanonicalForm());
			Set<Constraint> constraints = Sets.union(renamed1.constraints(), renamed2.constraints());
			if (mgu.isEmpty()) {
				// each PRV is a single random variable
				areDisjoint = !(renamed1.equals(renamed2));
			} else {
				areDisjoint = !mgu.isConsistentWith(constraints);
			}
		} catch (IllegalArgumentException e) {
			areDisjoint = true;
		}
		
		return areDisjoint;
	}	
}
