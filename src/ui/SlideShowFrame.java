package ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JComponent;

public class SlideShowFrame extends EasyJFrame implements KeyListener {

	private static final long serialVersionUID = -1066463237550014942L;

	private List<JComponent> comps;

	private int currentCompID;

	public SlideShowFrame(String title, int width, int height, boolean closeOnX, boolean showImmediately,
			List<JComponent> comps) {
		super(title, width, height, closeOnX, showImmediately);

		this.comps = comps;

		this.currentCompID = 0;
		this.getContentPane().add(this.comps.get(this.currentCompID));
		this.addKeyListener(this);
	}

	public void nextComponent() {
		// this.currentCompID++;
		int prevID = this.currentCompID;
		this.currentCompID = Math.abs(this.currentCompID + 1) % this.comps.size();

		this.getContentPane().remove(this.comps.get(prevID));
		this.getContentPane().add(this.comps.get(this.currentCompID));
		this.getContentPane().revalidate();
		this.repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.nextComponent();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}