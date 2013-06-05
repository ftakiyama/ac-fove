/**
 * 
 */
package br.usp.poli.takiyama.acfove;

import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.Population;
import br.usp.poli.takiyama.prv.Prv;

/**
 * This operation represents the expansion of a counting formula 
 * for all individuals from its bound variable satisfying its constraints. 
 * <p>
 * Given a set of parfactors, it is always possible to fully expand a 
 * counting formula from some parfactor.
 * </p>
 * <p>
 * After all the expansions, the {@link Shatter} macro operation is invoked to 
 * guarantee that all parameterized random variables represent equal or
 * disjoint sets of random variables.
 * </p>
 * 
 * @author Felipe Takiyama
 */
public final class FullExpand implements MacroOperation {

	// The marginal in which the parfactor to be expanded is contained
	private Marginal marginal;
	
	// The parfactor to be fully expanded
	private final Parfactor expandableParfactor;
	
	// The counting formula that will be expanded
	private final Prv expandableVariable;
	
	public FullExpand(Marginal marginal, Parfactor expandable, Prv countingFormula) {
		this.marginal = marginal;
		this.expandableParfactor = expandable;
		this.expandableVariable = countingFormula;
	}
	
	@Override
	public Marginal run() {
		
		// Fully expands the parfactor
		Parfactor expanded = fullExpand();
		
		// Replaces the expanded parfactor in the marginal
		StdMarginalBuilder resultBuilder = new StdMarginalBuilder();
		resultBuilder.add(marginal).replace(expandableParfactor, expanded);
		
		// Shatters the new marginal and returns the result
		Marginal result = new Shatter(resultBuilder.build()).run();
		return result;
	}
	
	/**
	 * Returns the result of fully expanding the expandable parfactor on 
	 * individuals from expandable variable.
	 */
	private Parfactor fullExpand() {
		
		Parfactor toExpand = expandableParfactor;
		Prv expandable = expandableVariable;
		
		// Keeps a reference for the counting formula being expanded
		int prvIndex = toExpand.factor().variables().indexOf(expandable);
		
		// Throws exception if expandable doesn't exist in the parfactor
		if (prvIndex == -1) {
			throw new IllegalStateException();
		}
		
		// Get individuals from bound logical variables satisfying constraints
		Population population = getBoundedIndividuals();
		
		for (Constant individual : population) {
			expandable = toExpand.factor().variables().get(prvIndex);
			if (toExpand.isExpandable(expandable, individual)) {
				toExpand = toExpand.expand(expandable, individual);
			}
		}
		return toExpand;
	}
	
	/* 
	 * Returns the number of individuals from the bounded logical variable 
	 * satisfying counting formula's constraints
	 */
	private Population getBoundedIndividuals() {
		return expandableVariable.boundVariable()
				.individualsSatisfying(expandableVariable.constraints());
	}
	
	/**
	 * The cost of full expand is the size of the factor it creates, which is
	 * given by the following expression:
	 * <p>
	 * <sup>|F|</sup>&frasl;<sub>|range(&gamma;)|</sub> 
	 * x |range(f)|<sup>|D(A):CA|</sup>
	 * </p>
	 * <p>
	 * where
	 * </p>
	 * <li> &langle; C, V, F &rangle; is the parfactor being expanded
	 * <li> &gamma; = #<sub>A:C<sub>A</sub></sub>[f(...,A,...)] is the counting
	 * formula being expanded
	 */
	@Override
	public int cost() {
		// Factor's size
		int factor = expandableParfactor.factor().size();
		
		// Counting formula range size
		int countingFormula = expandableVariable.range().size();
		
		// Counting formula associated PRV range size
		int prv = ((CountingFormula) expandableVariable).prvRangeSize();
		
		// Number of individuals from the bounded logical variable satisfying
		// counting formula's constraints
		int domain = getBoundedIndividuals().size();
		
		// Finally, the result
		int resultSize = factor / countingFormula * ((int) Math.pow(prv, domain));
		
		return resultSize;
	}

	/**
	 * Returns zero.
	 */
	@Override
	public int numberOfRandomVariablesEliminated() {
		return 0;
	}

}
