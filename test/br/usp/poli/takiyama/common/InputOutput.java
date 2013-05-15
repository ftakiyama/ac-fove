package br.usp.poli.takiyama.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class InputOutput<I, O> {
	
	// Both lists must have the same size
	private final List<I> inputs;
	private final List<O> outputs;
	
	private InputOutput() {
		inputs = new ArrayList<I>();
		outputs = new ArrayList<O>();
	}
	
	public static <T, U> InputOutput<T, U> getInstance() {
		return new InputOutput<T, U>();
	}
	
	public void add(I input, O output) {
		inputs.add(input);
		outputs.add(output);
	}
	
	public Collection<Object[]> toCollection() {
		List<Object[]> collection = new ArrayList<Object[]>(inputs.size());
		
		for (int i = 0; i < inputs.size(); i++) {
			Object [] pair = {inputs.get(i), outputs.get(i)};
			collection.add(pair);
		}
		
		return collection;
	}	
}
