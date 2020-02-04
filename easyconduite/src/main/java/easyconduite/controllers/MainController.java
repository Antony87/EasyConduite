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
import easyconduite.exception.EasyconduiteException;
import easyconduite.model.EasyMedia;
import easyconduite.objects.AudioTableWrapper;
import easyconduite.objects.EasyConduiteProperties;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.project.EasyTable;
import easyconduite.objects.project.MediaProject;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import java.util.stream.Collectors;

/**
 * This class implements a controller for audio table and AudioMediUI behaviors.
 *
 * @author A Fons
 */
public class MainController extends StackPane implements Initializable {


    private static final Logger LOG = LogManager.getLogger(MainController.class);
    private final ResourceBundle locale;
    private final Map<UUID, EasyconduitePlayer> playersMap;
    private final EasyConduiteProperties appProperties;
    /**
     * Map (ConcurrentHashMap) which amintains relationship between a Keybord
     * key and an AudioMediaUI.
     */
    private final Map<KeyCode, AudioMediaUI> keyCodesMap;
    @FXML
    StackPane mainPane;
    private MediaProject project;
    @FXML
    private FlowPane tableLayout;

    @FXML
    private TrackPropertiesController trackPropertiesController;

    @FXML
    private Menu openRecent;

    private MainListenersHandler listenersHandler;

    /**
     * Constructor without arguments, to respect instantiating by FXML.
     */
    public MainController() throws EasyconduiteException {
        LOG.debug("MainController instanciated");
        project = new MediaProject();
        keyCodesMap = new ConcurrentHashMap<>(100);
        playersMap = new ConcurrentHashMap<>(100);
        appProperties = EasyConduitePropertiesHandler.getInstance().getApplicationProperties();
        locale = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
    }

    @FXML
    private void menuFileOpen(ActionEvent event) {
        if (isOkForDelete()) {
            deleteProject();
            try {
                final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_PROJECT).build();
                final File projectfile = fileChooser.showOpenDialog(null);
                if (projectfile != null) openProject(projectfile);
                appProperties.setLastFileProject(projectfile);
            } catch (EasyconduiteException e) {
                ActionDialog.showWarning(locale.getString("dialog.error.header"), locale.getString("easyconduitecontroler.open.error"));
                LOG.error("Error occured during Open project ", e);
            }
        }
    }

    @FXML
    private void menuFileSaveProject(ActionEvent event) {
        try {
            if (PersistenceHelper.isFileExists(project.getProjectPath())) {
                final File projectFile = project.getProjectPath().toFile();
                PersistenceHelper.saveToJson(project, projectFile);
            } else {
                final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.SAVE).build();
                final File projectFile = fileChooser.showSaveDialog(null);
                if (projectFile != null) {
                    project.setProjectPath(projectFile.toPath());
                    PersistenceHelper.saveToJson(project, projectFile);
                    appProperties.setLastFileProject(projectFile);
                }
                //TODO mise à jour de la porpriétés lastProjectDir
            }
        } catch (EasyconduiteException | IOException e) {
            ActionDialog.showWarning(locale.getString("dialog.error.header"), locale.getString("easyconduitecontroler.save.error"));
        }
    }

    @FXML
    private void menuFileSaveProjectAs(ActionEvent event) {
        AudioTableWrapper.getInstance().saveAsToFile();
    }

    @FXML
    private void menuCloseProject(ActionEvent event) {
        if (isOkForDelete()) deleteProject();
    }

    @FXML
    public void menuQuit(ActionEvent event) {
        //TODO refactoring
        Optional<ButtonType> response = ActionDialog.showConfirmation(locale.getString(Labels.MSG_WARNING_SAVE_HEADER), locale.getString(Labels.MSG_WARNING_SAVE_CONT));
        if (response.isPresent() && response.get().equals(ButtonType.YES)) {
            menuFileSaveProject(event);
        }
        Platform.exit();
    }

    private void createMediaUI(EasyMedia media) {
        final AudioMediaUI audioMediaUI = new AudioMediaUI(media);
        // recuperation index de la view sur le layout
        tableLayout.getChildren().add(audioMediaUI);
        int indexView = tableLayout.getChildren().indexOf(audioMediaUI);
        media.setIndexInTable(indexView);
        // Event sur clic dans l'AudioMediUI pour récupérer le focus.
        audioMediaUI.setOnMouseClicked(eventFocus -> {
            if (eventFocus.getButton().equals(MouseButton.PRIMARY)) {
                audioMediaUI.requestFocus();
                if (trackPropertiesController != null) {
                    trackPropertiesController.setMediaUI(audioMediaUI);
                }
            }
        });
    }

    private void openProject(File file) {
        try {
            project = PersistenceHelper.openFromJson(file, MediaProject.class);
        } catch (IOException e) {
            ActionDialog.showWarning(locale.getString("dialog.error.header"), locale.getString("easyconduitecontroler.open.error"));
            LOG.error("Error occured during Open project ", e);
        }
        final List<EasyMedia> mediaList = project.getEasyMediaList();
        for (EasyMedia media : mediaList) createMediaUI(media);
        updateKeyCodeList();
        //TODO mise à jour de la propriété lastFileProject;
    }

    private void deleteProject() {
        final List<Node> mediaUIs = tableLayout.getChildren().stream().filter(node -> node instanceof AudioMediaUI).collect(Collectors.toList());
        for (Node mediaUI : mediaUIs) {
            this.deleteTrack((AudioMediaUI) mediaUI);
        }
        project = null;
        //TODO a refaire
        this.updateKeyCodeList();

    }

    private boolean isOkForDelete() {
        final List<Node> nodes = tableLayout.getChildren();
        if (nodes.size() > 0) {
            Optional<ButtonType> result = ActionDialog.showConfirmation(locale.getString(Labels.MSG_OPEN_HEADER), locale.getString(Labels.MSG_OPEN_CONT));
            return result.isPresent() && result.get() != ButtonType.NO;
        }
        return true;
    }

    @FXML
    private void menuPreferences(ActionEvent event) {
        try {
            PreferencesUI prefsUI = new PreferencesUI(locale);
            prefsUI.show();
        } catch (IOException ex) {
            LOG.debug("Error occurend ", ex);
        }
    }

    @FXML
    private void menuImportAudio(ActionEvent event) {
        try {
            final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_AUDIO).build();
            final List<File> audioFiles = fileChooser.showOpenMultipleDialog(null);
            if (audioFiles != null) {
                if (!audioFiles.isEmpty()) {
                    appProperties.setLastImportDir(audioFiles.get(0).getParentFile().toPath());
                    audioFiles.forEach(file -> {
                        final EasyMedia media = MediaFactory.getAudioVideoMedia(file);
                        // ajout dans la liste du projet
                        project.getEasyMediaList().add(media);
                        this.createMediaUI(media);
                    });
                }
            }
        } catch (EasyconduiteException e) {
            ActionDialog.showWarning(locale.getString("dialog.error.header"), locale.getString("easyconduitecontroler.open.error"));
        }
    }

    @FXML
    private void menuTrackDelete(ActionEvent event) {
        final List<Node> mediaUIs = tableLayout.getChildren();
        final AudioMediaUI mediaUI = (AudioMediaUI) mediaUIs.stream().filter(node -> node instanceof AudioMediaUI && node.isFocused()).findFirst().get();
        Optional<ButtonType> result = ActionDialog.showConfirmation(locale.getString(Labels.MSG_DELETE_HEADER), locale.getString(Labels.MSG_DELETE_CONT));
        if (result.get() == ButtonType.OK || result.get() == ButtonType.YES) {
            deleteTrack(mediaUI);
        }
    }

    private void deleteTrack(AudioMediaUI audioMediaUi) {
        ((AudioVideoMedia) audioMediaUi.getEasyMedia()).getPlayer().dispose();
        project.getEasyMediaList().remove(audioMediaUi.getEasyMedia());
        tableLayout.getChildren().remove(audioMediaUi);
        this.updateKeyCodeList();
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
        tableLayout.getChildren().forEach(node -> {
            if (node instanceof AudioMediaUI) {
                ((AudioMediaUI) node).getEasyMedia().pause();
            }
        });
    }


    @FXML
    private void handleStopAll(ActionEvent event) {
        tableLayout.getChildren().forEach(node -> {
            if (node instanceof AudioMediaUI) {
                ((AudioMediaUI) node).getEasyMedia().stop();
            }
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
//        listenersHandler = new MainListenersHandler(this);
//        listenersHandler.setDragAndDropFeature(tableLayout);

        final ContextMenu tableContextMenu = new ContextMenu();
        final MenuItem cmTitle = new MenuItem(locale.getString("menu.track.title"));
        cmTitle.setDisable(true);
        final SeparatorMenuItem cmSeparator = new SeparatorMenuItem();
        final MenuItem cmImportTrack = new MenuItem(locale.getString("menu.track.import"));
        cmImportTrack.setOnAction(e -> menuImportAudio(e));
        final MenuItem cmCloseProject = new MenuItem(locale.getString("menu.file.close"));
        cmCloseProject.setOnAction(e-> {
            if(isOkForDelete()) deleteProject();
        });
        tableContextMenu.getItems().addAll(cmTitle, cmSeparator, cmImportTrack,cmCloseProject);


        tableLayout.setOnContextMenuRequested(contextMenuEvent -> tableContextMenu.show(tableLayout, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY()));
        tableLayout.setOnMouseClicked(mouseEvent -> {
            if (tableContextMenu.isShowing()) {
                tableContextMenu.hide();
            }
        });

        if (appProperties.getLastFileProject() != null) {
            MenuItem recentFileMenuItem = new MenuItem(appProperties.getLastFileProject().toString());
            openRecent.getItems().add(recentFileMenuItem);
            recentFileMenuItem.setOnAction((event) -> {
                if (isOkForDelete()) {
                    openProject(new File(appProperties.getLastFileProject().toString()));
                }
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

    public void updateKeyCodeList() {
        LOG.trace("updateKeyCodeList called");
        EasyTable easyTable = AudioTableWrapper.getInstance().getEasyTable();
//        if (audioMediaViewList != null) {
//            keyCodesMap.clear();
//            CopyOnWriteArrayList<AudioMedia> cList = new CopyOnWriteArrayList(easyTable.getAudioMediaList());
//            cList.forEach((audioMedia) -> {
//                if (audioMedia.getKeycode() != null) {
//                    keyCodesMap.putIfAbsent(audioMedia.getKeycode(), findAudioMediaUI(audioMedia));
//                }
//            });
//        }
        LOG.trace("keyCodeMap size {}", keyCodesMap.size());
    }

    public EasyconduitePlayer getByUUID(UUID uuid) {
        return playersMap.get(uuid);
    }

    public MediaProject getProject() {
        return project;
    }
}
