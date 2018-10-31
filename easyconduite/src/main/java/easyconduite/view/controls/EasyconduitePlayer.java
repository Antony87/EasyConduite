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
package easyconduite.view.controls;

import easyconduite.controllers.MainController;
import easyconduite.exception.EasyconduiteException;
import easyconduite.objects.AudioMedia;
import easyconduite.tools.PersistenceHelper;
import easyconduite.view.commons.PlayerVolumeFader;
import java.util.UUID;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import easyconduite.model.ChainingUpdater;

/**
 * Class wraps a JavaFX MediaPlayer.<br>Offers severals features to
 * EasyConduite.
 *
 * @author antony
 */
public class EasyconduitePlayer implements ChainingUpdater {

    static final Logger LOG = LogManager.getLogger(EasyconduitePlayer.class);

    private AudioMedia audioMedia;

    private MainController mainController;

    private MediaPlayer player;

    private PlayerVolumeFader fadeHandler;

    public static EasyconduitePlayer create(AudioMedia media, MainController contoller) throws EasyconduiteException {
        return new EasyconduitePlayer(media, contoller);
    }

    private EasyconduitePlayer(AudioMedia media, MainController contoller) throws EasyconduiteException {
        LOG.debug("Construct EasyconduitePlayer with AudioMedia[{}]", media);

        audioMedia = media;
        mainController = contoller;

        fadeHandler = new PlayerVolumeFader(EasyconduitePlayer.this, audioMedia);

        try {
            final Media mediaForPlayer = new Media(PersistenceHelper.getPathURIString(audioMedia.getFilePathName()));
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
                player.volumeProperty().setValue(audioMedia.getVolume());
                playChildsEnd();
            }
        });
    }

    private void playChildsEnd() {
        audioMedia.getUuidChildEnd().forEach((UUID t) -> {
            mainController.getByUUID(t).playPause();
        });
    }

    private void playChildsBegin() {
        audioMedia.getUuidChildBegin().forEach((UUID t) -> {
            mainController.getByUUID(t).playPause();
        });
    }

    public final void stop() {
        fadeHandler.stop();
        player.stop();
        player.volumeProperty().setValue(audioMedia.getVolume());
    }

    public final void pause() {
        // si existe une transition fade out
        if (audioMedia.getFadeOutDuration() != Duration.ZERO) {
            fadeHandler.fadeOut(audioMedia.getFadeOutDuration());
        } else {
            player.pause();
        }
    }

    public final void play() {
        if (audioMedia.getFadeInDuration() != Duration.ZERO) {
            fadeHandler.fadeIn(audioMedia.getFadeInDuration());
        }
        Status status = player.getStatus();
        if (status.equals(Status.STOPPED) || status.equals(Status.READY)) {
            playChildsBegin();
        }
        player.play();
    }

    public final void playPause() {
        Status status = player.getStatus();
        switch (status) {
            case PAUSED:
                this.play();
                break;
            case PLAYING:
                this.pause();
                break;
            case READY:
                this.play();
                break;
            case STOPPED:
                this.play();
                break;
            default:
                break;
        }
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void setNext(ChainingUpdater next) {
    }

    @Override
    public final void updateFromAudioMedia(AudioMedia media) {
        if (audioMedia.equals(media)) {
            audioMedia = media;
            // configuration de EasyconduitePlayer en fonction de AudioMedia
            if (audioMedia.getRepeatable()) {
                player.setCycleCount(Integer.MAX_VALUE);
            } else {
                player.setCycleCount(1);
            }

            player.setVolume(audioMedia.getVolume());

        } else {
            ActionDialog.showWarning("Incohérence des objets", "Les objets AudioMedia ne sont pas egaux");
        }
    }

    @Override
    public void removeChild(ChainingUpdater audioMedia) {
        fadeHandler.stop();
        fadeHandler = null;
        player.dispose();
    }

}
