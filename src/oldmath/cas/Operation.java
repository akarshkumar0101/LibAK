package oldmath.cas;

enum Operation {
	POW, MUL, DIV, ADD, SUB, ABS, SIN, COS, TAN;

	/**
	 * Only does normal arithmetic like pow mul div add sub
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	double doOperation(double a, double b) {
		if (this == POW)
			return Math.pow(a, b);
		else if (this == MUL)
			return a * b;
		else if (this == DIV)
			return a / b;
		else if (this == ADD)
			return a + b;
		else if (this == SUB)
			return a - b;
		else
			return 0;
	}

	double doOperation(double a) {
		if (this == SIN)
			return Math.sin(a);
		if (this == COS)
			return Math.cos(a);
		if (this == TAN)
			return Math.tan(a);
		if (this == ABS)
			return Math.abs(a);

		return 0;
	}

	boolean isBasic() {
		if (this == POW || this == MUL || this == DIV || this == ADD || this == SUB)
			return true;

		return false;
	}

	boolean isTrig() {
		if (this == SIN || this == COS || this == TAN || this == ABS)
			return true;
		return false;
	}

	static Operation getOperation(char c) {

		if (c == '^')
			return POW;
		if (c == '*')
			return MUL;
		if (c == '/')
			return DIV;
		if (c == '+')
			return ADD;
		if (c == '-')
			return SUB;

		if (c == 'a')
			return ABS;
		if (c == 's')
			return SIN;
		if (c == 'c')
			return COS;
		if (c == 't')
			return TAN;

		return null;
	}

	@Override
	public String toString() {
		if (this == POW)
			return "^";
		else if (this == MUL)
			return "*";
		else if (this == DIV)
			return "/";
		else if (this == ADD)
			return "+";
		else if (this == SUB)
			return "-";
		else if (this == ABS)
			return "abs";
		else if (this == SIN)
			return "sin";
		else if (this == COS)
			return "cos";
		else if (this == TAN)
			return "tan";
		else
			return null;
	}
}
