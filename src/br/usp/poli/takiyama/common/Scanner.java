package br.usp.poli.takiyama.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.cfove.StdParfactor;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

public final class Scanner implements ParfactorDecorator {

	private final Parfactor p;
	
	public Scanner(Parfactor p) {
		this.p = p;
	}
	
	@Override
	public Set<LogicalVariable> logicalVariables() {
		Set<LogicalVariable> logicalVariables = new HashSet<LogicalVariable>();
		for (Prv prv : p.factor().variables()) {
			logicalVariables.addAll(prv.parameters());
			if (!prv.boundVariable().isEmpty()) {
				logicalVariables.add(prv.boundVariable());
			}
		}
		return logicalVariables;
	}
	
	// Everything else is delegation.
	
	@Override
	public Set<Constraint> constraints() {
		return p.constraints();
	}

	@Override
	public Factor factor() {
		return p.factor();
	}

	@Override
	public List<Prv> prvs() {
		return p.prvs();
	}

	@Override
	public int size() {
		return p.size();
	}

	@Override
	public Parfactor apply(Substitution s) {
		return p.apply(s);
	}

	@Override
	public boolean contains(Prv prv) {
		return p.contains(prv);
	}

	@Override
	public boolean isConstant() {
		return p.isConstant();
	}

	@Override
	public boolean isCountable(LogicalVariable lv) {
		return p.isCountable(lv);
	}

	@Override
	public boolean isExpandable(Prv cf, Substitution s) {
		return p.isExpandable(cf, s);
	}

	@Override
	public boolean isMultipliable(Parfactor other) {
		return p.isMultipliable(other);
	}

	@Override
	public boolean isSplittable(Substitution s) {
		return p.isSplittable(s);
	}

	@Override
	public boolean isEliminable(Prv prv) {
		return p.isEliminable(prv);
	}

	@Override
	public Parfactor count(LogicalVariable lv) {
		return p.count(lv);
	}

	@Override
	public Parfactor expand(Prv cf, Term t) {
		return p.expand(cf, t);
	}

	@Override
	public Parfactor multiply(Parfactor other) {
		return p.multiply(other);
	}

	@Override
	public Parfactor multiplicationHelper(Parfactor other) {
		return p.multiplicationHelper(other);
	}

	@Override
	public SplitResult splitOn(Substitution s) throws IllegalArgumentException {
		return p.splitOn(s);
	}

	@Override
	public Parfactor sumOut(Prv prv) {
		return p.sumOut(prv);
	}

	@Override
	public Parfactor simplifyLogicalVariables() {
		return p.simplifyLogicalVariables();
	}

	@Override
	public void accept(ParfactorVisitor visitor, Parfactor p) {
		this.p.accept(visitor, p);
	}

	@Override
	public void accept(ParfactorVisitor visitor, StdParfactor p) {
		this.p.accept(visitor, p);
	}

	@Override
	public void accept(ParfactorVisitor visitor, AggregationParfactor p) {
		this.p.accept(visitor, p);
	}

	@Override
	public boolean equals(Object o) {
		return p.equals(o);
	}

	@Override
	public int hashCode() {
		return p.hashCode();
	}

	@Override
	public String toString() {
		return p.toString();
	}
}
