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
import easyconduite.objects.ApplicationProperties;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.stage.Stage;
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
public class ApplicationPropertiesHelper {

    private static ApplicationPropertiesHelper INSTANCE;

    private ApplicationProperties applicationProperties;

    private final ResourceBundle localBundle;

    private static final Logger LOG = LogManager.getLogger(ApplicationPropertiesHelper.class);

    private ApplicationPropertiesHelper() {
        LOG.debug("ApplicationProperties singleton construct");

        if (Constants.FILE_EASYCONDUITE_PROPS.exists()) {
            try {
                applicationProperties = PersistenceUtil.readFromFile(Constants.FILE_EASYCONDUITE_PROPS, ApplicationProperties.class, PersistenceUtil.FILE_TYPE.BIN);
                LOG.trace("easyconduite.dat file found. ApplicationProperties [{}]", applicationProperties);
            } catch (PersistenceException ex) {
                LOG.error("An error occured during easyconduite.dat loading", ex);
                setDefaultProperties();
            }
        } else {
            setDefaultProperties();
        }
        localBundle = ResourceBundle.getBundle(Constants.RESOURCE_BASENAME, applicationProperties.getLocale());
    }

    private void setDefaultProperties() {
        applicationProperties = new ApplicationProperties(800, 600, Level.OFF);
        applicationProperties.setLocale(new Locale(System.getProperty("user.language"), System.getProperty("user.country")));
        LOG.trace("easyconduite.dat file not found or an error occured, create default ApplicationProperties [{}]", applicationProperties);
    }

    /**
     * This method return ApplicationPropertiesHelper.
     *
     * @return
     */
    public ApplicationProperties getProperties() {
        return applicationProperties;
    }

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public ResourceBundle getLocalBundle() {
        return localBundle;
    }

    /**
     * This method returns an INSTANCE of ApplicationPropertiesHelper.
     *
     * @return
     */
    public static ApplicationPropertiesHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (ApplicationPropertiesHelper.class) {
                INSTANCE = new ApplicationPropertiesHelper();
            }
        }
        return INSTANCE;
    }

    public <T> void applyProperties(T t) {
        if (t instanceof Stage) {
            ((Stage) t).setWidth(applicationProperties.getWindowWith());
            ((Stage) t).setHeight(applicationProperties.getWindowHeight());
            ((Stage) t).setTitle("EasyConduite" + localBundle.getString("easyconduite.version"));
        }
    }
}
