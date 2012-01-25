/**
 * 
 */
package br.usp.dml.takiyama.cfove;

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
		// later...
		Tuple tuple = new Tuple("false");
		f1.addAssignment(tuple, 0.8);
		
		tuple = new Tuple("false");
		f2.addAssignment(tuple, 0.6);
		
		tuple = new Tuple("true");
		f1.addAssignment(tuple, 0.2);
		tuple = new Tuple("true");
		f2.addAssignment(tuple, 0.4);
		
		tuple = new Tuple("false false false");
		f3.addAssignment(tuple, 1.0);
		tuple = new Tuple("false false true");
		f3.addAssignment(tuple, 0.0);
		tuple = new Tuple("false true false");
		f3.addAssignment(tuple, 0.2);
		tuple = new Tuple("false true true");
		f3.addAssignment(tuple, 0.8);
		tuple = new Tuple("true false false");
		f3.addAssignment(tuple, 0.1);
		tuple = new Tuple("true false true");
		f3.addAssignment(tuple, 0.9);
		tuple = new Tuple("true true false");
		f3.addAssignment(tuple, 0.01);
		tuple = new Tuple("true true true");
		f3.addAssignment(tuple, 0.99);
		
		System.out.println("Factors filled. I will print them now:");
		
		f1.print();
		f2.print();
		f3.print();
		
		return 0;
	}
	
	
	// error! not a good idea to use objects as keys to hashmaps
	// immutable object? what is that?
	public static void testinho() {
		Tuple t1 = new Tuple ("1 2");
		Tuple t2 = new Tuple ("1 2");
		
		Hashtable<Tuple, Double> a = new Hashtable<Tuple, Double>();
		
		a.put(t1, 0.1);
		
		System.out.println(a.get(t2) +" " + t1 + " " + t2);
		System.out.println(t1.equals(t2));
		
		Vector<String> x1 = new Vector<String>();
		Vector<String> x2 = new Vector<String>();
		
		x1.add("1");
		x2.add("1");
		
		Hashtable<Vector<String>, Double> b = new Hashtable<Vector<String>, Double>();
		
		b.put(x1, 0.1);
		System.out.print(b.get(x2));

		System.out.print(x1.equals(x2));
		
		
	}
	
}
