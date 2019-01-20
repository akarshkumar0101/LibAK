package math;

public class AKRandom {

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
