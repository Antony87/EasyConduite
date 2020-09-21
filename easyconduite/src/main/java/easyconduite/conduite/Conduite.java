package easyconduite.conduite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.SortedMap;
import java.util.TreeMap;

public class Conduite {

    @JsonIgnore
    private static final Logger LOG = LogManager.getLogger(Conduite.class);

    private final SortedMap<Integer, Trigger> triggers;

    @JsonIgnore
    private int currentIndex = 0;

    public Conduite() {
        triggers = new TreeMap<>();
    }

    public Trigger createTrigger() {
        int index;
        if (triggers.isEmpty()) {
            index = 1;
        } else {
            index = triggers.lastKey() + 1;
        }
        final Trigger trigger = new Trigger();
        triggers.put(index, trigger);
        LOG.trace("Add trigger {} to triggers map", trigger);
        return trigger;
    }

    public void fireNextTrigger() {
        if (!triggers.isEmpty()) {
            inactiveAllTriggers();
            forwardCurrentIndex();
            final Trigger actionTrigger = triggers.get(currentIndex);
            actionTrigger.runActions();
        }
    }

    public void firePreviousTrigger() {
        if (!triggers.isEmpty()) {
            inactiveAllTriggers();
            backwardCurrentIndex();
            final Trigger actionTrigger = triggers.get(currentIndex);
            actionTrigger.runActions();
        }
    }

    public void fireRewindTrigger(){
        if(!triggers.isEmpty()){
            inactiveAllTriggers();
            currentIndex=0;
        }
    }

    private void forwardCurrentIndex() {
        int lastIndex = triggers.lastKey();
        LOG.trace("nextIndex from last = {} and current = {}", lastIndex, currentIndex);
        if (lastIndex > currentIndex) currentIndex++;
    }

    private void backwardCurrentIndex() {
        int firstIndex = triggers.firstKey();
        LOG.trace("nextIndex from first = {} and current = {}", firstIndex, currentIndex);
        if (firstIndex<currentIndex) currentIndex--;
    }

    private void inactiveAllTriggers() {
        triggers.forEach((integer, trigger) -> trigger.setActif(false));
    }

    public SortedMap<Integer, Trigger> getTriggers() {
        return triggers;
    }
}
