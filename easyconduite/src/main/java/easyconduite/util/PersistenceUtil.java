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
import java.nio.file.Paths;
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

    /**
     *
     * @param file
     * @param audioTable
     * @throws IOException
     */
    public static void save(File file, AudioTable audioTable) throws IOException {
        audioTable.setName(file.getName());
        audioTable.setTablePathFile(file.toPath().toUri());
        serializeAsXML(file, audioTable);
    }

    private static void serializeAsXML(File file, AudioTable audioTable) {

        try {
            JAXBContext context = JAXBContext.newInstance(AudioTable.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(audioTable, file);
        } catch (PropertyException ex) {
            LOG.error("Erreur JAXB JAXB_FORMATTED_OUTPUT", ex);
        } catch (JAXBException ex) {
            LOG.error("Erreur JAXB", ex);
        }
    }

    /**
     * This method creates an AudioTable by file unmarshalling.
     *
     * @param file a xml file, suffixed by "ecp"
     * @return
     */
    public static AudioTable open(File file) {
        LOG.debug("Open file {}", file.getAbsolutePath());

        AudioTable audioTable = null;
        try {
            JAXBContext context = JAXBContext.newInstance(AudioTable.class);
            Unmarshaller um = context.createUnmarshaller();
            audioTable = (AudioTable) um.unmarshal(file);

            for (AudioMedia audioMedia : audioTable.getAudioMediaList()) {
                audioMedia.setAudioFile(new File(audioMedia.getFilePathName()));
            }

        } catch (JAXBException ex) {
            LOG.error("Erreur JAXB", ex);
        }
        return audioTable;
    }


    public static boolean isFileEmpty(AudioTable audioTable) {
        if (audioTable.getTablePathFile() == null) {
            return true;
        }
        return !Paths.get(audioTable.getTablePathFile()).toFile().exists();
    }
}
