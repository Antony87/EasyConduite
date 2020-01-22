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

import easyconduite.exception.EasyconduiteException;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.util.KeyCodeHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * AudioMedia.
 *
 * @author A Fons
 */
public class AudioMediaUI extends VBox {

    static final Logger LOG = LogManager.getLogger(AudioMediaUI.class);

    private final Label nameLabel = new Label();

    private final Label timeLabel = new Label();

    private final Label keycodeLabel = new Label();

    private final Region repeatRegion = new Region();

    private final PlayPauseHbox playPauseHbox;

    private final AudioVideoMedia audioMedia;

    private static final PseudoClass PLAYING_PSEUDO_CLASS = PseudoClass.getPseudoClass("playing");

    private final BooleanProperty playingClass = new BooleanPropertyBase() {

        @Override
        public void invalidated() {
            pseudoClassStateChanged(PLAYING_PSEUDO_CLASS, get());
        }

        @Override
        public Object getBean() {
            return AudioMediaUI.this;
        }

        @Override
        public String getName() {
            return "playing";
        }
    };

    //private ChainingUpdater nextChain;

    /**
     * Constructor du UI custom control for an AudioMedia.<br>
     * Not draw the control but construct object and assign a
     * {@link MediaPlayer}.<br>
     *
     * @param media
     */
    public AudioMediaUI(AudioVideoMedia media) {
        super();
        LOG.info("Construct an AudioMedia {}", media);

        this.audioMedia = media;
        ////////////////////////////////////////////////////////////////////////
        //               Initialize MediaPlayer
        ////////////////////////////////////////////////////////////////////////
        try {
            audioMedia.initPlayer();
            // Listenner sur la fin de l'initialisation du player.
            audioMedia.getPlayer().statusProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Status> observableValue, Status oldValue, Status newValue) {
                    switch (newValue) {
                        case PAUSED:
                            playPauseHbox.toFront(playPauseHbox.playRegion);
                            setPlayingClass(false);
                            break;
                        case PLAYING:
                            playPauseHbox.toFront(playPauseHbox.pauseRegion);
                            setPlayingClass(true);
                            break;
                        case READY:
                            audioMedia.setDuration(audioMedia.getPlayer().getStopTime());
                            updateAudioMedia();
                            playPauseHbox.toFront(playPauseHbox.playRegion);
                            setPlayingClass(false);
                            break;
                        case STOPPED:
                            playPauseHbox.toFront(playPauseHbox.playRegion);
                            setPlayingClass(false);
                            timeLabel.setText(formatTime(audioMedia.getDuration()));
                            break;
                        default:
                            break;
                    }
                }
            });
        } catch (EasyconduiteException ex) {
            LOG.error("Error occurend during AudioVideo player construction", ex);
        }

        ////////////////////////////////////////////////////////////////////////
        //                 Construction de l'UI
        ////////////////////////////////////////////////////////////////////////
        // attribution css for Track VBOX
        this.getStyleClass().add("audioMediaUi");

        this.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                this.requestFocus();
            }
        });
        //this.updateFromAudioMedia(audioMedia);

        ////////////////////////////////////////////////////////////////////////
        ///////////// current Time label               
        audioMedia.getPlayer().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> timeLabel.setText(formatTime(audioMedia.getDuration().subtract(newValue))));

        ////////////////////////////////////////////////////////////////////////
        // initialize KeyCodeLabel
        HBox keycodeHbox = new HBox();
        keycodeHbox.getStyleClass().add("baseHbox");

        repeatRegion.setId("repeatRegion");
        if (audioMedia.getLoppable()) {
            repeatRegion.getStyleClass().add("repeat");
        }
        keycodeHbox.getChildren().addAll(repeatRegion, keycodeLabel);

        playPauseHbox = new PlayPauseHbox();

        timeLabel.setMouseTransparent(true);
        keycodeLabel.setMouseTransparent(true);
        keycodeHbox.setMouseTransparent(true);
        nameLabel.setMouseTransparent(true);


        this.getChildren().addAll(nameLabel, new VolumeSlider(), timeLabel, keycodeHbox, playPauseHbox);
    }

    //FIXME
    @Deprecated
    public final AudioMedia getAudioMedia() {
        return new AudioMedia(null);
    }


    public void updateAudioMedia() {

        nameLabel.setText(audioMedia.getName());
        timeLabel.setText(formatTime(audioMedia.getDuration()));
        keycodeLabel.setText(KeyCodeHelper.toString(this.audioMedia.getKeycode()));
        repeatRegion.getStyleClass().remove("repeat");
        if (audioMedia.getLoppable()) {
            repeatRegion.getStyleClass().add("repeat");
        }

    }

    private String formatTime(Duration duration) {

        if (duration.greaterThan(Duration.ZERO)) {
            final double millis = duration.toMillis();
            final int dec = (int) ((millis / 100) % 10);
            final int seconds = (int) ((millis / 1000) % 60);
            final int minutes = (int) (millis / (1000 * 60));
            return String.format("%02d:%02d:%02d", minutes, seconds, dec);
        }
        return null;
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
            });
        }
    }

    private void setPlayingClass(boolean playing) {
        this.playingClass.set(playing);
    }

    public final void playPause() {
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

    private static class PlayRegion extends Region {
        protected PlayRegion() {
            PlayRegion.this.getStyleClass().add("playbutton");
        }
    }

    private static class PauseRegion extends Region {
        protected PauseRegion() {
            PauseRegion.this.getStyleClass().add("pausebutton");
        }
    }

    private static class StopRegion extends Region {
        protected StopRegion() {
            StopRegion.this.getStyleClass().add("stopbutton");
        }
    }

    private class PlayPauseHbox extends HBox {

        final ObservableList<Node> childs;
        final Region playRegion = new PlayRegion();
        final Region pauseRegion = new PauseRegion();
        final Region stopRegion = new StopRegion();

        protected PlayPauseHbox() {
            super();
            PlayPauseHbox.this.getStyleClass().add("commandHbox");
            StackPane playPausePane = new StackPane();
            playPausePane.setAlignment(Pos.CENTER);

            playPausePane.getChildren().addAll(pauseRegion, playRegion);
            PlayPauseHbox.this.getChildren().addAll(stopRegion, playPausePane);

            childs = playPausePane.getChildren();

            this.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    Region target = (Region) mouseEvent.getTarget();
                    if (target instanceof StopRegion) {
                        audioMedia.getPlayer().stop();
                    } else {
                        playPause();
                    }
                }
                mouseEvent.consume();

            });
        }

        protected void toFront(Region region) {
            int index = childs.indexOf(region);
            childs.get(index).toFront();
        }
    }
}
