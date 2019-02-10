package ui;

public class GraphParameters {
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
