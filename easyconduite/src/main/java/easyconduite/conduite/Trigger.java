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
import easyconduite.model.MediaPlayable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.HashSet;
import java.util.Optional;

/**
 * this class implements track trigger features.
 */
public class Trigger {

    private final HashSet<MediaAction> mediaActions;

    @JsonIgnore
    private final BooleanProperty actif = new SimpleBooleanProperty(false);

    public Trigger() {
        mediaActions = new HashSet<>();
    }

    public void runActions() {
        mediaActions.forEach((mediaAction) -> {
            mediaAction.runStatut();
            setActif(true);
        });
    }

    public MediaAction createMediaAction(MediaPlayable media){
        final MediaAction action = new MediaAction(media);
        mediaActions.add(action);
        return action;
    }

    public MediaAction findActionFromMedia(MediaPlayable media){
        final Optional<MediaAction> mediaOptionnal = mediaActions.stream().
                filter(mediaAction -> mediaAction.getMedia().equals(media))
                .findFirst();
        return mediaOptionnal.orElse(null);
    }

    @Override
    public String toString() {
        return "Trigger{" +
                "mediaActions=" + mediaActions +
                ", actif=" + actif +
                '}';
    }

    public HashSet<MediaAction> getMediaActions() {
        return mediaActions;
    }

    public boolean isActif() {
        return actif.get();
    }

    public BooleanProperty actifProperty() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif.set(actif);
    }
}
