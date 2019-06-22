package machinelearning.ne.neat;

import java.util.ArrayList;
import java.util.List;

public class NEATNNGeno {
	// List<NodeGene> nodeGenes;
	List<ConnectionGene> connectionGenes;

	// bias here is 0
	boolean hasBias = true;
	// inputs are 1..numInputNodes
	int numInputNodes;
	// outputs are numInputNodes+1..numInputNodes+numOutputNodes
	int numOutputNodes;
	// hidden are
	// numInputNodes+numOutputNodes+1..numInputNodes+numOutputNodes+numHiddenNodes
	int numHiddenNodes;

	public NEATNNGeno(boolean bias, int numInputNodes, int numOutputNodes, int numHiddenNodes) {

		this.hasBias = bias;
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

		NEATNNGeno child = new NEATNNGeno(this.hasBias || geno.hasBias,
				Math.max(this.numInputNodes, geno.numInputNodes), Math.max(this.numOutputNodes, geno.numOutputNodes),
				Math.max(this.numHiddenNodes, geno.numHiddenNodes));

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

	public double similarity(NEATNNGeno geno, double c1, double c2, double c3) {
		int N = Math.max(this.connectionGenes.size(), geno.connectionGenes.size());

		int numExcess = 0;
		int numDisjoint = 0;
		double avgWeightDiff = 0;
		int numCommonGenes = 0;

		int i1 = 0;
		int i2 = 0;
		// List<Gene> childGenes = new ArrayList<>();
		while (i1 < this.connectionGenes.size() || i2 < geno.connectionGenes.size()) {
			if (i1 >= this.connectionGenes.size()) {
				numExcess++;
				i2++;
				continue;
			}
			if (i2 >= geno.connectionGenes.size()) {
				numExcess++;
				i1++;
				continue;
			}
			ConnectionGene g1 = this.connectionGenes.get(i1);
			ConnectionGene g2 = geno.connectionGenes.get(i2);
			if (g1.innovationNumber == g2.innovationNumber) {
				numCommonGenes++;
				avgWeightDiff += Math.abs(g1.connectionWeight - g2.connectionWeight);

				i1++;
				i2++;
			} else if (g1.innovationNumber < g2.innovationNumber) {
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

	@Override
	public String toString() {
		String str = "{\n";

		if (this.hasBias) {
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
