package machinelearning.ne.neat.genome;

import java.util.ArrayList;
import java.util.List;

public class Genome {
	// List<NodeGene> nodeGenes;
	private final List<ConnectionGene> connectionGenes;

	private final BaseTemplate baseTemplate;
	// bias here is 0

	// inputs are 1..numInputNodes

	// outputs are numInputNodes+1..numInputNodes+numOutputNodes

	// hidden are
	// numInputNodes+numOutputNodes+1..numInputNodes+numOutputNodes+numHiddenNodes
	private int numHiddenNodes;

	public double fitness;

	public Genome(BaseTemplate baseTemplate, int numHiddenNodes) {

		this.baseTemplate = baseTemplate;
		this.numHiddenNodes = numHiddenNodes;
		// this.nodeGenes = new ArrayList<>();

		this.connectionGenes = new ArrayList<>();
	}

	public boolean hasConnection(int inputNodeID, int outputNodeID) {
		for (ConnectionGene cg : this.connectionGenes) {
			if (cg.getInputNodeID() == inputNodeID && cg.getOutputNodeID() == outputNodeID)
				return true;
		}
		return false;
	}

	/**
	 * @return the id of the new hidden node
	 */
	public int addNewHiddenNode() {
		int newNodeID = this.getNumTotalNodes();
		this.numHiddenNodes++;
		return newNodeID;
	}

	public String toStringReal() {
		String str = "{\n";

		if (this.baseTemplate.hasBias()) {
			str += "\t{Node 0, Type: BIAS}\n";
		}
		for (int i = 1; i <= this.baseTemplate.numInputNodes(); i++) {
			str += "\t{Node " + i + ", Type: INPUT}\n";
		}
		for (int i = this.baseTemplate.numInputNodes() + 1; i <= this.baseTemplate.numInputNodes()
				+ this.baseTemplate.numOutputNodes(); i++) {
			str += "\t{Node " + i + ", Type: OUTPUT}\n";
		}
		for (int i = this.baseTemplate.numInputNodes() + this.baseTemplate.numOutputNodes() + 1; i <= this.baseTemplate
				.numInputNodes() + this.baseTemplate.numOutputNodes() + this.numHiddenNodes; i++) {
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

	@Override
	public String toString() {
		String str = "{";

		str += this.numHiddenNodes + " hidden, " + this.connectionGenes.size() + " connections, fitness: "
				+ this.fitness;

		return str + "}";
	}

	public List<ConnectionGene> getConnectionGenes() {
		return this.connectionGenes;
	}

	public BaseTemplate getBaseTemplate() {
		return this.baseTemplate;
	}

	public void setNumHiddenNodes(int numHiddenNodes) {
		this.numHiddenNodes = numHiddenNodes;
	}

	public int getNumHiddenNodes() {
		return this.numHiddenNodes;
	}

	public int getNumTotalNodes() {
		return this.baseTemplate.numInputNodes() + this.numHiddenNodes + this.baseTemplate.numOutputNodes()
				+ (this.baseTemplate.hasBias() ? 1 : 0);
	}

	@Override
	public Genome clone() {
		Genome geno = new Genome(this.baseTemplate, this.numHiddenNodes);
		geno.fitness = this.fitness;
		for (ConnectionGene cg : this.connectionGenes) {
			geno.connectionGenes.add(cg.clone());
		}
		return geno;
	}
}
