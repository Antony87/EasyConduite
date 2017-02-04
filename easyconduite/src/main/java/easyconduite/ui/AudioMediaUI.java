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
import easyconduite.model.AudioConfigChain;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.EasyconduiteException;
import easyconduite.util.Const;
import easyconduite.util.KeyCodeUtil;
import java.io.IOException;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.effect.Bloom;
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
public class AudioMediaUI extends VBox implements AudioConfigChain {

    static final Logger LOG = LogManager.getLogger(AudioMediaUI.class);

    private final IconButton button_config = new IconButton("/icons/Gear.png");

    private final IconButton button_stop = new IconButton("/icons/StopRedButton.png");

    private final IconButton button_delete = new IconButton("/icons/MinusRedButton.png");

    private final Label keycodeLabel = new Label();

    private final ObjectProperty<KeyCode> keycode = new ReadOnlyObjectWrapper<>();

    private final Label nameLabel = new Label();

    private EasyconduitePlayer player;

    private final AudioMedia audioMedia;

    private IconButton buttonPlayPause;

    private final ImageView repeatImageView = new ImageView();

    private final ProgressIndicator progressTrack;

    private AudioConfigChain nextChain;

    //private final EasyconduiteController easyConduiteController;
    /**
     * Constructor du UI custom control for an AudioMedia.<br>
     * Not draw the control but construct object and assign a
     * {@link MediaPlayer}.<br>
     *
     * @param media
     * @param controller
     */
    public AudioMediaUI(AudioMedia media, EasyconduiteController controller) {
        super(10);
        LOG.info("Construct an AudioMedia {}", media);

        this.audioMedia = media;
        ////////////////////////////////////////////////////////////////////////
        //               Initialize MediaPlayer
        ////////////////////////////////////////////////////////////////////////
        player = null;

        try {

            player = EasyconduitePlayer.create(media);
            
            // Listenning player Status property
            player.getPlayer().statusProperty().addListener((ObservableValue<? extends Status> observable, Status oldValue, Status newValue) -> {
                switch (newValue) {
                    case PAUSED:
                        buttonPlayPause.setPathNameOfIcon(Const.NAME_ICON_PLAY);
                        setBackground(Const.STOP_BACKG);
                        break;
                    case PLAYING:
                        buttonPlayPause.setPathNameOfIcon(Const.NAME_ICON_PAUSE);
                        setBackground(Const.PLAY_BACKG);
                        break;
                    case STOPPED:
                        buttonPlayPause.setPathNameOfIcon(Const.NAME_ICON_PLAY);
                        setBackground(Const.STOP_BACKG);
                        break;
                }
            });

        } catch (EasyconduiteException ex) {
            LOG.error("Error occurend during EasyPlayer construction", ex);
        }
        // Positionne la chaine de responsabilité
        this.setNext(player);

        // attribution css for Track VBOX
        this.getStyleClass().add("track-vbox");

        // Label for name of the track /////////////////////////////////////////
        nameLabel.getStyleClass().add("texte-track");
        ////////////////////////////////////////////////////////////////////////

        // ToolBar with delete and confugure button ////////////////////////////
        HBox topHbox = hBoxForTrack();

        button_delete.setOnMouseClicked((MouseEvent event) -> {
            Optional<ButtonType> result = ActionDialog.showConfirmation("Vous allez supprimer cette piste", "Voulez-vous continuer ?");
            if (result.get() == ButtonType.OK) {
                controller.removeAudioMedia(audioMedia, this);
            }
        });

        // create button wich link a key to an AudioMedia
        button_config.setOnMouseClicked((MouseEvent event) -> {
            try {
                final TrackConfigDialog trackConfigDialog = new TrackConfigDialog(this.audioMedia, controller);
            } catch (IOException ex) {
                LOG.error("Error occurend during TrackConfigDialog construction", ex);
            }
        });
        topHbox.getChildren().addAll(button_delete, button_config);
        ////////////////////////////////////////////////////////////////////////

        // Slider for volume control
        Slider curseVolume = new Slider(0, 100, this.audioMedia.getVolume());
        curseVolume.getStyleClass().add("slider-volume-track");
        curseVolume.valueProperty().bindBidirectional(this.audioMedia.volumeProperty());

        // Progressbar /////////////////////////////////////////////////////////
        progressTrack = new ProgressBar(0);
        progressTrack.getStyleClass().add("progress-bar-track");
        player.getPlayer().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            progressTrack.setProgress(newValue.toMillis() / this.audioMedia.getAudioDuration().toMillis());
        });
        ////////////////////////////////////////////////////////////////////////

        // ToolBar with Stop and play button ///////////////////////////////////
        HBox bottomHbox = hBoxForTrack();
        buttonPlayPause = new IconButton(Const.NAME_ICON_PLAY);
        buttonPlayPause.setOnMouseClicked((MouseEvent event) -> {
            player.playPause();
        });
        button_stop.setOnMouseClicked((MouseEvent event) -> {
            player.stop();
        });
        bottomHbox.getChildren().addAll(button_stop, buttonPlayPause);

        // icone repeat for repeat /////////////////////////////////////////////
        repeatImageView.setFitHeight(18);
        repeatImageView.setFitWidth(18);
        ////////////////////////////////////////////////////////////////////////

        // initialize KeyCodeLabel
        keycodeLabel.getStyleClass().add("labelkey-track");
        keycodeLabel.setEffect(new Bloom(0.4));
        this.getChildren().addAll(nameLabel, topHbox, curseVolume, progressTrack, bottomHbox, repeatImageView, keycodeLabel);
    }

    public final AudioMedia getAudioMedia() {
        return audioMedia;
    }

    private void setRepeatable(boolean repeatable) {
//        player.setRepeatable(repeatable);
        if (repeatable) {
            repeatImageView.setImage(Const.REPEAT_IMAGE);
        } else {
            repeatImageView.setImage(null);
        }
    }

    public final EasyconduitePlayer getEasyPlayer() {
        return player;
    }

    private HBox hBoxForTrack() {
        HBox hbox = new HBox(10);
        hbox.setPrefWidth(80);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    public ObjectProperty<KeyCode> keycodeProperty() {
        return keycode;
    }

    public KeyCode getKeycode() {
        return keycode.get();
    }

    @Override
    public void setNext(AudioConfigChain next) {
        this.nextChain = next;
    }

    @Override
    public void chainConfigure(AudioMedia media) {
        if (this.audioMedia.equals(media)) {
            nameLabel.setText(this.audioMedia.getName());
            setRepeatable(this.audioMedia.getRepeatable());
            keycodeLabel.setText(KeyCodeUtil.toString(this.audioMedia.getKeycode()));
            keycode.setValue(this.audioMedia.getKeycode());
            this.nextChain.chainConfigure(this.audioMedia);
        } else {
            ActionDialog.showWarning("Incohérence des objets", "Les objets AudioMedia ne sont pas egaux");
        }
    }
}
