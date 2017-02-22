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
package easyconduite.util;

import javafx.stage.FileChooser;

/**
 *
 * @author antony
 */
public class EasyFileChooser {

    public final static String SAVE_TITLE = "Enregistrer le projet";
    public final static String SAVEAS_TITLE = "Enregistrer le projet sous...";
    public final static String OPEN_TITLE = "Ouvrir un projet";
    public final static String IMPORT_TITLE = "Importer un fichier audio";

    public enum Type {

        SAVE, SAVE_AS, OPEN_AUDIO, OPEN_PROJECT
    }

    public static class FileChooserBuilder {

        private String title;
        private String text;
        private String extension[];

        public FileChooserBuilder asType(Type type) {
            switch (type) {
                case OPEN_AUDIO:
                    title = IMPORT_TITLE;
                    text = "Fichier audio";
                    extension = new String[]{"*.mp3", "*.wav"};
                    break;
                case OPEN_PROJECT:
                    title = OPEN_TITLE;
                    text = "Fichier *.ecp";
                    extension = new String[]{"*.ecp"};
                    break;
                case SAVE:
                    title = SAVE_TITLE;
                    text = "Fichier *.ecp";
                    extension = new String[]{"*.ecp"};
                    break;
                case SAVE_AS:
                    title = SAVEAS_TITLE;
                    text = "Fichier *.ecp";
                    extension = new String[]{"*.ecp"};
                    break;
            }
            return this;
        }

        public FileChooserBuilder title(String title) {
            this.title = title;
            return this;
        }

        public FileChooserBuilder text(String text) {
            this.text = text;
            return this;
        }

        public FileChooserBuilder extensions(String[] extensions) {
            this.extension = extensions;
            return this;
        }

        public FileChooser build() {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(title);
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(text, extension));
            return fileChooser;
        }

    }

}
