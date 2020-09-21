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
import easyconduite.media.MediaStatus;
import easyconduite.model.AbstractMedia;
import easyconduite.model.MediaPlayable;

public class MediaAction {

    private Long uniqueId;

    @JsonIgnore
    private final MediaPlayable media;

    @JsonIgnore
    private final MediaStatus[] statusPossibles = {MediaStatus.UNKNOWN, MediaStatus.PLAYING, MediaStatus.PAUSED, MediaStatus.STOPPED};

    private int indexOfStatus;

    private MediaStatus statusAction = MediaStatus.UNKNOWN;

    public MediaAction(MediaPlayable media) {
        this.media = media;
        uniqueId = ((AbstractMedia)this.media).getUniqueId();
        indexOfStatus = 0;
    }

    public MediaStatus nextStatus() {
        indexOfStatus++;
        if (indexOfStatus > 3) indexOfStatus = 0;
        setStatusAction(statusPossibles[indexOfStatus]);
        return statusAction;
    }

    public void runStatut() {
        switch (statusAction) {
            case STOPPED:
                media.stop();
                break;
            case PLAYING:
                media.play();
                break;
            case PAUSED:
                media.pause();
                break;
            default:
                break;
        }
    }

    public MediaPlayable getMedia() {
        return media;
    }

    public MediaStatus getStatusAction() {
        return statusAction;
    }

    public void setStatusAction(MediaStatus statusAction) {
        this.statusAction = statusAction;
    }

}
