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

import easyconduite.util.PersistenceUtil;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

/**
 * This class manage an AudioMedia list and the entire drama Playing duration.
 *
 * @author A Fons
 */
@XmlRootElement(name = "audiotable")
@XmlAccessorType(XmlAccessType.FIELD)
public class AudioTable {

    private String name = "";

    private Duration duration;

    private String tablePathFile;

    private ObservableList<AudioMedia> audioMediaList;

    @XmlTransient
    public ListProperty<AudioMedia> listproperty;

    @XmlTransient
    private boolean updated = false;

    @XmlTransient
    static final Logger LOG = LogManager.getLogger(AudioTable.class);

    /**
     * Constructor.
     * <br> Initialize the list of AudioMedia.
     * <br> AudioMedia List is observable and have a callback for Keycode.
     */
    public AudioTable() {

        Callback<AudioMedia, Observable[]> cb = (AudioMedia a) -> new Observable[]{
            a.keycodeProperty(),
            a.nameProperty(),
            a.repeatableProperty(),
            a.volumeProperty(),
            a.fadeOutDurationProperty(),
            a.fadeInDurationProperty()
        };

//        audioMediaList = FXCollections.observableArrayList();
        audioMediaList = FXCollections.observableArrayList(cb);
        audioMediaList.addListener(new AudioMediaChangeListerner());
        
        listproperty = new SimpleListProperty<>(audioMediaList);


    }

    /**
     * Return te name of this AudioTable.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this AudioTable.
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
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
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
        if (!PersistenceUtil.isFileExists(this.getTablePathFile())) {
            LOG.debug("ifchier inexistant");
            return false;
        }
        return true;
    }

    private class AudioMediaChangeListerner implements ListChangeListener<AudioMedia> {

        @Override
        public void onChanged(Change<? extends AudioMedia> c) {
            while (c.next()) {
                if (c.wasRemoved()) {
                    setUpdated(true);
                    LOG.trace("{} was removed from AudioTable",c);
                }
                if (c.wasUpdated()) {
                    LOG.trace("{} was updated from AudioTable",c);
                    setUpdated(true);
                }
                if(c.wasAdded()){
                   LOG.trace("{} was added from AudioTable",c); 
                }
            }
        }
    }

}
