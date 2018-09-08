package ui;

import javax.swing.JFrame;

public class EasyJFrame extends JFrame {

	private static final long serialVersionUID = 8419145115411447836L;

	public EasyJFrame(String title, int width, int height, boolean closeOnX, boolean showImmediately) {
		super(title);
		this.setSize(width, height);
		if (closeOnX) {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		if (showImmediately) {
			setVisible(false);
		}
	}
}
