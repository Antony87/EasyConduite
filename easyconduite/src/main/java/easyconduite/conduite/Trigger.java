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
import easyconduite.model.MediaPlayable;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeSet;

/**
 * this class implements track trigger features.
 */
public class Trigger implements Comparable {

    private int index;

    private final TreeSet<MediaAction> mediaActions;

    public Trigger(Conduite conduite) {
        final SortedMap<Integer,Trigger> triggers = conduite.getTriggers();
        if(triggers.isEmpty()){
            index =1;
        }else{
            final Integer last = triggers.lastKey();
            index = last+1;
        }
        mediaActions = new TreeSet<>();
    }

    public void playActions() {
        mediaActions.forEach((mediaAction) -> {
            mediaAction.playStatut();
        });
    }

    public MediaAction createMediaAction(MediaPlayable media){
        final MediaAction action = new MediaAction(this,media, MediaStatus.UNKNOWN);
        mediaActions.add(action);
        return action;
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

    @Override
    public String toString() {
        return "Trigger{" +
                "index=" + index +
                ", mediaActions=" + mediaActions +
                '}';
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public TreeSet<MediaAction> getMediaActions() {
        return mediaActions;
    }
}
