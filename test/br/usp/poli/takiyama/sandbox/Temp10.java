package br.usp.poli.takiyama.sandbox;

import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.utils.Example;

/**
 * Exists network example.
 * Checks whether using r(X,Y) as a context variable corrects the result.
 * 
 * >> Nope.
 *
 */
public class Temp10 {
	
	private Example network;
	private int domainSize;
	
	@Before
	public void setup() {
		domainSize = 2;
		network = Example.existsNetworkWithMultipleContext(domainSize);
	}
	
	@Test
	public void inferExists() {
		
		Parfactor result = inferExistsInLiftedManner();
		
		assertNotEquals(network.expected(), result);
	}
	
	private Parfactor inferExistsInLiftedManner() {
		Prv b = network.prv("b ( Y )");
		Prv r = network.prv("r ( X Y )");
		Prv and = network.prv("and ( X Y )");
		
		Parfactor gb = network.parfactor("gb");
		Parfactor gr = network.parfactor("gr");
		Parfactor gand = network.parfactor("gand");
		Parfactor gexists = network.parfactor("gexists");
		
		Parfactor product = gb.multiply(gr).multiply(gand).multiply(gexists);
		Parfactor sumOutAnd = product.sumOut(and);
		Parfactor sumOutR = sumOutAnd.sumOut(r);
		Parfactor sumOutB = sumOutR.sumOut(b);
		
		return sumOutB;
	}
}
