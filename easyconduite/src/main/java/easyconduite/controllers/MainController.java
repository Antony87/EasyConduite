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

import easyconduite.controllers.helpers.FileHelper;
import easyconduite.controllers.helpers.MainListenersHandler;
import easyconduite.exception.EasyconduiteException;
import easyconduite.model.ChainingUpdater;
import easyconduite.objects.ApplicationProperties;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTableWrapper;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.project.EasyTable;
import easyconduite.objects.project.MediaProject;
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.Labels;
import easyconduite.view.AboutDialogUI;
import easyconduite.view.AudioMediaUI;
import easyconduite.view.PreferencesUI;
import easyconduite.view.TrackConfig;
import easyconduite.view.controls.ActionDialog;
import easyconduite.view.controls.EasyconduitePlayer;
import easyconduite.view.controls.FileChooserControl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class implements a controller for audio table and AudioMediUI behaviors.
 *
 * @author A Fons
 */
public class MainController extends StackPane implements Initializable, ChainingUpdater {


    private static final Logger LOG = LogManager.getLogger(MainController.class);
    private final ResourceBundle local;
    private final List<AudioMediaUI> audioMediaViewList;
    private final Map<UUID, EasyconduitePlayer> playersMap;
    private MediaProject project;
    /**
     * Map (ConcurrentHashMap) which amintains relationship between a Keybord
     * key and an AudioMediaUI.
     */
    private final Map<KeyCode, AudioMediaUI> keyCodesMap;
    @FXML
    StackPane mainPane;
    @FXML
    private FlowPane tableLayout;
    @FXML
    private Menu openRecent;
    private MainListenersHandler listenersHandler;
    private ChainingUpdater nextUpdater;

    /**
     * Constructor without arguments, to respect instantiating by FXML.
     */
    public MainController() throws EasyconduiteException {

        project = new MediaProject();
        audioMediaViewList = new CopyOnWriteArrayList<>();
        keyCodesMap = new ConcurrentHashMap<>(100);
        playersMap = new ConcurrentHashMap<>(100);
        local = EasyConduitePropertiesHandler.getInstance().getLocalBundle();

    }

    public List<AudioMediaUI> getAudioMediaViewList() {
        return audioMediaViewList;
    }

    @FXML
    private void menuFileOpen(ActionEvent event) throws EasyconduiteException {
        if (!isOkForDelete()) {
            return;
        }
        FileHelper openHelper = new FileHelper(this);
        project = openHelper.getProject();

        openProject(null);
        FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_PROJECT).build();
        File file = fileChooser.showOpenDialog(null);

    }

    private void openProject(File file) {
        deleteProject();
        EasyTable easyTable = AudioTableWrapper.getInstance().getFromFile(file);
        //FIXME
        easyTable.getAudioMediaList().forEach((audioMedia) -> {
            //AudioMediaUI ui = this.createAudioMediaView(audioMedia);
        });
        updateKeyCodeList();
        //updatePlayersMap();
    }

    @Deprecated
//    public void updatePlayersMap() {
//        //TODO to delete
//        LOG.trace("updatePlayersMap called");
//        if (audioMediaViewList != null) {
//            playersMap.clear();
//            CopyOnWriteArrayList<AudioMediaUI> cList = new CopyOnWriteArrayList(audioMediaViewList);
//            cList.forEach((AudioMediaUI t) -> {
//                playersMap.put(t.getAudioMedia().getUniqueId(), t.);
//            });
//        }
//        LOG.trace("playersMap size {}", playersMap.size());
//    }

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
            //this.removeChild(t);
        });
        this.updateKeyCodeList();
        //this.updatePlayersMap();
        AudioTableWrapper.getInstance().clearAudioTable();
    }

    private boolean isOkForDelete() {
        if (audioMediaViewList.size() > 0) {
            Optional<ButtonType> result = ActionDialog.showConfirmation(local.getString(Labels.MSG_OPEN_HEADER), local.getString(Labels.MSG_OPEN_CONT));
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
    private void menuImportAudio(ActionEvent event) throws EasyconduiteException {

        final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_AUDIO).build();
        final File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            final AudioVideoMedia audioVideoMedia = (AudioVideoMedia) MediaFactory.getAudioVisualMedia(file);
            AudioMediaUI audioMediaUI = new AudioMediaUI(audioVideoMedia);
            // recuperation index de la view sur le layout
            tableLayout.getChildren().add(audioMediaUI);
            int indexView = tableLayout.getChildren().indexOf(audioMediaUI);
            audioVideoMedia.setIndexInTable(indexView);
            // ajout dans la liste du projet
            project.getEasyMediaList().add(audioVideoMedia);
            LOG.debug("AudioVideoMedia {} added. List size :{}", audioVideoMedia, project.getEasyMediaList().size());
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
            //FIXME
            //audioMediaUi.getEasyPlayer().stop();
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
        Optional<ButtonType> result = ActionDialog.showConfirmation(local.getString(Labels.MSG_DELETE_HEADER), local.getString(Labels.MSG_DELETE_CONT));
        if (result.get() == ButtonType.OK || result.get() == ButtonType.YES) {
            //this.removeChild(audioMediaUi);
        }
        this.updateKeyCodeList();
        //this.updatePlayersMap();
    }

    @FXML
    public void menuQuit(ActionEvent event) {

        EasyTable easyTable = AudioTableWrapper.getInstance().getEasyTable();
        if (!easyTable.isClosable()) {
            Optional<ButtonType> response = ActionDialog.showConfirmation(local.getString(Labels.MSG_WARNING_SAVE_HEADER), local.getString(Labels.MSG_WARNING_SAVE_CONT));
            if (response.isPresent() && response.get().equals(ButtonType.YES)) {
                menuFileSave(event);
            }
        }
        Platform.exit();
    }

    @FXML
    private void handleKeyCodePlay(KeyEvent event) {
        if (keyCodesMap.containsKey(event.getCode())) {
            //FIXME
            //keyCodesMap.get(event.getCode()).getEasyPlayer().playPause();
        }
    }

    @FXML
    private void handlePauseAll(ActionEvent event) {
        //FIXME
        audioMediaViewList.forEach(u -> {
            //
        });
    }

    @FXML
    private void handleStopAll(ActionEvent event) {
        audioMediaViewList.forEach(u -> {
            //FIXME
            //u.getEasyPlayer().stop();
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
                openProject(appProps.getLastFileProject());
            });
        }
    }

    public FlowPane getTableLayout() {
        return tableLayout;
    }

    private AudioMediaUI createAudioMediaView(AudioVideoMedia audioMedia) {
        AudioMediaUI audioMediaUI = new AudioMediaUI(audioMedia);
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
        //this.setNext(ui);
        LOG.trace("After config, AudioMedia is {}", media);
        nextUpdater.updateFromAudioMedia(media);
    }

    @Override
    public void removeChild(ChainingUpdater audioMediaView) {
        LOG.debug("Remove AudioMediaUI[{}]", audioMediaView);
        AudioMediaUI audioUI = (AudioMediaUI) audioMediaView;
        AudioMedia audioMedia = audioUI.getAudioMedia();

        // remove whithin childs within the chain
        //this.setNext(audioUI);
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
