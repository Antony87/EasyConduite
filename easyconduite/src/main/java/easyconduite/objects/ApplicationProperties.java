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
package easyconduite.objects;

import javafx.stage.Window;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;

/**
 * This class implements properties for user datas.
 *
 * @author antony
 * @deprecated
 */
public class ApplicationProperties implements Serializable {

    private static final long serialVersionUID = 42L;

    private Locale locale;

    private int windowWith;

    private int windowHeight;

    private File lastFileProject;

    private Level logLevel;
    
    private File lastProjectDir;
    
    private File lastImportDir;
    
    private transient Window currentWindow;

    /**
     * Constructor with parameters.
     *
     * @param windowWith
     * @param windowHeight
     * @param logLevel
     */
    public ApplicationProperties(int windowWith, int windowHeight, Level logLevel) {
        this.windowWith = windowWith;
        this.windowHeight = windowHeight;
        this.logLevel = logLevel;

    }

    /**
     * Default constructor.
     */
    public ApplicationProperties() {

    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public int getWindowWith() {
        return windowWith;
    }

    public void setWindowWith(int WindowWith) {
        this.windowWith = WindowWith;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public File getLastProjectDir() {
        return lastProjectDir;
    }

    public void setLastProjectDir(File lastProjectDir) {
        this.lastProjectDir = lastProjectDir;
    }

    public File getLastImportDir() {
        return lastImportDir;
    }

    public void setLastImportDir(File lastImportDir) {
        this.lastImportDir = lastImportDir;
    }

    public File getLastFileProject() {
        return lastFileProject;
    }

    public void setLastFileProject(File lastFileProject) {
        this.lastFileProject = lastFileProject;
    }

    public Window getCurrentWindow() {
        return currentWindow;
    }

    public void setCurrentWindow(Window currentWindow) {
        this.currentWindow = currentWindow;
    }


    @Override
    public String toString() {
        return "ApplicationProperties{" + "locale=" + locale + ", WindowWith=" + windowWith + ", windowHeight=" + windowHeight + ", lastFileProject=" + lastFileProject + ", logLevel=" + logLevel + ", lastProjectDir=" + lastProjectDir + ", lastImportDir=" + lastImportDir + '}';
    }


}
