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
package br.usp.poli.takiyama.samples;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Sets;
import br.usp.poli.takiyama.utils.TestUtils;

/**
 * Water sprinkler network proposed by Kevin Murphy. The network was 
 * adapted to a lifted version: sprinkler and wet_grass are random 
 * variables parameterized on a logical variable Lot.
 * <p>
 * The original network is shown
 * <a href='http://www.cs.ubc.ca/~murphyk/Bayes/bnintro.html'>here</a>
 * </p>
 * <p>
 * The number of nodes in this network is 2 * (domainSize + 1)
 * </p>
 * 
 * @author Felipe Takiyama
 */
public final class WaterSprinklerNetwork {
	
	public final int domainSize;
	
	public final LogicalVariable lot;
	
	public final Prv cloudy;
	public final Prv rain;
	public final Prv sprinkler;
	public final Prv wetGrass;
	
	public final Parfactor cloudyParfactor;
	public final Parfactor rainParfactor;
	public final Parfactor sprinklerParfactor;
	public final Parfactor wetGrassParfactor;
	
	public Parfactor evidenceParfactor;
	public RandomVariableSet query;
	
	public WaterSprinklerNetwork(int domainSize) {
		
		// initializing domain size
		this.domainSize = domainSize;
		
		// initializing the logical variable
		this.lot = StdLogicalVariable.getInstance("Lot", "lot", domainSize);
		
		// initializing PRVs
		this.cloudy = StdPrv.getBooleanInstance("cloudy");
		this.rain = StdPrv.getBooleanInstance("rain");
		this.sprinkler = StdPrv.getBooleanInstance("sprinkler", lot);
		this.wetGrass = StdPrv.getBooleanInstance("wet_grass", lot);
		
		// initializing value arrays used in factors
		List<BigDecimal> fCloudy = TestUtils.toBigDecimalList(0.5, 0.5);
		List<BigDecimal> fRain = TestUtils.toBigDecimalList(0.8, 0.2, 0.2, 0.8);
		List<BigDecimal> fSprinkler = TestUtils.toBigDecimalList(0.5, 0.5, 0.9, 0.1);
		List<BigDecimal> fWetGrass = TestUtils.toBigDecimalList(1.0, 0.0, 0.1, 0.9, 0.1, 0.9, 0.01, 0.99);
		
		// initializing parfactors
		this.cloudyParfactor = new StdParfactorBuilder().variables(cloudy).values(fCloudy).build();
		this.rainParfactor = new StdParfactorBuilder().variables(cloudy, rain).values(fRain).build();
		this.sprinklerParfactor = new StdParfactorBuilder().variables(cloudy, sprinkler).values(fSprinkler).build();
		this.wetGrassParfactor = new StdParfactorBuilder().variables(sprinkler, rain, wetGrass).values(fWetGrass).build();
	}
	
	
	public void setEvidence(Prv evidence, int index) {
		if (evidence.parameters().size() > 0) {
			Substitution lot_i = getLot(index);
			Prv evidenceVariable = evidence.apply(lot_i);
			evidenceParfactor = new StdParfactorBuilder().variables(evidenceVariable).values(0.0, 1.0).build();
		} else {
			evidenceParfactor = new StdParfactorBuilder().variables(evidence).values(0.0, 1.0).build();
		}
	}
	
	
	public void setQuery(Prv query, Constraint ... constraints) {
		this.query = RandomVariableSet.getInstance(query, 
				new HashSet<Constraint>(Arrays.asList(constraints)));
	}
	
	
	public Marginal getMarginal() {
		Marginal marginal;
		if (evidenceParfactor == null) {
			marginal = new StdMarginalBuilder(4).parfactors(cloudyParfactor, 
					rainParfactor, sprinklerParfactor, wetGrassParfactor)
					.preservable(query).build();
		} else {
			marginal = new StdMarginalBuilder(5).parfactors(cloudyParfactor, 
					rainParfactor, sprinklerParfactor, wetGrassParfactor,
					evidenceParfactor).preservable(query).build();
		}
		return marginal;
	}
	
	public Substitution getLot(int index) {
		Term individual = lot.population().individualAt(index);
		Substitution lot_i = Substitution.getInstance(Binding.getInstance(lot, individual));
		return lot_i;
	}
}
