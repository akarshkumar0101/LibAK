package math;

import java.util.Random;

public class AKRandom extends Random {

	private static final long serialVersionUID = 2530172263479519602L;

	public AKRandom() {
		super();
	}

	public AKRandom(long seed) {
		super(seed);
	}

	public boolean nextRandomChance(double chance) {
		return AKRandom.randomChance(chance, this);
	}

	public static boolean randomChance(double chance, Random random) {
		return random.nextDouble() < chance;
	}

	public static boolean randomChance(double chance) {
		return Math.random() < chance;
	}

	public double nextRandomNumber(double higherBound) {
		return this.nextRandomNumber(0, higherBound);
	}

	public double nextRandomNumber(double lowerBound, double higherBound) {
		return AKRandom.randomNumber(lowerBound, higherBound, this);
	}

	public static double randomNumber(double higherBound, Random random) {
		return AKRandom.randomNumber(0, higherBound, random);
	}

	public static double randomNumber(double lowerBound, double higherBound, Random random) {
		return AKMath.scale(random.nextDouble(), 0, 1, lowerBound, higherBound);
	}

	public static double randomNumber(double higherBound) {
		return AKRandom.randomNumber(0, higherBound);
	}

	public static double randomNumber(double lowerBound, double higherBound) {
		return AKMath.scale(Math.random(), 0, 1, lowerBound, higherBound);
	}
}
