package util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

// INCOMPLETE
public class CombinedIterator<T> implements Iterator<T> {

	private final List<Iterable<T>> iterables;
	private Iterator<T> current;

	@SafeVarargs
	public CombinedIterator(final Iterable<T>... iterables) {
		this.iterables = new LinkedList<>(Arrays.asList(iterables));
	}

	public CombinedIterator(List<Iterable<T>> iterables) {
		this.iterables = new LinkedList<>();
		this.iterables.addAll(iterables);
	}

	@Override
	public boolean hasNext() {
		checkNext();
		return current != null && current.hasNext();
	}

	@Override
	public T next() {
		checkNext();
		if (current == null || !current.hasNext())
			throw new NoSuchElementException();
		return current.next();
	}

	@Override
	public void remove() {
		if (current == null)
			throw new IllegalStateException();
		current.remove();
	}

	private void checkNext() {
		while ((current == null || !current.hasNext()) && !iterables.isEmpty()) {
			current = iterables.remove(0).iterator();
		}
	}

}
