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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
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
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;
import org.controlsfx.dialog.Dialog;
/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * AudioMedia.
 *
 * @author A Fons
 */
public class AudioMediaUI extends VBox {

    private MediaPlayer player;

    private AudioMedia audioMedia;

    private IconButton buttonPlayPause;

    private final Label keyCodeLabel = new Label();

    private final ImageView repeatImageView = new ImageView();

    private final Label nameLabel = new Label();

    private ProgressIndicator progressTrack;

    private final EasyconduiteController easyConduiteController;

    private final static String NAME_ICON_PLAY = "/icons/PlayGreenButton.png";

    private final static String NAME_ICON_PAUSE = "/icons/PauseBlueButton.png";

    private static final Logger logger = Logger.getLogger(AudioMediaUI.class.getName());

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

        logger.log(Level.INFO, "Create AudioMediaUI with {0}", audioMedia);

        // initialize nameLabel
        if (this.audioMedia.getName() != null) {
            //name.set(this.audioMedia.getName());
            nameLabel.setText(this.audioMedia.getName());
        }

        // initialize keyCodeLabel
        keyCodeLabel.setText(KeyCodeUtil.toString(this.audioMedia.getLinkedKeyCode()));

        easyConduiteController = controller;

        Media media = new Media(audioMedia.getAudioFile().toURI().toString());
        player = new MediaPlayer(media);

        // Manage volume property.
        player.volumeProperty().setValue(this.audioMedia.getVolume());

        player.setOnEndOfMedia(() -> {
            player.setStartTime(Duration.ZERO);
            // if repeat is false, force to stop de player.
            if (!this.audioMedia.getRepeat()) {
                player.stop();
                buttonPlayPause.setPathNameOfIcon(NAME_ICON_PLAY);
            }

            logger.log(Level.INFO, "End of media player with status {0}", player.getStatus());
        });

        player.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            progressTrack.setProgress(newValue.toSeconds() / player.getTotalDuration().toSeconds());
        });

        // Draw the GUI for this media.
        drawUI();

        updateRepeat(this.audioMedia.getRepeat());

        player.volumeProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

            if (newValue != oldValue) {
                audioMedia.setVolume(newValue.doubleValue());
                logger.log(Level.FINE, "Change volume AudioMedia with {0}", audioMedia.getVolume());
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

    public void updateRepeat(Boolean repeatValue) {
        audioMedia.setRepeat(repeatValue);
        logger.log(Level.INFO, "Change repeat AudioMedia with {0}", audioMedia.getRepeat());
        if (repeatValue) {
            player.setCycleCount(MediaPlayer.INDEFINITE);
            repeatImageView.setImage(new Image(getClass().getResourceAsStream("/icons/repeat.png"), 18, 18, true, false));
            logger.log(Level.INFO, "CycleCount {0}", player.cycleCountProperty().getValue());
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

            Action reponse = Dialogs.create()
                    .title("Suppression")
                    .message("Desirez-vous réellement supprimer cette piste ?")
                    .graphic(new ImageView("/icons/HelpBlueButton.png"))
                    .showConfirm();
            if (reponse == Dialog.ACTION_OK) {
                easyConduiteController.removeAudioMediaUI(this);
            }

        });

        // create button wich link a key to an AudioMedia
        IconButton buttonAssocKey = new IconButton("/icons/Gear.png");

        buttonAssocKey.setOnMouseClicked((MouseEvent event) -> {
            try {
                LinkKeyBoardDialog dialog = new LinkKeyBoardDialog(this, easyConduiteController);
            } catch (IOException ex) {
                Logger.getLogger(AudioMediaUI.class.getName()).log(Level.SEVERE, null, ex);
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
            buttonPlayPause.setPathNameOfIcon(NAME_ICON_PLAY);
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

        Status status = getPlayer().getStatus();

        switch (status) {
            case PAUSED:
                getPlayer().play();
                buttonPlayPause.setPathNameOfIcon(NAME_ICON_PAUSE);
                break;
            case PLAYING:
                getPlayer().pause();
                buttonPlayPause.setPathNameOfIcon(NAME_ICON_PLAY);
                break;
            case READY:
                getPlayer().seek(Duration.ZERO);
                buttonPlayPause.setPathNameOfIcon(NAME_ICON_PAUSE);
                getPlayer().play();
                break;
            case STOPPED:
                getPlayer().play();
                buttonPlayPause.setPathNameOfIcon(NAME_ICON_PAUSE);
                break;
            case UNKNOWN:
                getPlayer().seek(Duration.ZERO);
                getPlayer().play();
                buttonPlayPause.setPathNameOfIcon(NAME_ICON_PAUSE);
                break;
            default:
                break;
        }

    }

    public AudioMedia getAudioMedia() {
        return audioMedia;
    }

    public void setAudioMedia(AudioMedia audioMedia) {
        this.audioMedia = audioMedia;
    }

    /**
     * Get the {@link MediaPlayer} assigned to this UI Control.
     *
     * @return
     */
    public MediaPlayer getPlayer() {
        return player;
    }

    private void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    private HBox hBoxForTrack() {

        HBox hbox = new HBox(10);
        hbox.setPrefWidth(100);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

}
