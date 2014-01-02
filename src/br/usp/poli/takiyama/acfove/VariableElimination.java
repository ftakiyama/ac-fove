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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.Substitution;

public class VariableElimination extends ACFOVE {

	private Set<Factor> factors;
	private Set<Prv> query;
	
	// no observation
	@Override
	public Parfactor run() {
		Set<Prv> preservables = new HashSet<Prv>(query); // a more general version would include evidences
		while (thereAreVariablesToEliminate(factors, preservables)) {
			Prv eliminable = selectPrvToEliminate(factors, preservables);
			factors = eliminate(factors, eliminable);
		}
		Factor product = multiplyAll(factors);
		return new StdParfactorBuilder().factor(product).build(); // returns unnormalized factor
	}
	
	// returns true if there is a variable to eliminate
	private boolean thereAreVariablesToEliminate(Set<Factor> allFactors, Set<Prv> preservables) {
		for (Factor factor : allFactors) {
			List<Prv> vars = factor.variables();
			vars.removeAll(preservables);
			if (!vars.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	// returns a prv in some factor that is not preservable
	private Prv selectPrvToEliminate(Set<Factor> factors, Set<Prv> preservables) {
		for (Factor factor : factors) {
			for (Prv prv : factor.variables()) {
				if (!preservables.contains(prv)) {
					return prv;
				}
			}
		}
		return null;
	}
	
	// eliminates a random variable from a set of factors
	private Set<Factor> eliminate(Set<Factor> allFactors, Prv eliminable) {
		Set<Factor> factorsAfterElimination = new HashSet<Factor>(allFactors);
		Factor product = null;
		for (Factor factor : allFactors) {
			if (factor.variables().contains(eliminable)) {
				if (product == null) {
					product = factor;
				} else {
					product = product.multiply(factor);
				}
				factorsAfterElimination.remove(factor);
			}
		}
		Factor sumOut = product.sumOut(eliminable);
		factorsAfterElimination.add(sumOut);
		return factorsAfterElimination;
	}
	
	// multiplies all factors in the specified set, returns null if the set is empty
	private Factor multiplyAll(Set<Factor> factors) {
		Factor product = null;
		for (Factor factor : factors) {
			if (product == null) {
				product = factor;
			} else {
				product = product.multiply(factor);
			}
		}
		return product;
	}
	
	
	public VariableElimination(Marginal parfactors, Level logLevel) {
		super(parfactors, logLevel);
		this.factors = propositionalizeAll(removeAggregation(parfactors));
		this.query = propositionalizeQuery(parfactors);
	}

	public VariableElimination(Marginal parfactors) {
		super(parfactors);
		this.factors = propositionalizeAll(removeAggregation(parfactors));
		this.query = propositionalizeQuery(parfactors);
	}

	/**
	 * Returns the specified network completely propositionalized. The resulting
	 * network can be used with VE algorithms.
	 * <p>
	 * The resulting network will not have logical variables and factors (space
	 * constraint).
	 * </p>
	 * 
	 * @param network The network to propositionalize.
	 * @return the specified network completely propositionalized.
	 */
	private Set<Factor> propositionalizeAll(Marginal marginal) {
		
		Set<LogicalVariable> logicalVariables = new HashSet<LogicalVariable>();
		
		// get all logical variables
		for (Parfactor parfactor : marginal) {
			logicalVariables.addAll(parfactor.logicalVariables());
		}
		
		// Auxiliary set of parfactors
		Set<Parfactor> parfactors = marginal.distribution().toSet();
		
		// propositionalizes all parfactors in the set on all logical variables
		for (LogicalVariable lv : logicalVariables) {
			for (Parfactor parfactor : parfactors) {
				if (parfactor.logicalVariables().contains(lv)) {
					MacroOperation propositionalize = new Propositionalize(marginal, parfactor, lv);
					marginal = propositionalize.run();
				}
			}
			// updates the set of parfactors
			parfactors = marginal.distribution().toSet();
		}
				
		// expands all counting formulas
		for (Parfactor parfactor : parfactors) {
			for (Prv prv : parfactor.prvs()) {
				if (prv instanceof CountingFormula) {
					MacroOperation fullExpand = new FullExpand(marginal, parfactor, prv);
					marginal = fullExpand.run();
				}
			}
			// updates the set of parfactors
			parfactors = marginal.distribution().toSet();
		}
		
		// extracts factors from parfactors
		Set<Factor> factors = getFactors(parfactors);
		
		return factors;
	}
	
	// propositionalizes the query; works for up to 1 parameter
	private Set<Prv> propositionalizeQuery(Marginal marginal) {
		RandomVariableSet preservable = marginal.preservable();
		Set<Prv> query = new HashSet<Prv>();
		
		for (LogicalVariable propositionalizable : preservable.parameters()) {
			for (Constant individual : propositionalizable.individualsSatisfying(preservable.constraints())) {
				Binding b = Binding.getInstance(propositionalizable, individual);
				Substitution s = Substitution.getInstance(b);
				query.add(preservable.prv().apply(s));
			}
		}

		if (preservable.parameters().isEmpty()) {
			query.add(preservable.prv());
		}
		
		return query;
	}
	
	// returns the factors from the specified set of parfactors
	private Set<Factor> getFactors(Set<Parfactor> parfactors) {
		Set<Factor> factors = new HashSet<Factor>(parfactors.size());
		for (Parfactor p : parfactors) {
			factors.add(p.factor());
		}
		return factors;
	}
	

	private static Marginal removeAggregation(Marginal marginal) {
		for (Parfactor parfactor : marginal) {
			if (parfactor instanceof AggParfactor) {
				MacroOperation convert = new ConvertToStdParfactors(marginal, parfactor);
				marginal = convert.run();
			}
		}
		return marginal;
	}
}
