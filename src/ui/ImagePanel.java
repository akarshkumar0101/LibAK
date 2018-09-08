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
		setImage(img);
		setDrawingType(drawingType);
	}

	public void setImage(Image img) {
		this.img = img;
	}

	public void setDrawingType(int drawingType) {
		this.drawingType = drawingType;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (drawingType == STRETCH_IMAGE) {
			paintStretch(g);
		} else if (drawingType == SCALE_IMAGE) {
			paintScale(g);
		} else if (drawingType == SCALE_IMAGE_ZOOM) {
			paintZoom(g);
		}
	}

	private void paintStretch(Graphics g) {
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
	}

	private void paintScale(Graphics g) {
		double imgRatio = img.getWidth(null) / img.getHeight(null);
		double panelRatio = getWidth() / getHeight();
		if (imgRatio > panelRatio) {
			paintMatchWidth(g, imgRatio);
		} else {
			paintMatchHeight(g, imgRatio);
		}
	}

	private void paintZoom(Graphics g) {
		double imgRatio = img.getWidth(null) / img.getHeight(null);
		double panelRatio = getWidth() / getHeight();
		if (imgRatio > panelRatio) {
			paintMatchHeight(g, imgRatio);
		} else {
			paintMatchWidth(g, imgRatio);
		}
	}

	private void paintMatchWidth(Graphics g, double imgRatio) {
		int width = getWidth();
		int height = (int) (width / imgRatio);

		int ystart = (getHeight() - img.getHeight(null)) / 2;
		g.drawImage(img, 0, ystart, width, height, null);
	}

	private void paintMatchHeight(Graphics g, double imgRatio) {
		int height = getHeight();
		int width = (int) (imgRatio * height);

		int xstart = (getWidth() - img.getWidth(null)) / 2;
		g.drawImage(img, xstart, 0, width, height, null);
	}

}
