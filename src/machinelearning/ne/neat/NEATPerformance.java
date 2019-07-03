package machinelearning.ne.neat;

import machinelearning.ne.neat.genome.Genome;

public class NEATPerformance {

	public static double averageFitness(NEAT neat) {
		double avg = 0;
		for (Genome geno : neat) {
			avg += neat.getFitnesses().get(geno);
		}
		avg /= neat.size();
		return avg;
	}

	public static Genome bestGenome(NEAT neat) {
		double maxFitness = -Double.MAX_VALUE;
		Genome bestGeno = null;
		for (Genome geno : neat) {
			double fit = neat.getFitnesses().get(geno);

			if (fit > maxFitness) {
				maxFitness = fit;
				bestGeno = geno;
			}
		}
		return bestGeno;
	}

	public static double averageHiddenNodes(NEAT neat) {
		double hiddenNodes = 0;
		for (Genome geno : neat) {
			hiddenNodes += geno.getNumHiddenNodes();
		}
		hiddenNodes /= neat.size();
		return hiddenNodes;
	}
}
