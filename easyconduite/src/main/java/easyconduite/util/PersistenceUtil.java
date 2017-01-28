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

import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author V902832
 */
public class PersistenceUtil {

    static final Logger LOG = LogManager.getLogger(PersistenceUtil.class);
    
    private static final String SUFFIXE = ".ecp";

    private static Path lastDirectory;

    public enum TypeFileChooser {
        SAVE, OPEN_AUDIO, OPEN_PROJECT
    }

    /**
     *
     * @param file
     * @param audioTable
     * @throws IOException
     */
    public static void save(File file, AudioTable audioTable) throws IOException {

        try {

            JAXBContext context = JAXBContext.newInstance(AudioTable.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            audioTable.setName(file.getName());
            m.marshal(audioTable, file);

        } catch (PropertyException ex) {
            LOG.error("Erreur JAXB JAXB_FORMATTED_OUTPUT",ex);
        } catch (JAXBException ex) {
            LOG.error("Erreur JAXB",ex);
        }

    }

    /**
     * This method creates an AudioTable by file unmarshalling.
     *
     * @param file a xml file, suffixed by "ecp"
     * @return
     */
    public static AudioTable open(File file) {
        LOG.debug("Open file {}",file.getAbsolutePath());
        
        AudioTable audioTable = null;
        try {
            JAXBContext context = JAXBContext.newInstance(AudioTable.class);
            Unmarshaller um = context.createUnmarshaller();
            audioTable = (AudioTable) um.unmarshal(file);
            
            for (AudioMedia audioMedia  : audioTable.getAudioMediaList()) {
                audioMedia.setAudioFile(new File(audioMedia.getFilePathName()));
            }
            
            
        } catch (JAXBException ex) {
            LOG.error("Erreur JAXB",ex);
        }
        return audioTable;
    }

    /**
     *
     * @param scene
     * @return
     */
    public static File getSaveProjectFile(final Scene scene) {

        File file = getFileChooser(TypeFileChooser.SAVE).showSaveDialog(scene.getWindow());
        if (file != null && !file.getName().endsWith(SUFFIXE)) {
            file.renameTo(new File(file.getAbsolutePath() + SUFFIXE));
        }
        return file;
    }

    /**
     *
     * @param scene
     * @return
     */
    public static File getOpenProjectFile(final Scene scene) {

        File file = getFileChooser(TypeFileChooser.OPEN_PROJECT).showOpenDialog(scene.getWindow());
        return file;

    }

    /**
     *
     * @param scene
     * @return
     */
    public static File getOpenAudioFile(final Scene scene) {

        File file = getFileChooser(TypeFileChooser.OPEN_AUDIO).showOpenDialog(scene.getWindow());

        if (file != null) {
            lastDirectory = file.toPath().getParent();
        }

        return file;

    }

    private static FileChooser getFileChooser(TypeFileChooser type) {

        String title = null;
        String text = null;
        String extension[] = null;

        switch (type) {
            case OPEN_AUDIO:
                title = "Importer fichier audio";
                text = "Fichier audio";
                extension = new String[]{"*.mp3", "*.wav"};
                break;
            case OPEN_PROJECT:
                title = "Ouvrir projet EasyConduite";
                text = "Fichier *.ecp";
                extension = new String[]{"*.ecp"};
                break;
            case SAVE:
                title = "Sauvegarder projet EasyConduite";
                text = "Fichier *.ecp";
                extension = new String[]{"*.ecp"};
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
