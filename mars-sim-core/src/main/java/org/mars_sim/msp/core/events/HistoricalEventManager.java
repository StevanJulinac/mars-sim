/**
 * Mars Simulation Project
 * HistoricalEventManager.java
 * @version 3.1.0 2017-09-09
 * @author Barry Evans
 */

package org.mars_sim.msp.core.events;

import org.mars_sim.msp.core.Simulation;
import org.mars_sim.msp.core.narrator.Narrator;
import org.mars_sim.msp.core.time.MarsClock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class provides a manager that maintains a model of the events that
 * have occurred during the current simulation run. It provides support for a
 * listener pattern so the external objects can be notified when new events have
 * been registered.
 * The manager maintains an ordered list in terms of decreasing time, i.e.
 * most recent event first.
 * It should be noted that the throughput of new events of the manager can be
 * in the order of 100 event per simulation tick.
 */
public class HistoricalEventManager implements Serializable {

    /** default serial id. */
    private static final long serialVersionUID = 1L;
	/**
	 * This defines the maximum number of events that are stored.
	 * It should be a standard property.
	 */
	private final static int TRANSIENT_EVENTS = 500;

	//private static int count;
	
	private transient List<HistoricalEventListener> listeners;
	
	private List<HistoricalEvent> events;// = new LinkedList<HistoricalEvent>();
	
	private Narrator narrator;
	
	private MarsClock marsClock;

	private transient List<HistoricalEvent> lastEvents = new ArrayList<>();;
	
	/**
	 * Create a new EventManager that represents a particular simulation.
	 */
	public HistoricalEventManager() {
		//logger.info("HistoricalEventManager's constructor is on " + Thread.currentThread().getName());
		// Note : the masterClock and marsClock CANNOAT initialized until the simulation start
		listeners = new ArrayList<HistoricalEventListener>();
		events = new LinkedList<HistoricalEvent>();
		
		narrator = new Narrator();		
	}

	/**
	 * Add a historical event listener
	 * @param newListener listener to add.
	 */
	// 5 models or panels called addListener()
	public void addListener(HistoricalEventListener newListener) {
		if (listeners == null) listeners = new ArrayList<HistoricalEventListener>();
		if (!listeners.contains(newListener)) listeners.add(newListener);
	}

	/**
	 * Removes a historical event listener.
	 * @param oldListener listener to remove.
	 */
	public void removeListener(HistoricalEventListener oldListener) {
		if (listeners.contains(oldListener)) listeners.remove(oldListener);
	}

	/**
	 * Get the event at a specified index.
	 * @param index Index of event to retrieve.
	 * @return Historical event.
	 */
	public HistoricalEvent getEvent(int index) {
		return events.get(index);
	}

	
	
	public boolean isSameEvent(HistoricalEvent newEvent) {
		boolean result = false;
		for (HistoricalEvent e : lastEvents) {
			
			if (e.getWhatCause().equals(newEvent.getWhatCause())
				|| e.getWho().equals(newEvent.getWho())
				|| e.getType() == newEvent.getType()
				|| e.getCategory() == newEvent.getCategory()
				|| e.getLocation0().equals(newEvent.getLocation0())
				|| e.getLocation1().equals(newEvent.getLocation1())
			 ) {
				result = true;
			}
			
		}
		return result;
	}
	
	/**
	 * An new event needs registering with the manager. The event will be
	 * time stamped with the current clock time and inserted at position zero.
	 * @param newEvent The event to register.
	 */
	// include any kind of events
	public void registerNewEvent(HistoricalEvent newEvent) {
		HistoricalEventCategory category = newEvent.getCategory();
		if (!category.equals(HistoricalEventCategory.TASK)
				&& !isSameEvent(newEvent)) {
			
			lastEvents.add(newEvent);
			if (lastEvents.size() > 5)
				lastEvents.remove(0);
	
			// check if event is MALFUNCTION or MEDICAL, save it for notification box display
			// Make space for the new event.
			if (events.size() >= TRANSIENT_EVENTS) {
				int excess = events.size() - (TRANSIENT_EVENTS - 1);
				removeEvents(events.size() - excess, excess);
			}
			// Note : the elaborate if-else conditions below is for passing the maven test
			if (marsClock == null) 
				marsClock = Simulation.instance().getMasterClock().getMarsClock();
		
			MarsClock timestamp =  (MarsClock) marsClock.clone();
			// Note: for debugging the NullPointerException at newEvent.setTimestamp(timestamp);
			 if (timestamp == null)
				 throw new IllegalStateException("timestamp is null");
			 
			newEvent.setTimestamp(timestamp);
	
			events.add(0, newEvent);

			if (listeners == null) {
				listeners = new ArrayList<HistoricalEventListener>();
			}
			
			Iterator<HistoricalEventListener> iter = listeners.iterator();
			while (iter.hasNext()) 
				iter.next().eventAdded(0, newEvent);

			narrator.translate(newEvent);
		}
	}

	/**
	 * An event is removed from the list.
	 * @param index Index of the event to be removed.
	 * @param number Number to remove.
	 */
	private void removeEvents(int index, int number) {

		// Remove the rows
		for(int i = index; i < (index + number); i++) {
			events.remove(i);
		}

		Iterator<HistoricalEventListener> iter = listeners.iterator();
		while(iter.hasNext()) iter.next().eventsRemoved(index, index + number);
	}

	/**
	 * Get the number of events in the manager.
	 * @return Stored event count.
	 */
	public int size() {
		return events.size();
	}

	public List<HistoricalEvent> getEvents() {
		return events;
	}
	
	
	/**
	 * Prepare object for garbage collection.
	 */
	public void destroy() {
		listeners.clear();
		listeners = null;
		events.clear();
		events = null;
	}
}