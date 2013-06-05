package br.usp.poli.takiyama.ve;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.usp.poli.takiyama.common.IntTuple;

/**
 * A set of tests to check the methods of {@link IntTuple}.
 * @author ftakiyama
 *
 */
@Ignore("Old code")
public class TupleTest {

	private IntTuple tuple;
	
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
		tuple = new IntTuple(temp);
	}
	
	@Test
	public void testSubtuple() {
		int[] indexes = {0,2,4,6};
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp.add(0);
		temp.add(20);
		temp.add(40);
		temp.add(60);
		IntTuple correctResult = new IntTuple(temp);
		
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
		IntTuple correctResult = new IntTuple(temp);

		assertTrue(tuple.getModifiedTuple(0, 1000).equals(correctResult));
		
	}

}
