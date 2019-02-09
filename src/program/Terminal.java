package program;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public abstract class Terminal implements Runnable {

	private final InputStream is;
	private final PrintStream pos;

	private final Scanner isScanner;

	public Terminal(InputStream is, PrintStream pos) {
		this.is = is;
		this.pos = pos;
		this.isScanner = new Scanner(is);
	}

	@Override
	public void run() {
		while (true) {
			String input = this.isScanner.nextLine();
			int spcIndex = input.indexOf(' ');
			// String command = input;
			if (spcIndex != -1) {

			}
		}
	}

	public InputStream getInputStream() {
		return this.is;
	}

	public PrintStream getPrintOutputStream() {
		return this.pos;
	}

}
