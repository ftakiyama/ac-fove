package br.usp.poli.takiyama.acfove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.Scanner;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.common.Tuple;
import br.usp.poli.takiyama.log.ConsoleLogger;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.NameGenerator;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Prvs;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.Sets;

/**
 * This operation makes all the necessary splits and expansions to 
 * guarantee that the sets of random variables represented by parameterized
 * random variables in each parfactor of the given set are equal or 
 * disjoint.
 * <p>
 * In other words, for any parameterized random variables p and q from
 * parfactors in the marginal, p and q represent identical or
 * disjoint sets of random variables.
 * </p>
 * <p>
 * This operation is used before multiplication and elimination 
 * on parfactors.
 * </p>
 * 
 * @author Felipe Takiyama
 */
public final class Shatter implements MacroOperation {
	
	private Marginal marginal;
	
	public static class MutableQueue<T> implements Iterable<Tuple<T>> {

		// The queue
		private final List<T> queue;
		
		// Iterator that returns pairs of parfactor from the queue
		private MutableQueueIterator iterator;
		
		public MutableQueue(Collection<? extends T> c) throws  IllegalArgumentException{
			if (c.size() < 2) { 
				throw new IllegalArgumentException();
			}
			queue = new ArrayList<T>(c);
			iterator = new MutableQueueIterator();
		}
		
		public void add(Collection<? extends T> c) {
			queue.addAll(c);
			iterator.reset();
		}
		
		/**
		 * Tries to remove the elements in the specified tuple from the queue.
		 * Elements that are not in the queue are not removed.
		 * @param t
		 */
		public void remove(Tuple<T> t) {
			boolean removedItem = false;
			for (int i = 0; i < t.size(); i++) {
				if (queue.remove(t.get(i))) {
					removedItem = true;
				}
			}
			if (removedItem) {
				/*
				 * Resets iteration only if queue structure was modified.
				 * Otherwise we would have an infinite loop.
				 */
				iterator.reset();
			}
		}
		
		private class MutableQueueIterator implements Iterator<Tuple<T>> {

			private int i, j;
						
			private MutableQueueIterator() {
				reset();
			}
			
			private void reset() {
				i = -1;
				j = 0;
			}
			
			@Override
			public boolean hasNext() {
				return ((i < (queue.size() - 2)) || (j < (queue.size() - 1)));
			}

			@Override
			public Tuple<T> next() {
				
				// calculates next tuple
				if ((i == -1) && (j == 0)) {
					i++;
					j++;
				} else {
					j++;
					if (j == queue.size()) {
						i++;
						if (i != (queue.size() - 1)) {
							j = i + 1;
						}
					}
				}
				
				Tuple<T> t = Tuple.getInstance(Lists.listOf(queue.get(i), queue.get(j)));
				
				return t;
			}

			/**
			 * Throws {@link UnsupportedOperationException}.
			 */
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		}
		
		@Override
		public Iterator<Tuple<T>> iterator() {
			return iterator;
		}
		
		public Set<T> toSet() {
			return new HashSet<T>(queue);
		}
		
	}
	
	public Shatter(Marginal marginal) {
		this.marginal = new StdMarginalBuilder().add(marginal).build();
		ConsoleLogger.setup();
	}
	
//	@Override
//	public Marginal run() {
//		if (marginal.distribution().isEmpty()) {
//			return marginal;
//		}
//		
//		simplifyLogicalVariables();
//		renameAllLogicalVariables();
//				
//		Stack<Parfactor> parfactorsToProcess = new Stack<Parfactor>();
//		parfactorsToProcess.addAll(marginal.distribution().toSet());
//		
//		// A set of shattered parfactors
//		Set<Parfactor> shatteredSet = new HashSet<Parfactor>();
//		// A temporary set of shattered parfactors
//		Set<Parfactor> shatteredPool = new HashSet<Parfactor>();
//		
//		while (!parfactorsToProcess.isEmpty()) {
//			Parfactor p1 = parfactorsToProcess.pop();
//			while (!parfactorsToProcess.isEmpty()) {
//				Parfactor p2 = parfactorsToProcess.pop();
//				logger.info("\n Evaluating\n" + p1 + "\nwith \n" + p2);
//				Marginal unifiedSet = unify(p1, p2);
//				if (unifiedSet.isEmpty()) {
//					logger.info("Unification result: they do not unify");
//					shatteredPool.add(p2);
//				} else {
//					logger.info("Unification result:\n" + unifiedSet);
//					parfactorsToProcess.addAll(unifiedSet.distribution().toSet());
//					parfactorsToProcess.addAll(shatteredPool);
//					parfactorsToProcess.addAll(shatteredSet);
//					shatteredPool.clear();
//					shatteredSet.clear();
//					break; //p1 = parfactorsToProcess.pop();
//				}
//			}
//			shatteredSet.add(p1);
//			parfactorsToProcess.addAll(shatteredPool);
//			shatteredPool.clear();
//		}
//		
//		shatteredSet = Sets.apply(NameGenerator.getOldNames(), shatteredSet);
//		
//		// clears buffered names
//		NameGenerator.reset();
//		
//		return new StdMarginalBuilder().parfactors(shatteredSet)
//				.preservable(marginal.preservable()).build();
//	}
	
	@Override
	public Marginal run() {
		int marginalSize = marginal.distribution().size();
		if (marginalSize < 2) {
			return marginal;
		}
		marginal = simplifyLogicalVariables(marginal);
		renameAllLogicalVariables();
		
		MutableQueue<Parfactor> queue = new MutableQueue<Parfactor>(marginal.distribution().toSet());
		for (Tuple<Parfactor> pair : queue) {
			Marginal unifiedSet = unify(pair.get(0), pair.get(1));
			if (!unifiedSet.isEmpty()) {
				queue.remove(pair);
				queue.add(unifiedSet.distribution().toSet());
			}
		}
		
		// Renames back logical variables
		Set<Parfactor> shattered = Sets.apply(NameGenerator.getOldNames(), queue.toSet());
		
		// Clears buffered names
		NameGenerator.reset();
		
		// Builds the resulting marginal
		Marginal result = new StdMarginalBuilder().parfactors(shattered)
				.preservable(marginal.preservable()).build();
		
		// Simplifies logical variables after shattering
		result = simplifyLogicalVariables(result);
		
		return result;
	}
	
	/**
	 * Replaces logical variables constrained to a single constant with this
	 * constant in all parfactors in the distribution.
	 */
	private Marginal simplifyLogicalVariables(Marginal marginal) {
		StdMarginalBuilder m = new StdMarginalBuilder(marginal.size());
		for (Parfactor p : marginal) {
			m.add(p.simplifyLogicalVariables());
		}
		RandomVariableSet query = marginal.preservable();
		return m.preservable(query).build();
	}
	
	
	/**
	 * Renames logical variables in parfactors. This is done to avoid repetition
	 * of logical variable names from different parfactors.
	 */
	private void renameAllLogicalVariables() {
		StdMarginalBuilder m = new StdMarginalBuilder(marginal.size());
		for (Parfactor p : this.marginal) {
			m.add(renameLogicalVariables(p));
		}
		RandomVariableSet query = marginal.preservable();
		this.marginal = m.preservable(query).build();
	}
	
	/**
	 * Renames logical variables from the specified parfactor. Names are 
	 * generated by a {@link NameGenerator}.
	 */
	private Parfactor renameLogicalVariables(Parfactor p) {
		Parfactor scanned = new Scanner(p);
		return p.apply(NameGenerator.rename(scanned.logicalVariables()));
	}
	
	/**
	 * Tries to unify two parfactors. This function returns on the first 
	 * oportunity where a pair of PRVs unify.
	 * To unify two parfactors it may be necessary to call this function 
	 * several times.
	 * Returns an empty Marginal if p1 and p2 do not have unifiable PRVs.
	 */
	private Marginal unify(Parfactor p1, Parfactor p2) {
		for (Prv prv1 : p1.prvs()) {
			for (Prv prv2 : p2.prvs()) {
				Marginal result = unify(p1, prv1, p2, prv2);
				if (!result.isEmpty()) {
					return result;
				}
			}
		}
		return new StdMarginalBuilder().build();
	}
	
	/**
	 * Unifies p1 and p2 on variables prv1 and prv2.
	 * prv1 must belong to p1 and prv2 must belong to p2, although no check is
	 * made to assure that. You will get a warming IndexOutOfBoundsException if 
	 * you try to do that =) 
	 * Returns an empty Marginal if prv1 and prv2 do not unify.
	 */
	private Marginal unify(Parfactor p1, Prv prv1, Parfactor p2, Prv prv2) {
		
		// Stores the reference for prv1 and prv2 (they change if they unify)
		int indexOfPrv1 = p1.prvs().indexOf(prv1);
		int indexOfPrv2 = p2.prvs().indexOf(prv2);
		
		StdMarginalBuilder result = new StdMarginalBuilder();
		
		try {
			Substitution mgu = Prvs.mgu(prv1, prv2);
			
			// Now must check whether MGU is consistent with all constraints,
			// including constraints from counting formulas
			Set<Constraint> allConstraints = Sets.union(p1.constraints(), 
					p2.constraints(), prv1.constraints(), prv2.constraints());
			if (!mgu.isEmpty() && mgu.isConsistentWith(allConstraints)) {
				
				// Splits p1 and p2 on MGU
				SplitResult firstSplit = split(p1, mgu);
				SplitResult secondSplit = split(p2, mgu);
				
				// Splits first result on second result constraints 
				prv2 = secondSplit.result().prvs().get(indexOfPrv2);
				allConstraints = Sets.union(secondSplit.result().constraints(), prv2.constraints());
				SplitResult firstSplitOnConstraints = split(firstSplit, allConstraints);
								
				// Splits second result on first result constraints >> keep symmetry
				prv1 = firstSplit.result().prvs().get(indexOfPrv1);
				allConstraints = Sets.union(firstSplit.result().constraints(), prv1.constraints());
				SplitResult secondSplitOnConstraints = split(secondSplit, allConstraints);
				
				// Put everything together
				Set<Parfactor> union = Sets.union(
						firstSplitOnConstraints.distribution().toSet(), 
						secondSplitOnConstraints.distribution().toSet());
				result.parfactors(union);
				result.preservable(marginal.preservable());
			} else {
				// PRVs do not unify
			}
		} catch (IllegalArgumentException e) {
			// PRVs represent disjoint sets of random variables
		}
		
		return result.build();
	}
	
	/**
	 * Returns the result of splitting the specified parfactor on the 
	 * specified MGU
	 * <p>
	 * When the MGU is consistent with a set of inequality constraints,
	 * parameterized random variables represent non-disjoint and possibly
	 * non-identical sets of random variables. To make then identical, we 
	 * split the parfactor involved on the MGU.
	 * </p>
	 * <p>
	 * This method splits the specified parfactor in all substitutions present 
	 * in the MGU. The result depends on the order in which substitutions are
	 * made.
	 * </p>
	 * 
	 * @param parfactor The parfactor to split
	 * @param mgu The Most General Unifier to split this parfactor.
	 * @return The result of splitting the specified parfactor on the 
	 * specified MGU
	 */
	private SplitResult split(Parfactor parfactor, Substitution mgu) {
		Parfactor result = parfactor;
		StdMarginalBuilder residues = new StdMarginalBuilder();
		for (Binding bind : mgu.asList()) {
			Parfactor scanner = new Scanner(result);
			if (scanner.logicalVariables().contains(bind.firstTerm())) {
				
				// expands all counting formulas - not sure if it is the right thing to do
				result = expand(result, Substitution.getInstance(bind));
				
				Substitution bindAsSub = Substitution.getInstance(bind);
				if (result.isSplittable(bindAsSub)) {
					SplitResult split = result.splitOn(bindAsSub);
					result = split.result();
					residues.parfactors(split.residue());
				} else {
					result = result.apply(bindAsSub);
				}
			}
		}
		// works for std split result too
		return SplitResult.getInstance(result, residues.build()); 
	}
	
	/**
	 * Returns the result of splitting the specified parfactor on the 
	 * specified constraints.
	 * The only difference is that constraints are converted to substitutions 
	 * and splits are made on residues.
	 * 
	 * @see #split(Parfactor, Substitution)
	 */
	private SplitResult split(SplitResult splitResult, Set<Constraint> constraints) {
		Parfactor residue = splitResult.result();
		StdMarginalBuilder byProduct = new StdMarginalBuilder();
		byProduct.parfactors(splitResult.residue());
		for (Constraint constraint : constraints) {
			
			Substitution constraintAsSub = convertToSubstitution(constraint);
			residue = expand(residue, constraintAsSub);
			if (residue.isSplittable(constraintAsSub)) {
				SplitResult split = residue.splitOn(constraintAsSub);
				residue = split.residue().iterator().next();
				byProduct.parfactors(split.result());
			}
		}
		return SplitResult.getInstance(residue, byProduct.build());
	}
	
	/**
	 * Returns the result of expanding all counting formulas from the 
	 * specified parfactor on the specified term. Expansion is made only if
	 * conditions for expansion are met.
	 */
	private Parfactor expand(Parfactor parfactor, Substitution sub) {
		List<Prv> variables = parfactor.prvs();
		for (Prv prv : variables) {
			if (parfactor.isExpandable(prv, sub)) {
				Term term = sub.getReplacement(prv.boundVariable());
				parfactor = parfactor.expand(prv, term);
			}
		}
		return parfactor;
	}
	
	private Substitution convertToSubstitution(Constraint constraint) {
		Substitution constraintAsSub;
		try {
			constraintAsSub = Substitution.getInstance(constraint.toBinding());
		} catch (IllegalStateException e) {
			constraintAsSub = Substitution.getInstance(constraint.toInverseBinding());
		}
		return constraintAsSub;
	}

	@Override
	public int cost() {
		return (int) Double.POSITIVE_INFINITY;
	}

	@Override
	public int numberOfRandomVariablesEliminated() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "SHATTER";
	}
}
