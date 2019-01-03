package thread;

public class Threading {

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void join(Thread another) {
		try {
			another.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void join(Thread[] threads) {
		try {
			for (Thread t : threads) {
				t.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
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
