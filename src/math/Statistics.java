package math;

import java.util.Iterator;

public class Statistics {

	public double average(Iterable<Double> iterable) {
		Iterator<Double> it = iterable.iterator();
		double average = 0;
		int i = 0;
		while (it.hasNext()) {
			average += it.next();
			i++;
		}
		average /= i;
		return average;
	}
}
