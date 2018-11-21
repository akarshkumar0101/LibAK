package math;

public class Math {
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
		return dist(point1[0], point1[1], point2[0], point2[1]);
	}

	public static long intFactorial(long a) {
		long ans = 1;
		if (a == 0)
			return 1;
		for (long i = a; i > 1; i--) {
			ans *= i;
		}
		return ans;
	}

	public static long nCr(long n, long r) {
		return (intFactorial(n)) / (intFactorial(r) * intFactorial(n - r));
	}

	public static long nPr(long n, long r) {
		return (intFactorial(n)) / intFactorial(n - r);
	}

}
