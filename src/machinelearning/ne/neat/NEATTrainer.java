package machinelearning.ne.neat;

import java.util.ArrayList;
import java.util.List;

import machinelearning.ne.neat.genome.BaseTemplate;
import machinelearning.ne.neat.genome.ConnectionGene;
import machinelearning.ne.neat.genome.Genome;
import math.AKRandom;

public interface NEATTrainer {

	public abstract double calculateFitness(Genome a, NEAT neat);

	public default Genome generateRandom(NEAT neat) {
		Genome geno = generateRandomGenome(neat);
		geno.cleanup();
		return geno;
	}

	public abstract Genome generateRandomGenome(NEAT neat);

	public abstract NEATStats calculateStatsForGeneration(NEAT neat);

	public default List<Double> calculateFitness(List<Genome> as, NEAT neat) {
		List<Double> fitnesses = new ArrayList<>(as.size());
		for (int i = 0; i < as.size(); i++) {
			fitnesses.add(this.calculateFitness(as.get(i), neat));
		}
		return fitnesses;
	}

	public default Genome mutate(Genome geno, NEAT neat) {
		geno = geno.clone();

		// 80% of the time mutate weights
		if (AKRandom.randomChance(0.80)) {
			for (ConnectionGene cg : geno.getConnectionGenes()) {
				NEATTrainer.mutateConnectionGene(cg);
			}
		}

		boolean probablyMutatedStructure = false;

		// 8% of the time add a new connection
		if (AKRandom.randomChance(0.08)) {
			this.mutateAddConnection(geno, neat);
			probablyMutatedStructure = true;
		}

		// 2% of the time add a node
		if (AKRandom.randomChance(0.02)) {
			this.mutateAddNode(geno, neat);
			probablyMutatedStructure = true;
		}
		if (probablyMutatedStructure) {
			// need to cleanup because we don't know if the order of genes is correct or not
			geno.cleanup();
		}

		return geno;
	}

	public default boolean isValidConnection(int inputNodeID, int outputNodeID, Genome geno,
			BaseTemplate baseTemplate) {
		int layer1 = geno.layer(inputNodeID), layer2 = geno.layer(outputNodeID);
		boolean isRecurrentAllowed = false;

		if (layer2 == 0)
			// input layer can't be output
			return false;
		if (!isRecurrentAllowed && layer2 < layer1)
			return false;

		return true;
	}

	public default void mutateAddConnection(Genome geno, NEAT neat) {
		int inputNodeID = 0;
		int outputNodeID = 0;

		// try to get a unique new connection 100 times at most
		for (int iterations = 0; outputNodeID == inputNodeID || geno.hasConnection(inputNodeID, outputNodeID)
				|| !this.isValidConnection(inputNodeID, outputNodeID, geno, geno.getBaseTemplate()); iterations++) {

			if (AKRandom.randomChance(0.5) && geno.getBaseTemplate().hasBias()) {
				inputNodeID = 0;
			} else {
				inputNodeID = (int) AKRandom.randomNumber(geno.getNumTotalNodes() - 1) + 1;
			}

			outputNodeID = (int) AKRandom.randomNumber(geno.getNumTotalNodes());
			if (iterations > 100)
				return; // could not find new connection
		}
		// now we found a connection

		// int innovationNumber = neat.accessAndIncrementCurrentInnovationNumber();
		int innovationNumber = neat.accessAndIncrementCurrentInnovationNumberSmart(inputNodeID, outputNodeID);
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

		int innovationNumber1 = neat.accessAndIncrementCurrentInnovationNumberSmart(inputNodeID, newNodeID);
		int innovationNumber2 = neat.accessAndIncrementCurrentInnovationNumberSmart(newNodeID, outputNodeID);

		ConnectionGene cg = new ConnectionGene(innovationNumber1, inputNodeID, newNodeID, 1, true);
		geno.getConnectionGenes().add(cg);

		ConnectionGene cg2 = new ConnectionGene(innovationNumber2, newNodeID, outputNodeID,
				toSplitCg.getConnectionWeight(), true);
		geno.getConnectionGenes().add(cg2);
	}

	public static void mutateConnectionGene(ConnectionGene cg) {
		// 10% of the time completely change the weight
		if (AKRandom.randomChance(0.00)) {
			cg.setConnectionWeight(AKRandom.randomNumber(-1, 1));
		} else {// otherwise slightly change it
			double weight = cg.getConnectionWeight();
			weight += AKRandom.randomNumber(-0.02, 0.02);
			// keep weight between bounds
			if (weight > 1) {
				//weight = 1;
			}
			if (weight < -1) {
				//weight = -1;
			}
			cg.setConnectionWeight(weight);
		}

		// 0% of the time toggle a connection
		if (AKRandom.randomChance(0.00)) {
			cg.setEnabled(!cg.isEnabled());
		}

	}

	public default Genome crossover(Genome a, Genome b, NEAT neat) {
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

				if (g1.isEnabled() && g2.isEnabled()) {
					g.setEnabled(true);
				} else if (g1.isEnabled() || g2.isEnabled()) {
					// g is disabled if one of parents are disabled and 75% chance is met.
					g.setEnabled(AKRandom.randomChance(.25));
				} else {
					g.setEnabled(false);
				}

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
		child.fitness = (a.fitness + b.fitness) / 2;

		return child;
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

		// System.out.println(similarity);
		// System.out.println(a.complexity() +" "+ b.complexity());

		return similarity;
	}

}