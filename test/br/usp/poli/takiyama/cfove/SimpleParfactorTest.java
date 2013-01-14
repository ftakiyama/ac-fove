package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.HashSet;
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
		List<Parfactor> result = objects.getSimpleParfactor("g1").split(binding);
		
		// Creates the correct answer
		List<SimpleParfactor> answer = new ArrayList<SimpleParfactor>();
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
	
	/**
	 * Example 2.18 from Kisynski, 2010.
	 */
	@Test
	public void AC3Algorithm() {
		
		objects.setExample2_18();
		
		// Replaces every logical variable constrained to a single constant
		// with this constant
		Parfactor result = objects.getSimpleParfactor("g1").replaceLogicalVariablesConstrainedToSingleConstant();
		
		// Creates the correct answer
		Parfactor answer = objects.getSimpleParfactor("g2");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Example 2.19 from Kisynski, 2010.
	 * I've changed it slightly to make implementation easier.
	 * Instead of constraint Z != x1, I'm doing Z != y1. I've also replaced
	 * PRV f(x1,Z) with f(X,Z).
	 */
	@Test
	public void renameLogicalVariables() {
		
		objects.setExample2_19();
		
		// Rename the logical variables in each parfactor to avoid name conflicts
		Parfactor result1 = objects.getSimpleParfactor("g1").renameLogicalVariables();
		Parfactor result2 = objects.getSimpleParfactor("g2").renameLogicalVariables();
		
		// Correct answer
		Parfactor answer1 = objects.getSimpleParfactor("g1'");
		Parfactor answer2 = objects.getSimpleParfactor("g2'");
		
		assertTrue(result1.equals(answer1) && result2.equals(answer2));
	}
	
	/**
	 * Example 2.20 from Kisynski, 2010.
	 * This is actually the second part of the example. The first part is
	 * on ParameterizedRandomVariableTest.
	 */
	@Test
	public void checkMguAgainstConstraints() {
		
		objects.setExample2_20();
		
		Substitution mgu = objects
			.getParameterizedRandomVariable("f")
			.getMgu(objects
					.getParameterizedRandomVariable("f[X1/1, X2/X4]"));
		HashSet<Constraint> constraints = new HashSet<Constraint>();
		constraints.add(objects.getConstraint("X1 != 1"));
		constraints.add(objects.getConstraint("X3 != X4"));
		constraints.add(objects.getConstraint("X4 != 0"));
		
		assertTrue(Parfactors.checkMguAgainstConstraints(mgu, constraints));
	
	}
	
	/**
	 * Example 2.21 from Kisynski (2010).
	 * I've changed it slightly to make it more consistent.
	 */
	@Test
	public void splitOnMgu() {
		
		objects.setExample2_21();
		
		ArrayList<Parfactor> parfactorsToSplit = new ArrayList<Parfactor>();
		
		parfactorsToSplit.add(objects.getSimpleParfactor("g3"));
		parfactorsToSplit.add(objects.getSimpleParfactor("g4"));
		
		HashSet<Parfactor> splitParfactors = new HashSet<Parfactor>();
		Substitution mgu = objects.getSubstitution("mgu");
		
		for (Parfactor parfactor : parfactorsToSplit) {
			splitParfactors.addAll((ArrayList<Parfactor>) parfactor.splitOnMgu(mgu)); 
		}
		
		HashSet<SimpleParfactor> answer = new HashSet<SimpleParfactor>();
		answer.add(objects.getSimpleParfactor("g4"));
		answer.add(objects.getSimpleParfactor("g5"));
		answer.add(objects.getSimpleParfactor("g6"));
		
		assertTrue(splitParfactors.equals(answer));
		
	}
	
	/**
	 * Example 2.22 from Kisynski (2010).
	 * I've changed it slightly to make it more consistent.
	 * <br>
	 * Instead of X4 &ne; x1, I'm using X4 &ne; y1.
	 * <br>
	 * Instead of f(x1,X4), I'm using f(y1,X4).
	 * <br>
	 * These changes also imply including inequality X1 &ne; y1 in parfactor
	 * [6], but since the inequality will always be true, I've taken it out
	 * of the parfactor. In the original version there is inequality X1 &ne; x1,
	 * which is not always true, thus necessary.
	 */
	@Test
	public void splitOnConstraints() {
		
		objects.setExample2_22();
		
		ArrayList<Parfactor> parfactorsToSplit = new ArrayList<Parfactor>();
		
		parfactorsToSplit.add(objects.getSimpleParfactor("g4"));
		parfactorsToSplit.add(objects.getSimpleParfactor("g5"));
		parfactorsToSplit.add(objects.getSimpleParfactor("g6"));
		
		HashSet<Parfactor> splitParfactors = new HashSet<Parfactor>();
		
		splitParfactors.addAll((ArrayList<Parfactor>) objects.getSimpleParfactor("g5").splitOnConstraints(objects.getSimpleParfactor("g4").getConstraints()));
		splitParfactors.add(objects.getSimpleParfactor("g4"));
		splitParfactors.add(objects.getSimpleParfactor("g6"));
		
		HashSet<SimpleParfactor> answer = new HashSet<SimpleParfactor>();
		answer.add(objects.getSimpleParfactor("g4"));
		answer.add(objects.getSimpleParfactor("g6"));
		answer.add(objects.getSimpleParfactor("g7"));
		answer.add(objects.getSimpleParfactor("g8"));
		
		assertTrue(splitParfactors.equals(answer));
		
	}
	
	/**
	 * Example 2.19 through 2.22 from Kisynski (2010).
	 * I've changed it slightly to make it more consistent.
	 * <br>
	 * Instead of X4 &ne; x1, I'm using X4 &ne; y1.
	 * <br>
	 * Instead of X3 &ne; X4, I'm using X3 &ne; x1.
	 * <br>
	 * Instead of D(X) = {y1,...,ym} in parfactor [2], I'm using 
	 * D(X) = {x1,....,xn}, since parameterized random variables are
	 * typed to their logical variables and having X with two different
	 * populations would make no sense.
	 */
	@Test
	public void unify() {
		
		objects.setExample2_19To2_22();
		
		LogicalVariableNameGenerator.reset();
		
		HashSet<Parfactor> splitParfactors = new HashSet<Parfactor>();
		splitParfactors.addAll(objects.getSimpleParfactor("g1").unify(objects.getSimpleParfactor("g2")));
		
		HashSet<SimpleParfactor> answer = new HashSet<SimpleParfactor>();
		answer.add(objects.getSimpleParfactor("g3"));
		answer.add(objects.getSimpleParfactor("g4"));
		answer.add(objects.getSimpleParfactor("g5"));
		answer.add(objects.getSimpleParfactor("g6"));
		
		assertTrue(splitParfactors.equals(answer));
		
	}
	
	@Test
	public void countOnSimpleParfactorWithoutConstraints() {
		
		objects.setCountingTestWithoutConstraints();
		
		Parfactor result = objects.getSimpleParfactor("g1").count(objects.getLogicalVariable("A"));
		Parfactor answer = objects.getSimpleParfactor("g2");
		
		assertTrue(result.equals(answer));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testExceptionOnCouting() {
		objects.setCountingTestWithoutConstraints();
		objects.getSimpleParfactor("g1").count(objects.getLogicalVariable("B"));
	}
	
	@Test
	public void countOnSimpleParfactorWithConstraints() {
		
		objects.setCountingTestWithConstraint();
		
		Parfactor result = objects.getSimpleParfactor("g1").count(objects.getLogicalVariable("A"));
		Parfactor answer = objects.getSimpleParfactor("g2");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Example 2.17 from Kisynski, 2010 modified. In this test, there are no
	 * constraints in the parfactor.
	 */
	@Test
	public void countOnParfactorWithoutConstraintsAndTwoVariables() {
		
		objects.setExample2_17WithoutConstraints();
		
		Parfactor result = objects.getSimpleParfactor("g1").count(objects.getLogicalVariable("A"));
		Parfactor answer = objects.getSimpleParfactor("g2");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Example 2.17 from Kisynski, 2010.
	 */
	@Test
	public void countLogicalVariable() {
		
		objects.setExample2_17();
		
		Parfactor result = objects.getSimpleParfactor("g1").count(objects.getLogicalVariable("A"));
		Parfactor answer = objects.getSimpleParfactor("g2");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Propositionalization test derived from example 2.15 of Kisynski (2010).
	 * The parfactor is the same, and the answer is obtained by 
	 * propositionalizing it on logical variable A.
	 */
	@Test
	public void propositionalize() {
		
		objects.setPropositionalizationTest();
		
		Set<Parfactor> result = objects.getSimpleParfactor("g").propositionalize(objects.getLogicalVariable("A"));
		
		Set<Parfactor> answer = new HashSet<Parfactor>();
		answer.add(objects.getSimpleParfactor("g1"));
		answer.add(objects.getSimpleParfactor("g2"));
		answer.add(objects.getSimpleParfactor("g3"));
		
		assertTrue(result.equals(answer));
	}
	
	@Test
	public void sumOutCountingFormulaWithCardinality1() {
		
		objects.setSumOutCountingFormulaWithCardinality1Test();
		
		Parfactor result = objects.getSimpleParfactor("g").sumOut(objects.getCountingFormula("#.A[f]"));
		Parfactor answer = objects.getSimpleParfactor("g_answer");
		
		assertTrue(result.equals(answer));
	}
	
	@Test
	public void sumOutCountingFormulaWithCardinality2() {
		
		objects.setSumOutCountingFormulaWithCardinality2Test();
		
		Parfactor result = objects.getSimpleParfactor("g").sumOut(objects.getCountingFormula("#.A[f]"));
		Parfactor answer = objects.getSimpleParfactor("g_answer");
		
		assertTrue(result.equals(answer));
	}

	@Test
	public void sumOutCountingFormulaWithCardinality10() {
		
		objects.setSumOutCountingFormulaWithCardinality10Test();
		
		Parfactor result = objects.getSimpleParfactor("g").sumOut(objects.getCountingFormula("#.A[f]"));
		Parfactor answer = objects.getSimpleParfactor("g_answer");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Example 2.13 from Kisynski (2010). 
	 * The objective of this test is to verify the elimination of counting
	 * formulas. I took out parfactor [2] because I'm not checking
	 * conditions yet.
	 */
	@Test
	public void sumOutCountingFormulaWithConstraint() {
		
		objects.setExample2_13();
		
		Parfactor result = objects.getSimpleParfactor("g").sumOut(objects.getCountingFormula("#.A[f]"));
		Parfactor answer = objects.getSimpleParfactor("g_answer");
		
		assertTrue(result.equals(answer));
	}
}
