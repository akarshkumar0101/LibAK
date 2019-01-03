package array;

import data.Data;
import data.function.Function1D;
import data.function.Function2D;

public class Arrays {

	public static <T> T lastElement(T[] array) {
		return array[array.length - 1];
	}

	public static <T> int lastIndex(T[] array) {
		return array.length - 1;
	}

	public static <T> boolean contains(T[] array, T element) {
		return Arrays.contains(array, element, false);
	}

	public static <T> boolean contains(T[] array, T element, boolean canEqual) {
		for (T e : array) {
			if (Data.equals(e, element, canEqual))
				return true;
		}
		return false;
	}

	public static <T> int numberOf(T[] array, T element) {
		return Arrays.numberOf(array, element, false);
	}

	public static <T> int numberOf(T[] array, T element, boolean canEqual) {
		int num = 0;
		for (T e : array) {
			if (Data.equals(e, element, canEqual)) {
				num++;
			}
		}
		return num;
	}

	public static <T> int indexOf(T[] array, T element) {
		return Arrays.indexOf(array, element, false);
	}

	public static <T> int indexOf(T[] array, T element, boolean canEqual) {
		for (int i = 0; i < array.length; i++) {
			if (Data.equals(array[i], element, canEqual))
				return i;
		}

		return -1;
	}

	public static <T> T[] append(T[] array, T element) {
		T[] newarray = java.util.Arrays.copyOf(array, array.length + 1);
		for (int i = 0; i < array.length; i++) {
			newarray[i] = array[i];
		}
		newarray[Arrays.lastIndex(newarray)] = element;
		return newarray;
	}

	public static <T> T[] remove(T[] array, T element) {
		T[] newarray = java.util.Arrays.copyOf(array, array.length - Arrays.numberOf(array, element));
		for (int i = 0, newi = 0; newi < newarray.length; i++, newi++) {
			if (Data.equals(array[i], element, true)) {
				i++;
			}
			newarray[newi] = array[i];
		}
		return newarray;
	}

	// public static <E extends Cloneable> E[] deepCopy(E[] array) {
	// array = array.clone();
	// for (int i = 0; i < array.length; i++) {
	// if (array[i] != null) {
	// array[i] = (E) array[i].clone();
	// }
	// }
	// java.util.Arrays.copyOf(original, newLength)
	// return array;
	// }

	public static <E> E[] toSingleArray(E[][] array, E[] result) {
		int i = 0;
		for (E[] arr : array) {
			for (E e : arr) {
				result[i++] = e;
			}
		}
		return result;
	}

	public static <E> E[][] toDoubleArray(E[] array, E[][] result) {
		int i = 0;

		for (E[] arr : result) {
			for (int j = 0; j < arr.length; j++) {
				arr[j] = array[i++];
				if (i == array.length)
					return result;
			}
		}
		return result;
	}

	public static <E> E[] sliceArray(E[] array, int from, int to) {
		return java.util.Arrays.copyOfRange(array, from, to);
	}

	public static <E> E[][] sliceArray(E[][] array, int from1, int to1, int from2, int to2) {
		E[][] newarray = java.util.Arrays.copyOfRange(array, from1, to1);
		for (int i = 0; i < newarray.length; i++) {
			newarray[i] = Arrays.sliceArray(array[i], from2, to2);
		}
		return newarray;
	}

	public static <E> E[][][] sliceArray(E[][][] array, int from1, int to1, int from2, int to2, int from3, int to3) {
		E[][][] newarray = java.util.Arrays.copyOfRange(array, from1, to1);
		for (int i = 0; i < newarray.length; i++) {
			newarray[i] = Arrays.sliceArray(array[i], from2, to2, from3, to3);
		}
		return newarray;
	}

	public static <E> E[] newArrayOfSameSize(E[] array, E placeHolder) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = placeHolder;
		}
		return array;
	}

	public static <E> E[][] newArrayOfSameSize(E[][] array, E placeHolder) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = Arrays.newArrayOfSameSize(array[i], placeHolder);
		}
		return array;
	}

	public static <E> E[][][] newArrayOfSameSize(E[][][] array, E placeHolder) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = Arrays.newArrayOfSameSize(array[i], placeHolder);
		}
		return array;
	}

	public static <E> E[] randomizeArray(E[] array) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			E e1 = array[i];
			int random = (int) (Math.random() * array.length);
			E e2 = array[random];
			array[i] = e2;
			array[random] = e1;
		}
		return array;
	}

	public static <X, Y> Y[] performFunction(X[] inarr, Y[] outarr, Function1D<X, Y> function) {
		for (int i = 0; i < inarr.length; i++) {
			outarr[i] = function.evaluate(inarr[i]);
		}
		return outarr;
	}

	public static <X, Y> Y[][] performFunction(X[][] inarr, Y[][] outarr, Function1D<X, Y> function) {
		for (int i = 0; i < inarr.length; i++) {
			outarr[i] = Arrays.performFunction(inarr[i], outarr[i], function);
		}
		return outarr;
	}

	public static <X, Y> Y[][][] performFunction(X[][][] inarr, Y[][][] outarr, Function1D<X, Y> function) {
		for (int i = 0; i < inarr.length; i++) {
			outarr[i] = Arrays.performFunction(inarr[i], outarr[i], function);
		}
		return outarr;
	}

	public static <A, B, C> C[] performFunction(A[] a, B[] b, Function2D<A, B, C> function, C[] c) {
		for (int i = 0; i < c.length; i++) {
			c[i] = function.evaluate(a[i], b[i]);
		}
		return c;
	}

	public static <A, B, C> C[][] performFunction(A[][] a, B[][] b, Function2D<A, B, C> function, C[][] c) {
		for (int i = 0; i < c.length; i++) {
			c[i] = Arrays.performFunction(a[i], b[i], function, c[i]);
		}
		return c;
	}

	public static <A, B, C> C[][][] performFunction(A[][][] a, B[][][] b, Function2D<A, B, C> function, C[][][] c) {
		for (int i = 0; i < c.length; i++) {
			c[i] = Arrays.performFunction(a[i], b[i], function, c[i]);
		}
		return c;
	}

}

class Temp {

	public static String toString(double[] arr) {
		String str = "[";
		for (double d : arr) {
			str += d + ", ";
		}
		return str + "]";
	}

	public static String toString(double[][] arr) {
		String str = "{";
		for (int i = 0; i < arr.length; i++) {
			double[] a = arr[i];
			str += Temp.toString(a) + ", ";
			if (i != arr.length - 1) {
				str += "\n";
			}
		}
		return str + "}";
	}

	public static String toString(double[][][] arr) {
		String str = "[";
		for (int i = 0; i < arr.length; i++) {
			double[][] a = arr[i];
			str += Temp.toString(a) + ", ";
			if (i != arr.length - 1) {
				str += "\n";
			}
		}
		return str + "]";
	}

}
