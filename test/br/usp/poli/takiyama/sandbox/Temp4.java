package br.usp.poli.takiyama.sandbox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.usp.poli.takiyama.acfove.AggParfactor.AggParfactorBuilder;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Or;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.utils.TestUtils;


public class Temp4 {

	/**
	 * Exists node
	 * - with context variables,
	 * - not multiplying factor on b(Y) before eliminating and(X,Y)
	 */
	@Test
	public void testExistsNodeManually() {
		
		int domainSize = 2;
		
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", domainSize);
		LogicalVariable y = StdLogicalVariable.getInstance("Y", "y", domainSize);
		
		Prv b = StdPrv.getBooleanInstance("b", y);
		Prv r = StdPrv.getBooleanInstance("r", x, y);
		Prv a = StdPrv.getBooleanInstance("and", x, y);
		Prv e = StdPrv.getBooleanInstance("exists", x);
		Prv ex = CountingFormula.getInstance(x, e);
		
		List<BigDecimal> fb = TestUtils.toBigDecimalList(0.1, 0.9);
		List<BigDecimal> fr = TestUtils.toBigDecimalList(0.2, 0.8);
		List<BigDecimal> fand = TestUtils.toBigDecimalList(1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0);
		
		Parfactor g1 = new StdParfactorBuilder().variables(b).values(fb).build();
		Parfactor g2 = new StdParfactorBuilder().variables(r).values(fr).build();
		Parfactor g3 = new StdParfactorBuilder().variables(r, b, a).values(fand).build();
		Parfactor g4 = new AggParfactorBuilder(a, e, Or.OR).context(b).build();
					
		Parfactor g5 = g2.multiply(g3).sumOut(r);
		Parfactor g6 = g5.multiply(g4).sumOut(a);
		Parfactor g7 = g6.multiply(g1);
		Parfactor g8 = g7.count(x);
		Parfactor g9 = g8.sumOut(b);
		
		List<BigDecimal> fexp = new ArrayList<BigDecimal>();
		BigDecimal vFalse = BigDecimal.valueOf(0.28).pow(domainSize);
		BigDecimal vTrue = BigDecimal.ONE.subtract(vFalse);
		for (int n = domainSize; n >= 0; n--) {
			fexp.add(vFalse.pow(n).multiply(vTrue.pow(domainSize - n)));
		}
		Parfactor expected = new StdParfactorBuilder().variables(ex).values(fexp).build();
		
		System.out.println("Welcome to this incredible test!");		
	}
}
