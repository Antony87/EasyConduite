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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

/**
 * This class manage an {@link ObservableList} of AudioMedia.
 *
 * @author A Fons
 */
@XStreamAlias("audiotable")
public class AudioTable {

    @XStreamAlias("name")
    private String name;
    
    @XStreamAlias("duration")
    private Duration duration;

    @XStreamImplicit(itemFieldName = "audiomedia")
    private List<AudioMedia> audioMediaList;

    /**
     * Constructor.
     * <br> Initialize the ObservableList of AudioFile.
     */
    public AudioTable() {
        audioMediaList = new ArrayList<>();
    }

    public void addIfNotPresent(AudioMedia audioMedia) {

        if (!audioMediaList.contains(audioMedia)) {
            audioMediaList.add(audioMedia);
        }
    }

    public void removeIfPresent(AudioMedia audioMedia) {

        if (audioMediaList.contains(audioMedia)) {
            audioMediaList.remove(audioMedia);
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AudioMedia> getAudioMediaList() {
        return audioMediaList;
    }

    public void setAudioMediaList(List<AudioMedia> audioMediaList) {
        this.audioMediaList = audioMediaList;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    
    

}
