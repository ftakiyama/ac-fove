/*******************************************************************************
 * Copyright 2014 Felipe Takiyama
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package br.usp.poli.takiyama.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.usp.poli.takiyama.utils.MathUtils.Multinomial;
import static br.usp.poli.takiyama.utils.MathUtils.*;
import static org.junit.Assert.*;

public class MathUtilsTest {
	
	/**
	 * Calculates multinomial(). Result is 1
	 */
	@Test
	public void testMultinomialEmpty() {
		List<Integer> m = new ArrayList<Integer>(1);
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(1);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Calculates multinomial(0). Result is 1
	 */
	@Test
	public void testMultinomial0() {
		List<Integer> m = new ArrayList<Integer>(1);
		m.add(Integer.valueOf(0));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(1);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Calculates multinomial(1). Result is 1
	 */
	@Test
	public void testMultinomial1() {
		List<Integer> m = new ArrayList<Integer>(1);
		m.add(Integer.valueOf(1));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(1);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Calculates multinomial(1, 1). Result is 2
	 */
	@Test
	public void testMultinomial1_1() {
		List<Integer> m = new ArrayList<Integer>(2);
		m.add(Integer.valueOf(1));
		m.add(Integer.valueOf(1));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(2);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Calculates multinomial(1,1,1). Result is 6
	 */
	@Test
	public void testMultinomial1_1_1() {
		List<Integer> m = new ArrayList<Integer>(1);
		m.add(Integer.valueOf(1));
		m.add(Integer.valueOf(1));
		m.add(Integer.valueOf(1));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(6);
		
		assertTrue(result.equals(answer));
	}
	
	
	/**
	 * Calculates multinomial(3,2). Result is 10
	 */
	@Test
	public void testMultinomial3_2() {
		List<Integer> m = new ArrayList<Integer>(1);
		m.add(Integer.valueOf(3));
		m.add(Integer.valueOf(2));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(10);
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Calculates multinomial(1,1,1,1). Result is 24
	 */
	@Test
	public void testMultinomial1_1_1_1() {
		List<Integer> m = new ArrayList<Integer>(1);
		m.add(Integer.valueOf(1));
		m.add(Integer.valueOf(1));
		m.add(Integer.valueOf(1));
		m.add(Integer.valueOf(1));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(24);
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Calculates multinomial(3,2,1). Result is 60
	 */
	@Test
	public void testMultinomial3_2_1() {
		List<Integer> m = new ArrayList<Integer>(1);
		m.add(Integer.valueOf(3));
		m.add(Integer.valueOf(2));
		m.add(Integer.valueOf(1));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(60);
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Calculates multinomial(7,4,2). Result is 25740.
	 * This test fails when using the traditional approach (causes overflow).
	 */
	@Test
	public void testMultinomial7_4_2() {
		List<Integer> m = new ArrayList<Integer>(1);
		m.add(Integer.valueOf(7));
		m.add(Integer.valueOf(4));
		m.add(Integer.valueOf(2));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(25740);
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Calculates multinomial(9,8,4). Result is 145,495,350.
	 * This test fails when using the traditional approach (causes overflow),
	 * even if we use 64 bit integers.
	 */
	@Test
	public void testMultinomial9_8_4() {
		List<Integer> m = new ArrayList<Integer>(1);
		m.add(Integer.valueOf(9));
		m.add(Integer.valueOf(8));
		m.add(Integer.valueOf(4));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(145495350);
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Calculates multinomial(10000,0). Result is 1. 
	 */
	@Test
	public void testMultinomial10000() {
		List<Integer> m = new ArrayList<Integer>(1);
		m.add(Integer.valueOf(10000));
		m.add(Integer.valueOf(0));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(1);
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Calculates multinomial(0,2). Result is 1. 
	 */
	@Test
	public void testMultinomial0_2() {
		List<Integer> m = new ArrayList<Integer>(1);
		m.add(Integer.valueOf(0));
		m.add(Integer.valueOf(2));
		
		Multinomial mul = Multinomial.getInstance(m);
		
		BigInteger result = multinomial(mul);
		BigInteger answer = BigInteger.valueOf(1);
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Calculates 0^0 = 1.
	 */
	@Test
	public void testPow0_0() {
		BigDecimal base = new BigDecimal(0);
		int p = 0;
		int q = 1;
		
		BigDecimal result = pow(base, p, q);
		BigDecimal answer = BigDecimal.ONE;
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Calculates 0^3 = 0.
	 */
	@Test
	public void testPow0_positiveNumber() {
		BigDecimal base = new BigDecimal(0);
		int p = 3;
		int q = 1;
		
		BigDecimal result = pow(base, p, q);
		BigDecimal answer = BigDecimal.ZERO;
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Calculates 0^-3. Must throw an exception.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testPow0_negativeNumber() {
		BigDecimal base = new BigDecimal(0);
		int p = -3;
		int q = 1;
		
		pow(base, p, q);
	}
	
	/**
	 * Calculates -2^3. Must throw an exception.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testPowMinus2_3() {
		BigDecimal base = new BigDecimal(-2);
		int p = 3;
		int q = 1;
		
		pow(base, p, q);
	}
	
	/**
	 * Calculates 2^(4/2) = 4.
	 */
	@Test
	public void testPow2_2() {
		BigDecimal base = new BigDecimal(2);
		int p = 4;
		int q = 2;
		
		BigDecimal result = pow(base, p, q);
		BigDecimal answer = new BigDecimal(4);
		
		assertTrue(result.equals(answer));
	}
	
	/**
	 * Calculates 4^(1/2) = 2.
	 */
	@Test
	public void testSqrt4() {
		BigDecimal base = new BigDecimal(4);
		int p = 1;
		int q = 2;
		
		BigDecimal result = pow(base, p, q);
		BigDecimal answer = new BigDecimal(2);
		
		assertTrue(result.equals(answer));
	}
}
