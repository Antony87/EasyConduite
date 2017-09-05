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

import easyconduite.exception.PersistenceException;
import easyconduite.model.EasyAudioChain;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import easyconduite.objects.UserData;
import easyconduite.ui.AboutDialog;
import easyconduite.ui.AudioMediaUI;
import easyconduite.ui.Chrono;
import easyconduite.ui.commons.ActionDialog;
import easyconduite.util.Constants;
import easyconduite.util.EasyFileChooser;
import easyconduite.util.PersistenceUtil;
import easyconduite.util.UserDataHandler;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class implements a controller for audio table and AudioMediUI behaviors.
 *
 * @author A Fons
 */
public class EasyconduiteController extends StackPane implements Initializable, EasyAudioChain {

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

    private final List<AudioMediaUI> audioMediaUIs;

    private EasyAudioChain nextChain;

    /**
     * Constructor without arguments, to respect instantiating by FXML.
     */
    public EasyconduiteController() {
        audioTable = new AudioTable();
        audioMediaUIs = new ArrayList<>();
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
    private void handleOpen(ActionEvent event) {
        LOG.debug("Menu Open called");

        if (audioMediaUIs.size() > 0) {
            Optional<ButtonType> result = ActionDialog.showConfirmation("Charger un nouveau fichier écrasera l'existant.", "Voulez-vous continuer ?");
            if (!result.isPresent() || result.get() == ButtonType.CANCEL) {
                return;
            }
        }
        final FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_PROJECT).build();
        final File file = fileChooser.showOpenDialog(getMyScene().getWindow());

        if (file != null) {
            // clear audiotable and childs (ui, player, etc)
            handleCloseTab(event);
            try {
                audioTable = (AudioTable)PersistenceUtil.readFromFile(file, AudioTable.class, PersistenceUtil.FILE_TYPE.XML);

                Stage primStage = (Stage) getMyScene().getWindow();
                primStage.setTitle("EasyConduite 1.2 -- Projet : " + audioTable.getName());

            } catch (PersistenceException ex) {
                LOG.error("Error occured during opening project file[{}]", file, ex);
                ActionDialog.showWarning("Une erreur est survenue", "Erreur durant l'ouverture du projet");
            }
            audioTable.getAudioMediaList().stream().forEach((audioMedia) -> {
                addAudioMediaUI(audioMedia);
            });
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        LOG.debug("Menu save called");

        try {
            if (PersistenceUtil.isFileExists(audioTable.getTablePathFile())) {
                File fileAudioTable = Paths.get(audioTable.getTablePathFile()).toFile();
                //PersistenceUtil.saveAudioTable(fileAudioTable, audioTable);
                PersistenceUtil.writeToFile(fileAudioTable, audioTable, PersistenceUtil.FILE_TYPE.XML);
            } else {
                handleSaveAs(event);
            }
        } catch (PersistenceException ex) {
            ActionDialog.showWarning("Une erreur est survenu", "Erreur durant l'enregistrement du projet");
            LOG.error("An error occured", ex);
        }
    }

    @FXML
    private void handleSaveAs(ActionEvent event) {
        LOG.debug("Menu save as.. called");

        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.SAVE_AS).build();
        File file = fileChooser.showSaveDialog(getMyScene().getWindow());
        if (file != null) {
            try {
                //PersistenceUtil.saveAudioTable(file, audioTable);
                PersistenceUtil.writeToFile(file, audioTable, PersistenceUtil.FILE_TYPE.XML);
            } catch (PersistenceException ex) {
                ActionDialog.showWarning("Une erreur est survenu", "Erreur durant l'enregistrement du projet");
                LOG.error("An error occured", ex);
            }
        }
    }

    @FXML
    private void handleCloseTab(ActionEvent event) {
        LOG.debug("handleCloseTab called");
        List<AudioMedia> audioMediaList = audioTable.getAudioMediaList();
        while (audioMediaList.iterator().hasNext()) {
            AudioMedia audioMedia = audioMediaList.iterator().next();
            this.removeChilds(audioMedia);
        }
    }

    /**
     * Cette méthode est appellée par l'événement du menu ajout d'une média dans
     * la table audio.
     *
     * @param event
     */
    @FXML
    private void handleImportAudio(ActionEvent event) {

        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_AUDIO).build();
        File file = fileChooser.showOpenDialog(getMyScene().getWindow());

        if (file != null) {
            AudioMedia audioMedia = new AudioMedia(file);
            audioTable.getAudioMediaList().add(audioMedia);
            addAudioMediaUI(audioMedia);
        }
    }

    private void addAudioMediaUI(AudioMedia audioMedia) {
        AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, EasyconduiteController.this);
        audioMediaUIs.add(audioMediaUI);
        tableLayout.getChildren().add(audioMediaUI);
        LOG.debug("AudioMedia {} added to AudioTable", audioMedia);
        //this.updateFromAudioMedia(audioMedia);
    }

    public void removeAudioMedia(AudioMedia audioMedia) {
        LOG.debug("AudioMedia {} removed from AudioTable", audioMedia);
        this.removeChilds(audioMedia);
    }

    @FXML
    public void handleQuit(ActionEvent event) {

        final UserData userdatas = UserDataHandler.getInstance().getUserData();
        userdatas.setWindowHeight(getMyScene().heightProperty().intValue());
        userdatas.setWindowWith(getMyScene().widthProperty().intValue());
        try {
            PersistenceUtil.writeToFile(Constants.FILE_USER_DATA.toFile(), userdatas, PersistenceUtil.FILE_TYPE.BIN);
        } catch (PersistenceException ex) {
                LOG.error("Error occured", ex);
        }
        if (this.audioTable.isClosable()) {
            Platform.exit();
        } else {
            Optional<ButtonType> response = ActionDialog.showConfirmation("Enregistrer le projet", "Le projet n'est pas sauvegardé.\rVoulez-vous l'enregistrer ?");
            if (response.isPresent() && response.get().equals(ButtonType.YES)) {
                handleSave(event);
            } else {
                Platform.exit();
            }
        }
    }

    @FXML
    private void handleKeyCodePlay(KeyEvent event) {
        audioMediaUIs.stream().filter(ui -> event.getCode() == ui.getKeycode()).findFirst().ifPresent((AudioMediaUI t) -> {
            t.getEasyPlayer().playPause();
            LOG.trace("Key {} was pressed playpause {}", event.getCode(), t);
        });
    }

    @FXML
    private void handlePauseAll(ActionEvent event) {
        audioMediaUIs.forEach(u -> {
            u.getEasyPlayer().pause();
        });
    }

    @FXML
    private void handleStopAll(ActionEvent event) {
        audioMediaUIs.forEach(u -> {
            u.getEasyPlayer().stop();
        });
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
        return audioMediaUIs.stream().anyMatch(u -> keycode == u.getKeycode());
    }

    private Scene getMyScene() {
        return (Scene) this.mainPane.getScene();
    }

    private AudioMediaUI findAudioMediaUI(AudioMedia audioMedia) {
        return audioMediaUIs.stream().filter(ui -> ui.getAudioMedia().equals(audioMedia)).findFirst().get();
    }

    @Override
    public void setNext(EasyAudioChain next) {
        nextChain = next;
    }

    @Override
    public void updateFromAudioMedia(AudioMedia media) {
        // trouver le AudioMediaUI associé à l'AudioMedia
        final AudioMediaUI audioMediaUI = findAudioMediaUI(media);
        // initialisation de la chaine.
        this.setNext(audioMediaUI);
        //audioMediaUI.setNext(audioMediaUI.getEasyPlayer());
        LOG.trace("After config, AudioMedia is {}", media);
        nextChain.updateFromAudioMedia(media);
    }

    @Override
    public void removeChilds(AudioMedia audioMedia) {
        LOG.debug("Remove AudioMedia[{}]", audioMedia);
        final AudioMediaUI ui = findAudioMediaUI(audioMedia);
        setNext(ui);
        nextChain.removeChilds(audioMedia);
        tableLayout.getChildren().remove(ui);
        audioMediaUIs.remove(ui);
        audioTable.getAudioMediaList().remove(audioMedia);
        LOG.trace("New audioMediaUI list size is {} and AudioMediaList size is {}", audioMediaUIs.size(), audioTable.getAudioMediaList().size());
    }
}
