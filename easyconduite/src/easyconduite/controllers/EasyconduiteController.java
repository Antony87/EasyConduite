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
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author V902832
 */
public class EasyconduiteController implements Initializable {

    /**
     * Zone texte affichant le timer du spectacle.
     */
    @FXML
    private Label timer;

    /**
     * Togglebutton de play/stop du timer
     */
    @FXML
    private ToggleButton chronobutton;

    /**
     * HBox de la table des pistes son.
     */
    @FXML
    private HBox table;

    private Timeline timeline;

    private Stage stage;

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
    private void handlePlayPauseAudio(MouseEvent event) {
        // get source of event and parent.
        if (event.getSource().getClass() == Button.class) {
            final Button button = (Button) event.getSource();
            final Pane pane = (Pane) button.getParent();
            final AudioMedia audioMedia = AudioTable.getInstance()
                    .getAudioMedia(new Integer(pane.getId()));
            Status status = audioMedia.getStatus();
            if (status == Status.PLAYING) {
                audioMedia.pause();
            }
            if (status == Status.PAUSED || status == Status.STOPPED
                    || status == Status.READY) {
                audioMedia.play();
            }
        }

    }

    @FXML
    private void handleDeleteTrack(MouseEvent event) {
        if (event.getSource().getClass() == Button.class) {
            final Button button = (Button) event.getSource();
            final Pane pane = (Pane) button.getParent();
            AudioTable.getInstance().deleteAudioMedia(new Integer(pane.getId()));
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
                AudioMediaUI audioMediaUI = new AudioMediaUI(getStage().getScene(), null);
            }
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

        // TODO
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

}
