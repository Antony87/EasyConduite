package easyconduite.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import easyconduite.exception.EasyconduiteException;
import easyconduite.model.AbstractMedia;
import easyconduite.tools.jackson.DurationDeserializer;
import easyconduite.tools.jackson.DurationSerializer;
import easyconduite.view.commons.PlayerVolumeFader;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Objects;


public class AudioMedia extends AbstractMedia {

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration fadeInDuration = Duration.ZERO;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration fadeOutDuration = Duration.ZERO;

    private URI resourcePath;

    private double volume = 0.5;

    @JsonIgnore
    private MediaPlayer player;

    @JsonIgnore
    private PlayerVolumeFader fadeHandler;

    public AudioMedia() {
        super();
    }

    public AudioMedia(URI resource) {
        this();
        this.resourcePath = resource;
    }

    /**
     * This method initialize a player for this type of media.
     *
     * @throws EasyconduiteException
     * @see MediaPlayer
     */
    @Override
    public void initPlayer() throws EasyconduiteException {

        try {
            final Media mediaForPlayer = new Media(this.getResourcePath().toString());
            player = new MediaPlayer(mediaForPlayer);
            player.setVolume(this.getVolume());
            fadeHandler = new PlayerVolumeFader(this);
            player.setStartTime(Duration.ZERO);

            if (getName() == null) {
                final String name = Paths.get(getResourcePath()).getFileName().toString();
                setName(name);
            }

            player.setOnEndOfMedia(() -> {
                if (!this.getLoppable()) {
                    this.stop();
                    player.volumeProperty().setValue(getVolume());
                    this.getPlayer().setCycleCount(1);
                } else {
                    player.seek(Duration.ZERO);
                }
            });


        } catch (NullPointerException ne) {
            throw new EasyconduiteException("Impossible de trouver le fichier et de constituer le media", ne);
        } catch (IllegalArgumentException iae) {
            throw new EasyconduiteException("Le chemin du fichier n'est pas conforme", iae);
        } catch (UnsupportedOperationException uoe) {
            throw new EasyconduiteException("Impossible d'acceder au fichier", uoe);
        } catch (MediaException me) {
            final String msg = "Impossible de charger le fichier [" + me.getType().toString() + "]";
            throw new EasyconduiteException(msg, me);
        }
    }

    @Override
    public void closePlayer() {
        player.dispose();
    }

    @Override
    public void play() {
        if (getFadeInDuration() != Duration.ZERO) {
            fadeHandler.fadeIn(this.fadeInDuration);
        }
        player.play();
    }

    @Override
    public void pause() {
        if (getFadeOutDuration() != Duration.ZERO) {
            fadeHandler.fadeOut(this.fadeOutDuration);
        } else {
            player.pause();
        }
    }

    @Override
    public void stop() {
        fadeHandler.stop();
        player.stop();
        player.volumeProperty().setValue(this.getVolume());
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

    public MediaPlayer getPlayer() {
        return player;
    }

    public URI getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(URI resourcePath) {
        this.resourcePath = resourcePath;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AudioMedia)) return false;
        if (!super.equals(o)) return false;
        AudioMedia that = (AudioMedia) o;
        return Double.compare(that.volume, volume) == 0 &&
                Objects.equals(fadeInDuration, that.fadeInDuration) &&
                Objects.equals(fadeOutDuration, that.fadeOutDuration) &&
                resourcePath.equals(that.resourcePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fadeInDuration, fadeOutDuration, resourcePath, volume);
    }

    @Override
    public String toString() {
        return "AudioMedia{" +
                "fadeInDuration=" + fadeInDuration +
                ", fadeOutDuration=" + fadeOutDuration +
                ", resourcePath=" + resourcePath +
                ", volume=" + volume +
                "} " + super.toString();
    }
}
