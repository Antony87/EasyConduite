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

import easyconduite.exception.EasyconduiteException;
import easyconduite.objects.EasyConduiteProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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

    private EasyConduitePropertiesHandler() throws EasyconduiteException {
        LOG.debug("EasyConduiteProperties singleton construct");
        try {
            if (Constants.FILE_EASYCONDUITE_PROPS.exists()) {
                applicationProperties = PersistenceHelper.openFromJson(Constants.FILE_EASYCONDUITE_PROPS,EasyConduiteProperties.class);
                LOG.trace("easyconduite.dat file found. [{}]", applicationProperties);
            } else {
                LOG.trace("easyconduite.dat file not found, create default EasyConduiteProperties");
                applicationProperties = new EasyConduiteProperties();
                PersistenceHelper.saveToJson(applicationProperties,Constants.FILE_EASYCONDUITE_PROPS);
            }

            applicationProperties.changeCompteurProperty().addListener((observableValue, number, t1) -> {
                        LOG.trace("ApplicationProperties [{}] changes", applicationProperties);
                        try {
                            PersistenceHelper.saveToJson(applicationProperties,Constants.FILE_EASYCONDUITE_PROPS);
                        } catch (IOException e) {
                            throw new RuntimeException();
                        }
                    }
            );

        } catch (IOException e) {
            LOG.error("Erreur lecture/ecriture json",e);
            throw new EasyconduiteException();
        }


        localBundle = ResourceBundle.getBundle(Constants.RESOURCE_BASENAME, applicationProperties.getLocale());


    }

    public EasyConduiteProperties getApplicationProperties() {
        return applicationProperties;
    }

    public ResourceBundle getLocalBundle() {
        return localBundle;
    }

    /**
     * This method returns an INSTANCE of ApplicationPropertiesHelper.
     *
     * @return fourni une instance unique
     */
    public static EasyConduitePropertiesHandler getInstance() throws EasyconduiteException {
        if (INSTANCE == null) {
            synchronized (EasyConduitePropertiesHandler.class) {
                INSTANCE = new EasyConduitePropertiesHandler();
            }
        }
        return INSTANCE;
    }

}
