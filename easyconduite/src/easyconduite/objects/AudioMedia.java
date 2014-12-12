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
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.io.File;
import java.util.Objects;
import java.util.UUID;

import javafx.scene.input.KeyCode;

/**
 * Class POJO that encapsulate AudioMedia behavior.
 *
 * @author A.Fons
 *
 */
@XStreamAlias("audiomedia")
public class AudioMedia {

    @XStreamAlias("id")
    private UUID uniqueId;
    /**
     * Name of track, to be displayed on top of the pane.
     *
     */
    @XStreamAlias("name")
    private String name;

    @XStreamAlias("keycode")
    private KeyCode linkedKeyCode;

    @XStreamOmitField
    private File audioFile;

    @XStreamAlias("path")
    private String filePathName;

    @XStreamAlias("volume")
    private Double volume;

    /**
     * Default constructor, to be conform bean specification.
     */
    private AudioMedia() {
    }

    public AudioMedia(final File audioFile) {
        
        // set default values;
        this.audioFile = audioFile;
        this.filePathName = audioFile.getAbsolutePath();
        uniqueId = UUID.randomUUID();
        linkedKeyCode = KeyCode.UNDEFINED;
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
        return volume;
    }

    public final void setVolume(final Double volume) {
        this.volume = volume;
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
