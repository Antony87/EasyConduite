/*
 * Copyright (C) 2018 Antony Fons
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

import easyconduite.controllers.helpers.MainListenersHandler;
import easyconduite.exception.PersistenceException;
import easyconduite.model.EasyAudioChain;
import easyconduite.objects.ApplicationProperties;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.tools.Constants;
import easyconduite.tools.PersistenceHelper;
import easyconduite.view.AboutDialogUI;
import easyconduite.view.AudioMediaUI;
import easyconduite.view.PreferencesUI;
import easyconduite.view.TrackConfig;
import easyconduite.view.commons.UITools;
import easyconduite.view.controls.ActionDialog;
import easyconduite.view.controls.EasyFileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    private final static String MSG_OPEN_HEADER = "easyconduitecontroler.open.header";
    private final static String MSG_OPEN_CONT = "easyconduitecontroler.open.content";
    private final static String MSG_DIAG_ERROR = "dialog.error.header";
    private final static String MSG_ERROR_OPEN = "easyconduitecontroler.open.error";
    private final static String MSG_ERROR_SAVE = "easyconduitecontroler.save.error";
    private final static String MSG_WARNING_SAVE_HEADER = "dialog.warning.save.header";
    private final static String MSG_WARNING_SAVE_CONT = "dialog.warning.save.content";
    private final static String MSG_DELETE_HEADER = "audiomediaui.delete.header";
    private final static String MSG_DELETE_CONT = "audiomediaui.delete.content";
    private static final Logger LOG = LogManager.getLogger(MainController.class);

    @FXML
    StackPane mainPane;

    @FXML
    private FlowPane tableLayout;

    @FXML
    private Pane calquePane;

    private MainListenersHandler listenersHandler;

    private AudioTable audioTable;

    private EasyAudioChain nextChainingElement;

    private final ResourceBundle local;

    private final ApplicationProperties applicationProperties;

    private final List<AudioMediaUI> audioMediaViewList;

    /**
     * Map (ConcurrentHashMap) which amintains relationship between a Keybord
     * key and an AudioMediaUI.
     */
    private final Map<KeyCode, AudioMediaUI> keyCodesMap;

    /**
     * Constructor without arguments, to respect instantiating by FXML.
     */
    public MainController() {
        audioTable = new AudioTable();
        audioMediaViewList = new CopyOnWriteArrayList<>();
        keyCodesMap = new ConcurrentHashMap<>(100);
        applicationProperties = ApplicationPropertiesHelper.getInstance().getProperties();
        local = ApplicationPropertiesHelper.getInstance().getLocalBundle();
    }

    @FXML
    private void menuFileOpen(ActionEvent event) {

        if (audioMediaViewList.size() > 0) {
            Optional<ButtonType> result = ActionDialog.showConfirmation(local.getString(MSG_OPEN_HEADER), local.getString(MSG_OPEN_CONT));
            if (!result.isPresent() || result.get() == ButtonType.NO) {
                return;
            }
        }

        final FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_PROJECT).build();
        final File file = fileChooser.showOpenDialog(UITools.getWindow(mainPane));

        if (file != null) {
            // clear audiotable and childs (ui, player, etc)
            deleteProject();
            applicationProperties.setLastProjectDir(PersistenceHelper.getDirectory(file.getParentFile()));
            try {
                audioTable = null;
                audioTable = (AudioTable) PersistenceHelper.readFromFile(file, AudioTable.class, PersistenceHelper.FILE_TYPE.XML);
                UITools.updateWindowsTitle(mainPane, audioTable.getName());

                audioTable.getAudioMediaList().forEach((audioMedia) -> {
                    // add AudioMediaUI within tableLayout
                    AudioMediaUI ui = createAudioMediaView(audioMedia);
                });
                updateKeyCodeList();
            } catch (PersistenceException ex) {
                LOG.error("Error occured during opening project file[{}]", file, ex);
                ActionDialog.showWarning(local.getString(MSG_DIAG_ERROR), local.getString(MSG_ERROR_OPEN));
            }
        }
    }

    public void updateKeyCodeList() {
        LOG.trace("updateKeyCodeList called");
        if (audioMediaViewList != null) {
            keyCodesMap.clear();
            CopyOnWriteArrayList<AudioMedia> cList = new CopyOnWriteArrayList(audioTable.getAudioMediaList());
            cList.forEach((audioMedia) -> {
                if (audioMedia.getKeycode() != null) {
                    keyCodesMap.putIfAbsent(audioMedia.getKeycode(), findAudioMediaUI(audioMedia));
                }
            });
        }
        LOG.trace("keyCodeMap size {}", keyCodesMap.size());
    }

    @FXML
    private void menuFileSave(ActionEvent event) {
        try {
            if (PersistenceHelper.isFileExists(audioTable.getTablePathFile())) {
                audioTable.setUpdated(false);
                final File fileAudioTable = Paths.get(audioTable.getTablePathFile()).toFile();
                PersistenceHelper.writeToFile(fileAudioTable, audioTable, PersistenceHelper.FILE_TYPE.XML);
            } else {
                menuFileSaveAs(event);
            }
        } catch (PersistenceException ex) {
            ActionDialog.showWarning(local.getString(MSG_DIAG_ERROR), local.getString(MSG_ERROR_SAVE));
            LOG.error("An error occured", ex);
        }
    }

    @FXML
    private void menuFileSaveAs(ActionEvent event) {

        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.SAVE_AS).build();
        final File file = fileChooser.showSaveDialog(UITools.getWindow(mainPane));
        if (file != null) {
            try {
                final File checkedFile = PersistenceHelper.suffixForEcp(file);
                audioTable.setUpdated(false);
                audioTable.setName(checkedFile.getName());
                audioTable.setTablePathFile(checkedFile.getAbsolutePath());
                PersistenceHelper.writeToFile(checkedFile, audioTable, PersistenceHelper.FILE_TYPE.XML);
                applicationProperties.setLastProjectDir(PersistenceHelper.getDirectory(file.getParentFile()));
                UITools.updateWindowsTitle(mainPane, audioTable.getName());
            } catch (PersistenceException ex) {
                ActionDialog.showWarning(local.getString(MSG_DIAG_ERROR), local.getString(MSG_ERROR_SAVE));
                LOG.error("An error occured", ex);
            }
        }
    }

    @FXML
    private void menuCloseProject(ActionEvent event) {
        deleteProject();
    }

    private void deleteProject() {
        LOG.debug("deleteProject called");
        CopyOnWriteArrayList<AudioMedia> cList = new CopyOnWriteArrayList(audioTable.getAudioMediaList());
        cList.forEach((audioMedia) -> {
            this.removeChild(audioMedia);
        });
    }

    @FXML
    private void menuPreferences(ActionEvent event) {
        try {
            PreferencesUI prefsUI = new PreferencesUI(local);
            prefsUI.show();
        } catch (IOException ex) {
            LOG.debug("Error occurend ", ex);
        }
    }

    @FXML
    private void handleImportAudio(ActionEvent event) {

        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_AUDIO).build();
        File file = fileChooser.showOpenDialog(UITools.getScene(mainPane).getWindow());

        if (file != null) {
            AudioMedia audioMedia = new AudioMedia(file);
            audioTable.getAudioMediaList().add(audioMedia);
            audioTable.setUpdated(true);
            createAudioMediaView(audioMedia);
            applicationProperties.setLastImportDir(PersistenceHelper.getDirectory(file.getParentFile()));
        }
    }

    @FXML
    private void menuTrackEdit(ActionEvent event) {
        audioMediaViewList.stream().filter((AudioMediaUI ui) -> ui.isFocused()).findFirst().ifPresent((AudioMediaUI ui) -> {
            editTrack(ui);
        });
    }

    public void editTrack(AudioMediaUI audioMediaUi) {
        final TrackConfig trackConfigDialog;
        try {
            audioMediaUi.getEasyPlayer().stop();
            trackConfigDialog = new TrackConfig(audioMediaUi.getAudioMedia(), this);
            trackConfigDialog.show();
        } catch (IOException ex) {
            LOG.error("Error occurend during TrackConfigDialog construction", ex);
        }
    }

    @FXML
    private void menuTrackDelete(ActionEvent event) {
        audioMediaViewList.stream().filter((AudioMediaUI ui) -> ui.isFocused()).findFirst().ifPresent((AudioMediaUI ui) -> {
            this.deleteTrack(ui);
        });
    }

    public void deleteTrack(AudioMediaUI audioMediaUi) {
        LOG.debug("deleteTrack called");
        Optional<ButtonType> result = ActionDialog.showConfirmation(local.getString(MSG_DELETE_HEADER), local.getString(MSG_DELETE_CONT));
        if (result.get() == ButtonType.OK || result.get() == ButtonType.YES) {
            this.removeChild(audioMediaUi.getAudioMedia());
        }
    }

    private AudioMediaUI createAudioMediaView(AudioMedia audioMedia) {
        AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, MainController.this);
        audioMediaViewList.add(audioMediaUI);
        tableLayout.getChildren().add(audioMediaUI);
        LOG.debug("AudioMedia {} added to AudioTable", audioMedia);
        return audioMediaUI;
    }

    @FXML
    public void menuQuit(ActionEvent event) {

        applicationProperties.setWindowHeight((int) UITools.getScene(mainPane).windowProperty().getValue().getHeight());
        applicationProperties.setWindowWith((int) UITools.getScene(mainPane).windowProperty().getValue().getWidth());

        try {
            PersistenceHelper.writeToFile(Constants.FILE_EASYCONDUITE_PROPS, applicationProperties, PersistenceHelper.FILE_TYPE.BIN);
        } catch (PersistenceException ex) {
            java.util.logging.Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.audioTable.isClosable()) {
            Platform.exit();
        } else {
            Optional<ButtonType> response = ActionDialog.showConfirmation(local.getString(MSG_WARNING_SAVE_HEADER), local.getString(MSG_WARNING_SAVE_CONT));
            if (response.isPresent() && response.get().equals(ButtonType.YES)) {
                menuFileSave(event);
                Platform.exit();
            } else {
                Platform.exit();
            }
        }
    }

    @FXML
    private void handleKeyCodePlay(KeyEvent event) {
        if (keyCodesMap.containsKey(event.getCode())) {
            keyCodesMap.get(event.getCode()).getEasyPlayer().playPause();
        }
    }

    @FXML
    private void handlePauseAll(ActionEvent event) {
        audioMediaViewList.forEach(u -> {
            u.getEasyPlayer().pause();
        });
    }

    @FXML
    private void handleStopAll(ActionEvent event) {
        audioMediaViewList.forEach(u -> {
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
        calquePane.setMouseTransparent(true);
        // initialization listeners
        listenersHandler = new MainListenersHandler(this);
        listenersHandler.setDragAndDropFeature(tableLayout);
    }

    /**
     * Return if a keyboard keycode exists within AudioMediaUI List.
     *
     * @param keycode
     * @return
     */
    public boolean isKeyCodeExist(KeyCode keycode) {
        return keyCodesMap.containsKey(keycode);
    }

    private AudioMediaUI findAudioMediaUI(AudioMedia audioMedia) {
        return audioMediaViewList.stream().filter(ui -> ui.getAudioMedia().equals(audioMedia)).findFirst().get();
    }

    public void orderAudioTable() {
        tableLayout.getChildren().filtered((t) -> {
            return t instanceof AudioMediaUI;
        }).forEach((t) -> {
            ((AudioMediaUI) t).getAudioMedia().setIndex(tableLayout.getChildren().indexOf(t));
        });
        audioTable.getAudioMediaList().sort((o1, o2) -> {
            return o1.getIndex() - o2.getIndex();
        });
        audioTable.setUpdated(true);
    }

    public Pane getCalquePane() {
        return calquePane;
    }

    public AudioTable getAudioTable() {
        return audioTable;
    }

    @Override
    public void setNext(EasyAudioChain next) {
        nextChainingElement = next;
    }

    @Override
    public void updateFromAudioMedia(AudioMedia media) {
        // trouver le AudioMediaUI associé à l'AudioMedia
        final AudioMediaUI ui = findAudioMediaUI(media);
        updateKeyCodeList();
        // Set up next chaining class to AudioMediaUi.
        this.setNext(ui);
        LOG.trace("After config, AudioMedia is {}", media);
        nextChainingElement.updateFromAudioMedia(media);
    }

    @Override
    public void removeChild(AudioMedia audioMedia) {
        LOG.debug("Remove AudioMedia[{}]", audioMedia);
        final AudioMediaUI ui = findAudioMediaUI(audioMedia);
        this.setNext(ui);
        nextChainingElement.removeChild(audioMedia);
        tableLayout.getChildren().remove(ui);
        audioMediaViewList.remove(ui);
        audioTable.getAudioMediaList().remove(audioMedia);
        updateKeyCodeList();
        LOG.trace("New audioMediaUI list size is {} and AudioMediaList size is {}", audioMediaViewList.size(), audioTable.getAudioMediaList().size());
    }
}
