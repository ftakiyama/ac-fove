package br.usp.poli.takiyama.common;

import br.usp.poli.takiyama.cfove.StdParfactor;

/**
 * Implementation of Visitor Pattern using triple dispatch. All this work to
 * avoid the use of instanceof....
 * 
 * @author Felipe Takiyama
 *
 */
public interface VisitableParfactor {
	
	public void accept(ParfactorVisitor visitor, Parfactor p);
	
	public void accept(ParfactorVisitor visitor, StdParfactor p); 
	
	public void accept(ParfactorVisitor visitor, AggregationParfactor p);
	
}
