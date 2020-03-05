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
import easyconduite.exception.EasyconduiteException;
import easyconduite.model.AbstractMedia;
import easyconduite.model.AbstractUIMedia;
import easyconduite.media.AudioMedia;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * AudioMedia.
 *
 * @author A Fons
 * @since 1.0
 */
public class AudioMediaUI extends AbstractUIMedia {

    static final Logger LOG = LogManager.getLogger(AudioMediaUI.class);
    private final AudioMedia audioMedia;

    /**
     * Constructor du UI custom control for an AudioMedia.
     *
     * @param media a media wich be play.
     * @param controller the main controller which interact with {@link AudioMediaUI}
     */
    public AudioMediaUI(AbstractMedia media, MainController controller) {
        super(media,controller);
        LOG.info("Construct an AudioMedia {}", media);
        this.audioMedia = (AudioMedia) media;

        if(audioMedia != null){
            try {
                LOG.trace("Initialisation du player");
                audioMedia.initPlayer();
            } catch (EasyconduiteException e) {
                //FIXME
                e.printStackTrace();
            }
            audioMedia.getPlayer().statusProperty().addListener((observableValue, oldValue, newValue) -> {
                if(newValue!=null){
                    LOG.trace("MediaStatus player {} is {}",this.audioMedia.getName(),newValue);
                    switch (newValue) {
                        case PAUSED:
                            playingClass.setValue(false);
                            break;
                        case PLAYING:
                            playingClass.setValue(true);
                            break;
                        case READY:
                            audioMedia.setDuration(audioMedia.getPlayer().getStopTime());
                            actualizeUI();
                            playingClass.setValue(false);
                            break;
                        case STOPPED:
                            playingClass.setValue(false);
                            timeLabel.setText(formatTime(audioMedia.getDuration()));
                            break;
                        default:
                            break;
                    }
                }
            });

        }

        ///////////// current Time label
        audioMedia.getPlayer().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> timeLabel.setText(formatTime(audioMedia.getDuration().subtract(newValue))));
        super.typeRegion.getStyleClass().add("typeAudio");
        contextHbox.getChildren().add(new VolumeSlider());
    }

    @Override
    public void playPause() {
        final Status status = audioMedia.getPlayer().getStatus();
        switch (status) {
            case PAUSED:
            case READY:
            case STOPPED:
                audioMedia.play();
                break;
            case PLAYING:
                audioMedia.pause();
                break;
            default:
                break;
        }
    }

    @Override
    public void stop() {
        audioMedia.getPlayer().stop();
    }

    private class VolumeSlider extends Slider {

        protected VolumeSlider() {
            super(0, 1, audioMedia.getVolume());
            final DoubleProperty volumeProperty = VolumeSlider.this.valueProperty();
            volumeProperty.bindBidirectional(audioMedia.getPlayer().volumeProperty());
            VolumeSlider.this.setOnMouseReleased((MouseEvent event) -> {
                if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                    audioMedia.setVolume(volumeProperty.getValue());
                    audioMedia.getPlayer().setVolume(volumeProperty.getValue());
                }
                event.consume();
            });
        }
    }
}
