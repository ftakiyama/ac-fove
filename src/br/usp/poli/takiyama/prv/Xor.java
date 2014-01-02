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

/**
 * The <code>XOR</code> logical operator, applicable to {@link  Bool} elements.
 * 
 * @author Felipe Takiyama
 */
public final class Xor implements Operator<Bool> {
	
	/**
	 * The <code>Or</code> operator. It is applicable to boolean arguments.
	 */
	public static final Xor XOR = new Xor();
	
	/**
	 * Private constructor that enforces non-instantiability.
	 */
	private Xor() { }
	
	
	@Override
	public Bool applyOn(Bool e1, Bool e2) {
		return Bool.valueOf(e1.value() ^ e2.value());
	}

	
	@Override
	public Bool applyOn(Bool e1, Bool e2, Bool e3) {
		return Bool.valueOf(e1.value() ^ e2.value() ^ e3.value());
	}
	

	@Override
	public Bool applyOn(Set<Bool> s) throws IllegalArgumentException,
	  		NullPointerException {
		
		if (s == null) {
			throw new NullPointerException();
		}
		if (s.isEmpty()) {
			throw new IllegalArgumentException();
		}
		if (s.size() == 1) {
			return s.iterator().next();
		}
		boolean result = false;
		for (Bool b : s) {
			result = result ^ b.value();
		}
		return Bool.valueOf(result);
	}

	
	@Override
	public Bool apply(Bool e, int n) throws IllegalArgumentException,
			NullPointerException {
		
		if (e == null) {
			throw new NullPointerException();
		}
		if (n < 0) {
			throw new IllegalArgumentException();
		}
		return e; // trivial operation for boolean elements
	}
	
	
	@Override
	public Class<Bool> getTypeArgument() {
		return Bool.class;
	}
	
	
	@Override
	public String toString() {
		return "XOR";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return (obj instanceof Xor);
	}
	
	/**
	 * Returns 3
	 */
	@Override
	public int hashCode() {
		return 3; // singleton
	}
}
