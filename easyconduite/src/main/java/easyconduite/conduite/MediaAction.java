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

import com.fasterxml.jackson.annotation.JsonIgnore;
import easyconduite.model.AbstractMedia;
import easyconduite.model.MediaPlayable;

/**
 * Cette classe encapsule une action qui sera déclenchée sur un {@link MediaPlayable}.
 */
public class MediaAction {

    /**
     * Les actions possibles dans l'ordre.
     */
    @JsonIgnore
    private final Action[] actionsPossibles={Action.NONE,Action.PLAY,Action.PAUSE,Action.STOP};

    /**
     * L'index de l'action courante.
     */
    private int currentActionIndex;

    private Action statusAction = Action.NONE;

    @JsonIgnore
    private MediaPlayable media;

    private long uniqueIdMedia;

    public MediaAction() {
        currentActionIndex = 0;
    }

    public MediaAction(MediaPlayable media) {
        this();
        this.media = media;
        uniqueIdMedia = ((AbstractMedia)media).getUniqueId();
    }

    public Action nextAction() {
        currentActionIndex++;
        if (currentActionIndex > 3) currentActionIndex = 0;
        setStatusAction(actionsPossibles[currentActionIndex]);
        return statusAction;
    }

    public void runAction() {
        switch (statusAction) {
            case STOP:
                this.media.stop();
                break;
            case PLAY:
                this.media.play();
                break;
            case PAUSE:
                this.media.pause();
                break;
            default:
                break;
        }
    }

    public Action getStatusAction() {
        return statusAction;
    }

    public void setStatusAction(Action statusAction) {
        this.statusAction = statusAction;
    }

    public MediaPlayable getMedia() {
        return media;
    }

    public void setMedia(MediaPlayable media) {
        this.media = media;
    }

    public int getCurrentActionIndex() {
        return currentActionIndex;
    }

    public void setCurrentActionIndex(int currentActionIndex) {
        this.currentActionIndex = currentActionIndex;
    }

    public long getUniqueIdMedia() {
        return uniqueIdMedia;
    }

    @Override
    public String toString() {
        return "MediaAction{" +
                ", currentActionIndex=" + currentActionIndex +
                ", statusAction=" + statusAction +
                ", media=" + media +
                ", uniqueIdMedia=" + uniqueIdMedia +
                '}';
    }
}
