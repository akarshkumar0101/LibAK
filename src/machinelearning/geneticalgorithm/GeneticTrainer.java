package machinelearning.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;

import data.tuple.Tuple2D;
import math.AKRandom;

//C = chromosome (the genetic encoding of solution)
public interface GeneticTrainer<C> {

	public abstract double calculateFitness(C a, GAEnvironment<C> env);

	public default List<Double> calculateFitness(List<C> as, GAEnvironment<C> env) {
		List<Double> fitnesses = new ArrayList<>(as.size());
		for (int i = 0; i < as.size(); i++) {
			fitnesses.add(this.calculateFitness(as.get(i), env));
		}
		return fitnesses;
	}

	public abstract double getMutationChance(C a, GAEnvironment<C> env);

	public abstract double getCrossoverChance(Tuple2D<C, C> partners, GAEnvironment<C> env);

	public abstract C generateRandom(GAEnvironment<C> env);

	public abstract C crossover(C a, C b, GAEnvironment<C> env);

	public abstract C mutate(C a, GAEnvironment<C> env);

	public abstract List<Tuple2D<C, C>> selectCrossoverPartners(List<C> population, GAEnvironment<C> env);

	public abstract List<C> killOff(List<C> population, int numToKill, GAEnvironment<C> env);

	// stochastic universal sampling
	public default List<Tuple2D<C, C>> selectCrossoverPartnersSUS(List<C> population, int numCrossovers,
			GAEnvironment<C> env) {

		double fitnessOffset = Double.MAX_VALUE;
		for (C c : population) {
			double fit = env.getFitnesses().get(c);
			if (fit < fitnessOffset) {
				fitnessOffset = fit;
			}
		}
		if (fitnessOffset < 0) {
			fitnessOffset *= -1;
		} else {
			fitnessOffset = 0;
		}
		fitnessOffset += 0.1;

		// calculate total fitness
		double totalFitness = 0;
		for (C c : population) {
			totalFitness += env.getFitnesses().get(c) + fitnessOffset;
		}

		List<Tuple2D<C, C>> partners = new ArrayList<>(numCrossovers);

		for (int i = 0; i < numCrossovers; i++) {
			double pick1Fit = AKRandom.randomNumber(0, totalFitness);
			double pick2Fit = (pick1Fit + totalFitness / 2) % totalFitness;

			C a = null, b = null;
			double currentFitAt = 0;
			for (C c : population) {
				double fit = env.getFitnesses().get(c) + fitnessOffset;
				currentFitAt += fit;

				if (currentFitAt > pick1Fit && a == null) {
					a = c;
				}
				if (currentFitAt > pick2Fit && b == null) {
					b = c;
				}
				if (a != null && b != null) {
					break;
				}
			}
			if (a == null || b == null) {
				System.out.println("rip");
			}
			partners.add(new Tuple2D<>(a, b));
		}
		return partners;

	}

	public default List<Tuple2D<C, C>> selectCrossoverPartnersRandomly(List<C> population, int numCrossovers,
			GAEnvironment<C> env) {

		ArrayList<Tuple2D<C, C>> partners = new ArrayList<>(numCrossovers);

		for (int i = 0; i < numCrossovers; i++) {
			C a = population.get((int) AKRandom.randomNumber(0, population.size()));
			C b = population.get((int) AKRandom.randomNumber(0, population.size()));
			partners.add(new Tuple2D<>(a, b));
		}

		return partners;
	}

	public default List<C> killOffWorst(List<C> population, int numToKill, GAEnvironment<C> env) {
		List<C> killed = new ArrayList<>(numToKill);

		for (int i = 0; i < numToKill; i++) {
			C leastFitC = null;
			double leastfitness = Double.MAX_VALUE;
			for (C c : population) {
				if (!killed.contains(c)) {
					if (env.getFitnesses().containsKey(c)) {
						double fitness = env.getFitnesses().get(c);
						if (fitness < leastfitness) {
							leastfitness = fitness;
							leastFitC = c;
						}
					}
				}
			}
			killed.add(leastFitC);
		}

		return killed;

	}

}
