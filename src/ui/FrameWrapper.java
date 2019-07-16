package ui;

import javax.swing.JComponent;

public class FrameWrapper<C extends JComponent> extends EasyJFrame {

	private static final long serialVersionUID = -8344421539521111117L;

	protected C component;

	public FrameWrapper(String title, int width, int height, boolean closeOnX, boolean showImmediately) {
		super(title, width, height, closeOnX, showImmediately);
		this.component = null;
	}

	public void setComponent(C comp) {
		this.component = comp;
		this.getContentPane().add(comp);
	}

	public C getComponent() {
		return this.component;
	}

	public static <C extends JComponent> FrameWrapper<C> immediateShow(C ui) {
		FrameWrapper<C> frame = new FrameWrapper<>("Testing", 800, 800, true, false);
		frame.setComponent(ui);
		frame.setVisible(true);
		return frame;
	}
}
