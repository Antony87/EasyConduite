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

import easyconduite.objects.EasyConduiteProperties;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class implements singleton design pattern and offers methods to manage
 * user datas.<br>
 * If ./user.dat file isn't find, an userdata object is instanciate with default
 * values.
 *
 * @author antony
 */
public class EasyConduitePropertiesHandler {

    private static EasyConduitePropertiesHandler INSTANCE;

    private EasyConduiteProperties applicationProperties;

    private final ResourceBundle localBundle;

    private static final Logger LOG = LogManager.getLogger(EasyConduitePropertiesHandler.class);

    private EasyConduitePropertiesHandler() throws IOException, ClassNotFoundException {
        LOG.debug("ApplicationProperties singleton construct");

        if (Constants.FILE_EASYCONDUITE_PROPS.exists()) {
            applicationProperties = (EasyConduiteProperties) PersistenceHelper.openXml(Constants.FILE_EASYCONDUITE_PROPS);
            LOG.trace("easyconduite.dat file found. [{}]", applicationProperties);
        } else {
            LOG.trace("easyconduite.dat file not found, create default EasyConduiteProperties");
            applicationProperties = new EasyConduiteProperties();
            applicationProperties.setWindowWith(800);
            applicationProperties.setWindowHeight(600);
            applicationProperties.setLogLevel(Level.OFF);
            applicationProperties.setLocale(new Locale(System.getProperty("user.language"), System.getProperty("user.country")));
            PersistenceHelper.saveXml(applicationProperties,Constants.FILE_EASYCONDUITE_PROPS);
        }
        localBundle = ResourceBundle.getBundle(Constants.RESOURCE_BASENAME, applicationProperties.getLocale());

        applicationProperties.changeCompteurProperty().addListener((observableValue, number, t1) -> {
                    LOG.trace("ApplicationProperties [{}] changes", applicationProperties);
                    try {
                        PersistenceHelper.saveXml(applicationProperties,Constants.FILE_EASYCONDUITE_PROPS);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    /**
     * This method return ApplicationPropertiesHelper.
     *
     * @return
     */
    public EasyConduiteProperties getProperties() {
        return applicationProperties;
    }

    public ResourceBundle getLocalBundle() {
        return localBundle;
    }

    /**
     * This method returns an INSTANCE of ApplicationPropertiesHelper.
     *
     * @return
     */
    public static EasyConduitePropertiesHandler getInstance() throws IOException, ClassNotFoundException {
        if (INSTANCE == null) {
            synchronized (EasyConduitePropertiesHandler.class) {
                INSTANCE = new EasyConduitePropertiesHandler();
            }
        }
        return INSTANCE;
    }

}
