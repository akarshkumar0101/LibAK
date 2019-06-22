package machinelearning.ne.neat;

import java.util.HashMap;
import java.util.Map;

public class NEATNeuron {
	double activation;

	boolean calculated;

	Map<NEATNeuron, Double> prevConnections;

	NEATNeuralNetwork network;

	NEATNeuron(NEATNeuralNetwork network) {
		this.network = network;
		this.calculated = false;

		this.prevConnections = new HashMap<>();
	}

	public void addConnection(NEATNeuron prevNeuron, double connectionWeight) {
		this.prevConnections.put(prevNeuron, connectionWeight);
	}

	public void calculate() {
		if (this.calculated)
			return;
		this.calculated = true;
		double input = 0;

		for (NEATNeuron neuron : this.prevConnections.keySet()) {
			double connectionWeight = this.prevConnections.get(neuron);
			if (!neuron.calculated) {
				neuron.calculate();
			}
			input += neuron.activation * connectionWeight;
		}
		this.activation = input;

		this.activationFunc();
	}

	public void invalidate() {
		this.calculated = false;
	}

	public void activationFunc() {
		this.activation = 1 / (1 + Math.exp(-this.activation));
	}
}

abstract class NEATInputNeuron extends NEATNeuron {
	public NEATInputNeuron(NEATNeuralNetwork network) {
		super(network);
	}

	@Override
	public void calculate() {
		this.activation = this.getInput();
		this.activationFunc();
	}

	public abstract double getInput();
}

class BiasNeuron extends NEATInputNeuron {

	public BiasNeuron(NEATNeuralNetwork network) {
		super(network);
	}

	@Override
	public void calculate() {
		this.activation = 1.0;
	}

	@Override
	public double getInput() {
		return 0;
	}

}


