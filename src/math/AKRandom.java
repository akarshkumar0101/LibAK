package math;

public class AKRandom {

	public static boolean randomChance(double chance) {
		return Math.random() < chance;
	}
	public static double randomNumber(double lowerbound, double higherbound) {
		return AKMath.scale(Math.random(), 0, 1, lowerbound, higherbound);
	}
}
