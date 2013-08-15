package br.usp.poli.takiyama.sandbox;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.utils.Example;


/**
 * Big Jackpot example.
 * Tries to sum out big_jackpot() before eliminating matched_6(Person). 
 * 
 * The result is slightly different. How to know if it is due to imprecision?
 */
public class Temp11 {
	private Example network;
	private int domainSize;
	
	@Before
	public void setup() {
		domainSize = 4;
		network = Example.bigJackpotNetworkNoContext(domainSize);
	}
	
	@Test
	public void inferExists() {
		
		Parfactor result = inferExistsInLiftedManner();
		
		assertEquals(network.expected(), result);
	}
	
	private Parfactor inferExistsInLiftedManner() {
		Prv big_jackpot = network.prv("big_jackpot ( )");
		Prv played = network.prv("played ( Person )");
		Prv matched_6 = network.prv("matched_6 ( Person )");
		
		Parfactor gbigjackpot = network.parfactor("gbigjackpot");
		Parfactor gplayed = network.parfactor("gplayed");
		Parfactor gmatched6 = network.parfactor("gmatched6");
		Parfactor gjackpotwon = network.parfactor("gjackpotwon");
		
		Parfactor sumOutPlayed = gplayed.multiply(gmatched6).sumOut(played);
		Factor sumOutBJ = sumOutPlayed.factor().multiply(gbigjackpot.factor()).sumOut(big_jackpot);
		Parfactor sumOutBigJackpot = new StdParfactorBuilder().factor(sumOutBJ).build();
		Parfactor sumOutMatched6 = sumOutBigJackpot.multiply(gjackpotwon).sumOut(matched_6);
		
		return sumOutMatched6;
	}
}
