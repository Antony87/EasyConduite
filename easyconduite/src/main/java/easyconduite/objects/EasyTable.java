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

import easyconduite.model.ChainingUpdater;
import easyconduite.model.IeasyMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.media.SoundMedia;
import easyconduite.tools.PersistenceHelper;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.UUID;

/**
 * This class manage an Media list and the entire drama Playing duration.
 *
 * @author A Fons
 */
@XmlRootElement(name = "audiotable")
@XmlAccessorType(XmlAccessType.FIELD)
public class EasyTable {

    private String name = "";

    private Duration duration;

    private String tablePathFile;

    public ObservableList<AudioMedia> audioMediaList;

    @XmlTransient
    private final BooleanProperty updated = new SimpleBooleanProperty(false);

    @XmlTransient
    static final Logger LOG = LogManager.getLogger(EasyTable.class);

    @XmlTransient
    private ChainingUpdater next;

    /**
     * Constructor.
     * <br> Initialize the list of AudioMedia.
     * <br> AudioMedia List is observable and have a callback for Keycode.
     */
    public EasyTable() {

        Callback<AudioMedia, Observable[]> cb = (AudioMedia a) -> new Observable[]{
                a.keycodeProperty(),
                a.nameProperty(),
                a.repeatableProperty(),
                a.volumeProperty(),
                a.fadeOutDurationProperty(),
                a.fadeInDurationProperty(),
                a.getUuidChildBegin(),
                a.getUuidChildEnd()

        };

        IeasyMedia soundMedia = MediaFactory.getEasyMedia();


        audioMediaList = FXCollections.observableArrayList(cb);
        audioMediaList.addListener(new AudioMediaChangeListerner());

    }

    /**
     * Return te name of this EasyTable.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this EasyTable.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the AudioMedia List.
     *
     * @return
     */
    public ObservableList<AudioMedia> getAudioMediaList() {
        return audioMediaList;
    }

    /**
     * Set the AudioMedia List.
     *
     * @param audioMediaList
     */
    public void setAudioMediaList(ObservableList<AudioMedia> audioMediaList) {
        this.audioMediaList = audioMediaList;
    }

    /**
     * Return drama duration.
     *
     * @return
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Set drama duration.
     *
     * @param duration
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Return th absolute path of the drama File.
     *
     * @return
     */
    public String getTablePathFile() {
        return tablePathFile;
    }

    public void setTablePathFile(String tablePathFile) {
        this.tablePathFile = tablePathFile;
    }

    public boolean isUpdated() {
        return updated.get();
    }

    public void setUpdated(boolean value) {
        updated.set(value);
    }

    public BooleanProperty updatedProperty() {
        return updated;
    }

    /**
     * @param uuid
     * @return
     */
    public AudioMedia findByUUID(UUID uuid) {
        if (uuid != null) {
            for (AudioMedia audioMedia : this.getAudioMediaList()) {
                if (uuid.equals(audioMedia.getUniqueId())) {
                    return audioMedia;
                }
            }
        }
        return null;
    }

    /**
     * This method tests when application may be closed without project's
     * saving.
     *
     * @return
     */
    public boolean isClosable() {
        if (this.getAudioMediaList().isEmpty()) {
            LOG.debug("list vide");
            return true;
        }
        if (this.isUpdated()) {
            LOG.debug("table update");
            return false;
        }
        if (Strings.isEmpty(this.getTablePathFile())) {
            LOG.debug("pathfile vide");
            return false;
        }
        if (!PersistenceHelper.isFileExists(this.getTablePathFile())) {
            LOG.debug("Fichier inexistant");
            return false;
        }
        return true;
    }

    private class AudioMediaChangeListerner implements ListChangeListener<AudioMedia> {

        @Override
        public void onChanged(Change<? extends AudioMedia> c) {
            while (c.next()) {
                LOG.debug("Change occured on Audiomedialist");
                setUpdated(true);
            }
        }
    }

}
