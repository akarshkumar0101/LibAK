package machinelearning.ne.neat;

import java.util.ArrayList;

import machinelearning.ne.neat.genome.Genome;

public class Species extends ArrayList<Genome> {

	private static final long serialVersionUID = -8114693581105898796L;

	private Genome representative;

	public Species(Genome geno) {
		representative = geno;
		this.add(geno);
	}

	public boolean isExtinct() {
		return this.size() <= 1;
	}

	public void assignNewRandomRepresentative() {
		if (!this.isEmpty()) {
			int randIndex = (int) (Math.random() * this.size());
			this.representative = this.get(randIndex);
		}
	}

	public Genome getRepresentative() {
		return this.representative;
	}

	public void setRepresentative(Genome representative) {
		this.representative = representative;
	}

}
