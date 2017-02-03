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

import easyconduite.model.AudioConfigChain;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.EasyconduiteException;
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
public class EasyconduitePlayer implements AudioConfigChain {

    static final Logger LOG = LogManager.getLogger(EasyconduitePlayer.class);

    private final AudioMedia audioMedia;

    private MediaPlayer player;

    private AudioConfigChain nextChain;

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

        player.setOnEndOfMedia(() -> {
            // if repeat is false, force to stop de player.
            if (!audioMedia.getRepeatable()) {
                this.stop();
            }
        });

        player.setOnReady(() -> {
            setRepeatable(audioMedia.getRepeatable());
            // Get duration by StopTime and put to the AudioMedia property.
            this.audioMedia.setAudioDuration(player.getStopTime());
            player.setOnReady(null);
        });

        player.volumeProperty().bind(audioMedia.volumeProperty().divide(100));
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

    public final void setRepeatable(boolean repeatable) {
        if (repeatable) {
            player.setCycleCount(Integer.MAX_VALUE);
        } else {
            player.setCycleCount(1);
        }
    }

    @Override
    public void setNext(AudioConfigChain next) {
        this.nextChain = next;
    }

    @Override
    public void chainConfigure(AudioMedia media) {
        if (this.audioMedia.equals(media)) {
            setRepeatable(audioMedia.getRepeatable());
        } else {
            ActionDialog.showWarning("Incohérence des objets", "Les objets AudioMedia ne sont pas egaux");
        }
    }
}
