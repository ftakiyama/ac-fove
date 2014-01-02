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
package br.usp.poli.takiyama.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.usp.poli.takiyama.common.ConstantFactor;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.StdFactor;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Term;

public class TestUtils {
	
	/**
	 * Returns a list of {@link BigDecimal} based on a list of doubles.
	 * @param list The list of doubles to convert to BigDecimal.
	 * @return A list of {@link BigDecimal} based on a list of doubles.
	 */
	public static List<BigDecimal> toBigDecimalList(double ... list) {
		List<BigDecimal> result = new ArrayList<BigDecimal>(list.length);
		for (int i = 0; i < list.length; i++) {
			result.add(BigDecimal.valueOf(list[i]));
		}
		return result;
	}
	
	public static class FactorBuilder {
		
		private final List<Prv> variables;
		private final List<BigDecimal> values;
		
		public FactorBuilder() {
			variables = new ArrayList<Prv>();
			values = new ArrayList<BigDecimal>();
		}
		
		public FactorBuilder vars(String ... vars) {
			for (String v : vars) {
				// remove whitespace characters
				v = v.replaceAll("\\s", "");
				// \w+ \( (\w+,)*(\w+) | () \)
				if (v.matches("(\\w)+\\(((((\\w)+,)*(\\w)+)|())\\)")) {
					// get functor and terms
					String [] tokens = v.split("[\\(\\),]");
					// first one is functor
					String functor = tokens[0];
					// remaining are terms
					tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
					Term [] terms = new Term[tokens.length];
					int p = 0;
					for (String t : tokens) {
						// accepts only constants as terms
						if (Character.isLowerCase(t.charAt(0))) {
							Constant c = Constant.getInstance(t);
							terms[p] = c;
							p++;
						} else {
							throw new IllegalArgumentException("Invalid term: '" + t + "'");
						}
					}
					// builds PRV, which is actually a RV
					Prv prv = StdPrv.getBooleanInstance(functor, terms);
					variables.add(prv);
				} else {
					throw new IllegalArgumentException("Invalid PRV: '" + v + "'");
				}
			}
			return this;
		}
		
		public FactorBuilder vals(double ... vals) {
			values.addAll(TestUtils.toBigDecimalList(vals));
			return this;
		}
		
		public FactorBuilder clear() {
			this.values.clear();
			this.variables.clear();
			return this;
		}
		
		public Factor build() {
			if (values.size() > 0) {
				return StdFactor.getInstance("", variables, values);
			} else {
				return StdFactor.getInstance(variables);
			}
		}
	}
}
