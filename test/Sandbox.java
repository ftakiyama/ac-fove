

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Parfactors;
import br.usp.poli.takiyama.common.Pool;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariableNameGenerator;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

public class Sandbox {
	
	private void getHistogram(List<String> H, int maxCount, ArrayList<Integer> h, int currentIndex) {
		if (currentIndex == h.size() - 1 || maxCount == 0) {
			h.set(currentIndex, maxCount);
			H.add(h.toString());
			return;
		}
		int count = maxCount;
		while (count >= 0) {
			h.set(currentIndex, count);
			getHistogram(H, maxCount-count, h, currentIndex+1);
			count--;
		}
		
	}
	
	@Test
	public void testHistogramGeneration() {
		ArrayList<String> H = new ArrayList<String>();
		ArrayList<Integer> h = new ArrayList<Integer>(10);
		h.add(0);
		h.add(0);
		h.add(0);
		getHistogram(H, 3, h, 0);
		System.out.println(H);
	}
	
	private class SubSandbox {
		int a;
		SubSandbox(int a) {
			this.a = a;
		}
		
		void set(int a) {
			this.a = a;
		}
		
		@Override
		public String toString() {
			return "sub " + a;
		}
	}
	
	@Test
	public void testObjectReference() {
		SubSandbox s1 = new SubSandbox(1);
		SubSandbox s2 = s1;
		
		System.out.println(s1);
		s2.set(3);
		System.out.println(s2);		
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
	public int combination(int n, int k) throws IllegalArgumentException {
		int r = 1;
		
		if (n < 0 || k < 0) {
			throw new IllegalArgumentException("Cannot calculate combination" 
					+ " for negative numbers.");
		}
		
		if (n == 0) return 0;
		//if (k == 0 && n >= 0) return 1;
		
		for (int i = 1; i <= k; i++) {
			r = r * (n - k + i) / i;
		}
		return r;
	}
	
	@Test
	public void testCombination() {
		for (int n = 0; n < 11; n++) {
			for (int k = 0; k < 11; k++) {
				System.out.println("C(" + n + "," + k + ") = " + combination(n,k));		
			}
		}
	}
	
	@Test
	public void testArrayString() {
		Pool p = new Pool();
		p.setExample2_5_2_7(5);
		for (int i = 1; i <= 13; i++)
			System.out.println(p.getSimpleParfactor("g" + i));
	}
	
}
