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

import easyconduite.exception.PersistenceException;
import easyconduite.objects.EasyconduiteProperty;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class implements singleton design pattern and offers methods to manage
 * user datas.<br>
 * If ./user.dat file isn't find, an userdata object is instanciate with default
 * values.
 *
 * @author antony
 */
public class EasyConduitePropertiesHandler {

    private EasyconduiteProperty properties;

    private final ResourceBundle localBundle;

    private static final Logger LOG = LogManager.getLogger(EasyConduitePropertiesHandler.class);

    private EasyConduitePropertiesHandler() {
        LOG.debug("EasyConduitePropertiesHandler singleton construct");

        if (Constants.FILE_EASYCONDUITE_PROPS.exists()) {
            try {
                properties = PersistenceUtil.readFromFile(Constants.FILE_EASYCONDUITE_PROPS, EasyconduiteProperty.class, PersistenceUtil.FILE_TYPE.BIN);
                LOG.trace("easyconduite.dat file found. EasyconduiteProperty [{}]",properties);
            } catch (PersistenceException ex) {
                LOG.error("An error occured during easyconduite.dat loading", ex);
                setDefaultProperties();
            }
        } else {
            setDefaultProperties();
        }
        localBundle = ResourceBundle.getBundle(Constants.RESOURCE_BASENAME, properties.getLocale());
    }

    private void setDefaultProperties() {
        properties = new EasyconduiteProperty(800, 600, Level.OFF);
        properties.setLocale(new Locale(System.getProperty("user.language"), System.getProperty("user.country")));
        LOG.trace("easyconduite.dat file not found or an error occured, create default EasyconduiteProperty [{}]",properties);
    }

    /**
     * This method return EasyconduiteProperty.
     *
     * @return
     */
    public EasyconduiteProperty getProperties() {
        return properties;
    }

    public ResourceBundle getLocalBundle() {
        return localBundle;
    }

    /**
     * This method returns an instance of EasyConduitePropertiesHandler.
     *
     * @return
     */
    public static EasyConduitePropertiesHandler getInstance() {
        return UserDataHolderHolder.INSTANCE;
    }

    private static class UserDataHolderHolder {

        private static final EasyConduitePropertiesHandler INSTANCE = new EasyConduitePropertiesHandler();
    }
}
