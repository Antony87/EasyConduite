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

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manage an AudioMedia list and the entire drama Playing duration.
 *
 * @author A Fons
 */
public class AudioTable implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name="";

    private Duration duration;

    private List<AudioMedia> audioMediaList;

    /**
     * Constructor.
     * <br> Initialize the list of AudioFile.
     */
    public AudioTable() {
        audioMediaList = new ArrayList<>();
    }

    /**
     * This method add an {@link AudioMedia} into the List if not already
     * present.
     *
     * @param audioMedia Media audio to be put into de List.
     */
    public void addIfNotPresent(AudioMedia audioMedia) {

        if (!audioMediaList.contains(audioMedia)) {
            audioMediaList.add(audioMedia);
        }
    }

    /**
     * This method remove an {@link AudioMedia} from the list if present.
     *
     * @param audioMedia Media audio to be remove from the List.
     */
    public void removeIfPresent(AudioMedia audioMedia) {

        if (audioMediaList.contains(audioMedia)) {
            audioMediaList.remove(audioMedia);
        }

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
    public List<AudioMedia> getAudioMediaList() {
        return audioMediaList;
    }

    /**
     * Set the AudioMedia List.
     *
     * @param audioMediaList
     */
    public void setAudioMediaList(List<AudioMedia> audioMediaList) {
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
