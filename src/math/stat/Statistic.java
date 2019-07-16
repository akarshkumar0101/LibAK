package math.stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import data.function.Function1D;
import math.AKMath;

public class Statistic<T> extends ArrayList<T> {

	private static final long serialVersionUID = -1153992090526107017L;

	private Function1D<T, Double> associatedStats;

	public Comparator<T> increasingSort = (o1, o2) -> (int) Math
			.signum(Statistic.this.associatedStats.evaluate(o1) - Statistic.this.associatedStats.evaluate(o2));
	public Comparator<T> decreasingSort = (o1, o2) -> -(int) Math
			.signum(Statistic.this.associatedStats.evaluate(o1) - Statistic.this.associatedStats.evaluate(o2));

	public Statistic(Collection<T> inputData, Function1D<T, Double> associatedStats) {
		super(inputData);
		this.associatedStats = associatedStats;

		this.sort();
	}

	public Statistic(Collection<T> inputData, Map<T, Double> statsMap) {
		super(inputData);
		this.associatedStats = a -> statsMap.get(a);

		this.sort();
	}

	@SuppressWarnings("unchecked")
	public Statistic(Collection<Double> inputData) {
		super((Collection<T>) inputData);
		this.associatedStats = a -> (Double) a;

		this.sort();
	}

	public Statistic() {
		this(a -> (Double) a);
	}

	public Statistic(Function1D<T, Double> associatedStats) {
		super();
		this.associatedStats = associatedStats;
	}

	@Override
	public boolean add(T element) {
		int indexOfInsert = this.binarySearchInsert(element);
		super.add(indexOfInsert, element);

		return true;
	}

	@Override
	public void add(int index, T element) {
		this.add(element);
	}

	@Override
	public boolean addAll(Collection<? extends T> coll) {
		for (T t : coll) {
			this.add(t);
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> coll) {
		for (T t : coll) {
			this.add(t);
		}
		return true;
	}

	private int binarySearchInsert(T element) {
		double val = this.associatedStats.evaluate(element);
		if (this.size() == 0) {
			return 0;
		}
		int lowerBound = 0;
		int upperBound = this.size() - 1;
		int currentIndex = 0;
		while (true) {
			currentIndex = (upperBound + lowerBound) / 2;// mid way
			double currentIndexVal = this.associatedStats.evaluate(this.get(currentIndex));
			if (currentIndexVal > val) {
				upperBound = currentIndex - 1; // its in the lower
				if (lowerBound > upperBound) {
					return currentIndex;
				}
			} else if (currentIndexVal < val) {
				lowerBound = currentIndex + 1; // its in the upper
				if (lowerBound > upperBound) {
					return currentIndex + 1;
				}
			} else {
				return currentIndex;
			}
		}
	}

	public void sort() {
		this.sort(this.increasingSort);
	}

	private double sum;
	private double average;
	private double standardDev; // uses N
	private double standardDevPrediction; // uses N-1 (when estimating for bigger population)

	public void calculateStats() {
		this.sort();

		this.sum = 0.0;

		for (T t : this) {
			double val = this.associatedStats.evaluate(t);
			this.sum += val;
		}

		this.average = this.sum / this.size();

		this.standardDev = 0.0;

		for (T t : this) {
			double val = this.associatedStats.evaluate(t);
			double diff = val - this.average;
			this.standardDev += diff * diff;
		}
		this.standardDevPrediction = this.standardDev;

		this.standardDev /= this.size();
		this.standardDevPrediction /= this.size() - 1;

		this.standardDev = Math.sqrt(this.standardDev);
		this.standardDevPrediction = Math.sqrt(this.standardDevPrediction);
	}

	// percentile is from 0 to 1
	public T getPercentile(double percentile) {
		// 1.0 percentile is (data.size-1)
		// 0 percentile is 0;

		int index = (int) AKMath.scale(percentile, 0.0, 1.0, 0.0, this.size() - 1);
		return this.get(index);
	}

	public double getPercentileVal(double percentile) {
		T t = this.getPercentile(percentile);
		// System.err.println(this.associatedStats.evaluate(t));
		return this.associatedStats.evaluate(t);
	}

	public double getAverageVal() {
		return this.average;
	}

	public T getMedian() {
		return this.getPercentile(0.5);
	}

	public double getMedianVal() {
		return this.getPercentileVal(0.5);
	}

	public T getMin() {
		return this.getPercentile(0.0);
	}

	public double getMinVal() {
		return this.getPercentileVal(0.0);
	}

	public T getMax() {
		return this.getPercentile(1.0);
	}

	public double getMaxVal() {
		return this.getPercentileVal(1.0);
	}

	public double getRange() {
		return this.getMaxVal() - this.getMinVal();
	}
}
