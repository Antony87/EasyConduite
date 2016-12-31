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
import easyconduite.ui.AudioMediaUI;
import easyconduite.util.KeyCodeUtil;
import easyconduite.ui.ParamConduiteDialog;
import easyconduite.util.DurationUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author A Fons
 */
public class EasyconduiteController implements Initializable {

    private static final Logger logger = Logger.getLogger(EasyconduiteController.class.getName());

    @FXML
    private Label timer;

    @FXML
    private ToggleButton chronobutton;

    @FXML
    private HBox table;

    @FXML
    private Rectangle timeLineRectangle;

    @FXML
    private Label timeLineLabel;

    @FXML
    private StackPane timelinepane;

    @FXML
    private Label duration;

    private Timeline timeline;

    private Timeline timeLineConduite;

    private Scene scene;

    private AudioTable audioTable;

    /**
     * List of AudioMediaUI added to the table.
     */
    private List<AudioMediaUI> audioMediaUIList;

    /**
     * Map of mapping from a KeyCode (Keyboard key) and an AudioMediaUI.
     */
    private Map<KeyCode, AudioMediaUI> keycodesAudioMap;

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

        File file = PersistenceUtil.getOpenProjectFile(scene);
        if (file != null) {
            audioTable = PersistenceUtil.open(file);
            List<AudioMedia> audioMedias = audioTable.getAudioMediaList();
            // avant d'ajouter les MediaUI, on vide la table
            table.getChildren().clear();
            audioMedias.stream().forEach((audioMedia) -> {
                addMediaUI(audioMedia);
            });
            updateKeycodesAudioMap();
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
        keycodesAudioMap.remove(audioMediaUI.getAudioMedia().getLinkedKeyCode(), audioMediaUI);

        audioMediaUI.getPlayer().dispose();
        table.getChildren().removeAll(audioMediaUI);

        logger.log(Level.INFO, "AudioMedia {0} remove from audioMediaObsList", audioMediaUI.getAudioMedia().toString());

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

        if (keycodesAudioMap.containsKey(event.getCode())) {
            AudioMediaUI audioMedia = keycodesAudioMap.get(event.getCode());
            audioMedia.playPause();
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {

        File file = PersistenceUtil.getSaveProjectFile(scene);
        if (file != null) {
            try {
                PersistenceUtil.save(file, audioTable);
            } catch (IOException ex) {
                Logger.getLogger(EasyconduiteController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void handleAbout(ActionEvent event) {

        try {
            AboutDialog aboutDialog = new AboutDialog();
        } catch (IOException ex) {
            Logger.getLogger(EasyconduiteController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void handleParamConduite(ActionEvent event) {

        try {
            System.out.println("Appel constructeur ParamDialog");
            ParamConduiteDialog conduiteDialog = new ParamConduiteDialog(audioTable, this);
        } catch (IOException ex) {
            Logger.getLogger(EasyconduiteController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        audioMediaUIList = new ArrayList<>();

        keycodesAudioMap = new EnumMap<>(KeyCode.class);

    }

    /**
     * This method add an AudioMediaUI to the scene, to the List for manage.
     *
     * @param audioMedia
     */
    public void addMediaUI(AudioMedia audioMedia) {

        AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, this);

        //audioMediaUI.setAffectedKeyCode(audioMedia.getLinkedKeyCode());
        //audioMediaUI.setName(audioMedia.getName());
        audioMediaUIList.add(audioMediaUI);

        table.getChildren().add(table.getChildren().size(), audioMediaUI);

    }

    public void updateKeycodesAudioMap() {

        keycodesAudioMap.clear();
        for (AudioMediaUI unAudioMediaUI : audioMediaUIList) {
            if (KeyCodeUtil.isValid(unAudioMediaUI.getAudioMedia().getLinkedKeyCode())) {
                keycodesAudioMap.put(unAudioMediaUI.getAudioMedia().getLinkedKeyCode(), unAudioMediaUI);
            }
        }
    }

    public void updateConduiteDuration(Duration duree) {
        if (null != duree) {
            audioTable.setDuration(duree);
            duration.setText(DurationUtil.toStringForConduite(duree));

//            if(duree!=null && !duree.equals(Duration.ZERO)){
//                CursorTimeLineConduite cursorConduite = CursorTimeLineConduite.getInstance(duree, timelinepane, timeLineRectangle);
//                timeLineConduite=cursorConduite.getTimeLine();
//            }
        }

    }

    public boolean isExistKeyCode(KeyCode keycode) {

        return keycodesAudioMap.containsKey(keycode);

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

    public Map<KeyCode, AudioMediaUI> getKeycodesAudioMap() {
        return keycodesAudioMap;
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
