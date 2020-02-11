package easyconduite.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.tools.jackson.DurationDeserializer;
import easyconduite.tools.jackson.DurationSerializer;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Objects;
import java.util.UUID;


/**
 * Classe abstraite offrant les caractéristiques communes d'un Easymédia jouable par EasyConduite.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AudioVideoMedia.class, name = "audiovideomedia"),
})
public abstract class EasyMedia implements IEasyMedia {

    private String name;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    private Long uniqueId = UUID.randomUUID().getMostSignificantBits();

    private int indexInTable;

    private KeyCode keycode;

    private boolean loppable = false;

    /**
     * @return Nom du EasyMédia.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public KeyCode getKeycode() {
        return keycode;
    }

    public void setKeycode(KeyCode keycode) {
        this.keycode = keycode;
    }

    /**
     * @return Boolean si le EasyMédia est jouable en boucle.
     */
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
        EasyMedia easyMedia = (EasyMedia) o;
        return indexInTable == easyMedia.indexInTable &&
                Objects.equals(name, easyMedia.name) &&
                Objects.equals(duration, easyMedia.duration) &&
                uniqueId.equals(easyMedia.uniqueId) &&
                keycode == easyMedia.keycode &&
                Objects.equals(loppable, easyMedia.loppable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, duration, uniqueId, indexInTable, keycode, loppable);
    }
}
