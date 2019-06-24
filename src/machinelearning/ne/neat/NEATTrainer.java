package machinelearning.ne.neat;

import java.util.ArrayList;
import java.util.List;

import data.tuple.Tuple2D;
import machinelearning.ne.neat.genome.BaseTemplate;
import machinelearning.ne.neat.genome.ConnectionGene;
import machinelearning.ne.neat.genome.Genome;

public interface NEATTrainer{

	
	public double calculateFitness(Genome a, NEAT neat);

	public default List<Double> calculateFitness(List<Genome> as, NEAT neat) {
		List<Double> fitnesses = new ArrayList<>(as.size());
		for (int i = 0; i < as.size(); i++) {
			fitnesses.add(this.calculateFitness(as.get(i), neat));
		}
		return fitnesses;
	}

	public default double getMutationChance(Genome a, NEAT neat) {
		return (double) neat.getPopulation().indexOf(a) / neat.getPopulation().size();
	}
	
	public double getCrossoverChance(Tuple2D<Genome, Genome> partners, NEAT neat);

	
	public Genome generateRandom(NEAT neat);

	
	public Genome crossover(Genome a, Genome b, NEAT neat);

	
	public Genome mutate(Genome a, NEAT neat);


	public List<Tuple2D<Genome, Genome>> selectCrossoverPartners(List<Genome> population,
			NEAT neat);

	
	public List<Genome> killOff(List<Genome> population, int numToKill, NEAT neat);
	
	

	public static Genome crossover(Genome a, Genome b) {
		Genome child = new Genome(NEATTrainer.forgeBaseTemplates(a.getBaseTemplate(), b.getBaseTemplate()),
				Math.max(a.getNumHiddenNodes(), b.getNumHiddenNodes()));

		int i1 = 0;
		int i2 = 0;
		// List<Gene> childGenes = new ArrayList<>();
		while (i1 < a.getConnectionGenes().size() || i2 < b.getConnectionGenes().size()) {
			if (i1 >= a.getConnectionGenes().size()) {
				child.getConnectionGenes().add(b.getConnectionGenes().get(i2).copy());
				i2++;
				continue;
			}
			if (i2 >= b.getConnectionGenes().size()) {
				child.getConnectionGenes().add(a.getConnectionGenes().get(i1).copy());
				i1++;
				continue;
			}
			ConnectionGene g1 = a.getConnectionGenes().get(i1);
			ConnectionGene g2 = b.getConnectionGenes().get(i2);
			if (g1.getInnovationNumber() == g2.getInnovationNumber()) {
				ConnectionGene g = g1.copy();
				g.setEnabled(g1.isEnabled() && g2.isEnabled());

				child.getConnectionGenes().add(g);
				i1++;
				i2++;
			} else if (g1.getInnovationNumber() < g2.getInnovationNumber()) {
				// g1 is disjoint
				child.getConnectionGenes().add(g1.copy());
				i1++;
			} else {
				// g2 is disjoint
				child.getConnectionGenes().add(g2.copy());
				i2++;
			}
		}

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
}