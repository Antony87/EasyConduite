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

import easyconduite.objects.AudioTable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author V902832
 */
public class PersistenceUtil {

    private static final String SUFFIXE = ".ecp";

    private static Path lastDirectory;

    private static final String CLASSNAME = PersistenceUtil.class.getName();

    private static final Logger LOGGER = Config.getCustomLogger(CLASSNAME);

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
        LOGGER.log(Level.INFO, "Save project with file.name[{0}] AudioTable.name[{1}]", new Object[]{file.getName(), audioTable.getName()});

        // ObjectOutputStream oos = null;
        try {
//            final FileOutputStream fichier = new FileOutputStream(file);
//            oos = new ObjectOutputStream(fichier);
//            oos.writeObject(audioTable);
//            oos.flush();

            JAXBContext context = JAXBContext.newInstance(AudioTable.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            audioTable.setName(file.getName());
            m.marshal(audioTable, file);

        } catch (PropertyException ex) {
            LOGGER.log(Level.SEVERE, "Erreur JAXB JAXB_FORMATTED_OUTPUT");
        } catch (JAXBException ex) {
            Logger.getLogger(PersistenceUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method creates an AudioTable by file unmarshalling.
     *
     * @param file a xml file, suffixed by "ecp"
     * @return
     */
    public static AudioTable open(File file) {

        LOGGER.log(Level.INFO, "Open {0} AudioTable file", file.getName());

        AudioTable audioTable = null;
        try {
            JAXBContext context = JAXBContext.newInstance(AudioTable.class);
            Unmarshaller um = context.createUnmarshaller();
            audioTable = (AudioTable) um.unmarshal(file);
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, "Une erreur est survenue lors de l'ouverture du fichier {0}",file.getName());
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
            LOGGER.log(Level.INFO, "lastDirectory {0}", lastDirectory.toString());
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
