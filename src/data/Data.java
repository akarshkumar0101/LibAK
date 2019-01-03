package data;

public class Data {
	// canEqual means a.equals(b) is also true
	public static boolean equals(Object a, Object b, boolean canEqual) {
		if (a == b)
			return true;
		else if (canEqual) {
			if (a != null && a.equals(b))
				return true;
		}
		return false;
	}
}
