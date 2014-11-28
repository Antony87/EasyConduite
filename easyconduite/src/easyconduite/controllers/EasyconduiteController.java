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
import java.util.ResourceBundle;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    private Stage stage;

    private AudioTable audioTable;

    private static ObservableList<AudioMedia> audioMediaObsList;

    @FXML
    private void handleMouseAction(MouseEvent event) {
        System.out.println("You clicked me!");
        if (timeline != null) {
            if (!chronobutton.isSelected()) {
                timeline.pause();
                chronobutton.setStyle(null);
            } else {
                chronobutton.setStyle("-fx-background-color : red");
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

        if (event.getSource() == MenuItem.class) {
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(this.stage);
            if (file != null) {
                AudioMedia audioMedia = new AudioMedia(file);
                AudioMediaUI audioMediaUI = new AudioMediaUI(getStage().getScene(), audioMedia);
            }
        }
    }

    public static void removeAudioMediaUI(AudioMedia audioMedia) {
        if (audioMediaObsList.contains(audioMedia)) {
            audioMediaObsList.remove(audioMedia);
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // initialize Observable List of AudioMedia
        setAudioMediaObsList(FXCollections.observableArrayList());
        // binding with Observable List from AudioTable
        Bindings.bindContent(getAudioMediaObsList(), AudioTable.getAudioMediaObsList());

    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static ObservableList<AudioMedia> getAudioMediaObsList() {
        return audioMediaObsList;
    }

    private static void setAudioMediaObsList(ObservableList<AudioMedia> audioMediaObsList) {
        EasyconduiteController.audioMediaObsList = audioMediaObsList;
    }

    public AudioTable getAudioTable() {
        return audioTable;
    }

    public void setAudioTable(AudioTable audioTable) {
        this.audioTable = audioTable;
    }
}
