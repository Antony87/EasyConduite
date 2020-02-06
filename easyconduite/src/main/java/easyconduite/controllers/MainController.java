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

import easyconduite.exception.EasyconduiteException;
import easyconduite.model.EasyMedia;
import easyconduite.objects.AudioTableWrapper;
import easyconduite.objects.EasyConduiteProperties;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.project.MediaProject;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.Labels;
import easyconduite.util.PersistenceHelper;
import easyconduite.view.AboutDialogUI;
import easyconduite.view.AudioMediaUI;
import easyconduite.view.PreferencesUI;
import easyconduite.view.TrackConfigDialog;
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
    private static final String DIALOG_ERROR_HEADER ="dialog.error.header";
    private final ResourceBundle locale;
    private final Map<UUID, EasyconduitePlayer> playersMap;
    private final EasyConduiteProperties appProperties;
    private MediaProject project;

    @FXML
    private StackPane mainPane;

    @FXML
    private FlowPane tableLayout;

    @FXML
    private TrackPropertiesController trackPropertiesController;

    @FXML
    private Menu openRecent;

    public MainController() throws EasyconduiteException {
        LOG.debug("MainController instanciated");
        project = new MediaProject();
        playersMap = new ConcurrentHashMap<>(100);
        appProperties = EasyConduitePropertiesHandler.getInstance().getApplicationProperties();
        locale = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
    }

    @FXML
    private void menuFileOpen(ActionEvent event) {
        if (isProjectErasable()) {
            clearProject();
            try {
                final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_PROJECT).build();
                final File projectfile = fileChooser.showOpenDialog(null);
                if (projectfile != null) openProject(projectfile);
                appProperties.setLastFileProject(projectfile);
            } catch (EasyconduiteException e) {
                ActionDialog.showWarning(locale.getString(DIALOG_ERROR_HEADER), locale.getString("easyconduitecontroler.open.error"));
                LOG.error("Error occured during Open project ", e);
            }
        }
        event.consume();
    }

    @FXML
    private void menuFileSaveProject(ActionEvent event) {
        final List<AudioMediaUI> mediaUIList = getMediaUIList();
        if (!mediaUIList.isEmpty()) {
            try {
                if (PersistenceHelper.isFileExists(project.getProjectPath())) {
                    final File projectFile = project.getProjectPath().toFile();
                    PersistenceHelper.saveToJson(project, projectFile);
                    appProperties.setSavingNeeded(false);
                } else {
                    final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.SAVE).build();
                    final File projectFile = fileChooser.showSaveDialog(null);
                    if (projectFile != null) {
                        project.setProjectPath(projectFile.toPath());
                        PersistenceHelper.saveToJson(project, projectFile);
                        appProperties.setLastFileProject(projectFile);
                        appProperties.setLastProjectDir(projectFile.toPath().getParent());
                        appProperties.setSavingNeeded(false);
                    }
                }
            } catch (EasyconduiteException | IOException e) {
                ActionDialog.showWarning(locale.getString(DIALOG_ERROR_HEADER), locale.getString("easyconduitecontroler.save.error"));
            }
        }
        event.consume();
    }

    @FXML
    private void menuFileSaveProjectAs(ActionEvent event) {
        AudioTableWrapper.getInstance().saveAsToFile();
        event.consume();
    }

    @FXML
    private void menuCloseProject(ActionEvent event) {
        if (this.isProjectErasable()) this.clearProject();
        event.consume();
    }

    @FXML
    public void menuQuit(ActionEvent event) {
        if (!isProjectErasable()) {
            final Optional<ButtonType> response = ActionDialog.showConfirmation(locale.getString(Labels.MSG_WARNING_SAVE_HEADER), locale.getString(Labels.MSG_WARNING_SAVE_CONT));
            if (response.isPresent() && response.get().equals(ButtonType.YES)) {
                this.menuFileSaveProject(event);
            }
        }
        Platform.exit();
    }

    @FXML
    public void menuEditTrack(ActionEvent event) {
        final Optional<AudioMediaUI> optionnal = getMediaUIList().stream().filter(Node::isFocused).findFirst();
        optionnal.ifPresent(this::editTrack);
        event.consume();
    }

    public void editTrack(AudioMediaUI audioMediaUi) {

        final TrackConfigDialog trackConfigDialog;
        try {
            audioMediaUi.stop();
            trackConfigDialog = new TrackConfigDialog();
            trackConfigDialog.getConfigController().setMediaUI(audioMediaUi);
            trackConfigDialog.show();
        } catch (IOException ex) {
            LOG.error("Error occurend during TrackConfigDialog construction", ex);
        }
    }

    private void createMediaUI(EasyMedia media) {
        final AudioMediaUI audioMediaUI = new AudioMediaUI(media);
        // recuperation index de la view sur le layout
        tableLayout.getChildren().add(audioMediaUI);
        int indexView = getMediaUIList().indexOf(audioMediaUI);
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
            ActionDialog.showWarning(locale.getString(DIALOG_ERROR_HEADER), locale.getString("easyconduitecontroler.open.error"));
            LOG.error("Error occured during Open project ", e);
        }
        final List<EasyMedia> mediaList = project.getEasyMediaList();
        for (EasyMedia media : mediaList) createMediaUI(media);
        updateKeyCodeList();
        //TODO mise à jour de la propriété lastFileProject;
    }

    private boolean isProjectErasable() {
        // Does the table contain Media UI and the project was not saved.
        if (!getMediaUIList().isEmpty() && appProperties.isSavingNeeded()) {
            Optional<ButtonType> result = ActionDialog.showConfirmation(locale.getString(Labels.MSG_OPEN_HEADER), locale.getString(Labels.MSG_OPEN_CONT));
            return result.isPresent() && result.get() != ButtonType.NO;
        }
        return true;
    }

    public List<AudioMediaUI> getMediaUIList() {
        final List<Node> mediaUIs = tableLayout.getChildren();
        return mediaUIs.parallelStream().filter(node -> node instanceof AudioMediaUI).map(node -> (AudioMediaUI) node).collect(Collectors.toList());
    }

    @FXML
    private void menuPreferences(ActionEvent event) {
        try {
            PreferencesUI prefsUI = new PreferencesUI(locale);
            prefsUI.show();
        } catch (IOException ex) {
            LOG.debug("Error occurend ", ex);
        }
        event.consume();
    }

    @FXML
    private void menuImportAudio(ActionEvent event) {
        try {
            final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_AUDIO).build();
            final List<File> audioFiles = fileChooser.showOpenMultipleDialog(null);
                if (audioFiles != null && !audioFiles.isEmpty()) {
                    appProperties.setLastImportDir(audioFiles.get(0).getParentFile().toPath());
                    audioFiles.forEach(file -> {
                        final EasyMedia media = MediaFactory.getAudioVideoMedia(file);
                        // ajout dans la liste du projet
                        project.getEasyMediaList().add(media);
                        this.createMediaUI(media);
                        appProperties.setSavingNeeded(true);
                    });
            }
        } catch (EasyconduiteException e) {
            ActionDialog.showWarning(locale.getString(DIALOG_ERROR_HEADER), locale.getString("easyconduitecontroler.open.error"));
        }
        event.consume();
    }

    @FXML
    private void menuTrackDelete(ActionEvent event) {
        final Optional<AudioMediaUI> optionnal = getMediaUIList().stream().filter(Node::isFocused).findFirst();
        if (optionnal.isPresent()) {
            final Optional<ButtonType> result = ActionDialog.showConfirmation(locale.getString(Labels.MSG_DELETE_HEADER), locale.getString(Labels.MSG_DELETE_CONT));
                if (result.get() == ButtonType.OK || result.get() == ButtonType.YES) {
                    deleteOneTrack(optionnal.get());
                }
        }
        event.consume();
    }

    private void deleteOneTrack(AudioMediaUI audioMediaUi) {
        ((AudioVideoMedia) audioMediaUi.getEasyMedia()).getPlayer().dispose();
        project.getEasyMediaList().remove(audioMediaUi.getEasyMedia());
        tableLayout.getChildren().remove(audioMediaUi);
        this.updateKeyCodeList();
    }

    private void clearProject() {
        final List<AudioMediaUI> mediaUIs = getMediaUIList();
        for (AudioMediaUI mediaUI : mediaUIs) {
            deleteOneTrack(mediaUI);
        }
        project = null;
        //TODO a refaire
        this.updateKeyCodeList();

    }

    @FXML
    private void handleKeyCodePlay(KeyEvent event) {
        final List<AudioMediaUI> mediaUIs = this.getMediaUIList();
        for (AudioMediaUI mediaUI : mediaUIs) {
            if (mediaUI.getEasyMedia().getKeycode().equals(event.getCode())) {
                mediaUI.playPause();
            }
        }
        event.consume();
    }

    @FXML
    private void handlePauseAll(ActionEvent event) {
        this.getMediaUIList().forEach(mediaUI -> mediaUI.getEasyMedia().pause());
        event.consume();
    }


    @FXML
    private void handleStopAll(ActionEvent event) {
        this.getMediaUIList().forEach(mediaUI -> mediaUI.getEasyMedia().stop());
        event.consume();
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

        final ContextMenu tableContextMenu = new ContextMenu();
        final MenuItem cmTitle = new MenuItem(locale.getString("menu.track.title"));
        cmTitle.setDisable(true);
        final SeparatorMenuItem cmSeparator = new SeparatorMenuItem();
        final MenuItem cmImportTrack = new MenuItem(locale.getString("menu.track.import"));
        cmImportTrack.setOnAction(this::menuImportAudio);
        final MenuItem cmCloseProject = new MenuItem(locale.getString("menu.file.close"));
        cmCloseProject.setOnAction(e -> {
            if (isProjectErasable()) clearProject();
        });
        tableContextMenu.getItems().addAll(cmTitle, cmSeparator, cmImportTrack, cmCloseProject);


        tableLayout.setOnContextMenuRequested(contextMenuEvent -> tableContextMenu.show(tableLayout, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY()));
        tableLayout.setOnMouseClicked(mouseEvent -> {
            if (tableContextMenu.isShowing()) {
                tableContextMenu.hide();
            }
        });

        if (appProperties.getLastFileProject() != null) {
            MenuItem recentFileMenuItem = new MenuItem(appProperties.getLastFileProject().toString());
            openRecent.getItems().add(recentFileMenuItem);
            recentFileMenuItem.setOnAction(event -> {
                if (isProjectErasable()) {
                    openProject(new File(appProperties.getLastFileProject().toString()));
                }
            });
        }
    }

    /**
     * Return if a keyboard keycode exists within AudioMediaUI List.
     *
     * @param keycode le code de la touche
     * @return Le code exist'il dans la liste
     */
    public boolean isKeyCodeExist(KeyCode keycode) {
        return true;
    }

    public void updateKeyCodeList() {
        LOG.trace("updateKeyCodeList called");
    }

    public EasyconduitePlayer getByUUID(UUID uuid) {
        return playersMap.get(uuid);
    }

    public MediaProject getProject() {
        return project;
    }
}
