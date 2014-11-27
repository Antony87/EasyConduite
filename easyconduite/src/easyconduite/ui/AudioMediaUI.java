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

import easyconduite.controllers.AudioMediaController;
import easyconduite.objects.AudioMedia;
import java.io.IOException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * AudioMedia.
 *
 * @author A Fons
 */
public class AudioMediaUI {

    private FlowPane pane;

    private MediaPlayer player;

    private AudioMedia audioMedia;

    private AudioMediaController controller;

    private final Scene scene;

    private final DoubleProperty volume = new SimpleDoubleProperty();

    private final static Logger LOGGER = LoggerFactory.getLogger(AudioMediaUI.class);

    private final static String FXML_CUSTOM_PATH = "ressources/piste.fxml";

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

        LOGGER.debug("Create AudioMedia with {}", audioMedia.getAudioFile().getPath());
        this.scene=scene;
        setAudioMedia(audioMedia);
        Media media = new Media(audioMedia.getAudioFile().toURI().toString());
        setPlayer(new MediaPlayer(media));
        // bind for volume property
        volume.bindBidirectional(player.volumeProperty());
        player.setVolume(0.5);

    }

    /**
     * Add the custom control UI for an {@link AudioMediaUI} to a scene.
     *
     * @param scene
     */
    public void addUI(final Scene scene) {

        final FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_CUSTOM_PATH));
        pane = null;
        try {
            controller = loader.getController();
            controller.setAudioMediaUI(this);
            pane = loader.load();
        } catch (IOException e) {
            LOGGER.error("Error loading {}", FXML_CUSTOM_PATH, e);
        }
        HBox table = (HBox) scene.lookup(ID_PANE_TABLE);
        table.getChildren().add(0, pane);

    }

    /**
     * Remove the UI Control pane from the scene.
     *
     * @param scene
     */
    public void remove(Scene scene) {
        player.dispose();
        HBox table = (HBox) scene.lookup("#table");
        table.getChildren().remove(pane);
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

}
