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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.acfove.Shatter.MutableQueue;
import br.usp.poli.takiyama.common.Tuple;


public class MutableQueueTest {
		
	private List<Integer> empty;
	private List<Integer> one;
	private List<Integer> two;
	private List<Integer> three;
	private List<Integer> four;
	
	@Before
	public void setUp() {
		empty = new ArrayList<Integer>(0);
		one = Lists.listOf(0);
		two = new ArrayList<Integer>(2);
		two.add(0);
		two.add(1);
		three = Lists.listOf(0, 1, 2);
		four = Lists.listOf(0, 1, 2, 3);
	}
	
	private <T> List<Tuple<T>> getAllPairs(List<T> list) {
		List<Tuple<T>> result = new ArrayList<Tuple<T>>();
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = i + 1; j < list.size(); j++) {
				List<T> t = Lists.listOf(list.get(i), list.get(j));
				result.add(Tuple.getInstance(t));
			}
		}
		return result;
	}
	
	@Test
	public void testGet() {
		MutableQueue<Integer> queue = new MutableQueue<Integer>(three);
		List<Tuple<Integer>> result = new ArrayList<Tuple<Integer>>(3);
		for (Tuple<Integer> t : queue) {
			result.add(t);
		}
		List<Tuple<Integer>> expected = getAllPairs(three);
		
		assertEquals(expected, result);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreationWithNoElements() {
		new MutableQueue<Integer>(empty);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreationWithOneElement() {
		new MutableQueue<Integer>(one);
	}
	
	/**
	 * Tests whether the iterator returns to the beginning of the queue
	 * after adding an element.
	 */
	@Test
	public void testAddingElementWhileIterating() {
		MutableQueue<Integer> queue = new MutableQueue<Integer>(three);
		List<Tuple<Integer>> result = new ArrayList<Tuple<Integer>>(3);
		
		boolean addedElement = false;
		for (Tuple<Integer> t : queue) {
			if (!addedElement) {
				queue.add(one);
				addedElement = true;
			} else {
				result.add(t);
			}
		}
		List<Integer> list = Lists.listOf(0, 1, 2, 0);
		List<Tuple<Integer>> expected = getAllPairs(list);
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testAddingElementInTheEndOfIteration() {
		MutableQueue<Integer> queue = new MutableQueue<Integer>(three);
		List<Tuple<Integer>> result = new ArrayList<Tuple<Integer>>(3);
		
		int end = 0;
		for (Tuple<Integer> t : queue) {
			if (end == 2) {
				queue.add(one);
			} else {
				result.add(t);
			}
			end++;
		}
		List<Integer> list = Lists.listOf(0, 1, 2, 0);
		List<Tuple<Integer>> expected = getAllPairs(list);
		expected.add(0, Tuple.getInstance(0, 2));
		expected.add(0, Tuple.getInstance(0, 1));
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testRemovingTupleInListWithTwoElements() {
		MutableQueue<Integer> queue = new MutableQueue<Integer>(two);
		List<Tuple<Integer>> result = new ArrayList<Tuple<Integer>>(1);
		
		for (Tuple<Integer> t : queue) {
			// removes the only tuple 
			queue.remove(Tuple.getInstance(0, 1));
			// puts the removed tuple in the result
			result.add(t);
		}
		List<Tuple<Integer>> expected = new ArrayList<Tuple<Integer>>(1);
		expected.add(Tuple.getInstance(0, 1));
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testRemovingTupleInListWithThreeElements() {
		MutableQueue<Integer> queue = new MutableQueue<Integer>(three);
		List<Tuple<Integer>> result = new ArrayList<Tuple<Integer>>(1);
		
		for (Tuple<Integer> t : queue) {
			/*
			 * Removes the first tuple. The queue is left with one element,
			 * so iteration restarts but imediately stops.
			 */
			queue.remove(Tuple.getInstance(0, 1));
			// puts the removed tuple in the result
			result.add(t);
		}
		List<Tuple<Integer>> expected = new ArrayList<Tuple<Integer>>(1);
		expected.add(Tuple.getInstance(0, 1));
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testRemovingTupleInListWithManyElements() {
		MutableQueue<Integer> queue = new MutableQueue<Integer>(four);
		List<Tuple<Integer>> result = new ArrayList<Tuple<Integer>>(1);
		
		for (Tuple<Integer> t : queue) {
			/*
			 * Removes the tuple (1,3). The queue is now {0, 2}. Iteration will
			 * restart and try to remove tuple (1,3) again, which removes
			 * nothing. Thus, this step is ignored once it has been executed
			 * for the first time.
			 */
			queue.remove(Tuple.getInstance(1, 3));
			// puts the removed tuple in the result
			result.add(t);
		}
		List<Tuple<Integer>> expected = new ArrayList<Tuple<Integer>>(2);
		expected.add(Tuple.getInstance(0, 1));
		expected.add(Tuple.getInstance(0, 2));
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testAddingAndRemovingElements() {
		MutableQueue<Integer> queue = new MutableQueue<Integer>(three);
		List<Tuple<Integer>> result = new ArrayList<Tuple<Integer>>(1);
		
		int count = 0;
		for (Tuple<Integer> t : queue) {
			switch(count) {
			case 2:
				// adds new elements after 2 iterations
				queue.add(two);
				break;
			case 3:
				// removes (1,2) after 3 iterations
				queue.remove(Tuple.getInstance(1, 2));
				break;
			default:
				break;
			}
			
			result.add(t);
			count++;
		}
		List<Tuple<Integer>> expected = new ArrayList<Tuple<Integer>>(2);
		expected.add(Tuple.getInstance(0, 1));
		expected.add(Tuple.getInstance(0, 2));
		expected.add(Tuple.getInstance(1, 2));
		// at this point elements 0, 1 are added and iteration restarts
		expected.add(Tuple.getInstance(0, 1));
		// at this point elements 1, 2 are removed (the first 1) and iteration restarts
		expected.add(Tuple.getInstance(0, 0));
		expected.add(Tuple.getInstance(0, 1));
		expected.add(Tuple.getInstance(0, 1));
		
		assertEquals(expected, result);
	}
}
