package machinelearning.ne.neat;

import java.util.ArrayList;

import machinelearning.ne.neat.genome.Genome;
import math.AKRandom;

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
		representative = null;
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
	
	public Genome selectGenome() {
		return this.get((int) (Math.random()*this.size()));
	}
	public Genome giveBaby(NEAT neat, NEATTrainer trainer) {
		Genome baby;
	    if (AKRandom.randomChance(0.25)) {//25% of the time there is no crossover and the child is simply a clone of a random(ish) player
	      baby =  selectGenome().clone();
	    } else {//75% of the time do crossover 

	      //get 2 random(ish) parents 
	      Genome parent1 = selectGenome();
	      Genome parent2 = selectGenome();

	      //the crossover function expects the highest fitness parent to be the object and the lowest as the argument
	      baby = trainer.crossover(parent1, parent2, neat);
	      
	    }
	    baby = trainer.mutateCBullet(baby, neat);//mutate that baby brain
	    return baby;
	}

}
