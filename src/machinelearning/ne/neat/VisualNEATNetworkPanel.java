package machinelearning.ne.neat;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JComponent;

import data.tuple.Tuple2D;
import machinelearning.ne.neat.network.NeuralNetwork;
import machinelearning.ne.neat.network.Neuron;
import math.AKMath;

public class VisualNEATNetworkPanel extends JComponent {

	private static final long serialVersionUID = 5395346094940056438L;

	private NeuralNetwork network;

	private final Map<Neuron, Tuple2D<Double, Double>> nodeLocations;

	public VisualNEATNetworkPanel(NeuralNetwork network) {
		this.nodeLocations = new HashMap<>();

		this.setNetwork(network);

	}

	private Tuple2D<Integer, Integer> locationOf(Neuron neuron) {
		Tuple2D<Double, Double> loc = this.nodeLocations.get(neuron);
		System.out.println(loc);
		return new Tuple2D<>((int) (loc.getA() * this.getWidth()), (int) (loc.getB() * this.getHeight()));
	}

	private Tuple2D<Integer, Integer> locationOff(Neuron neuron) {
		int layer = 0, nodeID = 0;
		int temp = 0;
		int numNeuronsInLayer = 0;
		if ((temp = this.network.getInputNeurons().indexOf(neuron)) != -1) {
			layer = 0;
			nodeID = temp;
			numNeuronsInLayer = this.network.getInputNeurons().size();
		} else if ((temp = this.network.getHiddenNeurons().indexOf(neuron)) != -1) {
			layer = 1;
			nodeID = temp;
			numNeuronsInLayer = this.network.getHiddenNeurons().size();
		} else if ((temp = this.network.getOutputNeurons().indexOf(neuron)) != -1) {
			layer = 2;
			nodeID = temp;
			numNeuronsInLayer = this.network.getOutputNeurons().size();
		}

		int x = (int) AKMath.scale(layer, -0.5, 2.5, 0, this.getWidth());
		int y = (int) AKMath.scale(nodeID, -1, numNeuronsInLayer, 0, this.getHeight());

		return new Tuple2D<>(x, y);
	}

	@Override
	public void paintComponent(Graphics g) {
		if (this.network == null)
			return;

		for (int layer = 0; layer < 3; layer++) {
			List<Neuron> neurons = layer == 0 ? this.network.getInputNeurons()
					: layer == 1 ? this.network.getHiddenNeurons() : this.network.getOutputNeurons();

			for (int nodeID = 0; nodeID < neurons.size(); nodeID++) {
				int x = (int) AKMath.scale(layer, -0.5, 2.5, 0, this.getWidth());
				int y = (int) AKMath.scale(nodeID, -1, neurons.size(), 0, this.getHeight());

				x = this.locationOf(neurons.get(nodeID)).getA();
				y = this.locationOf(neurons.get(nodeID)).getB();

				int circledia = Math.max(10,
						Math.min(this.getWidth() / 4 / (3 + 1), this.getHeight() / 4 / neurons.size()));

				double activation = neurons.get(nodeID).getActivation();
				int grayscaleAct = (int) AKMath.scale(activation, 0, 1, 0, 255);
				g.setColor(new Color(grayscaleAct, grayscaleAct, grayscaleAct));
				g.fillOval(x - circledia / 2, y - circledia / 2, circledia, circledia);

				int realNodeID = nodeID;
				if (layer >= 1) {
					realNodeID += this.network.getInputNeurons().size();
				}
				if (layer == 1) {
					realNodeID += this.network.getOutputNeurons().size();
				}
				if (!this.network.hasBias()) {
					realNodeID += 1;
				}

				// first drawString call takes a long time, this is the delay
				int fontSize = 20;
				g.setColor(Color.GREEN);
				g.drawString("" + realNodeID, x - fontSize / 2, y);

			}

		}
		for (int i = 0; i < this.network.getInputNeurons().size() + this.network.getHiddenNeurons().size()
				+ this.network.getOutputNeurons().size(); i++) {
			Neuron neuron = i < this.network.getInputNeurons().size() ? this.network.getInputNeurons().get(i)
					: i < this.network.getInputNeurons().size() + this.network.getHiddenNeurons().size()
							? this.network.getHiddenNeurons().get(i - this.network.getInputNeurons().size())
							: this.network.getOutputNeurons().get(
									i - this.network.getInputNeurons().size() - this.network.getHiddenNeurons().size());
			for (Neuron prevNeuron : neuron.getPrevConnections().keySet()) {
				Tuple2D<Integer, Integer> locNeuron = this.locationOf(neuron);
				Tuple2D<Integer, Integer> locPrevNeuron = this.locationOf(prevNeuron);

				int midx = (locNeuron.getA() + locPrevNeuron.getA()) / 2,
						midy = (locNeuron.getB() + locPrevNeuron.getB()) / 2;

				g.setColor(Color.ORANGE);
				g.drawLine(locPrevNeuron.getA(), locPrevNeuron.getB(), midx, midy);

				g.setColor(Color.GREEN);
				g.drawLine(midx, midy, locNeuron.getA(), locNeuron.getB());
			}

		}

	}

	public NeuralNetwork getNetwork() {
		return this.network;
	}

	public void setNetwork(NeuralNetwork network) {
		this.network = network;

		this.nodeLocations.clear();
		
		if(network==null) {
			return;
		}
		List<Neuron> inputNeurons = network.getInputNeurons();
		List<Neuron> hiddenNeurons = network.getHiddenNeurons();
		List<Neuron> outputNeurons = network.getOutputNeurons();

		Random random = new Random(network.networkID*1000+500);

		for (int i = 0; i < inputNeurons.size(); i++) {
			Neuron neuron = inputNeurons.get(i);
			double x = 0.1;
			double y = AKMath.scale(i, -0.5, inputNeurons.size(), 0, 1);
			Tuple2D<Double, Double> location = new Tuple2D<>(x, y);
			this.nodeLocations.put(neuron, location);
		}
		for (int i = 0; i < outputNeurons.size(); i++) {
			Neuron neuron = outputNeurons.get(i);
			double x = 0.9;
			double y = AKMath.scale(i, -0.5, outputNeurons.size(), 0, 1);
			Tuple2D<Double, Double> location = new Tuple2D<>(x, y);
			this.nodeLocations.put(neuron, location);
		}

		for (int i = 0; i < hiddenNeurons.size(); i++) {
			Neuron neuron = hiddenNeurons.get(i);
			double x = AKMath.scale(i, -1.5, hiddenNeurons.size(), 0, 1);
			double y = random.nextDouble();
			Tuple2D<Double, Double> location = new Tuple2D<>(x, y);
			this.nodeLocations.put(neuron, location);
		}
	}

}

//class VisualNEATNetworkPanelFFFF extends JComponent {
//
//	private static final long serialVersionUID = 3090346802463242357L;
//
//	private NeuralNetwork network;
//	private double[] expectedOutput;
//
//	private int[] networkDimensions;
//
//	public VisualNEATNetworkPanelFFFF(NeuralNetwork network) {
//		super();
//
//		this.networkDimensions = new int[3];
//
//		this.setNetwork(network);
//
//	}
//
//	public void setNetwork(NeuralNetwork network) {
////		if (this.network != null) {
////			this.network.removeNeuralNetworkListener(this);
////		}
////		this.network = network;
////		this.network.addNeuralNetworkListener(this);
//		this.network = network;
//
//		this.networkDimensions[0] = network.getInputNeurons().size();
//		this.networkDimensions[1] = network.getHiddenNeurons().size();
//		this.networkDimensions[2] = network.getOutputNeurons().size();
//	}
//
//	public NeuralNetwork getNetwork() {
//		return this.network;
//	}
//
//	public List<Neuron> getLayer(int layer) {
//		if (layer == 0)
//			return this.network.getInputNeurons();
//		if (layer == 2)
//			return this.network.getOutputNeurons();
//		else
//			return this.network.getHiddenNeurons();
//	}
//
//	public Tuple2D<Integer, Integer> layerAndIDFor(Neuron neuron) {
//		if (this.network.getInputNeurons().contains(neuron))
//			return new Tuple2D<>(0, this.network.getInputNeurons().indexOf(neuron));
//		if (this.network.getOutputNeurons().contains(neuron))
//			return new Tuple2D<>(2, this.network.getOutputNeurons().indexOf(neuron));
//		else
//			return new Tuple2D<>(1, this.network.getHiddenNeurons().indexOf(neuron));
//	}
//
//	public int circleDiameter(int layer) {
//		int circledia = (int) Math.max(10, Math.min(this.getWidth() / 1.5 / (this.networkDimensions.length + 1),
//				this.getHeight() / 1.5 / this.networkDimensions[layer]));
//		return circledia;
//	}
//
//	public void paintAllNeurons(Graphics g) {
//		for (int layer = 0; layer < this.networkDimensions.length; layer++) {
//			int circledia = this.circleDiameter(layer);
//			for (int nodeID = 0; nodeID < this.networkDimensions[layer]; nodeID++) {
//				Neuron neuron = this.getLayer(layer).get(nodeID);
//				double value = neuron.activation;
//				g.setColor(new Color((int) (value * 255), (int) (value * 255), (int) (value * 255)));
//				Tuple2D<Integer, Integer> loc = this.locationOfNode(neuron);
//				g.fillOval(loc.getA(), loc.getB(), circledia, circledia);
//			}
//		}
//	}
//
//	@Override
//	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		g.setColor(Color.LIGHT_GRAY);
//		g.fillRect(0, 0, this.getWidth(), this.getHeight());
//		g.setColor(Color.DARK_GRAY);
//
//		this.paintAllNeurons(g);
//
//		for (int layer = 1; layer < this.networkDimensions.length; layer++) {
//			for (int node2ID = 0; node2ID < this.networkDimensions[layer]; node2ID++) {
//				for (int node1ID = 0; node1ID < this.networkDimensions[layer - 1]; node1ID++) {
//					if (this.networkDimensions[layer] * this.networkDimensions[layer - 1] > 10000
//							&& Math.random() > 1) {
//						continue;
//					}
//					/*
//					 * double weight = this.network.weights[layer - 1][node2ID][node1ID]; int[] loc1
//					 * = this.centerLocationOfNode(layer - 1, node1ID); int[] loc2 =
//					 * this.centerLocationOfNode(layer, node2ID);
//					 *
//					 * Graphics2D g2 = (Graphics2D) g; g2.setStroke(new BasicStroke((float)
//					 * Math.abs(weight))); g2.setColor(weight >= 0 ? Color.GREEN : Color.RED);
//					 *
//					 * if (Math.abs(weight) > .01) { g2.drawLine(loc1[0], loc1[1], loc2[0],
//					 * loc2[1]); }
//					 */
//				}
//
//			}
//		}
//		/*
//		 * if (this.expectedOutput != null) { int circledia = (int) Math.max(10,
//		 * Math.min(this.getWidth() / 1.5 / (this.network.networkDimensions.length + 1),
//		 * this.getHeight() / 1.5 /
//		 * this.network.networkDimensions[this.network.networkDimensions.length - 1]));
//		 * for (int nodeID = 0; nodeID < this.expectedOutput.length; nodeID++) { double
//		 * value = this.expectedOutput[nodeID]; g.setColor(new Color((int) (value *
//		 * 255), (int) (value * 255), (int) (value * 255))); int[] loc = new int[] {
//		 * (int) (this.getWidth() - circledia * 1.2), (int) this.scale(nodeID, 0,
//		 * this.expectedOutput.length, 0, this.getHeight()) }; g.fillOval(loc[0],
//		 * loc[1], circledia, circledia); } }
//		 */
//	}
//
//	private Tuple2D<Integer, Integer> centerLocationOfNode(Neuron neuron) {
//		int circledia = this.circleDiameter(this.layerAndIDFor(neuron).getA());
//		Tuple2D<Integer, Integer> loc = this.locationOfNode(neuron);
//		return new Tuple2D<>(loc.getA() + circledia / 2, loc.getB() + circledia / 2);
//	}
//
//	private Tuple2D<Integer, Integer> locationOfNode(Neuron neuron) {
//		Tuple2D<Integer, Integer> layerID = this.layerAndIDFor(neuron);
//		int x = (int) AKMath.scale(layerID.getA(), 0, this.networkDimensions.length + 1, 0, this.getWidth());
//		int y = (int) AKMath.scale(layerID.getB(), 0, this.networkDimensions[layerID.getA()], 0, this.getHeight());
//		return new Tuple2D<>(x, y);
//	}
//
//}
//
