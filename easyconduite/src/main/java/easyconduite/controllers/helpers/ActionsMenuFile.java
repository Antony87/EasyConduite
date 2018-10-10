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
package easyconduite.controllers.helpers;

import easyconduite.controllers.MainController;
import easyconduite.exception.PersistenceException;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import easyconduite.objects.EasyconduiteProperty;
import easyconduite.ui.AudioMediaUI;
import easyconduite.ui.PreferencesDialogUI;
import easyconduite.ui.commons.ActionDialog;
import easyconduite.ui.commons.EasyconduiteUITools;
import easyconduite.ui.controls.EasyFileChooser;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.PersistenceUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author antony
 */
public class ActionsMenuFile {

    private static final Logger LOG = LogManager.getLogger(ActionsMenuFile.class);

    public static void closeProject(AudioTable audioTable, MainController controler) {
        List<AudioMedia> audioMediaList = audioTable.getAudioMediaList();
        while (audioMediaList.iterator().hasNext()) {
            AudioMedia audioMedia = audioMediaList.iterator().next();
            controler.removeChilds(audioMedia);
        }
    }

    public static void openPreferences() {
        ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        try {
            PreferencesDialogUI prefsUI = new PreferencesDialogUI(bundle);
            prefsUI.show();
        } catch (IOException ex) {
            LOG.debug("Error occurend ", ex);
        }
    }

    public static void saveFile(AudioTable audioTable, MainController controler) {
        ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        try {
            if (PersistenceUtil.isFileExists(audioTable.getTablePathFile())) {
                audioTable.setUpdated(false);
                final File fileAudioTable = Paths.get(audioTable.getTablePathFile()).toFile();
                PersistenceUtil.writeToFile(fileAudioTable, audioTable, PersistenceUtil.FILE_TYPE.XML);
            } else {
                saveAsFile(audioTable, controler);
            }
        } catch (PersistenceException ex) {
            ActionDialog.showWarning(bundle.getString("dialog.error.header"), bundle.getString("easyconduitecontroler.save.error"));
            LOG.error("An error occured", ex);
        }
    }

    public static void saveAsFile(AudioTable audioTable, MainController controler) {

        ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        EasyconduiteProperty userdatas = EasyConduitePropertiesHandler.getInstance().getProperties();

        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.SAVE_AS).build();
        fileChooser.setInitialDirectory(userdatas.getLastProjectDir());

        StackPane mainPane = controler.getMainPane();

        final File file = fileChooser.showSaveDialog(EasyconduiteUITools.getWindow(mainPane));
        if (file != null) {
            try {
                final File checkedFile = PersistenceUtil.suffixForEcp(file);
                audioTable.setUpdated(false);
                audioTable.setName(checkedFile.getName());
                audioTable.setTablePathFile(checkedFile.getAbsolutePath());
                PersistenceUtil.writeToFile(checkedFile, audioTable, PersistenceUtil.FILE_TYPE.XML);

                userdatas.setLastProjectDir(PersistenceUtil.getDirectory(file.getParentFile()));

                EasyconduiteUITools.updateWindowsTitle(EasyconduiteUITools.getScene(mainPane), "EasyConduite " + bundle.getString("easyconduite.version") + " : " + audioTable.getName());
            } catch (PersistenceException ex) {
                ActionDialog.showWarning(bundle.getString("dialog.error.header"), bundle.getString("easyconduitecontroler.save.error"));
                LOG.error("An error occured", ex);
            }
        }
    }

    public static void openFile(List<AudioMediaUI> audioMediaUIs, MainController controler, AudioTable audioTable) {

        ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        EasyconduiteProperty userdatas = EasyConduitePropertiesHandler.getInstance().getProperties();

        if (audioMediaUIs.size() > 0) {
            Optional<ButtonType> result = ActionDialog.showConfirmation(bundle.getString("easyconduitecontroler.open.header"), bundle.getString("easyconduitecontroler.open.content"));
            if (!result.isPresent() || result.get() == ButtonType.NO) {
                return;
            }
        }

        StackPane mainPane = controler.getMainPane();

        final FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_PROJECT).build();
        fileChooser.setInitialDirectory(userdatas.getLastProjectDir());
        final File file = fileChooser.showOpenDialog(EasyconduiteUITools.getWindow(mainPane));

        if (file != null) {
            // clear audiotable and childs (ui, player, etc)
            closeProject(audioTable, controler);
            //menuCloseProject(event);
            userdatas.setLastProjectDir(PersistenceUtil.getDirectory(file.getParentFile()));
            try {
                audioTable = (AudioTable) PersistenceUtil.readFromFile(file, AudioTable.class, PersistenceUtil.FILE_TYPE.XML);
                EasyconduiteUITools.updateWindowsTitle(EasyconduiteUITools.getScene(mainPane), "EasyConduite " + bundle.getString("easyconduite.version") + " : " + audioTable.getName());

                audioTable.getAudioMediaList().forEach((AudioMedia audioMedia) -> {
                    controler.addAudioMediaUI(audioMedia);
                });
            } catch (PersistenceException ex) {
                LOG.error("Error occured during opening project file[{}]", file, ex);
                ActionDialog.showWarning(bundle.getString("dialog.error.header"), bundle.getString("easyconduitecontroler.open.error"));
            }
        }
    }

}
