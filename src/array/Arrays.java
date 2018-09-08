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
		return contains(array, element, false);
	}

	public static <T> boolean contains(T[] array, T element, boolean identical) {
		for (T e : array) {
			if (Data.satisfiesEquavilance(e, element, identical))
				return true;
		}
		return false;
	}

	public static <T> int numberOf(T[] array, T element) {
		return numberOf(array, element, false);
	}

	public static <T> int numberOf(T[] array, T element, boolean identical) {
		int num = 0;
		for (T e : array) {
			if (Data.satisfiesEquavilance(e, element, identical)) {
				num++;
			}
		}
		return num;
	}

	public static <T> int indexOf(T[] array, T element) {
		return indexOf(array, element, false);
	}

	public static <T> int indexOf(T[] array, T element, boolean identical) {
		for (int i = 0; i < array.length; i++) {
			if (Data.satisfiesEquavilance(array[i], element, identical))
				return i;
		}

		return -1;
	}

	public static <T> T[] append(T[] array, T element) {
		T[] newarray = java.util.Arrays.copyOf(array, array.length + 1);
		for (int i = 0; i < array.length; i++) {
			newarray[i] = array[i];
		}
		newarray[lastIndex(newarray)] = element;
		return newarray;
	}

	public static <T> T[] remove(T[] array, T element) {
		T[] newarray = java.util.Arrays.copyOf(array, array.length - numberOf(array, element));
		for (int i = 0, newi = 0; newi < newarray.length; i++, newi++) {
			if (Data.satisfiesEquavilance(array[i], element, false)) {
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
			newarray[i] = sliceArray(array[i], from2, to2);
		}
		return newarray;
	}

	public static <E> E[][][] sliceArray(E[][][] array, int from1, int to1, int from2, int to2, int from3, int to3) {
		E[][][] newarray = java.util.Arrays.copyOfRange(array, from1, to1);
		for (int i = 0; i < newarray.length; i++) {
			newarray[i] = sliceArray(array[i], from2, to2, from3, to3);
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
			array[i] = newArrayOfSameSize(array[i], placeHolder);
		}
		return array;
	}

	public static <E> E[][][] newArrayOfSameSize(E[][][] array, E placeHolder) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = newArrayOfSameSize(array[i], placeHolder);
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

	public static <E> E[] performFunction(E[] array, Function1D<E, E> function) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = function.evaluate(array[i]);
		}
		return array;
	}

	public static <E> E[][] performFunction(E[][] array, Function1D<E, E> function) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = performFunction(array[i], function);
		}
		return array;
	}

	public static <E> E[][][] performFunction(E[][][] array, Function1D<E, E> function) {
		array = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = performFunction(array[i], function);
		}
		return array;
	}

	public static <A, B, C> C[] performFunction(A[] a, B[] b, Function2D<A, B, C> function, C[] c) {
		for (int i = 0; i < c.length; i++) {
			c[i] = function.evaluate(a[i], b[i]);
		}
		return c;
	}

	public static <A, B, C> C[][] performFunction(A[][] a, B[][] b, Function2D<A, B, C> function, C[][] c) {
		for (int i = 0; i < c.length; i++) {
			c[i] = performFunction(a[i], b[i], function, c[i]);
		}
		return c;
	}

	public static <A, B, C> C[][][] performFunction(A[][][] a, B[][][] b, Function2D<A, B, C> function, C[][][] c) {
		for (int i = 0; i < c.length; i++) {
			c[i] = performFunction(a[i], b[i], function, c[i]);
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
			str += toString(a) + ", ";
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
			str += toString(a) + ", ";
			if (i != arr.length - 1) {
				str += "\n";
			}
		}
		return str + "]";
	}

}
