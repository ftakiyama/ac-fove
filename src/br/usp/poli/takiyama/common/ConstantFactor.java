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
package br.usp.poli.takiyama.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RangeElement;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Lists;

public class ConstantFactor implements Factor {

	private final List<? extends Prv> variables;
	
	private final int size;
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	private ConstantFactor(List<? extends Prv> variables) {
		this.variables = Lists.listOf(variables);
		this.size = getSize(variables);
	}
	
	
	/**
	 * Returns the expected size of this factor.
	 */
	private static int getSize(List<? extends Prv> variables) {
		int size = 1;
		if (variables.isEmpty()) {
			size = 0;
		}
		for (Prv prv : variables) {
			size = size * prv.range().size();
		}
		return size;
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/
	
	/**
	 * Returns a constant parameterized factor.
	 * <p>
	 * A constant factor returns the value 1 for all tuples in the factor.
	 * </p>
	 * 
	 * @param variables A ordered list of {@link Prv}.
	 */
	public static Factor getInstance(List<? extends Prv> variables) {
		return new ConstantFactor(variables);
	}

	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	@Override
	public int getIndex(Tuple<RangeElement> tuple) throws IllegalArgumentException {
		if (tuple.isEmpty()) {
			throw new IllegalArgumentException("This tuple is empty!");
		} 
		int index = 0;
		int r = 1;
		for (int i = tuple.size() - 1; i >= 0; i--) {
			index = index + r * indexOf(i, tuple);
			r = r * rangeSize(i);
		}
		return index;
	}

	
	/**
	 * Returns the index of the range element that occupies the specified
	 * position in the tuple.
	 * 
	 * @param i The position in the tuple
	 * @param tuple A tuple of {@link RangeElement}
	 * @return
	 */
	private int indexOf(int i, Tuple<RangeElement> tuple) {
		return variables.get(i).range().indexOf(tuple.get(i));
	}
	
	
	/**
	 * Returns PRV's range size occupying the specified position in this
	 * factor.
	 * 
	 * @param i PRV's index in this factor
	 * @return PRV's range size occupying the specified position in this
	 * factor.
	 */
	private int rangeSize(int i) {
		return variables.get(i).range().size();
	}
	
	
	private RangeElement rangeElementAt(int rangeIndex, int prvIndex) {
		return variables.get(prvIndex).range().get(rangeIndex);
	}
	

	@Override
	public Tuple<RangeElement> getTuple(int index) {
		List<RangeElement> values = new ArrayList<RangeElement>(variables.size());
		for (int j = variables.size() - 1; j > 0; j--) {
			int domainSize = rangeSize(j);
			values.add(rangeElementAt(index % domainSize, j));
			index = index / domainSize;	
		}
		values.add(rangeElementAt(index, 0));
		Collections.reverse(values);
		return Tuple.getInstance(values);
	}
	

	@Override
	public BigDecimal getValue(int index) {
		return BigDecimal.ONE;
	}

	
	@Override
	public BigDecimal getValue(Tuple<RangeElement> tuple) {
		return getValue(0);
	}
	
	
	@Override
	public Iterator<Tuple<RangeElement>> iterator() {
		return new Iterator<Tuple<RangeElement>> () {
			int nextElementToReturn;
			
			@Override
			public boolean hasNext() {
				return nextElementToReturn != size;
			}
			
			@Override
			public Tuple<RangeElement> next() {
				int i = nextElementToReturn;
				if (i > size) {
					throw new NoSuchElementException();
				}
				nextElementToReturn = i + 1;
				return getTuple(i);
			}
			
			/**
			 * Throws {@link UnsupportedOperationException}.
			 */
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	

	@Override
	public int size() {
		return size;
	}

	@Override
	public String name() {
		return "1";
	}

	
	@Override
	public List<Prv> variables() {
		return Lists.listOf(variables);
	}
	

	@Override
	public List<BigDecimal> values() {
		//System.out.println("Trying to create array of size " + size);
		//System.out.println("Trying to create factor for " + variables.toString());
		List<BigDecimal> values = new ArrayList<BigDecimal>(size);
		Lists.fill(values, BigDecimal.ONE, size);
		return values;
	}

	
	@Override
	public boolean contains(Term t) {
		for (Prv prv : variables) {
			if (prv.contains(t)) {
				return true;
			}
		}
		return false;
	}

	
	@Override
	public int occurrences(Term t) {
		int count = 0;
		for (Prv prv : variables) {
			if (prv.contains(t)) {
				count++;
			}
		}
		return count;
	}

	
	@Override
	public Prv getVariableHaving(Term t) {
		Prv result = StdPrv.getInstance();
		for (Prv prv : variables) {
			if (prv.contains(t)) {
				result = prv;
			}
		}
		return result;
	}

	
	@Override
	public boolean isSubFactorOf(Factor factor) {
		return factor.variables().containsAll(variables);
	}
	

	@Override
	public boolean isConstant() {
		return true;
	}
	

	@Override
	public boolean isEmpty() {
		return variables.isEmpty();
	}

	
	@Override
	public Factor apply(Substitution s) {
		return ConstantFactor.getInstance(Lists.apply(s, Lists.listOf(variables)));
	}
	
	
	/**
	 * Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public Factor set(Tuple<RangeElement> tuple, BigDecimal value) {
		throw new UnsupportedOperationException();
	}

	
	@Override
	public Factor sumOut(Prv prv) {
		List<Prv> vars = variables();
		vars.remove(prv);
		return ConstantFactor.getInstance(vars);
	}

	
	@Override
	public Factor pow(int p, int q) {
		return this;
	}
	

	@Override
	public Factor multiply(Factor factor) {
		return factor;
	}
	

	@Override
	public Factor reorder(Factor reference) throws IllegalArgumentException {
		if (!Lists.sameElements(variables(), reference.variables())) {
			throw new IllegalArgumentException();
		}
		Factor result = ConstantFactor.getInstance(reference.variables());
		return result;
	}

	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + size;
		result = prime * result
				+ ((variables == null) ? 0 : variables.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ConstantFactor)) {
			return false;
		}
		ConstantFactor other = (ConstantFactor) obj;
		if (size != other.size) {
			return false;
		}
		if (variables == null) {
			if (other.variables != null) {
				return false;
			}
		} else if (!variables.equals(other.variables)) {
			return false;
		}
		return true;
	}
	
	
	@Override
	public String toString() {
		
//		StringBuilder result = new StringBuilder();
//		
//		if (this.variables.isEmpty()) {
//			return "Empty factor";
//		}
//		
//		String thinRule = "";
//		String thickRule = "";
//		String cellFormat = "%-10s"; //TODO: change to something more dynamic
//		String valueCellFormat = "%-10s\n";
//		
//		// Create the rules - aesthetic
//		for (int i = 0; i <= this.variables.size(); i++) {
//			thinRule += String.format(cellFormat, "").replace(" ", "-");
//		}
//		thickRule = thinRule.replace("-", "=");
//		
//		// Top rule
//		result.append(thickRule).append("\n");
//		
//		// Print the variables names
//		for (Prv prv : variables) {
//			result.append(String.format(cellFormat, prv.toString())); 
//		}
//		
//		// Value column
//		result.append(String.format(cellFormat, "VALUE")).append("\n");
//		
//		// Mid rule
//		result.append(thinRule).append("\n");
//		
//		// Print the contents
//		for (Tuple<RangeElement> tuple : this) {
//			for (int j = 0; j < tuple.size(); j++) {
//				result.append(String.format(cellFormat, tuple.get(j)));
//			}
//			// Round the value to 6 digits
//			result.append(String.format(valueCellFormat, BigDecimal.ONE));			
//		}
//		
//		// Bottom rule
//		result.append(thickRule).append("\n");
//		
//		return result.toString();
		return "";
	}

}
