

import java.util.ArrayList;
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
}
