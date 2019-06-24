package machinelearning.ne.neat.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import machinelearning.ne.neat.genome.ConnectionGene;
import machinelearning.ne.neat.genome.Genome;
import machinelearning.neuralnet.InputSource;

public class NeuralNetwork {

	private final List<Neuron> inputNeurons;
	private final List<Neuron> outputNeurons;

	private final List<Neuron> hiddenNeurons;

	private boolean hasBias;
	
	private final InputSource inputSource;

	public NeuralNetwork(Genome geno, InputSource inputSource) {
		this.inputNeurons = new ArrayList<>();
		this.outputNeurons = new ArrayList<>();
		this.hiddenNeurons = new ArrayList<>();
		
		this.inputSource = inputSource;

		this.buildFromGeno(geno);
	}

	public void calculate() {
		for (Neuron outputNeuron : this.outputNeurons) {
			if (!outputNeuron.calculated) {
				outputNeuron.calculate();
			}
		}
	}

	public void invalidateAll() {
		for (Neuron neuron : this.outputNeurons) {
			neuron.invalidate();
		}
		for (Neuron neuron : this.inputNeurons) {
			neuron.invalidate();
		}
		for (Neuron neuron : this.hiddenNeurons) {
			neuron.invalidate();
		}
	}

	public void buildFromGeno(Genome geno) {
		this.hasBias = geno.getBaseTemplate().hasBias();
		Map<Integer, Neuron> neurons = new HashMap<>();

		if (geno.getBaseTemplate().hasBias()) {
			// str += "\t{Node 0, Type: BIAS}\n";
			BiasNeuron biasNeuron = new BiasNeuron(this);
			neurons.put(0, biasNeuron);
			this.inputNeurons.add(biasNeuron);
		}
		for (int i = 1; i <= geno.getBaseTemplate().numInputNodes(); i++) {
			// str += "\t{Node " + i + ", Type: INPUT}\n";
			final int inputI=i-1;
			NEATInputNeuron inputNeuron = new NEATInputNeuron(this) {
				@Override
				public double getInput() {
					return inputSource.getInput(inputI);
				}
			};
			neurons.put(i, inputNeuron);
			this.inputNeurons.add(inputNeuron);
		}
		for (int i = geno.getBaseTemplate().numInputNodes() + 1; i <= geno.getBaseTemplate().numInputNodes()
				+ geno.getBaseTemplate().numOutputNodes(); i++) {
			// str += "\t{Node " + i + ", Type: OUTPUT}\n";
			Neuron outputNeuron = new Neuron(this);
			neurons.put(i, outputNeuron);
			this.outputNeurons.add(outputNeuron);
		}
		for (int i = geno.getBaseTemplate().numInputNodes() + geno.getBaseTemplate().numOutputNodes() + 1; i <= geno.getBaseTemplate()
				.numInputNodes() + geno.getBaseTemplate().numOutputNodes() + geno.getNumHiddenNodes(); i++) {
			// str += "\t{Node " + i + ", Type: HIDDEN}\n";
			Neuron hiddenNeuron = new Neuron(this);
			neurons.put(i, hiddenNeuron);
			this.hiddenNeurons.add(hiddenNeuron);
		}

		for (ConnectionGene connectionGene : geno.getConnectionGenes()) {
			if (connectionGene.isEnabled()) {
				Neuron outputNeuron = neurons.get(connectionGene.getOutputNodeID());
				Neuron inputNeuron = neurons.get(connectionGene.getInputNodeID());
				outputNeuron.addConnection(inputNeuron, connectionGene.getConnectionWeight());
			}
		}

	}
	
	public boolean hasBias() {
		return hasBias;
	}

	public List<Neuron> getInputNeurons() {
		return inputNeurons;
	}

	public List<Neuron> getOutputNeurons() {
		return outputNeurons;
	}

	public List<Neuron> getHiddenNeurons() {
		return hiddenNeurons;
	}
}
