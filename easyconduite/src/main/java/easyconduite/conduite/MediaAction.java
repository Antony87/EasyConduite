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

import java.util.Objects;
import java.util.TreeSet;

public class MediaAction implements Comparable {

    private final int index;

    private final AbstractMedia media;

    private MediaStatus status;

    public MediaAction(Trigger trigger, AbstractMedia media, MediaStatus status) {
        this.media = media;
        this.status = status;
        TreeSet<MediaAction> actions = trigger.getMediaActions();
        if(actions.isEmpty()){
            index =1;
        }else{
            final Integer last = actions.last().getIndex();
            index = last+1;
        }
    }

    public void switchToStatut() {
        switch (status) {
            case STOPPED:
                media.stop();
                break;
            case PLAYING:
                media.play();
                break;
            case PAUSED:
                media.pause();
                break;
        }
    }

    public AbstractMedia getMedia() {
        return media;
    }

    public MediaStatus getStatus() {
        return status;
    }

    public void setStatus(MediaStatus status) {
        this.status = status;
    }

    public Integer getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaAction)) return false;
        MediaAction that = (MediaAction) o;
        return index == that.index &&
                media.equals(that.media) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, media, status);
    }

    @Override
    public int compareTo(Object o) {
        MediaAction action = (MediaAction) o;
        if (index == action.index) return 0;
        if (index > action.index) return 1;
        return -1;
    }
}
