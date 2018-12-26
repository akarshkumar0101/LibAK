package math;

import data.tuple.Tuple2D;

public class Quadratic {
	public Tuple2D<Double, Double> solveQuadratic(double a, double b, double c) {
		double sqrt = Math.sqrt(b * b - 4 * a * c);
		double sol1 = (-b + sqrt) / (2 * a);
		double sol2 = (-b - sqrt) / (2 * a);
		return new Tuple2D<Double, Double>(sol1, sol2);
	}
}
