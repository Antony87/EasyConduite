/*
 * Copyright (C) 2014 A Fons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package easyconduite.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author V902832
 */
public class PersistenceUtil {

    /**
     * The character set. UTF-8 works good for windows, mac and Umlaute.
     */
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static final String SUFFIXE = "xml";

    private static Path lastDirectory;

    private static final Logger logger = Logger.getLogger(PersistenceUtil.class.getName());

    public enum TypeFileChooser {

        SAVE, OPEN_AUDIO, OPEN_PROJECT
    }

    public static void save(File file, AudioTable audioTable) throws IOException {

        XStream xstream = new XStream(new StaxDriver());
        xstream.processAnnotations(AudioMedia.class);
        xstream.processAnnotations(AudioTable.class);

        final String xml = xstream.toXML(audioTable);
        saveFile(xml, file);

    }

    public static AudioTable open(File file) {

        XStream xstream = new XStream(new StaxDriver());
        xstream.processAnnotations(AudioMedia.class);
        xstream.processAnnotations(AudioTable.class);

        AudioTable audioTable = (AudioTable) xstream.fromXML(file);
        List<AudioMedia> audioMedias = audioTable.getAudioMediaList();
        for (AudioMedia audioMedia : audioMedias) {
            audioMedia.setAudioFile(new File(audioMedia.getFilePathName()));
        }

        return audioTable;

    }

    
    public static File getSaveProjectFile(final Scene scene){
        
        File file = getFileChooser(TypeFileChooser.SAVE).showSaveDialog(scene.getWindow());
        return file;
        
    }

    public static File getOpenProjectFile(final Scene scene) {

        File file = getFileChooser(TypeFileChooser.OPEN_PROJECT).showOpenDialog(scene.getWindow());
        return file;

    }

    public static File getOpenAudioFile(final Scene scene) {


        File file = getFileChooser(TypeFileChooser.OPEN_AUDIO).showOpenDialog(scene.getWindow());

        if (file != null) {
            lastDirectory = file.toPath().getParent();
            logger.log(Level.INFO, "lastDirectory {0}", lastDirectory.toString());
        }

        return file;

    }

    /**
     * Saves the content String to the specified file.
     *
     * @param content
     * @param file
     * @throws IOException thrown if an I/O error occurs opening or creating the
     * file
     */
    private static void saveFile(String content, File file) throws IOException {

        if (file != null) {
            try (
                    BufferedWriter writer = Files.newBufferedWriter(file.toPath(), CHARSET)) {
                writer.write(content, 0, content.length());
            }
        }
    }


    private static FileChooser getFileChooser(TypeFileChooser type) {
        String title = null;
        String text = null;
        String extension[] = null;

        switch (type) {
            case OPEN_AUDIO:
                title = "Ouvrir fichier audio";
                text = "Fichier audio";
                extension = new String[]{"*.mp3", "*.wav"};
                break;
            case OPEN_PROJECT:
                title = "Ouvrir fichier projet";
                text = "Fichier xml";
                extension = new String[]{"*.xml"};
                break;
            case SAVE:
                title = "Sauvegarder fichier projet";
                text = "Fichier xml";
                extension = new String[]{"*.xml"};
                break;
        }
        
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter(text, extension));
        if (lastDirectory != null) {
            fileChooser.setInitialDirectory(new File(lastDirectory.toUri()));
        }
        
        return fileChooser;
    }
}
