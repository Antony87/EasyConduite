package easyconduite.objects.media;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import easyconduite.exception.EasyconduiteException;
import easyconduite.model.AudioVisualMedia;
import easyconduite.view.commons.PlayerVolumeFader;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;


public class AudioVideoMedia extends AudioVisualMedia {

    private Duration fadeInDuration = Duration.ZERO;

    private Duration fadeOutDuration = Duration.ZERO;

    @XStreamOmitField
    private MediaPlayer player;

    @XStreamOmitField
    private PlayerVolumeFader fadeHandler;

    public AudioVideoMedia(File file) {
        super(file);
    }

    public void initPlayer() throws EasyconduiteException {

        try {

            final Media mediaForPlayer = new Media(this.getMediaFile().toURI().toString());
            player = new MediaPlayer(mediaForPlayer);
            player.setVolume(this.getVolume());
            fadeHandler = new PlayerVolumeFader(this);

            boolean loppable = getLoppable();
            player.setOnEndOfMedia(() -> {
                if (!loppable) {
                    this.stop();
                    player.volumeProperty().setValue(getVolume());
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
}
