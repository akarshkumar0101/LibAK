package machinelearning.neuralnet;

import java.util.ArrayList;
import java.util.List;

public class TrackableNeuralNetwork extends NeuralNetwork {

	private final List<NeuralNetworkListener> listeners;

	public TrackableNeuralNetwork(int... nodesperlayer) {
		super(nodesperlayer);
		listeners = new ArrayList<>();
	}

	public TrackableNeuralNetwork(TrackableNeuralNetwork net) {
		super(net);
		listeners = new ArrayList<>();
	}

	public TrackableNeuralNetwork(Object parameters) {
		super(parameters);
		listeners = new ArrayList<>();
	}

	public TrackableNeuralNetwork(int[] nodesPerLayer, double[][] nodes, double[][][] weights, double[][] biases) {
		super(nodesPerLayer, nodes, weights, biases);
		listeners = new ArrayList<>();
	}

	public void addNeuralNetworkListener(NeuralNetworkListener listener) {
		listeners.add(listener);
	}

	public void removeNeuralNetworkListener(NeuralNetworkListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void calculateFully() {
		calculateLayer(1, true);
		for (NeuralNetworkListener listener : listeners) {
			listener.changedNodesInLayer(networkDimensions.length - 1);
		}
	}

	@Override
	protected void calculateLayer(int layer, boolean recurseFully) {
		for (NeuralNetworkListener listener : listeners) {
			listener.changedNodesInLayer(layer);
		}
		super.calculateLayer(layer, recurseFully);
	}

	@Override
	protected void gradientDescent(double[][][] examples) {
		super.gradientDescent(examples);

		for (NeuralNetworkListener listener : listeners) {
			listener.changedWeightsAndBiases();
		}
	}

	@Override
	protected void calculatePartials(double[] expectedOutput, boolean recurseFully) {
		for (NeuralNetworkListener listener : listeners) {
			listener.changedExpectedOutput(expectedOutput);
		}

		super.calculatePartials(expectedOutput, recurseFully);

		for (NeuralNetworkListener listener : listeners) {
			listener.changedPartials(1);
		}
	}

	@Override
	protected void calculatePartials(int layer, boolean recurseFully) {
		for (NeuralNetworkListener listener : listeners) {
			listener.changedPartials(layer);
		}
		super.calculatePartials(layer, recurseFully);
	}

	/**
	 * trainingExamples[0] is the input layer
	 * 
	 * trainingExamples[1] is the expected output layer
	 * 
	 * @param trainingExamples
	 * @return the cost of the network ("how bad it is") and boolean if it was
	 *         correct result
	 */
	@Override
	public Object[] calculatePerformance(double[][] trainingExample) {
		for (NeuralNetworkListener listener : listeners) {
			listener.changedExpectedOutput(trainingExample[1]);
		}

		return super.calculatePerformance(trainingExample);
	}
}
