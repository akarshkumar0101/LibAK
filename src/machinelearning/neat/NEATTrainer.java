package machinelearning.neat;

import java.util.List;

import array.DoubleArrays;
import data.tuple.Tuple2D;
import machinelearning.geneticalgorithm.GAEnvironment;
import machinelearning.geneticalgorithm.GeneticTrainer;
import machinelearning.neuralnet.NeuralNetwork;
import math.AKRandom;

public interface NEATTrainer extends GeneticTrainer<NeuralNetwork> {

	@Override
	public default double getMutationChance(NeuralNetwork a, GAEnvironment<NeuralNetwork> env) {
		if (env.getPopulation().indexOf(a) > env.getPopulation().size() / 2)
			return .05;
		else
			return 0;
	}

	@Override
	public default double getCrossoverChance(Tuple2D<NeuralNetwork, NeuralNetwork> partners,
			GAEnvironment<NeuralNetwork> env) {
		return .7;
	}

	@Override
	public default NeuralNetwork crossover(NeuralNetwork a, NeuralNetwork b, GAEnvironment<NeuralNetwork> env) {
		double[][][] weights = DoubleArrays.deepCopy(a.weights);
		double[][] biases = DoubleArrays.deepCopy(a.biases);

		for (int x = 0; x < weights.length; x++) {
			for (int y = 0; y < weights[x].length; y++) {
				for (int z = 0; z < weights[x][y].length; z++) {
					if (AKRandom.randomChance(.5)) {
						weights[x][y][z] = b.weights[x][y][z];
					}
				}
			}
		}
		for (int x = 0; x < biases.length; x++) {
			for (int y = 0; y < biases[x].length; y++) {
				if (AKRandom.randomChance(.5)) {
					biases[x][y] = b.biases[x][y];
				}
			}
		}
		return new NeuralNetwork(weights, biases);

	}

	@Override
	public default NeuralNetwork mutate(NeuralNetwork a, GAEnvironment<NeuralNetwork> env) {
		double[][][] weights = DoubleArrays.deepCopy(a.weights);
		double[][] biases = DoubleArrays.deepCopy(a.biases);

		for (int x = 0; x < weights.length; x++) {
			for (int y = 0; y < weights[x].length; y++) {
				for (int z = 0; z < weights[x][y].length; z++) {
					if (AKRandom.randomChance(.05)) {
						weights[x][y][z] = AKRandom.randomNumber(-1, 1);
					}
				}
			}
		}
		for (int x = 0; x < biases.length; x++) {
			for (int y = 0; y < biases[x].length; y++) {
				biases[x][y] = AKRandom.randomNumber(-1, 1);
			}
		}
		return new NeuralNetwork(weights, biases);
	}

	@Override
	public default List<Tuple2D<NeuralNetwork, NeuralNetwork>> selectCrossoverPartners(List<NeuralNetwork> population,
			GAEnvironment<NeuralNetwork> env) {
		return this.selectCrossoverPartnersSUS(population, env.getPopulation().size() / 3, env);
	}

	@Override
	public default List<NeuralNetwork> killOff(List<NeuralNetwork> population, int numToKill,
			GAEnvironment<NeuralNetwork> env) {
		return this.killOffWorst(population, numToKill, env);
	}

}
