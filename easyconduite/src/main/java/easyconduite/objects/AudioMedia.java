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
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;

import javafx.scene.input.KeyCode;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Class POJO that encapsulate AudioMedia behavior.
 *
 * @author A.Fons
 *
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AudioMedia implements Comparable<AudioMedia> {

    private final UUID uniqueId = UUID.randomUUID();

    private File audioFile;

    private String filePathName;

    public final BooleanProperty repeatable = new ReadOnlyBooleanWrapper();

    private final DoubleProperty volume = new ReadOnlyDoubleWrapper();

    private final StringProperty name = new ReadOnlyStringWrapper();

    private final ObjectProperty<KeyCode> keycode = new ReadOnlyObjectWrapper<>();

    /**
     * Default constructor, to be conform bean specification.
     */
    private AudioMedia() {
        
    }

    public AudioMedia(File file) {
        // set default values;
        this.audioFile = file;
        this.filePathName = file.getAbsolutePath();
        setRepeatable(Boolean.FALSE);
        setVolume(0.5);
        Path path = file.toPath();
        setName(path.getFileName().toString());
    }

    ////////////////////////////////////////////////////////////////////////////
    //                        Acesseurs
    ////////////////////////////////////////////////////////////////////////////
    public UUID getUniqueId() {
        return uniqueId;
    }

    ////////////////////////////////////////////////////////////////////////////
    @XmlTransient
    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public String getFilePathName() {
        return filePathName;
    }

    public final void setFilePathName(String filePathName) {
        this.filePathName = filePathName;
    }

    ////////////////////////////////////////////////////////////////////////////
    //                        JavaFX Properties
    ////////////////////////////////////////////////////////////////////////////
    public Boolean getRepeatable() {
        return repeatable.getValue();
    }

    public final void setRepeatable(Boolean b) {
        this.repeatable.setValue(b);
    }

    public BooleanProperty repeatableProperty() {
        return repeatable;
    }
    ////////////////////////////////////////////////////////////////////////////

    public Double getVolume() {
        return volume.getValue();
    }

    public final void setVolume(Double volume) {
        this.volume.setValue(volume);
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }
    ////////////////////////////////////////////////////////////////////////////

    public String getName() {
        return name.get();
    }

    public final void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }
    ////////////////////////////////////////////////////////////////////////////

    public final void setKeycode(KeyCode keycode) {
        this.keycode.set(keycode);
    }

    public KeyCode getKeycode() {
        return keycode.get();
    }

    public ObjectProperty<KeyCode> keycodeProperty() {
        return keycode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.uniqueId);
        hash = 79 * hash + Objects.hashCode(this.repeatable.get());
        hash = 79 * hash + Objects.hashCode(this.name.getValue());
        hash = 79 * hash + Objects.hashCode(this.keycode.getValue());
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
        if (!Objects.equals(this.repeatable, other.repeatable)) {
            return false;
        }
        if (!Objects.equals(this.volume, other.volume)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.keycode, other.keycode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AudioMedia{" + "uniqueId=" + uniqueId + ", filePathName=" + filePathName + ", name=" + name.getValue() + ", keycode=" + keycode.getValue().getName() + '}';
    }

    @Override
    public int compareTo(AudioMedia a) {
        int diff = this.getUniqueId().compareTo(a.getUniqueId());
        if (diff == 0) {
            diff = this.getKeycode().compareTo(a.getKeycode());
        }
        if (diff == 0) {
            diff = this.getName().compareTo(a.getName());
        }
        return diff;
    }

}
