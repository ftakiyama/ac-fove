/**
 * 
 */
package ve;

import java.util.Iterator;
import java.util.Vector;

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
}
