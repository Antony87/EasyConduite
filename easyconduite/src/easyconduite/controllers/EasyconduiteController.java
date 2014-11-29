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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private AudioTable audioTable;
    
    private Scene scene;
    
    private static ObservableList<AudioMedia> audioMediaObsList;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(EasyconduiteController.class);
    
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
     * Cette méthode est appellée par l'événement du menu ajout d'une média dans
     * la table audio.
     *
     * @param event
     */
    @FXML
    private void handleAddAudioMenu(ActionEvent event) {
        
        System.out.println("AddAudioMenu");
        
        LOGGER.debug("Call AddAudioMenu");
        
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (file != null) {
            AudioMedia audioMedia = new AudioMedia(file);
            if (!audioMediaObsList.contains(audioMedia)) {
                audioMediaObsList.add(audioMedia);
                AudioMediaUI audioMediaUI = new AudioMediaUI(getScene(), audioMedia);
                audioMediaUI.addUI();
            } else {
                audioMedia = null;
                // @TODO affichage message alerte.
            }
        }
        
    }
    
    public static void removeAudioMediaUI(AudioMedia audioMedia) {
        if (audioMediaObsList.contains(audioMedia)) {
            audioMediaObsList.remove(audioMedia);
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // initialize Observable List of AudioMedia
        audioMediaObsList = FXCollections.observableArrayList();
        
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
    
    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }
    
    public void setAudioTable(AudioTable audioTable) {
        this.audioTable = audioTable;
    }
    
    private Scene getScene() {
        return scene;
    }
    
    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
