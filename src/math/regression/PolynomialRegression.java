package math.regression;

//NOT COMPLETE
//works for linear, but is not efficient in gradient descent with degree>1
public class PolynomialRegression extends AlgebraicRegression {
	private final int degree;

	public PolynomialRegression(double[][] points, int degree) {
		super(points, degree + 1);
		this.degree = degree;
	}

	@Override
	public double[] costGradient(double[] parameters, double[][] points) {
		double[] gradient = new double[numParameters];
		for (double[] point : points) {
			double sum = 0;
			for (int pow = 0; pow <= degree; pow++) {
				sum += (parameters[pow] * Math.pow(point[0], pow));
			}
			double outderivative = 2 * (sum - point[1]);

			for (int param = 0; param < numParameters; param++) {
				gradient[param] += outderivative * Math.pow(point[0], param);
			}
		}
		return gradient;
	}

}
