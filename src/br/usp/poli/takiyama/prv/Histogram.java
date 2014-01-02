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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.MathUtils.Multinomial;

/**
 * This class represents the elements of the range of a counting formula.
 * Histograms are tuples composed by buckets, which in turn store the
 * count of elements from the range of the counted parameterized random
 * variable.
 * 
 * @author Felipe Takiyama
 *
 * @param <T> The type of element in the range of the counted 
 * parameterized random variable. For now, it is String.
 */
public final class Histogram<T extends RangeElement> implements RangeElement {
	
	private LinkedHashMap<T, Integer> distribution;
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	/**
	 * Creates an empty histogram.
	 * @param prvRange A list with the elements of the range of some 
	 * parameterized random variable
	 */
	Histogram(List<T> prvRange) {
		distribution = new LinkedHashMap<T, Integer>(2 * prvRange.size());
		for (int i = 0; i < prvRange.size(); i++) {
			distribution.put(prvRange.get(i), Integer.valueOf(0));
		}
	}
	
	
	/**
	 * Creates a copy of the specified histogram.
	 * @param histogram The histogram to copy
	 */
	Histogram(Histogram<? extends T> histogram) {
		this.distribution = 
			new LinkedHashMap<T, Integer>(histogram.distribution);
	}
	
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	/**
	 * Returns the count of the specified bucket
	 * 
	 * @param rangeValue The key to the bucket
	 * @return The count of the specified bucket
	 */
	int getCount(T rangeValue) {
		return distribution.get(rangeValue);
	}
	
	
	/**
	 * Returns the number of buckets in this histogram, which equals to
	 * the size of range(f), where f is the parameterized random variable 
	 * being counted. 
	 * 
	 * @return The number of buckets in this histogram
	 */
	int size() {
		return distribution.size();
	}
	
	
	/**
	 * Returns <code>true</code> if this histogram contains the specified
	 * bucket.
	 * 
	 * @param bucket The bucket to search for
	 * @return <code>true</code> if this histogram contains the specified
	 * bucket, <code>false</code> otherwise
	 * @see #combine(RangeElement)
	 */
	private boolean contains(RangeElement bucket) {
		return distribution.containsKey(bucket);
	}
	
	
	/**
	 * Returns true if this histogram contains a bucket with the 
	 * specified count.
	 * 
	 * @param count A count to check
	 * @return true If this histogram contains a bucket with the 
	 * specified count, false otherwise.
	 */
	public boolean containsValue(int count) {
		return distribution.containsValue(Integer.valueOf(count));
	}
	
	
	/* ************************************************************************
	 *    Setters
	 * ************************************************************************/

	/**
	 * Adds the specified amount to the count of the specified range
	 * element.
	 * 
	 * @param rangeValue The key to the bucket 
	 * @param amount The amount to sum to the bucket
	 */
	void addCount(T rangeValue, int amount) {
		distribution.put(rangeValue, distribution.get(rangeValue) + amount);
	}
	
	
	/**
	 * Set the specified amount as the count for the specified bucket.
	 * 
	 * @param rangeValue The key to the bucket
	 * @param amount The amount to set into the bucket
	 */
	void setCount(T rangeValue, int amount) {
		distribution.put(rangeValue, amount);
	}
	
	
	/**
	 * Converts the values contained in each bucket to a {@link Multinomial}
	 * object.
	 * 
	 * @return This histogram converted to a Multinomial.
	 */
	Multinomial toMultinomial() {
		List<Integer> values = new ArrayList<Integer>(distribution.values());
		return Multinomial.getInstance(values);
	}
	
	
	/**
	 * Returns this histogram with the count of the specified bucket 
	 * incremented by 1.
	 * 
	 */
	public RangeElement combine(RangeElement e) {
		RangeElement result = null;
		if (contains(e)) {
			Histogram<RangeElement> copy = new Histogram<RangeElement>(this);
			copy.addCount(e, 1);
			result = copy;
		} else {
			throw new IllegalArgumentException();
		}
		return result;
	}
	
	
	/**
	 * Returns the result of applying the specified operator to the elements of
	 * the expanded histogram corresponding to this object.
	 * <p>
	 * The expanded histogram is a list obtained by adding to it each range 
	 * element
	 * in this histogram the number of times specified by its bucket. For
	 * instance, suppose that h = (#.false = 2, #.true = 3) is a histogram.
	 * Then the expanded histogram list is {false, false, true, true, true}.
	 * </p>
	 */
	@Override
	public RangeElement apply(Operator<? extends RangeElement> op) {
		List<T> expandedHistogram = new ArrayList<T>();
		for (T key : distribution.keySet()) {
			int count = getCount(key);
			expandedHistogram.addAll(Lists.listOf(key, count));
		}
		return apply(op, expandedHistogram);
	}
	
	
	/**
	 * Returns the result of applying the specified operator to the specified
	 * set of elements. This is a helper method.
	 * 
	 * @param <E> The type of element to which the operator applies
	 * @param op The operator to apply
	 * @param elements The set of elements where the operator will be applied
	 * @return The result of applying the specified operator to the specified
	 * set of elements
	 */
	private <E extends RangeElement> E apply(Operator<E> op, 
			Collection<T> elements) {
		Set<E> elems = new HashSet<E>((int) (elements.size() / 0.75));
		for (RangeElement e : elements) {
			elems.add(op.getTypeArgument().cast(e));
		}
		return op.applyOn(elems);
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	@Override
	public String toString() {
		StringBuilder histogram = new StringBuilder();
		histogram.append("( ");
		for (T key : distribution.keySet()) {
			histogram.append("#.").append(key).append("=")
					 .append(distribution.get(key)).append(", ");
		}
		histogram.deleteCharAt(histogram.lastIndexOf(","));
		histogram.append(")");
		return histogram.toString();
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Histogram<?>))
			return false;
		Histogram<?> targetObject = (Histogram<?>) other;
		return (this.distribution == null) 
					? (targetObject.distribution == null) 
					: this.distribution.equals(targetObject.distribution);
	}
	
	
	@Override
	public int hashCode() { 
		int result = 48 + this.distribution.hashCode(); // 48 = 17 + 31
		return result;
	}
}
