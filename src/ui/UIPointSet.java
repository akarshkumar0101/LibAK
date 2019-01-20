package ui;

import java.awt.Color;
import java.util.ArrayList;

import util.vector.Vector2D;
import util.vector.VectorMath;

public class UIPointSet extends ArrayList<Vector2D<Double>> {

	private static final long serialVersionUID = 6804111132716061746L;

	private String title;

	private Color pointColor;
	private Color lineColor;

	private boolean hover;

	public UIPointSet() {
		super();
		this.setPointColor(Color.BLACK);
	}

	public UIPointSet(double[][] pointsarr) {
		this();
		for (double[] point : pointsarr) {
			this.add(new Vector2D<>(point[0], point[1], VectorMath.doubleVectorMath));
		}
	}

	public void setPointColor(Color col) {
		this.pointColor = col;
	}

	public void setLineColor(Color col) {
		this.lineColor = col;
	}

	public Color getPointColor() {
		return this.pointColor;
	}

	public Color getLineColor() {
		return this.lineColor;
	}

	public String getTitle() {
		return this.title;
	}

	public boolean getHover() {
		return this.hover;
	}

}
