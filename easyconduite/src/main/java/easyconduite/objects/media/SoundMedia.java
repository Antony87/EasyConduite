package easyconduite.objects.media;

import easyconduite.model.EasyMediaClass;
import easyconduite.view.commons.PlayerVolumeFader;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class SoundMedia extends EasyMediaClass {

    private Duration fadeInDuration = Duration.ZERO;

    private Duration fadeOutDuration = Duration.ZERO;

    private transient MediaPlayer player;

    private PlayerVolumeFader fadeHandler;

    private final DoubleProperty volume = new ReadOnlyDoubleWrapper();

    public SoundMedia(File fichierSon) {
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
