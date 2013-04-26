package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
public class Histogram<T extends RangeElement> implements RangeElement {
	
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
		int result = 17;
		result = 31 + result + this.distribution.hashCode();
		return result;
	}
}
