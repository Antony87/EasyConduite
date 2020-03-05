/*
 * Copyright (C) 2017 Antony Fons
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package easyconduite.view;

import easyconduite.controllers.MainController;
import easyconduite.model.AbstractUIMedia;
import easyconduite.media.RemoteMedia;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * AudioMedia.
 *
 * @author A Fons
 * @since 1.0
 */
public class RemoteMediaUI extends AbstractUIMedia {

    static final Logger LOG = LogManager.getLogger(RemoteMediaUI.class);
    private final RemoteMedia remoteMedia;

    /**
     * Constructor du UI custom control for an AudioMedia.
     *
     * @param media a media wich be play.
     * @param controller the main controller which interact with {@link RemoteMediaUI}
     */
    public RemoteMediaUI(RemoteMedia media, MainController controller) {
        super(media,controller);
        LOG.info("Construct an AudioMedia {}", media);
        this.remoteMedia = media;

        remoteMedia.statusProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue!=null){
                LOG.trace("MediaStatus player {} is {}",this.remoteMedia.getName(),newValue);
                switch (newValue) {
                    case PAUSED:
                        playingClass.setValue(false);
                        break;
                    case PLAYING:
                        playingClass.setValue(true);
                        break;
                    case READY:
                        playingClass.setValue(false);
                        break;
                    case STOPPED:
                        playingClass.setValue(false);
                        break;
                    default:
                        break;
                }
            }
        });

        ///////////// current Time label
        //audioMedia.getPlayer().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> timeLabel.setText(formatTime(audioMedia.getDuration().subtract(newValue))));

        if(remoteMedia.getType().equals(RemoteMedia.Type.KODI)){
            super.typeRegion.getStyleClass().add("typeKodi");
        }

        contextHbox.getChildren().add(new VolumeSlider());
    }

    @Override
    public void playPause() {
        remoteMedia.play();
    }

    @Override
    public void stop() {
        remoteMedia.stop();
    }

    private class VolumeSlider extends Slider {

        protected VolumeSlider() {
            super(0, 1, 0.5);
            final DoubleProperty volumeProperty = VolumeSlider.this.valueProperty();
        }
    }
}
