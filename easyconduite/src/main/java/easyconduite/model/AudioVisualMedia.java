package easyconduite.model;

import java.io.File;

public abstract class AudioVisualMedia extends EasyMedia implements IeasyMedia {

    private Double volume;

    private File mediaFile;

    public AudioVisualMedia(File file) {
        this.mediaFile = file;
        this.setName(file.getName());
    }

    public File getMediaFile() {
        return mediaFile;
    }

    public Double getVolume() {
        return volume;
    }

    public Double volumeProperty() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume=volume;
    }

}
