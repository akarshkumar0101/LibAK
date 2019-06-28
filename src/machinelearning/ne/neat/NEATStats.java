package machinelearning.ne.neat;

import data.tuple.Tuple2D;
import machinelearning.ne.neat.genome.Genome;

public class NEATStats {

	// similarity parameters for species calculation
	public double c1 = 1.0;
	public double c2 = 1.0;
	public double c3 = 0.4;
	public double deltaThreshold = 3.0;

	// general GA parameters
	public double percentPopulationToKill = 0.0;
	public double crossoverProbability = 0.0;

	// mutation parameters
	public double weightShiftStrengh = 0.0;
	public double weightRandomizeStrengh = 0.0;

	// mutation probabilities
	public double mutationProbability = 0.0;

	public double addConnectionProbability = 0.01;
	public double addNodeProbability = 0.1;
	public double weightShiftProbability = 0.02;
	public double weightRandomizeProbability = 0.02;
	public double toggleConnectionProbability = 0.0;

	public double getC1(NEAT neat) {
		return this.c1;
	}

	public double getC2(NEAT neat) {
		return this.c2;
	}

	public double getC3(NEAT neat) {
		return this.c3;
	}

	public double getDeltaThreshold(NEAT neat) {
		return this.deltaThreshold;
	}

	public double getPercentPopulationToKill(NEAT neat) {
		return this.percentPopulationToKill;
	}

	public double getCrossoverProbability(Tuple2D<Genome, Genome> partners, NEAT neat) {
		return this.crossoverProbability;
	}

	public double getWeightShiftStrengh(NEAT neat) {
		return this.weightShiftStrengh;
	}

	public double getWeightRandomizeStrengh(NEAT neat) {
		return this.weightRandomizeStrengh;
	}

	public double getMutationProbability(Genome a, NEAT neat) {
		return this.mutationProbability;
	}

	public double getAddConnectionProbability(NEAT neat) {
		return this.addConnectionProbability;
	}

	public double getAddNodeProbability(NEAT neat) {
		return this.addNodeProbability;
	}

	public double getWeightShiftProbability(NEAT neat) {
		return this.weightShiftProbability;
	}

	public double getWeightRandomizeProbability(NEAT neat) {
		return this.weightRandomizeProbability;
	}

	public double getToggleConnectionProbability(NEAT neat) {
		return this.toggleConnectionProbability;
	}

}
