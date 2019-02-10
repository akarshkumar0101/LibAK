package ui;

import java.awt.GridLayout;

public class GridFrame extends EasyJFrame {

	private static final long serialVersionUID = -1066463237550014942L;

	private final GridLayout gridLayout;

	public GridFrame(String title, int rows, int cols, int width, int height, boolean closeOnX,
			boolean showImmediately) {
		super(title, width, height, closeOnX, showImmediately);
		this.gridLayout = new GridLayout(rows, cols);
		this.getContentPane().setLayout(gridLayout);
	}

	public GridLayout getGridLayout() {
		return this.gridLayout;
	}
}
