package easyconduite.conduite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.SortedMap;
import java.util.TreeMap;

public class Conduite {

    @JsonIgnore
    private static final Logger LOG = LogManager.getLogger(Conduite.class);

    private final SortedMap<Integer,Trigger> triggers;

    @JsonIgnore
    private int seekIndex;

    @JsonIgnore
    private Trigger currentTrigger;

    public Conduite() {
        triggers = new TreeMap<>();
    }

    public Trigger createTrigger(){
        final Trigger trigger = new Trigger(this);
        triggers.put(trigger.getIndex(),trigger);
        LOG.trace("Add trigger {} to triggers map",trigger);
        return trigger;
    }

    public void fireNextTrigger(){
        if(currentTrigger!=null){
            seekIndex=currentTrigger.getIndex()+1;
        }else{
            seekIndex=1;
        }
        inactiveAllTriggers();
        currentTrigger=triggers.get(seekIndex);
        currentTrigger.playActions();
    }

    public void firePreviousTrigger(){

    }

    private void inactiveAllTriggers(){
        triggers.forEach((integer, trigger) -> trigger.setActif(false));
    }

    public SortedMap<Integer,Trigger> getTriggers() {
        return triggers;
    }

    public int getSeekIndex() {
        return seekIndex;
    }

    public void setSeekIndex(int seekIndex) {
        this.seekIndex = seekIndex;
    }
}
