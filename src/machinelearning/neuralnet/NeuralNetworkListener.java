package machinelearning.neuralnet;

public interface NeuralNetworkListener {

	public default void changedNodesInLayer(int layer) {
	}

	public default void changedWeightsAndBiases() {
	}

	public default void changedPartials(int layer) {
	}

	public default void changedExpectedOutput(double[] expectedOutput) {
	}

}
