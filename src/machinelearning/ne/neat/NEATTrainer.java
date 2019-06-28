package machinelearning.ne.neat;

import java.util.ArrayList;
import java.util.List;

import data.tuple.Tuple2D;
import machinelearning.ne.neat.genome.BaseTemplate;
import machinelearning.ne.neat.genome.ConnectionGene;
import machinelearning.ne.neat.genome.Genome;
import math.AKRandom;

public interface NEATTrainer {

	public abstract double calculateFitness(Genome a, NEAT neat);

	public abstract Genome generateRandom(NEAT neat);

	public abstract NEATStats calculateStatsForGeneration(NEAT neat);

	public default List<Double> calculateFitness(List<Genome> as, NEAT neat) {
		List<Double> fitnesses = new ArrayList<>(as.size());
		for (int i = 0; i < as.size(); i++) {
			fitnesses.add(this.calculateFitness(as.get(i), neat));
		}
		return fitnesses;
	}

	public default List<Tuple2D<Genome, Genome>> selectCrossoverPartners(List<Genome> population, NEAT neat,
			int numOffspring) {
		return this.selectCrossoverPartnersSUS(population, numOffspring, neat);
	}

	// assumes population is sorted by fitness
	public default List<Genome> killOff(List<Genome> population, int numToKill, NEAT neat) {
		List<Genome> killed = new ArrayList<>(numToKill);
		for (int i = population.size() - 1; i > population.size() - 1 - numToKill; i--) {
			killed.add(population.get(i));
		}
		return killed;
	}

	public default Genome crossover(Genome a, Genome b, NEAT neat) {
		// return NEATTrainer.crossover(a, b);
		return NEATTrainer.crossoverCBullet(a, b);
	}

	public default Genome mutate(Genome geno, NEAT neat) {
		if (true)
			return this.mutateCBullet(geno, neat);

		geno = geno.clone();

		if (AKRandom.randomChance(neat.getNeatStats().getAddConnectionProbability(neat))) {
			this.mutateAddConnection(geno, neat);
		}
		if (AKRandom.randomChance(neat.getNeatStats().getAddNodeProbability(neat))) {
			this.mutateAddNode(geno, neat);
		}
		if (AKRandom.randomChance(neat.getNeatStats().getWeightShiftProbability(neat))) {
			this.mutateShiftWeight(geno, neat);
		}
		if (AKRandom.randomChance(neat.getNeatStats().getWeightRandomizeProbability(neat))) {
			this.mutateRandomizeWeight(geno, neat);
		}
		if (AKRandom.randomChance(neat.getNeatStats().getToggleConnectionProbability(neat))) {
			this.mutateToggleConnection(geno, neat);
		}
		return geno;
	}

	public default boolean isValidConnection(int inputNodeID, int outputNodeID, Genome geno,
			BaseTemplate baseTemplate) {
		int layer1 = 0, layer2 = 0;

		if (inputNodeID > baseTemplate.numInputNodes()) {
			layer1 = 1;
		}
		if (outputNodeID > baseTemplate.numInputNodes()) {
			layer2 = 1;
		}
		if (inputNodeID > baseTemplate.numInputNodes() + geno.getNumHiddenNodes()) {
			layer1 = 2;
		}
		if (outputNodeID > baseTemplate.numInputNodes() + geno.getNumHiddenNodes()) {
			layer2 = 2;
		}
		if (layer1 == layer2)
			return false;

		return true;
	}

	public default void mutateAddConnection(Genome geno, NEAT neat) {
		int inputNodeID = 0;
		int outputNodeID = 0;

		// try to get a unique new connection 100 times at most
		for (int iterations = 0; outputNodeID == inputNodeID || geno.hasConnection(inputNodeID, outputNodeID)
				|| !this.isValidConnection(inputNodeID, outputNodeID, geno, geno.getBaseTemplate()); iterations++) {
			inputNodeID = (int) AKRandom.randomNumber(geno.getNumTotalNodes());
			outputNodeID = (int) AKRandom.randomNumber(geno.getNumTotalNodes());
			if (iterations > 100)
				return; // could not find new connection
		}
		// now we found a connection

		int innovationNumber = neat.accessAndIncrementCurrentInnovationNumber();
		double weightRandomStrengh = neat.getNeatStats().getWeightRandomizeStrengh(neat);
		double connectionWeight = AKRandom.randomNumber(-weightRandomStrengh, weightRandomStrengh);

		ConnectionGene cg = new ConnectionGene(innovationNumber, inputNodeID, outputNodeID, connectionWeight, true);
		geno.getConnectionGenes().add(cg);
	}

	public default void mutateAddNode(Genome geno, NEAT neat) {
		ConnectionGene toSplitCg = null;

		for (int iterations = 0; toSplitCg == null || !toSplitCg.isEnabled(); iterations++) {
			toSplitCg = geno.getConnectionGenes().get((int) AKRandom.randomNumber(geno.getConnectionGenes().size()));

			if (iterations > 100) // could not find an enabled connection
				return;
		}

		toSplitCg.setEnabled(false); // disable it

		int inputNodeID = toSplitCg.getInputNodeID(), outputNodeID = toSplitCg.getOutputNodeID();

		int newNodeID = geno.addNewHiddenNode();

		ConnectionGene cg = new ConnectionGene(neat.accessAndIncrementCurrentInnovationNumber(), inputNodeID, newNodeID,
				1, true);
		geno.getConnectionGenes().add(cg);

		ConnectionGene cg2 = new ConnectionGene(neat.accessAndIncrementCurrentInnovationNumber(), newNodeID,
				outputNodeID, toSplitCg.getConnectionWeight(), true);
		geno.getConnectionGenes().add(cg2);
	}

	public default void mutateShiftWeight(Genome geno, NEAT neat) {
		ConnectionGene cg = null;

		for (int iterations = 0; cg == null || !cg.isEnabled(); iterations++) {
			cg = geno.getConnectionGenes().get((int) AKRandom.randomNumber(geno.getConnectionGenes().size()));

			if (iterations > 100) // could not find an enabled connection
				return;
		}
		double connectionWeight = cg.getConnectionWeight();
		double shiftStrength = neat.getNeatStats().getWeightShiftStrengh(neat);
		connectionWeight += AKRandom.randomNumber(-shiftStrength, shiftStrength);
		cg.setConnectionWeight(connectionWeight);
	}

	public default void mutateRandomizeWeight(Genome geno, NEAT neat) {
		ConnectionGene cg = null;

		for (int iterations = 0; cg == null || !cg.isEnabled(); iterations++) {
			cg = geno.getConnectionGenes().get((int) AKRandom.randomNumber(geno.getConnectionGenes().size()));

			if (iterations > 100) // could not find an enabled connection
				return;
		}
		double randomizeStrength = neat.getNeatStats().getWeightRandomizeStrengh(neat);
		cg.setConnectionWeight(AKRandom.randomNumber(-randomizeStrength, randomizeStrength));
	}

	public default void mutateToggleConnection(Genome geno, NEAT neat) {
		ConnectionGene cg = geno.getConnectionGenes()
				.get((int) AKRandom.randomNumber(geno.getConnectionGenes().size()));
		cg.setEnabled(!cg.isEnabled());
	}

	public static Genome crossover(Genome a, Genome b) {
		Genome child = new Genome(NEATTrainer.forgeBaseTemplates(a.getBaseTemplate(), b.getBaseTemplate()),
				Math.max(a.getNumHiddenNodes(), b.getNumHiddenNodes()));

		int i1 = 0;
		int i2 = 0;
		// List<Gene> childGenes = new ArrayList<>();
		while (i1 < a.getConnectionGenes().size() || i2 < b.getConnectionGenes().size()) {
			if (i1 >= a.getConnectionGenes().size()) {
				child.getConnectionGenes().add(b.getConnectionGenes().get(i2).clone());
				i2++;
				continue;
			}
			if (i2 >= b.getConnectionGenes().size()) {
				child.getConnectionGenes().add(a.getConnectionGenes().get(i1).clone());
				i1++;
				continue;
			}
			ConnectionGene g1 = a.getConnectionGenes().get(i1);
			ConnectionGene g2 = b.getConnectionGenes().get(i2);
			if (g1.getInnovationNumber() == g2.getInnovationNumber()) {
				ConnectionGene g = g1.clone();
				g.setEnabled(g1.isEnabled() && g2.isEnabled());

				child.getConnectionGenes().add(g);
				i1++;
				i2++;
			} else if (g1.getInnovationNumber() < g2.getInnovationNumber()) {
				// g1 is disjoint
				child.getConnectionGenes().add(g1.clone());
				i1++;
			} else {
				// g2 is disjoint
				child.getConnectionGenes().add(g2.clone());
				i2++;
			}
		}

		return child;
	}

	public static Genome crossoverCBullet(Genome a, Genome b) {
		Genome child = new Genome(NEATTrainer.forgeBaseTemplates(a.getBaseTemplate(), b.getBaseTemplate()),
				Math.max(a.getNumHiddenNodes(), b.getNumHiddenNodes()));

		// assume a is more fit

		int i1 = 0;
		int i2 = 0;
		// List<Gene> childGenes = new ArrayList<>();
		while (i1 < a.getConnectionGenes().size() || i2 < b.getConnectionGenes().size()) {
			if (i1 >= a.getConnectionGenes().size()) {
				// dont inherit from unfit parent
				// child.getConnectionGenes().add(b.getConnectionGenes().get(i2).clone());
				i2++;
				continue;
			}
			if (i2 >= b.getConnectionGenes().size()) {
				child.getConnectionGenes().add(a.getConnectionGenes().get(i1).clone());
				i1++;
				continue;
			}
			ConnectionGene g1 = a.getConnectionGenes().get(i1);
			ConnectionGene g2 = b.getConnectionGenes().get(i2);
			if (g1.getInnovationNumber() == g2.getInnovationNumber()) {
				// pick random parent for weight
				ConnectionGene g = AKRandom.randomChance(0.5) ? g1.clone() : g2.clone();
				// g is disabled if one or more of parents are disabled and 75% chance is met.
				g.setEnabled(g1.isEnabled() && g2.isEnabled() || AKRandom.randomChance(.25));

				child.getConnectionGenes().add(g);
				i1++;
				i2++;
			} else if (g1.getInnovationNumber() < g2.getInnovationNumber()) {
				// g1 is disjoint
				child.getConnectionGenes().add(g1.clone());
				i1++;
			} else {
				// g2 is disjoint
				// dont inherit from unfit parent
				// child.getConnectionGenes().add(g2.clone());
				i2++;
			}
		}

		return child;
	}

	public static void mutateConnectionGene(ConnectionGene cg) {
		if (AKRandom.randomChance(0.10)) {// 10% of the time completely change the weight
			cg.setConnectionWeight(AKRandom.randomNumber(-1, 1));
		} else {// otherwise slightly change it
			double weight = cg.getConnectionWeight();
			weight += AKRandom.randomNumber(-0.02, 0.02);
			// keep weight between bounds
			if (weight > 1) {
				weight = 1;
			}
			if (weight < -1) {
				weight = -1;
			}
			cg.setConnectionWeight(weight);
		}
	}

	public default Genome mutateCBullet(Genome geno, NEAT neat) {
		geno = geno.clone();

		if (AKRandom.randomChance(0.80)) { // 80% of the time mutate weights
			for (ConnectionGene cg : geno.getConnectionGenes()) {
				NEATTrainer.mutateConnectionGene(cg);
			}
		}
		// 8% of the time add a new connection
		if (AKRandom.randomChance(0.08)) {
			this.mutateAddConnection(geno, neat);
		}

		// 2% of the time add a node
		if (AKRandom.randomChance(0.10)) {
			this.mutateAddNode(geno, neat);
		}

		return geno;
	}

	public static BaseTemplate forgeBaseTemplates(BaseTemplate t1, BaseTemplate t2) {
		if (t1 == t2 || t1.equals(t2))
			return t1;
		else {
			BaseTemplate tforge = new BaseTemplate(t1.hasBias() || t2.hasBias(),
					Math.max(t1.numInputNodes(), t1.numInputNodes()),
					Math.max(t1.numOutputNodes(), t2.numOutputNodes()));
			return tforge;
		}
	}

	public default boolean areSimilar(Genome a, Genome b, NEAT neat) {
		return NEATTrainer.similarity(a, b, neat.getNeatStats().getC1(neat), neat.getNeatStats().getC2(neat),
				neat.getNeatStats().getC3(neat)) < neat.getNeatStats().getDeltaThreshold(neat);
	}

	public static double similarity(Genome a, Genome b, double c1, double c2, double c3) {
		int N = Math.max(a.getConnectionGenes().size(), b.getConnectionGenes().size());

		int numExcess = 0;
		int numDisjoint = 0;
		double avgWeightDiff = 0;
		int numCommonGenes = 0;

		int i1 = 0;
		int i2 = 0;
		// List<Gene> childGenes = new ArrayList<>();
		while (i1 < a.getConnectionGenes().size() || i2 < b.getConnectionGenes().size()) {
			if (i1 >= a.getConnectionGenes().size()) {
				numExcess++;
				i2++;
				continue;
			}
			if (i2 >= b.getConnectionGenes().size()) {
				numExcess++;
				i1++;
				continue;
			}
			ConnectionGene g1 = a.getConnectionGenes().get(i1);
			ConnectionGene g2 = b.getConnectionGenes().get(i2);
			if (g1.getInnovationNumber() == g2.getInnovationNumber()) {
				numCommonGenes++;
				avgWeightDiff += Math.abs(g1.getConnectionWeight() - g2.getConnectionWeight());

				i1++;
				i2++;
			} else if (g1.getInnovationNumber() < g2.getInnovationNumber()) {
				// g1 is disjoint
				numDisjoint++;
				i1++;
			} else {
				// g2 is disjoint
				numDisjoint++;
				i2++;
			}
		}
		avgWeightDiff /= numCommonGenes;

		double similarity = 0;

		similarity += c1 * numExcess / N;
		similarity += c2 * numDisjoint / N;
		similarity += c3 * avgWeightDiff;

		return similarity;
	}

	/**
	 *
	 * Uses raw fitness for individuals when choosing crossover partners (not
	 * adjusted fitness)
	 *
	 * @param population
	 * @param numCrossovers
	 * @param neat
	 * @return
	 */
	public default List<Tuple2D<Genome, Genome>> selectCrossoverPartnersSUS(List<Genome> population, int numCrossovers,
			NEAT neat) {

		double fitnessOffset = Double.MAX_VALUE;
		for (Genome c : population) {

			double fit = neat.getFitnesses().get(c).getA();
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
		for (Genome c : population) {
			totalFitness += neat.getFitnesses().get(c).getA() + fitnessOffset;
		}

		List<Tuple2D<Genome, Genome>> partners = new ArrayList<>(numCrossovers);

		// System.out.println(fitnessOffset);

		for (int i = 0; i < numCrossovers; i++) {
			double pick1Fit = AKRandom.randomNumber(0, totalFitness);
			double pick2Fit = (pick1Fit + totalFitness / 2) % totalFitness;

			Genome a = null, b = null;
			double currentFitAt = 0;
			for (Genome c : population) {
				double fit = neat.getFitnesses().get(c).getA() + fitnessOffset;
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

	public default List<Tuple2D<Genome, Genome>> selectCrossoverPartnersRandomly(List<Genome> population,
			int numCrossovers, NEAT neat) {

		ArrayList<Tuple2D<Genome, Genome>> partners = new ArrayList<>(numCrossovers);

		for (int i = 0; i < numCrossovers; i++) {
			Genome a = population.get((int) AKRandom.randomNumber(0, population.size()));
			Genome b = population.get((int) AKRandom.randomNumber(0, population.size()));
			partners.add(new Tuple2D<>(a, b));
		}

		return partners;
	}

}