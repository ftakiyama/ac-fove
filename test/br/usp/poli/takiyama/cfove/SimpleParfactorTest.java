package br.usp.poli.takiyama.cfove;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Pool;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.Term;

/**
 * Unit tests for SimpleParfactors.
 * @author ftakiyama
 *
 */
public class SimpleParfactorTest {
	
	private Pool objects;
	
	@Before
	public void setUp() {
		objects = new Pool();
	}
	
	/**
	 * Example 2.15 from [Kisynski,2010]
	 */
	@Test
	public void split() {
		
		objects.setExample2_15();
		
		// Splits the parfactor on substitution {B/x1}
		Binding binding = Binding.create(objects.getLogicalVariable("B"), objects.getLogicalVariable("B").getPopulation().getIndividual(0));
		Set<Parfactor> result = objects.getSimpleParfactor("g1").split(binding);
		
		// Creates the correct answer
		Set<SimpleParfactor> answer = new HashSet<SimpleParfactor>();
		answer.add(objects.getSimpleParfactor("g1'"));
		answer.add(objects.getSimpleParfactor("g1[B/0]"));
		
		// Compares the result with the correct answer
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Example 2.16 from [Kisynski, 2010]
	 */
	@Test
	public void expand() {
		
		objects.setExample2_16();
		
		// Expands parfactor on individual x1
		CountingFormula countingFormula = objects.getCountingFormula("#.A:{A!=B}[f(A)]");
		Term x1 = objects.getLogicalVariable("A").getPopulation().getIndividual(1);
		Parfactor result = objects.getSimpleParfactor("g1").expand(countingFormula, x1);
		
		// Creates the correct answer
		Parfactor answer = objects.getSimpleParfactor("g1'");
		
		assertTrue(result.equals(answer));
	}
}
