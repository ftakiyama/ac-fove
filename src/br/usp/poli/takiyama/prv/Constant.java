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


/**
 * A constant is a word that starts with a lower-case letter. [Poole, 2010]
 * @author Felipe Takiyama
 *
 */
public final class Constant implements Term {
	
	private final String value;

	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	/**
	 * Creates a constant. 
	 * @param value The value of the constant. It must start with a lower-case
	 * letter.
	 * @throws IllegalArgumentException If <code>value</code> does not start 
	 * with a lower-case letter.
	 */
	private Constant(String value) throws IllegalArgumentException {
		this.value = new String(value);
		
		if (Character.isUpperCase(value.charAt(0))) {
			throw new IllegalArgumentException("Exception while creating " +
					"Constant: '" + value + "' must start with " +
					"lowercase letter.");
		}
	}
		
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * Creates a constant. 
	 * 
	 * @param value The value of the constant. It must start with a lower-case
	 * letter.
	 * @throws IllegalArgumentException If <code>value</code> does not start 
	 * with a lower-case letter.
	 */
	public static Constant getInstance(String value) throws IllegalArgumentException {
		return new Constant(value);
	}
	
	
	/**
	 * Returns a constant that is a copy of the specified constant.
	 * @param c The constant to copy.
	 * @return A constant that is a copy of the specified constant.
	 */
	public static Constant getInstance(Constant c) {
		return new Constant(c.value());
	}

	
	/* ************************************************************************
	 *    Inherited methods
	 * ************************************************************************/

	@Override
	public String value() {
		return value;
	}
	

	@Override
	public boolean isVariable() {
		return false;
	}
	
	
	@Override
	public boolean isConstant() {
		return !isVariable();
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		// Tests if both refer to the same object
		if (this == other)
	    	return true;
		// Tests if the Object is an instance of this class
	    if (!(other instanceof Constant))
	    	return false;
	    // Tests if both have the same attributes
	    Constant targetObject = (Constant) other;
	    return this.value.equals(targetObject.value);	    		
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
}
