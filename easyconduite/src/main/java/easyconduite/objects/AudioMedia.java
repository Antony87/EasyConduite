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
import javafx.util.Duration;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Class POJO that encapsulate AudioMedia behavior.<br>
 * This class instantiats an marshallable/unmarsallable object for JAXB.
 *
 * @author A.Fons
 * @version 1.1
 *
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AudioMedia implements Comparable<AudioMedia> {

    private final UUID uniqueId = UUID.randomUUID();

    private File audioFile;

    private String filePathName;
    
    private Duration audioDuration=Duration.ONE;
    
    private Duration fadeInDuration;
    
    private Duration fadeOutDuration;

    public final BooleanProperty repeatable = new ReadOnlyBooleanWrapper();

    private final DoubleProperty volume = new ReadOnlyDoubleWrapper();

    private final StringProperty name = new ReadOnlyStringWrapper();

    private final ObjectProperty<KeyCode> keycode = new ReadOnlyObjectWrapper<>();

    /**
     * Default constructor, to be conform bean specification.
     */
    private AudioMedia() {
    }

    /**
     * Constructs an AudioMedia object with audio audioFile.
     *
     * @param audioFile audio audioFile (*.wav, *.mp3) Depends on platform type.
     */
    public AudioMedia(File audioFile) {
        // set default values;
        this.audioFile = audioFile;
        this.filePathName = audioFile.getAbsolutePath();
        setRepeatable(Boolean.FALSE);
        setVolume(50.0);
        Path path = audioFile.toPath();
        setName(path.getFileName().toString());
    }

    /**
     * Get Unique id.
     *
     * @return
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * Set the audio file (*.wav or *.mp3).<br>
     * It's XmlTransient cause not able to be xml serialized.
     *
     * @param audioFile
     */
    @XmlTransient
    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    /**
     * Return the audio file.
     *
     * @return
     */
    public File getAudioFile() {
        return audioFile;
    }

    /**
     * Return path of the audio file.
     *
     * @return
     */
    public String getFilePathName() {
        return filePathName;
    }

    /**
     * Set audio file's path.
     *
     * @param filePathName
     */
    public final void setFilePathName(String filePathName) {
        this.filePathName = filePathName;
    }

    ////////////////////////////////////////////////////////////////////////////
    //                        JavaFX Properties
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Return Audio media's repeatability.
     *
     * @return
     */
    public Boolean getRepeatable() {
        return repeatable.getValue();
    }

    /**
     * Set Audio media repeatability's.
     *
     * @param repeatability true or false if the audio file must have
     * repeatability playing.
     */
    public final void setRepeatable(Boolean repeatability) {
        this.repeatable.setValue(repeatability);
    }

    /**
     * Observable property for repeatability.
     *
     * @return
     */
    public BooleanProperty repeatableProperty() {
        return repeatable;
    }
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Return playing volume's of audio (from 0 to 100).<br> 
     * Default is 50.
     *
     * @return
     */
    public Double getVolume() {
        return volume.getValue();
    }

    /**
     * Set playing's volume of audio.<br>
     * If volume > 100 then équal 100, if volume < 0 then 0.
     * @param volume 
     */
    public final void setVolume(Double volume) {
        if(volume<0d){
            volume=0d;
        }
        if(volume>100d){
            volume=100d;
        }
        this.volume.setValue(volume);
    }

    public Duration getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(Duration audioDuration) {
        this.audioDuration = audioDuration;
    }

    public Duration getFadeInDuration() {
        return fadeInDuration;
    }

    public void setFadeInDuration(Duration fadeInDuration) {
        this.fadeInDuration = fadeInDuration;
    }

    public Duration getFadeOutDuration() {
        return fadeOutDuration;
    }

    public void setFadeOutDuration(Duration fadeOutDuration) {
        this.fadeOutDuration = fadeOutDuration;
    }
    
    

    /**
     * Observable property for audio volume.
     * @return 
     */
    public DoubleProperty volumeProperty() {
        return volume;
    }
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Return the name of Audio Media object.
     * @return 
     */
    public String getName() {
        return name.get();
    }

    /**
     * Set Audio Media object name's.
     * @param name 
     */
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
        return "AudioMedia{" + "uniqueId=" + uniqueId + ", filePathName=" + filePathName + ", name=" + name.getValue() + ", keycode=" + keycode.getValue() + '}';
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
