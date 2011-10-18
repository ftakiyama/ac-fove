/**
 * 
 */
package ve;

import java.util.Iterator;
import java.util.Vector;
import java.util.NoSuchElementException;

/**
 * This class contains methods to manipulate Sets (not necessarily the Set
 * object).
 * @author ftakiyama
 *
 */
public class SetHandler {
	
	/**
	 * Calculates the cartesian product of an arbitrary number of sets.
	 * Each set must be a {@link Vector} of strings, and all sets must be
	 * within another Vector.
	 * 
	 * This cartesian product algorithm has some flaws:
	 * 1) It is recursive
	 * 2) Variables names are not very clear [solved]
	 * 3) It concatenates Strings using the '+' operator (StringBuilder
	 *    will be more effective for large sets)
	 * 4) I am not completely satisfied with its beauty. Looks very inefficient,
	 * 	  even though I have not tested it for large sets. 
	 * 
	 * There is one advantage: it works as far as I can tell.
	 * 
	 * @param sets A Vector containing the sets of strings to be processed.
	 * @param result The result of the cartesian product. Each position in the 
	 * Vector will have a tuple. Typically, this parameter will be passed as 
	 * an empty Vector. 
	 */
	public static void cartesianProduct(Vector<Vector<String>> sets, Vector<String> result) {
		try {
			if (sets.size() > 2) {
				Vector<String> aux = sets.remove(0);
				Vector<String> resultAux = new Vector<String>();
				cartesianProduct(sets, result);
				for (int i = 0; i < aux.size(); i++) {
					for (int j = 0; j < result.size(); j++) {
						resultAux.add(aux.get(i) + " " + result.get(j));
					}
				}
				Iterator<String> auxElement = resultAux.iterator();
				result.clear();
				while (auxElement.hasNext()) {
					result.add(auxElement.next());
				}
			} else if (sets.size() == 2) {
				for (int i = 0; i < sets.firstElement().size(); i++) {
					for (int j = 0; j < sets.lastElement().size(); j++) {
						result.add(sets.firstElement().get(i) + " " + sets.lastElement().get(j));
					}
				}
			} else {
				Iterator<String> auxElement = sets.firstElement().iterator();
				while (auxElement.hasNext()) {
					result.add(auxElement.next());
				}
			}
		} catch (NoSuchElementException e) {
			System.err.println("Parameter 'sets' is empty! Exiting program...");
			System.exit(-1);
		}
	}
	
	/**
	 * Return the Cartesian product of the domains of a list of random 
	 * variables.
	 * 
	 * @param factorVariables A vector of {@link RandomVariable}'s, from which
	 * the sets to create the product will be extracted.
	 * @return Cartesian product of the domains of the list of random 
	 * variables passed as parameter.
	 */
	public Vector<Tuple> cartesianProduct(Vector<RandomVariable> factorVariables) {
		Vector<Vector<String>> sets = new Vector<Vector<String>>();
		Iterator<RandomVariable> i = factorVariables.iterator();
		Vector<String> result = new Vector<String>();
		
		while (i.hasNext()) {
			sets.add(i.next().getDomain());
		}
		
		cartesianProduct(sets, result);
		
		Vector<Tuple> tuples = new Vector<Tuple>();
		Iterator<String> results = result.iterator();
		
		while (results.hasNext()) {
			tuples.add(new Tuple(results.next()));
		}
		
		return tuples;
	}
	
	/**
	 * Tests the method cartesianProduct().
	 */
	public static void testCartesianProduct() {
		Vector<String> a = new Vector<String>();
		Vector<String> b = new Vector<String>();
		Vector<String> c = new Vector<String>();
		Vector<String> d = new Vector<String>();
		Vector<Vector<String>> bc = new Vector<Vector<String>>();
		Vector<String> r = new Vector<String>();
		
		// Create the sets
		a.add("a1");
		a.add("a2");
		b.add("b1");
		b.add("b2");
		c.add("c1");
		c.add("c2");
		d.add("d1");
		d.add("d2");
		d.add("d3");
		
		// Put the sets into a Vector
		bc.add(a);
		bc.add(b);
		//bc.add(c);
		bc.add(d);
		
		cartesianProduct(bc, r);
		
		// Print the result
		for (int i = 0; i < r.size(); i++) {
			System.out.println(r.get(i));
		}
	}
}
