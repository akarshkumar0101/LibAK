package math;

public class Prime {
	public static boolean isPrime(long num) {
		if (num < 2)
			return false;
		for (long i = 2; i <= java.lang.Math.sqrt(num); i++) {
			if (num % i == 0)
				return false;
		}
		return true;
	}
}
