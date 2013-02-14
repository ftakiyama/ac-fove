package br.usp.poli.takiyama.common;

/**
 * Math utilities not found in java.lang.Math.
 * <br>
 * I am not worried with efficiency, for now.
 * 
 * @author ftakiyama
 *
 */
public class MathUtils {

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
	public static int combination(int n, int k) throws IllegalArgumentException {
		if (n < 0 || k < 0) {
			throw new IllegalArgumentException("Cannot calculate combination" 
					+ " for negative numbers.");
		}
		if (n == 0) return 0;
		
		int r = 1;
		for (int i = 1; i <= k; i++) {
			r = r * (n - k + i) / i;
		}
		return r;
	}
}
