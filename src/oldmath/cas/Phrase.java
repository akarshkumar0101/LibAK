package oldmath.cas;

import java.util.ArrayList;

public class Phrase extends Calculable {
	String originalString;
	String phrasalString;
	final ArrayList<Object> parts = new ArrayList<Object>();

	// 9+5^2*((3+2)/4)+2+(3*4+2);

	Phrase(Phrase phrase) {
		originalString = phrase.originalString;

		for (int i = 0; i < phrase.parts.size(); i++) {
			Object o = phrase.parts.get(i);

			if (o == null) {
				parts.add(null);
			} else if (o.getClass() == Number.class) {
				parts.add(new Number((Number) o));
			} else if (o.getClass() == Operation.class) {
				parts.add(o);
			} else if (o.getClass() == Phrase.class) {
				parts.add(new Phrase((Phrase) o));
			}
		}
		phrasalString = phrase.phrasalString;

	}

	public boolean isConstant() {
		return !originalString.contains("x");
	}

	Phrase(String inp) throws Exception {
		originalString = inp;
		inp = inp.substring(1, inp.length() - 1);
		String temp = inp;
		if (temp.equals(""))
			return;
		for (int index = 0; index < temp.length();) {

			char current = temp.charAt(index);

			// par spot
			if (current == '(') {
				int parAfter = 0;
				int i = 0;
				for (i = index + 1; i < temp.length(); i++) {
					char c = temp.charAt(i);

					if (c == '(') {
						parAfter++;
					} else if (c == ')') {
						if (parAfter > 0) {
							parAfter--;
						} else if (parAfter == 0) {// found end parenthesis

							String par = temp.substring(index, i + 1);

							// ADDING TO EQUATION (phrase)
							parts.add(new Phrase(par));
							break;
						}
					}

				}
				index = i + 1;
				continue;
			}

			// op spot
			else if (Operation.getOperation(current) != null) {
				// ADDING TO EQUATION (operation)
				parts.add(Operation.getOperation(current));
				if (Operation.getOperation(current).isBasic()) {
					index++;
				}
				if (Operation.getOperation(current).isTrig()) {
					index += 3;
				}
				continue;

			}

			// num spot
			else if (Number.isPartOfNumber(current)) {
				int i = 0;
				for (i = index + 1; i <= temp.length(); i++) {
					char c;
					try {
						c = temp.charAt(i);
					} catch (StringIndexOutOfBoundsException e) {
						c = 0;
					}

					if (!Number.isPartOfNumber(c) || i == temp.length()) {// found
																			// end
																			// of
																			// number

						String num = temp.substring(index, i);

						// ADDING TO EQUATION (number)
						parts.add(new Number(num));
						break;
					}
				}
				index = i;
				continue;
			} else if (current == 'x') {
				parts.add(null);
				index++;
			} else
				throw new Exception("Invalid Format Error");

		}
		// add needed multiplication signs
		if (parts.size() > 1) {
			for (int i = parts.size() - 1; i > 0; i--) {
				Object pre = parts.get(i - 1);
				Object now = parts.get(i);
				if (pre instanceof Calculable && now instanceof Phrase) {
					parts.add(i, Operation.MUL);
				}
				if (pre instanceof Calculable && now == null) {
					parts.add(i, Operation.MUL);
				}
				if (pre instanceof Calculable && now instanceof Operation) {
					if (((Operation) now).isTrig()) {
						parts.add(i, Operation.MUL);
					}
				}
			}
		}
		// add needed multiply by -1 for -x stuff
		if (parts.contains(Operation.SUB)) {
			if (parts.get(0) == Operation.SUB) {
				parts.set(0, new Number(-1));
				parts.add(1, Operation.MUL);
			}
		}

		try {
			this.getValue(1);
		} catch (Exception e) {
			throw new Exception("Could not calculate expression");
		}
		phrasalString = calculatePhrasalString();
	}

	@Override
	public String toString() {
		return originalString;
	}

	private String calculatePhrasalString() {
		String result = "";
		for (int i = 0; i < parts.size(); i++) {
			Object o = parts.get(i);
			if (o == null) {
				result += "null";
			} else if (o instanceof Phrase) {
				result += ((Phrase) o).getPhrasalString();
			} else if (o instanceof Operation) {
				result += ((Operation) o).toString();
			} else {
				result += o.toString();
			}
		}
		System.gc();

		return "(" + result + ")";

	}

	public String getPhrasalString() {
		return phrasalString;
	}

	@Override
	public double getValue() {
		Phrase temp = new Phrase(this);
		// check for trig/abs
		for (int i = 0; i < temp.parts.size(); i++) {
			Object obj = temp.parts.get(i);
			Operation op = null;
			if (obj.getClass() == Operation.class) {
				op = (Operation) obj;
			} else {
				continue;
			}
			if (op.isTrig()) {
				Calculable num = (Calculable) temp.parts.get(i + 1);

				// System.out.println(op+" " +num);
				temp.parts.set(i, new Number(op.doOperation(num.getValue())));

				temp.parts.remove(i + 1);
				i = i - 1;
			}
		}
		// check for exponents
		for (int i = 0; i < temp.parts.size(); i++) {
			if (temp.parts.get(i) == Operation.POW) {
				Calculable a = (Calculable) temp.parts.get(i - 1);
				Calculable b = (Calculable) temp.parts.get(i + 1);
				Operation op = (Operation) temp.parts.get(i);

				// System.out.println(a+" " +op +" " +b);
				temp.parts.set(i, new Number(op.doOperation(a.getValue(), b.getValue())));
				temp.parts.remove(i - 1);
				temp.parts.remove(i);
				i = i - 2;
			}
		}

		// check for multiply/divide
		for (int i = 0; i < temp.parts.size(); i++) {
			if (temp.parts.get(i) == Operation.MUL || temp.parts.get(i) == Operation.DIV) {
				Calculable a = (Calculable) temp.parts.get(i - 1);
				Calculable b = (Calculable) temp.parts.get(i + 1);
				Operation op = (Operation) temp.parts.get(i);

				// System.out.println(a+" " +op +" " +b);
				temp.parts.set(i, new Number(op.doOperation(a.getValue(), b.getValue())));
				temp.parts.remove(i - 1);
				temp.parts.remove(i);
				i = i - 2;
			}
		}
		// check for add/subtract
		for (int i = 0; i < temp.parts.size(); i++) {
			if (temp.parts.get(i) == Operation.ADD || temp.parts.get(i) == Operation.SUB) {
				Calculable a = (Calculable) temp.parts.get(i - 1);
				Calculable b = (Calculable) temp.parts.get(i + 1);
				Operation op = (Operation) temp.parts.get(i);

				// System.out.println(a+" " +op +" " +b);
				temp.parts.set(i, new Number(op.doOperation(a.getValue(), b.getValue())));
				temp.parts.remove(i - 1);
				temp.parts.remove(i);
				i = i - 2;
			}
		}

		return ((Calculable) temp.parts.get(0)).getValue();
	}

	public double getValue(double varval) {
		if (isConstant())
			return getValue();
		Phrase temp = new Phrase(this);
		Number num = new Number(varval);
		temp.replaceNullWith(num);

		return temp.getValue();
	}

	private void replaceNullWith(Number num) {
		for (int i = 0; i < parts.size(); i++) {
			Object o = parts.get(i);
			if (o == null) {
				parts.set(i, num);
			} else if (o.getClass() == Phrase.class) {
				((Phrase) o).replaceNullWith(num);
			}

		}
	}
}