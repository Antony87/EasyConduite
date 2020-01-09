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
package easyconduite.objects;

import easyconduite.exception.PersistenceException;
import easyconduite.objects.project.EasyTable;
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.tools.PersistenceHelper;
import easyconduite.view.controls.ActionDialog;
import easyconduite.view.controls.EasyFileChooser;
import java.io.File;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javafx.stage.FileChooser;

/**
 *
 * @author antony
 */
public class AudioTableWrapper {

    private static final ApplicationProperties APP_PROPERTIES = ApplicationPropertiesHelper.getInstance().getProperties();

    private EasyTable easyTable;

    private AudioTableWrapper() {
        easyTable = new EasyTable();
    }

    public static AudioTableWrapper getInstance() {
        return AudioTableWrapperHolder.INSTANCE;
    }

    private static class AudioTableWrapperHolder {

        private static final AudioTableWrapper INSTANCE = new AudioTableWrapper();
    }

    public EasyTable getEasyTable() {
        return easyTable;
    }

    public void setEasyTable(EasyTable easyTable) {
        this.easyTable = easyTable;
    }

    public void clearAudioTable() {
        easyTable = new EasyTable();
    }
    
    public void removeAudioMedia(AudioMedia audioMedia){
        synchronized (audioMedia){
            easyTable.getAudioMediaList().remove(audioMedia);
        }
    }

    public AudioMedia getAudioFromFile() {

        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_AUDIO).build();
        File file = fileChooser.showOpenDialog(APP_PROPERTIES.getCurrentWindow());
        AudioMedia audioMedia = null;
        if (file != null) {
            audioMedia = new AudioMedia(file);
            easyTable.getAudioMediaList().add(audioMedia);
            APP_PROPERTIES.setLastImportDir(PersistenceHelper.getDirectory(file.getParentFile()));
        }
        return audioMedia;
    }

    public void saveToFile() {
        try {
            if (PersistenceHelper.isFileExists(easyTable.getTablePathFile())) {
                easyTable.setUpdated(false);
                final File fileAudioTable = Paths.get(easyTable.getTablePathFile()).toFile();
                PersistenceHelper.writeToFile(fileAudioTable, easyTable, PersistenceHelper.FILE_TYPE.XML);
            } else {
                saveAsToFile();
            }
        } catch (PersistenceException e) {
            final ResourceBundle locale = ApplicationPropertiesHelper.getInstance().getLocalBundle();
            ActionDialog.showWarning(locale.getString("dialog.error.header"), locale.getString("easyconduitecontroler.save.error"));
        }
    }

    public void saveAsToFile() {

        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.SAVE_AS).build();
        final File file = fileChooser.showSaveDialog(APP_PROPERTIES.getCurrentWindow());
        if (file != null) {
            try {
                //EasyTable easyTable = AudioTableWrapper.getInstance().getEasyTable();
                final File checkedFile = PersistenceHelper.suffixForEcp(file);
                easyTable.setUpdated(false);
                easyTable.setName(checkedFile.getName());
                easyTable.setTablePathFile(checkedFile.getAbsolutePath());
                PersistenceHelper.writeToFile(checkedFile, easyTable, PersistenceHelper.FILE_TYPE.XML);
                APP_PROPERTIES.setLastProjectDir(PersistenceHelper.getDirectory(file.getParentFile()));
                //UITools.updateWindowsTitle(controller.getMainPane(), easyTable.getName());
            } catch (PersistenceException e) {
                final ResourceBundle locale = ApplicationPropertiesHelper.getInstance().getLocalBundle();
                ActionDialog.showWarning(locale.getString("dialog.error.header"), locale.getString("easyconduitecontroler.save.error"));
            }
        }
    }

    public EasyTable getFromFile(File file) {
        if (file == null) {
            final FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_PROJECT).build();
            file = fileChooser.showOpenDialog(APP_PROPERTIES.getCurrentWindow());
        }
        if (file != null) {
            APP_PROPERTIES.setLastProjectDir(PersistenceHelper.getDirectory(file.getParentFile()));
            try {
                AudioTableWrapper.getInstance().setEasyTable((EasyTable) PersistenceHelper.readFromFile(file, EasyTable.class, PersistenceHelper.FILE_TYPE.XML));
                //easyTable.setNext(controller);

                //UITools.updateWindowsTitle(mainPane, easyTable.getName());
                //AudioTableHelper.cleanChilds(easyTable);
                APP_PROPERTIES.setLastFileProject(file);
            } catch (PersistenceException ex) {
                final ResourceBundle locale = ApplicationPropertiesHelper.getInstance().getLocalBundle();
                ActionDialog.showWarning(locale.getString("dialog.error.header"), locale.getString("easyconduitecontroler.open.error"));
            }
        }
        return getEasyTable();
    }

}
