package logging;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {
    protected List<Observer> observers;

    // Constructor; initializes reference to empty list of observers
    public Observable() {
        observers = new ArrayList<>();
    }

    // Modifies this
    // Adds observer to list
    public void add(Observer o) {
        observers.add(o);
    }

    // Modifies EventLog
    // Signals change of this to observers
    public abstract void signal(String s);
}
