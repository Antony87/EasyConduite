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

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

/**
 * This class manage an {@link ObservableList} of AudioMedia.
 *
 * @author A Fons
 * @deprecated 
 */
public class AudioTable {

    private final List<AudioMedia> audioMediaList;

    /**
     * Constructor.
     * <br> Initialize the ObservableList of AudioFile.
     */
    public AudioTable() {
        audioMediaList = new ArrayList<>();
    }

    /**
     * This method add an AudioFile to List.
     *
     * @param audioMedia
     * @param audioFile
     * @throws IllegalArgumentException
     */
    public void addAudioMedia(AudioMedia audioMedia) throws IllegalArgumentException {

        if (!audioMediaList.contains(audioMedia)) {
            audioMediaList.add(audioMedia);
        } else {
            throw new IllegalArgumentException("AudioFile already added");
        }
    }

    public void removeAudioMedia(AudioMedia audioMedia) {
        if (audioMediaList.contains(audioMedia)) {
            audioMediaList.remove(audioMedia);
        }
    }

    /**
     * Rhis method expose the List, to allow binding from other class.
     *
     * @return
     */
    public List<AudioMedia> getAudioMediaObsList() {
        return audioMediaList;
    }

}
