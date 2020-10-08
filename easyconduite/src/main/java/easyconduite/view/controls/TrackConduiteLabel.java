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

package easyconduite.view.controls;

import easyconduite.media.AudioMedia;
import easyconduite.media.RemoteMedia;
import easyconduite.model.AbstractMedia;
import easyconduite.model.MediaPlayable;
import easyconduite.view.commons.PlayingPseudoClass;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackConduiteLabel extends Label {

    private static final Logger LOG = LogManager.getLogger(TrackConduiteLabel.class);
    final BooleanProperty playingClass;
    private final MediaPlayable mediaPlayable;


    public TrackConduiteLabel(MediaPlayable media) {
        mediaPlayable = media;
        this.textProperty().bind(((AbstractMedia) mediaPlayable).nameProperty());
        this.getStyleClass().add("labelConduite");
        playingClass = new PlayingPseudoClass(this);
        playingClassProperty().setValue(false);

        ReadOnlyObjectProperty<MediaPlayer.Status> statusProperty = null;
        if (media instanceof AudioMedia) {
            final AudioMedia audioMedia = (AudioMedia) media;
            statusProperty = audioMedia.getPlayer().statusProperty();
            addPlayingLookListener(statusProperty);
        } else if (media instanceof RemoteMedia) {
            final RemoteMedia remoteMedia = (RemoteMedia) media;
            statusProperty = remoteMedia.statusProperty();
            addPlayingLookListener(statusProperty);
        }
    }

    private void addPlayingLookListener(ReadOnlyObjectProperty<MediaPlayer.Status> property) {
        property.addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(MediaPlayer.Status.PLAYING)) {
                this.setPlayingClass(true);
            } else {
                this.setPlayingClass(false);
            }
        });
    }

    public void setPlayingClass(boolean playingClass) {
        this.playingClass.set(playingClass);
    }

    public BooleanProperty playingClassProperty() {
        return playingClass;
    }
}
