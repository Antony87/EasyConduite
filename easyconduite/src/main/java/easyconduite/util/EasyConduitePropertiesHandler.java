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

    private ResourceBundle bundle;

    static final Logger LOG = LogManager.getLogger(EasyConduitePropertiesHandler.class);

    private EasyConduitePropertiesHandler() {

        if (Constants.FILE_EASYCONDUITE_PROPS.exists()) {
            try {
                LOG.debug("easyconduite.dat file is found");
                properties = PersistenceUtil.readFromFile(Constants.FILE_EASYCONDUITE_PROPS, EasyconduiteProperty.class, PersistenceUtil.FILE_TYPE.BIN);
                easyconduite.Easyconduite.setLog4jLevel(properties.getLogLevel());
                LOG.trace("EasyconduiteProperty loaded [{}]", properties);
            } catch (PersistenceException ex) {
                LOG.error("An error occured during easyconduite.dat loading", ex);
            }
        } else {
            LOG.debug("easyconduite.dat file not found, create default EasyconduiteProperty object");
            properties = new EasyconduiteProperty(800, 600, Level.ALL);
            properties.setLocale(new Locale(System.getProperty("user.language"), System.getProperty("user.country")));
            LOG.trace("EasyconduiteProperty loaded [{}]", properties);
        }
        bundle = ResourceBundle.getBundle(Constants.RESOURCE_BASENAME);

    }

    /**
     * This method return EasyconduiteProperty.
     *
     * @return
     */
    public EasyconduiteProperty getProperties() {
        return properties;
    }

    public ResourceBundle getBundle() {
        return bundle;
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
