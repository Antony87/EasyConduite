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
import easyconduite.objects.ApplicationProperties;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.tools.PersistenceHelper;
import easyconduite.view.AudioMediaUI;
import easyconduite.view.commons.UITools;
import easyconduite.view.controls.ActionDialog;
import easyconduite.view.controls.EasyFileChooser;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

/**
 *
 * @author antony
 */
public class AudioTableHelper {

    private final static String MSG_ERROR_SAVE = "easyconduitecontroler.save.error";
    private final static String MSG_DIAG_ERROR = "dialog.error.header";
    private final static String MSG_ERROR_OPEN = "easyconduitecontroler.open.error";

    private static final ApplicationProperties APP_PROPERTIES = ApplicationPropertiesHelper.getInstance().getProperties();

    private final static ResourceBundle LOCAL = ApplicationPropertiesHelper.getInstance().getLocalBundle();

    public static void cleanChilds(AudioTable audioTable) {

        Set<UUID> uuids = new CopyOnWriteArraySet<>();
        List<AudioMedia> audioMedias = new CopyOnWriteArrayList<>(audioTable.getAudioMediaList());
        audioMedias.forEach((AudioMedia t) -> {
            uuids.add(t.getUniqueId());
        });
        audioMedias.forEach((t) -> {
            List<UUID> uuidChildsBegin = new CopyOnWriteArrayList<>(t.getUuidChildBegin());
            uuidChildsBegin.forEach((b) -> {
                if (!uuids.contains(b)) {
                    t.getUuidChildBegin().remove(b);
                }
            });
        });
    }

    public static void importAudioFromFile(AudioTable audioTable, MainController controller) {

        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_AUDIO).build();
        File file = fileChooser.showOpenDialog(UITools.getScene(controller.getMainPane()).getWindow());

        if (file != null) {
            AudioMedia audioMedia = new AudioMedia(file);
            audioTable.getAudioMediaList().add(audioMedia);
            audioTable.setUpdated(true);
            controller.createAudioMediaView(audioMedia);
            APP_PROPERTIES.setLastImportDir(PersistenceHelper.getDirectory(file.getParentFile()));
        }
    }

    public static void saveToFile(AudioTable audioTable, MainController controller) {
        try {
            if (PersistenceHelper.isFileExists(audioTable.getTablePathFile())) {
                audioTable.setUpdated(false);
                final File fileAudioTable = Paths.get(audioTable.getTablePathFile()).toFile();
                PersistenceHelper.writeToFile(fileAudioTable, audioTable, PersistenceHelper.FILE_TYPE.XML);
            } else {
                saveAsToFile(audioTable, controller);
            }
        } catch (PersistenceException ex) {
            ActionDialog.showWarning(LOCAL.getString(MSG_DIAG_ERROR), LOCAL.getString(MSG_ERROR_SAVE));
        }
    }

    public static void saveAsToFile(AudioTable audioTable, MainController controller) {

        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.SAVE_AS).build();
        final File file = fileChooser.showSaveDialog(UITools.getWindow(controller.getMainPane()));
        if (file != null) {
            try {
                final File checkedFile = PersistenceHelper.suffixForEcp(file);
                audioTable.setUpdated(false);
                audioTable.setName(checkedFile.getName());
                audioTable.setTablePathFile(checkedFile.getAbsolutePath());
                PersistenceHelper.writeToFile(checkedFile, audioTable, PersistenceHelper.FILE_TYPE.XML);
                APP_PROPERTIES.setLastProjectDir(PersistenceHelper.getDirectory(file.getParentFile()));
                UITools.updateWindowsTitle(controller.getMainPane(), audioTable.getName());
            } catch (PersistenceException ex) {
                ActionDialog.showWarning(LOCAL.getString(MSG_DIAG_ERROR), LOCAL.getString(MSG_ERROR_SAVE));
            }
        }
    }

    public static AudioTable getFromFile(AudioTable audioTable, MainController controller) {

        Pane mainPane = controller.getMainPane();
        final FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_PROJECT).build();
        final File file = fileChooser.showOpenDialog(UITools.getWindow(mainPane));

        if (file != null) {
            APP_PROPERTIES.setLastProjectDir(PersistenceHelper.getDirectory(file.getParentFile()));

            try {
                audioTable = null;
                audioTable = (AudioTable) PersistenceHelper.readFromFile(file, AudioTable.class, PersistenceHelper.FILE_TYPE.XML);
                UITools.updateWindowsTitle(mainPane, audioTable.getName());
                audioTable.getAudioMediaList().forEach((audioMedia) -> {
                    AudioMediaUI ui = controller.createAudioMediaView(audioMedia);
                });
                AudioTableHelper.cleanChilds(audioTable);
            } catch (PersistenceException ex) {
                ActionDialog.showWarning(LOCAL.getString(MSG_DIAG_ERROR), LOCAL.getString(MSG_ERROR_OPEN));
            }
        }
        return audioTable;
    }

}
