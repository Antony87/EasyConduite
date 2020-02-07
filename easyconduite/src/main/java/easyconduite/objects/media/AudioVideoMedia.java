package easyconduite.objects.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import easyconduite.exception.EasyconduiteException;
import easyconduite.model.EasyMedia;
import easyconduite.tools.jackson.DurationDeserializer;
import easyconduite.tools.jackson.DurationSerializer;
import easyconduite.view.commons.PlayerVolumeFader;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Objects;


public class AudioVideoMedia extends EasyMedia {

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration fadeInDuration = Duration.ZERO;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration fadeOutDuration = Duration.ZERO;

    private File mediaFile;

    private double volume = 0.5;

    @JsonIgnore
    private MediaPlayer player;

    @JsonIgnore
    private PlayerVolumeFader fadeHandler;

    public AudioVideoMedia() {
    }

    public AudioVideoMedia(File file) {
        super();
        this.mediaFile = file;
    }

    public void initPlayer() throws EasyconduiteException {

        try {
            final Media mediaForPlayer = new Media(this.getMediaFile().toURI().toString());
            player = new MediaPlayer(mediaForPlayer);
            player.setVolume(this.getVolume());
            fadeHandler = new PlayerVolumeFader(this);
            player.setStartTime(Duration.ZERO);

            if(getName()==null){
                setName(getMediaFile().getName());
            }

            player.setOnEndOfMedia(() -> {
                if (!this.getLoppable()) {
                    this.stop();
                    player.volumeProperty().setValue(getVolume());
                    this.getPlayer().setCycleCount(1);
                }else{
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

    public File getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(File mediaFile) {
        this.mediaFile = mediaFile;
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
        if (!(o instanceof AudioVideoMedia)) return false;
        if (!super.equals(o)) return false;
        AudioVideoMedia that = (AudioVideoMedia) o;
        return Double.compare(that.volume, volume) == 0 &&
                Objects.equals(fadeInDuration, that.fadeInDuration) &&
                Objects.equals(fadeOutDuration, that.fadeOutDuration) &&
                mediaFile.equals(that.mediaFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fadeInDuration, fadeOutDuration, mediaFile, volume);
    }
}
