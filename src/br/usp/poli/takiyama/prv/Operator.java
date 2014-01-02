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
 * A commutative and associative binary operator, such as AND and OR.
 * <p>
 * All classes that implement this interface must have represent an operator 
 * &otimes; with the following properties:
 * <li> &otimes; must be binary
 * <li> &otimes; must be commutative, that is, A &otimes; B = B &otimes; A
 * <li> &otimes; must be associative, that is, 
 * (A &otimes; B) &otimes; C = A &otimes; (B &otimes; C)
 * </p>
 * <p>
 * The operator must also be closed: once defined over a set A, if we apply the
 * operator on any two elements of A, the result must also belong to A.
 * </p>
 * 
 * @author Felipe Takiyama
 *
 */
public interface Operator<E extends RangeElement> {
	
	/**
	 * Applies the operator to the specified elements.
	 * 
	 * @param e1 The first element
	 * @param e2 The second element
	 * @return The result of applying the operator to the specified element.
	 */
	public E applyOn(E e1, E e2);
	
	
	/**
	 * Applies the operator to the specified elements.
	 * 
	 * @param e1 The first element
	 * @param e2 The second element
	 * @param e3 The third element
	 * @return The result of applying the operator to the specified element.
	 */
	public E applyOn(E e1, E e2, E e3);
	
	
	/**
	 * Applies the operator to all elements in the specified set.
	 * <p>
	 * Thanks to commutative and associative properties, the result of
	 * applying the operator to the elements of the set is independent of 
	 * the order in which it is applied.
	 * </p>
	 * <p>
	 * If the specified set contains only one element, then returns this
	 * element.
	 * </p>
	 * 
	 * @param s A set of elements where the operator will be applied.
	 * @return The result of applying the operator to the elements of the
	 * specified set.
	 * 
	 * @throws IllegalArgumentException if the specified set is empty 
	 * @throws NullPointerException if the specified set is null
	 */
	public E applyOn(Set<E> s) throws IllegalArgumentException, NullPointerException;
	
	
	/**
	 * Applies the operator to the specified element the specified number of
	 * times.
	 * <p>
	 * If the specified number is 0, then returns the specified element.
	 * </p>
	 * <p>
	 * If the specified number negative, throws a {@link IllegalArgumentException}.
	 * </p>
	 * 
	 * @param e The element to apply the operator on
	 * @param n The number of times to apply this operator.
	 * @return The result of applying this operator n times.
	 * 
	 * @throws IllegalArgumentException If n < 0
	 * @throws NullPointerException if the specified element is null
	 */
	public E apply(E e, int n) throws IllegalArgumentException, NullPointerException;
	
	
	public Class<E> getTypeArgument();
}
