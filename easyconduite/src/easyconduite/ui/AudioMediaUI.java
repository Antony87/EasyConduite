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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * AudioMedia.
 *
 * @author A Fons
 */
public class AudioMediaUI extends FlowPane {

    private MediaPlayer player;

    private AudioMedia audioMedia;

    private SimpleStringProperty name = new SimpleStringProperty();

    private final Scene scene;

    private final static Logger LOGGER = LoggerFactory.getLogger(AudioMediaUI.class);

    private final static String ID_PANE_TABLE = "#table";

    /**
     * Constructor du UI custom control for an AudioMedia.<br>
     * Not draw the control but construct object and assign a
     * {@link MediaPlayer}.<br>
     * Media's volume is set to 0.5 by default.
     *
     * @param scene
     * @param audioMedia
     */
    public AudioMediaUI(final Scene scene, final AudioMedia audioMedia) {

        super(Orientation.VERTICAL);

        LOGGER.debug("Create AudioMedia with {}", audioMedia.getAudioFile().getPath());

        this.scene = scene;
        setAudioMedia(audioMedia);
        Media media = new Media(audioMedia.getAudioFile().toURI().toString());
        player = new MediaPlayer(media);
    }

    /**
     * Add the custom control UI for an {@link AudioMediaUI} to a scene.
     *
     */
    public void addUI() {

        Insets insetForIncontrols = new Insets(10, 0, 0, 0);

        this.setAlignment(Pos.TOP_CENTER);
        this.setColumnHalignment(HPos.CENTER);
        this.setMaxHeight(USE_PREF_SIZE);
        this.setMaxWidth(USE_PREF_SIZE);
        this.setPrefHeight(380);
        this.setPrefWidth(80);
        this.setPrefWrapLength(380);
        this.setStyle("-fx-background-color: #444c57;");
        this.setPadding(insetForIncontrols);
        this.setVgap(10);

        Button boutonClose = new Button("X");
        boutonClose.setTextAlignment(TextAlignment.CENTER);
        boutonClose.setFont(new Font(9));
        boutonClose.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                remove();
            }
        });
        
        this.getChildren().add(boutonClose);

        Slider curseVolume = new Slider(0, 1, 1);
        curseVolume.setOrientation(Orientation.VERTICAL);
        curseVolume.setPrefHeight(250);
        curseVolume.setBlockIncrement(0.1);
        player.volumeProperty().bindBidirectional(curseVolume.valueProperty()); 
        this.getChildren().add(curseVolume);

        Button btnPlayPause = new Button("Play");
        btnPlayPause.setOnMouseReleased(new EventHandler<MouseEvent>() {

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
        
        this.getChildren().add(btnPlayPause);
        

        TextField textName = new TextField();
        textName.setPrefWidth(50);
        textName.setPromptText("nom");
        nameProperty().bindBidirectional(textName.textProperty());
        nameProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                audioMedia.setName(newValue);
            }
        });

        this.getChildren().add(textName);

        HBox table = (HBox) scene.lookup(ID_PANE_TABLE);
        table.getChildren().add(0, this);

    }

    /**
     * Remove the UI Control pane from the scene.
     *
     */
    public void remove() {
        player.dispose();
        HBox table = (HBox) scene.lookup("#table");
        table.getChildren().remove(this);
        EasyconduiteController.removeAudioMediaUI(this.getAudioMedia());
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

}
