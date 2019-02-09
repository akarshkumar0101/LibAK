package math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import array.Arrays;
import data.tuple.Tuple2D;

public class Combination {
	
	public static <T> Iterator<T[]> allArrangements(T[] array_) {
		Iterator<T[]> it = new Iterator<T[]>() {

			T[] array = array_.clone();

			long index = 0;

			int length = this.array.length;

			long numArrangements = Probability.intFactorial(this.length);

			List<Tuple2D<Integer, Integer>> currentPath = new ArrayList<>();

			{
				this.fillPathWithFirsts();

			}

			// TODO change this from calculating numArrangements(may be too big)
			// to checking if the path that it takes is maxed out, ie. is:
			// (0,n-1)(1,n-1)(2,n-1)...(n-1,n-1)
			@Override
			public boolean hasNext() {
				return this.index < this.numArrangements;
			}

			@Override
			public T[] next() {
				this.index++;
				T[] retarray = this.array.clone();
				this.incrementPath();
				return retarray;
			}

			private void incrementPath() {
				while (!this.tryNextPath()) {
					this.backtrack();
					if (this.currentPath.size() == 0)
						return;
				}
				this.fillPathWithFirsts();
			}

			private void fillPathWithFirsts() {
				int pathlen = this.currentPath.size();
				for (int i = pathlen; i < this.length; i++) {
					this.addToCurrentPath(i, i);
				}
			}

			private boolean tryNextPath() {
				Tuple2D<Integer, Integer> lastSwap = this.currentPath.get(this.currentPath.size() - 1);
				if (!(lastSwap.getB() < this.length - 1))
					return false;
				this.removeFromCurrentPath(this.currentPath.size() - 1);
				this.addToCurrentPath(lastSwap.getA(), lastSwap.getB() + 1);
				return true;
			}

			private void backtrack() {
				this.removeFromCurrentPath(this.currentPath.size() - 1);
			}

			private void addToCurrentPath(int i1, int i2) {
				Tuple2D<Integer, Integer> tup = new Tuple2D<>(i1, i2);
				this.currentPath.add(tup);
				this.performSwap(tup);
			}

			private Tuple2D<Integer, Integer> removeFromCurrentPath(int index) {
				Tuple2D<Integer, Integer> tup = this.currentPath.remove(index);
				this.performSwap(tup);
				return tup;
			}

			private void performSwap(Tuple2D<Integer, Integer> tup) {
				Arrays.swap(this.array, tup.getA(), tup.getB());
			}
		};
		return it;
	}
}
