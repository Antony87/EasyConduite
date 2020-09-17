package easyconduite.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import easyconduite.media.AudioMedia;
import easyconduite.media.RemoteMedia;
import easyconduite.tools.jackson.DurationDeserializer;
import easyconduite.tools.jackson.DurationSerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Objects;
import java.util.UUID;


/**
 * Classe abstraite offrant les caractéristiques communes d'un Easymédia jouable par EasyConduite.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AudioMedia.class, name = "audiomedia"),
        @JsonSubTypes.Type(value = RemoteMedia.class, name = "remotemedia"),
})
public abstract class AbstractMedia implements MediaPlayable {

//    @JsonDeserialize(using = NamePropertyDeserialize.class)
//    @JsonSerialize(using = StringPropertySerializer.class)
    private StringProperty name = new SimpleStringProperty();

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    private final Long uniqueId = UUID.randomUUID().getMostSignificantBits();

    private int indexInTable;

    private KeyCode keycode;

    private boolean loppable = false;

    @Override
    @JsonProperty("mediaName")
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    @JsonSetter("mediaName")
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * @return la durée {@link Duration} de lecture du EasyMédia
     */
    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Long getUniqueId() {
        return uniqueId;
    }

    /**
     * @return index de postionnement dans la table sur l'UI.
     */
    public int getIndexInTable() {
        return indexInTable;
    }

    public void setIndexInTable(int indexInTable) {
        this.indexInTable = indexInTable;
    }

    /**
     * @return le code {@link KeyCode} associé au EasyMédia pour pause,play,stop.
     */
    @Override
    public KeyCode getKeycode() {
        return keycode;
    }

    public void setKeycode(KeyCode keycode) {
        this.keycode = keycode;
    }

    /**
     * @return Boolean si le EasyMédia est jouable en boucle.
     */
    @Override
    public boolean getLoppable() {
        return loppable;
    }

    public void setLoppable(boolean loppable) {
        this.loppable = loppable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractMedia abstractMedia = (AbstractMedia) o;
        return indexInTable == abstractMedia.indexInTable &&
                Objects.equals(name, abstractMedia.name) &&
                Objects.equals(duration, abstractMedia.duration) &&
                uniqueId.equals(abstractMedia.uniqueId) &&
                keycode == abstractMedia.keycode &&
                Objects.equals(loppable, abstractMedia.loppable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), duration, uniqueId, indexInTable, keycode, loppable);
    }

    @Override
    public String toString() {
        return "AbstractMedia{" +
                "name='" + getName() + '\'' +
                ", duration=" + duration +
                ", uniqueId=" + uniqueId +
                ", indexInTable=" + indexInTable +
                ", keycode=" + keycode +
                ", loppable=" + loppable +
                '}';
    }
}
