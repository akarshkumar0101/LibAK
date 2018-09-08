package math.cas;

import array.Arrays;
import data.function.Function1D;

public abstract class Function extends Entity {

	protected final Entity[] parameters;

	// protected Function(Parameter parameter) {
	// this.parameter = parameter;
	// }

	public Function(CAS cas, Entity... parameters) {
		super(cas, parameters);
		this.parameters = parameters;
	}

	@Override
	public boolean equals(Object another) {

		if (getClass() != another.getClass())
			return false;
		Function func = (Function) another;
		if (parameters.length != func.parameters.length)
			return false;
		for (int i = 0; i < parameters.length; i++) {
			if (!parameters[i].equals(func.parameters[i]))
				return false;
		}
		return true;
	}

	/*
	 * Consolidates the parameters of the function and returns the same function
	 * 
	 */
	@Override
	public Entity consolidate() {
		String funcString = cas.getFunctionString(this.getClass());
		Entity[] newparams = new Entity[parameters.length];

		newparams = Arrays.performFunction(parameters, new Function1D<Entity, Entity>() {
			@Override
			public Entity evaluate(Entity a) {
				return a.consolidate();
			}
		});

		Function newFunc = cas.createFunction(funcString, newparams);

		if (newFunc.isConstant())
			return new Constant(cas, newFunc.evaluate(null));
		else
			return newFunc;
	}

	@Override
	public String toString() {
		String str = "";
		str += cas.getFunctionString(this.getClass());
		str += "(";
		for (int i = 0; i < parameters.length; i++) {
			str += parameters[i];
			if (i != parameters.length - 1) {
				str += ", ";
			}
		}
		str += ")";
		return str;
	}
}
