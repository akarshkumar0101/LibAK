package ui;

import java.awt.Color;

public class UIPointSet {
    private String title;

    private double[][] points;

    private Color pointColor;
    private Color lineColor;

    private boolean hover;

    public UIPointSet(double[][] points) {
	this.points = points;
    }

    public void setPoints(double[][] points) {
	this.points = points;
    }

    public double[][] getPoints() {
	return points;
    }

    public void setPointColor(Color col) {
	pointColor = col;
    }

    public void setLineColor(Color col) {
	lineColor = col;
    }

    public Color getPointColor() {
	return pointColor;
    }

    public Color getLineColor() {
	return lineColor;
    }

    public String getTitle() {
	return title;
    }

    public boolean getHover() {
	return hover;
    }

}
