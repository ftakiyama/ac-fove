/**
 * 
 */
package ve;


/**
 * This class is used to test the {@link RandomVariable} class.
 * @author ftakiyama
 *
 */
public class RandomVariableTester {
    	
	public static int test() {
		System.out.println("Creating a random variable...");
		String [] domain = {"x1", "x2", "x3"};		
		RandomVariable v = new RandomVariable("myRandomVarible", domain);
		System.out.println("Done. Let's see what has been created: ");
		v.print();
		
		return 0;
	}
}
