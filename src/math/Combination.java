package math;

import java.util.Iterator;

public class Combination {
	public static <T> Iterator<T[]> allArrangements(T[] array) {
		Iterator<T> it = new Iterator<T>() {
			long index = 0;
			long numArrangements = Probability.intFactorial(array.length);

			@Override
			public boolean hasNext() {
				return index < numArrangements;
			}

			@Override
			public T next() {
				index++;
				return null;
			}
		};
		return null;
	}
}
//abc
//
//abc - 0
//acb - 1
//bac - 2
//bca - 3
//cab - 4
//cba - 5
//
