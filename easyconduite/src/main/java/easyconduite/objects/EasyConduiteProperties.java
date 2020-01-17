/*
 *
 *
 *  * Copyright (c) 2020.  Antony Fons
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package easyconduite.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import easyconduite.tools.jackson.ApacheLogDeserializer;
import easyconduite.tools.jackson.ApacheLogSerializer;
import javafx.beans.property.SimpleLongProperty;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * This class implements properties for user datas.
 *
 * @author antony
 */
public class EasyConduiteProperties {

    private Locale locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));

    private int windowWith = 800;

    private int windowHeight = 600;

    private File lastFileProject;

    @JsonSerialize(using = ApacheLogSerializer.class)
    @JsonDeserialize(using = ApacheLogDeserializer.class)
    private Level logLevel = Level.OFF;

    private Path lastProjectDir;

    private Path lastImportDir;

    @JsonIgnore
    private final SimpleLongProperty changeCompteur = new SimpleLongProperty();

    public EasyConduiteProperties() {
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        updateChangeCount();
    }

    public int getWindowWith() {
        return windowWith;
    }

    public void setWindowWith(int WindowWith) {
        this.windowWith = WindowWith;
        updateChangeCount();
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
        updateChangeCount();
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
        updateChangeCount();
    }

    public Path getLastProjectDir() {
        return lastProjectDir;
    }

    public void setLastProjectDir(Path lastProjectDir) {
        this.lastProjectDir = lastProjectDir;
        updateChangeCount();
    }

    public Path getLastImportDir() {
        return lastImportDir;
    }

    public void setLastImportDir(Path lastImportDir) {
        this.lastImportDir = lastImportDir;
        updateChangeCount();
    }

    public File getLastFileProject() {
        return lastFileProject;
    }

    public void setLastFileProject(File lastFileProject) {
        this.lastFileProject = lastFileProject;
        updateChangeCount();
    }

    public long getChangeCompteur() {
        return changeCompteur.get();
    }

    public SimpleLongProperty changeCompteurProperty() {
        return changeCompteur;
    }

    public void setChangeCompteur(long changeCompteur) {
        this.changeCompteur.set(changeCompteur);
    }

    private void updateChangeCount() {
        changeCompteur.setValue(changeCompteur.get() + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EasyConduiteProperties that = (EasyConduiteProperties) o;
        return windowWith == that.windowWith &&
                windowHeight == that.windowHeight &&
                Objects.equals(locale, that.locale) &&
                Objects.equals(lastFileProject, that.lastFileProject) &&
                Objects.equals(logLevel, that.logLevel) &&
                Objects.equals(lastProjectDir, that.lastProjectDir) &&
                Objects.equals(lastImportDir, that.lastImportDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locale, windowWith, windowHeight, lastFileProject, logLevel, lastProjectDir, lastImportDir);
    }

    @Override
    public String toString() {
        return "EasyConduiteProperties{" +
                "locale=" + locale +
                ", windowWith=" + windowWith +
                ", windowHeight=" + windowHeight +
                ", lastFileProject=" + lastFileProject +
                ", logLevel=" + logLevel +
                ", lastProjectDir=" + lastProjectDir +
                ", lastImportDir=" + lastImportDir +
                '}';
    }
}
