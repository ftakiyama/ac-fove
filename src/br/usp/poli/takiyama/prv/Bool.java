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
 * This class represents boolean values, wrapping the <code>boolean</code>
 * primitive type. 
 * 
 * @author Felipe Takiyama
 *
 */
public final class Bool implements RangeElement {
	
	public static final Bool TRUE = new Bool(true);
	public static final Bool FALSE = new Bool(false);
	
	private boolean value;

	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	/**
	 * Creates a Bool object with the specified boolean value.
	 * @param value The value of this object
	 */
	private Bool(boolean value) {
		this.value = value;
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * Returns a <code>Bool</code> instance representing the specified boolean 
	 * value. If the specified boolean value is <code>true</code>, this method 
	 * returns <code>Bool.TRUE</code>; if it is <code>false</code>, this method 
	 * returns <code>Bool.FALSE</code>. 
	 * 
	 * @param b A boolean value
	 * @return A <code>Bool</code> instance representing <code>b</code>
	 */
	public static Bool valueOf(boolean b) {
		return (b ? TRUE : FALSE);
	}

	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	/**
	 * Returns the value of this <code>Bool</code> object as a boolean
	 * primitive.
	 * 
	 * @return the primitive <code>boolean</code> value of this object
	 */
	public boolean value() {
		return value;
	}
	
	
	/**
	 * Throws {@link UnsupportedOperationException}.
	 */
	public RangeElement combine(RangeElement e) {
		throw new UnsupportedOperationException("Not implemented");
	}
	

	/**
	 * Returns this object.
	 */
	@Override
	public RangeElement apply(Operator<? extends RangeElement> op) {
		return this;
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	/**
	 *  Returns a hash code for this <code>Bool</code> object.
	 *  
	 *  @return the integer <code>1231</code> if this object represents 
	 *  <code>true</code>; returns the integer <code>1237</code> if this object 
	 *  represents <code>false</code>.
	 */
	@Override
	public int hashCode() {
		return value ? 1231 : 1237;
    }
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Bool) {
			return value == ((Bool)obj).value();
		}
        return false;
	}
	
	
	@Override
	public String toString() {
		return value ? "true" : "false";
	}
}
