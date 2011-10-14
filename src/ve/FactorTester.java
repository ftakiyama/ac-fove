/**
 * 
 */
package ve;

import java.util.Hashtable;
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
		tuple = new Vector<String>();
		tuple.add("true");
		f1.addAssignment(tuple, 0.2);
		
		tuple = new Vector<String>();
		tuple.add("false");
		f2.addAssignment(tuple, 0.6);
		tuple = new Vector<String>();
		tuple.add("true");
		f2.addAssignment(tuple, 0.4);
		
		tuple = new Vector<String>();
		tuple.add("false");
		tuple.add("false");
		tuple.add("false");
		f3.addAssignment(tuple, 1.0);
		tuple = new Vector<String>();
		tuple.add("false");
		tuple.add("false");
		tuple.add("true");
		f3.addAssignment(tuple, 0.0);
		tuple = new Vector<String>();
		tuple.add("false");
		tuple.add("true");
		tuple.add("false");
		f3.addAssignment(tuple, 0.2);
		tuple = new Vector<String>();
		tuple.add("false");
		tuple.add("true");
		tuple.add("true");
		f3.addAssignment(tuple, 0.8);
		tuple = new Vector<String>();
		tuple.add("true");
		tuple.add("false");
		tuple.add("false");
		f3.addAssignment(tuple, 0.1);
		tuple = new Vector<String>();
		tuple.add("true");
		tuple.add("false");
		tuple.add("true");
		f3.addAssignment(tuple, 0.9);
		tuple = new Vector<String>();
		tuple.add("true");
		tuple.add("true");
		tuple.add("false");
		f3.addAssignment(tuple, 0.01);
		tuple = new Vector<String>();
		tuple.add("true");
		tuple.add("true");
		tuple.add("true");
		f3.addAssignment(tuple, 0.99);
		
		System.out.println("Factors filled. I will print them now:");
		
		f1.print();
		f2.print();
		f3.print();
		
		return 0;
	}
	
	// This should not be here...
	public static int testCartesianProduct() {
		Vector<String> a = new Vector<String>();
		Vector<String> b = new Vector<String>();
		Vector<String> c = new Vector<String>();
		Vector<String> d = new Vector<String>();
		Vector<Vector<String>> bc = new Vector<Vector<String>>();
		Vector<String> r = new Vector<String>();
		
		a.add("a1");
		a.add("a2");
		b.add("b1");
		b.add("b2");
		c.add("c1");
		c.add("c2");
		d.add("d1");
		d.add("d2");
		
		bc.add(a);
		bc.add(b);
		bc.add(c);
		//bc.add(d);
		
		Factor f = new Factor();
		
		f.cartesianProduct(bc, r);
		
		for (int i = 0; i < r.size(); i++) {
			System.out.println(r.get(i));
		}
		
		return 0;
	}
	
	static void testinho() {
		Hashtable<Vector<String>, Double> table = new Hashtable<Vector<String>, Double>();
		Vector<String> key = new Vector<String>();
		key.add("bla");
		table.put(key, 0.1);
		Vector<String> k = new Vector<String>();
		k.add("bla");
		System.out.print(table.get(k));
	}
}
