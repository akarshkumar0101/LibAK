package math.stat;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JComponent;

import math.AKMath;

public class StatisticsUI extends JComponent {

	private static final long serialVersionUID = -6017284366679545796L;

	private final List<Statistic<?>> statistics;

	private double max;
	private double min;

	public StatisticsUI(List<Statistic<?>> statistics) {
		// assume statistic is calculated already
		this.statistics = statistics;

		this.max = -Double.MAX_VALUE;
		this.min = Double.MAX_VALUE;

		for (Statistic<?> stat : this.statistics) {
			this.max = Math.max(this.max, stat.getMaxVal());
			this.min = Math.min(this.min, stat.getMinVal());
		}
	}

	private static final DecimalFormat df = new DecimalFormat("#####.###");

	@Override
	public void paintComponent(Graphics g) {

		// draw yvalue
		for (double yVal = this.min; yVal <= this.max; yVal += (this.max - this.min) / 10) {
			int py = (int) AKMath.scale(yVal, this.min, this.max, this.getHeight(), 0);

			g.setColor(Color.LIGHT_GRAY);
			g.drawString("" + StatisticsUI.df.format(yVal), 0, py);

			g.drawLine(0, py, this.getWidth(), py);
		}

		// draw x labels
		for (double xVal = 0; xVal < this.statistics.size(); xVal += (double) this.statistics.size() / 10) {
			int px = (int) AKMath.scale(xVal, 0, this.statistics.size(), 0, this.getWidth());

			g.setColor(Color.LIGHT_GRAY);
			g.drawString("" + StatisticsUI.df.format(xVal), px, this.getHeight());

			g.drawLine(px, 0, px, this.getHeight());
		}

		for (int statx = 0; statx < this.statistics.size(); statx++) {
			Statistic<?> stat = this.statistics.get(statx);

			int px = (int) AKMath.scale(statx, 0, this.statistics.size(), 0, this.getWidth());

			// draw circle
			for (double percentile = 0.0; percentile <= 1.0; percentile += 0.1) {
				double val = stat.getPercentileVal(percentile);
				int py = (int) AKMath.scale(val, this.min, this.max, this.getHeight(), 0);

				int dia = 3;
				g.setColor(Color.BLACK);
				g.fillOval(px - dia / 2, py - dia / 2, dia, dia);
			}

			// draw line
			if (statx > 0) {
				Statistic<?> prevStat = this.statistics.get(statx - 1);
				int prevpx = (int) AKMath.scale(statx - 1, 0, this.statistics.size(), 0, this.getWidth());

				for (double percentile = 0.0; percentile <= 1.0; percentile += 0.1) {
					double val = stat.getPercentileVal(percentile);
					int py = (int) AKMath.scale(val, this.min, this.max, this.getHeight(), 0);

					double prevVal = prevStat.getPercentileVal(percentile);
					int prevpy = (int) AKMath.scale(prevVal, this.min, this.max, this.getHeight(), 0);

					if (percentile == 0.5) {
						g.setColor(Color.RED);
					} else {
						g.setColor(Color.BLACK);
					}
					g.drawLine(prevpx, prevpy, px, py);
				}

				{
					// average line
					double avg = stat.getAverageVal();
					int py = (int) AKMath.scale(avg, this.min, this.max, this.getHeight(), 0);

					double prevVal = prevStat.getAverageVal();
					int prevpy = (int) AKMath.scale(prevVal, this.min, this.max, this.getHeight(), 0);

					g.setColor(Color.BLUE);

					g.drawLine(prevpx, prevpy, px, py);
				}

			}
		}

	}
}
