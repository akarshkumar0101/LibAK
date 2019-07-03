package machinelearning.ne.neat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import data.tuple.Tuple2D;
import machinelearning.ne.neat.genome.Genome;
import math.AKRandom;
import util.CombinedIterator;

public class NEAT implements Iterable<Genome> {

	// private Map<Genome, List<Genome>> species;
	public final List<Species> species;

	private final Map<Genome, Double> fitnesses;

	private NEATTrainer trainer;

	private int preferredPopulationSize;

	private int currentGeneration;

	public int currentInnovationNumber = 0;

	private NEATStats neatStats;

	List<Genome> population = null;
	List<Genome> nextGenPopulation = new ArrayList<Genome>();

	private Random random = new Random();

	public NEAT(int preferredPopulationSize, NEATTrainer trainer) {
		this.setTrainer(trainer);

		this.species = new ArrayList<>();
		this.fitnesses = new HashMap<>();

		this.preferredPopulationSize = preferredPopulationSize;
		this.currentGeneration = 0;
		this.currentInnovationNumber = 0;

		this.setNeatStats(this.trainer.calculateStatsForGeneration(this));

	}

	public void initialize() {
		for (int i = 0; i < preferredPopulationSize; i++) {
			nextGenPopulation.add(trainer.generateRandom(this));
		}

	}

	// AVERAGE FITNESS OF A SPECIES IS SAME AS THE TOTAL ADJUSTED FITNESSES

	// this will get the current population, find fitness and sort population into
	// species.
	// then it will use the current population to calculate the next generation
	// population.
	public void runGeneration() {
		population = nextGenPopulation;
		nextGenPopulation = new ArrayList<Genome>();

		// our generation is population variable
		// previous generation is in species so clear them

		for (Species spec : species) {
			spec.assignNewRandomRepresentative();
			spec.clear();
		}

		fitnesses.clear();

		calculateFitnesses(population);

		// Place genomes into species
		putIntoSpecies(population);

		// WE NEED TO NOT LET HALF OF THE SPECIES REPRODUCE, KILL THEM OFF OR SOMETHING

		for (Species spec : species) {
			// sort by fitness
			spec.sortByFitness(this);
			
			//kill off half
			// midPoint is second mid index if size is even, and the real mid index when odd
			int midPoint = spec.size() / 2;

			for (int i = spec.size() - 1; i >= midPoint; i--) {
				spec.remove(i);
			}
		}

		// Remove unused empty species
		removeExtinctSpecies();

		// put best genomes from each species into next generation
		for (Species spec : species) {
			Genome bestInSpec = spec.get(0);
			nextGenPopulation.add(bestInSpec);
		}
		System.out.println("Copied "+nextGenPopulation.size()+" champions from previous species.");

		int numOffspringNeeded = preferredPopulationSize - nextGenPopulation.size();

		// construct avgFitnessInSpecies
		Map<Species, Double> avgFitnessInSpecies = new HashMap<>(species.size());
		double sumAvgFitnessInSpecies = 0.0;
		for (Species spec : species) {
			double avgFitness = spec.calculateAverageFitness(this);
			avgFitnessInSpecies.put(spec, avgFitness);

			sumAvgFitnessInSpecies += avgFitness;
		}
		
		species.sort(new Comparator<Species>() {
			@Override
			public int compare(Species o1, Species o2) {
				return (int) Math.signum(avgFitnessInSpecies.get(o2)-avgFitnessInSpecies.get(o1));
			}
		});

		for (Species spec : species) {
			double percentFitnessMakeup = (avgFitnessInSpecies.get(spec) / sumAvgFitnessInSpecies);

			// round down always
			int numOffspring = (int) (percentFitnessMakeup * numOffspringNeeded);

			List<Genome> speciesOffSpring = getOffspringForSpecies(spec, numOffspring, avgFitnessInSpecies);

			mutatePopulation(speciesOffSpring);

			nextGenPopulation.addAll(speciesOffSpring);
		}

		// Fill rest of the population with brand new organisms
		while (nextGenPopulation.size() < preferredPopulationSize) {
			nextGenPopulation.add(trainer.generateRandom(this));
		}

		this.currentGeneration++;
		this.generationalInnovations.clear();
	}

	public List<Genome> getOffspringForSpecies(Species spec, int numOffspring,
			Map<Species, Double> avgFitnessInSpecies) {
		List<Genome> offspring = new ArrayList<>(numOffspring);

		// 25% of offspring are clones
		int quarterAmount = (int) (0.25 * numOffspring);
		for (int i = 0; i < quarterAmount; i++) {
			Genome geno = getWeightedRandom(spec, fitnesses, random);
			offspring.add(geno.clone());
		}

		for (int i = offspring.size(); i < numOffspring; i++) {
			Genome p1 = getWeightedRandom(spec, fitnesses, random);
			Genome p2 = null;

			// interspecies mating rate
			if (AKRandom.randomChance(0.001)) {
				Species randomSpec = getWeightedRandom(species, avgFitnessInSpecies, random);
				p2 = getWeightedRandom(randomSpec, fitnesses, random);
			} else {
				p2 = getWeightedRandom(spec, fitnesses, random);
			}

			Genome child = null;
			// use adjusted fitness here
			if (fitnesses.get(p1) >= fitnesses.get(p2)) {
				child = trainer.crossover(p1, p2, this);
			} else {
				child = trainer.crossover(p2, p1, this);
			}

			offspring.add(child);
		}

		return offspring;
	}

	private <T> T getWeightedRandom(List<T> list, Map<T, Double> fitnesses, Random random) {
		double totalWeight = 0.0;
		for (T t : list) {
			totalWeight += fitnesses.get(t);
		}

		double rand = AKRandom.randomNumber(totalWeight, random);

		double currentWeight = 0.0;
		for (T t : list) {
			currentWeight += fitnesses.get(t);
			if (currentWeight >= rand) {
				return t;
			}
		}
		throw new RuntimeException("There was an error calculating the fitnesses (probably had negative fitnesses)");
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
					double ret = fitnesses.get(o2) - fitnesses.get(o1);
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

	private void calculateFitnesses(List<Genome> population) {
		List<Double> fitnessesList = trainer.calculateFitness(population, this);

		for (int i = 0; i < population.size(); i++) {
			Genome geno = population.get(i);
			double fitness = fitnessesList.get(i);
			fitnesses.put(geno, fitness);
			geno.fitness = fitness;

//			if (fitness > highestScore) {
//				highestScore = fitness;
//				fittestGenome = geno;
//			}
		}
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

	private void removeExtinctSpecies() {
		this.species.removeIf(spec -> spec.isEmpty());
	}

	public void setTrainer(NEATTrainer trainer) {
		this.trainer = trainer;
	}

	public int getPreferredPopulationSize() {
		return this.preferredPopulationSize;
	}

	public Map<Genome, Double> getFitnesses() {
		return this.fitnesses;
	}

	public int getCurrentGeneration() {
		return this.currentGeneration;
	}

	public int accessAndIncrementCurrentInnovationNumber() {
		return this.currentInnovationNumber++;
	}

	private final HashMap<Tuple2D<Integer, Integer>, Integer> generationalInnovations = new HashMap<>();

	// public int samemutation = 0, uniquemutation = 0;

	public int accessAndIncrementCurrentInnovationNumberSmart(int inputNodeID, int outputNodeID) {
		for (Tuple2D<Integer, Integer> structure : this.generationalInnovations.keySet()) {
			if (structure.getA() == inputNodeID && structure.getB() == outputNodeID) {
//				System.out.println("found same mutation in generation!");
//				samemutation++;
//				System.out.println("distributed: " + this.generationalInnovations.get(structure));
				// found structure already in generation
				return this.generationalInnovations.get(structure);
			}
		}
		int innov = this.currentInnovationNumber++;

		this.generationalInnovations.put(new Tuple2D<>(inputNodeID, outputNodeID), innov);
//		uniquemutation++;
//		System.out.println("distributed: " + innov);
		return innov;
	}

	public NEATStats getNeatStats() {
		return this.neatStats;
	}

	public void setNeatStats(NEATStats neatStats) {
		this.neatStats = neatStats;
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

	@Override
	public Iterator<Genome> iterator() {
		List<Iterable<Genome>> iterables = new LinkedList<>();
		iterables.addAll(species);
		return new CombinedIterator<Genome>(iterables);
	}
}
