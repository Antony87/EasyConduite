/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyconduite.controllers;

import easyconduite.objects.AudioMedia;
import easyconduite.ui.AudioMediaUI;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
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
import javafx.stage.FileChooser;

/**
 *
 * @author A Fons
 */
public class EasyconduiteController implements Initializable {

    @FXML
    private Label timer;

    @FXML
    private ToggleButton chronobutton;

    @FXML
    private HBox table;

    private Timeline timeline;

    private Scene scene;

    private List<AudioMediaUI> audioMediaUIList;

    private Map<KeyCode, AudioMediaUI> keycodesAudioMap;

    private static final Logger logger = Logger.getLogger(EasyconduiteController.class.getName());

    @FXML
    private void handleMouseAction(MouseEvent event) {
        if (timeline != null) {
            if (!chronobutton.isSelected()) {
                logger.info("ToggleButton chronobutton was deselected : Chrono play");
                timeline.pause();
                //chronobutton.setStyle(null);
            } else {
                logger.info("ToggleButton chronobutton was selected : Chrono play");
                //chronobutton.setStyle("-fx-background-color : red");
                timeline.play();
            }
        }
    }

    @FXML
    private void handleFichierOuvrir(ActionEvent event) {

    }

    /**
     * Cette méthode est appellée par l'événement du menu ajout d'une média dans la table audio.
     *
     * @param event
     */
    @FXML
    private void handleAddAudioMenu(ActionEvent event) {

        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (file != null) {
            AudioMedia audioMedia = new AudioMedia(file);
            AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, this);
            addMediaUI(audioMediaUI);
        }

    }

    public void removeAudioMediaUI(AudioMediaUI audioMediaui) {
        
        if (audioMediaUIList.remove(audioMediaui)) {
            logger.log(Level.INFO, "AudioMedia {0} remove from audioMediaObsList", audioMediaui.getAudioMedia().toString());
            refreshKeycodesAudioMap(audioMediaui);
            audioMediaui.getPlayer().dispose();
            table.getChildren().removeAll(audioMediaui);
        }
    }

    /**
     * Cette méthode est appellé par l'action Quit du menu Fichier et ferme l'application.
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        audioMediaUIList = new ArrayList<>();

        // initialize Map for binding Keycode to AudioMediaUI
        keycodesAudioMap = new EnumMap<>(KeyCode.class);
         
    }

    /**
     * This method add an AudioMediaUI to the scene, to the List for manage.
     *
     * @param audioMediaUI
     */
    public void addMediaUI(AudioMediaUI audioMediaUI) {

        try {
            audioMediaUIList.add(audioMediaUI);
            refreshKeycodesAudioMap(audioMediaUI);
            table.getChildren().add(table.getChildren().size(), audioMediaUI);

        } catch (Exception e) {
            //TODO penser a une zone d'affichage d'erreur.
        }

    }

    public void refreshKeycodesAudioMap(AudioMediaUI audioMediaUI) {

        final KeyCode keyCode = audioMediaUI.getAffectedKeyCode();
        AudioMediaUI oldAudioMediaUI = keycodesAudioMap.get(keyCode);
        if(null!=oldAudioMediaUI){
            oldAudioMediaUI.setAffectedKeyCode(KeyCode.UNDEFINED);
        }        
        keycodesAudioMap.clear();
        for (Iterator<AudioMediaUI> iterator = audioMediaUIList.iterator(); iterator.hasNext();) {
            AudioMediaUI next = iterator.next();
            if (next.getAffectedKeyCode() != KeyCode.UNDEFINED || null != next.getAffectedKeyCode()) {
                logger.log(Level.INFO, "Put {0} , {1} in keycodesAudioMap",new Object[]{next.getAffectedKeyCode(),next.toString()});
                keycodesAudioMap.put(next.getAffectedKeyCode(), next);
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

    public Map<KeyCode, AudioMediaUI> getKeycodesAudioMap() {
        return keycodesAudioMap;
    }

    public ToggleButton getChronobutton() {
        return chronobutton;
    }
}
