package br.usp.poli.takiyama.sandbox;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import br.usp.poli.takiyama.acfove.FinalMultiplication;
import br.usp.poli.takiyama.acfove.MacroOperation;
import br.usp.poli.takiyama.acfove.Propositionalize;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.utils.TestUtils;


public class Temp2 {

	/**
	 * This is a multiplication test. Do i really need to exponentiate parfactors
	 * using the correction factor? Is it unecessary at some point?
	 * 
	 * Test results indicate that yes, this is necessary.
	 */
	@Test
	public void testInferenceOnExistsNode() {
		int n = 4;
		
		LogicalVariable x = StdLogicalVariable.getInstance("X", "x", n);

		Prv a = StdPrv.getBooleanInstance("a");
		Prv b = StdPrv.getBooleanInstance("b", x);
		
		List<BigDecimal> fa = TestUtils.toBigDecimalList(0.1, 0.9);
		List<BigDecimal> fb = TestUtils.toBigDecimalList(0.2, 0.8, 0.3, 0.7);

		Parfactor g1 = new StdParfactorBuilder().variables(a).values(fa).build();
		Parfactor g2 = new StdParfactorBuilder().variables(a, b).values(fb).build();
		
		Parfactor g3 = g1.multiply(g2);

		// Propositionalize product 
		Marginal m1 = new StdMarginalBuilder().parfactors(g3).build();
		MacroOperation propositionalizeM1 = new Propositionalize(m1, g3, x);
		m1 = propositionalizeM1.run();
		
		// Multiplies all parfactors
		MacroOperation productM1 = new FinalMultiplication(m1);
		m1 = productM1.run();
		
		// Propositionalize from the beginning
		Marginal m2 = new StdMarginalBuilder().parfactors(g1, g2).build();
		MacroOperation propositionalizeM2 = new Propositionalize(m2, g2, x);
		m2 = propositionalizeM2.run();
		
		// Multiplies all parfactors
		MacroOperation productM2 = new FinalMultiplication(m2);
		m2 = productM2.run();
		
		System.exit(0);
	}
}
