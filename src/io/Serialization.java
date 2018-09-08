package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialization {
	public static Object loadObject(String fileName) {
		return loadObject(new File(fileName));
	}

	public static Object loadObject(File file) {
		Object obj = null;
		try {
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);

			obj = in.readObject();

			in.close();
			fileIn.close();
		} catch (Exception e) {
			throw new RuntimeException("Could not load object from " + file);
		}
		return obj;
	}

	public static void saveObject(Object obj, String fileName) {
		saveObject(obj, new File(fileName));
	}

	public static void saveObject(Object obj, File file) {
		try {
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);

			out.writeObject(obj);

			out.close();
			fileOut.close();
		} catch (Exception e) {
			throw new RuntimeException("Could not save object to " + file);
		}
	}
}
