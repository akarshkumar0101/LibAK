package util.vector;

import data.tuple.Tuple2D;

public class Vector2D<T extends Number> extends Tuple2D<T, T> {

	private final VectorMath<T> vmath;

	@SuppressWarnings("unchecked")
	public Vector2D(T x, T y) {
		super(x, y);
		this.vmath = (VectorMath<T>) Vector2D.getVectorMathType(x.getClass());
	}

	public Vector2D(T x, T y, VectorMath<T> vmath) {
		super(x, y);
		this.vmath = vmath;
	}

	public static Vector2D<Double> createDoubleVector2D(double x, double y) {
		return new Vector2D<>(x, y, VectorMath.doubleVectorMath);
	}

	public static Vector2D<Integer> createIntegerVector2D(int x, int y) {
		return new Vector2D<>(x, y, VectorMath.integerVectorMath);
	}

	private static VectorMath<?> getVectorMathType(Class<? extends Number> clazz) {
		if (clazz == Double.class)
			return VectorMath.doubleVectorMath;
		else if (clazz == Integer.class)
			return VectorMath.integerVectorMath;
		return null;
	}

	public T x() {
		return this.getA();
	}

	public T y() {
		return this.getB();
	}

	public Vector2D<T> add(Vector2D<T> another) {
		return this.vmath.add(this, another);
	}

	public Vector2D<T> sub(Vector2D<T> another) {
		return this.vmath.sub(this, another);
	}

	public T dot(Vector2D<T> another) {
		return this.vmath.dot(this, another);
	}

	public Vector2D<T> cross(Vector2D<T> another) {
		return this.vmath.cross(this, another);
	}

	public Double dist(Vector2D<T> another) {
		return this.vmath.dist(this, another);
	}

}
