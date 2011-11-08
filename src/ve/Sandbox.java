/**
 * 
 */
package ve;

import java.util.Vector;

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
}
