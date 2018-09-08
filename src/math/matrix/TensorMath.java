package math.matrix;

import array.Arrays;
import data.function.Function2D;

public class TensorMath {

	public static final Function2D<Double, Double, Double> ADD_DOUBLE_FUNCTION_2D = new Function2D<Double, Double, Double>() {
		@Override
		public Double evaluate(Double a, Double b) {
			return a + b;
		}
	};

	public static Double[][] tensorAdd(Double[][] m1, Double[][] m2) {
		return Arrays.performFunction(m1, m2, ADD_DOUBLE_FUNCTION_2D, Arrays.newArrayOfSameSize(m1, null));
	}

	public static double[] tensorAdd(double[] t1, double[] t2) {
		double[] mResult = t1.clone();
		for (int i = 0; i < t1.length; i++) {
			mResult[i] = t1[i] + t2[i];
		}
		return mResult;
	}

	public static double[][] tensorAdd(double[][] t1, double[][] t2) {
		double[][] mResult = t1.clone();
		for (int i = 0; i < t1.length; i++) {
			mResult[i] = tensorAdd(t1[i], t2[i]);
		}
		return mResult;
	}

	public static double[][][] tensorAdd(double[][][] t1, double[][][] t2) {
		double[][][] mResult = t1.clone();
		for (int i = 0; i < t1.length; i++) {
			mResult[i] = tensorAdd(t1[i], t2[i]);
		}
		return mResult;
	}

	public static double[] tensorScale(double[] tensor, double scale) {
		tensor = tensor.clone();
		for (int i = 0; i < tensor.length; i++) {
			tensor[i] = tensor[i] * scale;
		}
		return tensor;
	}

	public static double[][] tensorScale(double[][] tensor, double scale) {
		tensor = tensor.clone();
		for (int i = 0; i < tensor.length; i++) {
			tensor[i] = tensorScale(tensor[i], scale);
		}
		return tensor;
	}

	public static double[][][] tensorScale(double[][][] tensor, double scale) {
		tensor = tensor.clone();
		for (int i = 0; i < tensor.length; i++) {
			tensor[i] = tensorScale(tensor[i], scale);
		}
		return tensor;
	}

	public static double[][] matrixMult(double[][] m1, double[][] m2) {
		int m1height = m1.length, m1width = m1[0].length;
		int m2height = m2.length, m2width = m2[0].length;

		if (m1width != m2height)
			return null;
		int mrheight = m1height; // m result rows length
		int mrwidth = m2width; // m result columns length
		double[][] mr = new double[mrheight][mrwidth];
		for (int i = 0; i < mrheight; i++) { // rows from m1
			for (int j = 0; j < mrwidth; j++) {
				mr[i][j] = 0;
				for (int k = 0; k < m1width; k++) {
					mr[i][j] += m1[i][k] * m2[k][j];
				}
			}
		}
		return mr;
	}

}
