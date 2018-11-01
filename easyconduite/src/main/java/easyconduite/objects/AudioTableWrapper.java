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
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.tools.PersistenceHelper;
import easyconduite.view.controls.ActionDialog;
import easyconduite.view.controls.EasyFileChooser;
import java.io.File;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;

/**
 *
 * @author antony
 */
public class AudioTableWrapper {

    private static final ApplicationProperties APP_PROPERTIES = ApplicationPropertiesHelper.getInstance().getProperties();

    private AudioTable audioTable;

    private AudioTableWrapper() {
        audioTable = new AudioTable();
    }

    public static AudioTableWrapper getInstance() {
        return AudioTableWrapperHolder.INSTANCE;
    }

    private static class AudioTableWrapperHolder {

        private static final AudioTableWrapper INSTANCE = new AudioTableWrapper();
    }

    public AudioTable getAudioTable() {
        return audioTable;
    }

    public void setAudioTable(AudioTable audioTable) {
        this.audioTable = audioTable;
    }

    public void clearAudioTable() {
        audioTable = new AudioTable();
    }
    
    public void removeAudioMedia(AudioMedia audioMedia){
        synchronized (audioMedia){
            audioTable.getAudioMediaList().remove(audioMedia);
        }
    }

    public AudioMedia getAudioFromFile() {

        FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_AUDIO).build();
        File file = fileChooser.showOpenDialog(APP_PROPERTIES.getCurrentWindow());
        AudioMedia audioMedia = null;
        if (file != null) {
            audioMedia = new AudioMedia(file);
            audioTable.getAudioMediaList().add(audioMedia);
            APP_PROPERTIES.setLastImportDir(PersistenceHelper.getDirectory(file.getParentFile()));
        }
        return audioMedia;
    }

    public void saveToFile() {
        try {
            if (PersistenceHelper.isFileExists(audioTable.getTablePathFile())) {
                audioTable.setUpdated(false);
                final File fileAudioTable = Paths.get(audioTable.getTablePathFile()).toFile();
                PersistenceHelper.writeToFile(fileAudioTable, audioTable, PersistenceHelper.FILE_TYPE.XML);
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
                //AudioTable audioTable = AudioTableWrapper.getInstance().getAudioTable();
                final File checkedFile = PersistenceHelper.suffixForEcp(file);
                audioTable.setUpdated(false);
                audioTable.setName(checkedFile.getName());
                audioTable.setTablePathFile(checkedFile.getAbsolutePath());
                PersistenceHelper.writeToFile(checkedFile, audioTable, PersistenceHelper.FILE_TYPE.XML);
                APP_PROPERTIES.setLastProjectDir(PersistenceHelper.getDirectory(file.getParentFile()));
                //UITools.updateWindowsTitle(controller.getMainPane(), audioTable.getName());
            } catch (PersistenceException e) {
                final ResourceBundle locale = ApplicationPropertiesHelper.getInstance().getLocalBundle();
                ActionDialog.showWarning(locale.getString("dialog.error.header"), locale.getString("easyconduitecontroler.save.error"));
            }
        }
    }

    public AudioTable getFromFile(File file) {
        if (file == null) {
            final FileChooser fileChooser = new EasyFileChooser.FileChooserBuilder().asType(EasyFileChooser.Type.OPEN_PROJECT).build();
            file = fileChooser.showOpenDialog(APP_PROPERTIES.getCurrentWindow());
        }
        if (file != null) {
            APP_PROPERTIES.setLastProjectDir(PersistenceHelper.getDirectory(file.getParentFile()));
            try {
                AudioTableWrapper.getInstance().setAudioTable((AudioTable) PersistenceHelper.readFromFile(file, AudioTable.class, PersistenceHelper.FILE_TYPE.XML));
                //audioTable.setNext(controller);

                //UITools.updateWindowsTitle(mainPane, audioTable.getName());
                //AudioTableHelper.cleanChilds(audioTable);
                APP_PROPERTIES.setLastFileProject(file);
            } catch (PersistenceException ex) {
                final ResourceBundle locale = ApplicationPropertiesHelper.getInstance().getLocalBundle();
                ActionDialog.showWarning(locale.getString("dialog.error.header"), locale.getString("easyconduitecontroler.open.error"));
            }
        }
        return getAudioTable();
    }

}
