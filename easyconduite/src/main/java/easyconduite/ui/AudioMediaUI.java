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
package easyconduite.ui;

import easyconduite.controllers.EasyconduiteController;
import easyconduite.model.EasyAudioChain;
import easyconduite.objects.AudioMedia;
import easyconduite.exception.EasyconduiteException;
import easyconduite.ui.commons.ActionDialog;
import easyconduite.ui.commons.IconButton;
import easyconduite.util.Constants;
import easyconduite.util.KeyCodeUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
public class AudioMediaUI extends VBox implements EasyAudioChain {

    static final Logger LOG = LogManager.getLogger(AudioMediaUI.class);

    private final SimpleDateFormat PROGRESS_TIME_FORMAT = new SimpleDateFormat("mm:ss:SSS");

    private final ObjectProperty<KeyCode> keycode = new ReadOnlyObjectWrapper<>();

    private final Label nameLabel = new Label();

    private final Label timeLabel = new Label();

    private final PlayPauseHbox playPauseHbox;

    private final Label keycodeLabel = new Label();
    
    private final Slider sliderVolume = new Slider(0, 1, 0.5d);

    private EasyconduitePlayer player;

    private AudioMedia audioMedia;

    private final ImageView repeatImageView = new ImageView();

    private EasyAudioChain nextChain;

    /**
     * Constructor du UI custom control for an AudioMedia.<br>
     * Not draw the control but construct object and assign a
     * {@link MediaPlayer}.<br>
     *
     * @param media
     * @param controller
     */
    public AudioMediaUI(AudioMedia media, EasyconduiteController controller) {
        super();
        LOG.info("Construct an AudioMedia {}", media);

        PROGRESS_TIME_FORMAT.setTimeZone(Constants.TZ);

        this.audioMedia = media;
        ////////////////////////////////////////////////////////////////////////
        //               Initialize MediaPlayer
        ////////////////////////////////////////////////////////////////////////
        player = null;
        try {
            player = EasyconduitePlayer.create(media);
            // Listenning player Status property
            player.getPlayer().statusProperty().addListener(getPlayerStatusListener());
        } catch (EasyconduiteException ex) {
            LOG.error("Error occurend during EasyPlayer construction", ex);
        }
        // Positionne la chaine de responsabilité
        this.nextChain = player;

        ////////////////////////////////////////////////////////////////////////
        //                 Construction de l'UI
        ////////////////////////////////////////////////////////////////////////
        // attribution css for Track VBOX
        this.getStyleClass().add("track-vbox");
        // Label for name of the track /////////////////////////////////////////
        nameLabel.getStyleClass().add("texte-track");
        // Construction du TopHbox qui offren boutons delete et configure
        TopHbox topHbox = new TopHbox(controller);

        ////////////////////////////////////////////////////////////////////////
        // Slider for volume control
        sliderVolume.setValue(audioMedia.getVolume());
        sliderVolume.getStyleClass().add("slider-volume-track");
        sliderVolume.valueProperty().bindBidirectional(player.getPlayer().volumeProperty());
        sliderVolume.setOnMouseReleased((MouseEvent event) -> {
            if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                audioMedia.setVolume(sliderVolume.getValue());
                // propagation au EasyconduitePlayer.
                player.updateFromAudioMedia(media);
            }
        });
        ////////////////////////////////////////////////////////////////////////
        ///////////// current Time label               
        timeLabel.getStyleClass().add("texte-time");
        player.getPlayer().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            timeLabel.setText(Constants.getFormatedDuration(audioMedia.getAudioDuration().subtract(newValue), PROGRESS_TIME_FORMAT));
        });
        // ToolBar with Stop and play button ///////////////////////////////////
        playPauseHbox = new PlayPauseHbox(player);
        // icone repeat for repeat /////////////////////////////////////////////
        repeatImageView.setFitHeight(18);
        ////////////////////////////////////////////////////////////////////////
        // initialize KeyCodeLabel
        keycodeLabel.getStyleClass().add("labelkey-track");
        // All childs are built. Call updateFromAudio to set there.
        this.updateFromAudioMedia(audioMedia);
        this.getChildren().addAll(nameLabel, topHbox, sliderVolume, timeLabel, playPauseHbox, repeatImageView, keycodeLabel);
    }

    public final AudioMedia getAudioMedia() {
        return audioMedia;
    }

    public final EasyconduitePlayer getEasyPlayer() {
        return player;
    }

    public ObjectProperty<KeyCode> keycodeProperty() {
        return keycode;
    }

    public KeyCode getKeycode() {
        return keycode.get();
    }

    @Override
    public void setNext(EasyAudioChain next) {
        nextChain = next;
    }

    @Override
    public void updateFromAudioMedia(AudioMedia media) {
        if (audioMedia.equals(media)) {
            // positionne tous les champs quand AudioMedi a changé et que la 
            // "chain of responsability" est déclenchée.
            nameLabel.setText(audioMedia.getName());
            timeLabel.setText(Constants.getFormatedDuration(audioMedia.getAudioDuration(), PROGRESS_TIME_FORMAT));
            if (audioMedia.getRepeatable()) {
                repeatImageView.setImage(Constants.REPEAT_IMAGE);
            } else {
                repeatImageView.setImage(null);
            }
            keycodeLabel.setText(KeyCodeUtil.toString(this.audioMedia.getKeycode()));
            keycode.setValue(this.audioMedia.getKeycode());
            // on passe la responsabilité au next (EasyconduitePlayer).
            nextChain.updateFromAudioMedia(this.audioMedia);

        } else {
            ActionDialog.showWarning("Incohérence des objets", "Les objets AudioMedia ne sont pas egaux");
        }
    }

    @Override
    public void removeChilds(AudioMedia audioMedia) {
        nextChain.removeChilds(audioMedia);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Listener
    ////////////////////////////////////////////////////////////////////////////
    private ChangeListener<Status> getPlayerStatusListener() {
        return (ObservableValue<? extends Status> observable, Status oldValue, Status newValue) -> {
            switch (newValue) {
                case PAUSED:
                    playPauseHbox.decoratePlaying(false);
                    AudioMediaUI.this.setEffect(Constants.SHADOW_EFFECT);
                    keycodeLabel.setEffect(null);
                    AudioMediaUI.this.setBackground(Constants.PAUSE_BACKG);
                    break;
                case PLAYING:
                    playPauseHbox.decoratePlaying(true);
                    AudioMediaUI.this.setEffect(Constants.SHADOW_EFFECT);
                    keycodeLabel.setEffect(Constants.KEYCODE_LABEL_BLOOM);
                    AudioMediaUI.this.setBackground(Constants.PLAY_BACKG);
                    break;
                case STOPPED:
                    playPauseHbox.decoratePlaying(false);
                    AudioMediaUI.this.setEffect(null);
                    keycodeLabel.setEffect(null);
                    AudioMediaUI.this.setBackground(Constants.STOP_BACKG);
                    break;
                case READY:
                    // if player ready, update AudioMedia duration an UI.
                    audioMedia.setAudioDuration(player.getPlayer().getStopTime());
                    updateFromAudioMedia(AudioMediaUI.this.audioMedia);
                    break;
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////
    //                    inner class for Ui components
    ////////////////////////////////////////////////////////////////////////////
    private class TopHbox extends HBox {

        protected TopHbox(EasyconduiteController controller) {
            super();
            TopHbox.this.getStyleClass().add("track-inner-hbox");
            final IconButton button_delete = new IconButton("/icons/MinusRedButton.png");
            button_delete.setOnMouseClicked((MouseEvent event) -> {
                Optional<ButtonType> result = ActionDialog.showConfirmation("Vous allez supprimer cette piste", "Voulez-vous continuer ?");
                if (result.get() == ButtonType.OK) {
                    controller.removeAudioMedia(audioMedia);
                }
            });
            final IconButton button_config = new IconButton("/icons/Gear.png");
            button_config.setOnMouseClicked((MouseEvent event) -> {
                try {
                    final TrackConfigDialog trackConfigDialog = new TrackConfigDialog(audioMedia, controller);
                    trackConfigDialog.show();
                } catch (IOException ex) {
                    LOG.error("Error occurend during TrackConfigDialog construction", ex);
                }
            });
            TopHbox.this.getChildren().addAll(button_delete, button_config);
        }
    }

    private class PlayPauseHbox extends HBox {

        private final IconButton button_stop = new IconButton("/icons/StopRedButton.png");

        private final IconButton buttonPlayPause;

        protected PlayPauseHbox(EasyconduitePlayer player) {
            super();
            PlayPauseHbox.this.getStyleClass().add("track-inner-hbox");
            buttonPlayPause = new IconButton(Constants.NAME_ICON_PLAY);
            buttonPlayPause.setOnMouseClicked((MouseEvent event) -> {
                player.playPause();
            });
            button_stop.setOnMouseClicked((MouseEvent event) -> {
                player.stop();
            });
            PlayPauseHbox.this.getChildren().addAll(button_stop, buttonPlayPause);
        }

        protected void decoratePlaying(boolean decorate) {
            if (decorate) {
                buttonPlayPause.setPathNameOfIcon(Constants.NAME_ICON_PAUSE);
            } else {
                buttonPlayPause.setPathNameOfIcon(Constants.NAME_ICON_PLAY);
            }
        }
    }
}
