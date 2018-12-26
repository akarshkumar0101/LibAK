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
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GraphPanel extends JComponent
		implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {

	public static final Font numFont = new Font("TimesRoman", Font.PLAIN, 25);
	public static final Font subFont = new Font("TimesRoman", Font.PLAIN, 17);

	public static final int MAX_NUM_FUNCTIONS = 8;
	// 25,14
	public static final int yoffset = 25, xoffset = 14;

	public final JFrame parentFrame;

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
		private static final int CIRCLEDIA = 2 * CIRCLERAD;

		public PointBox() {
		}

		public PointBox(PointBox another) {
			pixx = another.pixx;
			pixy = another.pixy;
			valx = another.valx;
			valy = another.valy;
			pointSet = another.pointSet;
		}

		/**
		 * pixx coordinates should be updated through the PointBox.updateBixCoordinate
		 * method prior to calling this method
		 */
		private void paint(Graphics g, boolean clicked, boolean highlight) {
			if (!(pixx > 0 && pixx < getWidth() && pixy > 0 && pixy < getHeight()))
				return;
			int raddif = clicked ? 10 : (highlight ? CIRCLERAD * 2 / 3 : 0);
			int diadif = raddif * 2;
			g.fillOval(pixx - CIRCLERAD - raddif, pixy - CIRCLERAD - raddif, CIRCLEDIA + diadif, CIRCLEDIA + diadif);

			// drawing the box
			String toDraw = valx + ", " + valy;
			int boxw = toDraw.length() * (xoffset - 1) + 8;
			int boxh = yoffset + 6;
			g.clearRect(pixx + 7, pixy - 10 - yoffset, boxw, boxh);
			g.drawRect(pixx + 7, pixy - 10 - yoffset, boxw, boxh);
			g.drawString(toDraw, pixx + 10, pixy - 10);
		}

		public void updatePixCoordinate() {
			pixx = (int) math.AKMath.scale(valx, param.minx, param.maxx, 0, getWidth());
			pixy = (int) math.AKMath.scale(valy, param.miny, param.maxy, getHeight(), 0);
		}

		@Override
		public boolean equals(Object another) {
			if (another.getClass() != PointBox.class)
				return false;
			PointBox an = (PointBox) another;
			updateUnitRangeCheck();
			if (pointSet == an.pointSet && pixx <= an.pixx + CIRCLEDIA && pixx >= an.pixx - CIRCLEDIA
					&& pixy <= an.pixy + CIRCLEDIA && pixy >= an.pixy - CIRCLEDIA && isSameXUnit(valx, an.valx)
					&& isSameYUnit(valy, an.valy))
				return true;
			else
				return false;
		}

	}

	private PointBox mBox = new PointBox();
	private boolean showmBox = false;
	private boolean showmBoxPrev = false;
	private boolean clickingmBox = false;

	private ArrayList<PointBox> savedBoxes = new ArrayList<PointBox>();

	public GraphPanel(JFrame frame) {
		super();
		parentFrame = frame;
		param = GraphParameters.getDefaultParam();

		pointSets = new ArrayList<>();
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);

	}

	@Override
	public void paintComponent(Graphics g) {
		// TODO comment next line out see what happens, move it and zoom in/out
		super.paintComponent(g);

		g.setColor(Color.BLACK);

		g.setFont(numFont);

		if (param.showaxis) {
			// pixels
			int actualcenterx = (int) math.AKMath.scale(0, param.minx, param.maxx, 0, getWidth());
			int actualcentery = (int) math.AKMath.scale(0, param.miny, param.maxy, getHeight(), 0);

			g.drawLine(actualcenterx, 0, actualcenterx, getHeight());
			g.drawLine(0, actualcentery, getWidth(), actualcentery);
			g.drawString(0 + "", actualcenterx - xoffset, actualcentery + yoffset);

			g.drawString(param.maxx + "", getWidth() - xoffset * (param.maxx + "").length(), actualcentery + yoffset);
			g.drawString(param.minx + "", 0, actualcentery + yoffset);
			g.drawString(param.maxy + "", actualcenterx - xoffset * (param.maxy + "").length(), yoffset);
			g.drawString(param.miny + "", actualcenterx - xoffset * (param.miny + "").length(), getHeight() - yoffset);
		}

		for (UIPointSet pointSet : pointSets) {

			int prevpx = 0, prevpy = 0;
			for (int i = 0; i < pointSet.getPoints().length; i++) {

				double[] point = pointSet.getPoints()[i];
				// pixels
				int px = (int) math.AKMath.scale(point[0], param.minx, param.maxx, 0, getWidth());
				int py = (int) math.AKMath.scale(point[1], param.miny, param.maxy, getHeight(), 0);

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

		for (PointBox bx : savedBoxes) {
			bx.updatePixCoordinate();
			g.setColor(bx.pointSet.getPointColor());
			bx.paint(g, clickingmBox && mBox.equals(bx), false);
		}
		if (showmBox) {
			int index = savedBoxes.indexOf(mBox);
			g.setColor(mBox.pointSet.getPointColor());
			if (index < 0) {
				mBox.paint(g, clickingmBox, false);
			} else {
				savedBoxes.get(index).paint(g, clickingmBox, true);
			}
			// now draw the box (top left)
			int corneroffset = 10;
			String eqS = "y  = " + mBox.pointSet.getTitle();
			String fnumS = pointSets.indexOf(mBox.pointSet) + "";

			int boxx = corneroffset + 10;
			int boxy = corneroffset;
			int boxw = eqS.length() * (xoffset - 4) + 8 + corneroffset;
			int boxh = yoffset + corneroffset;

			g.clearRect(boxx, boxy, boxw, boxh);

			g.drawString(eqS, xoffset + corneroffset, yoffset + corneroffset);

			g.setFont(subFont);
			g.drawString(fnumS, xoffset * 2 + corneroffset, (yoffset * 4 / 3) + corneroffset);

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
		rangex = (param.maxx - param.minx) / 200;
		rangey = (param.maxy - param.miny) / 200;
	}

	private boolean isSameXUnit(double x1, double x2) {
		if (Math.abs(x1 - x2) <= rangex)
			return true;
		else
			return false;
	}

	private boolean isSameYUnit(double y1, double y2) {
		if (Math.abs(y1 - y2) <= rangey)
			return true;
		else
			return false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// dont show coor box when dragging mouse
		showmBox = false;

		int newx = e.getX(), newy = getHeight() - e.getY();
		int changex = newx - mousemovex, changey = newy - mousemovey;

		double changewindowx = changex * (param.maxx - param.minx) / (getWidth());
		double changewindowy = changey * (param.maxy - param.miny) / (-getHeight());

		param.maxx -= changewindowx;
		param.minx -= changewindowx;
		param.maxy += changewindowy;
		param.miny += changewindowy;

		mousemovex = newx;
		mousemovey = newy;

		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		// dont show coor box when dragging mouse
		showmBox = false;

		int moved = event.getWheelRotation();

		int mousex = event.getX();
		int mousey = event.getY();

		double graphmousex = (double) mousex / getWidth() * (param.maxx - param.minx) + param.minx;
		double graphmousey = ((1 - (double) mousey / getHeight()) * (param.maxy - param.miny) + param.miny);

		double maxx = param.maxx, minx = param.minx, maxy = param.maxy, miny = param.miny;

		param.maxx += moved * (maxx - graphmousex) / 10;
		param.minx -= moved * (graphmousex - minx) / 10;
		param.maxy += moved * (maxy - graphmousey) / 10;
		param.miny -= moved * (graphmousey - miny) / 10;

		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		clickingmBox = true;
		repaint();

		mousemovex = e.getX();
		mousemovey = getHeight() - e.getY();

		// System.out.println(mousemovex + " " + mousemovey);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		clickingmBox = false;
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double unitx = math.AKMath.scale(e.getX(), 0, getWidth(), param.minx, param.maxx);
		double unity = math.AKMath.scale(e.getY(), getHeight(), 0, param.miny, param.maxy);

		updateUnitRangeCheck();
		for (UIPointSet pointSet : pointSets) {
			for (int i = 0; i < pointSet.getPoints().length; i++) {
				// if (unitx == f.points.get(i) && unity == f.points.get(i +1){
				if (isSameXUnit(pointSet.getPoints()[i][0], unitx) && isSameYUnit(pointSet.getPoints()[i][1], unity)) {
					mBox.valx = pointSet.getPoints()[i][0];
					mBox.valy = pointSet.getPoints()[i][1];
					// cant just use e.getx/y b/c it moves the with the cursor,
					// try it
					mBox.pixx = (int) math.AKMath.scale(mBox.valx, param.minx, param.maxx, 0, getWidth());
					mBox.pixy = (int) math.AKMath.scale(mBox.valy, param.miny, param.maxy, getHeight(), 0);
					mBox.pointSet = pointSet;
					showmBox = true;
					showmBoxPrev = true;
					repaint();
					return;
				}
			}
		}
		showmBox = false;
		if (showmBox != showmBoxPrev) {
			repaint();
		}
		showmBoxPrev = showmBox;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (showmBox) {
			int i = savedBoxes.indexOf(mBox);

			if (i < 0) {
				savedBoxes.add(new PointBox(mBox));
			} else {
				savedBoxes.remove(i);
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
		showaxis = another.showaxis;

		minx = another.minx;
		maxx = another.maxx;
		miny = another.miny;
		maxy = another.maxy;
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
		if (minx != another.minx)
			return false;
		if (maxx != another.maxx)
			return false;
		if (miny != another.miny)
			return false;
		if (maxy != another.maxy)
			return false;

		if (showaxis != another.showaxis)
			return false;

		return true;
	}

}