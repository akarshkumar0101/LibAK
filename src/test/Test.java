package test;

import java.util.Map;

import math.Factorization;
import math.cas.CAS;
import math.cas.Expression;

public class Test {

	public static void main(String[] args) {
		Map<Integer, Integer> factors = Factorization.factorize(9708131);
		for (int key : factors.keySet()) {
			System.out.println(key + "^" + factors.get(key));
		}

	}

	public static void testCAS() {
		// System.out.println(Math.PI * Math.sin(Math.pow(2.3, Math.tan(2.3))));

		CAS cas = new CAS();
		cas.registerVariable("x");
		cas.registerVariable("y");

		Expression exp = new Expression(cas, "(9*x+x*2)*y");
		System.out.println(exp.getRoot().partialWithRespectTo(cas.getVariable("x")).consolidate());

	}
}