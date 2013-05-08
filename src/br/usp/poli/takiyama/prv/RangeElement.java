package br.usp.poli.takiyama.prv;

/**
 * This class represents elements from parameterized random variable ranges.
 * 
 * @author Felipe Takiyama
 *
 */
public interface RangeElement {

	public RangeElement combine(RangeElement e);
	//public RangeElement apply(Operator<? extends RangeElement> op);
	
	/**
	 * Returns the result of applying the specified operator to this range
	 * element.
	 * 
	 * @param op The operator to apply to this object
	 * @return the result of applying the specified operator to this range
	 * element.
	 */
	public RangeElement apply(Operator<? extends RangeElement> op);
}
