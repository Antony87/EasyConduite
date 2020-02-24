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
import easyconduite.model.AbstractUIMedia;
import easyconduite.model.EasyMedia;
import easyconduite.model.ResourcePlayable;
import easyconduite.model.UIResourcePlayable;
import easyconduite.objects.EasyConduiteProperties;
import easyconduite.objects.media.AudioMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.project.MediaProject;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.Labels;
import easyconduite.util.PersistenceHelper;
import easyconduite.view.AboutDialogUI;
import easyconduite.view.AudioMediaUI;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
     * message that appears on the header.
     */
    private static final String DIALOG_ERROR_HEADER = "dialog.error.header";
    private final List<UIResourcePlayable> mediaUIList;
    private final ResourceBundle locale;
    /**
     * The properties of this application EasyConduite.
     * @see easyconduite.Easyconduite
     *
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
        LOG.debug("EasyConduite MainController is instancied");
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

    private void openProject(File file) {
        try {
            project = PersistenceHelper.openFromJson(file, MediaProject.class);
            project.setNeedToSave(false);
        } catch (IOException e) {
            ActionDialog.showWarning(locale.getString(DIALOG_ERROR_HEADER), locale.getString("easyconduitecontroler.open.error"));
            LOG.error("Error occured during Open project ", e);
        }
        final List<EasyMedia> mediaList = project.getEasyMediaList();
        for (EasyMedia media : mediaList) {
            final AudioMediaUI mediaUI = new AudioMediaUI(media, this);
            getTableLayout().getChildren().add(mediaUI.getMediaVboxNode());
        }
    }

    @FXML
    private Menu menuOpenRecent;

    @FXML
    private void menuFileSaveProject(ActionEvent event) {
        final List<UIResourcePlayable> uiList = getMediaUIList();
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
                ActionDialog.showWarning(locale.getString(DIALOG_ERROR_HEADER), locale.getString("easyconduitecontroler.save.error"));
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
    protected void menuImportAudio(ActionEvent event) {
        try {
            final FileChooser fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_AUDIO).build();
            final List<File> audioFiles = fileChooser.showOpenMultipleDialog(null);
            if (audioFiles != null && !audioFiles.isEmpty()) {
                appProperties.setLastImportDir(audioFiles.get(0).getParentFile().toPath());
                audioFiles.forEach(file -> {
                    final EasyMedia media = MediaFactory.getPlayableMedia(file);
                    // ajout dans la liste du projet
                    project.getEasyMediaList().add(media);
                    // construction de l'UI.
                    AudioMediaUI mediaUI = new AudioMediaUI(media, this);
                    getTableLayout().getChildren().add(mediaUI.getMediaVboxNode());
                    project.setNeedToSave(true);
                });
            }
        } catch (EasyconduiteException e) {
            ActionDialog.showWarning(locale.getString(DIALOG_ERROR_HEADER), locale.getString("easyconduitecontroler.open.error"));
        }
        event.consume();
    }

    @FXML
    public void menuAddKodiPlayer(ActionEvent event){
        //TODO implémenter
    }

    @FXML
    public void menuEditTrack(ActionEvent event) {
        final Optional<UIResourcePlayable> optionnal = getMediaUIList().stream().filter(UIResourcePlayable::isSelected).findFirst();
        optionnal.ifPresent(this::editTrack);
        event.consume();
    }

    public void editTrack(UIResourcePlayable mediaUi) {
        try {
            mediaUi.stop();
            new TrackConfigDialog(mediaUi, getMediaUIList());
        } catch (IOException ex) {
            LOG.error("Error occurend during TrackConfigDialog construction", ex);
        }
    }

    @FXML
    public void menuTrackDelete(ActionEvent event) {
        final Optional<UIResourcePlayable> optionnal = getMediaUIList().stream().filter(UIResourcePlayable::isSelected).findFirst();
        if (optionnal.isPresent()) {
            final Optional<ButtonType> result = ActionDialog.showConfirmation(locale.getString(Labels.MSG_DELETE_HEADER), locale.getString(Labels.MSG_DELETE_CONT));
            if (result.get() == ButtonType.OK || result.get() == ButtonType.YES) {
                deleteOneTrack(optionnal.get());
            }
        }
        event.consume();
    }

    private void deleteOneTrack(UIResourcePlayable mediaUI) {
        mediaUI.getEasyMedia().closePlayer();
        project.getEasyMediaList().remove(mediaUI.getEasyMedia());
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

    public List<UIResourcePlayable> getMediaUIList() {
        final List<Node> mediaUIs = tableLayout.getChildren();
        return mediaUIs.parallelStream().filter(node -> node instanceof ResourcePlayable).map(node -> (AudioMediaUI) node).collect(Collectors.toList());
    }

    @FXML
    private void menuPreferences(ActionEvent event) {
        //TODO a refaire
        event.consume();
    }

    protected void clearProject() {
        final List<UIResourcePlayable> mediaUIs = getMediaUIList();
        for (UIResourcePlayable mediaUI : mediaUIs) {
            deleteOneTrack(mediaUI);
        }
        project = null;
    }

    @FXML
    private void handleKeyCodePlay(KeyEvent event) {
        final List<UIResourcePlayable> mediaUIs = this.getMediaUIList();
        if (!event.isControlDown() && !event.isAltDown()) {
            LOG.trace("Key {} pressed ", event.getCode().getName());
            for (UIResourcePlayable mediaUI : mediaUIs) {
                if (mediaUI.getEasyMedia().getKeycode().equals(event.getCode())) {
                    mediaUI.playPause();
                }
            }
        }
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
                    openProject(new File(appProperties.getLastFileProject().toString()));
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
                    if (tableNode instanceof AudioMediaUI) mediaUIList.add((AudioMediaUI) tableNode);
                }
                for (Node tableNode : change.getRemoved()) {
                    if (tableNode instanceof AudioMediaUI) mediaUIList.remove(tableNode);
                }
            }
            LOG.debug(" {} MediaUI was added to the mainController MediaUIList size : {}", change.getAddedSize(), mediaUIList.size());
            LOG.debug(" {} MediaUI was removed to the mainController MediaUIList size : {}", change.getRemovedSize(), mediaUIList.size());
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
}
