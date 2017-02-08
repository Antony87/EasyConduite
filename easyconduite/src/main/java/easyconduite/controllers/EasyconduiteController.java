/*
 * Copyright (C) 2017 Antony Fons
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package easyconduite.controllers;

import easyconduite.model.AudioConfigChain;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import easyconduite.ui.AboutDialog;
import easyconduite.ui.ActionDialog;
import easyconduite.ui.AudioMediaUI;
import easyconduite.ui.Chrono;
import easyconduite.util.PersistenceUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class implements a controller for audio table and AudioMediUI behaviors.
 *
 * @author A Fons
 */
public class EasyconduiteController extends StackPane implements Initializable, AudioConfigChain {

    static final Logger LOG = LogManager.getLogger(EasyconduiteController.class);

    @FXML
    StackPane mainPane;

    @FXML
    private Label timer;

    @FXML
    private ToggleButton chronobutton;

    @FXML
    private FlowPane tableLayout;

    private Chrono chrono;

    private AudioTable audioTable;

    private final List<AudioMediaUI> mediaUIList;

    private AudioConfigChain nextChain;

    /**
     * Constructor without arguments, to respect instantiating by FXML.
     */
    public EasyconduiteController() {
        audioTable = new AudioTable();
        mediaUIList = new ArrayList<>();
    }

    @FXML
    private void handleRazChrono(ActionEvent event) {
        chrono.raz();
        chronobutton.setSelected(false);
    }

    @FXML
    private void handleMouseAction(MouseEvent event) {
        if (!chronobutton.isSelected()) {
            chrono.pause();
        } else {
            chrono.play();
        }
    }

    @FXML
    private void handleFichierOuvrir(ActionEvent event) {

        File file = null;
        if (mediaUIList.size() > 0) {

            Optional<ButtonType> result = ActionDialog.showConfirmation("Charger un nouveau fichier écrasera l'existant.", "Voulez-vous continuer ?");
            if (result.get() == ButtonType.OK) {
                file = PersistenceUtil.getOpenProjectFile(this.getMyScene());
            }
        } else if (mediaUIList.isEmpty()) {
            file = PersistenceUtil.getOpenProjectFile(this.getMyScene());
        }
        if (file != null) {
            audioTable = PersistenceUtil.open(file);
            mediaUIList.clear();
            tableLayout.getChildren().clear();
            audioTable.getAudioMediaList().stream().forEach((audioMedia) -> {
                addAudioMediaUI(audioMedia);
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
        File file = PersistenceUtil.getOpenAudioFile(this.getMyScene());
        if (file != null) {
            AudioMedia audioMedia = new AudioMedia(file);
            audioTable.getAudioMediaList().add(audioMedia);
            addAudioMediaUI(audioMedia);
        }
    }

    private void addAudioMediaUI(AudioMedia audioMedia) {
        AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, EasyconduiteController.this);
        mediaUIList.add(audioMediaUI);
        tableLayout.getChildren().add(audioMediaUI);
        LOG.debug("AudioMedia {} added to AudioTable", audioMedia);
        this.chainConfigure(audioMedia);
    }

    public void removeAudioMedia(AudioMedia audioMedia, AudioMediaUI ui) {
        ui.getEasyPlayer().stop();
        ui.getEasyPlayer().getPlayer().dispose();
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

        File file = PersistenceUtil.getSaveProjectFile(this.getMyScene());
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
        chrono = new Chrono(timer);
    }

    /**
     * Return if a keyboard keycode exists within AudioMediaUI List.
     *
     * @param keycode
     * @return
     */
    public boolean isKeyCodeExist(KeyCode keycode) {
        return mediaUIList.stream().anyMatch(u -> keycode == u.getKeycode());
    }

    private Scene getMyScene() {
        return (Scene) this.mainPane.getScene();
    }

    @Override
    public void setNext(AudioConfigChain next) {
        nextChain = next;
    }

    @Override
    public void chainConfigure(AudioMedia media) {
        // trouver le AudioMediaUI associé à l'AudioMedia
        AudioMediaUI audioMediaUI = mediaUIList.stream().filter(ui -> ui.getAudioMedia().equals(media)).findFirst().get();
        // initialisation de la chaine.
        this.setNext(audioMediaUI);
        audioMediaUI.setNext(audioMediaUI.getEasyPlayer());
        LOG.trace("After config, AudioMedia is {}", media);
        nextChain.chainConfigure(media);
    }

}
