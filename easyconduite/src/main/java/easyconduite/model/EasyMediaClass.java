package easyconduite.model;

import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.UUID;


/**
 * Classe abstraite offrant les caractéristiques communes d'un média.
 */
public abstract class EasyMediaClass implements IeasyMedia {

    private String name;

    private Duration duration;

    private UUID uniqueId = UUID.randomUUID();

    private int index;

    private String filePathName;

    private KeyCode keycode;

    private Boolean repetable;

    /**
     * @return le nom du média
     */
    public String getName() {
        return name;
    }

    /**
     * Positionne le nom du média.
     * @param name le nom du média
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return la durée du média
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Positionne la durée du média
     * @param duration durée du média
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * @return l'uudi unique du media
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * @return l'index du media dans dans la table.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Positionne l'index du média dans la table de l'interface graphique.
     * @param index index du média
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public String getFilePathName() {
        return filePathName;
    }

    public void setFilePathName(String filePathName) {
        this.filePathName = filePathName;
    }

    public KeyCode getKeycode() {
        return keycode;
    }

    public void setKeycode(KeyCode keycode) {
        this.keycode = keycode;
    }

    public Boolean getRepetable() {
        return repetable;
    }

    public void setRepetable(Boolean repetable) {
        this.repetable = repetable;
    }
}
