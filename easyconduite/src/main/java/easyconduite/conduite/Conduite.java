package easyconduite.conduite;

import java.util.SortedSet;
import java.util.TreeSet;

public class Conduite {

    private final SortedSet<Trigger> triggers;

    public Conduite() {
        triggers = new TreeSet<>();
    }

    public SortedSet<Trigger> getTriggers() {
        return triggers;
    }
}
