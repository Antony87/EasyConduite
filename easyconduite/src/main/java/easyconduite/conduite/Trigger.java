/*
 *
 *
 *  * Copyright (c) 2020.  Antony Fons
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package easyconduite.conduite;

import easyconduite.media.MediaStatus;
import easyconduite.model.AbstractMedia;

import java.util.*;

/**
 * this class implements track trigger features.
 */
public class Trigger implements Comparable {

    private int index;

    private final SortedMap<Integer,MediaAction> mediaActions;

    public Trigger(Conduite conduite) {
        SortedMap<Integer,Trigger> triggers = conduite.getTriggers();
        if(triggers.isEmpty()){
            index =1;
        }else{
            final Integer last = triggers.lastKey();
            index = last+1;
        }
        mediaActions = new TreeMap<>();
    }

    public void playActions() {
        mediaActions.forEach((integer, mediaAction) -> {
            mediaAction.switchToStatut();
        });
    }

    public void addNewMediaAction(AbstractMedia media){
        MediaAction action = new MediaAction(this,media, MediaStatus.UNKNOWN);
        mediaActions.put(action.getIndex(),action);
    }

    @Override
    public int compareTo(Object o) {
        Trigger trigger = (Trigger) o;
        if (index == trigger.index) return 0;
        if (index > trigger.index) return 1;
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trigger)) return false;
        Trigger trigger = (Trigger) o;
        return index == trigger.index &&
                Objects.equals(mediaActions, trigger.mediaActions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, mediaActions);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public SortedMap<Integer,MediaAction> getMediaActions() {
        return mediaActions;
    }
}
