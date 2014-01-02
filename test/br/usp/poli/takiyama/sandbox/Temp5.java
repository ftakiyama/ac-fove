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
package br.usp.poli.takiyama.sandbox;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import br.usp.poli.takiyama.acfove.AggParfactor.AggParfactorBuilder;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdFactor;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Or;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.TestUtils;


public class Temp5 {
	
	/**
	 * Checking the factor created just after performing sum-out on 
	 * aggregation parfactor.
	 */
	@Test
	public void testBigJackpotInference() {
		
		int populationSize = 2;
		
		LogicalVariable person = StdLogicalVariable.getInstance("Person", "x", populationSize);
		
		Prv big_jackpot = StdPrv.getBooleanInstance("big_jackpot");
		Prv played = StdPrv.getBooleanInstance("played", person);
		Prv matched_6 = StdPrv.getBooleanInstance("matched_6", person);
		Prv jackpot_won = StdPrv.getBooleanInstance("jackpot_won");
		
		List<BigDecimal> fBigJackpot = TestUtils.toBigDecimalList(0.8, 0.2);
		List<BigDecimal> fPlayed = TestUtils.toBigDecimalList(0.95, 0.05, 0.85, 0.15);
		List<BigDecimal> fMatched6 = TestUtils.toBigDecimalList(1.0, 0.0, 0.99999993, 0.00000007);
		
		Parfactor g1 = new StdParfactorBuilder().variables(big_jackpot).values(fBigJackpot).build();
		Parfactor g2 = new StdParfactorBuilder().variables(big_jackpot, played).values(fPlayed).build();
		Parfactor g3 = new StdParfactorBuilder().variables(played, matched_6).values(fMatched6).build();
		Parfactor g4 = new AggParfactorBuilder(matched_6, jackpot_won, Or.OR).context(big_jackpot).build();
		
		Parfactor g2xg3 = g2.multiply(g3);
		Parfactor afterEliminatingPlayed = g2xg3.sumOut(played);
		Parfactor g4xg5 = g4.multiply(afterEliminatingPlayed);
		Parfactor afterEliminatingMatched6 = g4xg5.sumOut(matched_6);

		Factor r = getCorrectResultOfBigJackpotInference(populationSize);
		Parfactor expected = new StdParfactorBuilder().factor(r).build();
		
		assertEquals(expected, afterEliminatingMatched6);
		
	}
	
	// propositionalizes the model above and calculates the factor created
	// after eliminating the aggregation parfactor
	private Factor getCorrectResultOfBigJackpotInference(int n) {
		
		List<Prv> matched_6 = new ArrayList<Prv>(n);
		for (int i = 0; i < n; i++) {
			Constant p = Constant.getInstance("x" + i);
			matched_6.add(StdPrv.getBooleanInstance("matched_6", p));
		}
		
		Prv jackpot_won = StdPrv.getBooleanInstance("jackpot_won");
		
		// Creates factor on matched_6(x0) ... matched_6(xn) jackpot_won()
		List<Prv> vars = Lists.listOf(matched_6);
		vars.add(jackpot_won);
		List<BigDecimal> fagg = new ArrayList<BigDecimal>();
		for (int i = 0; i < (int) Math.pow(2, n); i++) {
			fagg.add(BigDecimal.ZERO);
			fagg.add(BigDecimal.ONE);
		}
		fagg.set(0, BigDecimal.ONE);
		fagg.set(1, BigDecimal.ZERO);
		
		Factor jw = StdFactor.getInstance("", vars, fagg);
		
		// Creates factor on big_jackpot() matched_6(X)
		Prv big_jackpot = StdPrv.getBooleanInstance("big_jackpot");
		List<BigDecimal> f5vals = TestUtils.toBigDecimalList(0.9999999965, 0.0000000035, 0.9999999895, 0.0000000105);
		List<Factor> f5 = new ArrayList<Factor>(n);
		for (Prv m6 : matched_6) {
			List<Prv> rvs = new ArrayList<Prv>(2);
			rvs.add(big_jackpot);
			rvs.add(m6);
			f5.add(StdFactor.getInstance("", rvs, f5vals));
		}

		// Creates factor big_jackpot()
		List<BigDecimal> fbj = TestUtils.toBigDecimalList(0.8, 0.2);
		Factor bj = StdFactor.getInstance("", big_jackpot, fbj);
		
		// multiplies all factors
		Factor product = jw;
		for (Factor f : f5) {
			product = product.multiply(f);
		}
		
		// eliminates matched_6(x)
		Factor result = product;
		for (Prv m6 : matched_6) {
			result = result.sumOut(m6);
		}
				
		return result;
	}
}
