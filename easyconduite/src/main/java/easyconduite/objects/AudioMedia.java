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

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.File;
import java.util.Objects;
import java.util.UUID;

/**
 * Class POJO that encapsulate AudioMedia behavior.<br>
 * This class instantiats an marshallable/unmarsallable object for JAXB.
 *
 * @author A.Fons
 * @version 1.2
 * @deprecated
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@Deprecated
public class AudioMedia {

    private UUID uniqueId = UUID.randomUUID();

    private int index;

    private String filePathName;

    private Duration duration = Duration.ONE;

    private ObservableList<UUID> uuidChildEnd = FXCollections.observableArrayList();

    private ObservableList<UUID> uuidChildBegin = FXCollections.observableArrayList();

    private final ObjectProperty<Duration> fadeInDuration = new ReadOnlyObjectWrapper<>(Duration.ZERO);

    private final ObjectProperty<Duration> fadeOutDuration = new ReadOnlyObjectWrapper<>(Duration.ZERO);

    private final BooleanProperty repeatable = new ReadOnlyBooleanWrapper();

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
        /* set default values; */
        this.filePathName = audioFile.getAbsolutePath();
        setRepeatable(Boolean.FALSE);
        setVolume(0.5d);
        setName(audioFile.getName());
    }

    /**
     * Get Unique id.
     *
     * @return the unique Id for this audio Media
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Return path of the audio file.
     *
     * @return the string path of audio media (by getAbsolutePath).
     */
    public String getFilePathName() {
        return filePathName;
    }

    /**
     * Set audio file's path.
     *
     * @param filePathName Absolute path string of audio media
     */
    public final void setFilePathName(String filePathName) {
        this.filePathName = filePathName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
     *                      repeatability playing.
     */
    final void setRepeatable(Boolean repeatability) {
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
     * If volume greater than 100 then équal 100, if volume less than 0 then 0.
     *
     * @param volume
     */
    public final void setVolume(Double volume) {
        this.volume.setValue(volume);
    }

    /**
     * Observable property for audio volume.
     *
     * @return
     */
    public DoubleProperty volumeProperty() {
        return volume;
    }

    /**
     * Get the total duration of audio media.
     *
     * @return total duration (Duration ms) of the audio media
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Set the total duration of audio media.
     *
     * @param duration
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Get the fade in duration uses during playing.
     *
     * @return
     */
    public Duration getFadeInDuration() {
        return fadeInDuration.getValue();
    }

    /**
     * Set the fade in duration uses during playing.
     *
     * @param fadeInDuration
     */
    public void setFadeInDuration(Duration fadeInDuration) {
        this.fadeInDuration.setValue(fadeInDuration);
    }

    /**
     * Observable fadeInDuration porperty.
     *
     * @return
     */
    public ObjectProperty<Duration> fadeInDurationProperty() {
        return fadeInDuration;
    }

    /**
     * Get the fade out duration uses during playing.
     *
     * @return
     */
    public Duration getFadeOutDuration() {
        return fadeOutDuration.getValue();
    }

    /**
     * Set the fade out duration uses during playing.
     *
     * @param fadeOutDuration
     */
    public void setFadeOutDuration(Duration fadeOutDuration) {
        this.fadeOutDuration.setValue(fadeOutDuration);
    }

    /**
     * Observable fadeOutDuration porperty.
     *
     * @return
     */
    public ObjectProperty<Duration> fadeOutDurationProperty() {
        return fadeOutDuration;
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Return the name of Audio Media object.
     *
     * @return
     */
    public String getName() {
        return name.get();
    }

    /**
     * Set Audio Media object name's.
     *
     * @param name
     */
    public final void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Set keyboard KeyCode matched to audio media player.
     *
     * @param keycode
     */
    public final void setKeycode(KeyCode keycode) {
        this.keycode.set(keycode);
    }

    /**
     * Get keyboard KeyCode matched to audio media player.
     *
     * @return
     */
    public KeyCode getKeycode() {
        return keycode.get();
    }

    public ObjectProperty<KeyCode> keycodeProperty() {
        return keycode;
    }

    public ObservableList<UUID> getUuidChildEnd() {
        return uuidChildEnd;
    }

    public void setUuidChildEnd(ObservableList<UUID> uuidChildEnd) {
        this.uuidChildEnd = uuidChildEnd;
    }

    public ObservableList<UUID> getUuidChildBegin() {
        return uuidChildBegin;
    }

    public void setUuidChildBegin(ObservableList<UUID> uuidChildBegin) {
        this.uuidChildBegin = uuidChildBegin;
    }


    public void addToChildEnd(AudioMedia audioMedia) throws IllegalArgumentException {
        if (!audioMedia.getUniqueId().equals(this.uniqueId)) {
            try {
                getUuidChildEnd().add(audioMedia.getUniqueId());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public void clearChildEnd() {
        getUuidChildEnd().clear();
    }

    public void clearChildBegin() {
        getUuidChildBegin().clear();
    }

    public void addToChildBegin(AudioMedia audioMedia) throws IllegalArgumentException {
        if (!audioMedia.getUniqueId().equals(this.uniqueId)) {
            try {
                getUuidChildBegin().add(audioMedia.getUniqueId());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.uniqueId);
        return hash;
    }

    /**
     * Equals for AudioMedia object, based on UniqueId.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AudioMedia other = (AudioMedia) obj;
        return Objects.equals(this.uniqueId, other.uniqueId);
    }

    @Override
    public String toString() {
        return "AudioMedia{" + "uniqueId=" + uniqueId + ", filePathName=" + filePathName + ", audioDuration=" + duration + ", fadeInDuration=" + fadeInDuration + ", fadeOutDuration=" + fadeOutDuration + ", repeatable=" + repeatable + ", volume=" + volume + ", name=" + name + ", keycode=" + keycode + '}';
    }
}
