/*
 * Copyright (C) 2014 antony fons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package easyconduite.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * This class encapsulates behaviors for severals configurations.<br>As
 * properties, logger...
 *
 * @author antony fons
 */
public class Config {

    public static final Background PLAY_BACKG = new Background(new BackgroundFill(Color.web("#455473"), CornerRadii.EMPTY, Insets.EMPTY));

    public static final Background STOP_BACKG = new Background(new BackgroundFill(Color.web("#535965"), CornerRadii.EMPTY, Insets.EMPTY));

    /**
     * This method return a custom logger who write to a easyconduite.log file.
     *
     * @param className name of the class who call the logger.
     * @return
     */
    public static Logger getCustomLogger(String className) {

        Logger logger = Logger.getLogger(className);
        logger.setLevel(Level.ALL);
        logger.addHandler(SingleFileHandler.getFileHandlerInstance());

        return logger;
    }

    /**
     * This inner class implements a FileHandler with the Singleton pattern.
     */
    private static class SingleFileHandler {

        private static SingleFileHandler fileHandler = null;
        private static FileHandler fileTxt;

        /**
         * Method of the inner class that creates a Singleton pattern
         * FileHandler.
         *
         * @return
         */
        public static FileHandler getFileHandlerInstance() {

            if (fileHandler == null) {
                fileHandler = new SingleFileHandler();
            }
            return fileTxt;
        }

        private SingleFileHandler() {
            try {
                fileTxt = new FileHandler("easyconduite.log", true);
                fileTxt.setFormatter(new SimpleFormatter());
                fileTxt.setEncoding("UTF8");
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
