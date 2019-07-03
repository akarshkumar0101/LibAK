package math;

import java.util.Random;

public class AKRandom {

	public static boolean randomChance(double chance, Random random) {
		return random.nextDouble() < chance;
	}

	public static double randomNumber(double higherBound, Random random) {
		return AKRandom.randomNumber(0, higherBound, random);
	}

	public static double randomNumber(double lowerBound, double higherBound, Random random) {
		return AKMath.scale(random.nextDouble(), 0, 1, lowerBound, higherBound);
	}

	public static boolean randomChance(double chance) {
		return Math.random() < chance;
	}

	public static double randomNumber(double higherBound) {
		return AKRandom.randomNumber(0, higherBound);
	}

	public static double randomNumber(double lowerBound, double higherBound) {
		return AKMath.scale(Math.random(), 0, 1, lowerBound, higherBound);
	}
}
