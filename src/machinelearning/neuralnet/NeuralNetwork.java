package machinelearning.neuralnet;

import array.Arrays;
import array.DoubleArrays;
import data.function.DoubleFunction1D;
import math.matrix.TensorMath;

public class NeuralNetwork {

	public final int[] networkDimensions;

	// nodes[layer][nodeid]
	public double[][] nodes;

	// nodes before the sigmoid activation function
	// nodes_Z[layer][nodeid]
	public double[][] nodes_Z;

	// weights between layer1 and layer2
	// weights[layer1][node2id][node1id]
	public double[][][] weights;

	// biases at layer
	// biases[layer-1][nodeid]
	public double[][] biases;

	// these are the partials of the cost function (for given training example) with
	// respect to the following variables

	// nodes[layer][nodeid]
	protected double[][] nodePartials;

	// node_Z_Partials is the node values before sigmoid
	protected double[][] node_Z_Partials;

	// weights between layer1 and layer2
	// weights[layer1][node2id][node1id]
	protected double[][][] weightPartials;

	// biases at layer
	// biases[layer][nodeid]
	protected double[][] biasPartials;

	protected NeuralNetworkTrainer<?, ?> trainer;

	public NeuralNetwork(int... nodesperlayer) {
		this(nodesperlayer, null, null, null);
	}

	public NeuralNetwork(double[][][] weights, double[][] biases) {
		this(determineNetworkDimensions(weights), null, weights, biases);
	}

	public NeuralNetwork(NeuralNetwork net) {
		this(net.networkDimensions, net.nodes, net.weights, net.biases);
	}

	public NeuralNetwork(Object parameters) {
		this((int[]) ((Object[]) parameters)[0], null, (double[][][]) ((Object[]) parameters)[1],
				(double[][]) ((Object[]) parameters)[2]);
	}

	public NeuralNetwork(int[] nodesPerLayer, double[][] nodes, double[][][] weights, double[][] biases) {
		networkDimensions = nodesPerLayer.clone();

		initNetworkAndZ(nodes);
		initWeights(weights);
		initBiases(biases);

		initPartials(false);
	}

	private static int[] determineNetworkDimensions(double[][][] weights) {
		int[] networkDimensions = new int[weights.length + 1];
		for (int i = 0; i < networkDimensions.length - 1; i++) {
			networkDimensions[i] = weights[i][0].length;
		}
		networkDimensions[networkDimensions.length - 1] = Arrays.lastElement(weights).length;
		return networkDimensions;
	}

	public void initNetworkAndZ(double[][] nodes) {
		if (nodes != null) {
			this.nodes = DoubleArrays.deepCopy(nodes);
		} else {
			this.nodes = new double[networkDimensions.length][];
			for (int l = 0; l < networkDimensions.length; l++) {
				this.nodes[l] = new double[networkDimensions[l]];
			}
		}
		nodes_Z = TensorMath.tensorScale(this.nodes, 0);
	}

	public void initWeights(double[][][] weights) {
		if (weights != null) {
			this.weights = DoubleArrays.deepCopy(weights);
		} else {
			this.weights = new double[networkDimensions.length - 1][][];
			for (int l = 0; l < networkDimensions.length - 1; l++) {
				this.weights[l] = new double[networkDimensions[l + 1]][networkDimensions[l]];
			}
		}
	}

	public void initBiases(double[][] biases) {
		if (biases != null) {
			this.biases = DoubleArrays.deepCopy(biases);
		} else {
			this.biases = new double[networkDimensions.length - 1][];
			for (int l = 1; l < networkDimensions.length; l++) {
				this.biases[l - 1] = new double[networkDimensions[l]];
			}
		}
	}

	protected void initPartials(boolean init) {
		if (init) {
			nodePartials = TensorMath.tensorScale(nodes, 0);
			node_Z_Partials = TensorMath.tensorScale(nodes_Z, 0);
			weightPartials = TensorMath.tensorScale(weights, 0);
			biasPartials = TensorMath.tensorScale(biases, 0);
		} else {
			nodePartials = null;
			node_Z_Partials = null;
			weightPartials = null;
			biasPartials = null;
		}
	}

	public Object exportParameters() {
		return new Object[] { networkDimensions.clone(), DoubleArrays.deepCopy(weights),
				DoubleArrays.deepCopy(biases) };
	}

	public void feed(double... inputLayerData) {
		if (inputLayerData.length != nodes[0].length)
			throw new IllegalArgumentException("Must feed input layer with correct size");
		nodes[0] = inputLayerData;
		nodes_Z[0] = DoubleArrays.deepCopy(inputLayerData);
	}

	public double[] getOutput() {
		double[] output = nodes[nodes.length - 1];
		return output.clone();
	}

	public static boolean[] activatedNodes(double[] nodes) {
		boolean[] arr = new boolean[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			arr[i] = (Math.floor(nodes[i] + .5)) == 1;
		}
		return arr;
	}

	public void randomizeWeightsAndBiases() {
		randomizeWeightsAndBiases(-2, 2, -2, 2);
	}

	public void randomizeWeightsAndBiases(double lowlw, double uplw, double lowlb, double uplb) {
		for (double[][] weightmat : weights) {
			for (double[] arr : weightmat) {
				for (int i = 0; i < arr.length; i++) {
					double randweight = (Math.random() - 0.5) * (uplw - lowlw) + ((uplw + lowlw) / 2);
					arr[i] = randweight;
				}
			}

		}
		for (double[] biasarr : biases) {
			for (int i = 0; i < biasarr.length; i++) {
				double randbias = (Math.random() - 0.5) * (uplb - lowlb) + ((uplb + lowlb) / 2);
				biasarr[i] = randbias;
			}
		}
	}

	public void calculateFully() {
		calculateLayer(1, true);
	}

	protected void calculateLayer(int layer, boolean recurseFully) {
		if (layer == 0)
			throw new RuntimeException("Cannot calculate input layer");
		if (layer >= networkDimensions.length)
			return;

		double[] nodearr = nodes[layer - 1];
		double[] biasarr = biases[layer - 1];

		double[][] result = TensorMath.matrixMult(weights[layer - 1], DoubleArrays.toDoubleArray(nodearr));
		result = TensorMath.tensorAdd(result, DoubleArrays.toDoubleArray(biasarr));
		nodes_Z[layer] = DoubleArrays.toSingleArray(result);
		nodes[layer] = DoubleArrays.performFunction(nodes_Z[layer], sigmoidFunction);

		if (recurseFully) {
			calculateLayer(layer + 1, recurseFully);
		}
	}

	// TRAINING
	public void setTrainer(NeuralNetworkTrainer<?, ?> trainer) {
		this.trainer = trainer;
	}

	public NeuralNetworkTrainer<?, ?> getTrainer() {
		return trainer;
	}

	/**
	 * Train Intensive uses gradient descent with the entire training examples
	 * sample size through a given number of iterations, ie proper theoretical
	 * machine learning
	 * 
	 * @param descentIterations
	 * @param numExamples
	 */
	public void trainIntensive(int descentIterations, int numExamples) {
		initPartials(true);

		double[][][] examples = trainer.getRandomTrainingExamples(numExamples);
		for (int i = 0; i < descentIterations; i++) {

			double[] beforePerformance = null;
			if (i % trainer.getN() == 0) {
				beforePerformance = calculateAveragePerformance(examples);
			}

			gradientDescent(examples);

			if (i % trainer.getN() == 0) {
				double[] afterPerformance = calculateAveragePerformance(examples);
				trainer.runEveryNIterations(beforePerformance, afterPerformance);
			}

		}

		initPartials(false);
	}

	/**
	 * trainWithBatches is a more optimized machine learning technique that uses a
	 * batch of random examples to descend the cost function over and over again,
	 * each iteration is a new batch.
	 * 
	 * @param descentIterations
	 * @param batchSize
	 */
	public void trainWithBatches(int descentIterations, int batchSize) {
		initPartials(true);

		for (int i = 0; i < descentIterations; i++) {
			double[][][] examples = trainer.getRandomTrainingExamples(batchSize);

			double[] beforePerformance = null;
			if (i % trainer.getN() == 0) {
				beforePerformance = calculateAveragePerformance(examples);
			}

			gradientDescent(examples);

			if (i % trainer.getN() == 0) {
				double[] afterPerformance = calculateAveragePerformance(examples);

				trainer.runEveryNIterations(beforePerformance, afterPerformance);
			}
		}

		initPartials(false);
	}

	protected void gradientDescent(double[][][] examples) {
		double[][][] dW = TensorMath.tensorScale(weightPartials, 0);
		double[][] dB = TensorMath.tensorScale(biasPartials, 0);
		for (double[][] example : examples) {
			feed(example[0]);
			calculateFully();
			calculatePartials(example[1], true);

			dW = TensorMath.tensorAdd(dW, weightPartials);
			dB = TensorMath.tensorAdd(dB, biasPartials);
		}
		dW = TensorMath.tensorScale(dW, -trainer.getWeightLearningRate() / 100);
		dB = TensorMath.tensorScale(dB, -trainer.getBiasLearningRate() / 100);
		takeWeightGradientDescentStep(dW);
		takeBiasGradientDescentStep(dB);
	}

	protected void calculatePartials(double[] expectedOutput, boolean recurseFully) {
		for (int j = 0; j < networkDimensions[networkDimensions.length - 1]; j++) {
			nodePartials[networkDimensions.length - 1][j] = trainer
					.getDerivativeOfCost(nodes[networkDimensions.length - 1][j], expectedOutput[j]);
		}
		if (recurseFully) {
			calculatePartials(networkDimensions.length - 1, recurseFully);
		}
	}

	protected void calculatePartials(int layer, boolean recurseFully) {
		if (layer < 1)
			return;
		for (int i = 0; i < networkDimensions[layer]; i++) {
			node_Z_Partials[layer][i] = nodePartials[layer][i] * sigmoidFunctionDerivative.evaluate(nodes_Z[layer][i]);
			for (int k = 0; k < networkDimensions[layer - 1]; k++) {
				weightPartials[layer - 1][i][k] = node_Z_Partials[layer][i] * nodes[layer - 1][k];
			}
			biasPartials[layer - 1][i] = node_Z_Partials[layer][i];
		}
		for (int k = 0; k < networkDimensions[layer - 1]; k++) {
			double sum = 0;
			for (int i = 0; i < networkDimensions[layer]; i++) {
				sum += node_Z_Partials[layer][i] * weights[layer - 1][i][k];
			}
			nodePartials[layer - 1][k] = sum;
		}

		if (recurseFully) {
			calculatePartials(layer - 1, recurseFully);
		}
	}

	protected void takeWeightGradientDescentStep(double[][][] dW) {
		weights = TensorMath.tensorAdd(weights, dW);
	}

	protected void takeBiasGradientDescentStep(double[][] dB) {
		biases = TensorMath.tensorAdd(biases, dB);
	}

	/**
	 * trainingExamples[trainingExampleNum][0] is the input layer
	 * trainingExamples[trainingExampleNum][1] is the expected output layer
	 * 
	 * @param trainingExamples
	 * @return the average cost of the network and the average accuracy in
	 *         classification
	 */

	public double[] calculateAveragePerformance(double[][][] trainingExamples) {
		double totalCost = 0, totalAccuracy = 0;
		for (double[][] example : trainingExamples) {
			Object[] performance = calculatePerformance(example);
			totalCost += ((double) performance[0]);
			totalAccuracy += (((boolean) performance[1]) ? 1 : 0);
		}
		double cost = totalCost / trainingExamples.length;
		double accuracy = totalAccuracy / trainingExamples.length;
		return new double[] { cost, accuracy };
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
	public Object[] calculatePerformance(double[][] trainingExample) {
		feed(trainingExample[0]);
		calculateFully();
		double[] output = getOutput();
		double[] expectedOutput = trainingExample[1];

		double cost = trainer.getCost(output, expectedOutput);
		boolean correct = trainer.isCorrect(output, expectedOutput);
		return new Object[] { cost, correct };
	}

	@Override
	public String toString() {
		String str = "";
		for (double[] element : nodes) {
			for (double element2 : element) {
				str += element2 + " ";
			}
			str += "\n";
		}
		return str;
	}

	public static final DoubleFunction1D sigmoidFunction = new DoubleFunction1D() {
		@Override
		public double evaluate(double inp) {
			return 1 / (1 + Math.exp(-inp));
		}
	};
	public static final DoubleFunction1D sigmoidFunctionDerivative = new DoubleFunction1D() {
		@Override
		public double evaluate(double inp) {
			return sigmoidFunction.evaluate(inp) * (1 - sigmoidFunction.evaluate(inp));
		}
	};
}
