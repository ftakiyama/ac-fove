package br.usp.poli.takiyama.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Math utilities not found in java.lang.Math.
 * <br>
 * I am not worried with efficiency, for now.
 * 
 * @author ftakiyama
 *
 */
public final class MathUtils {

	private MathUtils() { 
		// avoids instantiation
	}
	
	/**
	 * @deprecated Because it cannot calculate factorial of numbers greater 
	 * than 20
	 * 
	 * @param n
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static long factorial(int n) throws IllegalArgumentException {
		long n_factorial = 1;
		
		if (n == 1) return n_factorial;
		if (n < 0) throw new IllegalArgumentException("Cannot calculate factorial of negative numbers!");
		if (n > 20) throw new IllegalArgumentException("I can calculate up to 20!.");
		
		for (int i = 2; i <= n; i++) {
			n_factorial *= i;
		}
		return n_factorial;
	}
	
	/**
	 * Calculates the binomial coefficient C(n,k).
	 * <br>
	 * This method returns 0 if n = 0 and k > 0. It throws an
	 * IllegalArgumentException if any specified argument is negative.
	 * 
	 * @param n A nonnegative integer
	 * @param k A nonnegative integer
	 * @return The binomial coefficient C(n,k).
	 * @throws IllegalArgumentException If the specified arguments are
	 * negative (at least one of them)
	 */
	public static BigInteger combination(int n, int k) 
			throws IllegalArgumentException {
		
		if (n < 0 || k < 0) {
			throw new IllegalArgumentException("Cannot calculate combination" 
					+ " for negative numbers.");
		}
		if (n == 0) {
			return BigInteger.ZERO;
		}
		
		BigInteger r = BigInteger.ONE;
		int nMinusK = n - k;
		for (int i = 1; i <= k; i++) {
			r = r.multiply(BigInteger.valueOf(nMinusK + i))
				 .divide(BigInteger.valueOf(i));
		}
		return r;
	}
		
	
	public static class Multinomial  {
		private final List<Integer> multi;
		
		private Multinomial(List<? extends Integer> m) {
			multi = new ArrayList<Integer>(m);
		}
		
		public static Multinomial getInstance(List<? extends Integer> m) {
			return new Multinomial(m);
		}
		
		public int size() {
			return multi.size();
		}
				
		int get(int index) {
			return multi.get(index);
		}
		
		/**
		 * Subtracts 1 from the number at the specified position.
		 * @param index The position to decrement
		 * @return This tuple with the specified position decremented
		 */
		Multinomial decrement(int index) {
			List<Integer> decremented = new ArrayList<Integer>(multi);
			decremented.set(index, decremented.get(index) - 1);
			return Multinomial.getInstance(decremented);
		}
		
		/**
		 * Returns <code>true</code> if this Multinomial only has positive
		 * values, <code>false</code> otherwise.
		 * @return <code>true</code> if this Multinomial only has positive
		 * values, <code>false</code> otherwise.
		 */
		boolean isValid() {
			for (Integer i : multi) {
				if (i.compareTo(Integer.valueOf(0)) < 0) {
					return false;
				}
			}
			return true;
		}
		
		/**
		 * Returns <code>true</code> if this Multinomial only has zero
		 * values, <code>false</code> otherwise.
		 * @return <code>true</code> if this Multinomial only has zero
		 * values, <code>false</code> otherwise.
		 */
		boolean isZeroed() {
			for (Integer i : multi) {
				if (i.compareTo(Integer.valueOf(0)) != 0) {
					return false;
				}
			}
			return true;
		}
		
		/**
		 * Sums all terms in the specified interval. The interval is closed,
		 * that is, the sum includes the indexes specified for the interval.
		 * @param fromIndex First index to sum
		 * @param toIndex Last index to sum
		 * @return The sum of all terms in the specified interval
		 */
		int sumTerms(int fromIndex, int toIndex) {
			int result = 0;
			for (int i = fromIndex; i <= toIndex; i++) {
				result = result + multi.get(i);
			}
			return result;
		}
		
		@Override
		public String toString() {
			return multi.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((multi == null) ? 0 : multi.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Multinomial)) {
				return false;
			}
			Multinomial other = (Multinomial) obj;
			if (multi == null) {
				if (other.multi != null) {
					return false;
				}
			} else if (!multi.equals(other.multi)) {
				return false;
			}
			return true;
		}
	}
	
	/*
	 * Cache for multinomial calculation. All calculated multinomials are 
	 * cached in order to improve algorithm's efficiency.
	 */
	private static final Map<Multinomial, BigInteger> cache = 
			new HashMap<Multinomial, BigInteger>();
	
	/**
	 * @deprecated
	 * Calculates the 
	 * <a href="https://en.wikipedia.org/wiki/Multinomial_coefficient#
	 * Multinomial_coefficients">multinomial coefficient.</a>
	 * <p>
	 * This function is recursive, so results are cached in order to avoid
	 * intensive computation of repetitive results. Unfortunately, some
	 * simple calculations may throw a {@link StackOverflowError} due to
	 * recursivity piling up too many calls to this function. 
	 * </p>
	 * <p>
	 * The algorithm was inspired on Dave Barber's approach to 
	 * <a href="http://home.comcast.net/~tamivox/dave/multinomial/index.html">
	 * calculate multinomials</a>.
	 * </p>
	 * <p>
	 * 17/04/2013<br>
	 * This method is not efficient and subject to stack overflow. Prefer 
	 * {@link #multinomial(Multinomial)} to calculate multinomials. 
	 * </p>
	 * 
	 * @param m The multinomial to calculate
	 * @return The value of the specified multinomial
	 */
	static BigInteger calculate(Multinomial m) {
		BigInteger result = BigInteger.ZERO;
		if (m.size() == 0 || m.isZeroed()) {
			result = BigInteger.ONE;
		} else if (!m.isValid()) {
			// returns 0 when invalid
		} else if (cache.containsKey(m)) {
			System.out.println("Retrieving value from cache for : " + m);
			result = cache.get(m);
		} else {
			for (int i = 0; i < m.size(); i++) {
				result = result.add(multinomial(m.decrement(i)));
			}
			cache.put(m, result);
		}
		return result;
	}
	
	/**
	 * Calculates the 
	 * <a href="https://en.wikipedia.org/wiki/Multinomial_coefficient#
	 * Multinomial_coefficients">multinomial coefficient.</a>
	 * 
	 * @param m The multinomial to calculate
	 * @return The value of the specified multinomial
	 */
	public static BigInteger multinomial(Multinomial m) {
		BigInteger result = BigInteger.ONE;
		if (m.size() < 2 || m.isZeroed()) {
			result = BigInteger.ONE;
		} else {
			for (int i = 1; i < m.size(); i++) {
				int n = m.sumTerms(0, i);
				int k = m.get(i);
				BigInteger c = combination(n, k);
				result = result.multiply(c);
			}
		}
		return result;
	}
	
	
	/**
	 * Returns the result of <code>b<sup>p/q</sup></code> as a 
	 * {@link BigDecimal}.
	 * <p>
	 * This method is not defined for negative numbers, so <code>b</code>
	 * and <code>p/q</code> must be positive numbers. It throws a 
	 * {@link IllegalArgumentException} when the exponent is negative or
	 * the base is negative.
	 * </p>
	 * <p>
	 * Exceptional cases:
	 * <li> 0<sup>n</sup> = 0 for n > 0
	 * <li> 0<sup>0</sup> = 1
	 * </p>
	 * <br>
	 * 
	 * @param b The base
	 * @param p The numerator of the exponent
	 * @param q The denominator of the exponent
	 * @return the result of <code>b<sup>p/q</sup></code>
	 * @throws IllegalArgumentException if the exponet is negative or
	 * the base is negative.
	 */
	public static BigDecimal pow(BigDecimal b, int p, int q) 
			throws IllegalArgumentException {
		
		BigDecimal result;
		int sign = p * q; 
		if (b.signum() == 0) {
			if (sign > 0) {
				result = BigDecimal.ZERO;
			} else if (sign == 0) {
				result = BigDecimal.ONE;
			} else {
				throw new IllegalArgumentException("0^n, n < 0 is undefined!");
			}
		} else if (b.signum() < 0 || sign < 0) {
			throw new IllegalArgumentException("Operation not defined for"
					+ " negative numbers.");
		} else {
			// separates p/q in two parts: whole (i) and decimal (d)
			int intPart = p / q;
			double decPart = ((double) p) / q - intPart;
			
			// calculates b^i using BigDecimal.pow()
			BigDecimal intPow = b.pow(intPart);
			
			// calculates b^d using Math.pow()
			double bAsDouble = b.doubleValue();
			BigDecimal decPow = new BigDecimal(Math.pow(bAsDouble, decPart));
			
			result = intPow.multiply(decPow);
		}
		return result;
	}
}
