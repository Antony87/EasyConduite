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
import easyconduite.ui.AudioMediaUI;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
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

    public enum TypeFileChooser {

        SAVE, OPEN
    }

    public static void save(File file, AudioTable audioTable) throws IOException {

        XStream xstream = new XStream(new StaxDriver());
        xstream.processAnnotations(AudioMedia.class);
        xstream.processAnnotations(AudioTable.class);

        final String xml = xstream.toXML(audioTable);
        saveFile(xml, file);

    }

    public static File getFileChooser(final Scene scene, final TypeFileChooser type) {

        File file = null;
        if (type != null) {

            final FileChooser fileChooser = new FileChooser();
            if (type.equals(TypeFileChooser.OPEN)) {
                fileChooser.setTitle("Ouvrir fichier audio");
                fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Fichiers audio", "*.mp3", "*.wav"));
                file = fileChooser.showOpenDialog(scene.getWindow());
            }
            if (type.equals(TypeFileChooser.SAVE)) {
                fileChooser.setTitle("Sauvegarder projet");
                fileChooser.getExtensionFilters().add(new ExtensionFilter("Fichiers xml", "*.xml"));
                file = fileChooser.showSaveDialog(scene.getWindow());
            }
        }
        return file;

    }

    public static void prepareAudioTable(AudioTable audioTable, List<AudioMediaUI> audioMediaUIList) {

        for (Iterator<AudioMediaUI> iterator = audioMediaUIList.iterator(); iterator.hasNext();) {
            AudioMediaUI next = iterator.next();
            audioTable.addIfNotPresent(next.getAudioMedia());
        }

    }

    /**
     * Saves the content String to the specified file.
     *
     * @param content
     * @param file
     * @throws IOException thrown if an I/O error occurs opening or creating the file
     */
    private static void saveFile(String content, File file) throws IOException {

        try (
                BufferedWriter writer = Files.newBufferedWriter(file.toPath(), CHARSET)) {
            writer.write(content, 0, content.length());
        }
    }

}
