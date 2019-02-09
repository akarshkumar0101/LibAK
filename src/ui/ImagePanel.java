package ui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class ImagePanel extends JComponent {

	private static final long serialVersionUID = -3290256496027015906L;

	public static final int STRETCH_IMAGE = 0;
	public static final int SCALE_IMAGE = 0;
	public static final int SCALE_IMAGE_ZOOM = 0;

	private Image img;
	private int drawingType;

	public ImagePanel(Image img, int drawingType) {
		super();
		this.setImage(img);
		this.setDrawingType(drawingType);
	}

	public void setImage(Image img) {
		this.img = img;
	}

	public void setDrawingType(int drawingType) {
		this.drawingType = drawingType;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (this.drawingType == ImagePanel.STRETCH_IMAGE) {
			this.paintStretch(g);
		} else if (this.drawingType == ImagePanel.SCALE_IMAGE) {
			this.paintScale(g);
		} else if (this.drawingType == ImagePanel.SCALE_IMAGE_ZOOM) {
			this.paintZoom(g);
		}
	}

	private void paintStretch(Graphics g) {
		g.drawImage(this.img, 0, 0, this.getWidth(), this.getHeight(), null);
	}

	private void paintScale(Graphics g) {
		double imgRatio = this.img.getWidth(null) / this.img.getHeight(null);
		double panelRatio = this.getWidth() / this.getHeight();
		if (imgRatio > panelRatio) {
			this.paintMatchWidth(g, imgRatio);
		} else {
			this.paintMatchHeight(g, imgRatio);
		}
	}

	private void paintZoom(Graphics g) {
		double imgRatio = this.img.getWidth(null) / this.img.getHeight(null);
		double panelRatio = this.getWidth() / this.getHeight();
		if (imgRatio > panelRatio) {
			this.paintMatchHeight(g, imgRatio);
		} else {
			this.paintMatchWidth(g, imgRatio);
		}
	}

	private void paintMatchWidth(Graphics g, double imgRatio) {
		int width = this.getWidth();
		int height = (int) (width / imgRatio);

		int ystart = (this.getHeight() - this.img.getHeight(null)) / 2;
		g.drawImage(this.img, 0, ystart, width, height, null);
	}

	private void paintMatchHeight(Graphics g, double imgRatio) {
		int height = this.getHeight();
		int width = (int) (imgRatio * height);

		int xstart = (this.getWidth() - this.img.getWidth(null)) / 2;
		g.drawImage(this.img, xstart, 0, width, height, null);
	}

}
