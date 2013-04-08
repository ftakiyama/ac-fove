package br.usp.poli.takiyama.acfove;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.Parfactors;
import br.usp.poli.takiyama.common.RandomVariableSet;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdDistribution;
import br.usp.poli.takiyama.common.StdSplitResult;
import br.usp.poli.takiyama.prv.Prv;

public final class Shatter extends AbstractMacroOperation {
	
	
	
	/**
	 * Makes all the necessary splits and expansions to 
	 * guarantee that the sets of random variables represented by parameterized
	 * random variables in each parfactor of the current distribution are equal 
	 * or disjoint.
	 * <p>
	 * In other words, for any parameterized random variables p and q from
	 * parfactors of the current distribution, p and q represent identical or
	 * disjoint sets of random variables.
	 * </p>
	 * <p>
	 * This operation is used before multiplication and elimination 
	 * on parfactors.
	 * </p>
	 * <p>
	 * If the current distribution is empty, nothing is done.
	 * </p>
	 * 
	 * @param parfactors The set of parfactors to shatter.
	 * @return The specified set of parfactors shattered, or an empty set if
	 * the specified set is also empty.
	 */
	public void run() {
		if (distribution().isEmpty()) {
			return;
		}
		
		Stack<Parfactor> parfactorsToProcess = new Stack<Parfactor>();
		parfactorsToProcess.addAll(distribution().toSet());
		
		Set<Parfactor> shatteredSet = new HashSet<Parfactor>();
		Set<Parfactor> shatteredPool = new HashSet<Parfactor>();
		
		while (!parfactorsToProcess.isEmpty()) {
			Parfactor p1 = parfactorsToProcess.pop();
			while (!parfactorsToProcess.isEmpty()) {
				if (!parfactorsToProcess.isEmpty()) {
					Parfactor p2 = parfactorsToProcess.pop();
					SplitResult unifiedSet = unify(p1, p2);
					if (unifiedSet.isEmpty()) {
						shatteredPool.add(p2);
					} else {
						parfactorsToProcess.add(unifiedSet.result()); // TODO need to add parfactors from the unifiedSet but also need to preserve the eliminables 
						parfactorsToProcess.addAll(unifiedSet.residue().toSet());
						parfactorsToProcess.addAll(shatteredPool);
						parfactorsToProcess.addAll(shatteredSet);
						shatteredPool.clear();
						shatteredSet.clear();
						p1 = parfactorsToProcess.pop();
					}
				} else {
					shatteredSet.add(p1);
				}
			}
			parfactorsToProcess.addAll(shatteredPool);
			shatteredPool.clear();
		}
		
		//return shatteredSet;
		// set the distribution to the shatteredSet
	}
	
	private SplitResult unify(Parfactor p1, Parfactor p2) {
//		for (Prv prv1 : p1.prvs()) {
//			for (Prv prv2 : p2.prvs()) {
//				SplitResult result = Parfactors.unify(p1, prv1, p2, prv2);
//				if (!result.isEmpty()) {
//					return result;
//				}
//			}
//		}
		return StdSplitResult.getInstance();
	}
}
