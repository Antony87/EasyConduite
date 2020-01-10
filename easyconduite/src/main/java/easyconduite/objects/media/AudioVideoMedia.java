package easyconduite.objects.media;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import easyconduite.model.AudioVisualMedia;
import easyconduite.view.commons.PlayerVolumeFader;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;


public class AudioVideoMedia extends AudioVisualMedia {

    private Duration fadeInDuration = Duration.UNKNOWN;

    private Duration fadeOutDuration = Duration.UNKNOWN;

    @XStreamOmitField
    private MediaPlayer player;

    @XStreamOmitField
    private PlayerVolumeFader fadeHandler;

    public AudioVideoMedia(File file) {
        super(file);
    }

    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

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

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public PlayerVolumeFader getFadeHandler() {
        return fadeHandler;
    }

    public void setFadeHandler(PlayerVolumeFader fadeHandler) {
        this.fadeHandler = fadeHandler;
    }
}
