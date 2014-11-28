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

import java.io.File;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Class that encapsulate audio media, behavior.
 *
 * @author A.Fons
 *
 */
public class AudioMedia {

    /**
     * id
     */
    private Integer id;

    /**
     * Name of track, to be displayed on top of the pane.
     *
     */
    private String name;

    private int linkedKeyCode;

    private File audioFile;

    private final DoubleProperty volume = new SimpleDoubleProperty();

    /**
     * Default constructor, to be conform bean specification.
     */
    public AudioMedia() {

    }

    public AudioMedia(final File audioFile) {
        this.audioFile = audioFile;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLinkedKeyCode() {
        return linkedKeyCode;
    }

    public void setLinkedKeyCode(final int linkedKeyCode) {
        this.linkedKeyCode = linkedKeyCode;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public final Double getVolume() {
        return volume.get();
    }

    public final void setVolume(final Double volume) {
        this.volume.set(volume);
    }

}
