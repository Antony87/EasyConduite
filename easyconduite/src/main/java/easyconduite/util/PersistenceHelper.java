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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import easyconduite.exception.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Deprecated
    public enum FILE_TYPE {
        BIN, XML
    }

    public static <T> void saveToJson(T o, File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.WRAPPER_ARRAY);
        mapper.writeValue(file,o);
    }

    //TODO javadoc
    public static <T> T openFromJson(File file, Class<T> o) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.WRAPPER_ARRAY);
        T deserialized = (T) mapper.readValue(file, o);;
        return deserialized;
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

    /**
     * This method return TRUE if file describe by getTablePathFile EXISTS.
     *
     * @param path
     * @return
     */
    public static boolean isFileExists(Path path) {
        if(path==null) return false;
        return Files.exists(path);
    }

    public static String getPathURIString(String path) {
        final Path realPath = Paths.get(path);
        return realPath.toUri().toString();
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
