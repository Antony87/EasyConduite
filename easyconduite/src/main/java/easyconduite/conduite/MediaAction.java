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

public class MediaAction {

    private final AbstractMedia media;

    private MediaStatus status;

    public MediaAction(AbstractMedia media, MediaStatus status) {
        this.media = media;
        this.status = status;
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
}
