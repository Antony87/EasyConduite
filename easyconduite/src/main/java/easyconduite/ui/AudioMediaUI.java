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

import easyconduite.util.KeyCodeUtil;
import easyconduite.controllers.EasyconduiteController;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.EasyconduiteException;
import easyconduite.util.Const;
import java.io.IOException;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
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

        audioMedia = media;
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

        // attribution css for Track VBOX
        this.getStyleClass().add("track-vbox");

        // Label for name of the track /////////////////////////////////////////
        nameLabel.getStyleClass().add("texte-track");
        nameLabel.textProperty().bind(audioMedia.nameProperty());
        ////////////////////////////////////////////////////////////////////////

        // ToolBar with delete and confugure button ////////////////////////////
        HBox topHbox = hBoxForTrack();

        button_delete.setOnMouseClicked((MouseEvent event) -> {
            Alert alert = ActionDialog.createActionDialog("Vous allez supprimer cette piste.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                controller.removeAudioMedia(audioMedia, this);
            }
        });

        // create button wich link a key to an AudioMedia
        button_config.setOnMouseClicked((MouseEvent event) -> {
            try {
                this.audioMedia.keycodeProperty().addListener((ObservableValue<? extends KeyCode> observable, KeyCode oldValue, KeyCode newValue) -> {
                    if (newValue != oldValue) {
                        keycodeLabel.setText(KeyCodeUtil.toString(newValue));
                    }
                });
                LinkKeyBoardDialog dialog = new LinkKeyBoardDialog(audioMedia, controller);

            } catch (IOException ex) {
                LOG.error("Error loading LinkKeyBoardDialog", ex);
            }
        });

        topHbox.getChildren().addAll(button_delete, button_config);
        ////////////////////////////////////////////////////////////////////////

        // Slider for volume control
        Slider curseVolume = new Slider(0, 1, this.audioMedia.getVolume());
        curseVolume.getStyleClass().add("slider-volume-track");
        curseVolume.valueProperty().bindBidirectional(this.audioMedia.volumeProperty());

        // Progressbar /////////////////////////////////////////////////////////
        progressTrack = new ProgressBar(0);
        progressTrack.getStyleClass().add("progress-bar-track");
        progressTrack.progressProperty().bind(player.currentProgressProperty());
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
        ////////////////////////////////////////////////////////////////////////

        //initialize repeat icon
        setRepeatable(audioMedia.getRepeatable());
        audioMedia.repeatable.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            setRepeatable(newValue);
        });
        // icone repeat for repeat /////////////////////////////////////////////
        repeatImageView.setFitHeight(18);
        repeatImageView.setFitWidth(18);
        ////////////////////////////////////////////////////////////////////////

        // initialize KeyCodeLabel
        keycode.bind(audioMedia.keycodeProperty());
        keycodeLabel.getStyleClass().add("labelkey-track");
        setKeycodeLabel();

        this.getChildren().addAll(nameLabel, topHbox, curseVolume, progressTrack, bottomHbox, repeatImageView, keycodeLabel);

    }

    public final AudioMedia getAudioMedia() {
        return audioMedia;
    }

    private void setRepeatable(boolean repeatable) {
        player.setRepeatable(repeatable);
        if (repeatable) {
            repeatImageView.setImage(Const.REPEAT_IMAGE);
        } else {
            repeatImageView.setImage(null);
        }
    }

    private void setKeycodeLabel() {
        keycodeLabel.setText(KeyCodeUtil.toString(audioMedia.getKeycode()));
    }

    public final EasyconduitePlayer getEasyPlayer() {
        return player;
    }

    private HBox hBoxForTrack() {

        HBox hbox = new HBox(10);
        hbox.setPrefWidth(100);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    public ObjectProperty<KeyCode> keycodeProperty() {
        return keycode;
    }

    public KeyCode getKeycode() {
        return keycode.get();
    }
}
