package machinelearning.ne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NEAT {
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

class NEATNeuron {
	double activation;

	boolean calculated;

	Map<NEATNeuron, Double> prevConnections;

	NEATNeuralNetwork network;

	NEATNeuron(NEATNeuralNetwork network) {
		this.network = network;
		this.calculated = false;

		this.prevConnections = new HashMap<>();
	}

	public void addConnection(NEATNeuron prevNeuron, double connectionWeight) {
		this.prevConnections.put(prevNeuron, connectionWeight);
	}

	public void calculate() {
		if (this.calculated)
			return;
		this.calculated = true;
		double input = 0;

		for (NEATNeuron neuron : this.prevConnections.keySet()) {
			double connectionWeight = this.prevConnections.get(neuron);
			if (!neuron.calculated) {
				neuron.calculate();
			}
			input += neuron.activation * connectionWeight;
		}
		this.activation = input;

		this.activationFunc();
	}

	public void invalidate() {
		this.calculated = false;
	}

	public void activationFunc() {
		this.activation = 1 / (1 + Math.exp(-this.activation));
	}
}

abstract class NEATInputNeuron extends NEATNeuron {
	public NEATInputNeuron(NEATNeuralNetwork network) {
		super(network);
	}

	@Override
	public void calculate() {
		this.activation = this.getInput();
		this.activationFunc();
	}

	public abstract double getInput();
}

class BiasNeuron extends NEATInputNeuron {

	public BiasNeuron(NEATNeuralNetwork network) {
		super(network);
	}

	@Override
	public void calculate() {
		this.activation = 1.0;
	}

	@Override
	public double getInput() {
		return 0;
	}

}

abstract class Gene {
	// historical marker
	int innovationNumber;

	public Gene(int innovationNumber) {
		this.innovationNumber = innovationNumber;
	}

	public abstract Gene copy();
}

class NodeGene extends Gene {
	int nodeID;

	NodeType type;

	public NodeGene(int innovationNumber, int nodeID, NodeType type) {
		super(innovationNumber);
		this.nodeID = nodeID;
		this.type = type;
	}

	@Override
	public NodeGene copy() {
		return new NodeGene(this.innovationNumber, this.nodeID, this.type);
	}

	@Override
	public String toString() {
		String str = "";

		str += "{Innov: " + this.innovationNumber + ", Node " + this.nodeID + ", Type: " + this.type + "}";

		return str;
	}

}

enum NodeType {
	BIAS, INPUT, HIDDEN, OUTPUT
}

class ConnectionGene extends Gene {
	int inputNodeID;
	int outputNodeID;
	double connectionWeight;

	boolean enabled;

	public ConnectionGene(int innovationNumber, int inputNodeID, int outputNodeID, double connectionWeight,
			boolean enabled) {
		super(innovationNumber);
		this.inputNodeID = inputNodeID;
		this.outputNodeID = outputNodeID;
		this.connectionWeight = connectionWeight;
		this.enabled = enabled;
	}

	@Override
	public ConnectionGene copy() {
		return new ConnectionGene(this.innovationNumber, this.inputNodeID, this.outputNodeID, this.connectionWeight,
				this.enabled);
	}

	@Override
	public String toString() {
		String str = "";

		str += "{Innov: " + this.innovationNumber + ", " + this.inputNodeID + " -> " + this.outputNodeID + ", Weight: "
				+ this.connectionWeight + ", " + this.enabled + "}";

		return str;
	}
}

class NEATNNGeno {
	// List<NodeGene> nodeGenes;
	List<ConnectionGene> connectionGenes;

	// bias here is 0
	boolean bias = true;
	// inputs are 1..numInputNodes
	int numInputNodes;
	// outputs are numInputNodes+1..numInputNodes+numOutputNodes
	int numOutputNodes;
	// hidden are
	// numInputNodes+numOutputNodes+1..numInputNodes+numOutputNodes+numHiddenNodes
	int numHiddenNodes;

	public NEATNNGeno(boolean bias, int numInputNodes, int numOutputNodes, int numHiddenNodes) {

		this.bias = bias;
		this.numInputNodes = numInputNodes;
		this.numOutputNodes = numOutputNodes;
		this.numHiddenNodes = numHiddenNodes;
		// this.nodeGenes = new ArrayList<>();

		this.connectionGenes = new ArrayList<>();
	}

	public NEATNNGeno crossover(NEATNNGeno geno) {
		// List<ConnectionGene> agenes = new ArrayList<>();
		// genes.addAll(this.nodeGenes);
		// agenes.addAll(this.connectionGenes);

		// List<ConnectionGene> bgenes = new ArrayList<>();
		// genes2.addAll(geno.nodeGenes);
		// bgenes.addAll(geno.connectionGenes);

		// Comparator<Gene> comparator = (o1, o2) -> o1.innovationNumber -
		// o2.innovationNumber;
		// agenes.sort(comparator);
		// bgenes.sort(comparator);

		NEATNNGeno child = new NEATNNGeno(this.bias || geno.bias, Math.max(this.numInputNodes, geno.numInputNodes),
				Math.max(this.numOutputNodes, geno.numOutputNodes), Math.max(this.numHiddenNodes, geno.numHiddenNodes));

		int i1 = 0;
		int i2 = 0;
		// List<Gene> childGenes = new ArrayList<>();
		while (i1 < this.connectionGenes.size() || i2 < geno.connectionGenes.size()) {
			if (i1 >= this.connectionGenes.size()) {
				child.connectionGenes.add(geno.connectionGenes.get(i2).copy());
				i2++;
				continue;
			}
			if (i2 >= geno.connectionGenes.size()) {
				child.connectionGenes.add(this.connectionGenes.get(i1).copy());
				i1++;
				continue;
			}
			ConnectionGene g1 = this.connectionGenes.get(i1);
			ConnectionGene g2 = geno.connectionGenes.get(i2);
			if (g1.innovationNumber == g2.innovationNumber) {
				ConnectionGene g = g1.copy();
				g.enabled = g1.enabled && g2.enabled;

				child.connectionGenes.add(g);
				i1++;
				i2++;
			} else if (g1.innovationNumber < g2.innovationNumber) {
				// g1 is disjoint
				child.connectionGenes.add(g1.copy());
				i1++;
			} else {
				// g2 is disjoint
				child.connectionGenes.add(g2.copy());
				i2++;
			}
		}

		return child;
	}

	@Override
	public String toString() {
		String str = "{\n";

		if (this.bias) {
			str += "\t{Node 0, Type: BIAS}\n";
		}
		for (int i = 1; i <= this.numInputNodes; i++) {
			str += "\t{Node " + i + ", Type: INPUT}\n";
		}
		for (int i = this.numInputNodes + 1; i <= this.numInputNodes + this.numOutputNodes; i++) {
			str += "\t{Node " + i + ", Type: OUTPUT}\n";
		}
		for (int i = this.numInputNodes + this.numOutputNodes + 1; i <= this.numInputNodes + this.numOutputNodes
				+ this.numHiddenNodes; i++) {
			str += "\t{Node " + i + ", Type: HIDDEN}\n";
		}

		/*
		 * for (NodeGene nodeGene : this.nodeGenes) { str += "\t" + nodeGene + "\n"; }
		 */
		for (ConnectionGene connectionGene : this.connectionGenes) {
			str += "\t" + connectionGene + "\n";
		}

		return str + "}";
	}
}

class NEATNeuralNetwork {

	List<NEATNeuron> inputNeurons;
	List<NEATNeuron> outputNeurons;

	List<NEATNeuron> hiddenNeurons;

	public NEATNeuralNetwork(NEATNNGeno geno) {
		this.inputNeurons = new ArrayList<>();
		this.outputNeurons = new ArrayList<>();
		this.hiddenNeurons = new ArrayList<>();

		this.buildFromGeno(geno);
	}

	public void calculate() {
		for (NEATNeuron outputNeuron : this.outputNeurons) {
			if (!outputNeuron.calculated) {
				outputNeuron.calculate();
			}
		}
	}

	public void invalidateAll() {
		for (NEATNeuron neuron : this.outputNeurons) {
			neuron.invalidate();
		}
		for (NEATNeuron neuron : this.inputNeurons) {
			neuron.invalidate();
		}
		for (NEATNeuron neuron : this.hiddenNeurons) {
			neuron.invalidate();
		}
	}

	public void buildFromGeno(NEATNNGeno geno) {
		Map<Integer, NEATNeuron> neurons = new HashMap<>();

		if (geno.bias) {
			// str += "\t{Node 0, Type: BIAS}\n";
			BiasNeuron biasNeuron = new BiasNeuron(this);
			neurons.put(0, biasNeuron);
			this.inputNeurons.add(biasNeuron);
		}
		for (int i = 1; i <= geno.numInputNodes; i++) {
			// str += "\t{Node " + i + ", Type: INPUT}\n";
			NEATInputNeuron inputNeuron = new NEATInputNeuron(this) {
				@Override
				public double getInput() {
					return 0;
				}
			};
			neurons.put(i, inputNeuron);
			this.inputNeurons.add(inputNeuron);
		}
		for (int i = geno.numInputNodes + 1; i <= geno.numInputNodes + geno.numOutputNodes; i++) {
			// str += "\t{Node " + i + ", Type: OUTPUT}\n";
			NEATNeuron outputNeuron = new NEATNeuron(this);
			neurons.put(i, outputNeuron);
			this.outputNeurons.add(outputNeuron);
		}
		for (int i = geno.numInputNodes + geno.numOutputNodes + 1; i <= geno.numInputNodes + geno.numOutputNodes
				+ geno.numHiddenNodes; i++) {
			// str += "\t{Node " + i + ", Type: HIDDEN}\n";
			NEATNeuron hiddenNeuron = new NEATNeuron(this);
			neurons.put(i, hiddenNeuron);
			this.hiddenNeurons.add(hiddenNeuron);
		}

		for (ConnectionGene connectionGene : geno.connectionGenes) {
			if (connectionGene.enabled) {
				NEATNeuron outputNeuron = neurons.get(connectionGene.outputNodeID);
				NEATNeuron inputNeuron = neurons.get(connectionGene.inputNodeID);
				outputNeuron.addConnection(inputNeuron, connectionGene.connectionWeight);
			}
		}

	}
}
