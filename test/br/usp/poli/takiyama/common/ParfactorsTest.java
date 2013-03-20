package br.usp.poli.takiyama.common;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static br.usp.poli.takiyama.acfove.VariablesToEliminate.buffer;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

/**
 * Unit tests for Parfactors.
 * @author ftakiyama
 *
 */
public class ParfactorsTest {
	
	private Pool objects;
	
	@Before
	public void setUp() {
		objects = new Pool();
		buffer.clear();
	}
	
	/* ************************************************************************
	 *   Unification Tests
	 * ***********************************************************************/
	
	@Test
	public void testUnificationGAPxSPonB() {
		
		objects.setGenAggUnificationTest();
		
		Parfactor g1 = objects.getGenAggParfactor("g.1.in.1");
		Parfactor g2 = objects.getGenAggParfactor("g.1.in.2");
		
		Set<Parfactor> result = Parfactors.unify(g1, g2);
		
		Set<Parfactor> answer = new HashSet<Parfactor>(3);
		answer.add(objects.getGenAggParfactor("g.1.out.1"));
		answer.add(objects.getGenAggParfactor("g.1.out.2"));
		answer.add(objects.getGenAggParfactor("g.1.out.3"));
		
		assertTrue(result.equals(answer)
				&& buffer.size() == 0);
	}
	
	@Test
	public void testUnificationGAPxSPonA() {
		
		objects.setGenAggUnificationTest();
		
		Parfactor g1 = objects.getGenAggParfactor("g.2.in.1");
		Parfactor g2 = objects.getGenAggParfactor("g.2.in.2");
		
		Set<Parfactor> result = Parfactors.unify(g1, g2);
		
		ParameterizedRandomVariable resultAuxChild = ParameterizedRandomVariable.getEmptyInstance();
		Iterator<ParameterizedRandomVariable> it = buffer.iterator();
		if (it.hasNext() && buffer.size() == 1) {
			resultAuxChild = it.next();
		}
		
		Set<Parfactor> answer = new HashSet<Parfactor>(3);
		answer.add(objects.getGenAggParfactor("g.2.out.1"));
		answer.add(objects.getGenAggParfactor("g.2.out.2"));
		answer.add(objects.getGenAggParfactor("g.2.out.3"));
		
		ParameterizedRandomVariable answerAuxChild = objects.getParameterizedRandomVariable("c'");
		
		assertTrue(result.equals(answer) 
				&& resultAuxChild.equals(answerAuxChild));
	}
	
	@Test
	public void testUnificationGAPxSPonAWithConstraints() {
		
		objects.setGenAggUnificationTest();
		
		Parfactor g1 = objects.getGenAggParfactor("g.3.in.1");
		Parfactor g2 = objects.getGenAggParfactor("g.3.in.2");
		
		Set<Parfactor> result = Parfactors.unify(g1, g2);
		
		ParameterizedRandomVariable resultAuxChild = ParameterizedRandomVariable.getEmptyInstance();
		Iterator<ParameterizedRandomVariable> it = buffer.iterator();
		if (it.hasNext() && buffer.size() == 1) {
			resultAuxChild = it.next();
		}
		
		Set<Parfactor> answer = new HashSet<Parfactor>(4);
		answer.add(objects.getGenAggParfactor("g.3.out.1"));
		answer.add(objects.getGenAggParfactor("g.3.out.2"));
		answer.add(objects.getGenAggParfactor("g.3.out.3"));
		answer.add(objects.getGenAggParfactor("g.3.out.4"));
		
		ParameterizedRandomVariable answerAuxChild = objects.getParameterizedRandomVariable("c'");
		
		assertTrue(result.equals(answer) 
				&& resultAuxChild.equals(answerAuxChild));
	}
	
	@Test
	public void testUnificationGAPxAP() {
		
		objects.setGenAggUnificationTest();
		
		Parfactor g1 = objects.getGenAggParfactor("g.4.in.1");
		Parfactor g2 = objects.getGenAggParfactor("g.4.in.2");
		
		Set<Parfactor> result = Parfactors.unify(g1, g2);
		
		Set<Parfactor> answer1 = new HashSet<Parfactor>(2);
		answer1.add(objects.getGenAggParfactor("g.4.out.1"));
		answer1.add(objects.getGenAggParfactor("g.4.out.2"));
		
		Set<Parfactor> answer2 = new HashSet<Parfactor>(2);
		answer1.add(objects.getGenAggParfactor("g.4.out.3"));
		answer1.add(objects.getGenAggParfactor("g.4.out.4"));
		
		assertTrue((result.equals(answer1) || result.equals(answer2))
				&& buffer.size() == 0);
	}
	
	@Test
	public void testUnificationGAPxAPWithContext() {
		
		objects.setGenAggUnificationTest();
		
		Parfactor g1 = objects.getGenAggParfactor("g.5.in.1");
		Parfactor g2 = objects.getGenAggParfactor("g.5.in.2");
		
		Set<Parfactor> result = Parfactors.unify(g1, g2);
		
		Set<Parfactor> answer1 = new HashSet<Parfactor>(2);
		answer1.add(objects.getGenAggParfactor("g.5.out.1"));
		answer1.add(objects.getGenAggParfactor("g.5.out.2"));
		
		Set<Parfactor> answer2 = new HashSet<Parfactor>(2);
		answer1.add(objects.getGenAggParfactor("g.5.out.3"));
		answer1.add(objects.getGenAggParfactor("g.5.out.4"));
		
		assertTrue(result.equals(answer1)
				|| result.equals(answer2));
	}
	
	@Test
	public void testUnificationGAPxGAP() {
		
		objects.setGenAggUnificationTest();
		
		Parfactor g1 = objects.getGenAggParfactor("g.6.in.1");
		Parfactor g2 = objects.getGenAggParfactor("g.6.in.2");
		
		Set<Parfactor> result = Parfactors.unify(g1, g2);
		
		Set<Parfactor> answer = new HashSet<Parfactor>(4);
		answer.add(objects.getGenAggParfactor("g.6.out.1"));
		answer.add(objects.getGenAggParfactor("g.6.out.2"));
		answer.add(objects.getGenAggParfactor("g.6.out.3"));
		
		assertTrue(result.equals(answer)
				&& buffer.size() == 0);
	}
	
	@Test
	public void testUnificationGAPxGAPWithConstraints() {
		
		objects.setGenAggUnificationTest();
		
		Parfactor g1 = objects.getGenAggParfactor("g.7.in.1");
		Parfactor g2 = objects.getGenAggParfactor("g.7.in.2");
		
		Set<Parfactor> result = Parfactors.unify(g1, g2);
		
		ParameterizedRandomVariable resultAuxChild = ParameterizedRandomVariable.getEmptyInstance();
		Iterator<ParameterizedRandomVariable> it = buffer.iterator();
		if (it.hasNext() && buffer.size() == 1) {
			resultAuxChild = it.next();
		}
		
		Set<Parfactor> answer = new HashSet<Parfactor>(5);
		answer.add(objects.getGenAggParfactor("g.7.out.1"));
		answer.add(objects.getGenAggParfactor("g.7.out.2"));
		answer.add(objects.getGenAggParfactor("g.7.out.3"));
		answer.add(objects.getGenAggParfactor("g.7.out.4"));
		answer.add(objects.getGenAggParfactor("g.7.out.5"));
		
		ParameterizedRandomVariable answerAuxChild = objects.getParameterizedRandomVariable("c'");
		
		assertTrue(result.equals(answer) 
				&& resultAuxChild.equals(answerAuxChild));
	}
}
