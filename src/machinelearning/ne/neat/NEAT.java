package machinelearning.ne.neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.tuple.Tuple2D;
import machinelearning.ne.neat.genome.Genome;
import math.AKRandom;

public class NEAT {

	private final List<Genome> population;

	// private Map<Genome, List<Genome>> species;
	private final List<Species> species;

	private final Map<Genome, Tuple2D<Double, Double>> fitnesses;

	private NEATTrainer trainer;

	private int preferredPopulationSize;

	private int currentGeneration;

	private int currentInnovationNumber = 0;

	public NEAT(int preferredPopulationSize, NEATTrainer trainer) {
		this.preferredPopulationSize = preferredPopulationSize;
		this.setTrainer(trainer);

		this.population = new ArrayList<>(preferredPopulationSize);
		this.species = new ArrayList<>();
		this.fitnesses = new HashMap<>();

		this.populateRest();

		this.currentGeneration = 0;
		this.currentInnovationNumber = 0;
	}

	public void runGeneration() {
		this.populateRest();

		this.calculateFitnesses();

		this.groupIntoSpecies(this.population);

		this.calculateAdjustedFitnesses();

		this.sortSpeciesByFitness();

		// kill off
		this.killInSpecies(this.trainer.getKillPercent());

		this.cleanupFitness();

		// get offspring from cross
		List<Genome> offspring = this.crossPopulation();

		// mutate
		this.mutatePopulation();

		// add offspring
		this.population.addAll(offspring);

		this.killExtinctSpecies();

		this.currentGeneration++;
	}

	/**
	 * Populates the rest of the population list with random new genomes from
	 * trainer.generateRandom().
	 */
	private void populateRest() {
		for (int i = this.population.size(); i < this.preferredPopulationSize; i++) {
			this.population.add(this.trainer.generateRandom(this));
		}
	}

	/**
	 * Groups genos into the corresponding species based on delta threshold. If a
	 * geno does not fit into an existing species, a new species is created.
	 *
	 * @param genos
	 */
	private void groupIntoSpecies(List<Genome> genos) {
		for (Genome geno : genos) {
			Species spec = this.calculateCorrespondingSpecies(geno);
			if (spec == null) {
				this.createNewSpecies(geno);
			} else {
				if (!spec.contains(geno)) {
					spec.add(geno);
				}
			}
		}
	}

	/**
	 * Sorts all of the species list from largest to smallest fitness
	 */
	private void sortSpeciesByFitness() {
		for (Species spec : this.species) {
			spec.sort((o1, o2) -> {
				double ret = NEAT.this.fitnesses.get(o2).getB() - NEAT.this.fitnesses.get(o1).getB();
				return ret < 0 ? -1 : ret > 0 ? 1 : 0;
			});
		}
	}

	/**
	 * Kills of percentKilled percent in each species. It kills the worst of
	 * species. Assumes that sortSpeciesByFitness() has been called prior to it.
	 * Returns the number of genomes actually killed.
	 *
	 * @param percentKilled
	 * @return
	 */
	private int killInSpecies(double percentKilled) {
		int totalNumKilled = 0;
		for (Species spec : this.species) {
			int numToKill = (int) Math.ceil(percentKilled * spec.size());

			List<Genome> killed = trainer.killOff(spec, numToKill, this);
			
			for(Genome geno: killed) {
				this.killGenome(geno);
			}

			totalNumKilled += killed.size();
		}

		return totalNumKilled;
	}

	/**
	 * Kills the genome from all accounts (population and species)
	 *
	 * @param geno
	 */
	private void killGenome(Genome geno) {
		this.population.remove(geno);

		for (Species spec : this.species) {
			if (spec.contains(geno)) {
				spec.remove(geno);

				if (spec.getRepresentative() == geno) {
					spec.assignNewRandomRepresentative();
				}
			}
		}
	}

	public void calculateFitnesses() {
		List<Double> fits = this.trainer.calculateFitness(this.population, this);

		for (int i = 0; i < this.population.size(); i++) {
			this.fitnesses.put(this.population.get(i), new Tuple2D<>(fits.get(i), 0.0));
		}
	}

	// clean up fitnesses hashmap before this
	// represented by i, proper way in paper
	private void calculateAdjustedFitnesses() {

		for (Genome geno : this.population) {
			int numSimilar = this.numSimilarTo(geno);

			Tuple2D<Double, Double> oriFitness = this.fitnesses.get(geno);
			double adjustedFitness = oriFitness.getA() / numSimilar;
			Tuple2D<Double, Double> newFitness = new Tuple2D<>(oriFitness.getA(), adjustedFitness);
			this.fitnesses.put(geno, newFitness);
		}
	}

	// represented by species, not by i
	private void calculateAdjustedFitnesses2() {
		for (Species spec : this.species) {
			int numInSpecies = spec.size();

			for (Genome geno : spec) {
				Tuple2D<Double, Double> oriFitness = this.fitnesses.get(geno);
				double adjustedFitness = oriFitness.getA() / numInSpecies;
				Tuple2D<Double, Double> newFitness = new Tuple2D<>(oriFitness.getA(), adjustedFitness);
				this.fitnesses.put(geno, newFitness);
			}
		}
	}

	private void sortPopulation() {
		Collections.sort(this.population, (o1, o2) -> {
			double dec = this.fitnesses.get(o2).getA() - this.fitnesses.get(o1).getA();
			return dec == 0 ? 0 : dec > 0 ? 1 : -1;
		});
	}

	// TODO make sure each species gets only how many offsprings it deserves
	private List<Genome> crossPopulation() {
		List<Genome> offspring = new ArrayList<>();

		double totalSumAdjustedFitnesses = 0.0;
		for (Genome geno : this.population) {
			totalSumAdjustedFitnesses += this.fitnesses.get(geno).getB();
		}

		for (Species spec : this.species) {

			double speciesSumAdjustedFitnesses = 0.0;
			for (Genome geno : spec) {
				speciesSumAdjustedFitnesses += this.fitnesses.get(geno).getB();
			}
			int numOffspring = (int) (speciesSumAdjustedFitnesses / totalSumAdjustedFitnesses);

			List<Tuple2D<Genome, Genome>> crossoverPartners = this.trainer.selectCrossoverPartners(spec, this,
					numOffspring);

			for (Tuple2D<Genome, Genome> partners : crossoverPartners) {
				if (AKRandom.randomChance(this.trainer.getCrossoverChance(partners, this))) {
					Genome geno = this.trainer.crossover(partners.getA(), partners.getB(), this);
					offspring.add(geno);
				}
			}

		}

		return offspring;
	}

	private void mutatePopulation() {
		for (int i = 0; i < this.population.size(); i++) {
			Genome geno = this.population.get(i);

			if (AKRandom.randomChance(this.trainer.getMutationChance(geno, this))) {
				Genome newgeno = this.trainer.mutate(geno, this);
				this.population.remove(i);
				this.population.add(i, newgeno);
			}
		}
	}

	/*
	 * private void selectSurvivors() { int numShouldBeKilled =
	 * this.population.size() - this.preferredPopulationSize; List<Genome> killed =
	 * this.trainer.killOff(this.population, numShouldBeKilled, this); for (Genome
	 * geno : killed) { this.population.remove(geno); } }
	 */

	private Species calculateCorrespondingSpecies(Genome geno) {
		for (Species spec : this.species) {
			Genome rep = spec.getRepresentative();
			if (NEATTrainer.similarity(geno, rep, this.trainer.getC1(), this.trainer.getC2(),
					this.trainer.getC3()) <= this.trainer.getDeltaThreshold())
				return spec;
		}

		return null;
	}

	private int numSimilarTo(Genome i) {
		int numSimilar = 0;
		for (Genome geno : this.population) {
			if (NEATTrainer.similarity(i, geno, this.trainer.getC1(), this.trainer.getC2(),
					this.trainer.getC3()) <= this.trainer.getDeltaThreshold()) {
				numSimilar++;
			}
		}
		return numSimilar;
	}

	private void createNewSpecies(Genome geno) {
		this.species.add(new Species(geno));
	}

	private void killExtinctSpecies() {
		this.species.removeIf(spec -> spec.size() <= 1);
	}

	private void cleanupFitness() {
		this.fitnesses.entrySet().removeIf(e -> !this.population.contains(e.getKey()));
	}

	public void setTrainer(NEATTrainer trainer) {
		this.trainer = trainer;
	}

	public int getPreferredPopulationSize() {
		return this.preferredPopulationSize;
	}

	public List<Genome> getPopulation() {
		return this.population;
	}

	public Map<Genome, Tuple2D<Double, Double>> getFitnesses() {
		return this.fitnesses;
	}

	public int getCurrentGeneration() {
		return this.currentGeneration;
	}

	public int getCurrentInnovationNumber() {
		return this.currentInnovationNumber;
	}

}
