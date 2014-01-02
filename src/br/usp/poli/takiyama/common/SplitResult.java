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

import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;

/**
 * Represents the result of a split operation over {@link Parfactor}s.
 * 
 * <p>
 * Splitting a parfactor on a substitution always results in at least two 
 * other parfactors: the result of applying the substitution to the parfactor
 * and the residue. The residue may or may not be composed of more than one
 * parfactor. 
 * </p>
 * 
 * @author Felipe Takiyama
 * @see Parfactor
 * @see Distribution
 * @see Marginal
 */
public final class SplitResult {
	
	// This marginal is the residue, even  though the result also belongs to the result
	private final Marginal marginal;
	
	// Stores references to parfactors in marginal
	private final Parfactor result;
	private final Distribution residue;
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/

	private SplitResult(Parfactor result, Marginal marginal) {
		this.result = result;
		this.marginal = marginal;
		this.residue = marginal.distribution();
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	public static SplitResult getInstance(Parfactor result,	Marginal residue) {
		return new SplitResult(result, residue);
	}
	
	public static SplitResult getInstance(Parfactor result, Parfactor residue) {
		Marginal marginal = new StdMarginalBuilder().parfactors(residue).build();
		return new SplitResult(result, marginal);
	}
	
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	/**
	 * Returns the result from the split.
	 * 
	 * @return The result from the split.
	 */
	public Parfactor result() {
		return result;
	}
	
	
	/**
	 * Returns the residual parfactors.
	 * 
	 * @return The residual parfactors.
	 */
	public Distribution residue() {
		return residue;
	}
	
	
	/**
	 * Returns a distribution composed by the result and residual parfactors.
	 * @return a distribution composed by the result and residual parfactors.
	 */
	public Distribution distribution() {
		return residue.add(result);
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	@Override
	public String toString() {
		return "Result:\n" + result + "\nResidues:\n" + marginal;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((marginal == null) ? 0 : marginal.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
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
		if (!(obj instanceof SplitResult)) {
			return false;
		}
		SplitResult other = (SplitResult) obj;
		if (marginal == null) {
			if (other.marginal != null) {
				return false;
			}
		} else if (!marginal.equals(other.marginal)) {
			return false;
		}
		if (result == null) {
			if (other.result != null) {
				return false;
			}
		} else if (!result.equals(other.result)) {
			return false;
		}
		return true;
	}
}
