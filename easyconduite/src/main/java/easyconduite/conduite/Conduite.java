package easyconduite.conduite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.LinkedList;

public class Conduite {

    @JsonIgnore
    private static final Logger LOG = LogManager.getLogger(Conduite.class);

    private final LinkedList<Trigger> listeTriggers;

    @JsonIgnore
    private int currentIndex = 0;

    public Conduite() {
        listeTriggers = new LinkedList<>();
    }

    public Trigger createTrigger() {
        int index;
        if (listeTriggers.isEmpty()) {
            index = 1;
        } else {
            index = listeTriggers.getLast().getIndex() + 1;
        }
        LOG.trace("Create Trigger with index {}", index);
        final Trigger trigger = new Trigger(index);
        listeTriggers.addLast(trigger);
        Collections.sort(listeTriggers);
        LOG.trace("Add trigger {} to triggers map", trigger);
        return trigger;
    }

    public void fireNextTrigger() {
        if (!listeTriggers.isEmpty()) {
            inactiveAllTriggers();
            forwardCurrentIndex();
            final Trigger actionTrigger = listeTriggers.get(currentIndex - 1);
            actionTrigger.runAllActions();
        }
    }

    public void firePreviousTrigger() {
        if (!listeTriggers.isEmpty()) {
            inactiveAllTriggers();
            backwardCurrentIndex();
            final Trigger actionTrigger = listeTriggers.get(currentIndex - 1);
            actionTrigger.runAllActions();
        }
    }

    public void fireRewindTrigger() {
        if (!listeTriggers.isEmpty()) {
            inactiveAllTriggers();
            currentIndex = 0;
        }
    }

    private void forwardCurrentIndex() {
        int lastIndex = listeTriggers.getLast().getIndex();
        LOG.trace("nextIndex from last = {} and current = {}", lastIndex, currentIndex);
        if (lastIndex > currentIndex) currentIndex++;
    }

    private void backwardCurrentIndex() {
        int firstIndex = listeTriggers.getFirst().getIndex();
        LOG.trace("nextIndex from first = {} and current = {}", firstIndex, currentIndex);
        if (firstIndex < currentIndex) currentIndex--;
    }

    private void inactiveAllTriggers() {
        listeTriggers.forEach(trigger -> trigger.setActif(false));
    }

    public LinkedList<Trigger> getListeTriggers() {
        return listeTriggers;
    }

    @Override
    public String toString() {
        return "Conduite{" +
                "listeTriggers=" + listeTriggers +
                ", currentIndex=" + currentIndex +
                '}';
    }
}
