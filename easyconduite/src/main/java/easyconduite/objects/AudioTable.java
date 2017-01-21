/*
 EasyConduite Copyright 2014 Antony Fons

 This file is part of EasyConduite.

 EasyConduite is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 2 of the License, or
 any later version.

 EasyConduite is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with EasyConduite.  If not, see <http://www.gnu.org/licenses/>.
 */
package easyconduite.objects;

import java.time.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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

    //private List<AudioMedia> audioMediaList;
    private ObservableList<AudioMedia> audioMediaList;

    /**
     * Constructor.
     * <br> Initialize the list of AudioMedia.
     * <br> AudioMedia List is observable and have a callback for Keycode.
     */
    public AudioTable() {
        audioMediaList = FXCollections.observableArrayList();
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

}
