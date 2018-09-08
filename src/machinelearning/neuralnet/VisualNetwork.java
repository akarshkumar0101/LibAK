package machinelearning.neuralnet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class VisualNetwork extends JFrame implements NeuralNetworkListener {

	private static final long serialVersionUID = 3090146802464835357L;

	public TrackableNeuralNetwork network;
	private double[] expectedOutput;

	public final Drawer drawer;

	public VisualNetwork(TrackableNeuralNetwork network) {
		super("Visualizing neural network");
		setNetwork(network);

		drawer = new Drawer();
		drawer.setBorder(BorderFactory.createLineBorder(Color.red));
		getContentPane().add(drawer);

		this.setSize(900, 900);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void setNetwork(TrackableNeuralNetwork network) {
		if (this.network != null) {
			this.network.removeNeuralNetworkListener(this);
		}
		this.network = network;
		this.network.addNeuralNetworkListener(this);
	}

	@Override
	public void changedNodesInLayer(int layer) {
		drawer.repaint();
	}

	@Override
	public void changedWeightsAndBiases() {
		drawer.repaint();
	}

	@Override
	public void changedPartials(int layer) {
		drawer.repaint();
	}

	@Override
	public void changedExpectedOutput(double[] expectedOutput) {
		this.expectedOutput = expectedOutput;
		drawer.repaint();
	}

	class Drawer extends JComponent {

		private static final long serialVersionUID = 1811576747648421764L;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.DARK_GRAY);
			for (int layer = 0; layer < network.networkDimensions.length; layer++) {
				int circledia = (int) Math.max(10, Math.min((getWidth() / 1.5) / (network.networkDimensions.length + 1),
						(getHeight() / 1.5) / network.networkDimensions[layer]));
				for (int nodeID = 0; nodeID < network.networkDimensions[layer]; nodeID++) {
					double value = network.nodes[layer][nodeID];
					g.setColor(new Color((int) (value * 255), (int) (value * 255), (int) (value * 255)));
					int[] loc = locationOfNode(layer, nodeID);
					g.fillOval(loc[0], loc[1], circledia, circledia);
				}
			}
			for (int layer = 1; layer < network.networkDimensions.length; layer++) {
				for (int node2ID = 0; node2ID < network.networkDimensions[layer]; node2ID++) {
					for (int node1ID = 0; node1ID < network.networkDimensions[layer - 1]; node1ID++) {
						if ((network.networkDimensions[layer] * network.networkDimensions[layer - 1]) > 10000
								&& Math.random() > 1) {
							continue;
						}

						double weight = network.weights[layer - 1][node2ID][node1ID];
						int[] loc1 = centerLocationOfNode(layer - 1, node1ID);
						int[] loc2 = centerLocationOfNode(layer, node2ID);

						Graphics2D g2 = (Graphics2D) g;
						g2.setStroke(new BasicStroke((float) Math.abs(weight)));
						g2.setColor(weight >= 0 ? Color.GREEN : Color.RED);

						if (Math.abs(weight) > .01) {
							g2.drawLine(loc1[0], loc1[1], loc2[0], loc2[1]);
						}
					}

				}
			}
			if (expectedOutput != null) {
				int circledia = (int) Math.max(10, Math.min((getWidth() / 1.5) / (network.networkDimensions.length + 1),
						(getHeight() / 1.5) / network.networkDimensions[network.networkDimensions.length - 1]));
				for (int nodeID = 0; nodeID < expectedOutput.length; nodeID++) {
					double value = expectedOutput[nodeID];
					g.setColor(new Color((int) (value * 255), (int) (value * 255), (int) (value * 255)));
					int[] loc = new int[] { (int) (getWidth() - circledia * 1.2),
							(int) scale(nodeID, 0, expectedOutput.length, 0, getHeight()) };
					g.fillOval(loc[0], loc[1], circledia, circledia);
				}
			}
		}

		private int[] centerLocationOfNode(int layer, int nodeID) {
			int circledia = (int) Math.max(10, Math.min((getWidth() / 1.5) / (network.networkDimensions.length + 1),
					(getHeight() / 1.5) / network.networkDimensions[layer]));
			int[] loc = locationOfNode(layer, nodeID);
			loc[0] += circledia / 2;
			loc[1] += circledia / 2;
			return loc;
		}

		private int[] locationOfNode(int layer, int nodeID) {
			int x = (int) scale(layer, 0, network.networkDimensions.length + 1, 0, getWidth());
			int y = (int) scale(nodeID, 0, network.networkDimensions[layer], 0, getHeight());
			return new int[] { x, y };
		}

		private double scale(double num, double s1low, double s1up, double s2low, double s2up) {
			num -= s1low;
			num *= (s2up - s2low) / (s1up - s1low);
			num += s2low;
			return num;
		}

	}
}
