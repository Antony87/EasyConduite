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
package easyconduite.tools;

import easyconduite.exception.PersistenceException;
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
public class PersistenceHelper {

    static final Logger LOG = LogManager.getLogger(PersistenceHelper.class);

    /**
     * Suffixe for Easyconduite project file.
     */
    public static final String ECP_SUFFIXE = ".ecp";

    public enum FILE_TYPE {
        BIN, XML
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

    /**
     * This method writes an object to file (xml ou bin).
     *
     * @param <T>
     * @param file
     * @param t
     * @param type
     * @throws PersistenceException
     */
    public static <T> void writeToFile(File file, T t, FILE_TYPE type) throws PersistenceException {
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
                oos = new ObjectOutputStream(new FileOutputStream(file, false));
                oos.writeObject(t);
            } catch (FileNotFoundException ex) {
                LOG.error("File {} not found for Outputstream", file.getAbsolutePath(), ex);
                throw new PersistenceException(ex);
            } catch (IOException ex) {
                throw new PersistenceException(ex);
            } finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                } catch (IOException ex) {
                    LOG.error("Error occured during close Outputstream", ex);
                    throw new PersistenceException(ex);
                }
            }
        }
    }

    /**
     * This method reads an object from a xml or bin file.
     *
     * @param <T>
     * @param file
     * @param t
     * @param type
     * @return
     * @throws PersistenceException
     */
    public static <T> T readFromFile(File file, Class<T> t, FILE_TYPE type) throws PersistenceException {
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
                LOG.error("File {} not found for Inputstream", file.getAbsolutePath(), ex);
                throw new PersistenceException(ex);
            } catch (IOException | ClassNotFoundException ex) {
                throw new PersistenceException(ex);
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException ex) {
                    LOG.error("Error occured during close Inputstream", ex);
                    throw new PersistenceException(ex);
                }
            }
        }
        return objet;
    }

    /**
     * This method tests if a file ends with .ecp, if not, adds .ecp
     *
     * @param file
     * @return
     * @throws PersistenceException
     */
    public static File suffixForEcp(File file) throws PersistenceException {
        File checkedFile = null;
        if (file == null) {
            throw new PersistenceException("File is NULL");
        } else {
            final String path = file.getAbsolutePath();
            final String name = file.getName();
            if (path.endsWith(ECP_SUFFIXE) && name.endsWith(ECP_SUFFIXE)) {
                return file;
            }
            checkedFile = new File(path + ECP_SUFFIXE);
        }
        return checkedFile;
    }

    /**
     * Return a directory from File, or NULL.
     *
     * @param lastDirectory
     * @return
     */
    public static File getDirectory(File lastDirectory) {
        if (lastDirectory == null) {
            return null;
        }

        if (lastDirectory.isDirectory()) {
            return lastDirectory;
        }
        return null;
    }
}
