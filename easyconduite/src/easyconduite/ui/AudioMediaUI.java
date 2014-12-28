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

import easyconduite.controllers.EasyconduiteController;
import easyconduite.objects.AudioMedia;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
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
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

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

    private Label keyCodeLabel;
    
    private ImageView repeatImageView;

    private ProgressIndicator progressTrack;

    private final SimpleStringProperty name = new SimpleStringProperty();

    private final ObjectProperty<KeyCode> affectedKeyCode = new SimpleObjectProperty<>();

    private final BooleanProperty repeat = new SimpleBooleanProperty(false);

    private final EasyconduiteController easyConduiteController;

    private final static String NAME_ICON_PLAY = "/icons/PlayGreenButton.png";

    private final static String NAME_ICON_PAUSE = "/icons/PauseBlueButton.png";

    private static final Logger logger = Logger.getLogger(AudioMediaUI.class.getName());

    /**
     * Constructor du UI custom control for an AudioMedia.<br>
     * Not draw the control but construct object and assign a
     * {@link MediaPlayer}.<br>
     * Media's volume is set to 0.5 by default.
     *
     * @param audioMedia
     * @param controller
     */
    public AudioMediaUI(final AudioMedia audioMedia, final EasyconduiteController controller) {

        super(10);
        keyCodeLabel = new Label();
        
        repeatImageView = new ImageView();

        logger.log(Level.INFO, "Create AudioMediaUI with {0}", audioMedia);

        //affectedKeyCodeProperty().setValue(audioMedia.getLinkedKeyCode());
        this.audioMedia = audioMedia;

        if (this.audioMedia.getName() != null) {
            name.set(this.audioMedia.getName());
        }

        easyConduiteController = controller;

        Media media = new Media(audioMedia.getAudioFile().toURI().toString());
        player = new MediaPlayer(media);
        player.setOnEndOfMedia(() -> {
            player.setStartTime(Duration.ZERO);
            // if repeat is false, force to stop de player.
            if (!repeat.getValue()) {
                player.stop();
                buttonPlayPause.setPathNameOfIcon(NAME_ICON_PLAY);
            }

            logger.log(Level.INFO, "End of media player with status {0}", player.getStatus());
        });

        affectedKeyCodeProperty().addListener((ObservableValue<? extends KeyCode> observable, KeyCode oldValue, KeyCode newValue) -> {
            if (newValue != null) {
                keyCodeLabel.setText(KeyCodeUtil.toString(newValue));
                getAudioMedia().setLinkedKeyCode(newValue);
                controller.updateKeycodesAudioMap();
            }
        });

        repeatProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                player.setCycleCount(MediaPlayer.INDEFINITE);
                getAudioMedia().setRepeat(newValue);
                repeatImageView.setImage(new Image(getClass().getResourceAsStream("/icons/repeat.png"), 30, 30, true, false));
                logger.log(Level.INFO, "CycleCount {0}", player.cycleCountProperty().getValue());
            }else{
                repeatImageView.setImage(null);
            }
        });

        player.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            progressTrack.setProgress(newValue.toSeconds() / player.getTotalDuration().toSeconds());
        });

        addUI();

    }

    /**
     * Add the custom control UI for an {@link AudioMediaUI} to a scene.
     * affectedKeyCode.bind(affectedKeyCode)affectedKeyCode);
     */
    public final void addUI() {

        // attribution css for Track VBOX
        this.getStyleClass().add("track-vbox");

        // Label for name of the track /////////////////////////////////////////
        Label nameLabel = new Label();
        nameLabel.getStyleClass().add("texte-track");
        nameProperty().bindBidirectional(nameLabel.textProperty());
        this.getChildren().add(nameLabel);
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
            if (reponse == Dialog.ACTION_YES) {
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
        this.getChildren().add(topHbox);
        ////////////////////////////////////////////////////////////////////////

        // Slider for volume control
        Slider curseVolume = new Slider(0, 1, 1);//
        curseVolume.getStyleClass().add("slider-volume-track");

        player.volumeProperty().bindBidirectional(curseVolume.valueProperty());
        this.getChildren().add(curseVolume);

        // Progressbar /////////////////////////////////////////////////////////
        progressTrack = new ProgressBar(0);
        progressTrack.getStyleClass().add("progress-bar-track");
        this.getChildren().add(progressTrack);
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
        this.getChildren().add(bottomHbox);
        ////////////////////////////////////////////////////////////////////////

        // icone repeat for repeat /////////////////////////////////////////////////
        repeatImageView.setFitHeight(30);
        repeatImageView.setFitWidth(30);
        this.getChildren().add(repeatImageView);


        keyCodeLabel.getStyleClass().add("labelkey-track");
        this.getChildren().add(keyCodeLabel);
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

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public KeyCode getAffectedKeyCode() {
        return affectedKeyCode.getValue();
    }

    public void setAffectedKeyCode(KeyCode code) {
        this.affectedKeyCode.setValue(code);
    }

    public ObjectProperty<KeyCode> affectedKeyCodeProperty() {
        return affectedKeyCode;
    }

    public Boolean getRepeat() {
        return repeat.getValue();
    }

    public void setRepeat(Boolean repeat) {
        this.repeat.setValue(repeat);
    }

    public BooleanProperty repeatProperty() {
        return repeat;
    }

    private HBox hBoxForTrack() {

        HBox hbox = new HBox(10);
        hbox.setPrefWidth(100);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.audioMedia);
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.affectedKeyCode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AudioMediaUI other = (AudioMediaUI) obj;
        if (!Objects.equals(this.audioMedia, other.audioMedia)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.affectedKeyCode, other.affectedKeyCode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AudioMediaUI{" + "audioMedia=" + audioMedia + ", name=" + name + ", affectedKeyCode=" + affectedKeyCode + '}';
    }

}
