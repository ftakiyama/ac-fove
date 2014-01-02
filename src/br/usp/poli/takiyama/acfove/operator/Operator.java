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
 * A commutative and associative binary operator, such as AND and OR.
 * <br>
 * All classes that implement this interface must have represent an operator 
 * &otimes; with the following properties:
 * <li> &otimes; must be binary
 * <li> &otimes; must be commutative, that is, A &otimes; B = B &otimes; A
 * <li> &otimes; must be associative, that is, 
 * (A &otimes; B) &otimes; C = A &otimes; (B &otimes; C)
 * @Deprecated

 * @author ftakiyama
 *
 */
interface Operator<T extends Object> {
	
	/**
	 * Applies the operator to the specified arguments.
	 * 
	 * @param a The first argument
	 * @param b The second argument
	 * @return The result of applying the operator to the specified arguments.
	 */
	public T applyOn(T a, T b);
	
	/**
	 * Applies the operator to the specified arguments.
	 * 
	 * @param a The first argument
	 * @param b The second argument
	 * @param c The third argument
	 * @return The result of applying the operator to the specified arguments.
	 */
	public T applyOn(T a, T b, T c);
	
	/**
	 * Applies the operator to all elements in the specified set.
	 * <br>
	 * Thanks to commutative and associative properties, the result of
	 * applying the operator to the elements of the set is independent of 
	 * the order in which it is applied.
	 * <br>
	 * If the specified set contains only one element, then returns this
	 * element.
	 * 
	 * @param s A set of elements where the operator will be applied.
	 * @return The result of applying the operator to the elements of the
	 * specified set.
	 * @throws IllegalArgumentException if the specified set is empty 
	 * @throws NullPointerException if the specified set is null
	 */
	public T applyOn(Set<T> s) throws IllegalArgumentException, NullPointerException;
	
	/**
	 * Applies the operator to the specified element the specified number of
	 * times.
	 * <br>
	 * If the specified number is 0, then returns the specified element.
	 * 
	 * @param a The first argument
	 * @param n The number of times to apply this operator.
	 * @return The result of applying this operator n times.
	 * @throws IllegalArgumentException If n < 1
	 * @throws NullPointerException if the specified element is null
	 */
	public T applyOn(T a, int n) throws IllegalArgumentException, NullPointerException;
}
