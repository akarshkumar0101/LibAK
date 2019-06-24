package machinelearning.ne.neat.genome;

import java.util.ArrayList;
import java.util.List;

public class Genome {
	// List<NodeGene> nodeGenes;
	private final List<ConnectionGene> getConnectionGenes;

	private final BaseTemplate baseTemplate;
	// bias here is 0

	// inputs are 1..numInputNodes

	// outputs are numInputNodes+1..numInputNodes+numOutputNodes

	// hidden are
	// numInputNodes+numOutputNodes+1..numInputNodes+numOutputNodes+numHiddenNodes
	private final int numHiddenNodes;

	public Genome(BaseTemplate baseTemplate, int numHiddenNodes) {

		this.baseTemplate = baseTemplate;
		this.numHiddenNodes = numHiddenNodes;
		// this.nodeGenes = new ArrayList<>();

		this.getConnectionGenes = new ArrayList<>();
	}

	@Override
	public String toString() {
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
		for (ConnectionGene connectionGene : this.getConnectionGenes) {
			str += "\t" + connectionGene + "\n";
		}

		return str + "}";
	}

	public List<ConnectionGene> getConnectionGenes() {
		return getConnectionGenes;
	}

	public BaseTemplate getBaseTemplate() {
		return baseTemplate;
	}

	public int getNumHiddenNodes() {
		return numHiddenNodes;
	}
	
}


