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
import easyconduite.objects.ApplicationProperties;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.project.EasyTable;
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.tools.Constants;
import easyconduite.tools.PersistenceHelper;
import easyconduite.view.AboutDialogUI;
import easyconduite.view.AudioMediaUI;
import easyconduite.view.PreferencesUI;
import easyconduite.view.TrackConfig;
import easyconduite.view.controls.ActionDialog;
import easyconduite.view.controls.EasyconduitePlayer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import easyconduite.model.ChainingUpdater;
import easyconduite.objects.AudioTableWrapper;
import javafx.scene.control.Label;

/**
 * This class implements a controller for audio table and AudioMediUI behaviors.
 *
 * @author A Fons
 */
public class MainController extends StackPane implements Initializable, ChainingUpdater {

    private final static String MSG_OPEN_HEADER = "easyconduitecontroler.open.header";
    private final static String MSG_OPEN_CONT = "easyconduitecontroler.open.content";
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
    private Menu openRecent;

    private MainListenersHandler listenersHandler;

    private ChainingUpdater nextUpdater;

    private final ResourceBundle local;

    private final List<AudioMediaUI> audioMediaViewList;

    public List<AudioMediaUI> getAudioMediaViewList() {
        return audioMediaViewList;
    }
    
    private final Map<UUID, EasyconduitePlayer> playersMap;

    /**
     * Map (ConcurrentHashMap) which amintains relationship between a Keybord
     * key and an AudioMediaUI.
     */
    private final Map<KeyCode, AudioMediaUI> keyCodesMap;

    /**
     * Constructor without arguments, to respect instantiating by FXML.
     */
    public MainController() {
        audioMediaViewList = new CopyOnWriteArrayList<>();
        keyCodesMap = new ConcurrentHashMap<>(100);
        playersMap = new ConcurrentHashMap<>(100);
        local = ApplicationPropertiesHelper.getInstance().getLocalBundle();
    }

    @FXML
    private void menuFileOpen(ActionEvent event) {
        if (!isOkForDelete()) {
            return;
        }
        openFile(null);
    }

    private void openFile(File file) {
        deleteProject();
        EasyTable easyTable = AudioTableWrapper.getInstance().getFromFile(file);
        easyTable.getAudioMediaList().forEach((audioMedia) -> {
            AudioMediaUI ui = this.createAudioMediaView(audioMedia);
        });
        updateKeyCodeList();
        updatePlayersMap();
    }

    public void updatePlayersMap() {
        LOG.trace("updatePlayersMap called");
        if (audioMediaViewList != null) {
            playersMap.clear();
            CopyOnWriteArrayList<AudioMediaUI> cList = new CopyOnWriteArrayList(audioMediaViewList);
            cList.forEach((AudioMediaUI t) -> {
                playersMap.put(t.getAudioMedia().getUniqueId(), t.getEasyPlayer());
            });
        }
        LOG.trace("playersMap size {}", playersMap.size());
    }

    @FXML
    private void menuFileSave(ActionEvent event) {
        AudioTableWrapper.getInstance().saveToFile();
    }

    @FXML
    private void menuFileSaveAs(ActionEvent event) {
        AudioTableWrapper.getInstance().saveAsToFile();
    }

    @FXML
    private void menuCloseProject(ActionEvent event) {
        deleteProject();
    }

    private void deleteProject() {
        LOG.debug("deleteProject called");
        audioMediaViewList.forEach((AudioMediaUI t) -> {
            this.removeChild(t);
        });
        this.updateKeyCodeList();
        this.updatePlayersMap();
        AudioTableWrapper.getInstance().clearAudioTable();
    }

    private boolean isOkForDelete() {
        if (audioMediaViewList.size() > 0) {
            Optional<ButtonType> result = ActionDialog.showConfirmation(local.getString(MSG_OPEN_HEADER), local.getString(MSG_OPEN_CONT));
            if (!result.isPresent() || result.get() == ButtonType.NO) {
                return false;
            }
        }
        return true;
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
        AudioMedia audioMedia = AudioTableWrapper.getInstance().getAudioFromFile();
        if (audioMedia != null) {
            this.createAudioMediaView(audioMedia);
        }
        updatePlayersMap();
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
            this.removeChild(audioMediaUi);
        }
        this.updateKeyCodeList();
        this.updatePlayersMap();
    }

    @FXML
    public void menuQuit(ActionEvent event) {

        ApplicationProperties appProps = ApplicationPropertiesHelper.getInstance().getProperties();
        // get window sizes
        int width = (int) appProps.getCurrentWindow().getWidth();
        int height = (int) appProps.getCurrentWindow().getHeight();
        appProps.setWindowHeight(height);
        appProps.setWindowWith(width);
        try {
            PersistenceHelper.writeToFile(Constants.FILE_EASYCONDUITE_PROPS, appProps, PersistenceHelper.FILE_TYPE.BIN);
        } catch (PersistenceException e) {
            LOG.error("Error occurend during writing ApplicationProperties", e);
        }
        EasyTable easyTable = AudioTableWrapper.getInstance().getEasyTable();
        if (!easyTable.isClosable()) {
            Optional<ButtonType> response = ActionDialog.showConfirmation(local.getString(MSG_WARNING_SAVE_HEADER), local.getString(MSG_WARNING_SAVE_CONT));
            if (response.isPresent() && response.get().equals(ButtonType.YES)) {
                menuFileSave(event);
            }
        }
        Platform.exit();
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
        // initialization listeners
        listenersHandler = new MainListenersHandler(this);
        listenersHandler.setDragAndDropFeature(tableLayout);
        
        ApplicationProperties appProps = ApplicationPropertiesHelper.getInstance().getProperties();
               
        if (appProps.getLastFileProject() != null) {
            MenuItem recentFileMenuItem = new MenuItem(appProps.getLastFileProject().toString());
            openRecent.getItems().add(recentFileMenuItem);
            recentFileMenuItem.setOnAction((event) -> {
                if (!isOkForDelete()) {
                    return;
                }
                openFile(appProps.getLastFileProject());
            });
        }        
    }

    public FlowPane getTableLayout() {
        return tableLayout;
    }

    private AudioMediaUI createAudioMediaView(AudioMedia audioMedia) {
        AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia, MainController.this);
        audioMediaViewList.add(audioMediaUI);
        tableLayout.getChildren().add(audioMediaUI);
        LOG.debug("AudioMedia {} added to EasyTable", audioMedia);
        return audioMediaUI;
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

    public void updateKeyCodeList() {
        LOG.trace("updateKeyCodeList called");
        EasyTable easyTable = AudioTableWrapper.getInstance().getEasyTable();
        if (audioMediaViewList != null) {
            keyCodesMap.clear();
            CopyOnWriteArrayList<AudioMedia> cList = new CopyOnWriteArrayList(easyTable.getAudioMediaList());
            cList.forEach((audioMedia) -> {
                if (audioMedia.getKeycode() != null) {
                    keyCodesMap.putIfAbsent(audioMedia.getKeycode(), findAudioMediaUI(audioMedia));
                }
            });
        }
        LOG.trace("keyCodeMap size {}", keyCodesMap.size());
    }

    public EasyconduitePlayer getByUUID(UUID uuid) {
        return playersMap.get(uuid);
    }

    @Override
    public void setNext(ChainingUpdater next) {
        nextUpdater = next;
    }

    @Override
    public void updateFromAudioMedia(AudioMedia media) {
        // trouver le AudioMediaUI associé à l'AudioMedia
        final AudioMediaUI ui = findAudioMediaUI(media);
        // Set up next chaining class to AudioMediaUi.
        this.setNext(ui);
        LOG.trace("After config, AudioMedia is {}", media);
        nextUpdater.updateFromAudioMedia(media);
    }

    @Override
    public void removeChild(ChainingUpdater audioMediaView) {
        LOG.debug("Remove AudioMediaUI[{}]", audioMediaView);
        AudioMediaUI audioUI = (AudioMediaUI) audioMediaView;
        AudioMedia audioMedia = audioUI.getAudioMedia();

        // remove whithin childs within the chain
        this.setNext(audioUI);
        nextUpdater.removeChild(audioMediaView);

        // remove from local list
        tableLayout.getChildren().remove((AudioMediaUI) audioMediaView);
        audioMediaViewList.remove((AudioMediaUI) audioMediaView);
        // remove within EasyTable
        AudioTableWrapper.getInstance().removeAudioMedia(audioMedia);
        //AudioTableHelper.cleanChilds(audioTable);
        LOG.trace("New audioMediaUI list size is {} and AudioMediaList size is {}", audioMediaViewList.size(), AudioTableWrapper.getInstance().getEasyTable().getAudioMediaList().size());
    }

}
