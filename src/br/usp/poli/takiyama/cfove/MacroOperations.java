package br.usp.poli.takiyama.cfove;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.RandomVariableSet;
import br.usp.poli.takiyama.prv.OldCountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

public final class MacroOperations {
	
	/**
	 * Empty constructor that enforces non-instantiability
	 */
	private MacroOperations() { }
	
	/**
	 * This operation makes all the necessary splits and expansions to 
	 * guarantee that the sets of random variables represented by parameterized
	 * random variables in each parfactor of the given set are equal or 
	 * disjoint.
	 * <br>
	 * In other words, for any parameterized random variables p and q from
	 * parfactors of the specified set, p and q represent identical or
	 * disjoint sets of random variables.
	 * <br>
	 * This operation is used before multiplication and elimination 
	 * on parfactors.
	 * <br>
	 * If the specified set is empty, returns an empty set.
	 * 
	 * @param parfactors The set of parfactors to shatter.
	 * @return The specified set of parfactors shattered, or an empty set if
	 * the specified set is also empty.
	 */
	public static Set<Parfactor> shatter(Set<Parfactor> parfactors) {
		
		if (parfactors.isEmpty()) 
			return parfactors;
		
		Stack<Parfactor> parfactorsToProcess = new Stack<Parfactor>();
		parfactorsToProcess.addAll(parfactors);
		parfactors = null;
		
		Set<Parfactor> shatteredSet = new HashSet<Parfactor>();
		Set<Parfactor> shatteredPool = new HashSet<Parfactor>();
		
		while (!parfactorsToProcess.isEmpty()) {
			Parfactor p1 = parfactorsToProcess.pop();
			while (!parfactorsToProcess.isEmpty()) {
				Parfactor p2;
				try {
					p2 = parfactorsToProcess.pop();
				} catch (EmptyStackException e) {
					continue;
				}
				Set<Parfactor> unifiedSet = p1.unify(p2);
				if (unifiedSet.size() == 2
						&& unifiedSet.contains(p1)
						&& unifiedSet.contains(p2)) {
					shatteredPool.add(p2);
				} else {
					parfactorsToProcess.addAll(unifiedSet);
					parfactorsToProcess.addAll(shatteredPool);
					parfactorsToProcess.addAll(shatteredSet);
					unifiedSet.clear();
					shatteredPool.clear();
					shatteredSet.clear();
					p1 = parfactorsToProcess.pop();
				}
			}
			shatteredSet.add(p1);
			parfactorsToProcess.addAll(shatteredPool);
			shatteredPool.clear();
		}
		
		return shatteredSet;
	}
	
	/**
	 * This operation makes all the necessary splits to 
	 * guarantee that the sets of random variables represented by parameterized
	 * random variables in each parfactor of the given set are equal or 
	 * disjoint to the specified query set.
	 * <br>
	 * In other words, for any parameterized random variable p from
	 * parfactors of the specified set, p and the query set represent identical 
	 * or disjoint sets of random variables.
	 * <br>
	 * This operation is used once at the beginning of the C-FOVE algorithm.
	 * <br>
	 * If the specified set is empty, returns an empty set.
	 * <br>
	 * <b>I am not sure whether this operation should be used every time SHATTER
	 * is called.</b>
	 * 
	 * @param parfactors The set of parfactors to shatter.
	 * @param query A set of random variables to shatter against. It must be 
	 * given in the form of a {@link RandomVariableSet}
	 * @return The specified set of parfactors shattered, or an empty set if
	 * the specified set is also empty.
	 */
	public static Set<Parfactor> shatter (
			Set<Parfactor> parfactors, 
			RandomVariableSet query) {
		
		HashSet<Parfactor> shatteredSet = new HashSet<Parfactor>(parfactors);
		for (Parfactor p : parfactors) {
			if (p.contains(query.getPrv())) {
				HashSet<Constraint> constraints = 
						new HashSet<Constraint>(query.getConstraints());
				shatteredSet.remove(p);
				shatteredSet.addAll(p.splitOnConstraints(constraints));
			}
		}
		return shatteredSet;
	}
	
	/**
	 * This operation eliminates the specified Logical Variable in the 
	 * specified parfactor. This is done using the Counting operation.
	 * <br>
	 * Conditions to call this method:
	 * <li> The specified parfactor must belong to the specified set of
	 * Parfactors
	 * <li> The specified logical variable must appear free in single
	 * parameterized random variable in the specified Parfactor.
	 * <br>
	 * This method does not check for the conditions outlined above. They
	 *  must be verified in the caller method.
	 * 
	 * @param parfactors A set of parfactors
	 * @param parfactorToProcess The parfactor where the logical variable to
	 * eliminate resides. This parfactor must be in the specified set of 
	 * parfactors.
	 * @param lv The free logical variable to eliminate. It must satisfy the 
	 * conditions specified above.
	 * @return The specified set of parfactors modified so that the logical
	 * variable no longer appears free in the specified parfactor.
	 */
	public static Set<Parfactor> countingConvert(
			Set<Parfactor> parfactors, 
			Parfactor parfactorToProcess, 
			LogicalVariable lv) {
		Parfactor result = parfactorToProcess.count(lv);
		parfactors.add(result);
		parfactors.remove(parfactorToProcess);
		return parfactors;
	}
	
	/**
	 * This operation executes a split on the specified parfactor for every 
	 * constant in the population
	 * of the specified Logical Variable.
	 * <br>
	 * Conditions to call this method:
	 * <li> The specified parfactor must belong to the specified set of
	 * Parfactors
	 * <li> The specified logical variable must appear free in 
	 * parameterized random variables of the specified Parfactor.
	 * <br>
	 * <br>
	 * This method does not check for the conditions outlined above. They
	 *  must be verified in the caller method.
	 * It is always possible to propositionalize a parfactor.
	 * <br>
	 * After all the splits, the SHATTER macro operation is invoked to 
	 * guarantee that all parameterized random variables represent equal or
	 * disjoint sets of random variables.
	 * <br>
	 * 
	 * @param parfactors A set of parfactors
	 * @param parfactorToPropositionalize A parfactor from the specified set
	 * @param freeLogicalVariable A free logical variable belonging to the
	 * specified parfactor.
	 * @return The specified set of parfactors with the parfactor 
	 * propositionalized in individuals of the specified logival variable.
	 */
	public static Set<Parfactor> propositionalize(
			Set<Parfactor> parfactors,
			Parfactor parfactorToPropositionalize,
			LogicalVariable freeLogicalVariable) {
		parfactors.remove(parfactorToPropositionalize);
		parfactors.addAll(parfactorToPropositionalize.propositionalize(freeLogicalVariable));
		return shatter(parfactors);
	}
	
	/**
	 * <p>
	 * Expands the specified counting formula in the specified parfactor for all
	 * individuals of the bound variable that satisfy the constraints of the
	 * counting formula.
	 * </p>
	 * <p>
	 * Conditions to call this method:
	 * </p>
	 * <li> The specified parfactor must belong to the specified set of
	 * Parfactors
	 * <li> The specified counting formula must appear in the set of
	 * parameterized random variables of the specified Parfactor.
	 * <br>
	 * <br>
	 * <p>
	 * This method does not check for the conditions outlined above. They
	 *  must be verified in the caller method.
	 * </p>
	 * <p>
	 * Given a set of parfactors, it is always possible to fully expand a 
	 * parfactor.
	 * </p>
	 * <p>
	 * After all the splits, the SHATTER macro operation is invoked to 
	 * guarantee that all parameterized random variables represent equal or
	 * disjoint sets of random variables.
	 * </p>
	 * 
	 * @param parfactors A set of parfactors
	 * @param parfactorToExpand The parfactor in which the expansion will take
	 * place.
	 * @param countingFormula The counting formula to expand.
	 * @return The specified set of parfactor with the counting formula expanded
	 * in the specified parfactor for all individuals satisfying the constraints
	 * of the counting formula.
	 */
	public static Set<Parfactor> fullExpand(
			Set<Parfactor> parfactors,
			Parfactor parfactorToExpand,
			OldCountingFormula countingFormula) {
		
		parfactors.remove(parfactorToExpand);
		parfactors.add(parfactorToExpand.fullExpand(countingFormula));
		
		return shatter(parfactors);
	}
	
	/**
	 * <p>
	 * Eliminates the set of random variables represented by the specified
	 * parameterized random variable constrained to the given set of
	 * constraints. The parfactors that involve the specified PRV are 
	 * multiplied and then the PRV is eliminated in a lifted manner from the
	 * product, if possible.
	 * </p>
	 * <p>
	 * When it is not possible to eliminate the specified PRV, all parfactors
	 * that involve it are replace by their product.
	 * </p>
	 * <p>
	 * Conditions to call this method:
	 * </p>
	 * <li> The specified set of parfactors must have been shattered 
	 * previously to enable multiplication.
	 * <br>
	 * <br>
	 * <p>
	 * This method does not check for the conditions outlined above. They
	 *  must be verified in the caller method.
	 * </p>
	 * @param parfactors A set of parfactors
	 * @param variableToEliminate The parameterized random variable to
	 * eliminate from the set of parfactors.
	 * @param constraints A set of constraints involving logical variables
	 * from the specified parameterized random variable. It can be empty.
	 * @return The specified set of parfactors with all random variables
	 * represented by the specified PRV subject to the specified set of
	 * constraints eliminated.
	 */
	public static Set<Parfactor> globalSumOut(
			Set<Parfactor> parfactors,
			ParameterizedRandomVariable variableToEliminate,
			Set<Constraint> constraints) {
		
		Parfactor result = SimpleParfactor.getConstantInstance();
		Set<Parfactor> parfactorsCopy = new HashSet<Parfactor>(parfactors);
		for (Parfactor parfactor : parfactorsCopy) {
			if (parfactor.contains(variableToEliminate) 
					&& parfactor.getConstraints().containsAll(constraints)) {
				result = result.multiply(parfactor);
				parfactors.remove(parfactor);
			}
		}
		if (variableToEliminate.getParameters().equals(result.getLogicalVariables())) {
			result = result.sumOut(variableToEliminate);
		}
		if(!result.isConstant()) {
			parfactors.add(result);
		}
		return parfactors;
	}
}