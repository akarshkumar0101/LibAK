package math.regression;

import array.DoubleArrays;
import data.function.DoubleFunction1D;

public abstract class AlgebraicRegression {

	private double[][] points;
	int numParameters;

	protected AlgebraicRegression(double[][] points, int numParameters) {
		setPoints(points);
		this.numParameters = numParameters;
	}

	public double[] performRegression(double[][] points, int descentIterations, double learningRate) {
		return performRegression(new double[numParameters], points, descentIterations, learningRate);
	}

	public double[] performRegression(double[] initParameters, double[][] points, int descentIterations,
			double learningRate) {
		double[] parameters = initParameters.clone();
		for (int i = 0; i < descentIterations; i++) {
			double[] gradient = costGradient(parameters, points);
			gradient = DoubleArrays.scaleExtremeToMagnitudeOne(gradient);
			double[] descentGradient = DoubleArrays.performFunction(gradient, new DoubleFunction1D() {
				@Override
				public double evaluate(double inp) {
					return -inp * .01;
				}
			});
			parameters = DoubleArrays.performFunction(parameters, descentGradient, DoubleArrays.ADD_FUNCTION, null);

			// System.out.println(Arrays.toString(gradient));
			// System.out.println(Arrays.toString(parameters));
			// System.out.println();
		}
		return parameters;
	}

	public abstract double[] costGradient(double[] parameters, double[][] points);

	public double[][] getPoints() {
		return points;
	}

	public void setPoints(double[][] points) {
		this.points = points;
	}
}
