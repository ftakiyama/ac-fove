/**
 * 
 */
package ve;

import java.util.Vector;

/**
 * This class is used to test the {@link Factor} class.
 * @author ftakiyama
 *
 */
public class FactorTester {
	public static int test() {
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
		
		// Sets of variables for the factors
		Vector<RandomVariable> v1 = new Vector<RandomVariable>();
		Vector<RandomVariable> v2 = new Vector<RandomVariable>();
		Vector<RandomVariable> v3 = new Vector<RandomVariable>();
		
		v1.add(rain);
		v2.add(sprinkler);
		v3.add(rain);
		v3.add(sprinkler);
		v3.add(wetGrass);
		
		//Factors
		Factor f1 = new Factor(v1);
		Factor f2 = new Factor(v2);
		Factor f3 = new Factor(v3);
		
		System.out.println("Factors created. Now let's fill them.");
		
		// Filling the factors with values
		// For now it is pretty manual, but I think I will do something about it
		Vector<String> tuple = new Vector<String>();
		tuple.add("false");
		f1.addAssignment(tuple, 0.8);
		tuple.clear();
		tuple.add("true");
		f1.addAssignment(tuple, 0.2);
		
		tuple.clear();
		tuple.add("false");
		f2.addAssignment(tuple, 0.6);
		tuple.clear();
		tuple.add("true");
		f2.addAssignment(tuple, 0.4);
		
		tuple.clear();
		tuple.add("false");
		tuple.add("false");
		tuple.add("false");
		f3.addAssignment(tuple, 1.0);
		tuple.clear();
		tuple.add("false");
		tuple.add("false");
		tuple.add("true");
		f3.addAssignment(tuple, 0.0);
		tuple.clear();
		tuple.add("false");
		tuple.add("true");
		tuple.add("false");
		f3.addAssignment(tuple, 0.2);
		tuple.clear();
		tuple.add("false");
		tuple.add("true");
		tuple.add("true");
		f3.addAssignment(tuple, 0.8);
		tuple.clear();
		tuple.add("true");
		tuple.add("false");
		tuple.add("false");
		f3.addAssignment(tuple, 0.1);
		tuple.clear();
		tuple.add("true");
		tuple.add("false");
		tuple.add("true");
		f3.addAssignment(tuple, 0.9);
		tuple.clear();
		tuple.add("true");
		tuple.add("true");
		tuple.add("false");
		f3.addAssignment(tuple, 0.01);
		tuple.clear();
		tuple.add("true");
		tuple.add("true");
		tuple.add("true");
		f3.addAssignment(tuple, 0.99);
		tuple.clear();
		
		System.out.println("Factors filled. I will print them now:");
		
		f1.print();
		f2.print();
		f3.print();
		
		return 0;
	}
}
