package easyconduite.conduite;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class Conduite {

    private final SortedMap<Integer,Trigger> triggers;

    public Conduite() {
        triggers = new TreeMap<>();
    }

    public void addNewTrigger(){
        final Trigger trigger = new Trigger(this);
        triggers.put(trigger.getIndex(),trigger);
    }

    public SortedMap<Integer,Trigger> getTriggers() {
        return triggers;
    }
}
