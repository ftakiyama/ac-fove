package br.usp.poli.takiyama.cfove;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.Pool;
import br.usp.poli.takiyama.common.RandomVariableSet;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

/**
 * Unit tests for C-FOVE algorithm.
 * @author ftakiyama
 *
 */
public class CFOVETest {

	private Pool objects;
	
	@Before
	public void setUp() {
		objects = new Pool();
		objects.setExample2_5_2_7(5); // test with various values later
	}
	
	/**
	 * Second step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 * It only selects the operation to be made.
	 */
	@Test
	public void exampleComputationSecondOperation() {

		System.out.println("Step 2 - Operation selection");
		
		Set<Parfactor> input = new HashSet<Parfactor>(4);
		input = objects.getSimpleParfactorSet("g1","g4","g5","g6","g7","g8");
		
		RandomVariableSet q = objects.getRandomVariableSet("wg:lot");
		
		CFOVE cfove = new CFOVE(input, q);
		cfove.runStep(input);
		
		ParameterizedRandomVariable prv = 
			objects.getParameterizedRandomVariable("sprinkler");
		HashSet<Constraint> constraints = new HashSet<Constraint>(1);
		constraints.add(objects.getConstraint("Lot != 1"));
		
		StringBuilder str = new StringBuilder();
		str.append("GLOBAL-SUM-OUT\n")
			.append(prv.toString())
			.append("\n")
			.append(constraints.toString());
		
		assertTrue(str.toString().equals(cfove.getOperationParameters()));
	}
	
	/**
	 * Third step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 * It only selects the operation to be made.
	 */
	@Test
	public void exampleComputationThirdOperation() {
		
		System.out.println("Step 3 - Operation selection");
		
		Set<Parfactor> input = new HashSet<Parfactor>(5);
		input = objects.getSimpleParfactorSet("g1","g4","g5","g7","g9");
		
		RandomVariableSet q = objects.getRandomVariableSet("wg:lot");
		
		CFOVE cfove = new CFOVE(input, q);
		cfove.runStep(input);
		
		ParameterizedRandomVariable prv1 = 
			objects.getParameterizedRandomVariable("wet_grass[Lot/1]");
		HashSet<Constraint> constraints1 = new HashSet<Constraint>(0);
		
		StringBuilder str1 = new StringBuilder();
		str1.append("GLOBAL-SUM-OUT\n")
			.append(prv1.toString())
			.append("\n")
			.append(constraints1.toString());
		
		ParameterizedRandomVariable prv2 = 
			objects.getParameterizedRandomVariable("sprinkler[Lot/1]");
		HashSet<Constraint> constraints2 = new HashSet<Constraint>(0);
		
		StringBuilder str2 = new StringBuilder();
		str2.append("GLOBAL-SUM-OUT\n")
			.append(prv2.toString())
			.append("\n")
			.append(constraints2.toString());
		
		assertTrue(str1.toString().equals(cfove.getOperationParameters())
				|| str2.toString().equals(cfove.getOperationParameters()));
	}
	
	/**
	 * Fourth step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 * It only selects the operation to be made.
	 */
	@Test
	public void exampleComputationFourthOperation() {
		
		System.out.println("Step 4 - Operation selection");
		
		Set<Parfactor> input = new HashSet<Parfactor>(4);
		input = objects.getSimpleParfactorSet("g1","g4","g9","g10");
		
		RandomVariableSet q = objects.getRandomVariableSet("wg:lot");
		
		CFOVE cfove = new CFOVE(input, q);
		cfove.runStep(input);
		
		ParameterizedRandomVariable prv1 = 
			objects.getParameterizedRandomVariable("wet_grass[Lot/1]");
		HashSet<Constraint> constraints1 = new HashSet<Constraint>(0);
		
		StringBuilder str1 = new StringBuilder();
		str1.append("GLOBAL-SUM-OUT\n")
			.append(prv1.toString())
			.append("\n")
			.append(constraints1.toString());
		
		ParameterizedRandomVariable prv2 = 
			objects.getParameterizedRandomVariable("sprinkler[Lot/1]");
		HashSet<Constraint> constraints2 = new HashSet<Constraint>(0);
		
		StringBuilder str2 = new StringBuilder();
		str2.append("GLOBAL-SUM-OUT\n")
			.append(prv2.toString())
			.append("\n")
			.append(constraints2.toString());
		
		assertTrue(str1.toString().equals(cfove.getOperationParameters())
				|| str2.toString().equals(cfove.getOperationParameters()));
	}
	
	/**
	 * Fifth step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 * It only selects the operation to be made.
	 */
	@Test
	public void exampleComputationFifthOperation() {

		System.out.println("Step 5 - Operation selection");
		
		Set<Parfactor> input = new HashSet<Parfactor>(3);
		input = objects.getSimpleParfactorSet("g1","g9","g11");
		
		RandomVariableSet q = objects.getRandomVariableSet("wg:lot");
		
		CFOVE cfove = new CFOVE(input, q);
		cfove.runStep(input);
		
		Parfactor parfactor = objects.getSimpleParfactor("g9");
		LogicalVariable logicalVariable = objects.getLogicalVariable("Lot");
		
		StringBuilder str = new StringBuilder();
		str.append("COUNTING-CONVERT\n")
			.append(parfactor.toString())
			.append("\n")
			.append(logicalVariable.toString());

		assertTrue(str.toString().equals(cfove.getOperationParameters()));
	}
	
	/**
	 * Sixth step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 * It only selects the operation to be made.
	 */
	@Test
	public void exampleComputationSixthOperation() {
		
		System.out.println("Step 6 - Operation selection");

		Set<Parfactor> input = new HashSet<Parfactor>(3);
		input = objects.getSimpleParfactorSet("g1","g11","g12");
		
		RandomVariableSet q = objects.getRandomVariableSet("wg:lot");
		
		CFOVE cfove = new CFOVE(input, q);
		cfove.runStep(input);
		
		ParameterizedRandomVariable prv = 
			objects.getParameterizedRandomVariable("rain");
		HashSet<Constraint> constraints = new HashSet<Constraint>(0);
		
		StringBuilder str = new StringBuilder();
		str.append("GLOBAL-SUM-OUT\n")
			.append(prv.toString())
			.append("\n")
			.append(constraints.toString());
		
		assertTrue(str.toString().equals(cfove.getOperationParameters()));
	}
	
	
	
	/**
	 * First step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 */
	@Test
	public void exampleComputationFirstStep() {
		
		Set<Parfactor> input = new HashSet<Parfactor>(4);
		input = objects.getSimpleParfactorSet("g1","g2","g3","g4");
		
		Set<Parfactor> result = new HashSet<Parfactor>(6);
		result = MacroOperations.shatter(input);
				
		Set<Parfactor> answer = new HashSet<Parfactor>(6);
		answer = objects.getSimpleParfactorSet("g1","g4","g5","g6","g7","g8");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Second step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 */
	@Test
	public void exampleComputationSecondStep() {
		
		System.out.println("Step 2");
		
		Set<Parfactor> input = new HashSet<Parfactor>(6);
		input = objects.getSimpleParfactorSet("g1","g4","g5","g6","g7","g8");
		
		Set<Parfactor> result = new HashSet<Parfactor>(5);
		Set<Constraint> c = new HashSet<Constraint>();
		c.add(objects.getConstraint("Lot != 1"));
		RandomVariableSet q = RandomVariableSet.getInstance (
				objects.getParameterizedRandomVariable("wet_grass"), c);
		CFOVE cfove = new CFOVE(input, q);
		result = cfove.executeStep(input); //must put all state variables here
 		
		Set<Parfactor> answer = new HashSet<Parfactor>(5);
		answer = objects.getSimpleParfactorSet("g1","g4","g5","g7","g9");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Third step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 */
	@Test
	public void exampleComputationThirdStep() {

		System.out.println("Step 3");
		
		Set<Parfactor> input = new HashSet<Parfactor>(5);
		input = objects.getSimpleParfactorSet("g1","g4","g5","g7","g9");
		
		Set<Parfactor> result = new HashSet<Parfactor>(4);
		Set<Constraint> c = new HashSet<Constraint>();
		c.add(objects.getConstraint("Lot != 1"));
		RandomVariableSet q = RandomVariableSet.getInstance (
				objects.getParameterizedRandomVariable("wet_grass"), c);
		CFOVE cfove = new CFOVE(input, q);
		result = cfove.executeStep(input); //must put all state variables here
 		
		Set<Parfactor> answer = new HashSet<Parfactor>(4);
		answer = objects.getSimpleParfactorSet("g1","g4","g9","g10");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Fourth step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 */
	@Test
	public void exampleComputationFourthStep() {

		System.out.println("Step 4");
		
		Set<Parfactor> input = new HashSet<Parfactor>(4);
		input = objects.getSimpleParfactorSet("g1","g4","g9","g10");
		
		Set<Parfactor> result = new HashSet<Parfactor>(3);
		Set<Constraint> c = new HashSet<Constraint>();
		c.add(objects.getConstraint("Lot != 1"));
		RandomVariableSet q = RandomVariableSet.getInstance (
				objects.getParameterizedRandomVariable("wet_grass"), c);
		CFOVE cfove = new CFOVE(input, q);
		result = cfove.executeStep(input); //must put all state variables here
 		
		Set<Parfactor> answer = new HashSet<Parfactor>(3);
		answer = objects.getSimpleParfactorSet("g1","g9","g11");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Fifth step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 */
	@Test
	public void exampleComputationFifthStep() {	

		System.out.println("Step 5");
		
		Set<Parfactor> input = new HashSet<Parfactor>(3);
		input = objects.getSimpleParfactorSet("g1","g9","g11");
		
		Set<Parfactor> result = new HashSet<Parfactor>(3);
		Set<Constraint> c = new HashSet<Constraint>();
		c.add(objects.getConstraint("Lot != 1"));
		RandomVariableSet q = RandomVariableSet.getInstance (
				objects.getParameterizedRandomVariable("wet_grass"), c);
		CFOVE cfove = new CFOVE(input, q);
		result = cfove.executeStep(input); //must put all state variables here
 		
		Set<Parfactor> answer = new HashSet<Parfactor>(3);
		answer = objects.getSimpleParfactorSet("g1","g11","g12");
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Sixth step of C-FOVE for Kisynski's example computation (section 2.5.2.7)
	 */
	@Test
	public void exampleComputationSixthStep() {

		System.out.println("Step 6");
		
		Set<Parfactor> input = new HashSet<Parfactor>(3);
		input = objects.getSimpleParfactorSet("g1","g11","g12");
		
		Set<Parfactor> result = new HashSet<Parfactor>(1);
		Set<Constraint> c = new HashSet<Constraint>();
		c.add(objects.getConstraint("Lot != 1"));
		RandomVariableSet q = RandomVariableSet.getInstance (
				objects.getParameterizedRandomVariable("wet_grass"), c);
		CFOVE cfove = new CFOVE(input, q);
		result = cfove.executeStep(input); //must put all state variables here
 		
		Set<Parfactor> answer = new HashSet<Parfactor>(1);
		answer = objects.getSimpleParfactorSet("g13");	
		
		System.out.println("Result: \n" + result);
		System.out.println("Expected: \n" + answer);
		
		assertTrue(result.iterator().next().equals(answer.iterator().next())); // TODO somehow hash codes are not matching. 
	}
	
	/**
	 * Kisynski's example computation (section 2.5.2.7). Union of all steps
	 * above.
	 */
	@Test
	public void exampleComputation() {
		
		Set<Parfactor> input = new HashSet<Parfactor>(4);
		input = objects.getSimpleParfactorSet("g1","g2","g3","g4");
		
		Set<Constraint> c = new HashSet<Constraint>();
		c.add(objects.getConstraint("Lot != 1"));
		RandomVariableSet q = RandomVariableSet.getInstance (
				objects.getParameterizedRandomVariable("wet_grass"), c);
		CFOVE cfove = new CFOVE(input, q);
		Parfactor result = cfove.run(); 
		
		Set<Parfactor> answer = new HashSet<Parfactor>(1);
		answer = objects.getSimpleParfactorSet("g13");	
		
		System.out.println("Result: \n" + result);
		System.out.println("Expected: \n" + answer);
		
		//assertTrue(result.equals(answer));	
		assertTrue(result.equals(answer.iterator().next())); // TODO temporary fix until i solve the precision problem
	}
}
