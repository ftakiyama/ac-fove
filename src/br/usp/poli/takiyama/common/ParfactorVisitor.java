package br.usp.poli.takiyama.common;

import br.usp.poli.takiyama.cfove.StdParfactor;

public interface ParfactorVisitor {
	
	public void visit(StdParfactor p1, StdParfactor p2);
	public void visit(AggregationParfactor p1, StdParfactor p2);
	public void visit(AggregationParfactor p1, AggregationParfactor p2);
}
