package br.usp.dml.takiyama.ve;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/**
 * A set of tests to check the methods of {@link Tuple}.
 * @author ftakiyama
 *
 */
public class TupleTest {

	private Tuple tuple;
	
	@Before
	public void initialSetup() {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp.add(0);
		temp.add(10);
		temp.add(20);
		temp.add(30);
		temp.add(40);
		temp.add(50);
		temp.add(60);
		temp.add(70);
		temp.add(80);
		temp.add(90);
		tuple = new Tuple(temp);
	}
	
	@Test
	public void testSubtuple() {
		int[] indexes = {0,2,4,6};
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp.add(0);
		temp.add(20);
		temp.add(40);
		temp.add(60);
		Tuple correctResult = new Tuple(temp);
		
		assertTrue(tuple.subTuple(indexes).equals(correctResult));
	}
	
	@Test
	public void testgetModifiedTuple() {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp.add(1000);
		temp.add(10);
		temp.add(20);
		temp.add(30);
		temp.add(40);
		temp.add(50);
		temp.add(60);
		temp.add(70);
		temp.add(80);
		temp.add(90);
		Tuple correctResult = new Tuple(temp);

		assertTrue(tuple.getModifiedTuple(0, 1000).equals(correctResult));
		
	}

}
