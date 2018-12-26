package math.number;

import java.math.BigDecimal;

public class AKBigDecimal extends BigDecimal {

	/**
	 *
	 */
	private static final long serialVersionUID = 4578710434434026566L;

	public AKBigDecimal(String val) {
		super(val);
	}

	public static AKBigDecimal valueOf(BigDecimal bigint) {
		// return new AKBigDecimal(bigint.toByteArray());
		return null;
	}

	public AKBigDecimal addL(long num) {
		return this.add(BigDecimal.valueOf(num));
	}

	public AKBigDecimal subtract(long num) {
		return this.subtract(BigDecimal.valueOf(num));
	}

	@Override
	public AKBigDecimal add(BigDecimal num) {
		return AKBigDecimal.valueOf(super.add(num));
	}

	@Override
	public AKBigDecimal subtract(BigDecimal num) {
		return AKBigDecimal.valueOf(super.subtract(num));
	}

}
