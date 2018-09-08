package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import data.Association;
import data.tuple.Tuple3D;

public class Log {

	private static final Association<Tuple3D<String, ?, PrintWriter>> writers = new Association<>();

	public static final String DEFAULT_LOGGER_ID = "DEFAULT_LOGGER";

	public static void registerLogger(String ID, String fileName, boolean autoFlush) {
		if (!fileName.endsWith(".txt"))
			throw new IllegalArgumentException(fileName + " is not a txt file.");
		registerLogger(ID, new File(fileName), autoFlush);
	}

	public static void registerLogger(String ID, File file, boolean autoFlush) {
		try {
			PrintWriter pw = autoFlush
					? new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))), true)
					: new PrintWriter(file);
			writers.setTuple(new Tuple3D<String, File, PrintWriter>(ID, file, pw), 0);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void registerLogger(String ID, OutputStream out, boolean autoFlush) {
		PrintWriter pw = new PrintWriter(out, autoFlush);
		writers.setTuple(new Tuple3D<String, OutputStream, PrintWriter>(ID, out, pw), 0);
	}

	public static void registerLogger(File file, boolean autoFlush) {
		registerLogger(DEFAULT_LOGGER_ID, file, autoFlush);
	}

	public static Object getLoggingLocation(String ID) {
		return writers.getTuple(ID).getB();
	}

	public static PrintWriter getLogger(Object ID) {
		return writers.getTuple(ID).getC();
	}

	public static PrintWriter getLogger() {
		return getLogger(DEFAULT_LOGGER_ID);
	}

	public static void print(Object ID, Object message) {
		getLogger(ID).print(message);
	}

	public static void print(Object message) {
		getLogger().print(message);
	}

	public static void println(Object ID) {
		getLogger(ID).println();
	}

	public static void println() {
		getLogger().println();
	}

	public static void println(Object ID, String message) {
		getLogger(ID).println(message);
	}

	public static void println(String message) {
		getLogger().println(message);
	}

	public static void flush(Object ID) {
		getLogger(ID).flush();
	}

	public static void flush() {
		getLogger().flush();
	}

}
