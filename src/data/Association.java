package data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import data.tuple.Tuple;

public class Association<T extends Tuple> implements Iterable<T> {

	private final List<T> tuples;

	public Association() {
		this.tuples = new ArrayList<>();
	}

	public T getTuple(Object o) {
		for (T tup : this.tuples) {
			if (tup.contains(o))
				return tup;
		}
		return null;
	}

	public T setTuple(T tup, int indexOfCheck) {
		for (int i = 0; i < this.tuples.size(); i++) {
			if (Data.equals(tup, this.tuples.get(i), true)) {
				T ret = this.tuples.remove(i);
				this.tuples.set(i, tup);
				return ret;
			}
		}
		this.tuples.add(tup);
		return null;
	}

	public boolean contains(Object o) {
		return this.getTuple(o) != null;
	}

	@Override
	public Iterator<T> iterator() {
		return new AssociationIterator();
	}

	class AssociationIterator implements Iterator<T> {
		private int index = 0;

		@Override
		public boolean hasNext() {
			return this.index < Association.this.tuples.size();
		}

		@Override
		public T next() {
			return Association.this.tuples.get(this.index++);
		}

	}

}
