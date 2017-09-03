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
import java.io.IOException;
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

    private static final String SUFFIXE = ".ecp";

    /**
     * This method save AudioTable to a file.
     *
     * @param file file to save to.
     * @param audioTable
     * @throws easyconduite.objects.PersistenceException
     */
    public static void save(File file, AudioTable audioTable) throws PersistenceException {

        // TODO forcer le suffixe a ecp.
        
        try {
            audioTable.setName(file.getName());
            audioTable.setTablePathFile(file.getAbsolutePath());
            serializeAsXML(file, audioTable);
            // audiotable saving, so set updated to false
            audioTable.setUpdated(false);
        } catch (IOException ex) {
            LOG.error("JAXB error", ex);
            throw new PersistenceException(ex);
        }
    }

    /**
     * This method creates an AudioTable by file unmarshalling.
     *
     * @param file a xml file, suffixed by "ecp"
     * @return
     * @throws easyconduite.objects.PersistenceException
     */
    public static AudioTable open(File file) throws PersistenceException {
        LOG.debug("Open file {}", file.getAbsolutePath());
        AudioTable audioTable = (AudioTable) deserialiseFromXML(file, AudioTable.class);
        return audioTable;
    }
    
        /**
     * This method tests when application may be closed without project's saving.
     *
     * @param audioTable
     * @return
     */
    public static boolean isClosable(AudioTable audioTable) {
        if(audioTable.getAudioMediaList().isEmpty()){
            LOG.debug("list vide");
            return true;
        }
        if (audioTable.isUpdated()) {
            LOG.debug("table update");
            return false;
        }
        if (Strings.isEmpty(audioTable.getTablePathFile())) {
            LOG.debug("pathfile vide");
            return false;
        }
        if (!PersistenceUtil.isFileExists(audioTable)) {
            LOG.debug("ifchier inexistant");
            return false;
        }
        return true;
    }

    /**
     * This method return TRUE if file describe by getTablePathFile EXISTS.
     * @param audioTable
     * @return 
     */
    public static boolean isFileExists(AudioTable audioTable) {
        if (Strings.isEmpty(audioTable.getTablePathFile())) {
            return false;
        }
        final Path filePath = Paths.get(audioTable.getTablePathFile());
        return Files.exists(filePath);
    }

    public static String getPathURIString(String path) {
        final Path realPath = Paths.get(path);
        return realPath.toUri().toString();
    }
    
    private static <T> void serializeAsXML(File file, T t) throws IOException {
        Class c = t.getClass();
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(t, file);
        } catch (JAXBException ex) {
            LOG.error("JAXB error", ex);
            throw new IOException(ex);
        }
    }

    private static <T> T deserialiseFromXML(File file, Class<T> t) throws PersistenceException {
        T o = null;
        try {
            JAXBContext context = JAXBContext.newInstance(t);
            Unmarshaller um = context.createUnmarshaller();
            o = (T) um.unmarshal(file);
        } catch (JAXBException ex) {
            LOG.error("JAXB error", ex);
            throw new PersistenceException(ex);
        }
        return o;
    }
}
