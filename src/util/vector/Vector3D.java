package util.vector;

import data.tuple.Tuple3D;

public class Vector3D<T extends Number> extends Tuple3D<T, T, T> {

	private final VectorMath<T> vmath;

	@SuppressWarnings("unchecked")
	public Vector3D(T x, T y, T z) {
		super(x, y, z);
		this.vmath = (VectorMath<T>) Vector3D.getVectorMathType(x.getClass());
	}

	public Vector3D(T x, T y, T z, VectorMath<T> vmath) {
		super(x, y, z);
		this.vmath = vmath;
	}

	public static Vector3D<Double> createDoubleVector3D(double x, double y, double z) {
		return new Vector3D<>(x, y, z, VectorMath.doubleVectorMath);
	}

	public static Vector3D<Integer> createIntegerVector3D(int x, int y, int z) {
		return new Vector3D<>(x, y, z, VectorMath.integerVectorMath);
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

	public T z() {
		return this.getC();
	}

	public Vector3D<T> add(Vector3D<T> another) {
		return this.vmath.add(this, another);
	}

	public Vector3D<T> sub(Vector3D<T> another) {
		return this.vmath.sub(this, another);
	}

	public T dot(Vector3D<T> another) {
		return this.vmath.dot(this, another);
	}

	public Vector3D<T> cross(Vector3D<T> another) {
		return this.vmath.cross(this, another);
	}

	public Double dist(Vector3D<T> another) {
		return this.vmath.dist(this, another);
	}

}
