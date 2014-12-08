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
import java.util.Objects;
import java.util.UUID;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.KeyCode;

/**
 * Class that encapsulate audio media, behavior.
 *
 * @author A.Fons
 *
 */
public class AudioMedia {

    private UUID uniqueId;
    /**
     * Name of track, to be displayed on top of the pane.
     *
     */
    private String name;

    private KeyCode linkedKeyCode;

    private File audioFile;

    private final DoubleProperty volume = new SimpleDoubleProperty();

    /**
     * Default constructor, to be conform bean specification.
     */
    public AudioMedia() {

    }

    public AudioMedia(final File audioFile) {
        this.audioFile = audioFile;
        uniqueId = UUID.randomUUID();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KeyCode getLinkedKeyCode() {
        return linkedKeyCode;
    }

    public void setLinkedKeyCode(final KeyCode linkedKeyCode) {
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.uniqueId);
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Objects.hashCode(this.linkedKeyCode);
        hash = 41 * hash + Objects.hashCode(this.audioFile);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AudioMedia other = (AudioMedia) obj;
        if (!Objects.equals(this.uniqueId, other.uniqueId)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.linkedKeyCode != other.linkedKeyCode) {
            return false;
        }
        if (!Objects.equals(this.audioFile, other.audioFile)) {
            return false;
        }
        return true;
    }
    
}
