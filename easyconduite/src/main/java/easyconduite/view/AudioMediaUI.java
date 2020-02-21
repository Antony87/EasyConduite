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
import easyconduite.model.EasyMedia;
import easyconduite.model.IEasyMediaUI;
import easyconduite.objects.media.AudioMedia;
import easyconduite.util.KeyCodeHelper;
import easyconduite.view.commons.MediaSelectedPseudoClass;
import easyconduite.view.commons.PlayingPseudoClass;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
public class AudioMediaUI extends VBox implements IEasyMediaUI {

    static final Logger LOG = LogManager.getLogger(AudioMediaUI.class);
    private final Label nameLabel = new Label();
    private final Label timeLabel = new Label();
    private final Label keycodeLabel = new Label();
    private final Region repeatRegion = new Region();
    private final AudioMedia audioMedia;
    private final BooleanProperty playingClass = new PlayingPseudoClass(this);
    private final BooleanProperty mediaSelectedClass = new MediaSelectedPseudoClass(this);

    /**
     * Constructor du UI custom control for an AudioMedia.
     *
     * @param media a media wich be play.
     * @param controller the main controller which interact with {@link AudioMediaUI}
     */
    public AudioMediaUI(EasyMedia media, MainController controller) {
        super();
        LOG.info("Construct an AudioMedia {}", media);
        this.audioMedia = (AudioMedia) media;


        ////////////////////////////////////////////////////////////////////////
        //                 Construction de l'UI
        ////////////////////////////////////////////////////////////////////////
        // attribution css for Track VBOX
        this.getStyleClass().add("audioMediaUi");
        // Name of the track, just top of the VBOX (this).
        nameLabel.setMouseTransparent(true);

        // Construction de la partie contextuelle
        //TODO extraire dans une classe et créer une classe abstraite MediaUI.
        final HBox contextHbox = new HBox();
        contextHbox.setId("contextHbox");
        final VBox infoVbox = new VBox();
        infoVbox.setId("infoVbox");
        final Region typeRegion = new Region();
        typeRegion.setId("typeRegion");
        typeRegion.getStyleClass().add("typeAudio");
        repeatRegion.setId("repeatRegion");
        if (audioMedia.getLoppable()) {
            repeatRegion.getStyleClass().add("repeat");
        }
        infoVbox.getChildren().addAll(typeRegion, repeatRegion);
        // partie contextuelle qui initialize un player spécifique au média.
        try {
            audioMedia.initPlayer();
            // Listenner sur la fin de l'initialisation du player.

            audioMedia.statusPropertyProperty().addListener((observableValue, oldValue, newValue) -> {
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
            });
        } catch (EasyconduiteException ex) {
            LOG.error("Error occurend during AudioVideo player construction", ex);
        }

        // Fin de la partie contextuelle


        timeLabel.setMouseTransparent(true);
        timeLabel.setId("timeLabel");
        keycodeLabel.setMouseTransparent(true);
        keycodeLabel.setId("keycodeLabel");

        final HBox playPauseHbox = new HBox();
        playPauseHbox.getStyleClass().add("commandHbox");

        final Region playRegion = new Region();
        final Region stopRegion = new Region();
        stopRegion.getStyleClass().add("stopbutton");
        playRegion.getStyleClass().add("playbutton");
        playPauseHbox.getChildren().addAll(stopRegion, playRegion);

        playPauseHbox.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                Region target = (Region) mouseEvent.getTarget();
                if (target.getStyleClass().contains("stopbutton")) {
                    audioMedia.getPlayer().stop();
                } else {
                    playPause();
                }
            }
            mouseEvent.consume();
        });

        // Gestion des mouse events pour la sélection d'un UI.
        this.setOnMouseClicked(eventFocus -> {
            if (eventFocus.getButton().equals(MouseButton.PRIMARY)) {
                this.requestFocus();
                if (this.isFocused()) {
                    controller.getMediaUIList().forEach(mediaUI -> mediaUI.setSelected(false));
                    this.setSelected(true);
                }
            }
            if (eventFocus.getClickCount() == 2) {
                controller.editTrack(this);

            }
            eventFocus.consume();
        });

        ///////////// current Time label
        audioMedia.getPlayer().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> timeLabel.setText(formatTime(audioMedia.getDuration().subtract(newValue))));

        contextHbox.getChildren().addAll(infoVbox, new VolumeSlider());
        this.getChildren().addAll(nameLabel, contextHbox, timeLabel, keycodeLabel, playPauseHbox);
    }

    /**
     * This is the time string formater for display human-readable the media duration.
     *
     * @param duration
     * @return the time duration in mm:ss:dc format (dc = 1/10 s)
     */
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

    @Override
    public boolean isSelected() {
        return mediaSelectedClass.getValue();
    }

    @Override
    public void setSelected(boolean selected) {
        mediaSelectedClass.setValue(selected);
    }

    @Override
    public void actualizeUI() {
        nameLabel.setText(audioMedia.getName());
        timeLabel.setText(formatTime(audioMedia.getDuration()));
        keycodeLabel.setText(KeyCodeHelper.toString(this.audioMedia.getKeycode()));
        repeatRegion.getStyleClass().remove("repeat");
        if (audioMedia.getLoppable()) {
            repeatRegion.getStyleClass().add("repeat");
        }
    }

    @Override
    public EasyMedia getEasyMedia() {
        return this.audioMedia;
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
