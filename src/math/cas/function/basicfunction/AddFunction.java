package math.cas.function.basicfunction;

import java.util.Map;

import array.Arrays;
import data.function.Function1D;
import data.tuple.Tuple2D;
import math.cas.CAS;
import math.cas.Constant;
import math.cas.Entity;
import math.cas.Variable;

public class AddFunction extends BasicFunction {

	public AddFunction(CAS cas, Entity... parameters) {
		super(cas, parameters);
	}

	@Override
	public double evaluate(Map<Variable, Double> variableValues) {
		return a.evaluate(variableValues) + b.evaluate(variableValues);
	}

	@Override
	public Entity partialWithRespectTo(Variable var) {
		return new AddFunction(cas, Arrays.performFunction(parameters, new Function1D<Entity, Entity>() {
			@Override
			public Entity evaluate(Entity a) {
				return a.partialWithRespectTo(var);
			}
		}));
	}

	@Override
	public Entity consolidate() {
		Entity ans = super.consolidate();

		if (ans instanceof AddFunction) {
			AddFunction func = (AddFunction) ans;

			Entity check0 = func.consolidateCheck0s();
			if (check0 != null)
				return check0;

			Entity checkLinComb = func.consolidateLinearCombination();
			if (checkLinComb != null)
				return checkLinComb;
		}

		return ans;
	}

	private Entity consolidateCheck0s() {
		if (a.equals(cas.ZERO))
			return b;
		else if (b.equals(cas.ZERO))
			return a;
		return null;
	}

	private Entity consolidateLinearCombination() {
		Tuple2D<Constant, Entity> scalea = splitScale(a), scaleb = splitScale(b);
		if (scalea.getB().equals(scaleb.getB())) {
			Constant newConst = new Constant(cas, scalea.getA().evaluate(null) + scaleb.getA().evaluate(null));
			return new MulFunction(cas, newConst, scalea.getB());
		}
		return null;
	}

	private Tuple2D<Constant, Entity> splitScale(Entity en) {
		if (en instanceof MulFunction) {
			MulFunction mulen = (MulFunction) en;
			if (mulen.a instanceof Constant)
				return new Tuple2D<Constant, Entity>((Constant) mulen.a, mulen.b);
		}
		return new Tuple2D<Constant, Entity>(cas.ONE, en);
	}

}
