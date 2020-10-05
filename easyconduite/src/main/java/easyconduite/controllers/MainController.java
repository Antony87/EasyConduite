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

import com.jfoenix.controls.JFXSpinner;
import easyconduite.EasyConduiteProperties;
import easyconduite.Easyconduite;
import easyconduite.exception.EasyconduiteException;
import easyconduite.media.MediaFactory;
import easyconduite.model.*;
import easyconduite.project.MediaProject;
import easyconduite.project.ProjectContext;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.Labels;
import easyconduite.util.PersistenceHelper;
import easyconduite.view.*;
import easyconduite.view.controls.ActionDialog;
import easyconduite.view.controls.FileChooserControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class implements the main controller for a Media Project.
 * <p>This controller is instanciate by fxml/easyconduite_v3.fxml</p>
 *
 * @author A Fons
 * @since 1.0
 */
public class MainController extends BaseController {

    private static final Logger LOG = LogManager.getLogger(MainController.class);

    private static final ProjectContext context = ProjectContext.getContext();
    /**
     * message that appears on the dialog box header.
     */
    private static final String DIALOG_EXCEPTION_HEADER = "dialog.exception.header";
    /**
     * This is the list of UI that are placed in the playlist.
     * <p>This avoids being directly dependent on tableLayout</p>
     */
    private final ObservableList<UIMediaPlayable> mediaUIList = FXCollections.observableArrayList();
    /**
     * The properties of this application EasyConduite.
     *
     * @see easyconduite.Easyconduite
     */
    private final EasyConduiteProperties appProperties;
    /**
     * The medias project.
     */
    private MediaProject project;

    /**
     * This is the main Pane of the application.
     */
    @FXML
    private StackPane mainPane;

    /**
     * This is the FlowPane, container of the MediaUI for the tracks.
     */
    @FXML
    private FlowPane tableLayout;

    @FXML
    private Pane infoPane;

    @FXML
    private ConduiteController conduiteController;

    @FXML
    private BorderPane conduite;

    private JFXSpinner waitSpinner;
    @FXML
    private Menu menuOpenRecent;

    /* ============================================================================
    Menu File statements.
     ==============================================================================*/

    public MainController() throws EasyconduiteException {
        LOG.debug("EasyConduite MainController is instantiated");
        project = new MediaProject();
        appProperties = EasyConduitePropertiesHandler.getInstance().getApplicationProperties();
    }

    @FXML
    private void menuFileOpen(ActionEvent event) {
        infoPane.getChildren().add(waitSpinner);
        if (isProjectErasable()) {
            clearProject();
            try {
                final FileChooser fileChooser;
                fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_PROJECT).build();
                final File projectfile = fileChooser.showOpenDialog(null);
                if (projectfile != null) openProject(projectfile);
                appProperties.setLastFileProject(projectfile);
            } catch (EasyconduiteException | IOException e) {
                ActionDialog.showException(resourceBundle.getString(DIALOG_EXCEPTION_HEADER), resourceBundle.getString("easyconduitecontroler.open.error"), e);
                LOG.error("Error occured during opening project", e);
            } finally {
                infoPane.getChildren().remove(waitSpinner);
            }
        }
        event.consume();
    }

    private void openProject(File file) throws IOException {
        project = PersistenceHelper.openFromJson(file, MediaProject.class);
        conduiteController.setConduite(project.getConduite());
        final List<MediaPlayable> mediaList = project.getMediaPlayables();
        for (MediaPlayable media : mediaList) {
            try {
                media.initPlayer();
            } catch (EasyconduiteException e) {
                ActionDialog.showError(resourceBundle.getString("dialog.error.header"), resourceBundle.getString("easyconduitecontroler.open.error"));
                LOG.error("Error occured during player initialize at media {}", media);
            }
            //TODO ajouter le média en fonction de l'id dans la table.
            addMediaToTracks(media);
        }
        conduiteController.updateConduiteUI(project.getConduite());
        context.setNeedToSave(false);
    }

    @FXML
    private void menuFileSaveProject(ActionEvent event) {
        final List<UIMediaPlayable> uiList = getMediaUIList();
        if (!uiList.isEmpty()) {
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
                        appProperties.setLastProjectDir(projectFile.toPath().getParent());
                    }
                }
                context.setNeedToSave(false);
            } catch (EasyconduiteException | IOException e) {
                ActionDialog.showException(resourceBundle.getString(DIALOG_EXCEPTION_HEADER), resourceBundle.getString("easyconduitecontroler.save.error"), e);
                LOG.error("Error occured during saving project", e);
            }
        }
        event.consume();
    }

    @FXML
    private void menuFileSaveProjectAs(ActionEvent event) {
        final List<UIMediaPlayable> uiList = getMediaUIList();
        if (!uiList.isEmpty()) {
            try {
                final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.SAVE).build();
                final File projectFile = fileChooser.showSaveDialog(null);
                if (projectFile != null) {
                    project.setProjectPath(projectFile.toPath());
                    PersistenceHelper.saveToJson(project, projectFile);
                    appProperties.setLastFileProject(projectFile);
                    appProperties.setLastProjectDir(projectFile.toPath().getParent());
                }
                context.setNeedToSave(false);
            } catch (EasyconduiteException | IOException e) {
                ActionDialog.showException(resourceBundle.getString(DIALOG_EXCEPTION_HEADER), resourceBundle.getString("easyconduitecontroler.save.error"), e);
                LOG.error("Error occured during saving project", e);
            }
        }
        event.consume();
    }

    @FXML
    public void menuCloseProject(ActionEvent event) {
        if (isProjectErasable()) this.clearProject();
        event.consume();
    }

    @FXML
    public void menuQuit(ActionEvent event) {
        if (!isProjectErasable()) {
            this.menuFileSaveProject(event);
        }
        Easyconduite.quitEasyConduite();
    }

    /* ============================================================================
    Menu Tracks Statements.
     ==============================================================================*/

    @FXML
    public void menuImportAudio(ActionEvent event) {
        infoPane.getChildren().add(waitSpinner);
        try {
            final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_AUDIO).build();
            final List<File> audioFiles = fileChooser.showOpenMultipleDialog(null);
            if (audioFiles != null && !audioFiles.isEmpty()) {
                appProperties.setLastImportDir(audioFiles.get(0).getParentFile().toPath());
                audioFiles.forEach(file -> {
                    final MediaPlayable media = MediaFactory.createPlayableMedia(file);
                    LOG.trace("Create media from file {} : {}", file, media);
                    try {
                        assert media != null;
                        media.initPlayer();
                    } catch (EasyconduiteException e) {
                        ActionDialog.showException(resourceBundle.getString(DIALOG_EXCEPTION_HEADER), resourceBundle.getString("easyconduitecontroler.import.error"), e);
                    }
                    addMediaToTracks(media);
                    project.getMediaPlayables().add(media);
                });
                context.setNeedToSave(true);
            }
        } catch (EasyconduiteException e) {
            ActionDialog.showException(resourceBundle.getString(DIALOG_EXCEPTION_HEADER), resourceBundle.getString("easyconduitecontroler.import.error"), e);
        } finally {
            infoPane.getChildren().remove(waitSpinner);
        }
        event.consume();
    }

    @FXML
    public void menuAddKodiPlayer(ActionEvent event) {
        try {
            new TrackConfigDialog().withNoMedia().build();
        } catch (NullPointerException | EasyconduiteException e) {
            ActionDialog.showException(resourceBundle.getString(DIALOG_EXCEPTION_HEADER), resourceBundle.getString("menu.track.kodiexception"), e);
            LOG.error("Error occured during create Kodi media", e);
        }
        event.consume();
    }

    @FXML
    public void menuEditTrack(ActionEvent event) {
        final Optional<UIMediaPlayable> optionnal = getMediaUIList().stream().filter(UIMediaPlayable::isSelected).findFirst();
        optionnal.ifPresent(uiResourcePlayable -> editTrack(uiResourcePlayable));
        context.setNeedToSave(true);
        event.consume();
    }

    public void editTrack(UIMediaPlayable mediaUi) {
        LOG.trace("Edit media with UIMediaPlayable {}", mediaUi);
        mediaUi.stop();
        try {
            if (mediaUi instanceof AudioMediaUI)
                new TrackConfigDialog().withAudioMedia((AudioMediaUI) mediaUi).build();
            if (mediaUi instanceof RemoteMediaUI)
                new TrackConfigDialog().withRemoteMedia((RemoteMediaUI) mediaUi).build();
        } catch (EasyconduiteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void menuTrackDelete(ActionEvent event) {
        final Optional<UIMediaPlayable> optionnal = getMediaUIList().stream().filter(UIMediaPlayable::isSelected).findFirst();
        if (optionnal.isPresent()) {
            final Optional<ButtonType> result = ActionDialog.showConfirmation(resourceBundle.getString(Labels.MSG_DELETE_HEADER), resourceBundle.getString(Labels.MSG_DELETE_CONT));
            if (result.get() == ButtonType.OK || result.get() == ButtonType.YES) {
                deleteOneTrack(optionnal.get());
            }
        }
        event.consume();
    }

    private void deleteOneTrack(UIMediaPlayable mediaUI) {
        final MediaPlayable media = mediaUI.getMediaPlayable();
        media.closePlayer();
        mediaUIList.remove(mediaUI);
        conduiteController.deleteOneMedia(media);
        project.getMediaPlayables().remove(media);
        tableLayout.getChildren().remove(mediaUI);
        context.setNeedToSave(true);
    }

    protected boolean isProjectErasable() {
        if (!context.isNeedToSave()) return true;
        Optional<ButtonType> result = ActionDialog.showConfirmation(resourceBundle.getString("dialog.warning.clear.header"), resourceBundle.getString("dialog.warning.clear.content"));
        if (result.isPresent()) {
            ButtonType response = result.get();
            if (response == ButtonType.NO) return false;
            return response == ButtonType.YES || response == ButtonType.OK;
        }
        return false;
    }

    @FXML
    private void menuCueAddTrigger(ActionEvent event) {
        conduiteController.addTriggerAction(event);
    }

    @FXML
    private void menuCueNextTrigger(ActionEvent event) {
        conduiteController.fireNextTriggerAction(event);
    }

    @FXML
    private void menuCuePreviousTrigger(ActionEvent event) {
        conduiteController.firePreviousTriggerAction(event);
    }

    @FXML
    private void menuPreferences(ActionEvent event) {
        //TODO a refaire
    }

    protected void clearProject() {
        final List<UIMediaPlayable> mediaUIs = getMediaUIList();

        for (UIMediaPlayable mediaUI : mediaUIs) {
            deleteOneTrack(mediaUI);
        }
        project.setName(null);
        project.setProjectPath(null);
        project.setConduite(null);
        context.setNeedToSave(false);
    }

    @FXML
    private void handleKeyCodePlay(KeyEvent event) {
        if (!event.isControlDown() && !event.isAltDown()) {
            LOG.trace("Key {} pressed ", event.getCode().getName());
            for (UIMediaPlayable mediaUI : this.getMediaUIList()) {
                if (mediaUI.getMediaPlayable().getKeycode() == event.getCode()) {
                    LOG.trace("PlayPause with {} pressed ", mediaUI.getMediaPlayable().getKeycode());
                    mediaUI.playPause();
                    event.consume();
                }
            }
        }
    }

    @FXML
    public void handleStopAll(ActionEvent event) {
        this.getMediaUIList().forEach(mediaUI -> mediaUI.getMediaPlayable().stop());
        event.consume();
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        try {
            new AboutDialogUI();
        } catch (IOException ex) {
            LOG.error("An error occured", ex);
        }
        event.consume();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        LOG.debug("EasyConduite MainController is initialized");


        if (appProperties.getLastFileProject() != null) {
            MenuItem recentFileMenuItem = new MenuItem(appProperties.getLastFileProject().toString());
            menuOpenRecent.getItems().add(recentFileMenuItem);
            recentFileMenuItem.setOnAction(event -> {
                if (isProjectErasable()) {
                    try {
                        openProject(new File(appProperties.getLastFileProject().toString()));
                    } catch (IOException e) {
                        ActionDialog.showException(resourceBundle.getString(DIALOG_EXCEPTION_HEADER), resourceBundle.getString("easyconduitecontroler.open.error"), e);
                        LOG.error("Error occured during opening project", e);
                    }
                }
            });
        }

        // initialisation du spinner pour l'infoPane
        waitSpinner = new JFXSpinner();
        waitSpinner.layoutXProperty().bind(infoPane.widthProperty().subtract(waitSpinner.widthProperty()).divide(2));
        waitSpinner.layoutYProperty().bind(infoPane.heightProperty().subtract(waitSpinner.heightProperty()).divide(2));

        // injection de la conduite dans le ConduiteController
        conduiteController.setConduite(getProject().getConduite());
    }

    public UIMediaPlayable addMediaToTracks(MediaPlayable media) {
        LOG.trace("Media added to Project : {}", media);
        final UIMediaPlayable mediaUI = MediaUIFactory.createMediaUI(media);
        mediaUIList.add(mediaUI);
        conduiteController.addMedia(media);
        if (mediaUIList.contains(mediaUI)) tableLayout.getChildren().add((AbstractUIMedia) mediaUI);
        return mediaUI;
    }

    public UIMediaPlayable findUIfromMedia(MediaPlayable media) {
        for (UIMediaPlayable uiMedia : mediaUIList) {
            final MediaPlayable mediaPlayable = uiMedia.getMediaPlayable();
            if (mediaPlayable.equals(media)) {
                return uiMedia;
            }
        }
        return null;
    }

    private void removeMediaFromTracks(MediaPlayable media) {
    }

    public MediaProject getProject() {
        return project;
    }

    public FlowPane getTableLayout() {
        return tableLayout;
    }

    public ObservableList<UIMediaPlayable> getMediaUIList() {
        return mediaUIList;
    }
}
