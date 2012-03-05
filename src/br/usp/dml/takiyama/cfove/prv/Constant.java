package br.usp.dml.takiyama.cfove.prv;

/**
 * A constant is a word that starts with a lower-case letter. [Poole, 2010]
 * @author ftakiyama
 *
 */
class Constant extends Term {
	public Constant(String value) throws IllegalArgumentException{
		super(value, true);
		
		if (Character.isUpperCase(value.charAt(0))) {
			throw new IllegalArgumentException("Exception while creating " +
					"Constant: '" + value + "' must start with " +
					"lowercase letter.");
		}
	}
	
	
}
