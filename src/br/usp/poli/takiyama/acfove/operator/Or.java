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
package br.usp.poli.takiyama.acfove.operator;

import java.util.Set;

/**
 * Aggregation operator.
 * @author ftakiyama
 *@Deprecated
 */
final public class Or implements BooleanOperator {

	/**
	 * The <code>Or</code> operator. It is applicable to boolean arguments.
	 */
	public static Or OR = new Or();
	
	/**
	 * Private constructor that enforces non-instantiability.
	 */
	private Or() { }
	
	@Override
	public Boolean applyOn(Boolean a, Boolean b) {
		boolean b1 = a;
		boolean b2 = b;
		return Boolean.valueOf((b1 || b2));
	}
	
	@Override
	public Boolean applyOn(Boolean a, Boolean b, Boolean c) {
		boolean b1 = a.booleanValue();
		boolean b2 = b.booleanValue();
		boolean b3 = c.booleanValue();
		return Boolean.valueOf(b1 || b2 || b3);
	}
	
	@Override
	public Boolean applyOn(Set<Boolean> s) throws IllegalArgumentException,
												  NullPointerException {
		if (s == null) {
			throw new NullPointerException();
		}
		if (s.isEmpty()) {
			throw new IllegalArgumentException("The specified set is empty.");
		}
		boolean result = false;
		for (Boolean b : s) {
			boolean temp = b.booleanValue();
			result = result || temp;
		}
		return result;
	}
	
	@Override
	public Boolean applyOn(Boolean a, int n) throws IllegalArgumentException,
	    											NullPointerException {
		if (a == null) {
			throw new NullPointerException();
		}
		if (n < 1) {
			throw new IllegalArgumentException();
		}
		boolean b = a.booleanValue();
		boolean result = b;
		for (int i = 0; i < n; i++) {
			result = result || b;
		}
		return Boolean.valueOf(result);
	}

	@Override
	public boolean getIdentity() {
		return false;
	}
	
	@Override
	public String toString() {
		return "OR";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return (obj instanceof Or);
	}
	
	/**
	 * Returns 1, since this is a singleton.
	 */
	@Override
	public int hashCode() {
		return 1; // singleton
	}
}
