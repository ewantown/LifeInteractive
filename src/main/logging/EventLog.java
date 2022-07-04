package logging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

// Log of events; singleton pattern ensures uniqueness
public class EventLog implements Iterable<Event>, Observer {
    private static EventLog theLog;
    private Collection<Event> events;

    // Constructs THE log
    private EventLog() {
        events = new ArrayList<>();
    }

    // Gets the log (calls constructor if not initialized)
    public static EventLog getInstance() {
        if (theLog == null) {
            theLog = new EventLog();
        }
        return theLog;
    }

    // Adds event to log
    public void logEvent(Event e) {
        events.add(e);
    }

    // Clears all events from log
    public void clear() {
        events.clear();
        logEvent(new Event("Event log cleared."));
    }

    @Override
    public Iterator<Event> iterator() {
        return events.iterator();
    }

    @Override
    public void update(Event e) {
        logEvent(e);
    }
}
