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

import easyconduite.objects.AudioMedia;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an AudioMedia.
 *
 * @author A Fons
 */
public class AudioMediaUI {

    private FlowPane pane;

    private MediaPlayer player;

    final private AudioMedia audioMedia;
    
    private final Logger logger = LoggerFactory.getLogger(AudioMediaUI.class);

    private final static String FXML_CUSTOM_PATH = "ressources/piste.fxml";

    /**
     * Constructor du UI custom control for an AudioMedia.
     *
     * @param scene
     * @param audioMedia
     */
    public AudioMediaUI(final Scene scene, final AudioMedia audioMedia) {
        
        logger.debug("Create AudioMedia with {}",audioMedia.getAudioFile().getPath());
        
        this.audioMedia = audioMedia;
    }

    /**
     * Add to a scene the custom control UI for an {@link AudioMediaUI}.
     *
     * @param scene
     */
    public void addUI(final Scene scene) {

        final FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_CUSTOM_PATH));

        pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HBox table = (HBox) scene.lookup("#table");
        table.getChildren().add(0, pane);

    }

    public MediaPlayer getPlayer() {
        return player;
    }

    private void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public AudioMedia getAudioMedia() {
        return audioMedia;
    }

}
