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

import com.jfoenix.controls.JFXSlider;
import easyconduite.controllers.MainController;
import easyconduite.exception.EasyconduiteException;
import easyconduite.media.AudioMedia;
import easyconduite.model.AbstractMedia;
import easyconduite.model.AbstractUIMedia;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
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
     * @param media      a media wich be play.
     * @param controller the main controller which interact with {@link AudioMediaUI}
     */
    public AudioMediaUI(AbstractMedia media, MainController controller) {
        super(media, controller);
        LOG.info("Construct an AudioMedia {}", media);
        this.audioMedia = (AudioMedia) media;

        if (audioMedia != null) {
            try {
                LOG.trace("Initialisation du player");
                audioMedia.initPlayer();
            } catch (EasyconduiteException e) {
                //FIXME
                e.printStackTrace();
            }
            audioMedia.getPlayer().statusProperty().addListener((observableValue, oldValue, newValue) -> {
                if (newValue != null) {
                    LOG.trace("MediaStatus player {} is {}", this.audioMedia.getName(), newValue);
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
        contextHbox.getChildren().add(new JFXVolumeSlider());
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

    private class JFXVolumeSlider extends JFXSlider {
        private boolean changing;

        protected JFXVolumeSlider() {
            super(0, 100, audioMedia.getVolume() * 100);
            final DoubleProperty volumeProperty = JFXVolumeSlider.this.valueProperty();
            final DoubleProperty volumePlayer = audioMedia.getPlayer().volumeProperty();

            volumePlayer.addListener((observableValue, oldvalue, newvalue) -> {
                if (!changing) {
                    try {
                        changing = true;
                        volumeProperty.set(newvalue.doubleValue()*100);
                    } finally {
                        changing = false;
                    }
                }
            });

            volumeProperty.addListener((observableValue, oldvalue, newvalue) -> {
                if (!changing) {
                    try {
                        changing = true;
                        volumePlayer.set(newvalue.doubleValue()/100);
                        audioMedia.setVolume(volumeProperty.getValue() / 100);
                    } finally {
                        changing = false;
                    }
                }
            });
        }
    }
}
