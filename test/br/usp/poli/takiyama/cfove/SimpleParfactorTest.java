package br.usp.poli.takiyama.cfove;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Pool;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.prv.Binding;

/**
 * Unit tests for Parfactors.
 * @author ftakiyama
 *
 */
public class SimpleParfactorTest {
	
	private Pool objects;
	
	@Before
	public void setUp() {
		objects = new Pool();
		objects.populatePool();
	}
	
	
	// TODO: make this work!
	// Example 2.15 from [Kisynski,2010]
	@Test
	public void split() {
		
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
	
}
