/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyconduite.controllers;

import easyconduite.util.PersistenceUtil;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import easyconduite.ui.AboutDialog;
import easyconduite.ui.ActionDialog;
import easyconduite.ui.AudioMediaUI;
import easyconduite.ui.Chrono;
import easyconduite.util.Config;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
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
            audioTable = PersistenceUtil.open(file);
            List<AudioMedia> audioMedias = audioTable.getAudioMediaList();
            // avant d'ajouter les MediaUI, on vide la table
            tableHbox.getChildren().clear();
            audioMedias.stream().forEach((audioMedia) -> {
                addMediaUI(audioMedia);
            });
            //updateKeycodesAudioMap();
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
            addMediaUI(audioMedia);
        }

    }

    public void removeAudioMediaUI(AudioMediaUI audioMediaUI) {

        // remove audioMedia from AudioTable.
        audioTable.removeIfPresent(audioMediaUI.getAudioMedia());

        // remove from list aof AudioMediaUI
        audioMediaUIList.remove(audioMediaUI);

        // remove audioMediaUI from keycodesAudioMap
        //keycodesAudioMap.remove(audioMediaUI.getAudioMedia().getLinkedKeyCode(), audioMediaUI);
        audioMediaUI.getPlayer().dispose();
        tableHbox.getChildren().removeAll(audioMediaUI);

        LOGGER.log(Level.INFO, "AudioMedia {0} remove from audioMedia List", audioMediaUI.getAudioMedia().toString());

    }

    /**
     * Cette méthode est appellé par l'action Quit du menu Fichier et ferme
     * l'application.
     *
     * @param event
     */
    @FXML
    private void handleQuit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleKeyCodePlay(KeyEvent event) {

        AudioMediaUI audioMediaUIOpt = findAudioMediaUI(audioMediaUIList, event.getCode());
        if (audioMediaUIOpt != null) {
            audioMediaUIOpt.playPause();
        }

//        if (keycodesAudioMap.containsKey(event.getCode())) {
//            AudioMediaUI audioMedia = keycodesAudioMap.get(event.getCode());
//            audioMedia.playPause();
//        }
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

        // = new EnumMap<>(KeyCode.class);
    }

    public AudioMediaUI findAudioMediaUI(List<AudioMediaUI> list, KeyCode keycode) {
        LOGGER.log(Level.FINE, "findAudioMediaUI by Keycode within {0} size list and Keycode {1}", new Object[]{list.size(), keycode.toString()});
        AudioMediaUI audioMediaUIOpt = null;

        audioMediaUIOpt = list.stream().filter(x -> keycode.equals(x.getAudioMedia().getKeycode())).findAny().orElse(null);

        return audioMediaUIOpt;
    }

    /**
     * This method add an AudioMediaUI to the scene, to the List for manage.
     *
     * @param audioMedia
     */
    public void addMediaUI(AudioMedia audioMedia) {

        AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, this);
        try {
            audioMediaUIList.add(audioMediaUI);
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

//    public void updateKeycodesAudioMap() {
//
//        keycodesAudioMap.clear();
//        audioMediaUIList.stream().filter((unAudioMediaUI) -> (KeyCodeUtil.isValid(unAudioMediaUI.getAudioMedia().getLinkedKeyCode()))).forEach((unAudioMediaUI) -> {
//            keycodesAudioMap.put(unAudioMediaUI.getAudioMedia().getLinkedKeyCode(), unAudioMediaUI);
//        });
//    }
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
