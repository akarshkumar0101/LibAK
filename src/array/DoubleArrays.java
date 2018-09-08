package array;

import data.function.DoubleFunction1D;
import data.function.DoubleFunction2D;

public class DoubleArrays {
	public static final DoubleFunction2D ADD_FUNCTION = new DoubleFunction2D() {
		@Override
		public double evaluate(double a, double b) {
			return a + b;
		}
	};
	public static final DoubleFunction2D SUB_FUNCTION = new DoubleFunction2D() {
		@Override
		public double evaluate(double a, double b) {
			return a - b;
		}
	};
	public static final DoubleFunction2D MUL_FUNCTION = new DoubleFunction2D() {
		@Override
		public double evaluate(double a, double b) {
			return a * b;
		}
	};
	public static final DoubleFunction2D DIV_FUNCTION = new DoubleFunction2D() {
		@Override
		public double evaluate(double a, double b) {
			return a / b;
		}
	};

	public static double[] sliceArray(double[] array, int from, int to) {
		return java.util.Arrays.copyOfRange(array, from, to);
	}

	public static double[][] sliceArray(double[][] array, int from1, int to1, int from2, int to2) {
		double[][] newarray = java.util.Arrays.copyOfRange(array, from1, to1);
		for (int i = 0; i < newarray.length; i++) {
			newarray[i] = sliceArray(array[i], from2, to2);
		}
		return newarray;
	}

	public static double[][][] sliceArray(double[][][] array, int from1, int to1, int from2, int to2, int from3,
			int to3) {
		double[][][] newarray = java.util.Arrays.copyOfRange(array, from1, to1);
		for (int i = 0; i < newarray.length; i++) {
			newarray[i] = sliceArray(array[i], from2, to2, from3, to3);
		}
		return newarray;
	}

	public static double[][] toDoubleArray(double[] arr) {
		double[][] mat = new double[arr.length][1];

		for (int i = 0; i < mat.length; i++) {
			mat[i][0] = arr[i];
		}
		return mat;
	}

	public static double[] toSingleArray(double[][] mat) {
		double[] arr = new double[mat.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = mat[i][0];
		}
		return arr;
	}

	public static double[] deepCopy(double[] arr) {
		return arr.clone();
	}

	public static double[][] deepCopy(double[][] arr) {
		arr = arr.clone();
		for (int i = 0; i < arr.length; i++) {
			arr[i] = deepCopy(arr[i]);
		}
		return arr;
	}

	public static double[][][] deepCopy(double[][][] arr) {
		arr = arr.clone();
		for (int i = 0; i < arr.length; i++) {
			arr[i] = deepCopy(arr[i]);
		}
		return arr;
	}

	public static double[] performFunction(double[] array, DoubleFunction1D function) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = function.evaluate(array[i]);
		}
		return array;
	}

	public static double[][] performFunction(double[][] array, DoubleFunction1D function) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = performFunction(array[i], function);
		}
		return array;
	}

	public static double[][][] performFunction(double[][][] array, DoubleFunction1D function) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = performFunction(array[i], function);
		}
		return array;
	}

	public static double[] performFunction(double[] a, double[] b, DoubleFunction2D function, double[] c) {
		if (c == null) {
			c = a.clone();
		}
		for (int i = 0; i < c.length; i++) {
			c[i] = function.evaluate(a[i], b[i]);
		}
		return c;
	}

	public static double[][] performFunction(double[][] a, double[][] b, DoubleFunction2D function, double[][] c) {
		if (c == null) {
			c = a.clone();
		}
		for (int i = 0; i < c.length; i++) {
			c[i] = performFunction(a[i], b[i], function, c[i]);
		}
		return c;
	}

	public static double[][][] performFunction(double[][][] a, double[][][] b, DoubleFunction2D function,
			double[][][] c) {
		if (c == null) {
			c = a.clone();
		}
		for (int i = 0; i < c.length; i++) {
			c[i] = performFunction(a[i], b[i], function, c[i]);
		}
		return c;
	}

	public static int max(double[] array) {
		double max = Double.MIN_VALUE;
		int index = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
				index = i;
			}
		}
		return index;
	}

	public static int min(double[] array) {
		double min = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] < min) {
				min = array[i];
				index = i;
			}
		}
		return index;
	}

	public static double[] scaleExtremeToMagnitudeOne(double[] array) {
		int exi = max(performFunction(array, new DoubleFunction1D() {
			@Override
			public double evaluate(double inp) {
				return Math.abs(inp);
			}
		}));

		double scale = Math.signum(array[exi]) / array[exi];

		return performFunction(array, new DoubleFunction1D() {
			@Override
			public double evaluate(double inp) {
				return inp * scale;
			}
		});
	}

}
