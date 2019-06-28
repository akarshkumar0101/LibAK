package machinelearning.ne.neat.genome;

public class NodeGene extends Gene {
	int nodeID;

	NodeType type;

	public NodeGene(int innovationNumber, int nodeID, NodeType type) {
		super(innovationNumber);
		this.nodeID = nodeID;
		this.type = type;
	}

	@Override
	public NodeGene clone() {
		return new NodeGene(this.innovationNumber, this.nodeID, this.type);
	}

	@Override
	public String toString() {
		String str = "";

		str += "{Innov: " + this.innovationNumber + ", Node " + this.nodeID + ", Type: " + this.type + "}";

		return str;
	}

	public static enum NodeType {
		BIAS, INPUT, HIDDEN, OUTPUT
	}
}
