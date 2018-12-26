package math;

public class Range {

	double left, right;

	public Range(double left, double right) {
		this.left = left;
		this.right = right;
	}

	// TODO
	public boolean overlaps(Range another) {
		return false;
	}

	@Override
	public String toString() {
		return "(" + left + ", " + right + ")";
	}
}