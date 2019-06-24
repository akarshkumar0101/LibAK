package machinelearning.ne.neat.genome;

public class ConnectionGene extends Gene {
	private final int inputNodeID;
	private final int outputNodeID;
	private double connectionWeight;

	private boolean enabled;

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

	public int getInputNodeID() {
		return this.inputNodeID;
	}

	public int getOutputNodeID() {
		return this.outputNodeID;
	}

	public double getConnectionWeight() {
		return this.connectionWeight;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setConnectionWeight(double connectionWeight) {
		this.connectionWeight = connectionWeight;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
