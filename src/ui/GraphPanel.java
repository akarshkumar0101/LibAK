package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import util.vector.Vector2D;

public class GraphPanel extends JComponent
		implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 6410325060030674885L;
	public static final Font numFont = new Font("TimesRoman", Font.PLAIN, 25);
	public static final Font subFont = new Font("TimesRoman", Font.PLAIN, 17);

	public static final int MAX_NUM_FUNCTIONS = 8;
	// 25,14
	public static final int yoffset = 25, xoffset = 14;

	// public final JFrame parentFrame;

	public GraphParameters param;
	public List<UIPointSet> pointSets;

	// USED JUST FOR TRACKING MOUSE DRAGS(moving graph)
	private int mousemovex = 0, mousemovey = 0;

	private class PointBox {
		// stored with pixels not units b/c avoiding too much calculations, and
		// mouse box only paints when cursor is active on the function(not when
		// moving graph or zooming in,etc)
		private int pixx = 0, pixy = 0;
		private double valx = 0, valy = 0;
		private UIPointSet pointSet;

		private static final int CIRCLERAD = 6;
		private static final int CIRCLEDIA = 2 * PointBox.CIRCLERAD;

		public PointBox() {
		}

		public PointBox(PointBox another) {
			this.pixx = another.pixx;
			this.pixy = another.pixy;
			this.valx = another.valx;
			this.valy = another.valy;
			this.pointSet = another.pointSet;

		}

		/**
		 * pixx coordinates should be updated through the PointBox.updateBixCoordinate
		 * method prior to calling this method
		 */
		private void paint(Graphics g, boolean clicked, boolean highlight) {
			if (!(this.pixx > 0 && this.pixx < GraphPanel.this.getWidth() && this.pixy > 0
					&& this.pixy < GraphPanel.this.getHeight()))
				return;
			int raddif = clicked ? 10 : highlight ? PointBox.CIRCLERAD * 2 / 3 : 0;
			int diadif = raddif * 2;
			g.fillOval(this.pixx - PointBox.CIRCLERAD - raddif, this.pixy - PointBox.CIRCLERAD - raddif,
					PointBox.CIRCLEDIA + diadif, PointBox.CIRCLEDIA + diadif);

			// drawing the box
			String toDraw = this.valx + ", " + this.valy;
			int boxw = toDraw.length() * (GraphPanel.xoffset - 1) + 8;
			int boxh = GraphPanel.yoffset + 6;
			g.clearRect(this.pixx + 7, this.pixy - 10 - GraphPanel.yoffset, boxw, boxh);
			g.drawRect(this.pixx + 7, this.pixy - 10 - GraphPanel.yoffset, boxw, boxh);
			g.drawString(toDraw, this.pixx + 10, this.pixy - 10);
		}

		public void updatePixCoordinate() {
			this.pixx = (int) math.AKMath.scale(this.valx, GraphPanel.this.param.minx, GraphPanel.this.param.maxx, 0,
					GraphPanel.this.getWidth());
			this.pixy = (int) math.AKMath.scale(this.valy, GraphPanel.this.param.miny, GraphPanel.this.param.maxy,
					GraphPanel.this.getHeight(), 0);
		}

		@Override
		public boolean equals(Object another) {
			if (another.getClass() != PointBox.class)
				return false;
			PointBox an = (PointBox) another;
			GraphPanel.this.updateUnitRangeCheck();
			if (this.pointSet == an.pointSet && this.pixx <= an.pixx + PointBox.CIRCLEDIA
					&& this.pixx >= an.pixx - PointBox.CIRCLEDIA && this.pixy <= an.pixy + PointBox.CIRCLEDIA
					&& this.pixy >= an.pixy - PointBox.CIRCLEDIA && GraphPanel.this.isSameXUnit(this.valx, an.valx)
					&& GraphPanel.this.isSameYUnit(this.valy, an.valy))
				return true;
			else
				return false;
		}

	}

	private PointBox mBox = new PointBox();
	private boolean showmBox = false;
	private boolean showmBoxPrev = false;
	private boolean clickingmBox = false;

	private ArrayList<PointBox> savedBoxes = new ArrayList<>();

	public GraphPanel() {
		super();
		// this.parentFrame = frame;
		this.param = GraphParameters.getDefaultParam();

		this.pointSets = new ArrayList<>();
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addMouseWheelListener(this);
		this.addComponentListener(this);

	}

	@Override
	public void paintComponent(Graphics g) {
		// TODO comment next line out see what happens, move it and zoom in/out
		super.paintComponent(g);

		g.setColor(Color.BLACK);

		g.setFont(GraphPanel.numFont);

		if (this.param.showaxis) {
			// pixels
			int actualcenterx = (int) math.AKMath.scale(0, this.param.minx, this.param.maxx, 0, this.getWidth());
			int actualcentery = (int) math.AKMath.scale(0, this.param.miny, this.param.maxy, this.getHeight(), 0);

			g.drawLine(actualcenterx, 0, actualcenterx, this.getHeight());
			g.drawLine(0, actualcentery, this.getWidth(), actualcentery);
			g.drawString(0 + "", actualcenterx - GraphPanel.xoffset, actualcentery + GraphPanel.yoffset);

			g.drawString(this.param.maxx + "", this.getWidth() - GraphPanel.xoffset * (this.param.maxx + "").length(),
					actualcentery + GraphPanel.yoffset);
			g.drawString(this.param.minx + "", 0, actualcentery + GraphPanel.yoffset);
			g.drawString(this.param.maxy + "", actualcenterx - GraphPanel.xoffset * (this.param.maxy + "").length(),
					GraphPanel.yoffset);
			g.drawString(this.param.miny + "", actualcenterx - GraphPanel.xoffset * (this.param.miny + "").length(),
					this.getHeight() - GraphPanel.yoffset);
		}

		for (UIPointSet pointSet : this.pointSets) {

			int prevpx = 0, prevpy = 0;
			for (int i = 0; i < pointSet.size(); i++) {

				Vector2D<Double> point = pointSet.get(i);
				// pixels
				int px = (int) math.AKMath.scale(point.x(), this.param.minx, this.param.maxx, 0, this.getWidth());
				int py = (int) math.AKMath.scale(point.y(), this.param.miny, this.param.maxy, this.getHeight(), 0);

				if (pointSet.getPointColor() != null) {
					int pointWidth = 10;
					g.setColor(pointSet.getPointColor());
					g.fillOval(px - pointWidth / 2, py - pointWidth / 2, pointWidth, pointWidth);
				}

				if (pointSet.getLineColor() != null && i > 0) {// if (Math.abs(y2 - y) < (getHeight())) {
					g.setColor(pointSet.getLineColor());
					g.drawLine(prevpx, prevpy, px, py);
					for (int ii = 1; ii < 2; ii++) {
						g.drawLine(prevpx, prevpy - ii, px, py - ii);
						g.drawLine(prevpx, prevpy + ii, px, py + ii);
					}
				}
				prevpx = px;
				prevpy = py;

			}
		}

		for (PointBox bx : this.savedBoxes) {
			bx.updatePixCoordinate();
			g.setColor(bx.pointSet.getPointColor());
			bx.paint(g, this.clickingmBox && this.mBox.equals(bx), false);
		}
		if (this.showmBox) {
			int index = this.savedBoxes.indexOf(this.mBox);
			g.setColor(this.mBox.pointSet.getPointColor());
			if (index < 0) {
				this.mBox.paint(g, this.clickingmBox, false);
			} else {
				this.savedBoxes.get(index).paint(g, this.clickingmBox, true);
			}
			// now draw the box (top left)
			int corneroffset = 10;
			String eqS = "y  = " + this.mBox.pointSet.getTitle();
			String fnumS = this.pointSets.indexOf(this.mBox.pointSet) + "";

			int boxx = corneroffset + 10;
			int boxy = corneroffset;
			int boxw = eqS.length() * (GraphPanel.xoffset - 4) + 8 + corneroffset;
			int boxh = GraphPanel.yoffset + corneroffset;

			g.clearRect(boxx, boxy, boxw, boxh);

			g.drawString(eqS, GraphPanel.xoffset + corneroffset, GraphPanel.yoffset + corneroffset);

			g.setFont(GraphPanel.subFont);
			g.drawString(fnumS, GraphPanel.xoffset * 2 + corneroffset, GraphPanel.yoffset * 4 / 3 + corneroffset);

			g.drawRect(boxx, boxy, boxw, boxh);

		}
	}

	// private static ImageIcon getImage(String img, int x, int y) {
	// ImageIcon icon = new ImageIcon(GraphPanel.class.getResource("/img/" + img +
	// ".png"));
	// Image image = icon.getImage();
	// image = image.getScaledInstance(x, y, Image.SCALE_SMOOTH);
	// icon = new ImageIcon(image);
	// return icon;
	// }

	double rangex = 0, rangey = 0;

	private void updateUnitRangeCheck() {
		this.rangex = (this.param.maxx - this.param.minx) / 200;
		this.rangey = (this.param.maxy - this.param.miny) / 200;
	}

	private boolean isSameXUnit(double x1, double x2) {
		if (Math.abs(x1 - x2) <= this.rangex)
			return true;
		else
			return false;
	}

	private boolean isSameYUnit(double y1, double y2) {
		if (Math.abs(y1 - y2) <= this.rangey)
			return true;
		else
			return false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// dont show coor box when dragging mouse
		this.showmBox = false;

		int newx = e.getX(), newy = this.getHeight() - e.getY();
		int changex = newx - this.mousemovex, changey = newy - this.mousemovey;

		double changewindowx = changex * (this.param.maxx - this.param.minx) / this.getWidth();
		double changewindowy = changey * (this.param.maxy - this.param.miny) / -this.getHeight();

		this.param.maxx -= changewindowx;
		this.param.minx -= changewindowx;
		this.param.maxy += changewindowy;
		this.param.miny += changewindowy;

		this.mousemovex = newx;
		this.mousemovey = newy;

		this.repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		// dont show coor box when dragging mouse
		this.showmBox = false;

		int moved = event.getWheelRotation();

		int mousex = event.getX();
		int mousey = event.getY();

		double graphmousex = (double) mousex / this.getWidth() * (this.param.maxx - this.param.minx) + this.param.minx;
		double graphmousey = (1 - (double) mousey / this.getHeight()) * (this.param.maxy - this.param.miny)
				+ this.param.miny;

		double maxx = this.param.maxx, minx = this.param.minx, maxy = this.param.maxy, miny = this.param.miny;

		this.param.maxx += moved * (maxx - graphmousex) / 10;
		this.param.minx -= moved * (graphmousex - minx) / 10;
		this.param.maxy += moved * (maxy - graphmousey) / 10;
		this.param.miny -= moved * (graphmousey - miny) / 10;

		this.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.clickingmBox = true;
		this.repaint();

		this.mousemovex = e.getX();
		this.mousemovey = this.getHeight() - e.getY();

		// System.out.println(mousemovex + " " + mousemovey);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.clickingmBox = false;
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double unitx = math.AKMath.scale(e.getX(), 0, this.getWidth(), this.param.minx, this.param.maxx);
		double unity = math.AKMath.scale(e.getY(), this.getHeight(), 0, this.param.miny, this.param.maxy);

		this.updateUnitRangeCheck();
		for (UIPointSet pointSet : this.pointSets) {
			for (int i = 0; i < pointSet.size(); i++) {
				// if (unitx == f.points.get(i) && unity == f.points.get(i +1){
				if (this.isSameXUnit(pointSet.get(i).x(), unitx) && this.isSameYUnit(pointSet.get(i).y(), unity)) {
					this.mBox.valx = pointSet.get(i).x();
					this.mBox.valy = pointSet.get(i).y();
					// cant just use e.getx/y b/c it moves the with the cursor,
					// try it
					this.mBox.pixx = (int) math.AKMath.scale(this.mBox.valx, this.param.minx, this.param.maxx, 0,
							this.getWidth());
					this.mBox.pixy = (int) math.AKMath.scale(this.mBox.valy, this.param.miny, this.param.maxy,
							this.getHeight(), 0);
					this.mBox.pointSet = pointSet;
					this.showmBox = true;
					this.showmBoxPrev = true;
					this.repaint();
					return;
				}
			}
		}
		this.showmBox = false;
		if (this.showmBox != this.showmBoxPrev) {
			this.repaint();
		}
		this.showmBoxPrev = this.showmBox;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (this.showmBox) {
			int i = this.savedBoxes.indexOf(this.mBox);

			if (i < 0) {
				this.savedBoxes.add(new PointBox(this.mBox));
			} else {
				this.savedBoxes.remove(i);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {

	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

}

class GraphParameters {
	public boolean showaxis;

	public double minx;
	public double maxx;
	public double miny;
	public double maxy;

	public GraphParameters(GraphParameters another) {
		this.showaxis = another.showaxis;

		this.minx = another.minx;
		this.maxx = another.maxx;
		this.miny = another.miny;
		this.maxy = another.maxy;
	}

	public GraphParameters() {
	}

	public static GraphParameters getDefaultParam() {
		GraphParameters param = new GraphParameters();

		param.minx = -10;
		param.maxx = 10;
		param.miny = -10;
		param.maxy = 10;

		param.showaxis = true;

		return param;
	}

	public boolean equals(GraphParameters another) {
		if (this.minx != another.minx)
			return false;
		if (this.maxx != another.maxx)
			return false;
		if (this.miny != another.miny)
			return false;
		if (this.maxy != another.maxy)
			return false;

		if (this.showaxis != another.showaxis)
			return false;

		return true;
	}

}
