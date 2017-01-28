/*
 * Copyright (C) 2017 Antony Fons
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package easyconduite.ui;

import easyconduite.objects.AudioMedia;
import easyconduite.objects.EasyconduiteException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class wraps a JavaFX MediaPlayer.<br>Offers severals features to
 * EasyConduite.
 *
 * @author antony
 */
public class EasyconduitePlayer {

    static final Logger LOG = LogManager.getLogger(EasyconduitePlayer.class);

    private final AudioMedia audioMedia;

    private MediaPlayer player;

    private Duration totalDuration;

    private DoubleProperty currentProgress = new ReadOnlyDoubleWrapper(0);

    public static EasyconduitePlayer create(AudioMedia media) throws EasyconduiteException {
        return new EasyconduitePlayer(media);
    }

    private EasyconduitePlayer(AudioMedia media) throws EasyconduiteException {

        LOG.debug("Construct EasyconduitePlayer with AudioMedia[{}]", media);

        this.audioMedia = media;
        try {
            final Media mediaForPlayer = new Media(audioMedia.getAudioFile().toURI().toString());
            player = new MediaPlayer(mediaForPlayer);
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

        player.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            setCurrentProgress(newValue.toMillis() / totalDuration.toMillis());
        });

        player.setOnEndOfMedia(() -> {
            // if repeat is false, force to stop de player.
            if (!audioMedia.getRepeatable()) {
                this.stop();
            }
        });

        player.setOnReady(() -> {
            if (audioMedia.getRepeatable()) {
                setRepeatable(true);
            } else {
                setRepeatable(false);
            }
            setTotalDuration(player.getStopTime());
            LOG.trace("Duration is {}", totalDuration.toMillis());
        });

        player.volumeProperty().bind(audioMedia.volumeProperty());
    }

    public final void stop() {
        player.stop();
    }

    public final void pause() {
        player.pause();
    }

    public void playPause() {

        Status status = player.getStatus();
        if (status == null) {
        } else {
            switch (status) {
                case PAUSED:
                    player.play();
                    break;
                case PLAYING:
                    player.pause();
                    break;
                case READY:
                    player.play();
                    break;
                case STOPPED:
                    player.play();
                    break;
                case UNKNOWN:
                    player.seek(Duration.ZERO);
                    player.play();
                    break;
                default:
                    break;
            }
        }
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public final Status getStatus() {
        return player.getStatus();
    }

    public boolean isRepeatable() {
        if (player.getCycleCount() == MediaPlayer.INDEFINITE) {
            return false;
        }
        return player.getCycleCount() > 1;
    }

    public final void setRepeatable(boolean repeatable) {
        if (repeatable) {
            player.setCycleCount(Integer.MAX_VALUE);
        } else {
            player.setCycleCount(1);
        }
    }

    public final void setTotalDuration(Duration totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Duration getTotalDuration() {
        return totalDuration;
    }

    public Double getCurrentProgress() {
        return currentProgress.doubleValue();
    }

    public final void setCurrentProgress(Double current) {
        currentProgress.setValue(current);
    }

    public DoubleProperty currentProgressProperty() {
        return currentProgress;
    }

}
