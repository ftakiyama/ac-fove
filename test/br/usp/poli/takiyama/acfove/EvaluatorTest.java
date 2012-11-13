//package br.usp.poli.takiyama.acfove;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//
//import org.junit.Test;
//
//import br.usp.poli.takiyama.cfove.ParameterizedFactor;
//import br.usp.poli.takiyama.cfove.SimpleParfactor;
//import br.usp.poli.takiyama.cfove.SimpleParfactor;
//import br.usp.poli.takiyama.common.Constraint;
//import br.usp.poli.takiyama.prv.PRV;
//import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
//
//public class EvaluatorTest {
//	
//	private HashMap<String, ParameterizedRandomVariable> variables;
//	private HashMap<String, ParameterizedFactor> factors;
//	private HashSet<SimpleParfactor> parfactors;
//	
//	private void createKangarooOntology() {
//		String[] nodeNames = {"human", "hasChild", "c", "exists_h.H", "parent"};
//		
//		variables = new HashMap<String, ParameterizedRandomVariable>();
//		variables.put(nodeNames[0], PRV.getBooleanPrvWithTwoParameters(nodeNames[0], 3, 3)); // I have changed this because the example is wrong
//		variables.put(nodeNames[1], PRV.getBooleanPrvWithTwoParameters(nodeNames[1], 3, 3));
//		variables.put(nodeNames[2], PRV.getBooleanPrvWithTwoParameters(nodeNames[2], 3, 3));
//		variables.put(nodeNames[3], PRV.getBooleanPrvWithOneParameter(nodeNames[3], 3));
//		variables.put(nodeNames[4], PRV.getBooleanPrvWithOneParameter(nodeNames[4], 3));
//		
//		factors = new HashMap<String, ParameterizedFactor>(); 
//		
//		ArrayList<ParameterizedRandomVariable> prvs = new ArrayList<ParameterizedRandomVariable>();
//		ArrayList<Number> mapping = new ArrayList<Number>();
//		String name = "";
//		
//		name = nodeNames[0];
//		prvs.clear();
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("0.46"));
//		mapping.add(Double.valueOf("0.54"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = nodeNames[1];
//		prvs.clear();
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("0.70"));
//		mapping.add(Double.valueOf("0.30"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = nodeNames[2];
//		prvs.clear();
//		prvs.add(variables.get("human"));
//		prvs.add(variables.get("hasChild"));
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("1"));
//		mapping.add(Double.valueOf("0"));
//		mapping.add(Double.valueOf("1"));
//		mapping.add(Double.valueOf("0"));
//		mapping.add(Double.valueOf("1"));
//		mapping.add(Double.valueOf("0"));
//		mapping.add(Double.valueOf("0"));
//		mapping.add(Double.valueOf("1"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		name = nodeNames[3]; 
//		prvs.clear();
//		prvs.add(variables.get("c"));
//		mapping.clear();
//		mapping.add(Double.valueOf("1"));
//		mapping.add(Double.valueOf("1"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping)); // constant factor for agg parfactor
//		
//		name = nodeNames[4];
//		prvs.clear();
//		prvs.add(variables.get("exists_h.H"));
//		prvs.add(variables.get(name));
//		mapping.clear();
//		mapping.add(Double.valueOf("1"));
//		mapping.add(Double.valueOf("0"));
//		mapping.add(Double.valueOf("0"));
//		mapping.add(Double.valueOf("1"));
//		factors.put(name, ParameterizedFactor.getInstance(name, prvs, mapping));
//		
//		parfactors = new HashSet<SimpleParfactor>();
//		parfactors.add(SimpleParfactor.getInstanceWithoutConstraints(factors.get(nodeNames[0])));
//		parfactors.add(SimpleParfactor.getInstanceWithoutConstraints(factors.get(nodeNames[1])));
//		parfactors.add(SimpleParfactor.getInstanceWithoutConstraints(factors.get(nodeNames[2])));
//		parfactors.add(AggregationParfactor.getInstanceWithoutConstraints(variables.get("c"), variables.get("exists_h.H"), factors.get(nodeNames[3]), Or.getInstance()));
//		parfactors.add(SimpleParfactor.getInstanceWithoutConstraints(factors.get(nodeNames[4])));
//		
//		//ACFOVE algorithm = new ACFOVE(parfactors, variables.get("parent"));
//		
//		//System.out.println(algorithm.execute());
//	}
//	
//	@Test
//	public void testSumOutChoice() {
//		createKangarooOntology();
//		
//		Evaluator evaluator = new Evaluator();
//		HashSet<Constraint> constraints = new HashSet<Constraint>();
//		ParameterizedRandomVariable prv; 
//		
//		prv = variables.get("human");
//		evaluator.checkCostOfGlobalSumOut(parfactors, prv, constraints);
//		System.out.println(evaluator.toString());
//		this.parfactors = new HashSet<SimpleParfactor>(evaluator.executeCurrentOperation());
//		//System.out.println(this.parfactors.toString());
//		
//		prv = variables.get("hasChild");
//		evaluator.checkCostOfGlobalSumOut(parfactors, prv, constraints);
//		System.out.println(evaluator.toString());
//		this.parfactors = new HashSet<SimpleParfactor>(evaluator.executeCurrentOperation());
//		//System.out.println(this.parfactors.toString());
//		
//		prv = variables.get("c");
//		evaluator.checkCostOfGlobalSumOut(parfactors, prv, constraints);
//		System.out.println(evaluator.toString());
//		this.parfactors = new HashSet<SimpleParfactor>(evaluator.executeCurrentOperation());
//		System.out.println(this.parfactors.toString());
//		
//		System.out.println(evaluator.toString());
//		
//	}
//}
