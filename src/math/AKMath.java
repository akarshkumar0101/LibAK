package math;

public class AKMath {
	public static double scale(double num, double min1, double max1, double min2, double max2) {
		num -= min1;
		num *= (max2 - min2) / (max1 - min1);
		num += min2;
		return num;
	}

	public static double dist(double x1, double y1, double x2, double y2) {
		return java.lang.Math.sqrt(java.lang.Math.pow(x2 - x1, 2) + java.lang.Math.pow(y2 - y1, 2));
	}

	public static double dist(double[] point1, double[] point2) {
		return AKMath.dist(point1[0], point1[1], point2[0], point2[1]);
	}

	public static double sqr(double a) {
		return a * a;
	}

	public static double round(double a, double nearestDec, double offsetFromZero) {
		a -= offsetFromZero;
		a /= nearestDec;
		a = Math.floor(a + .5);
		a *= nearestDec;
		a += offsetFromZero;
		return a;
	}

}
