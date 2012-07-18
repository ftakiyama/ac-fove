package br.usp.poli.takiyama.ve;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import br.usp.poli.takiyama.common.RandomVariable;

import com.google.common.collect.*;

/**
 * A set of tests to verify how the {@link Sets} class from Guava library works.
 * The tests in here are not "real" tests.
 * 
 * @author ftakiyama
 *
 */
public class SetsTest {
	
	HashMap<String, ImmutableSet<RandomVariable>> sets;
	
	@Before
	public void initialSetup() {
		
		this.sets = new HashMap<String,ImmutableSet<RandomVariable>>();
		
		int numberRandomVariables = 0;
		String setName = "a";
		RandomVariable[] s = new RandomVariable[numberRandomVariables];
		for (int i = 0; i < numberRandomVariables; i++) {
			s[i] = getDefaultBooleanRandomVariable(setName + Integer.toString(i));
		}
		sets.put(setName, ImmutableSet.copyOf(s));
		
		numberRandomVariables = 1;
		setName = "b";
		s = new RandomVariable[numberRandomVariables];
		for (int i = 0; i < numberRandomVariables; i++) {
			s[i] = getDefaultBooleanRandomVariable(setName + Integer.toString(i));
		}
		sets.put(setName, ImmutableSet.copyOf(s));
		
		numberRandomVariables = 2;
		setName = "c";
		s = new RandomVariable[numberRandomVariables];
		for (int i = 0; i < numberRandomVariables; i++) {
			s[i] = getDefaultBooleanRandomVariable(setName + Integer.toString(i));
		}
		sets.put(setName, ImmutableSet.copyOf(s));
	}
	
	private RandomVariable getDefaultBooleanRandomVariable(String name) {
		ArrayList<String> domain = new ArrayList<String>();
		domain.add("true");
		domain.add("false");
		
		ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
		values.add(new BigDecimal(0.5));
		values.add(new BigDecimal(0.5));
		
		return RandomVariable.createRandomVariable(name, domain, values);
	}
	
	@Test
	public void testUnionWithEmptySets() {		
		ImmutableSet<RandomVariable> correctResult = ImmutableSet.of();
		ImmutableSet<RandomVariable> result = Sets.union(sets.get("a"), sets.get("a")).immutableCopy();
		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testUnionWithEmptySet() {
		ImmutableSet<RandomVariable> correctResult = ImmutableSet.of(
				getDefaultBooleanRandomVariable("b0")
				);
		ImmutableSet<RandomVariable> result = Sets.union(sets.get("a"), sets.get("b")).immutableCopy();
		assertTrue(result.equals(correctResult));	
	}
	
	@Test
	public void testUnionWithUnitarySets() {
		ImmutableSet<RandomVariable> correctResult = ImmutableSet.of(
				getDefaultBooleanRandomVariable("b0")
				);
		ImmutableSet<RandomVariable> result = Sets.union(sets.get("b"), sets.get("b")).immutableCopy();
		assertTrue(result.equals(correctResult));	
	}
	
	@Test
	public void testUnionWithNonUnitarySets() {
		ImmutableSet<RandomVariable> correctResult = ImmutableSet.of(
				getDefaultBooleanRandomVariable("b0"),
				getDefaultBooleanRandomVariable("c0"),
				getDefaultBooleanRandomVariable("c1")
				);
		ImmutableSet<RandomVariable> result = Sets.union(sets.get("b"), sets.get("c")).immutableCopy();
		assertTrue(result.equals(correctResult));	
	}
	
	@Test
	public void testDifferenceWithEmptySets() {
		ImmutableSet<RandomVariable> correctResult = ImmutableSet.of();
		ImmutableSet<RandomVariable> result = Sets.difference(sets.get("a"), sets.get("a")).immutableCopy();
		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testDifferenceWithEmptySet() {
		ImmutableSet<RandomVariable> correctResult = ImmutableSet.of();
		ImmutableSet<RandomVariable> result = Sets.difference(sets.get("a"), sets.get("b")).immutableCopy();
		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testDifferenceSubtractingEmptySet() {
		ImmutableSet<RandomVariable> correctResult = ImmutableSet.of(
				getDefaultBooleanRandomVariable("c0"),
				getDefaultBooleanRandomVariable("c1")
				);
		ImmutableSet<RandomVariable> result = Sets.difference(sets.get("c"), sets.get("a")).immutableCopy();
		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testDifferenceWithSameSets() {
		ImmutableSet<RandomVariable> correctResult = ImmutableSet.of();
		ImmutableSet<RandomVariable> result = Sets.difference(sets.get("b"), sets.get("b")).immutableCopy();
		assertTrue(result.equals(correctResult));
	}
	
	@Test
	public void testDifferenceWithDifferentSets() {
		ImmutableSet<RandomVariable> correctResult = ImmutableSet.of(
				getDefaultBooleanRandomVariable("c0"),
				getDefaultBooleanRandomVariable("c1")
				);
		ImmutableSet<RandomVariable> result = Sets.difference(sets.get("c"), sets.get("b")).immutableCopy();
		assertTrue(result.equals(correctResult));
	}
}
