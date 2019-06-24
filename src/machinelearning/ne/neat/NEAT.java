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

	private List<Genome> population;

	//private Map<Genome, List<Genome>> species;
	private List<Species> species;
	
	private Map<Genome, Tuple2D<Double, Double>> fitnesses;

	private double c1 = 1, c2 = 1, c3 = 1;
	private double deltaThreshhold = 2.3;

	private double killPercent = 0.2;

	private NEATTrainer trainer;

	private int preferredPopulationSize;

	private int currentGeneration;

	private int currentInnovationNumber = 0;

	public NEAT(int preferredPopulationSize, NEATTrainer trainer) {
		this.preferredPopulationSize = preferredPopulationSize;
		this.setTrainer(trainer);

		this.population = new ArrayList<>(preferredPopulationSize);
		species = new ArrayList<>();
		this.fitnesses = new HashMap<>();

		this.populateRest();

		this.currentGeneration = 0;
	}

	public void runGeneration() {
		this.calculateFitnesses();

		this.groupIntoSpecies(this.population);

		this.calculateAdjustedFitnesses();

		this.sortSpeciesByFitness();

		// kill off

		// get offspring from cross

		// mutate

		// add offspring

		this.currentGeneration++;
	}

	public void runGenerationGA() {

		this.calculateFitnesses();

		this.sortPopulation();

		this.selectSurvivors();

		this.cleanup();

		List<Genome> offspring = this.crossPopulation();// new members added

		this.mutatePopulation();

		this.population.addAll(offspring);

		this.currentGeneration++;
	}

	private void populateRest() {
		for (int i = this.population.size(); i < this.preferredPopulationSize; i++) {
			this.population.add(this.trainer.generateRandom(this));
		}
	}

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

	private void sortSpeciesByFitness() {
		for(Species spec: this.species) {
			spec.sort((o1, o2) -> {
				double ret = NEAT.this.fitnesses.get(o1).getB() - NEAT.this.fitnesses.get(o2).getB();
				return ret < 0 ? -1 : ret > 0 ? 1 : 0;
			});
		}
	}

	private int killWorstInSpecies(double percentKilled) {
		int totalNumKilled = 0;
		for(Species spec: this.species) {
			int numKilled = (int) Math.ceil(percentKilled * spec.size());
			
			for(int i=0; i <numKilled;i++) {
				spec.remove(0);
			}
			
			
			totalNumKilled += numKilled;
		}

		return totalNumKilled;
	}

	private void kill(Genome geno) {
		this.population.remove(geno);

		for(Species spec: this.species) {
			if (spec.contains(geno)) {
				spec.remove(geno);

				if (spec.getRepresentative() == geno) {
					spec.assignNewRandomRepresentative();
				}
			}
		}
	}

	private void killExtinctSpecies() {

	}

	private void cleanup() {
		this.fitnesses.entrySet().removeIf(e -> !this.population.contains(e.getKey()));
	}

	public void calculateFitnesses() {
		List<Double> fits = this.trainer.calculateFitness(this.population, this);

		for (int i = 0; i < this.population.size(); i++) {
			this.fitnesses.put(this.population.get(i), new Tuple2D<>(fits.get(i), 0.0));
		}
	}

	// represented by species, not by i
	private void calculateAdjustedFitnesses2() {
		for(Species spec: this.species) {
			int numInSpecies = spec.size();

			for (Genome geno : spec) {
				Tuple2D<Double, Double> oriFitness = this.fitnesses.get(geno);
				double adjustedFitness = oriFitness.getA() / numInSpecies;
				Tuple2D<Double, Double> newFitness = new Tuple2D<>(oriFitness.getA(), adjustedFitness);
				this.fitnesses.put(geno, newFitness);
			}
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

	private void sortPopulation() {
		Collections.sort(this.population, (o1, o2) -> {
			double dec = this.fitnesses.get(o2).getA() - this.fitnesses.get(o1).getA();
			return dec == 0 ? 0 : dec > 0 ? 1 : -1;
		});
	}

	private List<Genome> crossPopulation() {
		List<Tuple2D<Genome, Genome>> crossoverPartners = this.trainer.selectCrossoverPartners(this.population,
				this);

		List<Genome> offspring = new ArrayList<>(crossoverPartners.size());

		for (Tuple2D<Genome, Genome> partners : crossoverPartners) {
			if (AKRandom.randomChance(this.trainer.getCrossoverChance(partners, this))) {
				Genome geno = this.trainer.crossover(partners.getA(), partners.getB(), this);
				offspring.add(geno);
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
		// System.out.println("Mutated " + toAdd.size() + " members");

	}

	private void selectSurvivors() {
		int numShouldBeKilled = this.population.size() - this.preferredPopulationSize;
		List<Genome> killed = this.trainer.killOff(this.population, numShouldBeKilled, this);
		for (Genome geno : killed) {
			this.population.remove(geno);
		}
	}

	private Species calculateCorrespondingSpecies(Genome geno) {
		for(Species spec: this.species) {
			Genome rep = spec.getRepresentative();
			if (NEATTrainer.similarity(geno, rep, this.c1, this.c2, this.c3) <= this.deltaThreshhold)
				return spec;
		}

		return null;
	}

	private void createNewSpecies(Genome geno) {
		species.add(new Species(geno));
	}

	private int numSimilarTo(Genome i) {
		int numSimilar = 0;
		for (Genome geno : this.population) {
			if (NEATTrainer.similarity(i, geno, this.c1, this.c2, this.c3) <= this.deltaThreshhold) {
				numSimilar++;
			}
		}
		return numSimilar;
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

}
