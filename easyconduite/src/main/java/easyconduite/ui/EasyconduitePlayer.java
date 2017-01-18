/*
 * Copyright (C) 2017 Antony Fons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package easyconduite.ui;

import easyconduite.objects.AudioMedia;
import easyconduite.objects.EasyconduiteException;
import easyconduite.util.Config;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

/**
 *
 * @author antony
 */
public class EasyconduitePlayer {

    private final AudioMedia audioMedia;

    private final MediaPlayer player;

    private final Duration totalDuration;

    private DoubleProperty currentProgress = new ReadOnlyDoubleWrapper(0);

    private static final String CLASSNAME = EasyconduitePlayer.class.getName();
    private static final Logger LOGGER = Config.getCustomLogger(CLASSNAME);

    public static EasyconduitePlayer create(AudioMedia media) throws EasyconduiteException {
        return new EasyconduitePlayer(media);
    }

    private EasyconduitePlayer(AudioMedia media) throws EasyconduiteException {

        this.audioMedia = media;
        try {
            final Media mediaForPlayer = new Media(audioMedia.getAudioFile().toURI().toString());
            player = new MediaPlayer(mediaForPlayer);
        } catch (NullPointerException ne) {
            LOGGER.log(Level.SEVERE, "File path for media is null or media is null for AUdioMedia[{0}]", audioMedia.getName());
            throw new EasyconduiteException("Impossible de trouver le fichier et de constituer le media", ne);
        } catch (IllegalArgumentException iae) {
            LOGGER.log(Level.SEVERE, "File path for media is non-conform for AUdioMedia[{0}]", audioMedia.getName());
            throw new EasyconduiteException("Le chemin du fichier n'est pas conforme", iae);
        } catch (UnsupportedOperationException uoe) {
            LOGGER.log(Level.SEVERE, "File acces isn't supported for AUdioMedia[{0}]", audioMedia.getName());
            throw new EasyconduiteException("Impossible d'acceder au fichier", uoe);
        } catch (MediaException me) {
            LOGGER.log(Level.SEVERE, "File isn't supported for AUdioMedia[{0}]", audioMedia.getName());
            final String msg = "Impossible de charger le fichier [" + me.getType().toString() + "]";
            throw new EasyconduiteException(msg, me);
        }

        player.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            if (newValue.greaterThan(oldValue)) {
                setCurrentProgress(newValue.toSeconds() / player.getTotalDuration().toSeconds());
            }
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
            player.seek(player.getStartTime());
        });
        
        player.setOnStopped(() -> {
            currentProgressProperty().set((double) 0);
        });

        totalDuration = player.getTotalDuration();

        player.volumeProperty().bind(audioMedia.volumeProperty());

    }

    public final void stop() {
        player.stop();
    }

    public void playPause() {

        Status status = player.getStatus();
        if (status == null) {
            LOGGER.warning("Play/pause with null status !");
        } else {
            LOGGER.log(Level.FINE, "Play/pause action with {0}", status);
            switch (status) {
                case PAUSED:
                    LOGGER.log(Level.FINE, "Call Player play method");
                    player.play();
                    break;
                case PLAYING:
                    LOGGER.log(Level.FINE, "Call Player pause method");
                    player.pause();
                    break;
                case READY:
                    LOGGER.log(Level.FINE, "Call Player play method");
                    player.play();
                    break;
                case STOPPED:
                    LOGGER.log(Level.FINE, "Call Player play method");
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
    
    public final Status getStatus(){
        return player.getStatus();
    }

    public boolean isRepeatable() {
        if (player.getCycleCount() == MediaPlayer.INDEFINITE) {
            return false;
        }
        if (player.getCycleCount() > 1) {
            return true;
        }
        return false;
    }

    public final void setRepeatable(boolean repeatable) {
        if (repeatable) {
            player.setCycleCount(Integer.MAX_VALUE);
        } else {
            player.setCycleCount(1);
        }
    }

    public Duration getTotalDuration() {
        return totalDuration;
    }

    public Double getCurrentProgress() {
        return currentProgress.doubleValue();
    }

    public final void setCurrentProgress(Double current) {
        currentProgress.set(current);
    }

    public DoubleProperty currentProgressProperty() {
        return currentProgress;
    }

}
