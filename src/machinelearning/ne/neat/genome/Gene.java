package machinelearning.ne.neat.genome;

public abstract class Gene {
	// historical marker
	protected final int innovationNumber;

	public Gene(int innovationNumber) {
		this.innovationNumber = innovationNumber;
	}

	public abstract Gene copy();

	public int getInnovationNumber() {
		return this.innovationNumber;
	}
}
