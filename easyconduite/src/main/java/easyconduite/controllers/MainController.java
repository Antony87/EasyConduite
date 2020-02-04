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
import easyconduite.exception.PersistenceException;
import easyconduite.model.ChainingUpdater;
import easyconduite.model.EasyMedia;
import easyconduite.objects.ApplicationProperties;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTableWrapper;
import easyconduite.objects.EasyConduiteProperties;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.project.EasyTable;
import easyconduite.objects.project.MediaProject;
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.Labels;
import easyconduite.util.PersistenceHelper;
import easyconduite.view.AboutDialogUI;
import easyconduite.view.AudioMediaUI;
import easyconduite.view.PreferencesUI;
import easyconduite.view.controls.ActionDialog;
import easyconduite.view.controls.EasyconduitePlayer;
import easyconduite.view.controls.FileChooserControl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private final EasyConduiteProperties appProperties;
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
    private TrackPropertiesController trackPropertiesController;

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
        appProperties=EasyConduitePropertiesHandler.getInstance().getApplicationProperties();
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
    }

    @FXML
    private void menuFileSaveProject(ActionEvent event) {
        try {
            if (PersistenceHelper.isFileExists(project.getProjectPath())) {
                final File projectFile = project.getProjectPath().toFile();
                PersistenceHelper.saveToJson(project,projectFile);
            } else {
                FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.SAVE).build();
                final File projectFile = fileChooser.showSaveDialog(null);
                project.setProjectPath(projectFile.toPath());
                PersistenceHelper.saveToJson(project,projectFile);
            }
        } catch (EasyconduiteException | IOException e) {
            ActionDialog.showWarning(local.getString("dialog.error.header"), local.getString("easyconduitecontroler.save.error"));
        }
    }

    @FXML
    private void menuFileSaveProjectAs(ActionEvent event) {
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
        //TODO Passer en mode multi sélection de fichiers.

        final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_AUDIO).build();
        final File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            appProperties.setLastImportDir(file.getParentFile().toPath());
            final EasyMedia media = MediaFactory.getAudioVideoMedia(file);
            AudioMediaUI audioMediaUI = new AudioMediaUI(media);
            // recuperation index de la view sur le layout
            tableLayout.getChildren().add(audioMediaUI);
            int indexView = tableLayout.getChildren().indexOf(audioMediaUI);
            media.setIndexInTable(indexView);

            // Event sur clic dans l'AudioMediUI pour récupérer le focus.
            audioMediaUI.setOnMouseClicked(eventFocus -> {
                if (eventFocus.getButton().equals(MouseButton.PRIMARY)) {
                    audioMediaUI.requestFocus();
                    if(trackPropertiesController != null){
                        trackPropertiesController.setMediaUI(audioMediaUI);
                    }
                }
            });

            // ajout dans la liste du projet
            project.getEasyMediaList().add(media);
            LOG.debug("AudioVideoMedia {} added. List size :{}", media, project.getEasyMediaList().size());
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

        }
        this.updateKeyCodeList();
    }

    @FXML
    public void menuQuit(ActionEvent event) {

        EasyTable easyTable = AudioTableWrapper.getInstance().getEasyTable();
        if (!easyTable.isClosable()) {
            Optional<ButtonType> response = ActionDialog.showConfirmation(local.getString(Labels.MSG_WARNING_SAVE_HEADER), local.getString(Labels.MSG_WARNING_SAVE_CONT));
            if (response.isPresent() && response.get().equals(ButtonType.YES)) {
                menuFileSaveProject(event);
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

        });
    }

    @FXML
    private void handleStopAll(ActionEvent event) {
        audioMediaViewList.forEach(u -> {
            u.stop();
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

        //initialization nested controller
        trackPropertiesController.setMainController(this);

        // initialization listeners
        listenersHandler = new MainListenersHandler(this);
        listenersHandler.setDragAndDropFeature(tableLayout);

        ApplicationProperties appProps = ApplicationPropertiesHelper.getInstance().getProperties();

        final ContextMenu cmTableLayout = new ContextMenu();
        final MenuItem cmTitle = new MenuItem(local.getString("menu.track.title"));
        cmTitle.setDisable(true);
        final SeparatorMenuItem cmSeparator = new SeparatorMenuItem();
        final MenuItem cmImportTrack = new MenuItem(local.getString("menu.track.import"));
        cmImportTrack.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    menuImportAudio(e);
                } catch (EasyconduiteException ex) {
                    ex.printStackTrace();
                }
            }
        });
        cmTableLayout.getItems().addAll(cmTitle,cmSeparator,cmImportTrack);


        tableLayout.setOnContextMenuRequested(contextMenuEvent -> cmTableLayout.show(tableLayout, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY()));
        tableLayout.setOnMouseClicked(mouseEvent -> {
            if (cmTableLayout.isShowing()) {
                cmTableLayout.hide();
            }
        });

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
        return null;//audioMediaViewList.stream().filter(ui -> ui.getAudioMedia().equals(audioMedia)).findFirst().get();
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
        //AudioMedia audioMedia = audioUI.getAudioMedia();

        // remove whithin childs within the chain
        //this.setNext(audioUI);
        nextUpdater.removeChild(audioMediaView);

        // remove from local list
        tableLayout.getChildren().remove((AudioMediaUI) audioMediaView);
        audioMediaViewList.remove((AudioMediaUI) audioMediaView);
        // remove within EasyTable
        //AudioTableWrapper.getInstance().removeAudioMedia(audioMedia);
        //AudioTableHelper.cleanChilds(audioTable);
        LOG.trace("New audioMediaUI list size is {} and AudioMediaList size is {}", audioMediaViewList.size(), AudioTableWrapper.getInstance().getEasyTable().getAudioMediaList().size());
    }

    public MediaProject getProject() {
        return project;
    }
}
