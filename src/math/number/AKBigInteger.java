package math.number;

import java.math.BigInteger;

public class AKBigInteger extends BigInteger {

	private static final long serialVersionUID = -7297100195361130381L;

	public AKBigInteger(byte[] val) {
		super(val);
	}

	public AKBigInteger(int signum, byte[] mag) {
		super(signum, mag);
	}

	public AKBigInteger(String val) {
		super(val);
	}

	public static AKBigInteger valueOf(BigInteger bigint) {
		return new AKBigInteger(bigint.toByteArray());
	}

	public AKBigInteger addL(long num) {
		return this.add(BigInteger.valueOf(num));
	}

	public AKBigInteger subtract(long num) {
		return this.subtract(BigInteger.valueOf(num));
	}

	@Override
	public AKBigInteger add(BigInteger num) {
		return AKBigInteger.valueOf(super.add(num));
	}

	@Override
	public AKBigInteger subtract(BigInteger num) {
		return AKBigInteger.valueOf(super.subtract(num));
	}

}
