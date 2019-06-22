package util;

import java.util.Iterator;

// INCOMPLETE
public class CombinedIterator<T> implements Iterator<T> {

	private final Iterator<T>[] iterators;

	private int currentIt;

	public CombinedIterator(@SuppressWarnings("unchecked") Iterator<T>... iterators) {
		this.iterators = iterators.clone();
		this.currentIt = 0;

		this.findNextAvailableIterator();
	}

	private void findNextAvailableIterator() {
		
		while (currentIt<iterators.length && !this.iterators[this.currentIt].hasNext()) {
			this.currentIt++;
		}
	}

	@Override
	public boolean hasNext() {
		this.findNextAvailableIterator();
		if (this.currentIt >= this.iterators.length)
			return false;
		return true;
	}

	@Override
	public T next() {
		this.findNextAvailableIterator();
		return this.iterators[this.currentIt].next();
	}

}
