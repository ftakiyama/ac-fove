/**
 * 
 */
package ve;

import java.util.Vector;

/**
 * @author ftakiyama
 *
 */
public class FactorHandlerTester {
	public static int testSumOut() {
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
}
