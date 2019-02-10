package time;

public class Stopwatch {
	long startTime = 0;

	public void start() {
		this.startTime = System.currentTimeMillis();
	}

	public long currentTimeElapsed() {
		return System.currentTimeMillis() - this.startTime;
	}

}
