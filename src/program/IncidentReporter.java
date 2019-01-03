package program;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * IncidentReporters are used to maintain multiple IncidentListeners that are
 * listening for the same exact event to occur.
 *
 * @author Akarsh
 *
 */
public class IncidentReporter {

	/**
	 * Listeners listening to this for broadcasts from this reporter. The boolean
	 * value suggests if the listener is only listening for one broadcast. If it is
	 * true, the listener will be removed from the list of listeners after it has
	 * received one broadcast.
	 */
	private final List<IncidentListener> listeners;

	/**
	 * Initializes the IncidentReporter.
	 */
	public IncidentReporter() {
		this.listeners = new ArrayList<>();
	}

	/**
	 * Notifies all of the IncidentListeners that a incident has been reported with
	 * the given specifications.
	 *
	 * @param specifications for the listeners
	 */
	public void reportIncident(Object... args) {
		Iterator<IncidentListener> it = this.listeners.iterator();
		while (it.hasNext()) {
			IncidentListener listener = it.next();

			listener.incidentReported(args);
		}
	}

	/**
	 * @return the list of listeners.
	 */
	public List<IncidentListener> getListeners() {
		return this.listeners;
	}

	/**
	 * Adds the given listener to the list of listeners that will be notified on
	 * broadcast of this IncidentReporter.
	 *
	 * @param listener
	 */
	public void add(IncidentListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Removes the given listener from the list of listeners to be notified on
	 * broadcast.
	 *
	 * @param listener
	 */
	public void remove(IncidentListener listener) {
		this.listeners.remove(listener);
	}

}