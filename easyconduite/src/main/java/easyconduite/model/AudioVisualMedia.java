package easyconduite.model;

import java.io.File;
import java.util.Objects;

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

    public void setVolume(double volume) {
        this.volume=volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AudioVisualMedia that = (AudioVisualMedia) o;
        return Objects.equals(volume, that.volume) &&
                mediaFile.equals(that.mediaFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), volume, mediaFile);
    }
}
