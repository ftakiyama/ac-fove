

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.acfove.operator.And;
import br.usp.poli.takiyama.common.Parfactors;
import br.usp.poli.takiyama.common.Pool;
import br.usp.poli.takiyama.common.ParfactorI;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.OldCountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariableNameGenerator;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.MathUtils;


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
	
	public void testCombination() {
		for (int n = 0; n < 11; n++) {
			for (int k = 0; k < 11; k++) {
				System.out.println("C(" + n + "," + k + ") = " + combination(n,k));		
			}
		}
	}
	

	public void testArrayString() {
		Pool p = new Pool();
		p.setExample2_5_2_7(5);
		for (int i = 1; i <= 13; i++)
			System.out.println(p.getSimpleParfactor("g" + i));
	}
	
	public void testArrayListReference() {
		ArrayList<StringBuilder> a = new ArrayList<StringBuilder>();
		StringBuilder s = new StringBuilder("1");
		a.add(s);
		s.append(" 2");
		a.add(s);
		System.out.println(s);
	}
	
	
	public void testConversionToBase2() {
		for (int n = 3; n < 1000000; n++) {
			double log = Math.log(n) / Math.log(2);
			int m = ((int) log) + 1;
			String bin = Integer.toBinaryString(n);
			
			System.out.format("%d %d %s\n", n, m, bin);
			
			if (m != bin.length()) {
				System.out.println("Meh: " + n);	
				break;
			}
		}
	}
	
	public void testFillArray() {
		int populationSize = 3;
		double [] f1 = new double[8 * populationSize];
		Arrays.fill(f1, 0, 4, 1);
		Arrays.fill(f1, 4, 8, 0);
		for (int i = 1; i < populationSize; i++) {
			Arrays.fill(f1, 8 * i, 8 * i + 4, 0);
			Arrays.fill(f1, 8 * i + 4, 8 * (i + 1), 1);
		}
		System.out.print(Arrays.toString(f1));
	}
	
	public interface Marginal<E extends ParameterizedRandomVariable> {
		
	}
	
	public class MarginalDecorator<E extends ParameterizedRandomVariable> {
		Marginal<E> m;
	}
	
	private interface Operator {
		
	}
	
	private interface GenOperator<E extends Object> extends Operator {
		
	}
	
	private class OR implements GenOperator<Boolean> {
		 
	}
	
	public void testTypes() {
		Operator op = new OR();
	}
	
	public void testGenerics() {
		HashMap<String, Number> m1 = new HashMap<String, Number>();
		HashMap<String, Double> m2 = new HashMap<String, Double>();
		
		Number d = new Double(1.0);
		System.out.println(d instanceof Double);
		System.out.println(d instanceof Number);
		
	}
	
	public void testRecursion() {
		inc(0);
	}
	
	private int inc(int i) {
		System.out.println(i);
		return inc(++i);
	}
	
	@Test
	public void testPow() {
		for (int i = 0; i < 100; i++) {
			int p = 1;
			int q = 2;
			BigDecimal iBig = new BigDecimal(i);
			System.out.println(iBig + " => " + MathUtils.pow(iBig, p, q));
		}
	}
}
