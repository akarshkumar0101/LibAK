package machinelearning.ne.neat;

import java.util.ArrayList;

import machinelearning.ne.neat.genome.Genome;
import math.AKRandom;

public class Species extends ArrayList<Genome> {

	private static final long serialVersionUID = -8114693581105898796L;

	private Genome representative;
	
	public double totalAdjustedFitness;

	public Species(Genome geno) {
		this.representative = geno;
		this.add(geno);
	}

	public boolean isExtinct() {
		return this.size() <= 1;
	}

	public void assignNewRandomRepresentative() {
		this.representative = null;
		if (!this.isEmpty()) {
			int randIndex = (int) (Math.random() * this.size());
			this.representative = this.get(randIndex);
		}
	}

	public Genome getRepresentative() {
		return this.representative;
	}

	public void setRepresentative(Genome representative) {
		this.representative = representative;
	}

	public Genome selectGenome() {
		double fitnessOffset = Double.MAX_VALUE;
		for (Genome c : this) {
			double fit = c.fitness;
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
		for (Genome c : this) {
			totalFitness += c.fitness + fitnessOffset;
		}

		// System.out.println(fitnessOffset);

		double pick1Fit = AKRandom.randomNumber(0, totalFitness);

		Genome a = null;
		double currentFitAt = 0;
		for (Genome c : this) {
			double fit = c.fitness + fitnessOffset;
			currentFitAt += fit;

			if (currentFitAt > pick1Fit && a == null) {
				a = c;
			}

			if (a != null) {
				break;
			}
		}
		return a;
	}

	public Genome selectGenomeRandom() {
		return this.get((int) (Math.random() * this.size()));
	}

	public Genome giveBaby(NEAT neat, NEATTrainer trainer) {
		Genome baby;
		if (AKRandom.randomChance(0.25)) {// 25% of the time there is no crossover and the child is simply a clone of a
											// random(ish) player
			baby = this.selectGenome().clone();
		} else {// 75% of the time do crossover

			// get 2 random(ish) parents
			Genome parent1 = this.selectGenome();
			Genome parent2 = this.selectGenome();

			// the crossover function expects the highest fitness parent to be the object
			// and the lowest as the argument
			baby = trainer.crossover(parent1, parent2, neat);
		}
		baby.cleanup();
		return baby;
	}

}
