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

import easyconduite.objects.AudioTable;
import easyconduite.objects.PersistenceException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

/**
 *
 * @author V902832
 */
public class PersistenceUtil {

    static final Logger LOG = LogManager.getLogger(PersistenceUtil.class);

    private enum FILE_TYPE {
        BIN, XML
    }

    /**
     * This method saves AudioTable to a file.
     *
     * @param file file to saveAudioTable to.
     * @param audioTable
     * @throws easyconduite.objects.PersistenceException
     */
    public static void saveAudioTable(File file, AudioTable audioTable) throws PersistenceException {
        audioTable.setName(file.getName());
        audioTable.setTablePathFile(file.getAbsolutePath());
        writeToFile(file, audioTable, FILE_TYPE.XML);
        // audiotable saving, so set updated to false
        audioTable.setUpdated(false);
    }

    /**
     * This method creates an AudioTable by file unmarshalling.
     *
     * @param file a xml file, suffixed by "ecp"
     * @return
     * @throws easyconduite.objects.PersistenceException
     */
    public static AudioTable openAudioTable(File file) throws PersistenceException {
        LOG.debug("Open file {}", file.getAbsolutePath());
        AudioTable audioTable = (AudioTable) readFromFile(file, AudioTable.class, FILE_TYPE.XML);
        return audioTable;
    }

    /**
     * This method return TRUE if file describe by getTablePathFile EXISTS.
     *
     * @param path
     * @return
     */
    public static boolean isFileExists(String path) {
        if (Strings.isEmpty(path)) {
            return false;
        }
        final Path filePath = Paths.get(path);
        return Files.exists(filePath);
    }

    public static String getPathURIString(String path) {
        final Path realPath = Paths.get(path);
        return realPath.toUri().toString();
    }

    private static <T> void writeToFile(File file, T t, FILE_TYPE type) throws PersistenceException {
        Class c = t.getClass();
        if (type.equals(FILE_TYPE.XML)) {
            final JAXBContext context;
            try {
                context = JAXBContext.newInstance(c);
                final Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                m.marshal(t, file);
            } catch (JAXBException ex) {
                LOG.error("JAXB marshalling occured", ex);
                throw new PersistenceException(ex);
            }
        } else if (type.equals(FILE_TYPE.BIN)) {
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(t);
            } catch (FileNotFoundException ex) {
                LOG.error("File {} not found", file.getName(), ex);
                throw new PersistenceException(ex);
            } catch (IOException ex) {
                throw new PersistenceException(ex);
            } finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                } catch (IOException ex) {
                    throw new PersistenceException(ex);
                }
            }
        }
    }

    private static <T> T readFromFile(File file, Class<T> t, FILE_TYPE type) throws PersistenceException {
        T objet = null;
        if (type.equals(FILE_TYPE.XML)) {
            try {
                final JAXBContext context = JAXBContext.newInstance(t);
                final Unmarshaller um = context.createUnmarshaller();
                objet = (T) um.unmarshal(file);
            } catch (JAXBException ex) {
                LOG.error("JAXB error", ex);
                throw new PersistenceException(ex);
            }
        } else if (type.equals(FILE_TYPE.BIN)) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                objet = (T) ois.readObject();
            } catch (FileNotFoundException ex) {

            } catch (IOException | ClassNotFoundException ex) {

            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException ex) {
                    throw new PersistenceException(ex);
                }
            }
        }
        return objet;
    }

}
