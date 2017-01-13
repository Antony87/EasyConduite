/*
 * Copyright (C) 2014 A Fons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package easyconduite.ui;

import easyconduite.util.KeyCodeUtil;
import easyconduite.controllers.EasyconduiteController;
import easyconduite.objects.AudioMedia;
import easyconduite.util.Config;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * AudioMedia.
 *
 * @author A Fons
 */
public class AudioMediaUI extends VBox {
    
    private static final String NAME_ICON_PLAY = "/icons/PlayGreenButton.png";
    private static final String NAME_ICON_PAUSE = "/icons/PauseBlueButton.png";
    private static final String CLASSNAME = AudioMediaUI.class.getName();
    private static final Logger LOGGER = Config.getCustomLogger(CLASSNAME);

    private MediaPlayer player;

    private AudioMedia audioMedia;

    private IconButton buttonPlayPause;

    private final Label keyCodeLabel = new Label();

    private final ImageView repeatImageView = new ImageView();

    private final Label nameLabel = new Label();

    private ProgressIndicator progressTrack;

    private final EasyconduiteController easyConduiteController;


    /**
     * Constructor du UI custom control for an AudioMedia.<br>
     * Not draw the control but construct object and assign a
     * {@link MediaPlayer}.<br>
     *
     * @param unAudioMedia
     * @param controller
     */
    public AudioMediaUI(final AudioMedia unAudioMedia, final EasyconduiteController controller) {

        super(10);
        // initialize audioMedia
        this.audioMedia = unAudioMedia;
        LOGGER.log(Level.INFO, "Create AudioMediaUI with {0}", audioMedia);

        // initialize nameLabel
        if (this.audioMedia.getName() != null) {
            //name.set(this.audioMedia.getName());
            nameLabel.setText(this.audioMedia.getName());
        }

        // initialize values
        keyCodeLabel.setText(KeyCodeUtil.toString(this.audioMedia.getLinkedKeyCode()));
        
        // initialize controller
        easyConduiteController = controller;

        ////////////////////////////////////////////////////////////////////////
        //               Initialize MediaPlayer
        ////////////////////////////////////////////////////////////////////////
        Media media = new Media(audioMedia.getAudioFile().toURI().toString());

        player = new MediaPlayer(media);

        player.setOnEndOfMedia(() -> {
            // if repeat is false, force to stop de player.
            if (!this.audioMedia.getRepeat()) {
                player.stop();
                buttonPlayPause.setPathNameOfIcon(NAME_ICON_PLAY);
            }
            LOGGER.log(Level.FINE, "End of media player with status {0}", player.getStatus());
        });

        player.setOnError(() -> {
            final Alert alert = ActionDialog.createActionDialog("Une erreur de média est survenue.");
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText(player.getError().getMessage());
            LOGGER.log(Level.SEVERE, "Media error", player.getError());
            alert.showAndWait();
            alert.close();
        });

        player.setOnPaused(() -> {
            buttonPlayPause.setPathNameOfIcon(NAME_ICON_PLAY);
            this.setBackground(Config.STOP_BACKG);
        });
        
        player.setOnPlaying(() -> {
            buttonPlayPause.setPathNameOfIcon(NAME_ICON_PAUSE);
            this.setBackground(Config.PLAY_BACKG);
        });

        player.setOnStopped(() -> {
            buttonPlayPause.setPathNameOfIcon(NAME_ICON_PLAY);
            this.setBackground(Config.STOP_BACKG);
        });

        player.setOnReady(() -> {
            player.seek(player.getStartTime());
            buttonPlayPause.setPathNameOfIcon(NAME_ICON_PLAY);
        });
        ////////////////////////////////////////////////////////////////////////
                
        player.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            progressTrack.setProgress(newValue.toSeconds() / player.getTotalDuration().toSeconds());
        });

        // Draw the GUI for this media.
        drawUI();
        
        // As Player loaded, be able to delete setOnReady event.
        player.setOnReady(null);

        updateRepeat(this.audioMedia.getRepeat());

        // Manage volume property.
        player.volumeProperty().setValue(this.audioMedia.getVolume());
        
        player.volumeProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue != oldValue) {
                audioMedia.setVolume(newValue.doubleValue());
                LOGGER.log(Level.FINE, "Change volume AudioMedia with {0}", audioMedia.getVolume());
            }
        });
    }

    public void updateAffectedKeyCode(KeyCode code) {
        if (code != null) {
            keyCodeLabel.setText(KeyCodeUtil.toString(code));
            audioMedia.setLinkedKeyCode(code);
            easyConduiteController.updateKeycodesAudioMap();
        }
    }

    public final void updateRepeat(Boolean repeatValue) {
        audioMedia.setRepeat(repeatValue);
        LOGGER.log(Level.INFO, "Change repeat AudioMedia with {0}", audioMedia.getRepeat());
        if (repeatValue) {
            player.setCycleCount(MediaPlayer.INDEFINITE);
            repeatImageView.setImage(new Image(getClass().getResourceAsStream("/icons/repeat.png"), 18, 18, true, false));
            LOGGER.log(Level.INFO, "CycleCount {0}", player.cycleCountProperty().getValue());
        } else {
            repeatImageView.setImage(null);
            player.setCycleCount(1);
        }
    }

    public void updateName(String name) {
        if (null != name) {
            audioMedia.setName(name);
            nameLabel.setText(name);
        }
    }

    /**
     * Add the custom control UI for an {@link AudioMediaUI} to a scene.
     * affectedKeyCode.bind(affectedKeyCode)affectedKeyCode);
     */
    public final void drawUI() {

        // attribution css for Track VBOX
        this.getStyleClass().add("track-vbox");

        // Label for name of the track /////////////////////////////////////////
        nameLabel.getStyleClass().add("texte-track");
        ////////////////////////////////////////////////////////////////////////

        // ToolBar with delete and confugure button ////////////////////////////
        HBox topHbox = hBoxForTrack();
        IconButton buttonDelete = new IconButton("/icons/MinusRedButton.png");

        buttonDelete.setOnMouseClicked((MouseEvent event) -> {

            Alert alert = ActionDialog.createActionDialog("Vous allez supprimer cette piste.");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                easyConduiteController.removeAudioMediaUI(this);
            }

        });

        // create button wich link a key to an AudioMedia
        IconButton buttonAssocKey = new IconButton("/icons/Gear.png");

        buttonAssocKey.setOnMouseClicked((MouseEvent event) -> {
            try {
                LinkKeyBoardDialog dialog = new LinkKeyBoardDialog(this, easyConduiteController);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Erreur chargement easyConduiteController");
            }
        });

        topHbox.getChildren().addAll(buttonDelete, buttonAssocKey);
        ////////////////////////////////////////////////////////////////////////

        // Slider for volume control
        Slider curseVolume = new Slider(0, 1, this.audioMedia.getVolume());
        curseVolume.getStyleClass().add("slider-volume-track");
        player.volumeProperty().bindBidirectional(curseVolume.valueProperty());

        // Progressbar /////////////////////////////////////////////////////////
        progressTrack = new ProgressBar(0);
        progressTrack.getStyleClass().add("progress-bar-track");
        ////////////////////////////////////////////////////////////////////////

        // ToolBar with Stop and play button ///////////////////////////////////
        HBox bottomHbox = hBoxForTrack();
        buttonPlayPause = new IconButton(NAME_ICON_PLAY);
        buttonPlayPause.setOnMouseClicked((MouseEvent event) -> {
            playPause();
        });

        IconButton buttonStop = new IconButton("/icons/StopRedButton.png");
        buttonStop.setOnMouseClicked((MouseEvent event) -> {
            player.stop();
            //buttonPlayPause.setPathNameOfIcon(NAME_ICON_PLAY);
        });

        bottomHbox.getChildren().addAll(buttonStop, buttonPlayPause);
        ////////////////////////////////////////////////////////////////////////

        // icone repeat for repeat /////////////////////////////////////////////
        repeatImageView.setFitHeight(18);
        repeatImageView.setFitWidth(18);
        ////////////////////////////////////////////////////////////////////////

        keyCodeLabel.getStyleClass().add("labelkey-track");

        this.getChildren().addAll(nameLabel, topHbox, curseVolume, progressTrack, bottomHbox, repeatImageView, keyCodeLabel);
    }

    public void playPause() {

        Status status = player.getStatus();
        if (status == null) {
            LOGGER.warning("Play/pause with null status !");
        } else {
            LOGGER.log(Level.FINE, "Play/pause action with {0}", status);
            switch (status) {
                case PAUSED:
                    LOGGER.log(Level.FINE, "Call Player play method");
                    player.play();
                    break;
                case PLAYING:
                    LOGGER.log(Level.FINE, "Call Player pause method");
                    player.pause();
                    break;
                case READY:
                    LOGGER.log(Level.FINE, "Call Player play method");
                    player.play();
                    break;
                case STOPPED:
                    LOGGER.log(Level.FINE, "Call Player play method");
                    player.play();
                    break;
                case UNKNOWN:
                    player.seek(Duration.ZERO);
                    player.play();
                    buttonPlayPause.setPathNameOfIcon(NAME_ICON_PAUSE);
                    break;
                default:
                    break;
            }
        }
    }

    public AudioMedia getAudioMedia() {
        return audioMedia;
    }

    public void setAudioMedia(AudioMedia audioMedia) {
        this.audioMedia = audioMedia;
    }

    private HBox hBoxForTrack() {

        HBox hbox = new HBox(10);
        hbox.setPrefWidth(100);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

}
