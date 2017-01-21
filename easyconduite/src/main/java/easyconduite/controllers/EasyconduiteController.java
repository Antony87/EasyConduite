/*
 * Copyright (C) 2017 Antony Fons
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

import easyconduite.util.PersistenceUtil;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import easyconduite.ui.AboutDialog;
import easyconduite.ui.ActionDialog;
import easyconduite.ui.AudioMediaUI;
import easyconduite.ui.Chrono;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author A Fons
 */
public class EasyconduiteController implements Initializable {

    static final Logger LOG = LogManager.getLogger(EasyconduiteController.class);

    @FXML
    private Label timer;

    @FXML
    private ToggleButton chronobutton;

    @FXML
    private HBox tableLayout;

    private Timeline timeline;

    private Scene scene;

    private AudioTable audioTable;

    private List<AudioMediaUI> mediaUIList;

    @FXML
    private void handleRazChrono(ActionEvent event) {
        timeline.stop();
        timeline = null;
        timeline = Chrono.getTimeline(timer);
        timer.setText("00:00:00");
        chronobutton.setSelected(false);
    }

    @FXML
    private void handleMouseAction(MouseEvent event) {
        // TODO refactorer 
        if (timeline != null) {
            if (!chronobutton.isSelected()) {
                timeline.pause();
                //timeLineConduite.pause();
                //chronobutton.setStyle(null);
            } else {
                timeline.play();
                //timeLineConduite.play();
            }
        }
    }

    @FXML
    private void handleFichierOuvrir(ActionEvent event) {

        File file = null;
        if (mediaUIList.size() > 0) {
            Alert alert = ActionDialog.createActionDialog("Charger un projet écrasera l'actuel");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                file = PersistenceUtil.getOpenProjectFile(scene);
            }
        } else if (mediaUIList.isEmpty()) {
            file = PersistenceUtil.getOpenProjectFile(scene);
        }
        if (file != null) {
            audioTable = PersistenceUtil.open(file);
            mediaUIList.clear();
            tableLayout.getChildren().clear();
            for (AudioMedia audioMedia : audioTable.getAudioMediaList()) {
                AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, EasyconduiteController.this);
                mediaUIList.add(audioMediaUI);
                tableLayout.getChildren().add(audioMediaUI);
            }   
        }
    }

    /**
     * Cette méthode est appellée par l'événement du menu ajout d'une média dans
     * la table audio.
     *
     * @param event
     */
    @FXML
    private void handleAddAudioMenu(ActionEvent event) {

        File file = PersistenceUtil.getOpenAudioFile(scene);
        if (file != null) {
            AudioMedia audioMedia = new AudioMedia(file);
            audioTable.getAudioMediaList().add(audioMedia);
            AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, EasyconduiteController.this);
            mediaUIList.add(audioMediaUI);
            tableLayout.getChildren().add(audioMediaUI);
            LOG.debug("AudioMedia {} added to AudioTable", audioMedia);
        }

    }

    public void removeAudioMedia(AudioMedia audioMedia, AudioMediaUI ui) {

        tableLayout.getChildren().remove(ui);
        mediaUIList.remove(ui);
        audioTable.getAudioMediaList().remove(audioMedia);
        LOG.debug("AudioMedia {} removed from AudioTable", audioMedia);
    }

    @FXML
    private void handleQuit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleKeyCodePlay(KeyEvent event) {
        mediaUIList.stream().filter(ui -> event.getCode() == ui.getKeycode()).findFirst().ifPresent((AudioMediaUI t) -> {
            t.getEasyPlayer().playPause();
            LOG.trace("Key {} was pressed playpause {}", event.getCode(), t);
        });
    }

    @FXML
    private void handlePauseAll(ActionEvent event) {
        mediaUIList.forEach(u -> {
            u.getEasyPlayer().pause();
        });
    }

    @FXML
    private void handleStopAll(ActionEvent event) {
        mediaUIList.forEach(u -> {
            u.getEasyPlayer().stop();
        });
    }

    @FXML
    private void handleSave(ActionEvent event) {

        File file = PersistenceUtil.getSaveProjectFile(scene);
        if (file != null) {
            try {
                PersistenceUtil.save(file, audioTable);
            } catch (IOException ex) {
                LOG.error("An error occured", ex);
            }
        }
    }

    @FXML
    private void handleAbout(ActionEvent event) {

        try {
            AboutDialog aboutDialog = new AboutDialog();
        } catch (IOException ex) {
            LOG.error("An error occured", ex);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOG.debug("Controler initialisation");
        audioTable = new AudioTable();
        mediaUIList = new ArrayList<>();
    }

    public boolean isKeyCodeExist(KeyCode keycode) {
        return mediaUIList.stream().anyMatch(u -> keycode == u.getKeycode());
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public AudioTable getAudioTable() {
        return audioTable;
    }

    public HBox getTableLayout() {
        return tableLayout;
    }

    public List<AudioMediaUI> getMediaUIList() {
        return mediaUIList;
    }
}
