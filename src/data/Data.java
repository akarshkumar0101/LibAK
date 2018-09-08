package data;

public class Data {
	public static boolean satisfiesEquavilance(Object a, Object b, boolean identical) {
		return ((a == b) || (a != null && a.equals(b))) ? true : false;
	}
}
