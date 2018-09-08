package data.tuple;

public class Tuple2D<A, B> extends Tuple {
	public Tuple2D(A a, B b) {
		super(a, b);
	}

	@SuppressWarnings("unchecked")
	public A getA() {
		return (A) data[0];
	}

	@SuppressWarnings("unchecked")
	public B getB() {
		return (B) data[1];
	}

	public void setA(A a) {
		data[0] = a;
	}

	public void setB(B b) {
		data[1] = b;
	}

}
