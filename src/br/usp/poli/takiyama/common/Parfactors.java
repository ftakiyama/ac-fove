package br.usp.poli.takiyama.common;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;

/**
 * This class contains various methods for manipulating Parfactors.
 * @author ftakiyama
 *
 */
public class Parfactors {
	private Parfactors() {
		// enforces non-instantiability
	}
	
	public static boolean isConsistent(Substitution mgu, Set<Constraint> constraints) {
		for (Constraint constraint : constraints) {
			if (mgu.contains(constraint.toBinding())) {
				return false;
			}
			if (constraint.secondTerm() instanceof LogicalVariable) { // TODO making unchecked cast
				if (mgu.hasCommonReplacement((LogicalVariable) constraint.firstTerm(), (LogicalVariable) constraint.secondTerm())
						|| mgu.contains(constraint.toInverseBinding())) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static Set<Parfactor> restoreLogicalVariableNames(Set<Parfactor> parfactors) {
		Set<Parfactor> renamedParfactors = new HashSet<Parfactor>();
		for (Parfactor p : parfactors) {
			renamedParfactors.add(p.restoreLogicalVariableNames());
		}
		return renamedParfactors;
	}
	
	public static Set<Parfactor> unify(Parfactor p1, Parfactor p2) {
		
//		Parfactor [] parfactor = new Parfactor[2];
//		parfactor[0] = p1;
//		parfactor[1] = p2;
//		
//		parfactor = simplifyLogicalVariables(parfactor);
//		parfactor = renameLogicalVariables(parfactor);
//		
//		p1 = parfactor[0];
//		p2 = parfactor[1];
//		
//		boolean setUpdated = true;
//		while (setUpdated) {
//			setUpdated = false;
//			for (Prv prv1 : p1.variables()) {
//				for (Prv prv2 : p2.variables()) {
//					try {
//						Substitution mgu = Prvs.mgu(prv1, prv2);
//						if (!mgu.isEmpty() && mguIsConsistent(allConstraints)) {
//							splitOn(mgu);
//							splitOnConstraints();
//							setUpdated = true;
//							
//							/*
//							 * Since the set of parfactors was updated, 
//							 * WAIT! need to refactor this algorithm because it
//							 * is WRONG!!!!
//							 * */
//							break;
//						}
//					} catch (IllegalArgumentException e) {
//						/* 
//						 * prv1 and prv2 represent disjoint sets of 
//						 * random variables, thus no splits are needed;
//						 * go to next prv in p2 
//						 */
//						continue;
//					}
//				}
//			}
//		}
		
//		Set<Parfactor> unifiedSet = new LinkedHashSet<Parfactor>();
//		unifiedSet.add(g1);
//		unifiedSet.add(g2);
//		
//		unifiedSet = simplify(unifiedSet);
//		unifiedSet = renameLogicalVariables(unifiedSet);
//		
//		g1 = unifiedSet.iterator().next();
//		g2 = unifiedSet.iterator().next();
//		
//		boolean wasModified = true;
//		while (wasModified) {
//			wasModified = false;
//			for (ParameterizedRandomVariable f1 : g1.variables()) {
//				for (ParameterizedRandomVariable f2 : g2.variables()) {
//					try {
//						Substitution mgu = f1.getMgu(f2);
//						Set<Constraint> allConstraints = constraintsFromParfactors(g1, g2);
//						allConstraints.add(constraintsFromPrvs(f1, f2));
//						if (isSplittable(mgu, allConstraints)) {
//							int [] index = {g1.indexOf(f1), g2.indexOf(f2)};
//							Distribution [] splitOnMgu = {g1.splitOn(mgu), g2.splitOn(mgu)};
//							
//							// encpasulate
//							Constraint [] resultConstraint = constraintsFromParfactors(splitOnMgu);
//							resultConstraint[0] = (splitOnMgu[1].result().prvAt(index).constraints());
//							resultConstraint[1] = (splitOnMgu[0].result().prvAt(index).constraints());
//							
//							Distribution [] splitOnConstraints = {g1.splitOn(resultConstraint),...};
							
//						}
//					} catch (IllegalArgumentException e) {
//						// f1 and f2 are disjoint sets of RVs
//						continue;
//					}
//				}
//			}
//			
//		}
//		
//		return unifiedSet;
		return null;
	}
//	
//	/**
//	 * Replaces logical variables restricted to a single value in parfactors
//	 * from the specified set.
//	 * @param parfactors A set of parfactors.
//	 * @return The specified set with simplified parfactors
//	 */
//	private static Set<Parfactor> simplify(Set<Parfactor> parfactors) {
//		Set<Parfactor> simplified = new LinkedHashSet<Parfactor>(parfactors.size());
//		for (Parfactor p : parfactors) {
//			simplified.add(p.replaceLogicalVariablesConstrainedToSingleConstant());
//		}
//		return simplified;
//	}
//	
//	/**
//	 * Renames logical variables in parfactors from the specified set, so
//	 * that no logical variables appears in two different parfactors.
//	 * @param parfactors A set of parfactors
//	 * @return The specified set with all logical variables renamed
//	 */
//	private static Set<Parfactor> renameLogicalVariables(Set<Parfactor> parfactors) {
//		Set<Parfactor> renamed = new LinkedHashSet<Parfactor>(parfactors.size());
//		for (Parfactor p : parfactors) {
//			renamed.add(p.renameLogicalVariables());
//		}
//		return renamed;
//	}
//	
//	private static Set<Constraint> getAllConstraints(Parfactor g1, Parfactor g2,
//			ParameterizedRandomVariable f1, ParameterizedRandomVariable f2) {
//		Set<Constraint> allConstraints = new HashSet<Constraint>();
//		allConstraints.addAll(g1.getConstraints());
//		allConstraints.addAll(g2.getConstraints());
//		allConstraints.addAll(f1.constraints());
//		allConstraints.addAll(f2.constraints());
//	}
//	
//	private static boolean isSplittable(Substitution mgu, Set<Constraint> constraints) {
//		boolean mguIsNotEmpty = !mgu.isEmpty();
//		boolean mguIsConsistentWithConstraints = isConsistent(mgu, constraints);
//		boolean isSplittable = mguIsNotEmpty && mguIsConsistentWithConstraints;
//		
//		return isSplittable;
//	}
//	
//	private static Set<Parfactor> splitOnMgu(Substitution mgu, Parfactor g1, Parfactor g2) {
//		List<Parfactor> firstSplitOnMgu = g1.splitOnMgu(mgu);
//		List<Parfactor> secondSplitOnMgu = g2.splitOnMgu(mgu);
//		
//		/// this is ugly and unclear. come back to it later...
//		Parfactor firstResult = firstSplitOnMgu.remove(0);
//		Parfactor secondResult = secondSplitOnMgu.remove(0);
//		
//	}
	

}
