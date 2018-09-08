package data.tuple;

public class Tuple3D<A, B, C> extends Tuple {
	public Tuple3D(A a, B b, C c) {
		super(a, b, c);
	}

	@SuppressWarnings("unchecked")
	public A getA() {
		return (A) data[0];
	}

	@SuppressWarnings("unchecked")
	public B getB() {
		return (B) data[1];
	}

	@SuppressWarnings("unchecked")
	public C getC() {
		return (C) data[2];
	}

	public void setA(A a) {
		data[0] = a;
	}

	public void setB(B b) {
		data[1] = b;
	}

	public void setC(C c) {
		data[2] = c;
	}

}
