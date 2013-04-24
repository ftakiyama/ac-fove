package br.usp.poli.takiyama.prv;

import static org.junit.Assert.*;

import org.junit.Test;


public class PrvsTest {
	
	/**
	 * Example 2.20 from Kisynski (2010).
	 * <p>
	 * Finds the MGU for f(X1,X2) and f(x1,X4), which is {X1/x1, X2/X4}.
	 * </p>
	 */
	@Test
	public void testMgu() {
		LogicalVariable x1 = StdLogicalVariable.getInstance("X1", "x", 10);
		LogicalVariable x2 = StdLogicalVariable.getInstance("X2", "x", 10);
		LogicalVariable x4 = StdLogicalVariable.getInstance("X4", "x", 10);
		
		Constant c1 = x1.population().individualAt(1);
		
		Prv f1 = StdPrv.getBooleanInstance("f", x1, x2);
		Prv f2 = StdPrv.getBooleanInstance("f", c1, x4);
		
		Substitution result = Prvs.mgu(f1, f2);
		
		Binding x1_1 = Binding.getInstance(x1, x1.population().individualAt(1));
		Binding x2_x4 = Binding.getInstance(x2, x4);
		
		Substitution answer = Substitution.getInstance(x1_1, x2_x4);
		
		assertTrue(result.equals(answer));
	}
}
