package math;

import java.util.HashMap;

public class Factorization {
	public static HashMap<Integer, Integer> factorize(int num) {
		return factorize(num, new HashMap<>());
	}

	private static HashMap<Integer, Integer> factorize(int num, HashMap<Integer, Integer> factorization) {
		if (Prime.isPrime(num)) {
			addFactor(num, factorization);
			return factorization;
		} else {
			for (int i = 2; i <= java.lang.Math.sqrt(num); i++) {
				if (Prime.isPrime(i) && num % i == 0) {
					addFactor(i, factorization);
					return factorize(num / i, factorization);
				}
			}
			return null;
		}
	}

	private static void addFactor(int factor, HashMap<Integer, Integer> factorization) {
		if (factorization.containsKey(factor)) {
			factorization.put(factor, factorization.get(factor) + 1);
		} else {
			factorization.put(factor, 1);
		}
	}

}
