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

import easyconduite.Config;
import easyconduite.controllers.EasyconduiteController;
import easyconduite.objects.AudioMedia;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * AudioMedia.
 *
 * @author A Fons
 */
public class AudioMediaUI extends VBox {

    private MediaPlayer player;

    private AudioMedia audioMedia;

    private SimpleStringProperty name = new SimpleStringProperty();

    private final EasyconduiteController easyConduiteController;

    private final static String ID_PANE_TABLE = "#table";

    private static Logger logger = Logger.getLogger(AudioMediaUI.class.getName());

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

        logger.log(Level.INFO, "Create AudioMediaUI with {0}", audioMedia);

        easyConduiteController = controller;
        setAudioMedia(audioMedia);
        Media media = new Media(audioMedia.getAudioFile().toURI().toString());
        player = new MediaPlayer(media);
    }

    /**
     * Add the custom control UI for an {@link AudioMediaUI} to a scene.
     *
     */
    public void addUI() {

        logger.setLevel(Config.getLevel());
        logger.entering(this.getClass().getName(), "addUI");
        // attribution css for Track VBOX
        this.getStyleClass().add("track-vbox");

        HBox topHbox = hBoxForTrack();
        IconButton buttonQuit = new IconButton("/icons/MinusRedButton.png");
        buttonQuit.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                remove();
            }
        });

        IconButton buttonAssocKey = new IconButton("/icons/Gear.png");
        buttonAssocKey.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                LinkKeyBoardDialog dialog = new LinkKeyBoardDialog(getThis(), easyConduiteController);
            }
        });
        topHbox.getChildren().addAll(buttonQuit, buttonAssocKey);
        this.getChildren().add(topHbox);

        // Slider for volume control
        Slider curseVolume = new Slider(0, 1, 1);
        curseVolume.setOrientation(Orientation.VERTICAL);
        curseVolume.setPrefHeight(250);
        curseVolume.setBlockIncrement(0.1);
        curseVolume.setShowTickMarks(true);
        curseVolume.setMajorTickUnit(0.1);
        player.volumeProperty().bindBidirectional(curseVolume.valueProperty());
        this.getChildren().add(curseVolume);

        TextField textName = new TextField();
        textName.getStyleClass().add("texteTrack");
        textName.setPromptText("nom du son");
        nameProperty().bindBidirectional(textName.textProperty());
        nameProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                audioMedia.setName(newValue);
            }
        });

        this.getChildren().add(textName);
        
        HBox bottomHbox = hBoxForTrack();
        IconButton buttonPlayPause = new IconButton("/icons/PlayGreenButton.png");
        buttonPlayPause.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                MediaPlayer.Status status = player.getStatus();

                if (status == MediaPlayer.Status.PLAYING) {
                    System.out.println("pause");
                    player.pause();
                }
                if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.STOPPED
                        || status == MediaPlayer.Status.READY) {
                    System.out.println("play");
                    player.seek(Duration.ZERO);
                    player.play();
                }
            }
        });
        IconButton buttonStop = new IconButton("/icons/StopRedButton.png");
        
        bottomHbox.getChildren().addAll(buttonStop,buttonPlayPause);
        this.getChildren().add(bottomHbox);
        
        HBox table = (HBox) getSceneFromController().lookup(ID_PANE_TABLE);
        table.getChildren().add(table.getChildren().size(), this);
    }

    /**
     * Remove the UI Control pane from the scene.
     *
     */
    public void remove() {
        player.dispose();
        HBox table = (HBox) getSceneFromController().lookup(ID_PANE_TABLE);
        table.getChildren().remove(this);
        easyConduiteController.removeAudioMediaUI(this);
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

    /**
     * Get the AudioMedia.
     *
     * @return
     */
    public AudioMedia getAudioMedia() {
        return audioMedia;
    }

    private void setAudioMedia(AudioMedia audioMedia) {
        this.audioMedia = audioMedia;
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

    private Scene getSceneFromController() {
        return easyConduiteController.getScene();
    }

    private AudioMediaUI getThis() {
        return this;
    }

    private HBox hBoxForTrack() {

        HBox hbox = new HBox(10);
        hbox.setPrefWidth(100);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

}
