package machinelearning.ne.neat;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import machinelearning.ne.neat.genome.Genome;

public class VisualSpeciesPanel extends JComponent{
	
	private static final long serialVersionUID = -7911729139086160825L;

	private final GridLayout gridLayout;
	
	public VisualSpeciesPanel(NEAT neat) {
		gridLayout = new GridLayout(neat.species.size(), 1);
		this.setLayout(gridLayout);
		
		for(Species spec:neat.species) {
			GridLayout layout = new GridLayout(1, spec.size());
			JPanel panel = new JPanel(layout);
			for(Genome geno:spec) {
				VisualNEATNetworkPanel p = new VisualNEATNetworkPanel(geno);
				p.setBorder(BorderFactory.createLineBorder(Color.BLUE));
				panel.add(p);
			}
			this.add(panel);
		}
	}
	
	
	
}
