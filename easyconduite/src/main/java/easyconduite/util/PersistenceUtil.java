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
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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

    /**
     * This method save AudioTable to a file.
     *
     * @param file file to save to.
     * @param audioTable
     * @throws easyconduite.objects.PersistenceException
     */
    public static void save(File file, AudioTable audioTable) throws PersistenceException {

        try {
            audioTable.setName(file.toPath().getFileName().toString());
            audioTable.setTablePathFile(getRelativePath(file).toString());
            serializeAsXML(file, audioTable);
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

    public static boolean isFileEmpty(AudioTable audioTable) throws IOException {
        if (audioTable.getTablePathFile() == null) {
            return true;
        }
        return !Paths.get(audioTable.getTablePathFile()).toRealPath().toFile().exists();
    }

    public static Path getRelativePath(File file) {

        Path path = null;
        try {
            Path currentPath = Paths.get(".").toRealPath().normalize();
            path = currentPath.relativize(file.toPath()).normalize();
        } catch (IOException ex) {
            LOG.error("Failed to ge relative path form", file, ex);
        }
        return path;
    }

    public static String getRealPathURIString(String relativePath) {

        String realUri = null;
        try {
            Path realPath = Paths.get(relativePath).toRealPath();
            realUri = realPath.toUri().toString();
        } catch (IOException ex) {
            LOG.error("Fail to get real path from {}", relativePath, ex);
        }
        return realUri;
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
