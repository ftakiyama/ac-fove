package br.usp.poli.takiyama.acfove;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import br.usp.poli.takiyama.cfove.StdParfactor;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.Parfactors;
import br.usp.poli.takiyama.common.RandomVariableSet;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdDistribution;
import br.usp.poli.takiyama.common.StdSplitResult;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Prvs;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.utils.Lists;

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
	
	// return na primeira oportunidade em que f1 in p1 e f2 em p2 unificam
	private SplitResult unify(Parfactor p1, Parfactor p2) {
//		for (Prv prv1 : p1.prvs()) {
//			for (Prv prv2 : p2.prvs()) {
//				SplitResult result = unify(p1, prv1, p2, prv2);
//				if (!result.isEmpty()) {
//					return result;
//				}
//			}
//		}
		return StdSplitResult.getInstance();
	}
	
	// unifica os parfactors p1 e p2 nas vari‡veis prv1 e prv2
	private SplitResult unify(Parfactor p1, Prv prv1, Parfactor p2, Prv prv2) {
		p1 = simplify(p1).renameLogicalVariables();
		p2 = simplify(p2).renameLogicalVariables();
		Substitution mgu = Prvs.mgu(prv1, prv2);
		
		SplitResult result = StdSplitResult.getInstance();
		if (areNotDisjoint(prv1, prv2)) {
			SplitResult firstSplit = split(p1, mgu);
			SplitResult secondSplit = split(p2, mgu);
			firstSplit = split(firstSplit.result(). secondSplit.result().constraints());
			secondSplit = split(secondSplit.result(), firstSplit.result().constraints());
			
			result = StdSplitResult.getInstance(firstSplit, secondSplit);
		}
		
		return null;
	}
	
	// nope, need to know the type of parfactor.
	private Parfactor simplify(Parfactor parfactor) {
		LinkedList<LogicalVariable> queue = getVariablesInConstraints(parfactor);
		Set<Constraint> unaryConstraints;
		Set<Constraint> binaryConstraints;
		while (!queue.isEmpty()) {
			LogicalVariable lv = queue.poll();
			int populationSize = lv.numberOfIndividualsSatisfying(unaryConstraints);
			switch (populationSize) {
			case 0:
				simplified = StdParfactor.getInstance();
				break;
			case 1:
				Constant c = lv.individualsSatisfying(unaryConstraints);
				split.removeUnariesInvolving(lv);
				queue.addAll(split.variablesInConstraintsInvolving(lv));
				split.apply(Substitution.getInstance(Binding.getInstance(lv, c)));
				variables = Lists.apply(s, variables);
				break;
			default:
				break;
			}
		}
		
	}
}
