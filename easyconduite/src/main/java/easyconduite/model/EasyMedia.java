package easyconduite.model;

import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.UUID;


/**
 * Classe abstraite offrant les caractéristiques communes d'un Easymédia jouable par EasyConduite.
 */
public abstract class EasyMedia {

    private String name;

    private Duration duration;

    private UUID uniqueId = UUID.randomUUID();

    private int indexInTable;

    private KeyCode keycode;

    private Boolean loppable;

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

    public UUID getUniqueId() {
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
    public Boolean getLoppable() {
        return loppable;
    }

    public void setLoppable(Boolean loppable) {
        this.loppable = loppable;
    }
}
