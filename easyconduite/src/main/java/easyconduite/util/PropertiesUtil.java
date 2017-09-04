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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class implements singleton design pattern and offers methods for manage
 * properties.
 *
 * @author antony
 */
public class PropertiesUtil {

    private Properties props;

    private static final String PROPS_DIR = "../conf/";

    private static final String PROPS_FILE = "easyconduite.properties";

    static final Logger LOG = LogManager.getLogger(PropertiesUtil.class);

    private PropertiesUtil() {

        props = new Properties();
        
        try {
            File file = Paths.get(PROPS_DIR + PROPS_FILE).toFile();
            InputStream inputStream = null;
            if (file.exists()) {
                inputStream = new FileInputStream(PROPS_DIR + PROPS_FILE);
                LOG.trace("Loading properties from files {}", file.getPath());
            } else {
                inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(PROPS_FILE);
                if (inputStream == null) {
                    LOG.error("Loading {} from classpath return NULL", PROPS_FILE);
                    throw new IOException();
                }
                LOG.trace("Loading properties from classpath {}", PROPS_FILE);
            }
            props.load(inputStream);
        } catch (IOException ex) {
            LOG.error("Error occured during loading {}", PROPS_FILE, ex);
        }

    }

    private static class PropertiesLoaderHolder {

        private final static PropertiesUtil instance = new PropertiesUtil();
    }

    /**
     * This method return an unique instance of PropertiesUtil.
     *
     * @return
     */
    public static PropertiesUtil getInstance() {
        return PropertiesLoaderHolder.instance;
    }

    /**
     * This method return value of property bye a key.
     *
     * @param key
     * @return
     */
    public String getValue(String key) {
        return props.getProperty(key);
    }

}
