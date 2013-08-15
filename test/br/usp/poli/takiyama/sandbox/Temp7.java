package br.usp.poli.takiyama.sandbox;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.AggregationParfactor;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdFactor;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.utils.Example;
import br.usp.poli.takiyama.utils.TestUtils;

/**
 * The exists quantifier network problem.
 * Runs lifted elimination step by step and compares with propositionalized
 * version.
 */
public class Temp7 {
	
	private Example network;
	private Example propositionalized;
	private int domainSize;
	
	@Before
	public void setup() {
		domainSize = 2;
		network = Example.existsNetwork(domainSize);
		propositionalized = Example.existsNetworkPropositionalized(domainSize);
	}
	
	@Test
	public void sumOutRole() {
		
		// Lifted sum out
		Parfactor sumOutR = sumOutRoleInLiftedManner();
		
		// Propositionalized sum out
		Map<String, Factor> results = sumOutRoleInPropositionalizedManner();

		System.out.print("Test finished");
	}
	
	private String getName(String prvName, int i, int j) {
		return prvName + " ( x" + i + " y" + j + " )";
	}
	
	private String getName(String prvName, String x, int i) {
		return prvName + " ( " + x + i + " )";
	}
	
	// make a propositionalization method?
	
	private Parfactor sumOutRoleInLiftedManner() {

		Prv r = network.prv("r ( X Y )");

		Parfactor gr = network.parfactor("gr");
		Parfactor gand = network.parfactor("gand");

		return gr.multiply(gand).sumOut(r);		
	}
	
	private Map<String, Factor> sumOutRoleInPropositionalizedManner() {
		
		Map<String, Factor> results = new HashMap<String, Factor>();
		for (int x = 0; x < domainSize; x++) {
			for (int y = 0; y < domainSize; y++) {
				String role = getName("r", x, y);
				Prv rolePrv = propositionalized.prv(role);
				String and = getName("and", x, y);
				Factor roleFactor = propositionalized.factor(role);
				Factor andFactor = propositionalized.factor(and);
				Factor result = roleFactor.multiply(andFactor).sumOut(rolePrv);
				results.put(and, result);
			}
		}
		return results;
	}
	
	@Test
	public void sumOutAnd() {
		
		// Lifted sum out
		Parfactor sumOutAnd = sumOutAndInLiftedManner();
		
		// Propositionalized sum out
		Map<String, Factor> propositionalizedSumOut = sumOutAndInPropositionalizedManner();

		System.out.print("Test finished");
	}
	
	private Parfactor sumOutAndInLiftedManner() {
		
		LogicalVariable y = network.lv("Y");
		
		Prv and = network.prv("and ( X Y )");
		
		Parfactor sumOutR = sumOutRoleInLiftedManner();
		Parfactor gexists = network.parfactor("gexists");
		
		return  sumOutR.multiply(gexists).sumOut(and).count(y);
	}
	
	private Map<String, Factor> sumOutAndInPropositionalizedManner() {
		
		Map<String, Factor> sumOutR = sumOutRoleInPropositionalizedManner();
		Map<String, Factor> results = new HashMap<String, Factor>();
		
		for (int x = 0; x < domainSize; x++) {
			
			// gets factor on exists(X)
			String exists = getName("exists", "x", x);
			Factor result = propositionalized.factor(exists);
			
			// for each factor on and(X,Y), multiplies it by exists(X) and sum it out
			for (int y = 0; y < domainSize; y++) {
				String and = getName("and", x, y);
				Prv andPrv = propositionalized.prv(and);
				Factor andFactor = sumOutR.get(and);
				result = result.multiply(andFactor).sumOut(andPrv);
			}
			results.put(exists, result);
		}
		
		return results;
	}
	
	@Test
	public void sumOutB() {
		// Lifted sum out
		Parfactor sumOutB = sumOutBInLiftedManner();
		
		// Propositionalized sum out
		Map<String, Factor> propositionalizedSumOut = sumOutBInPropositionalizedManner();
		
		Set<Factor> all = propositionalized.allFactors();

		System.out.println("Test finished");
	}
	
	private Parfactor sumOutBInLiftedManner() {
		
		LogicalVariable x = network.lv("X");
		
		Prv b = network.prv("b ( Y )");
		
		Parfactor sumOutAnd = sumOutAndInLiftedManner();
		Parfactor gb = network.parfactor("gb");
		
		return  sumOutAnd.multiply(gb).count(x).sumOut(b);
	}
	
	private Map<String, Factor> sumOutBInPropositionalizedManner() {
		
		Map<String, Factor> sumOutAnd = sumOutAndInPropositionalizedManner();
		Map<String, Factor> results = new HashMap<String, Factor>();
		
		// Multiplies all remaining factors
		Factor product = StdFactor.getInstance();
		
		// Multiplies all factors on exists(X)
		for (int x = 0; x < domainSize; x++) {
			String exists = getName("exists", "x", x);
			Factor existsFactor = sumOutAnd.get(exists);
			product = product.multiply(existsFactor);
		}
		
		// Multiplies all factors on b(Y)
		for (int y = 0; y < domainSize; y++) {
			String b = getName("b", "y", y);
			Factor bFactor = propositionalized.factor(b);
			product = product.multiply(bFactor);
		}
		
		// Sums out b(Y)
		Factor result = product;
		for (int y = 0; y < domainSize; y++) {
			String b = getName("b", "y", y);
			Prv bPrv = propositionalized.prv(b);
			result = result.sumOut(bPrv);
		}
		
		results.put("result", result);
		
		result = StdFactor.getInstance();
		for (Factor f : propositionalized.allFactors()) {
			result = result.multiply(f);
		}
		for (Prv v : propositionalized.allPrvs()) {
			if (!v.name().startsWith("exists")) {
				result = result.sumOut(v);
			}
		}
		results.put("anohter", result);
		
		return results;
	}
	
	@Test
	public void testAggregationConversion() {
		AggregationParfactor ap = (AggregationParfactor) network.parfactor("gexists");
		System.out.println("Check it out!");
		System.out.println(ap.toStdParfactors());
	}
	
	@Test
	public void existsNetworkStepByStep() {
		
		Example network = Example.existsNetwork(2);
		
		LogicalVariable x = network.lv("X");

		Prv b = network.prv("b ( Y )");
		Prv r = network.prv("r ( X Y )");
		Prv and = network.prv("and ( X Y )");
		
		Parfactor gb = network.parfactor("gb");
		Parfactor gr = network.parfactor("gr");
		Parfactor gand = network.parfactor("gand");
		Parfactor gexists = network.parfactor("gexists");
		
		Parfactor sumOutR = gr.multiply(gand).sumOut(r);
		Parfactor sumOutAnd = gexists.multiply(sumOutR).sumOut(and);
		Parfactor countX = sumOutAnd.count(x);
		Parfactor sumOutB = countX.multiply(gb).sumOut(b);
		
		Parfactor expected = network.expected();
		
		assertEquals(expected, sumOutB);
	}

}
