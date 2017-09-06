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
import easyconduite.objects.UserData;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * This class implements singleton design pattern and offers methods to manage
 * user datas.<br>
 * If ./user.dat file isn't find, an userdata object is instanciate with default
 * values.
 *
 * @author antony
 */
public class UserDataHandler {

    private UserData userDatas;

    private final ResourceBundle localeBundle;

    static final Logger LOG = LogManager.getLogger(UserDataHandler.class);

    private UserDataHandler() {

        if (Constants.FILE_USER_DATA.exists()) {
            try {
                LOG.debug("Found user.data file");
                userDatas = PersistenceUtil.readFromFile(Constants.FILE_USER_DATA, UserData.class, PersistenceUtil.FILE_TYPE.BIN);
                setLog4jLevel(userDatas.getLogLevel());
                LOG.trace("UserData [{}]", userDatas);
            } catch (PersistenceException ex) {
                LOG.error("An error occured during user.dat loading", ex);
            }
        } else {
            LOG.debug("user.data file not found, create default UserData object");
            userDatas = new UserData(800, 600, Level.ALL);
            userDatas.setLocale(new Locale(System.getProperty("user.language"), System.getProperty("user.country")));
            LOG.trace("UserData [{}]", userDatas);
        }
        
        localeBundle = ResourceBundle.getBundle(Constants.RESOURCE_BASENAME);
        
    }

    public ResourceBundle getLocaleBundle() {
        return localeBundle;
    }

    /**
     * This method return UserData.
     *
     * @return
     */
    public UserData getUserData() {
        return userDatas;
    }

    /**
     * This method sets log4j level for easyconduite logger.
     *
     * @param level
     */
    public final void setLog4jLevel(Level level) {

        userDatas.setLogLevel(level);
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final LoggerConfig loggerConfig = config.getLoggerConfig(LOG.getName());
        loggerConfig.setLevel(level);
        ctx.updateLoggers();

    }

    /**
     * This method returns an instance of UserDataHandler.
     *
     * @return
     */
    public static UserDataHandler getInstance() {
        return UserDataHolderHolder.INSTANCE;
    }

    private static class UserDataHolderHolder {

        private static final UserDataHandler INSTANCE = new UserDataHandler();
    }
}
