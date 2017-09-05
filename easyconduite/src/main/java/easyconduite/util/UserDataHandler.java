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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
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

    private UserData userData;

    private static final Path FILE_PATH = Paths.get("user.dat");

    static final Logger LOG = LogManager.getLogger(UserDataHandler.class);

    private UserDataHandler() {

        if (Files.exists(FILE_PATH)) {
            try {
                userData = PersistenceUtil.readFromFile(FILE_PATH.toFile(), UserData.class, PersistenceUtil.FILE_TYPE.BIN);
            } catch (PersistenceException ex) {
                LOG.error("An error occured during user.dat loading", ex);
            }
        } else {
            userData = new UserData();
            userData.setWindowWith(0);
            userData.setWindowHeight(0);
            userData.setLocale(new Locale(System.getProperty("user.language"), System.getProperty("user.country")));
            userData.setLogLevel(Level.ALL);
        }

    }

    /**
     * This method return UserData.
     *
     * @return
     */
    public UserData getUserData() {
        return userData;
    }

    /**
     * This method sets log4j level for easyconduite logger.
     *
     * @param level
     */
    public void setLog4jLevel(Level level) {

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
