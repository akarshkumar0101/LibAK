package machinelearning.neuralnet;

import data.tuple.Tuple2D;

//D = Data, A = Answer
public interface NeuralNetworkTrainer<D, A> {

	public abstract double[] rawToInputLayer(D d);

	public abstract double[] rawToOutputLayer(A a);

	/**
	 * @return a [? instance of D, ? instance of A];
	 */
	public abstract Tuple2D<D, A> getRandomRawData();

	// return a double such that arr[0] is the input and arr[1] is the
	// expectedOutput
	public default double[][] getRandomTrainingExample() {
		Tuple2D<D, A> da = getRandomRawData();
		D d = da.getA();
		A a = da.getB();
		return new double[][] { rawToInputLayer(d), rawToOutputLayer(a) };
	}

	public default double[][][] getRandomTrainingExamples(int batchSize) {
		double[][][] examples = new double[batchSize][][];
		for (int i = 0; i < examples.length; i++) {
			examples[i] = getRandomTrainingExample();
		}
		return examples;
	}

	public static final double STANDARD_ACCEPTABLE_ERROR = .05;

	public default double acceptableOutputError() {
		return STANDARD_ACCEPTABLE_ERROR;
	}

	public default boolean isCorrect(double[] output, double[] expectedOutput) {
		for (int i = 0; i < output.length; i++) {
			if (Math.abs((output[i] - expectedOutput[i]) / expectedOutput[i]) > acceptableOutputError())
				return false;
		}
		return true;
	}

	public default double getCost(double[] output, double[] expectedOutput) {
		double cost = 0;
		for (int i = 0; i < output.length; i++) {
			cost += Math.pow(output[i] - expectedOutput[i], 2);
		}
		return cost;
	}

	public default double getDerivativeOfCost(double output, double expectedOutput) {
		return 2 * (output - expectedOutput);
	}

	public abstract double getWeightLearningRate();

	public default double getBiasLearningRate() {
		return getWeightLearningRate();
	}

	public default int getN() {
		return Integer.MAX_VALUE;
	}

	public default void runEveryNIterations(double[] beforePerformance, double[] afterPerformance) {
	}

}
