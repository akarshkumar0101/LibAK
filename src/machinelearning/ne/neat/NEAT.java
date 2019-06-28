package machinelearning.ne.neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.tuple.Tuple2D;
import machinelearning.ne.neat.genome.Genome;
import math.AKRandom;

public class NEAT {

	private final List<Genome> population;

	// private Map<Genome, List<Genome>> species;
	public final List<Species> species;

	private final Map<Genome, Tuple2D<Double, Double>> fitnesses;

	private NEATTrainer trainer;

	private int preferredPopulationSize;

	private int currentGeneration;

	private int currentInnovationNumber = 0;

	private NEATStats neatStats;

	public NEAT(int preferredPopulationSize, NEATTrainer trainer) {
		this.preferredPopulationSize = preferredPopulationSize;
		this.setTrainer(trainer);

		this.population = new ArrayList<>(preferredPopulationSize);
		this.species = new ArrayList<>();
		this.fitnesses = new HashMap<>();

		this.populateRest();

		this.currentGeneration = 0;
		this.currentInnovationNumber = 0;
		this.setNeatStats(this.trainer.calculateStatsForGeneration(this));

		this.calculateFitnesses();

		this.groupIntoSpecies(this.population);

		this.calculateAdjustedFitnesses();

		this.sortSpeciesByFitness();

	}

	public void runGeneration() {
		this.setNeatStats(this.trainer.calculateStatsForGeneration(this));

		// put this stuff at the end of the method

		// kill off
		this.killInSpecies(this.neatStats.getPercentPopulationToKill(this));

		this.cleanupFitness();

		// get offspring from cross
		List<Genome> offspring = this.crossPopulationCBullet((int) (this.preferredPopulationSize * 0.75));
		List<Genome> clonedOffspring = this.clonePopulationCBullet((int) (this.preferredPopulationSize * 0.25));
		offspring.addAll(clonedOffspring);

		// mutate
		this.mutatePopulation(offspring);

		// add offspring
		this.population.clear();
		this.population.addAll(offspring);

		this.killExtinctSpecies();

		this.populateRest();

		this.calculateFitnesses();

		this.groupIntoSpecies(this.population);

		this.calculateAdjustedFitnesses();

		this.sortSpeciesByFitness();

		this.currentGeneration++;

		this.generationalInnovations.clear();
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
		for (Species spec : this.species) {
			spec.clear();
		}
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

			List<Genome> killed = this.trainer.killOff(spec, numToKill, this);

			for (Genome geno : killed) {
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
			this.population.get(i).fitness = fits.get(i);
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
			int numOffspring = (int) (speciesSumAdjustedFitnesses
					* (this.preferredPopulationSize - this.population.size()) / totalSumAdjustedFitnesses);

			List<Tuple2D<Genome, Genome>> crossoverPartners = this.trainer.selectCrossoverPartners(spec, this,
					numOffspring);

			for (Tuple2D<Genome, Genome> partners : crossoverPartners) {
				if (AKRandom.randomChance(this.neatStats.getCrossoverProbability(partners, this))) {
					Genome geno = this.trainer.crossover(partners.getA(), partners.getB(), this);
					offspring.add(geno);
				}
			}

		}

		return offspring;
	}

	private List<Genome> crossPopulationCBullet(int numberToProduce) {
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
			int numOffspring = (int) (speciesSumAdjustedFitnesses * numberToProduce / totalSumAdjustedFitnesses);

			for (int i = 0; i < numOffspring; i++) {
				offspring.add(spec.giveBaby(this, this.trainer));
			}
		}

		return offspring;
	}

	private List<Genome> clonePopulationCBullet(int numberToProduce) {
		List<Genome> offspring = new ArrayList<>();

		for (int i = 0; i < numberToProduce; i++) {
			Genome geno = this.population.get((int) (Math.random() * this.population.size())).clone();
			offspring.add(geno);
		}

		return offspring;

	}

	private void mutatePopulation(List<Genome> population) {
		for (int i = 0; i < population.size(); i++) {
			Genome geno = population.get(i);

			if (AKRandom.randomChance(this.neatStats.getMutationProbability(geno, this))) {
				Genome newgeno = this.trainer.mutate(geno, this);
				population.remove(i);
				population.add(i, newgeno);
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
			if (this.trainer.areSimilar(geno, rep, this))
				return spec;
		}

		return null;
	}

	private int numSimilarTo(Genome i) {
		int numSimilar = 0;
		for (Genome geno : this.population) {
			if (this.trainer.areSimilar(i, geno, this)) {
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

	public int accessAndIncrementCurrentInnovationNumber() {
		return this.currentInnovationNumber++;
	}

	private final HashMap<Tuple2D<Integer, Integer>, Integer> generationalInnovations = new HashMap<>();

	public int accessAndIncrementCurrentInnovationNumberSmart(int inputNodeID, int outputNodeID) {
		for (Tuple2D<Integer, Integer> structure : this.generationalInnovations.keySet()) {
			if (structure.getA() == inputNodeID && structure.getB() == outputNodeID)
				// found structure already in generation
				return this.generationalInnovations.get(structure);
		}
		int innov = this.currentInnovationNumber++;

		this.generationalInnovations.put(new Tuple2D<>(inputNodeID, outputNodeID), innov);

		return innov;
	}

	public NEATStats getNeatStats() {
		return this.neatStats;
	}

	public void setNeatStats(NEATStats neatStats) {
		this.neatStats = neatStats;
	}

	public double averageFitness() {
		double avg = 0;
		for (Genome geno : this.population) {
			avg += this.fitnesses.get(geno).getA();
		}
		avg /= this.population.size();
		return avg;
	}

	public Genome bestGenome() {
		double maxFitness = -Double.MAX_VALUE;
		Genome bestGeno = null;
		for (Genome geno : this.population) {
			double fit = this.fitnesses.get(geno).getA();

			if (fit > maxFitness) {
				maxFitness = fit;
				bestGeno = geno;
			}
		}
		return bestGeno;
	}

	public double averageHiddenNodes() {
		double hiddenNodes = 0;
		for (Genome geno : this.population) {
			hiddenNodes += geno.getNumHiddenNodes();
		}
		hiddenNodes /= this.population.size();
		return hiddenNodes;

	}
}
