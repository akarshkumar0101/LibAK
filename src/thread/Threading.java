package thread;

public class Threading {

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static Thread newThread(Runnable run, boolean startInstantly) {
		Thread thread = new Thread(run);
		if (startInstantly) {
			thread.start();
		}
		return thread;
	}

	public static Thread[] newThreads(Runnable[] runs, boolean startInstantly) {
		Thread[] threads = new Thread[runs.length];
		for (int i = 0; i < runs.length; i++) {
			threads[i] = Threading.newThread(runs[i], startInstantly);
		}
		return threads;
	}
}
