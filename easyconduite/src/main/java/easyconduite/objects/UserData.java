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

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Queue;
import org.apache.logging.log4j.Level;

/**
 * This class implements properties for user datas.
 *
 * @author antony
 */
public class UserData implements Serializable {

    private static final long serialVersionUID = 42L;

    private Locale locale;

    private int WindowWith;

    private int windowHeight;

    private Queue<Path> recentsPaths;

    private Level logLevel;

    /**
     * Constructor with parameters.
     *
     * @param WindowWith
     * @param windowHeight
     * @param logLevel
     */
    public UserData(int WindowWith, int windowHeight, Level logLevel) {
        this.WindowWith = WindowWith;
        this.windowHeight = windowHeight;
        this.logLevel = logLevel;
    }

    /**
     * Default constructor.
     */
    public UserData() {
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public int getWindowWith() {
        return WindowWith;
    }

    public void setWindowWith(int WindowWith) {
        this.WindowWith = WindowWith;
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

    @Override
    public String toString() {
        return "UserData{" + "locale=" + locale + ", WindowWith=" + WindowWith + ", windowHeight=" + windowHeight + ", recentsPaths=" + recentsPaths + ", logLevel=" + logLevel + '}';
    }

}
