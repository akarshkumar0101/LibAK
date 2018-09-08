package data;

import java.util.ArrayList;
import java.util.List;

import data.tuple.Tuple;

public class Association<T extends Tuple> {

	private final List<T> tuples;

	public Association() {
		tuples = new ArrayList<T>();
	}

	public T getTuple(Object o) {
		for (T tup : tuples) {
			if (tup.contains(o))
				return tup;
		}
		return null;
	}

	public T setTuple(T tup, int indexOfCheck) {
		for (int i = 0; i < tuples.size(); i++) {
			if (Data.satisfiesEquavilance(tup, tuples.get(i), false)) {
				T ret = tuples.remove(i);
				tuples.set(i, tup);
				return ret;
			}
		}
		tuples.add(tup);
		return null;
	}

	public boolean contains(Object o) {
		return getTuple(o) != null;
	}

}
