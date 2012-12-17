package br.usp.poli.takiyama.common;

import java.util.Set;

import br.usp.poli.takiyama.prv.LogicalVariable;
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
	
	public static boolean checkMguAgainstConstraints(Substitution mgu, Set<Constraint> constraints) {
		for (Constraint constraint : constraints) {
			if (mgu.contains(constraint.toBinding())) {
				return false;
			}
			if (constraint.secondTermIsLogicalVariable()) {
				if (mgu.haveCommonReplacement(constraint.getFirstTerm(), (LogicalVariable) constraint.getSecondTerm())
						|| mgu.contains(constraint.toInverseBinding())) {
					return false;
				}
			}
		}
		return true;
	}

}
