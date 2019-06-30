package machinelearning.ne.neat.genome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.tuple.Tuple2D;

public class Genome extends ArrayList<ConnectionGene> {

	private static final long serialVersionUID = -5784317383291473781L;

	// List<NodeGene> nodeGenes;

	private final BaseTemplate baseTemplate;
	// bias here is 0

	// inputs are 1..numInputNodes

	// outputs are numInputNodes+1..numInputNodes+numOutputNodes

	// hidden are
	// numInputNodes+numOutputNodes+1..numInputNodes+numOutputNodes+numHiddenNodes
	private int numHiddenNodes;

	public double fitness;

	public static int GenomeIDGenerator = 0;
	public final int genomeID;
	
	public static final Map<Integer,Genome> GENOMES = new HashMap<>(); 

	public Genome(BaseTemplate baseTemplate, int numHiddenNodes) {
		super();
		this.baseTemplate = baseTemplate;
		this.numHiddenNodes = numHiddenNodes;
		// this.nodeGenes = new ArrayList<>();

		this.genomeID = Genome.GenomeIDGenerator++;
		
		GENOMES.put(genomeID, this);
	}

	public boolean hasConnection(int inputNodeID, int outputNodeID) {
		for (ConnectionGene cg : this) {
			if (cg.getInputNodeID() == inputNodeID && cg.getOutputNodeID() == outputNodeID)
				return true;
		}
		return false;
	}
	public Tuple2D<Integer,Integer> complexity() {
		return new Tuple2D<>(this.numHiddenNodes, this.size());
	}

	public void calculateNumHiddenNodes() {
		int numHiddenNodes = 0;

		for (ConnectionGene cg : this) {
			numHiddenNodes = Math.max(numHiddenNodes, cg.getInputNodeID());
			numHiddenNodes = Math.max(numHiddenNodes, cg.getOutputNodeID());
		}
		numHiddenNodes -= (baseTemplate.numInputNodes() + baseTemplate.numOutputNodes());

		this.numHiddenNodes = numHiddenNodes;
	}

	public void cleanup() {
		this.sort(new Comparator<ConnectionGene>() {
			@Override
			public int compare(ConnectionGene o1, ConnectionGene o2) {
				return o1.getInnovationNumber() - o2.getInnovationNumber();
			}
		});
		this.calculateNumHiddenNodes();
	}

	/**
	 * @return the id of the new hidden node
	 */
	public int addNewHiddenNode() {
		int newNodeID = this.getNumTotalNodes();
		if (!baseTemplate.hasBias()) {
			newNodeID++;
		}
		this.numHiddenNodes++;
		
		return newNodeID;
	}

	public int layer(int id) {
		int layer = 0;
		if (id > baseTemplate.numInputNodes()) {
			layer = 2;
		}
		if (id > baseTemplate.numInputNodes() + numHiddenNodes) {
			layer = 1;
		}
		return layer;
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
		for (ConnectionGene connectionGene : this) {
			str += "\t" + connectionGene + "\n";
		}

		return str + "}";
	}

	public String toStringfffffff() {
		String str = "{";

		str += this.numHiddenNodes + " hidden, " + this.size() + " connections, fitness: " + this.fitness;

		return str + "}";
	}

	@Override
	public String toString() {
		String str = "";

		str += "Genome ID: " + genomeID;
		str += '\n';

		str += "Fitness: " + fitness;
		str += '\n';

		List<ConnectionGene> genes = new ArrayList<>();
		int listI = 0;
		for (int innov = 0; innov <= this.get(this.size() - 1).getInnovationNumber(); innov++) {
			if (innov == this.get(listI).getInnovationNumber()) {
				genes.add(this.get(listI));
				listI++;
			} else {
				genes.add(null);
			}

		}

		for (int i = 0; i < genes.size() * 7 + 1; i++) {
			str += "_";
		}
		str += '\n';

		for (int i = 0; i < genes.size(); i++) {
			str += "|";
			str += "  ";
			if (genes.get(i) != null) {
				str += String.format("%2d", genes.get(i).getInnovationNumber());
			} else {
				str += "  ";
			}
			str += "  ";
		}
		str += "|";
		str += '\n';

		for (int i = 0; i < genes.size(); i++) {
			str += "|";
			if (genes.get(i) != null) {
				str += String.format("%2d", genes.get(i).getInputNodeID());
			} else {
				str += "  ";
			}
			if (genes.get(i) != null) {
				str += "->";
			} else {
				str += "  ";
			}
			if (genes.get(i) != null) {
				str += String.format("%2d", genes.get(i).getOutputNodeID());
			} else {
				str += "  ";
			}
		}
		str += "|";
		str += '\n';

		for (int i = 0; i < genes.size(); i++) {
			str += "|";

			if (genes.get(i) != null) {
				if (genes.get(i).getConnectionWeight() >= 0) {
					str += " ";
				}
				str += String.format("%,.2f", genes.get(i).getConnectionWeight());
			} else {
				str += "     ";
			}
			str += " ";

		}
		str += "|";
		str += '\n';

		for (int i = 0; i < genes.size(); i++) {
			str += "|";
			str += "  ";

			if (genes.get(i) != null) {
				str += genes.get(i).isEnabled() ? "  " : " D";
			} else {
				str += "  ";
			}
			str += "  ";
		}
		str += "|";
		str += '\n';

		for (int i = 0; i < genes.size() * 7 + 1; i++) {
			str += "-";
		}
		str += '\n';

		return str;
	}

	public List<ConnectionGene> getConnectionGenes() {
		return this;
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
		for (ConnectionGene cg : this) {
			geno.add(cg.clone());
		}
		return geno;
	}
}
