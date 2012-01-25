/**
 * 
 */
package br.usp.dml.takiyama.cfove;

import java.util.Iterator;
import java.util.Vector;

import br.usp.dml.takiyama.trash.SetHandler;

/**
 * @author ftakiyama
 *
 */
public class FactorHandlerTester {
	public static int testSumOut () {
		System.out.println("Creating a factor...");
		
		// All variables will be binary
		Vector<String> domain = new Vector<String>();
		domain.add("true");
		domain.add("false");
		
		// Variables
		RandomVariable sprinkler = new RandomVariable("sprinker", domain);
		RandomVariable rain = new RandomVariable("rain", domain);
		RandomVariable wetGrass = new RandomVariable("wet_grass", domain);
		
		System.out.println("Variables created.");
		
		// Set of variables for the factor
		Vector<RandomVariable> v = new Vector<RandomVariable>();
		
		v.add(rain);
		v.add(sprinkler);
		v.add(wetGrass);
		
		//Factors
		Factor f = new Factor(v);
		
		System.out.println("Factor created. Now let's fill it.");
		
		// Filling the factor with values
		// For now it is pretty manual, but I think I will do something about it
		// later...
		Tuple tuple = null;		
		tuple = new Tuple("false false false");
		f.addAssignment(tuple, 1.0);
		tuple = new Tuple("false false true");
		f.addAssignment(tuple, 0.0);
		tuple = new Tuple("false true false");
		f.addAssignment(tuple, 0.2);
		tuple = new Tuple("false true true");
		f.addAssignment(tuple, 0.8);
		tuple = new Tuple("true false false");
		f.addAssignment(tuple, 0.1);
		tuple = new Tuple("true false true");
		f.addAssignment(tuple, 0.9);
		tuple = new Tuple("true true false");
		f.addAssignment(tuple, 0.01);
		tuple = new Tuple("true true true");
		f.addAssignment(tuple, 0.99);
		
		System.out.println("Factors filled. I will print it now:");
		
		f.print();
		
		System.out.println("Summing out sprinkler...");
		
		Factor g = FactorHandler.sumOut(f, sprinkler);
		
		g.print();
		
		return 0;
	}
	
	/**
	 * Test the sumOut method using a large factor.
	 * For now, I get a java heap space error thanks to my recursive method
	 * to calculate the cross product of sets.
	 * TODO find a way not to calculate the cross product recursively.
	 * @return
	 */
	public static int bigTestSumOut () {
		System.out.println("Creating a factor...");

		// Two types of domain
		Vector<String> d1 = new Vector<String>();
		d1.add("v1");
		d1.add("v2");
		Vector<String> d2 = new Vector<String>();
		d2.add("w1");
		d2.add("w2");
		d2.add("w3");
		d2.add("w4");
		
		// Variables
		Vector<RandomVariable> variable = new Vector<RandomVariable>();
		for (int i = 0; i < 1000; i++) {
			if (i % 2 == 0) {
				RandomVariable rv = new RandomVariable("X" + i, d1);
				variable.add(rv);
			} else {
				RandomVariable rv = new RandomVariable("X" + i, d2);
				variable.add(rv);
			}
		}
		
		System.out.println("Variables created. Number of variables: " + variable.size());
		
		//Factor
		Factor f = new Factor(variable);
		
		System.out.println("Factor created. Now let's fill it.");
		
		Vector<Tuple> tuple = new Vector<Tuple>();		
		
		tuple = SetHandler.cartesianProduct(variable);
		
		Iterator<Tuple> it = tuple.iterator();
		
		while (it.hasNext()) {
			f.addAssignment(it.next(), Math.random());
		}
		
		System.out.println("Factor filled. I will print it now:");
		
		f.print();
		
		System.out.println("Summing out X0...");
		
		Factor g = FactorHandler.sumOut(f, variable.firstElement());
		
		g.print();
		
		return 0;
	}
	
	public static void testMultiplication () {
		System.out.println("Creating factors...");
		
		// All variables will be binary
		Vector<String> domain = new Vector<String>();
		domain.add("false");
		domain.add("true");
		
		// Except for one
		Vector<String> anotherDomain = new Vector<String>();
		anotherDomain.add("0");
		anotherDomain.add("1");
		anotherDomain.add("2");
		
		
		// Variables
		RandomVariable x1 = new RandomVariable("x1", domain);
		RandomVariable y1 = new RandomVariable("y1", domain);
		RandomVariable y2 = new RandomVariable("y2", domain);
		RandomVariable z1 = new RandomVariable("z1", domain);
		RandomVariable z2 = new RandomVariable("z2", domain);
		RandomVariable z3 = new RandomVariable("z3", anotherDomain);
		
		// Set of variables for the factor
		Vector<RandomVariable> v1 = new Vector<RandomVariable>();
		Vector<RandomVariable> v2 = new Vector<RandomVariable>();
		Vector<RandomVariable> v3 = new Vector<RandomVariable>();
		Vector<RandomVariable> v4 = new Vector<RandomVariable>();
		Vector<RandomVariable> v5 = new Vector<RandomVariable>();
		Vector<RandomVariable> v6 = new Vector<RandomVariable>();
		
		v1.add(x1);
		v1.add(y1);
		v1.add(y2);

		v2.add(z1);
		v2.add(y1);
		v2.add(y2);
		
		v3.add(x1);
		v3.add(y1);
		
		v4.add(y1);
		v4.add(z1);
		
		v5.add(y1);
		v5.add(z1);
		v5.add(z2);
		
		v6.add(y1);
		v6.add(z3);
		
		//Factors
		Factor f1 = new Factor(v1);
		Factor f2 = new Factor(v2);
		Factor f3 = new Factor(v3);
		Factor f4 = new Factor(v4);
		Factor f5 = new Factor(v5);
		Factor f6 = new Factor(v6);
		
		// Filling the factor with values
		Vector<Tuple> allTuples = new Vector<Tuple>();
		
		// Filling f1
		allTuples = SetHandler.cartesianProduct(v1);
		Iterator<Tuple> it = allTuples.iterator();
		double value = 1;
		while (it.hasNext()) {
			f1.addAssignment(it.next(), value);
			value++;
		}
		
		// Filling f2
		allTuples = SetHandler.cartesianProduct(v2);
		it = allTuples.iterator();
		value = 1;
		while (it.hasNext()) {
			f2.addAssignment(it.next(), value);
			value++;
		}
		
		// Filling f3
		allTuples = SetHandler.cartesianProduct(v3);
		it = allTuples.iterator();
		value = 0.1;
		while (it.hasNext()) {
			f3.addAssignment(it.next(), value);
			value += 0.1;
		}
		
		// Filling f4
		allTuples = SetHandler.cartesianProduct(v4);
		it = allTuples.iterator();
		value = 1;
		while (it.hasNext()) {
			f4.addAssignment(it.next(), value);
			value++;
		}
		
		// Filling f5
		allTuples = SetHandler.cartesianProduct(v5);
		it = allTuples.iterator();
		value = 1;
		while (it.hasNext()) {
			f5.addAssignment(it.next(), value);
			value++;
		}
		
		// Filling f6
		allTuples = SetHandler.cartesianProduct(v6);
		it = allTuples.iterator();
		value = 2;
		while (it.hasNext()) {
			f6.addAssignment(it.next(), value);
			value += 2;
		}
		
		System.out.println("Printing the factors:");
		
		f1.print();
		f2.print();
		f3.print();
		f4.print();
		f5.print();
		f6.print();
		
		System.out.println("Test #1: F1 x F2");
		Factor g = FactorHandler.multiply(f1, f2);
		g.print();
		
		System.out.println("Test #2: F3 x F4");
		g = FactorHandler.multiply(f3, f4);
		g.print();
		
		System.out.println("Test #3: F3 x F5");
		g = FactorHandler.multiply(f3, f5);
		g.print();
		
		System.out.println("Test #4: F3 x F6");
		g = FactorHandler.multiply(f3, f6);
		g.print();
		
	}
}
