package machinelearning.ne.neat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import data.tuple.Tuple2D;
import machinelearning.ne.neat.genome.Genome;
import math.AKRandom;
import util.CombinedIterator;

public class NEAT implements Iterable<Genome> {

	// private Map<Genome, List<Genome>> species;
	public final List<Species> species;

	private final Map<Genome, Tuple2D<Double, Double>> fitnesses;

	private NEATTrainer trainer;

	private int preferredPopulationSize;

	private int currentGeneration;

	public int currentInnovationNumber = 0;

	private NEATStats neatStats;

	public NEAT(int preferredPopulationSize, NEATTrainer trainer) {

		this.setTrainer(trainer);

		this.species = new ArrayList<>();
		this.fitnesses = new HashMap<>();

		this.preferredPopulationSize = preferredPopulationSize;
		this.currentGeneration = 0;
		this.currentInnovationNumber = 0;

		
		this.setNeatStats(this.trainer.calculateStatsForGeneration(this));
		
		this.populateRest();

		this.calculateFitnesses();

		this.calculateAdjustedFitnesses();

		this.sortSpeciesByFitness();
	}

	public void runGeneration() {
		this.setNeatStats(this.trainer.calculateStatsForGeneration(this));

		// kill off
		this.killInSpecies(this.neatStats.getPercentPopulationToKill(this));

		this.cleanupFitness();

		// get offspring from cross
		List<Genome> offspring = this.crossPopulation((int) (this.preferredPopulationSize * 0.75));
		List<Genome> clonedOffspring = this.clonePopulation((int) (this.preferredPopulationSize * 0.25));
		offspring.addAll(clonedOffspring);

		// mutate
		this.mutatePopulation(offspring);

		// add offspring

		for (Species spec : species) {
			spec.clear();
		}
		this.putIntoSpecies(offspring);
		this.populateRest();

		this.killExtinctSpecies();

		this.calculateFitnesses();

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
		for (int i = this.size(); i < this.preferredPopulationSize; i++) {
			Genome geno = this.trainer.generateRandom(this);
			putIntoSpecies(geno);
		}
	}

	private void putIntoSpecies(Genome geno) {
		for (Species spec : this.species) {
			if (trainer.areSimilar(geno, spec.getRepresentative(), this)) {
				// found it
				spec.add(geno);
				return;
			}
		}
		this.species.add(new Species(geno));
	}

	private void putIntoSpecies(List<Genome> genos) {
		for (Genome geno : genos) {
			putIntoSpecies(geno);
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
			int numToKill = (int) Math.floor((double) percentKilled * spec.size());

			spec.sort(new Comparator<Genome>() {
				@Override
				public int compare(Genome o1, Genome o2) {
					double ret = fitnesses.get(o2).getA() - fitnesses.get(o1).getB();
					return ret > 0 ? 1 : ret < 0 ? -1 : 0;
				}
			});
			int size = spec.size();
			for (int i = spec.size() - 1; i > size - numToKill - 1; i--) {
				spec.remove(i);
			}
			if (!spec.contains(spec.getRepresentative())) {
				spec.assignNewRandomRepresentative();
			}

			totalNumKilled += numToKill;
		}

		return totalNumKilled;
	}

	public void calculateFitnesses() {
		for (Species spec : species) {
			List<Double> fits = this.trainer.calculateFitness(spec, this);

			for (int i = 0; i < spec.size(); i++) {
				this.fitnesses.put(spec.get(i), new Tuple2D<>(fits.get(i), 0.0));
				spec.get(i).fitness = fits.get(i);
			}
		}
	}

	// clean up fitnesses hashmap before this
	// represented by i, proper way in paper
	private void calculateAdjustedFitnesses() {
		for (Genome geno : this) {
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

	private List<Genome> crossPopulation(int numberToProduce) {
		List<Genome> offspring = new ArrayList<>();

		double totalSumAdjustedFitnesses = 0.0;
		for (Genome geno : this) {
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

	private List<Genome> clonePopulation(int numberToProduce) {
		List<Genome> offspring = new ArrayList<>();

		double totalSumAdjustedFitnesses = 0.0;
		for (Genome geno : this) {
			totalSumAdjustedFitnesses += this.fitnesses.get(geno).getB();
		}

		for (Species spec : this.species) {

			double speciesSumAdjustedFitnesses = 0.0;
			for (Genome geno : spec) {
				speciesSumAdjustedFitnesses += this.fitnesses.get(geno).getB();
			}
			int numOffspring = (int) (speciesSumAdjustedFitnesses * numberToProduce / totalSumAdjustedFitnesses);

			for (int i = 0; i < numOffspring; i++) {
				Genome geno = spec.selectGenome().clone();
				offspring.add(geno);
			}
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

	private int numSimilarTo(Genome i) {
		int numSimilar = 0;
		for (Genome geno : this) {
			if (this.trainer.areSimilar(i, geno, this)) {
				numSimilar++;
			}
		}
		return numSimilar;
	}

	private void killExtinctSpecies() {
		this.species.removeIf(spec -> spec.size() <= 1);
	}

	private void cleanupFitness() {
		this.fitnesses.entrySet().removeIf(e -> !this.contains(e.getKey()));
	}

	public void setTrainer(NEATTrainer trainer) {
		this.trainer = trainer;
	}

	public int getPreferredPopulationSize() {
		return this.preferredPopulationSize;
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
		for (Genome geno : this) {
			avg += this.fitnesses.get(geno).getA();
		}
		avg /= this.size();
		return avg;
	}

	public Genome bestGenome() {
		double maxFitness = -Double.MAX_VALUE;
		Genome bestGeno = null;
		for (Genome geno : this) {
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
		for (Genome geno : this) {
			hiddenNodes += geno.getNumHiddenNodes();
		}
		hiddenNodes /= this.size();
		return hiddenNodes;

	}

	public int size() {
		int size = 0;
		for (Species spec : species) {
			size += spec.size();
		}

		return size;
	}

	public boolean contains(Genome g) {
		for (Species spec : species) {
			if (spec.contains(g)) {
				return true;
			}
		}

		return false;
	}
	List<Iterable<Genome>> iterables;
	@Override
	public Iterator<Genome> iterator() {
		iterables = new LinkedList<>();
		iterables.addAll(species);
		return new CombinedIterator<Genome>(iterables);
	}
}
