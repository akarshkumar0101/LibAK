package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TextFileWriter extends PrintWriter {

	public TextFileWriter(File file) throws FileNotFoundException {
		super(file);
	}

	public TextFileWriter(String fileName) throws FileNotFoundException {
		super(fileName);
	}

}
