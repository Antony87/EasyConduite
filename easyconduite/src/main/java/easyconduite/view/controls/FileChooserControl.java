/*
 * Copyright (C) 2017 Antony Fons
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
package easyconduite.view.controls;

import easyconduite.exception.EasyconduiteException;
import easyconduite.EasyConduiteProperties;
import easyconduite.util.EasyConduitePropertiesHandler;
import javafx.stage.FileChooser;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 *
 * @author antony
 */
public class FileChooserControl {

    public enum Action {
        SAVE, SAVE_AS, OPEN_AUDIO, OPEN_PROJECT
    }

    public static class FileChooserBuilder {

        private String title;
        private String text;
        private String extension[];
        private Path initialDirectory;

        private final ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        
        private final EasyConduiteProperties applicationProperties = EasyConduitePropertiesHandler.getInstance().getApplicationProperties();

        public FileChooserBuilder() throws EasyconduiteException {

            initialDirectory = applicationProperties.getLastImportDir();
            if(initialDirectory==null){
                initialDirectory = FileSystems.getDefault().getPath(".");
            }
        }

        public FileChooserBuilder asType(Action type) {

            final String fileString = bundle.getString("easyfilechoose.file");
            switch (type) {
                case OPEN_AUDIO:
                    title = bundle.getString("easyfilechoose.title.import");
                    text = fileString + " audio";
                    extension = new String[]{"*.mp3", "*.wav"};
                    break;
                case OPEN_PROJECT:
                    title = bundle.getString("easyfilechoose.title.open");
                    text = fileString + " *.ecp";
                    extension = new String[]{"*.ecp"};
                    break;
                case SAVE:
                    title = bundle.getString("easyfilechoose.title.save");
                    text = fileString + " *.ecp";
                    extension = new String[]{"*.ecp"};
                    break;
                case SAVE_AS:
                    title = bundle.getString("easyfilechoose.title.saveas");
                    text = fileString + " *.ecp";
                    extension = new String[]{"*.ecp"};
                    break;
            }
            return this;
        }

        public FileChooser build() {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(title);
            fileChooser.setInitialDirectory(initialDirectory.toFile());
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(text, extension));
            return fileChooser;
        }

    }

}
