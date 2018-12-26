package math;

public class Probability {

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
