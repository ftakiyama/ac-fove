package br.usp.poli.takiyama.acfove;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.ParfactorI;
import br.usp.poli.takiyama.common.Pool;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

/**
 * Unit tests for Aggregation parfactors.
 * @author ftakiyama
 *
 */
public class GeneralizedAggregationParfactorTest {

	private Pool objects;
	
	@Before
	public void setUp() {
		objects = new Pool();
	}
	
	/**
	 * Example 3.9 of Kisysnki (2010)
	 */
	@Test
	public void testSimpleConversionToParfactor() {
		
		objects.setGenAggParfactorConversionTest(10);
		
		GeneralizedAggregationParfactor ag = objects.getGenAggParfactor("g1");
		
		List<ParfactorI> result = ag.convertToParfactor();
		
		List<ParfactorI> answer = new ArrayList<ParfactorI>(2);
		answer.add(objects.getSimpleParfactor("g3"));
		answer.add(objects.getSimpleParfactor("g2"));
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Converts aggregation parfactor
	 * &lang; { B&ne;x1 }, p(A,B), c(B), { v(C), u(D,E) }, Fpv, OR, { A&ne;x2 } &rang; 
	 * to simple parfactors.
	 * <br>
	 * <br>
	 * Result should be
	 * { &lang; { B&ne;x1, A&ne;x2 }, { p(A,B), v(C), u(D,E)}, Fpv &rang;,<br>      
	 *   &lang; { B&ne;x1 }, { #<sub>A:{A&ne;x2}</sub>[p(A,B)], c(B), v(C), u(D,E) } F# &rang; }.
	 */
	@Test
	public void testSimpleConversionToParfactorWithContextVariablesAndConstraints() {
		
		objects.setGenAggParfactorConversionTest(10);
		
		GeneralizedAggregationParfactor ag = objects.getGenAggParfactor("g4");
		
		List<ParfactorI> result = ag.convertToParfactor();
		
		List<ParfactorI> answer = new ArrayList<ParfactorI>(2);
		answer.add(objects.getSimpleParfactor("g6"));
		answer.add(objects.getSimpleParfactor("g5"));
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; &empty;, p(A,B), c(B), { v(A), u(B) }, 1, OR, &empty; &rang; 
	 * on substitution {B/x1}.
	 * <br>
	 * <br>
	 * Result should be
	 * J( { &lang; &empty;, p(A,x1), c(x1), { v(A), u(x1) }, 1, OR, &empty; &rang;,      
	 * 		&lang; {B &ne; x1}, p(A,B), c(B), { v(A), u(B) }, 1, OR, &empty; &rang; } ).
	 */
	@Test
	public void testSimpleSplit() {
		
		objects.setGenAggParfactorSplitTest();
		
		GeneralizedAggregationParfactor ag = objects.getGenAggParfactor("ag1");
		Binding substitution = objects.getBinding("B/1");
		List<ParfactorI> result = ag.split(substitution);
		
		List<ParfactorI> answer = objects.getParfactorList("m1");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; &empty;, p(A,B), c(B), { v(A), u(B) }, 1, OR, &empty; &rang; 
	 * on substitution {A/x1}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; &empty;, p(A,B), c'(B), { v(A), u(B) }, 1, OR, {A&ne;x1} &rang;,      
	 * 		&lang; &empty;, { p(x1,B), v(x1), u(B), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test
	public void testSplitOnExtraWithConstant() {
		
		objects.setGenAggParfactorSplitTest();

		GeneralizedAggregationParfactor ag = objects.getGenAggParfactor("ag1");
		Binding substitution = objects.getBinding("A/1");
		List<ParfactorI> result = ag.split(substitution);
		
		List<ParfactorI> answer = objects.getParfactorList("m2");  
				
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; &empty;, p(A,B), c(B), { v(A), u(B) }, 1, OR, &empty; &rang; 
	 * on substitution {A/B}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; &empty;, p(A,B), c'(B), { v(A), u(B) }, 1, OR, {A&ne;B} &rang;,      
	 * 		&lang; &empty;, { p(B,B), v(B), u(B), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test 
	public void testSplitOnExtraWithVariable() {
		
		objects.setGenAggParfactorSplitTest();

		GeneralizedAggregationParfactor ag = objects.getGenAggParfactor("ag1");
		Binding substitution = objects.getBinding("A/B");
		List<ParfactorI> result = ag.split(substitution);
		
		List<ParfactorI> answer = objects.getParfactorList("m3");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; &empty;, p(A,B), c(B), { v(A), u(B) }, 1, OR, &empty; &rang; 
	 * on substitution {B/A}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; &empty;, p(A,B), c'(B), { v(A), u(B) }, 1, OR, {A&ne;B} &rang;,      
	 * 		&lang; &empty;, { p(A,A), v(A), u(A), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test 
	public void testSplitWithExtra() {
		
		objects.setGenAggParfactorSplitTest();

		GeneralizedAggregationParfactor ag = objects.getGenAggParfactor("ag1");
		Binding substitution = objects.getBinding("B/A");
		List<ParfactorI> result = ag.split(substitution);
		
		List<ParfactorI> answer = objects.getParfactorList("m4");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; {B&ne;x2}, p(A,B), c(B), { v(A), u(B) }, 1, OR, {A&ne;x2} &rang; 
	 * on substitution {B/x1}.
	 * <br>
	 * <br>
	 * Result should be
	 * J( { &lang; &empty;, p(A,x1), c(x1), { v(A), u(x1) }, 1, OR, {A&ne;x2} &rang;,      
	 * 		&lang; {B&ne;x1,B&ne;x2}, p(A,B), c(B), { v(A), u(B) }, 1, OR, {A&ne;x2} &rang; } ).
	 */
	@Test
	public void testSimpleSplitConstrainedParfactor() {
		
		objects.setGenAggParfactorSplitTest();

		GeneralizedAggregationParfactor ag = objects.getGenAggParfactor("ag2");
		Binding substitution = objects.getBinding("B/1");
		List<ParfactorI> result = ag.split(substitution);
		
		List<ParfactorI> answer = objects.getParfactorList("m5");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; {B&ne;x2}, p(A,B), c(B), { v(A), u(B) }, 1, OR, {A&ne;x2} &rang; 
	 * on substitution {A/x1}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; {B&ne;x2}, p(A,B), c'(B), { v(A), u(B) }, 1, OR, {A&ne;x1,A&ne;x2} &rang;,      
	 * 		&lang; {B&ne;x2}, { p(x1,B), v(x1), u(B), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test
	public void testSplitConstrainedParfactorOnExtraWithConstant() {
		
		objects.setGenAggParfactorSplitTest();

		GeneralizedAggregationParfactor ag = objects.getGenAggParfactor("ag2");
		Binding substitution = objects.getBinding("A/1");
		List<ParfactorI> result = ag.split(substitution);
		
		List<ParfactorI> answer = objects.getParfactorList("m6");
		
		assertTrue(result.equals(answer));
		
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; {B&ne;x2}, p(A,B), c(B), { v(A), u(B) }, 1, OR, {A&ne;x2} &rang; 
	 * on substitution {A/B}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; {B&ne;x2}, p(A,B), c'(B), { v(A), u(B) }, 1, OR, {A&ne;B,A&ne;x2} &rang;,      
	 * 		&lang; {B&ne;x2}, { p(B,B), v(B), u(B), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test 
	public void testSplitConstrainedParfactorOnExtraWithVariable() {
		
		objects.setGenAggParfactorSplitTest();

		GeneralizedAggregationParfactor ag = objects.getGenAggParfactor("ag2");
		Binding substitution = objects.getBinding("A/B");
		List<ParfactorI> result = ag.split(substitution);
		
		List<ParfactorI> answer = objects.getParfactorList("m7");
		
		assertTrue(result.equals(answer));
		
	}
	
	/**
	 * Splits aggregation parfactor
	 * &lang; {B&ne;x2}, p(A,B), c(B), { v(A), u(B) }, 1, OR, {A&ne;x2} &rang; 
	 * on substitution {B/A}.
	 * <br>
	 * <br>
	 * Result should be
	 * &sum;<sub>ground(c')</sub> 
	 * J( { &lang; {B&ne;x2}, p(A,B), c'(B), { v(A), u(B) }, 1, OR, {A&ne;B,A&ne;x2} &rang;,      
	 * 		&lang; {A&ne;x2}, { p(A,A), v(A), u(A), c'(B), c(B) }, Fc &rang; } ).
	 */
	@Test 
	public void testSplitConstrainedParfactorWithExtra() {
		
		objects.setGenAggParfactorSplitTest();

		GeneralizedAggregationParfactor ag = objects.getGenAggParfactor("ag2");
		Binding substitution = objects.getBinding("B/A");
		List<ParfactorI> result = ag.split(substitution);
		
		List<ParfactorI> answer = objects.getParfactorList("m8");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Example 3.15 of Kisynski (2010).
	 * <br>
	 * Multiplies parfactors
	 * <br>
	 * g4 = &lang; &empty;, matched_6(Person), jackpot_won(), { big_jackpot() }, 1, OR, &empty; &rang;
	 * <br>
	 * g5 = &lang; &empty;, {big_jackpot(), matched_6(Person)}, Fmatched_6' &rang;
	 * <br>
	 * <br>
	 * The result in the calculation is parfactor
	 * &lang; &empty;, matched_6(Person), jackpot_won(), { big_jackpot() }, Fmatched_6', OR, &empty; &rang;
	 */
	@Test
	public void testTrivialMultiplication() {
		
		objects.setGenAggParfactorMultiplicationTest();
		
		ParfactorI g4 = objects.getGenAggParfactor("g4");
		ParfactorI g5 = objects.getSimpleParfactor("g5");
		ParfactorI result = g4.multiply(g5);
		
		ParfactorI answer = objects.getGenAggParfactor("g6");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Multiplies
	 * <br>
	 * g1 = &lang; {A&ne;x2,B&ne;x1}, {p(A,B), v(A), u(B)}, Fpv &rang;
	 * <br>
	 * with
	 * <br>
	 * gA = &lang; {B&ne;x1}, p(A,B), c(B), { v(A), u(B) }, Fpv, OR, {A&ne;x2} &rang;
	 * <br>
	 * <br>
	 * The result is
	 * gAr = &lang; {B&ne;x1}, p(A,B), c(B), { v(A), u(B) }, Fpv&otimes;Fpv, OR, {A&ne;x2} &rang;
	 */
	@Test
	public void testMultiplication() {

		objects.setGenAggParfactorMultiplicationTest();
		
		ParfactorI g1 = objects.getSimpleParfactor("g1");
		ParfactorI g2 = objects.getGenAggParfactor("ga");
		ParfactorI result = g2.multiply(g1);
		
		ParfactorI answer = objects.getGenAggParfactor("gar");

		assertTrue(result.equals(answer));
	}
	
	/**
	 * Tests multiplication reflexivity.
	 * <br>
	 * Given two parfactors g1 and g2, g1 &odot; g2 = g2 &odot; g1.
	 */
	@Test
	public void testMultiplicationReflexivity() {
		
		objects.setGenAggParfactorMultiplicationTest();
		
		ParfactorI g1 = objects.getSimpleParfactor("g1");
		ParfactorI g2 = objects.getGenAggParfactor("ga");
		ParfactorI result = g2.multiply(g1);
		ParfactorI sameResult = g1.multiply(g2); 
		
		assertTrue(result.equals(sameResult) 
				&& sameResult.equals(result));
	}
	
	/**
	 * Second part of Example 3.15 from Kisysnki (2010). 
	 * Sums out matched_6(Person) from the aggregation parfactor
	 * <br>
	 * &lang; &empty;, matched_6(Person), jackpot_won(Person), {big_jackpot()} Fmatched6', OR, &empty; &rang;
	 * <br>
	 * <br>
	 * The result is parfactor &lang; &empty;, {big_jackpot(), jackpot_won()}, Fjackpot_won &rang;, where
	 * F depends on |D(Person)|. The test is set for |D(Person)| = 5.
	 */
	@Test
	public void testSimpleSumOut() {
		
		objects.setGenAggParfactorSumOutTest();
		
		ParfactorI agg = objects.getGenAggParfactor("g6");
		ParameterizedRandomVariable prv = 
				objects.getParameterizedRandomVariable("matched_6");
		ParfactorI result = agg.sumOut(prv); 
		
		ParfactorI answer = objects.getSimpleParfactor("g7");
		
		assertTrue(result.equals(answer));
	}
	
}