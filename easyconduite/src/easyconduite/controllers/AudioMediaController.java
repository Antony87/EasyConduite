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
package easyconduite.controllers;

import easyconduite.ui.AudioMediaUI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the UI custom control {@link AudioMediaUI}.
 *
 * @author A Fons
 */
public class AudioMediaController extends FlowPane implements Initializable{
    
    private AudioMediaUI audioMediaUI;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(AudioMediaController.class);

    @FXML
    private void handlePlayPauseAudio(MouseEvent event) {
        // get source of event and parent.
        if (event.getSource().getClass() == Button.class) {
            final Button button = (Button) event.getSource();

            MediaPlayer player = audioMediaUI.getPlayer();
            Status status = player.getStatus();

            if (status == MediaPlayer.Status.PLAYING) {
                player.pause();
            }
            if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.STOPPED
                    || status == MediaPlayer.Status.READY) {
                player.play();
            }
        }

    }

    @FXML
    private void handleDeleteTrack(MouseEvent event) {
        if (event.getSource().getClass() == Button.class) {
            EasyconduiteController.removeAudioMediaUI(audioMediaUI.getAudioMedia());
            audioMediaUI.remove(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public AudioMediaUI getAudioMediaUI() {
        return audioMediaUI;
    }

    public void setAudioMediaUI(AudioMediaUI audioMediaUI) {
        this.audioMediaUI = audioMediaUI;
    }

}
