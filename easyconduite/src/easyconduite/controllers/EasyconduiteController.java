/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyconduite.controllers;

import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import easyconduite.ui.AudioMediaUI;
import java.io.File;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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

    private Timeline timeline;

    private AudioTable audioTable;

    private Scene scene;

    private ObservableList<AudioMedia> audioMediaObsList;

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
     * Cette méthode est appellée par l'événement du menu ajout d'une média dans
     * la table audio.
     *
     * @param event
     */
    @FXML
    private void handleAddAudioMenu(ActionEvent event) {

        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (file != null) {
            AudioMedia audioMedia = new AudioMedia(file);
            if (!audioMediaObsList.contains(audioMedia)) {
                audioMediaObsList.add(audioMedia);
                AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, this);
            } else {
                audioMedia = null;
                // @TODO affichage message alerte.
            }
        }

    }

    public void removeAudioMediaUI(AudioMediaUI audioMediaui) {

        if (audioMediaObsList.contains(audioMediaui.getAudioMedia())) {
            audioMediaObsList.remove(audioMediaui.getAudioMedia());
        }
        if (keycodesAudioMap.containsValue(audioMediaui)) {
            keycodesAudioMap.remove(audioMediaui.affectedKeyCodeProperty(), audioMediaui);

        }
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // initialize Observable List of AudioMedia
        audioMediaObsList = FXCollections.observableArrayList();

        // initialize Map for binding Keycode to AudioMediaUI
        keycodesAudioMap = new EnumMap<>(KeyCode.class);

        audioMediaObsList.addListener(new ListChangeListener<AudioMedia>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends AudioMedia> change) {

                while (change.next()) {
                    if (change.wasAdded()) {
                        audioTable.addAudioMedia(change.getAddedSubList().get(0));
                        break;
                    }
                    if (change.wasRemoved()) {
                        audioTable.removeAudioMedia(change.getRemoved().get(0));
                        break;
                    }
                }

            }
        });

    }

    public void updateKeyCodetoMap(final KeyCode newKeycode, final AudioMediaUI newAudioMediaUi) {

        if (keycodesAudioMap.containsKey(newKeycode)) {
            AudioMediaUI oldAUdioMediaUi = keycodesAudioMap.get(newKeycode);
            oldAUdioMediaUi.setAffectedKeyCode(KeyCode.UNDEFINED);
            keycodesAudioMap.remove(newKeycode);
        }
        for (Map.Entry<KeyCode, AudioMediaUI> entrySet : keycodesAudioMap.entrySet()) {
            KeyCode key = entrySet.getKey();
            AudioMediaUI value = entrySet.getValue();
            if (newAudioMediaUi.equals(value)) {
                keycodesAudioMap.remove(key, value);
                break;
            }
        }

        newAudioMediaUi.setAffectedKeyCode(newKeycode);
        keycodesAudioMap.put(newKeycode, newAudioMediaUi);

        for (Map.Entry<KeyCode, AudioMediaUI> entrySet : keycodesAudioMap.entrySet()) {
            KeyCode key = entrySet.getKey();
            AudioMediaUI value = entrySet.getValue();
            logger.log(Level.INFO, "KeyMap key {0} AUdioMediUI {1}", new Object[]{key, value});

        }
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public void setAudioTable(AudioTable audioTable) {
        this.audioTable = audioTable;
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
