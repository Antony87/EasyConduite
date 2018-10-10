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

import easyconduite.controllers.helpers.ActionsMenuFile;
import easyconduite.controllers.helpers.MediaUIDragAndDrop;
import easyconduite.exception.PersistenceException;
import easyconduite.model.EasyAudioChain;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import easyconduite.objects.EasyconduiteProperty;
import easyconduite.ui.AboutDialogUI;
import easyconduite.ui.AudioMediaUI;
import easyconduite.ui.TrackConfigDialogUI;
import easyconduite.ui.commons.ActionDialog;
import easyconduite.ui.commons.EasyconduiteUITools;
import easyconduite.ui.controls.Chrono;
import easyconduite.ui.controls.EasyFileChooser;
import easyconduite.util.Constants;
import easyconduite.util.EasyConduitePropertiesHandler;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class implements a controller for audio table and AudioMediUI behaviors.
 *
 * @author A Fons
 */
public class MainController extends StackPane implements Initializable, EasyAudioChain {
    private static final Logger LOG = LogManager.getLogger(MainController.class);

    @FXML
    StackPane mainPane;


    @FXML
    private Label timer;

    @FXML
    private ToggleButton chronobutton;

    @FXML
    private FlowPane tableLayout;


    @FXML
    Pane calquePane;


//    public void setTableLayout(FlowPane tableLayout) {
//        this.tableLayout = tableLayout;
//    }
    @FXML
    private Label timeLineLabel;

    private Chrono chrono;

    private AudioTable audioTable;

    private EasyAudioChain nextChain;

    private ResourceBundle bundle;

    private final EasyconduiteProperty userdatas;

    private final List<AudioMediaUI> audioMediaUIs;


    /**
     * Constructor without arguments, to respect instantiating by FXML.
     */
    public MainController() {
        audioTable = new AudioTable();
        audioMediaUIs = new ArrayList<>();
        userdatas = EasyConduitePropertiesHandler.getInstance().getProperties();
    }
    public StackPane getMainPane() {
        return mainPane;
    }
    public FlowPane getTableLayout() {
        return tableLayout;
    }
    public Pane getCalquePane() {
        return calquePane;
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
    private void menuOpenFile(ActionEvent event) {
        LOG.debug("handleOpen called");
        ActionsMenuFile.openFile(audioMediaUIs, this, audioTable);

//        if (audioMediaUIs.size() > 0) {
//            Optional<ButtonType> result = ActionDialog.showConfirmation(bundle.getString("easyconduitecontroler.open.header"), bundle.getString("easyconduitecontroler.open.content"));
//            if (!result.isPresent() || result.get() == ButtonType.NO) {
//                return;
//            }
//        }
//
//        final FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_PROJECT).build();
//        fileChooser.setInitialDirectory(userdatas.getLastProjectDir());
//        final File file = fileChooser.showOpenDialog(EasyconduiteUITools.getWindow(mainPane));
//
//        if (file != null) {
//            // clear audiotable and childs (ui, player, etc)
//            menuCloseProject(event);
//            userdatas.setLastProjectDir(PersistenceUtil.getDirectory(file.getParentFile()));
//            try {
//                audioTable = (AudioTable) PersistenceUtil.readFromFile(file, AudioTable.class, PersistenceUtil.FILE_TYPE.XML);
//                EasyconduiteUITools.updateWindowsTitle(EasyconduiteUITools.getScene(mainPane), "EasyConduite " + bundle.getString("easyconduite.version") + " : " + audioTable.getName());
//                timeLineLabel.setText(bundle.getString("easyconduitecontroler.open.loading"));
//
//                Platform.runLater(() -> {
//                    audioTable.getAudioMediaList().forEach((audioMedia) -> {
//                        addAudioMediaUI(audioMedia);
//                    });
//                    timeLineLabel.setText("");
//                });
//            } catch (PersistenceException ex) {
//                LOG.error("Error occured during opening project file[{}]", file, ex);
//                ActionDialog.showWarning(bundle.getString("dialog.error.header"), bundle.getString("easyconduitecontroler.open.error"));
//            }
//        }
    }

    @FXML
    private void menuSaveFile(ActionEvent event) {
        LOG.debug("menuSaveFile called");
        ActionsMenuFile.saveFile(audioTable, this);
//        try {
//            if (PersistenceUtil.isFileExists(audioTable.getTablePathFile())) {
//                audioTable.setUpdated(false);
//                final File fileAudioTable = Paths.get(audioTable.getTablePathFile()).toFile();
//                PersistenceUtil.writeToFile(fileAudioTable, audioTable, PersistenceUtil.FILE_TYPE.XML);
//            } else {
//                menuSaveFileAs(event);
//            }
//        } catch (PersistenceException ex) {
//            ActionDialog.showWarning(bundle.getString("dialog.error.header"), bundle.getString("easyconduitecontroler.save.error"));
//            LOG.error("An error occured", ex);
//        }
    }

    @FXML
    private void menuSaveFileAs(ActionEvent event) {
        LOG.debug("Save as.. called");
        ActionsMenuFile.saveAsFile(audioTable, this);

//        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.SAVE_AS).build();
//        fileChooser.setInitialDirectory(userdatas.getLastProjectDir());
//        final File file = fileChooser.showSaveDialog(EasyconduiteUITools.getWindow(mainPane));
//        if (file != null) {
//            try {
//                final File checkedFile = PersistenceUtil.suffixForEcp(file);
//                audioTable.setUpdated(false);
//                audioTable.setName(checkedFile.getName());
//                audioTable.setTablePathFile(checkedFile.getAbsolutePath());
//                PersistenceUtil.writeToFile(checkedFile, audioTable, PersistenceUtil.FILE_TYPE.XML);
//
//                userdatas.setLastProjectDir(PersistenceUtil.getDirectory(file.getParentFile()));
//                
//                EasyconduiteUITools.updateWindowsTitle(EasyconduiteUITools.getScene(mainPane), "EasyConduite " + bundle.getString("easyconduite.version") + " : " + audioTable.getName());
//            } catch (PersistenceException ex) {
//                ActionDialog.showWarning(bundle.getString("dialog.error.header"), bundle.getString("easyconduitecontroler.save.error"));
//                LOG.error("An error occured", ex);
//            }
//        }
    }

    @FXML
    private void menuCloseProject(ActionEvent event) {
        LOG.debug("handleCloseTab called");
        ActionsMenuFile.closeProject(audioTable, this);
    }

    @FXML
    private void menuPreferences(ActionEvent event) {
        LOG.debug("handlePreferences called");
        ActionsMenuFile.openPreferences();
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
        fileChooser.setInitialDirectory(userdatas.getLastImportDir());
        File file = fileChooser.showOpenDialog(EasyconduiteUITools.getScene(mainPane).getWindow());

        if (file != null) {
            AudioMedia audioMedia = new AudioMedia(file);
            audioTable.getAudioMediaList().add(audioMedia);
            audioTable.setUpdated(true);
            addAudioMediaUI(audioMedia);
            userdatas.setLastImportDir(PersistenceUtil.getDirectory(file.getParentFile()));
        }
    }

    @FXML
    private void menuTrackEdit(ActionEvent event) {
        audioMediaUIs.stream().filter((AudioMediaUI ui) -> ui.isFocused()).findFirst().ifPresent((AudioMediaUI ui) -> {
            editTrack(ui);
        });
    }

    public void editTrack(AudioMediaUI audioMediaUi) {
        final TrackConfigDialogUI trackConfigDialog;
        try {
            trackConfigDialog = new TrackConfigDialogUI(audioMediaUi.getAudioMedia(), this);
            trackConfigDialog.show();
        } catch (IOException ex) {
            LOG.error("Error occurend during TrackConfigDialog construction", ex);
        }
    }

    @FXML
    private void menuTrackDelete(ActionEvent event) {
        audioMediaUIs.stream().filter((AudioMediaUI ui) -> ui.isFocused()).findFirst().ifPresent((AudioMediaUI ui) -> {
            deleteTrack(ui);
        });
    }

    public void deleteTrack(AudioMediaUI audioMediaUi) {
        Optional<ButtonType> result = ActionDialog.showConfirmation(bundle.getString("audiomediaui.delete.header"), bundle.getString("audiomediaui.delete.content"));
        if (result.get() == ButtonType.OK || result.get() == ButtonType.YES) {
            removeAudioMedia(audioMediaUi.getAudioMedia());
        }
    }

    public void addAudioMediaUI(AudioMedia audioMedia) {
        AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, MainController.this);
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

        userdatas.setWindowHeight((int) EasyconduiteUITools.getScene(mainPane).windowProperty().getValue().getHeight());
        userdatas.setWindowWith((int) EasyconduiteUITools.getScene(mainPane).windowProperty().getValue().getWidth());

        try {
            PersistenceUtil.writeToFile(Constants.FILE_EASYCONDUITE_PROPS, userdatas, PersistenceUtil.FILE_TYPE.BIN);
        } catch (PersistenceException ex) {
            LOG.error("Error occured during easyconduite properties saving", ex);
        }
        if (this.audioTable.isClosable()) {
            Platform.exit();
        } else {
            Optional<ButtonType> response = ActionDialog.showConfirmation(bundle.getString("dialog.warning.save.header"), bundle.getString("dialog.warning.save.content"));
            if (response.isPresent() && response.get().equals(ButtonType.YES)) {
                menuSaveFile(event);
                Platform.exit();
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
            AboutDialogUI aboutDialog = new AboutDialogUI();
        } catch (IOException ex) {
            LOG.error("An error occured", ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOG.debug("Controler initialisation");
        chrono = new Chrono(timer);
        bundle = rb;
        calquePane.setMouseTransparent(true);

        MediaUIDragAndDrop.activateDrangAndDrop(tableLayout);

        LOG.trace("Bundle {} loaded", rb.getLocale());
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
