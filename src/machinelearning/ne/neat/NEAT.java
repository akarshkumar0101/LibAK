package machinelearning.ne.neat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ui.FrameWrapper;

public class NEAT {
	Map<NEATNNGeno, List<NEATNNGeno>> species;
	
	public NEAT() {
		species = new HashMap<>();
	}

	public static void main(String... args) {

		NEATNNGeno geno1 = new NEATNNGeno(false, 3, 1, 1);
		geno1.connectionGenes.add(new ConnectionGene(1, 1, 4, 0.3, true));
		geno1.connectionGenes.add(new ConnectionGene(2, 2, 4, 5.0, false));
		geno1.connectionGenes.add(new ConnectionGene(3, 3, 4, 2.0, true));
		geno1.connectionGenes.add(new ConnectionGene(4, 2, 5, 1.7, true));
		geno1.connectionGenes.add(new ConnectionGene(5, 5, 4, 0.2, true));
		geno1.connectionGenes.add(new ConnectionGene(8, 1, 5, 0.5, true));

		NEATNNGeno geno2 = new NEATNNGeno(false, 3, 1, 2);
		geno2.connectionGenes.add(new ConnectionGene(1, 1, 4, 0.3, true));
		geno2.connectionGenes.add(new ConnectionGene(2, 2, 4, 5.0, false));
		geno2.connectionGenes.add(new ConnectionGene(3, 3, 4, 2.0, true));
		geno2.connectionGenes.add(new ConnectionGene(4, 2, 5, 1.7, true));
		geno2.connectionGenes.add(new ConnectionGene(5, 5, 4, 0.2, false));
		geno2.connectionGenes.add(new ConnectionGene(6, 5, 6, 4.5, true));
		geno2.connectionGenes.add(new ConnectionGene(7, 6, 4, 0.8, true));
		geno2.connectionGenes.add(new ConnectionGene(9, 3, 5, 2.3, true));
		geno2.connectionGenes.add(new ConnectionGene(10, 1, 6, 5.2, true));

		NEATNNGeno childGeno = geno1.crossover(geno2);
		NEATNeuralNetwork network = new NEATNeuralNetwork(childGeno);

		// System.out.println(geno1);
		// System.out.println(geno2);
		System.out.println(childGeno);

		network.calculate();

		System.out.println(network.outputNeurons.get(0).activation);

		FrameWrapper<VisualNEATNetworkPanel> frame = new FrameWrapper<>("NEAT", 800, 800, true, false);

		frame.setComponent(new VisualNEATNetworkPanel(network));

		frame.setVisible(true);
	}

	public static void test1() {
		NEATNNGeno geno = new NEATNNGeno(true, 2, 1, 1);

		geno.connectionGenes.add(new ConnectionGene(1, 0, 4, 0.3, true));
		geno.connectionGenes.add(new ConnectionGene(2, 1, 4, 5.0, true));
		geno.connectionGenes.add(new ConnectionGene(3, 2, 4, 2.0, true));
		geno.connectionGenes.add(new ConnectionGene(4, 2, 3, 1.7, true));
		geno.connectionGenes.add(new ConnectionGene(5, 4, 3, -0.2, true));
		geno.connectionGenes.add(new ConnectionGene(6, 0, 2, 0.56, false));

		NEATNeuralNetwork network = new NEATNeuralNetwork(geno);

		network.calculate();
	}
}
