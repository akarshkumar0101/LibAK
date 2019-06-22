package machinelearning.ne.neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NEATNeuralNetwork {

	List<NEATNeuron> inputNeurons;
	List<NEATNeuron> outputNeurons;

	List<NEATNeuron> hiddenNeurons;

	boolean hasBias;

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
		this.hasBias = geno.hasBias;
		Map<Integer, NEATNeuron> neurons = new HashMap<>();

		if (geno.hasBias) {
			// str += "\t{Node 0, Type: BIAS}\n";
			BiasNeuron biasNeuron = new BiasNeuron(this);
			neurons.put(0, biasNeuron);
			this.inputNeurons.add(biasNeuron);
		}
		for (int i = 1; i <= geno.numInputNodes; i++) {
			// str += "\t{Node " + i + ", Type: INPUT}\n";
			NEATInputNeuron inputNeuron = new NEATInputNeuron(this) {
				@Override
				public double getInput() {
					return 0;
				}
			};
			neurons.put(i, inputNeuron);
			this.inputNeurons.add(inputNeuron);
		}
		for (int i = geno.numInputNodes + 1; i <= geno.numInputNodes + geno.numOutputNodes; i++) {
			// str += "\t{Node " + i + ", Type: OUTPUT}\n";
			NEATNeuron outputNeuron = new NEATNeuron(this);
			neurons.put(i, outputNeuron);
			this.outputNeurons.add(outputNeuron);
		}
		for (int i = geno.numInputNodes + geno.numOutputNodes + 1; i <= geno.numInputNodes + geno.numOutputNodes
				+ geno.numHiddenNodes; i++) {
			// str += "\t{Node " + i + ", Type: HIDDEN}\n";
			NEATNeuron hiddenNeuron = new NEATNeuron(this);
			neurons.put(i, hiddenNeuron);
			this.hiddenNeurons.add(hiddenNeuron);
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
