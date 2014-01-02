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
 * This class represents elements from parameterized random variable ranges.
 * 
 * @author Felipe Takiyama
 *
 */
public interface RangeElement {

	public RangeElement combine(RangeElement e);
	//public RangeElement apply(Operator<? extends RangeElement> op);
	
	/**
	 * Returns the result of applying the specified operator to this range
	 * element.
	 * 
	 * @param op The operator to apply to this object
	 * @return the result of applying the specified operator to this range
	 * element.
	 */
	public RangeElement apply(Operator<? extends RangeElement> op);
}
