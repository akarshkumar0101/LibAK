package machinelearning.ne;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NEAT {
	public static void main(String... args) {
		NEATNNGeno geno = new NEATNNGeno(true, 2, 1, 1);
		geno.nodeGenes.add(new NodeGene(0, 0, NodeType.BIAS));
		geno.nodeGenes.add(new NodeGene(1, 1, NodeType.INPUT));
		geno.nodeGenes.add(new NodeGene(2, 2, NodeType.INPUT));
		geno.nodeGenes.add(new NodeGene(3, 3, NodeType.OUTPUT));
		geno.nodeGenes.add(new NodeGene(4, 4, NodeType.HIDDEN));

		geno.connectionGenes.add(new ConnectionGene(5, 0, 4, 0.3, true));
		geno.connectionGenes.add(new ConnectionGene(6, 1, 4, 5.0, true));
		geno.connectionGenes.add(new ConnectionGene(7, 2, 4, 2.0, true));
		geno.connectionGenes.add(new ConnectionGene(8, 2, 3, 1.7, true));
		geno.connectionGenes.add(new ConnectionGene(9, 4, 3, -0.2, true));
		geno.connectionGenes.add(new ConnectionGene(10, 0, 2, 0.56, false));

		NEATNeuralNetwork network = new NEATNeuralNetwork(geno);

		// network.calculate();

		System.out.println(geno);
	}

	public static void test1() {
		NEATNNGeno geno = new NEATNNGeno(true, 2, 1, 1);
		geno.nodeGenes.add(new NodeGene(0, 0, NodeType.BIAS));
		geno.nodeGenes.add(new NodeGene(1, 1, NodeType.INPUT));
		geno.nodeGenes.add(new NodeGene(2, 2, NodeType.INPUT));
		geno.nodeGenes.add(new NodeGene(3, 3, NodeType.OUTPUT));
		geno.nodeGenes.add(new NodeGene(4, 4, NodeType.HIDDEN));

		geno.connectionGenes.add(new ConnectionGene(5, 0, 4, 0.3, true));
		geno.connectionGenes.add(new ConnectionGene(6, 1, 4, 5.0, true));
		geno.connectionGenes.add(new ConnectionGene(7, 2, 4, 2.0, true));
		geno.connectionGenes.add(new ConnectionGene(8, 2, 3, 1.7, true));
		geno.connectionGenes.add(new ConnectionGene(9, 4, 3, -0.2, true));
		geno.connectionGenes.add(new ConnectionGene(10, 0, 2, 0.56, false));

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
	List<NodeGene> nodeGenes;
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
		this.nodeGenes = new ArrayList<>();
		this.connectionGenes = new ArrayList<>();

		this.bias = bias;
		this.numInputNodes = numInputNodes;
		this.numOutputNodes = numOutputNodes;
		this.numHiddenNodes = numHiddenNodes;
	}

	public NEATNNGeno crossover(NEATNNGeno geno) {
		List<Gene> genes = new ArrayList<>();
		genes.addAll(this.nodeGenes);
		genes.addAll(this.connectionGenes);

		List<Gene> genes2 = new ArrayList<>();
		genes2.addAll(geno.nodeGenes);
		genes2.addAll(geno.connectionGenes);

		Comparator<Gene> comparator = (o1, o2) -> o1.innovationNumber - o2.innovationNumber;
		genes.sort(comparator);
		genes2.sort(comparator);

		NEATNNGeno child = new NEATNNGeno(this.bias, this.numInputNodes, this.numOutputNodes, this.numHiddenNodes);

		int i1 = 0;
		int i2 = 0;
		List<Gene> childGenes = new ArrayList<>();
		while (i1 < genes.size() || i2 < genes2.size()) {
			if (i1 >= genes.size()) {
				childGenes.add(genes2.get(i2).copy());
				i2++;
			}
			if (i2 >= genes2.size()) {
				childGenes.add(genes.get(i1).copy());
				i1++;
			}
			Gene g1 = genes.get(i1), g2 = genes.get(i2);
			if (g1.innovationNumber == g2.innovationNumber) {
				Gene g = g1.copy();
				if (g instanceof ConnectionGene) {
					((ConnectionGene) g).enabled = ((ConnectionGene) g1).enabled && ((ConnectionGene) g2).enabled;
				} else {
					childGenes.add(g);
				}
			} else if (g1.innovationNumber < g2.innovationNumber) {
				// g1 is disjoint
				childGenes.add(g1.copy());
			} else {
				// g2 is disjoint
				childGenes.add(g2.copy());
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

		for (NodeGene nodeGene : this.nodeGenes) {
			str += "\t" + nodeGene + "\n";
		}
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
		for (NodeGene nodeGene : geno.nodeGenes) {
			NEATNeuron neuron;
			switch (nodeGene.type) {
			case BIAS:
				neuron = new BiasNeuron(this);
				break;
			case INPUT:
				neuron = new NEATInputNeuron(this) {
					@Override
					public double getInput() {
						return 0;
					}
				};
				break;
			default:
				neuron = new NEATNeuron(this);
				break;
			}
			neurons.put(nodeGene.nodeID, neuron);

			switch (nodeGene.type) {
			case BIAS:
				this.inputNeurons.add(neuron);
				break;
			case INPUT:
				this.inputNeurons.add(neuron);
				break;
			case OUTPUT:
				this.outputNeurons.add(neuron);
				break;
			default:
				this.hiddenNeurons.add(neuron);
				break;
			}

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
