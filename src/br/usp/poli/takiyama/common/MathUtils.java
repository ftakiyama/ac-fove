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
}
