package machinelearning.ne.neat;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JComponent;

import data.tuple.Tuple2D;
import math.AKMath;

public class VisualNEATNetworkPanel extends JComponent {

	private static final long serialVersionUID = 5395346094940056438L;

	private NEATNeuralNetwork network;

	public VisualNEATNetworkPanel(NEATNeuralNetwork network) {
		this.network = network;

	}

	private Tuple2D<Integer, Integer> locationOf(NEATNeuron neuron) {
		int layer = 0, nodeID = 0;
		int temp = 0;
		int numNeuronsInLayer = 0;
		if ((temp = this.network.inputNeurons.indexOf(neuron)) != -1) {
			layer = 0;
			nodeID = temp;
			numNeuronsInLayer = this.network.inputNeurons.size();
		} else if ((temp = this.network.hiddenNeurons.indexOf(neuron)) != -1) {
			layer = 1;
			nodeID = temp;
			numNeuronsInLayer = this.network.hiddenNeurons.size();
		} else if ((temp = this.network.outputNeurons.indexOf(neuron)) != -1) {
			layer = 2;
			nodeID = temp;
			numNeuronsInLayer = this.network.outputNeurons.size();
		}

		int x = (int) AKMath.scale(layer, -0.5, 2.5, 0, this.getWidth());
		int y = (int) AKMath.scale(nodeID, -1, numNeuronsInLayer, 0, this.getHeight());

		return new Tuple2D<>(x, y);
	}

	@Override
	public void paintComponent(Graphics g) {

		for (int layer = 0; layer < 3; layer++) {
			List<NEATNeuron> neurons = layer == 0 ? this.network.inputNeurons
					: layer == 1 ? this.network.hiddenNeurons : this.network.outputNeurons;

			for (int nodeID = 0; nodeID < neurons.size(); nodeID++) {
				int x = (int) AKMath.scale(layer, -0.5, 2.5, 0, this.getWidth());
				int y = (int) AKMath.scale(nodeID, -1, neurons.size(), 0, this.getHeight());

				int circledia = Math.max(10,
						Math.min(this.getWidth() / 4 / (3 + 1), this.getHeight() / 4 / neurons.size()));

				double activation = neurons.get(nodeID).activation;
				int grayscaleAct = (int) AKMath.scale(activation, 0, 1, 0, 255);
				g.setColor(new Color(grayscaleAct, grayscaleAct, grayscaleAct));
				g.fillOval(x - circledia / 2, y - circledia / 2, circledia, circledia);

				int realNodeID = nodeID;
				if (layer >= 1) {
					realNodeID += this.network.inputNeurons.size();
				}
				if (layer == 1) {
					realNodeID += this.network.outputNeurons.size();
				}
				if (!this.network.hasBias) {
					realNodeID += 1;
				}

				// first drawString call takes a long time, this is the delay
				int fontSize = 20;
				g.setColor(Color.GREEN);
				g.drawString("" + realNodeID, x - fontSize / 2, y);

			}

		}
		for (int i = 0; i < this.network.inputNeurons.size() + this.network.hiddenNeurons.size()
				+ this.network.outputNeurons.size(); i++) {
			NEATNeuron neuron = i < this.network.inputNeurons.size() ? this.network.inputNeurons.get(i)
					: i < this.network.inputNeurons.size() + this.network.hiddenNeurons.size()
							? this.network.hiddenNeurons.get(i - this.network.inputNeurons.size())
							: this.network.outputNeurons
									.get(i - this.network.inputNeurons.size() - this.network.hiddenNeurons.size());
			for (NEATNeuron prevNeuron : neuron.prevConnections.keySet()) {
				Tuple2D<Integer, Integer> locNeuron = this.locationOf(neuron);
				Tuple2D<Integer, Integer> locPrevNeuron = this.locationOf(prevNeuron);

				g.setColor(Color.ORANGE);
				g.drawLine(locNeuron.getA(), locNeuron.getB(), locPrevNeuron.getA(), locPrevNeuron.getB());
			}

		}

	}

}

class VisualNEATNetworkPanelFFFF extends JComponent {

	private static final long serialVersionUID = 3090346802463242357L;

	private NEATNeuralNetwork network;
	private double[] expectedOutput;

	private int[] networkDimensions;

	public VisualNEATNetworkPanelFFFF(NEATNeuralNetwork network) {
		super();

		this.networkDimensions = new int[3];

		this.setNetwork(network);

	}

	public void setNetwork(NEATNeuralNetwork network) {
//		if (this.network != null) {
//			this.network.removeNeuralNetworkListener(this);
//		}
//		this.network = network;
//		this.network.addNeuralNetworkListener(this);
		this.network = network;

		this.networkDimensions[0] = network.inputNeurons.size();
		this.networkDimensions[1] = network.hiddenNeurons.size();
		this.networkDimensions[2] = network.outputNeurons.size();
	}

	public NEATNeuralNetwork getNetwork() {
		return this.network;
	}

	public List<NEATNeuron> getLayer(int layer) {
		if (layer == 0)
			return this.network.inputNeurons;
		if (layer == 2)
			return this.network.outputNeurons;
		else
			return this.network.hiddenNeurons;
	}

	public Tuple2D<Integer, Integer> layerAndIDFor(NEATNeuron neuron) {
		if (this.network.inputNeurons.contains(neuron))
			return new Tuple2D<>(0, this.network.inputNeurons.indexOf(neuron));
		if (this.network.outputNeurons.contains(neuron))
			return new Tuple2D<>(2, this.network.outputNeurons.indexOf(neuron));
		else
			return new Tuple2D<>(1, this.network.hiddenNeurons.indexOf(neuron));
	}

	public int circleDiameter(int layer) {
		int circledia = (int) Math.max(10, Math.min(this.getWidth() / 1.5 / (this.networkDimensions.length + 1),
				this.getHeight() / 1.5 / this.networkDimensions[layer]));
		return circledia;
	}

	public void paintAllNeurons(Graphics g) {
		for (int layer = 0; layer < this.networkDimensions.length; layer++) {
			int circledia = this.circleDiameter(layer);
			for (int nodeID = 0; nodeID < this.networkDimensions[layer]; nodeID++) {
				NEATNeuron neuron = this.getLayer(layer).get(nodeID);
				double value = neuron.activation;
				g.setColor(new Color((int) (value * 255), (int) (value * 255), (int) (value * 255)));
				Tuple2D<Integer, Integer> loc = this.locationOfNode(neuron);
				g.fillOval(loc.getA(), loc.getB(), circledia, circledia);
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.DARK_GRAY);

		this.paintAllNeurons(g);

		for (int layer = 1; layer < this.networkDimensions.length; layer++) {
			for (int node2ID = 0; node2ID < this.networkDimensions[layer]; node2ID++) {
				for (int node1ID = 0; node1ID < this.networkDimensions[layer - 1]; node1ID++) {
					if (this.networkDimensions[layer] * this.networkDimensions[layer - 1] > 10000
							&& Math.random() > 1) {
						continue;
					}
					/*
					 * double weight = this.network.weights[layer - 1][node2ID][node1ID]; int[] loc1
					 * = this.centerLocationOfNode(layer - 1, node1ID); int[] loc2 =
					 * this.centerLocationOfNode(layer, node2ID);
					 *
					 * Graphics2D g2 = (Graphics2D) g; g2.setStroke(new BasicStroke((float)
					 * Math.abs(weight))); g2.setColor(weight >= 0 ? Color.GREEN : Color.RED);
					 *
					 * if (Math.abs(weight) > .01) { g2.drawLine(loc1[0], loc1[1], loc2[0],
					 * loc2[1]); }
					 */
				}

			}
		}
		/*
		 * if (this.expectedOutput != null) { int circledia = (int) Math.max(10,
		 * Math.min(this.getWidth() / 1.5 / (this.network.networkDimensions.length + 1),
		 * this.getHeight() / 1.5 /
		 * this.network.networkDimensions[this.network.networkDimensions.length - 1]));
		 * for (int nodeID = 0; nodeID < this.expectedOutput.length; nodeID++) { double
		 * value = this.expectedOutput[nodeID]; g.setColor(new Color((int) (value *
		 * 255), (int) (value * 255), (int) (value * 255))); int[] loc = new int[] {
		 * (int) (this.getWidth() - circledia * 1.2), (int) this.scale(nodeID, 0,
		 * this.expectedOutput.length, 0, this.getHeight()) }; g.fillOval(loc[0],
		 * loc[1], circledia, circledia); } }
		 */
	}

	private Tuple2D<Integer, Integer> centerLocationOfNode(NEATNeuron neuron) {
		int circledia = this.circleDiameter(this.layerAndIDFor(neuron).getA());
		Tuple2D<Integer, Integer> loc = this.locationOfNode(neuron);
		return new Tuple2D<>(loc.getA() + circledia / 2, loc.getB() + circledia / 2);
	}

	private Tuple2D<Integer, Integer> locationOfNode(NEATNeuron neuron) {
		Tuple2D<Integer, Integer> layerID = this.layerAndIDFor(neuron);
		int x = (int) AKMath.scale(layerID.getA(), 0, this.networkDimensions.length + 1, 0, this.getWidth());
		int y = (int) AKMath.scale(layerID.getB(), 0, this.networkDimensions[layerID.getA()], 0, this.getHeight());
		return new Tuple2D<>(x, y);
	}

}
