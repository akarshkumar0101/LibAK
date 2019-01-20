package util.vector;

public interface VectorMath<T extends Number> {

	public abstract Vector2D<T> add(Vector2D<T> a, Vector2D<T> b);

	public abstract Vector2D<T> sub(Vector2D<T> a, Vector2D<T> b);

	public abstract T dot(Vector2D<T> a, Vector2D<T> b);

	public abstract Vector2D<T> cross(Vector2D<T> a, Vector2D<T> b);

	public abstract Double dist(Vector2D<T> a, Vector2D<T> b);

	public static final VectorMath<Integer> integerVectorMath = new VectorMath<Integer>() {
		@Override
		public Vector2D<Integer> add(Vector2D<Integer> a, Vector2D<Integer> b) {
			return new Vector2D<>(a.x() + b.x(), a.y() + b.y(), this);
		}

		@Override
		public Vector2D<Integer> sub(Vector2D<Integer> a, Vector2D<Integer> b) {
			return new Vector2D<>(a.x() - b.x(), a.y() - b.y(), this);
		}

		@Override
		public Integer dot(Vector2D<Integer> a, Vector2D<Integer> b) {
			return a.x() * b.x() + a.y() + b.y();
		}

		@Override
		public Vector2D<Integer> cross(Vector2D<Integer> a, Vector2D<Integer> b) {
			return null;
		}

		@Override
		public Double dist(Vector2D<Integer> a, Vector2D<Integer> b) {
			int dx = a.x() - b.x(), dy = a.y() - b.y();
			return Math.sqrt((double) dx * dx + dy * dy);
		}
	};
	public static final VectorMath<Double> doubleVectorMath = new VectorMath<Double>() {
		@Override
		public Vector2D<Double> add(Vector2D<Double> a, Vector2D<Double> b) {
			return new Vector2D<>(a.x() + b.x(), a.y() + b.y(), this);
		}

		@Override
		public Vector2D<Double> sub(Vector2D<Double> a, Vector2D<Double> b) {
			return new Vector2D<>(a.x() - b.x(), a.y() - b.y(), this);
		}

		@Override
		public Double dot(Vector2D<Double> a, Vector2D<Double> b) {
			return a.x() * b.x() + a.y() + b.y();
		}

		@Override
		public Vector2D<Double> cross(Vector2D<Double> a, Vector2D<Double> b) {
			return null;
		}

		@Override
		public Double dist(Vector2D<Double> a, Vector2D<Double> b) {
			double dx = a.x() - b.x(), dy = a.y() - b.y();
			return Math.sqrt(dx * dx + dy * dy);
		}
	};

}
