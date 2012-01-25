/**
 * 
 */
package br.usp.dml.takiyama.cfove;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * This class is used only for test purposes and it doesn't interfere with
 * the main program.
 * @author ftakiyama
 *
 */

public class Sandbox {
	public static void getDigit(int number, int base, int order) {
		int count = 0;
		int remainder = number;		
		while (order != count && number != 0) {
			remainder = number % base;
			number = number / base;
			count++;
			System.out.println("x" + count + " = " + remainder);
		}
	}
	
	public static void getDigits (int number, int base) {
		Vector<Integer> remainder = new Vector<Integer>();		
		for (int i = 0; number != 0; i++) {
			remainder.add(number % base);
			number = number / base;
		}
		System.out.println(remainder);
	}
	
	/**
	 * Tests the cartesian product implemented in the guava project.
	 * It cannot handle large results. For example, the cartesian product
	 * of 10 sets, each one with 10 elements (the result will have 10^10
	 * 10-tuples).
	 */
	public static void testCartesianProduct() {
		ArrayList<ImmutableSet<String>> sets = new ArrayList<ImmutableSet<String>>();
		Set<List<String>> result;
		
		final int numSets = 10;
		final int numElements = 10;
		// Create the sets
		for (int i = 0; i < numSets; i++) {
			String[] set = new String[numElements];
			for (int j = 0; j < numElements; j++) {
				set[j] = "x_" + i + "_" + j;
			}
			sets.add(ImmutableSet.copyOf(set));
			
		}
		
		result = Sets.cartesianProduct(sets);
			       
		// Print the result
		Iterator<List<String>> it = result.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
		
	}
	
	/**
	 * Compares the difference between double and BigDecimal
	 */
	public static void sumTest() {
		double v = 0.1;
		BigDecimal z = new BigDecimal(0.1);
		for (int i = 0; i < 10; i++ ) {
			System.out.println(v + "\t" + z.toEngineeringString());
			v += 0.1;
			z = z.add(new BigDecimal(0.1));
		}
	}
}
