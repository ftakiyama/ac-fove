package br.usp.poli.takiyama.cfove;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.usp.poli.takiyama.cfove.MacroOperations;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.ParfactorI;
import br.usp.poli.takiyama.common.Pool;

/**
 * JUnit tests for macro operations.
 * 
 * @author ftakiyama
 *
 */
@Ignore("old version")
public class MacroOperationsTest {
	
	private Pool objects;
	
	@Before
	public void setUp() {
		objects = new Pool();
	}
	
	@Test
	public void shatter() {
		
		objects.setExample2_5_2_7forShattering();
		
		HashSet<ParfactorI> toShatter = new HashSet<ParfactorI>();
		toShatter.add(objects.getSimpleParfactor("g1"));
		toShatter.add(objects.getSimpleParfactor("g2"));
		toShatter.add(objects.getSimpleParfactor("g3"));
		toShatter.add(objects.getSimpleParfactor("g4"));
		
		Set<ParfactorI> result = MacroOperations.shatter(toShatter);
		
		HashSet<ParfactorI> answer = new HashSet<ParfactorI>();
		answer.add(objects.getSimpleParfactor("g1"));
		answer.add(objects.getSimpleParfactor("g4"));
		answer.add(objects.getSimpleParfactor("g5"));
		answer.add(objects.getSimpleParfactor("g6"));
		answer.add(objects.getSimpleParfactor("g7"));
		answer.add(objects.getSimpleParfactor("g8"));
		
		assertTrue(result.equals(answer));
	}
	
	@Test
	public void countConvert() {
		
		objects.setExample2_5_2_7forCountingConvert();
		
		HashSet<ParfactorI> toCount = new HashSet<ParfactorI>();
		toCount.add(objects.getSimpleParfactor("g1"));
		toCount.add(objects.getSimpleParfactor("g9"));
		toCount.add(objects.getSimpleParfactor("g11"));
		
		Set<ParfactorI> result = MacroOperations
			.countingConvert(
					toCount, 
					objects.getSimpleParfactor("g9"), 
					objects.getLogicalVariable("Lot"));
		
		HashSet<ParfactorI> answer = new HashSet<ParfactorI>();
		answer.add(objects.getSimpleParfactor("g1"));
		answer.add(objects.getSimpleParfactor("g11"));
		answer.add(objects.getSimpleParfactor("g12"));
		
		assertTrue(result.equals(answer));
	}
	
	@Test
	public void propositionalize() {
		
		objects.setPropositionalizationMacroTest();
		
		HashSet<ParfactorI> toPropositionalize = new HashSet<ParfactorI>();
		toPropositionalize.add(objects.getSimpleParfactor("g1"));
		toPropositionalize.add(objects.getSimpleParfactor("g2"));
		toPropositionalize.add(objects.getSimpleParfactor("g3"));
		toPropositionalize.add(objects.getSimpleParfactor("g4"));
		
		Set<ParfactorI> result = MacroOperations.propositionalize(
				toPropositionalize, 
				objects.getSimpleParfactor("g3"), 
				objects.getLogicalVariable("Lot"));
		
		HashSet<ParfactorI> answer = new HashSet<ParfactorI>();
		answer.add(objects.getSimpleParfactor("g1"));
		answer.add(objects.getSimpleParfactor("g4"));
		answer.add(objects.getSimpleParfactor("g2.0"));
		answer.add(objects.getSimpleParfactor("g2.1"));
		answer.add(objects.getSimpleParfactor("g2.2"));
		answer.add(objects.getSimpleParfactor("g3.0"));
		answer.add(objects.getSimpleParfactor("g3.1"));
		answer.add(objects.getSimpleParfactor("g3.2"));

		assertTrue(result.equals(answer));
	}
	
	@Test
	public void fullExpand() {
		
		objects.setFullExpandTest();
		
		HashSet<ParfactorI> toExpand = new HashSet<ParfactorI>();
		toExpand.add(objects.getSimpleParfactor("g1"));
		toExpand.add(objects.getSimpleParfactor("g2"));
		
		Set<ParfactorI> result = MacroOperations.fullExpand(
				toExpand,
				objects.getSimpleParfactor("g2"),
				objects.getCountingFormula("#.A[f]"));
		
		HashSet<ParfactorI> answer = new HashSet<ParfactorI>();
		answer.add(objects.getSimpleParfactor("g1.0"));
		answer.add(objects.getSimpleParfactor("g1.1"));
		answer.add(objects.getSimpleParfactor("g1.2"));
		answer.add(objects.getSimpleParfactor("g3"));

		assertTrue(result.equals(answer));
	}
	
	@Test
	public void globalSumOutWithConstraint() {
		
		objects.setGlobalSumOutTest();
		
		HashSet<Constraint> constraints =  new HashSet<Constraint>();
		constraints.add(objects.getConstraint("Lot != 1"));
		
		HashSet<ParfactorI> input = new HashSet<ParfactorI>();
		input.add(objects.getSimpleParfactor("g1"));
		input.add(objects.getSimpleParfactor("g4"));
		input.add(objects.getSimpleParfactor("g5"));
		input.add(objects.getSimpleParfactor("g6"));
		input.add(objects.getSimpleParfactor("g7"));
		input.add(objects.getSimpleParfactor("g8"));
		
		Set<ParfactorI> output = MacroOperations.globalSumOut(
				input,
				objects.getParameterizedRandomVariable("sprinkler"),
				constraints);
		
		Set<ParfactorI> answer = new HashSet<ParfactorI>();
		answer.add(objects.getSimpleParfactor("g1"));
		answer.add(objects.getSimpleParfactor("g4"));
		answer.add(objects.getSimpleParfactor("g5"));
		answer.add(objects.getSimpleParfactor("g7"));
		answer.add(objects.getSimpleParfactor("g9"));
		
		assertTrue(output.equals(answer));		
	}

	@Test
	public void globalSumOutNoConstraints() {
		
		objects.setGlobalSumOutTest();
		
		HashSet<Constraint> constraints =  new HashSet<Constraint>();
		
		HashSet<ParfactorI> input = new HashSet<ParfactorI>();
		input.add(objects.getSimpleParfactor("g1"));
		input.add(objects.getSimpleParfactor("g4"));
		input.add(objects.getSimpleParfactor("g5"));
		input.add(objects.getSimpleParfactor("g7"));
		input.add(objects.getSimpleParfactor("g9"));
		
		Set<ParfactorI> output = MacroOperations.globalSumOut(
				input,
				objects.getParameterizedRandomVariable("sprinkler[Lot/1]"),
				constraints);
		
		Set<ParfactorI> answer = new HashSet<ParfactorI>();
		answer.add(objects.getSimpleParfactor("g1"));
		answer.add(objects.getSimpleParfactor("g4"));
		answer.add(objects.getSimpleParfactor("g9"));
		answer.add(objects.getSimpleParfactor("g10"));
		
		assertTrue(output.equals(answer));
	}

	@Test
	public void globalSumOutThreeSimpleParfactors() {
		
		objects.setGlobalSumOutTest();
		
		HashSet<Constraint> constraints =  new HashSet<Constraint>();
		
		HashSet<ParfactorI> input = new HashSet<ParfactorI>();
		input.add(objects.getSimpleParfactor("g1"));
		input.add(objects.getSimpleParfactor("g11"));
		input.add(objects.getSimpleParfactor("g12"));
		
		Set<ParfactorI> output = MacroOperations.globalSumOut(
				input,
				objects.getParameterizedRandomVariable("rain"),
				constraints);
		
		Set<ParfactorI> answer = new HashSet<ParfactorI>();
		answer.add(objects.getSimpleParfactor("g13"));
		
		assertTrue(output.equals(answer));
	}
}

//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import br.usp.poli.takiyama.common.Constraint;
//import br.usp.poli.takiyama.prv.PRV;
//import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
//
//
//public class MacroOperationsTest {
//	
//	private HashMap<String, ParameterizedRandomVariable> variables;
//	private HashMap<String, ParameterizedFactor> factors;
//	private HashMap<String, SimpleParfactor> parfactors;
//	
//	
//	@Before
//	public void initialSetup() {
//		// Pool of PRVs
//		variables = new HashMap<String, ParameterizedRandomVariable>();
//		for (int i = 0; i < 3; i++) {
//			String name = "f" + i;
//			variables.put(name,
//						  PRV.getBooleanPrvWithOneParameter(name, i + 1));
//		}
//		for (int i = 0; i < 3; i++) {
//			String name = "g" + i;
//			variables.put(name,
//						  PRV.getBooleanPrvWithTwoParameters(name, i + 1, i + 2));
//		}
//		for (int i = 0; i < 3; i++) {
//			String name = "h" + i;
//			variables.put(name,
//						  PRV.getBooleanPrvWithThreeParameters(name, i + 1, i + 2, i + 3));
//		}
//		
//		// Logical variable for the wine ontology
////		String name = "wine";
////		variables.put(name, PRV.getBooleanPrvWithOneParameter(name, 10));
//		
//		// Pool of factors
//		factors = new HashMap<String, ParameterizedFactor>(); 
//		
//		String name = "factor1";
//		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
//		prvs.add(variables.get("f0"));
//		ArrayList<Number> mapping = new ArrayList<Number>();
//		mapping.add(0.1);
//		mapping.add(0.2);
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "factor2";
//		prvs.add(variables.get("g0"));
//		mapping.add(0.3);
//		mapping.add(0.4);
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "factor3";
//		prvs.add(variables.get("h0"));
//		mapping.add(0.5);
//		mapping.add(0.6);
//		mapping.add(0.7);
//		mapping.add(0.8);
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "factor4";
//		prvs.clear();
//		prvs.add(variables.get("f0"));
//		mapping.clear();
//		mapping.add(0.3);
//		mapping.add(0.4);
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "factor5";
//		prvs.clear();
//		prvs.add(variables.get("f2"));
//		mapping.clear();
//		mapping.add(0.3);
//		mapping.add(0.4);
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "empty_factor";
//		prvs.clear();
//		mapping.clear();
//		mapping.add(1);
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		// Pool of parfactors
//		parfactors = new HashMap<String, SimpleParfactor>(); 
//		parfactors.put("parfactor1", SimpleParfactor.getInstanceWithoutConstraints(factors.get("factor1")));
//		parfactors.put("parfactor2", SimpleParfactor.getInstanceWithoutConstraints(factors.get("factor2")));
//		parfactors.put("parfactor3", SimpleParfactor.getInstanceWithoutConstraints(factors.get("factor3")));
//		parfactors.put("parfactor4", SimpleParfactor.getInstanceWithoutConstraints(factors.get("factor4")));
//		parfactors.put("parfactor5", SimpleParfactor.getInstanceWithoutConstraints(factors.get("factor5")));
//		parfactors.put("empty_parfactor", SimpleParfactor.getInstanceWithoutConstraints(factors.get("empty_factor")));
//	}
//	
//	// Example 1 from ENIA'2012 paper
//	private void createPortWineOntology() {
//		
//		variables = new HashMap<String, ParameterizedRandomVariable>();
//		String[] nodes = {"port",
//						  "redWine",
//						  "wine",
//						  "hasWineColorRed",
//						  "hasWineBodyFull",
//						  "hasWineFlavourStrong",
//						  "hasWineSugarSweet",
//						  "locatedInPortugalRegion"};
//		for (String name : nodes) {
//			variables.put(name, PRV.getBooleanPrvWithOneParameter(name, 10));
//		}
//		
//		factors = new HashMap<String, ParameterizedFactor>(); 
//		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
//		ArrayList<Number> mapping = new ArrayList<Number>();
//		String name = "";
//		
//		name = "wine";
//		prvs.clear();
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("0.1"));
//		mapping.add(Double.valueOf("0.9"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "hasWineColorRed";
//		prvs.clear();
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("0.67"));
//		mapping.add(Double.valueOf("0.33"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//
//		name = "redWine";
//		prvs.clear();
//		prvs.add(variables.get("wine"));
//		prvs.add(variables.get("hasWineColorRed"));
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("1.0"));
//		mapping.add(Double.valueOf("0.0"));
//		mapping.add(Double.valueOf("1.0"));
//		mapping.add(Double.valueOf("0.0"));
//		mapping.add(Double.valueOf("1.0"));
//		mapping.add(Double.valueOf("0.0"));
//		mapping.add(Double.valueOf("0.0"));
//		mapping.add(Double.valueOf("1.0"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "hasWineBodyFull";
//		prvs.clear();
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("0.67"));
//		mapping.add(Double.valueOf("0.33"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "hasWineFlavourStrong";
//		prvs.clear();
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("0.67"));
//		mapping.add(Double.valueOf("0.33"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "hasWineSugarSweet";
//		prvs.clear();
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("0.67"));
//		mapping.add(Double.valueOf("0.33"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "locatedInPortugalRegion";
//		prvs.clear();
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("0.85"));
//		mapping.add(Double.valueOf("0.15"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = "port";
//		prvs.clear();
//		prvs.add(variables.get("redWine"));
//		prvs.add(variables.get("hasWineBodyFull"));
//		prvs.add(variables.get("hasWineFlavourStrong"));
//		prvs.add(variables.get("hasWineSugarSweet"));
//		prvs.add(variables.get("locatedInPortugalRegion"));
//		prvs.add(variables.get(name));
//		mapping.clear();
//		for (int i = 0; i < 31; i++) {
//			mapping.add(Double.valueOf("1.0"));
//			mapping.add(Double.valueOf("0.0"));
//		}
//		mapping.add(Double.valueOf("0.0"));
//		mapping.add(Double.valueOf("1.0"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		parfactors = new HashMap<String, SimpleParfactor>(); 
//		for (String node : nodes) {
//			parfactors.put(node, SimpleParfactor.getInstanceWithoutConstraints(factors.get(node)));
//		}
//	}
//	
//	
//	@Test
//	public void testBasicGlobalSumOut() {
//		System.out.println("\nTest: Basic GLOBAL-SUM-OUT");
//		
//		HashSet<SimpleParfactor> setOfParfactors = new HashSet<SimpleParfactor>();
//		setOfParfactors.add(parfactors.get("parfactor1"));
//		setOfParfactors.add(parfactors.get("parfactor2"));
//		
//		System.out.println("Before GLOBAL-SUM-OUT: \n" + setOfParfactors);
//		
//		System.out.println("After GLOBAL-SUM-OUT: \n" + MacroOperations
//				.globalSumOut(setOfParfactors, 
//							  variables.get("g0"), 
//							  new HashSet<Constraint>()));		
//	}
//	
//	@Test
//	public void testPortWineProblem() {
//		System.out.println("\nTest: Port Wine Problem");
//		
//		createPortWineOntology();
//		
//		HashSet<SimpleParfactor> setOfParfactors = new HashSet<SimpleParfactor>();
//		String[] nodes = {"wine",
//						  "hasWineColorRed",
//						  "redWine",
//						  "hasWineBodyFull",
//						  "hasWineFlavourStrong",
//						  "hasWineSugarSweet",
//						  "locatedInPortugalRegion",
//						  "port"};
//		for (String name : nodes) {
//			setOfParfactors.add(parfactors.get(name));
//		}
//		
//		System.out.println("Before GLOBAL-SUM-OUT: \n" + setOfParfactors);
//		
//		setOfParfactors = new HashSet<SimpleParfactor>(MacroOperations
//				.globalSumOut(setOfParfactors, 
//							  variables.get("wine"), 
//							  new HashSet<Constraint>()));
//		
//		System.out.println("After GLOBAL-SUM-OUT(wine(X)): \n" + setOfParfactors);
//
//		
//		setOfParfactors = new HashSet<SimpleParfactor>(MacroOperations
//				.globalSumOut(setOfParfactors, 
//							  variables.get("hasWineColorRed"), 
//							  new HashSet<Constraint>()));
//		
//		System.out.println("After GLOBAL-SUM-OUT(hasWineColorRed(X)): \n" + setOfParfactors);
//		
//
//		setOfParfactors = new HashSet<SimpleParfactor>(MacroOperations
//				.globalSumOut(setOfParfactors, 
//							  variables.get("hasWineBodyFull"), 
//							  new HashSet<Constraint>()));
//		
//		setOfParfactors = new HashSet<SimpleParfactor>(MacroOperations
//				.globalSumOut(setOfParfactors, 
//							  variables.get("hasWineFlavourStrong"), 
//							  new HashSet<Constraint>()));
//		
//		setOfParfactors = new HashSet<SimpleParfactor>(MacroOperations
//				.globalSumOut(setOfParfactors, 
//							  variables.get("hasWineSugarSweet"), 
//							  new HashSet<Constraint>()));
//		
//		setOfParfactors = new HashSet<SimpleParfactor>(MacroOperations
//				.globalSumOut(setOfParfactors, 
//							  variables.get("locatedInPortugalRegion"), 
//							  new HashSet<Constraint>()));
//		
//		setOfParfactors = new HashSet<SimpleParfactor>(MacroOperations
//				.globalSumOut(setOfParfactors, 
//							  variables.get("redWine"), 
//							  new HashSet<Constraint>()));
//		
//		System.out.println("After Everything: \n" + setOfParfactors);
//		
//	}
//}
