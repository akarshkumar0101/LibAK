package data;

import java.util.ArrayList;

import data.tuple.Tuple;

public class Association<T extends Tuple> extends ArrayList<T> implements Iterable<T> {

	private static final long serialVersionUID = -4291746612701313280L;

	public Association() {
		super();
	}

	public T getTuple(Object o) {
		for (T tup : this) {
			if (tup.contains(o))
				return tup;
		}
		return null;
	}

	public T setTuple(T tup, int indexOfCheck) {
		for (int i = 0; i < this.size(); i++) {
			if (Data.equals(tup, this.get(i), true)) {
				T ret = this.remove(i);
				this.set(i, tup);
				return ret;
			}
		}
		this.add(tup);
		return null;
	}

	@Override
	public boolean contains(Object o) {
		return this.getTuple(o) != null;
	}

}
