package br.usp.poli.takiyama.acfove.operator;

public interface BooleanOperator extends Operator<Boolean> {
	
	/**
	 * Returns the identity to this operator.
	 * <br>
	 * Let  &otimes; be a boolean operator and '1' be the identity.
	 * Then 1 &otimes; A = A.
	 * @return The identity for this operator.
	 */
	public boolean getIdentity();
}
