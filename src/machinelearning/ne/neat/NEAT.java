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

	public NEAT(int preferredPopulationSize, NEATTrainer trainer, boolean secondImpl) {
		this.setTrainer(trainer);

		this.species = new ArrayList<>();
		this.fitnesses = new HashMap<>();

		this.preferredPopulationSize = preferredPopulationSize;
		this.currentGeneration = 0;
		this.currentInnovationNumber = 0;

		this.setNeatStats(this.trainer.calculateStatsForGeneration(this));

		for (int i = 0; i < preferredPopulationSize; i++) {
			population.add(trainer.generateRandom(this));
		}
	}

	public void runGeneration() {
		this.setNeatStats(this.trainer.calculateStatsForGeneration(this));

		// kill off
		this.killInSpecies(this.neatStats.getPercentPopulationToKill(this));

		this.saveFitnessMem();

		// get offspring from cross
		List<Genome> offspring = this.crossPopulation((int) (this.preferredPopulationSize * 0.75));
		List<Genome> clonedOffspring = this.clonePopulation((int) (this.preferredPopulationSize * 0.25));
		offspring.addAll(clonedOffspring);

		offspring.sort(new Comparator<Genome>() {
			@Override
			public int compare(Genome o1, Genome o2) {
				double ret = o1.fitness - o2.fitness;
				return ret > 0 ? 1 : ret < 0 ? -1 : 0;
			}
		});

		// mutate
		this.mutatePopulation(offspring);

		// add offspring

		for (Species spec : species) {
			spec.clear();
		}
		this.putIntoSpecies(offspring);
		this.populateRest();

		for (Species spec : species) {
			spec.assignNewRandomRepresentative();
		}

		this.killExtinctSpecies();

		this.calculateFitnesses();

		this.calculateAdjustedFitnesses2();

		this.sortSpeciesByFitness();

		if (species.size() > 1) {
			System.out.println("new species");
		}
		for (Species spec : species) {
			if (spec.getRepresentative() == null) {
				System.out.println("wtf");
			}
		}

		species.sort(new Comparator<Species>() {
			@Override
			public int compare(Species o1, Species o2) {
				return o1.getRepresentative().complexity().getA() - o2.getRepresentative().complexity().getA();
			}
		});

		this.currentGeneration++;

		this.generationalInnovations.clear();

		System.out.println("\n\n\n\n");
//		System.out.println("population size: " + this.size());
//		System.out.println("avg fitness: " + this.averageFitness());
//		System.out.println("best fitness: " + this.getFitnesses().get(this.bestGenome()));
//		System.out.println("avg hidden nodes: " + this.averageHiddenNodes());
//		System.out.println("num species: " + this.species.size());

	}

	List<Genome> population = new ArrayList<Genome>();
	List<Genome> nextGenPopulation = new ArrayList<Genome>();
	Map<Genome, Species> speciesMap = new HashMap<>();
	
	private Random random = new Random();

	public void runGeneration2() {
		for (Species spec : species) {
			spec.assignNewRandomRepresentative();
			spec.clear();
			spec.totalAdjustedFitness = 0;
		}

		fitnesses.clear();
		speciesMap.clear();
		nextGenPopulation.clear();

		// Place genomes into species
		for (Genome geno : population) {
			boolean foundSpecies = false;
			for (Species spec : this.species) {
				if (trainer.areSimilar(geno, spec.getRepresentative(), this)) {
					// found it
					spec.add(geno);
					speciesMap.put(geno, spec);
					foundSpecies = true;
					break;
				}
			}
			if (!foundSpecies) { // if there is no appropiate species for genome, make a new one
				Species newSpecies = new Species(geno);
				species.add(newSpecies);
				speciesMap.put(geno, newSpecies);
			}
		}

		// Remove unused species
		Iterator<Species> iter = species.iterator();
		while (iter.hasNext()) {
			Species s = iter.next();
			if (s.isEmpty()) {
				iter.remove();
			}
		}
		// Evaluate genomes and assign score
		for (Genome geno : population) {
			Species s = speciesMap.get(geno); // Get species of the genome

			double fitness = trainer.calculateFitness(geno, this);
			double adjustedFitness = fitness / s.size();

			s.totalAdjustedFitness += adjustedFitness;
			fitnesses.put(geno, new Tuple2D<>(fitness, adjustedFitness));
			
			geno.fitness = fitness;
//			if (fitness > highestScore) {
//				highestScore = fitness;
//				fittestGenome = geno;
//			}
		}
		// put best genomes from each species into next generation
		for (Species spec : species) {
			// sort species with adjusted fitness
			spec.sort(new Comparator<Genome>() {
				@Override
				public int compare(Genome o1, Genome o2) {
					double ret = fitnesses.get(o2).getB() - fitnesses.get(o1).getB();
					return ret > 0 ? 1 : ret < 0 ? -1 : 0;
				}
			});
			Genome bestInSpec = spec.get(0);

			nextGenPopulation.add(bestInSpec);
		}
		
		// Breed the rest of the genomes
		while (nextGenPopulation.size() < preferredPopulationSize) { // replace removed genomes by randomly breeding
			Species s = getRandomSpeciesBiasedAjdustedFitness(random);

			Genome p1 = getRandomGenomeBiasedAdjustedFitness(s, random);
			Genome p2 = getRandomGenomeBiasedAdjustedFitness(s, random);

			Genome child;
			//use adjusted fitness here
			if (fitnesses.get(p1).getB() >= fitnesses.get(p2).getB()) {
				child = trainer.crossover(p1, p2, this);
			} else {
				child = trainer.crossover(p2, p1, this);
			}
			child = trainer.mutate(child, this);
			nextGenPopulation.add(child);
		}

		population = nextGenPopulation;
		nextGenPopulation = new ArrayList<Genome>();

	}
	
	/**
	 * Selects a random species from the species list, where species with a higher total adjusted fitness have a higher chance of being selected
	 */
	private Species getRandomSpeciesBiasedAjdustedFitness(Random random) {
		double completeWeight = 0.0;	// sum of probablities of selecting each species - selection is more probable for species with higher fitness
		for (Species s : species) {
            completeWeight += s.totalAdjustedFitness;
		}
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (Species s : species) {
            countWeight += s.totalAdjustedFitness;
            if (countWeight >= r) {
            	 return s;
            }
        }
        throw new RuntimeException("Couldn't find a species... Number is species in total is "+species.size()+", and the total adjusted fitness is "+completeWeight);
	}
	
	/**
	 * Selects a random genome from the species chosen, where genomes with a higher adjusted fitness have a higher chance of being selected
	 */
	private Genome getRandomGenomeBiasedAdjustedFitness(Species spec, Random random) {
		double completeWeight = 0.0;	// sum of probablities of selecting each genome - selection is more probable for genomes with higher fitness
		//use adjusted fitness here
		for (Genome geno : spec) {
			completeWeight += fitnesses.get(geno).getB();
		}
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (Genome geno : spec) {
            countWeight += fitnesses.get(geno).getB();
            if (countWeight >= r) {
            	 return geno;
            }
        }
        throw new RuntimeException("Couldn't find a genome... Number is genomes in selæected species is "+spec.size()+", and the total adjusted fitness is "+completeWeight);
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
				Genome baby = spec.giveBaby(this, this.trainer);
				offspring.add(baby);
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

	private void saveFitnessMem() {
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

	@Override
	public Iterator<Genome> iterator() {
		List<Iterable<Genome>> iterables = new LinkedList<>();
		iterables.addAll(species);
		return new CombinedIterator<Genome>(iterables);
	}
}
