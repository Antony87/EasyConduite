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
import easyconduite.model.AbstractMedia;
import easyconduite.model.AbstractUIMedia;
import easyconduite.model.UIMediaPlayable;
import easyconduite.EasyConduiteProperties;
import easyconduite.media.MediaFactory;
import easyconduite.media.RemoteMedia;
import easyconduite.project.MediaProject;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.Labels;
import easyconduite.util.PersistenceHelper;
import easyconduite.view.AboutDialogUI;
import easyconduite.view.MediaUIFactory;
import easyconduite.view.TrackConfigDialog;
import easyconduite.view.controls.ActionDialog;
import easyconduite.view.controls.FileChooserControl;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * This class implements the main controller for a Media Project.
 * <p>This controller is instanciate by fxml/easyconduite_v3.fxml</p>
 *
 * @author A Fons
 * @since 1.0
 */
public class MainController extends StackPane implements Initializable {

    private static final Logger LOG = LogManager.getLogger(MainController.class);
    /**
     * message that appears on the dialog box header.
     */
    private static final String DIALOG_EXCEPTION_HEADER = "dialog.exception.header";
    /**
     * This is the list of UI that are placed in the playlist.
     * <p>This avoids being directly dependent on tableLayout</p>
     */
    private final List<UIMediaPlayable> mediaUIList;
    private final ResourceBundle locale;
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

    public MainController() throws EasyconduiteException {
        LOG.debug("EasyConduite MainController is instantiated");
        project = new MediaProject();
        mediaUIList = new ArrayList<>(0);
        appProperties = EasyConduitePropertiesHandler.getInstance().getApplicationProperties();
        locale = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
    }

    /* ============================================================================
    Menu File statements.
     ==============================================================================*/

    @FXML
    private void menuFileOpen(ActionEvent event) {
        if (isProjectErasable(this.project)) {
            clearProject();
            try {
                final FileChooser fileChooser;
                fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_PROJECT).build();
                final File projectfile = fileChooser.showOpenDialog(null);
                if (projectfile != null) openProject(projectfile);
                appProperties.setLastFileProject(projectfile);
            } catch (EasyconduiteException | IOException e) {
                ActionDialog.showException(locale.getString(DIALOG_EXCEPTION_HEADER), locale.getString("easyconduitecontroler.open.error"), e);
                LOG.error("Error occured during opening project", e);
            }
        }
        event.consume();
    }

    private void openProject(File file) throws IOException {
        project = PersistenceHelper.openFromJson(file, MediaProject.class);
        project.setNeedToSave(false);
        final List<AbstractMedia> mediaList = project.getAbstractMediaList();
        for (AbstractMedia media : mediaList) {
            final AbstractUIMedia mediaUI = (AbstractUIMedia) MediaUIFactory.createMediaUI(media, this);
            getTableLayout().getChildren().add(mediaUI);
        }
    }

    @FXML
    private Menu menuOpenRecent;

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
                project.setNeedToSave(false);
            } catch (EasyconduiteException | IOException e) {
                ActionDialog.showException(locale.getString(DIALOG_EXCEPTION_HEADER), locale.getString("easyconduitecontroler.save.error"), e);
                LOG.error("Error occured during saving project", e);
            }
        }
        event.consume();
    }

    @FXML
    private void menuFileSaveProjectAs(ActionEvent event) {
        //TODO a refaire.
        //AudioTableWrapper.getInstance().saveAsToFile();
        event.consume();
    }

    @FXML
    private void menuCloseProject(ActionEvent event) {
        if (isProjectErasable(this.project)) this.clearProject();
        event.consume();
    }

    @FXML
    public void menuQuit(ActionEvent event) {
        if (!isProjectErasable(this.project)) {
            this.menuFileSaveProject(event);
        }
        Platform.exit();
    }

    /* ============================================================================
    Menu Tracks Statements.
     ==============================================================================*/

    @FXML
    public void menuImportAudio(ActionEvent event) {
        try {
            final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_AUDIO).build();
            final List<File> audioFiles = fileChooser.showOpenMultipleDialog(null);
            if (audioFiles != null && !audioFiles.isEmpty()) {
                appProperties.setLastImportDir(audioFiles.get(0).getParentFile().toPath());
                audioFiles.forEach(file -> {
                    final AbstractMedia media = MediaFactory.createPlayableMedia(file);
                    // ajout dans la liste du projet
                    project.getAbstractMediaList().add(media);
                    // construction de l'UI.
                    final UIMediaPlayable mediaUI = MediaUIFactory.createMediaUI(media, this);
                    getTableLayout().getChildren().add((AbstractUIMedia) mediaUI);
                    project.setNeedToSave(true);
                });
            }
        } catch (EasyconduiteException e) {
            ActionDialog.showException(locale.getString(DIALOG_EXCEPTION_HEADER), locale.getString("easyconduitecontroler.import.error"), e);
        }
        event.consume();
    }

    @FXML
    public void menuAddKodiPlayer(ActionEvent event) {
        final AbstractMedia media = MediaFactory.createPlayableMedia(RemoteMedia.Type.KODI);
        project.getAbstractMediaList().add(media);
        final UIMediaPlayable mediaUI = MediaUIFactory.createMediaUI(media, this);
        try {
            editTrack(Objects.requireNonNull(mediaUI));
            getTableLayout().getChildren().add((AbstractUIMedia) mediaUI);
            media.initPlayer();
            project.setNeedToSave(true);
        } catch (NullPointerException | EasyconduiteException e) {
            ActionDialog.showException(locale.getString(DIALOG_EXCEPTION_HEADER), locale.getString("menu.track.kodiexception"), e);
            LOG.error("Error occured during create Kodi media", e);
        }
        event.consume();
    }

    @FXML
    public void menuEditTrack(ActionEvent event) {
        final Optional<UIMediaPlayable> optionnal = getMediaUIList().stream().filter(UIMediaPlayable::isSelected).findFirst();
        optionnal.ifPresent(uiResourcePlayable -> editTrack(uiResourcePlayable));
        event.consume();
    }

    public void editTrack(UIMediaPlayable mediaUi) {
        mediaUi.stop();
        new TrackConfigDialog(mediaUi, getMediaUIList());
    }

    @FXML
    public void menuTrackDelete(ActionEvent event) {
        final Optional<UIMediaPlayable> optionnal = getMediaUIList().stream().filter(UIMediaPlayable::isSelected).findFirst();
        if (optionnal.isPresent()) {
            final Optional<ButtonType> result = ActionDialog.showConfirmation(locale.getString(Labels.MSG_DELETE_HEADER), locale.getString(Labels.MSG_DELETE_CONT));
            if (result.get() == ButtonType.OK || result.get() == ButtonType.YES) {
                deleteOneTrack(optionnal.get());
            }
        }
        event.consume();
    }

    private void deleteOneTrack(UIMediaPlayable mediaUI) {
        mediaUI.getAbstractMedia().closePlayer();
        project.getAbstractMediaList().remove(mediaUI.getAbstractMedia());
        tableLayout.getChildren().remove(mediaUI);
        project.setNeedToSave(true);
    }

    protected boolean isProjectErasable(MediaProject project) {
        if (project.isNeedToSave()) {
            Optional<ButtonType> result = ActionDialog.showConfirmation(locale.getString("dialog.warning.save.header"), locale.getString("dialog.warning.save.content"));
            return result.isPresent() && result.get() == ButtonType.NO;
        }
        return true;
    }

    @FXML
    private void menuPreferences(ActionEvent event) {
        //TODO a refaire
        event.consume();
    }

    protected void clearProject() {
        final List<UIMediaPlayable> mediaUIs = getMediaUIList();
        for (UIMediaPlayable mediaUI : mediaUIs) {
            //TODO concuurent exception
            deleteOneTrack(mediaUI);
        }
        project.setName(null);
    }

    @FXML
    private void handleKeyCodePlay(KeyEvent event) {
        if (!event.isControlDown() && !event.isAltDown()) {
            LOG.trace("Key {} pressed ", event.getCode().getName());
            for (UIMediaPlayable mediaUI : this.getMediaUIList()) {
                if (mediaUI.getAbstractMedia().getKeycode() == event.getCode()) {
                    mediaUI.playPause();
                    event.consume();
                }
            }
        }
    }

    @FXML
    private void handlePauseAll(ActionEvent event) {
        this.getMediaUIList().forEach(mediaUI -> mediaUI.getAbstractMedia().pause());
        event.consume();
    }


    @FXML
    private void handleStopAll(ActionEvent event) {
        this.getMediaUIList().forEach(mediaUI -> mediaUI.getAbstractMedia().stop());
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
        LOG.debug("EasyConduite MainController is initialized");

        if (appProperties.getLastFileProject() != null) {
            MenuItem recentFileMenuItem = new MenuItem(appProperties.getLastFileProject().toString());
            menuOpenRecent.getItems().add(recentFileMenuItem);
            recentFileMenuItem.setOnAction(event -> {
                if (isProjectErasable(this.project)) {
                    try {
                        openProject(new File(appProperties.getLastFileProject().toString()));
                    } catch (IOException e) {
                        ActionDialog.showException(locale.getString(DIALOG_EXCEPTION_HEADER), locale.getString("easyconduitecontroler.open.error"), e);
                        LOG.error("Error occured during opening project", e);
                    }
                }
            });
        }

        MainControllerContextMenu contextMenu = new MainControllerContextMenu(this);
        tableLayout.setOnMouseClicked(mouseEvent -> {
            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
            getMediaUIList().forEach(mediaUI -> mediaUI.setSelected(false));
        });

        tableLayout.getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                for (Node tableNode : change.getAddedSubList()) {
                    if (tableNode instanceof UIMediaPlayable) {
                        mediaUIList.add((UIMediaPlayable) tableNode);
                        LOG.trace("MediaUI added to tableLayout and mediaUIList : {}", change.getAddedSubList());
                    }
                }
                for (Node tableNode : change.getRemoved()) {
                    if (tableNode instanceof UIMediaPlayable) {
                        mediaUIList.remove(tableNode);
                        LOG.trace("MediaUI removed from tableLayout and mediaUIList : {}", Arrays.toString(change.getRemoved().toArray()));
                    }
                }
            }
        });
    }

    public MediaProject getProject() {
        return project;
    }

    public ResourceBundle getLocale() {
        return locale;
    }

    public FlowPane getTableLayout() {
        return tableLayout;
    }

    public List<UIMediaPlayable> getMediaUIList() {
        return mediaUIList;
    }


}
