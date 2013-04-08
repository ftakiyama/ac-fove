package br.usp.poli.takiyama.prv;

import java.util.Set;

import br.usp.poli.takiyama.common.Constraint;

public interface Prv {
	//public Prv apply(Binding s);
	
	public Set<Constraint> constraints();
	
	public String name();
	
	public Set<StdLogicalVariable> parameters();
	
	//public RangeElement rangeElementAt(int index);
	
	public int groundSetSize();
	
	public int rangeSize();
	
	public boolean contains(Term t);
	
	public Prv apply(Substitution s);
	
	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();
	
	@Override
	public String toString();
}
