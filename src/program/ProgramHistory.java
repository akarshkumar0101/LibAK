package program;

import java.io.File;

import io.Serialization;

public interface ProgramHistory {

	public abstract File getFileLocation();

	public abstract Object getHistoryObject();

	public default Object loadHistory() {
		try {
			return Serialization.loadObject(getFileLocation());
		} catch (Exception e) {
			return null;
		}
	}

	public default void saveHistory() {
		Serialization.saveObject(getHistoryObject(), getFileLocation());
	}

	public default void setupAutomaticSaveAtTermination() {
		Thread shutdownThread = new Thread() {
			@Override
			public void run() {
				saveHistory();
			}
		};
		Runtime.getRuntime().addShutdownHook(shutdownThread);
	}

}
