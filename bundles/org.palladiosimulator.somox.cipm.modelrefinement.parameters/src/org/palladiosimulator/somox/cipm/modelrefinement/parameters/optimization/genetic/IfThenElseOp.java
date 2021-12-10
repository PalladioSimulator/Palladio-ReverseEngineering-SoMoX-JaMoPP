package org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic;

import java.util.function.Function;

import io.jenetics.prog.op.Op;

public class IfThenElseOp implements Op<Double>{
	private final String _name;
	private final int _arity;
	private final Function<Double[], Double> _function;

	private IfThenElseOp(final String name, final int arity, final Function<Double[], Double> function) {
		assert name != null;
		assert arity >= 0;
		assert function != null;

		_name = name;
		_function = function;
		_arity = arity;
	}

	@Override
	public Double apply(Double[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		return _name;
	}

	@Override
	public int arity() {
		return _arity;
	}
}
