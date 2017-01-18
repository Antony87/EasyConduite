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
import easyconduite.ui.EasyconduitePlayer;
import easyconduite.util.Config;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.media.MediaPlayer.Status;

/**
 *
 * @author A Fons
 */
public class EasyconduiteController implements Initializable {

    private static final String CLASSNAME = EasyconduiteController.class.getName();
    private static final Logger LOGGER = Config.getCustomLogger(CLASSNAME);

    @FXML
    private Label timer;

    @FXML
    private ToggleButton chronobutton;

    @FXML
    private HBox tableHbox;

    private Timeline timeline;

    private Scene scene;

    private AudioTable audioTable;

    /**
     * List of AudioMediaUI added to the table.
     */
    private List<AudioMediaUI> audioMediaUIList;

    private EnumMap<KeyCode, EasyconduitePlayer> keyCodePlayersMap;

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

        if (audioMediaUIList.size() > 0) {
            Alert alert = ActionDialog.createActionDialog("Charger un projet écrasera l'actuel");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                file = PersistenceUtil.getOpenProjectFile(scene);
            }
        } else if (audioMediaUIList.isEmpty()) {
            file = PersistenceUtil.getOpenProjectFile(scene);
        }

        if (file != null) {

            audioTable = null;
            audioMediaUIList.clear();
            keyCodePlayersMap.clear();
            audioTable = PersistenceUtil.open(file);

            // reconstitution de la table de controle.
            List<AudioMedia> audioMedias = audioTable.getAudioMediaList();
            // avant d'ajouter les MediaUI, on vide la table
            tableHbox.getChildren().clear();
            audioMedias.stream().forEach((audioMedia) -> {
                addMediaUIControl(audioMedia);
            });
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
            // Add audioMedia to the AudioTable.
            audioTable.addIfNotPresent(audioMedia);
            // Create custom control.
            addMediaUIControl(audioMedia);
        }

    }

    public void removeAudioMediaUI(AudioMediaUI audioMediaUI) {

        final AudioMedia audioMedia = audioMediaUI.getAudioMedia();
        if (null != audioMedia.getKeycode()) {
            if (keyCodePlayersMap.containsKey(audioMedia.getKeycode())) {
                keyCodePlayersMap.remove(audioMedia.getKeycode());
            }
        }
        // remove audioMedia from AudioTable.
        audioTable.removeIfPresent(audioMedia);
        // remove from list aof AudioMediaUI
        audioMediaUIList.remove(audioMediaUI);
        // remove audioMediaUI from keycodesAudioMap
        audioMediaUI.getEasyPlayer().getPlayer().dispose();
        tableHbox.getChildren().removeAll(audioMediaUI);
        LOGGER.log(Level.INFO, "AudioMedia {0} remove from audioMedia List", audioMediaUI.getAudioMedia().toString());

    }

    @FXML
    private void handleQuit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleKeyCodePlay(KeyEvent event) {
        if (keyCodePlayersMap.containsKey(event.getCode())) {
            keyCodePlayersMap.get(event.getCode()).playPause();
        }
    }

    @FXML
    private void handlePauseAll(ActionEvent event) {
        audioMediaUIList.stream().filter(x -> x.getEasyPlayer().getStatus().equals(Status.PLAYING)).forEach(x -> x.getEasyPlayer().playPause());
    }
    
    @FXML
    private void handleStopAll(ActionEvent event) {
        audioMediaUIList.stream().filter(x -> x.getEasyPlayer().getStatus().equals(Status.PAUSED)).forEach(x -> x.getEasyPlayer().stop());
        audioMediaUIList.stream().filter(x -> x.getEasyPlayer().getStatus().equals(Status.PLAYING)).forEach(x -> x.getEasyPlayer().stop());
    }

    @FXML
    private void handleSave(ActionEvent event) {

        File file = PersistenceUtil.getSaveProjectFile(scene);
        if (file != null) {
            try {
                PersistenceUtil.save(file, audioTable);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error occured", ex);
            }
        }
    }

    @FXML
    private void handleAbout(ActionEvent event) {

        try {
            AboutDialog aboutDialog = new AboutDialog();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error occured", ex);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        audioMediaUIList = new ArrayList<>();
        keyCodePlayersMap = new EnumMap(KeyCode.class);

    }

    /**
     * This method add an AudioMediaUI to the scene, to the List for manage.
     *
     * @param audioMedia
     */
    private void addMediaUIControl(AudioMedia audioMedia) {

        final AudioMediaUI audioMediaUI;
        audioMediaUI = new AudioMediaUI(audioMedia, this);
        try {
            // add Audio UI control to List
            audioMediaUIList.add(audioMediaUI);
            updateKeycodeMap(audioMedia);
            LOGGER.log(Level.INFO, "AudioMediaUI[{0}] added at {1}", new Object[]{audioMediaUI.getAudioMedia().getName(), audioMediaUIList.size()});
            //table.getChildren().add(table.getChildren().size(), audioMediaUI);
            tableHbox.getChildren().add(audioMediaUI);

        } catch (ClassCastException ce) {
            LOGGER.log(Level.SEVERE, "This class can't be added to the list", ce.getCause());
            ActionDialog.displayErrorDialog("Type d'objet incompatible avec liste AudioMediaUI");
        } catch (NullPointerException ne) {
            LOGGER.log(Level.SEVERE, "Immpossible d'ajouter NULL dans la liste AudioMediaUI", ne.getCause());
            ActionDialog.displayErrorDialog("Type d'objet incompatible avec liste AudioMediaUI");
        }
    }

    /**
     *
     * @param audioMedia
     */
    public final void updateKeycodeMap(AudioMedia audioMedia) {
        if (null != audioMedia.getKeycode()) {
            if (!keyCodePlayersMap.containsKey(audioMedia.getKeycode())) {
                // find AudioMediaUI and player
                Optional<AudioMediaUI> optional = audioMediaUIList.stream().filter(x -> x.getAudioMedia().equals(audioMedia)).findAny();
                if (optional.isPresent()) {
                    keyCodePlayersMap.put(audioMedia.getKeycode(), optional.get().getEasyPlayer());
                }
            }
        }
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

    public List<AudioMediaUI> getAudioMediaUIList() {
        return audioMediaUIList;
    }

    public ToggleButton getChronobutton() {
        return chronobutton;
    }

    public AudioTable getAudioTable() {
        return audioTable;
    }

    public void setAudioTable(AudioTable audioTable) {
        this.audioTable = audioTable;
    }

}
