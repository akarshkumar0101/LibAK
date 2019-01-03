package machinelearning.geneticalgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import data.tuple.Tuple2D;
import math.AKRandom;

//C = chromosome (the genetic encoding of solution)

public class GAEnvironment<C> {

	private GeneticTrainer<C> trainer;

	private List<C> population;

	private HashMap<C, Double> fitnesses;

	private int populationSize;

	private int currentGeneration;

	private int numShouldBeKilled = 0;

	public GAEnvironment(int populationSize, GeneticTrainer<C> trainer) {

		this.populationSize = populationSize;
		this.setTrainer(trainer);

		this.population = new ArrayList<>(this.populationSize);
		this.fitnesses = new HashMap<>();

		this.populate();

		this.currentGeneration = 0;
	}

	public void setTrainer(GeneticTrainer<C> trainer) {
		this.trainer = trainer;
	}

	public List<C> getPopulation() {
		return this.population;
	}

	public HashMap<C, Double> getFitnesses() {
		return this.fitnesses;
	}

	private void populate() {
		for (int i = 0; i < this.populationSize; i++) {
			this.population.add(this.trainer.generateRandom(this));
		}
	}

	public void runGeneration() {

		this.cleanup();

		this.calculateFitnesses();

		Collections.sort(this.population, (o1, o2) -> {
			double dec = GAEnvironment.this.fitnesses.get(o2) - GAEnvironment.this.fitnesses.get(o1);
			// following logic ensures decimal values keep order when subtracting
			dec = Math.floor(dec);
			dec += .5;
			dec *= 2;
			return (int) dec;
		});

		this.crossPopulation();
		this.mutatePopulation();
		this.selectSurvivors();

		this.currentGeneration++;
	}

	private void cleanup() {

		this.fitnesses.entrySet().removeIf(e -> !GAEnvironment.this.population.contains(e.getKey()));
	}

	private void calculateFitnesses() {
		List<Double> fits = this.trainer.calculateFitness(this.population, this);

		for (int i = 0; i < this.population.size(); i++) {
			this.fitnesses.put(this.population.get(i), fits.get(i));
		}
	}

	private void crossPopulation() {
		List<Tuple2D<C, C>> crossoverPartners = this.trainer.selectCrossoverPartners(this.population, this);

		this.numShouldBeKilled = 0;

		for (Tuple2D<C, C> partners : crossoverPartners) {
			if (AKRandom.randomChance(this.trainer.getCrossoverChance(partners, this))) {
				C offspring = this.trainer.crossover(partners.getA(), partners.getB(), this);
				this.population.add(offspring);
				this.numShouldBeKilled++;
			}
		}
	}

	private void mutatePopulation() {
		Iterator<C> it = this.population.iterator();
		List<C> toAdd = new ArrayList<>();
		while (it.hasNext()) {
			C chr = it.next();
			if (AKRandom.randomChance(this.trainer.getMutationChance(chr, this))) {
				C newchr = this.trainer.mutate(chr, this);
				it.remove();
				this.population.remove(chr);
				toAdd.add(newchr);
			}
		}
		for (C c : toAdd) {
			this.population.add(c);
		}
	}

	private void selectSurvivors() {
		List<C> killed = this.trainer.killOff(this.population, this.numShouldBeKilled, this);
		Iterator<C> it = this.population.iterator();
		while (it.hasNext()) {
			C c = it.next();
			if (killed.contains(c)) {
				it.remove();
			}
		}
	}

}
