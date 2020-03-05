/*
 *
 *
 *  * Copyright (c) 2020.  Antony Fons
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package easyconduite.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import easyconduite.exception.EasyconduiteException;
import easyconduite.model.AbstractMedia;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class VideoMedia extends AbstractMedia {

    private File mediaFile;

    private double volume = 0.5;

    public VideoMedia() {
    }

    public VideoMedia(File mediaFile) {
        super();
        this.mediaFile = mediaFile;
    }

    @JsonIgnore
    private MediaPlayer player;

    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void initPlayer() throws EasyconduiteException {
        try {
            final Media mediaForPlayer = new Media(this.getMediaFile().toURI().toString());
            player = new MediaPlayer(mediaForPlayer);
            player.setVolume(this.getVolume());
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
    public void closePlayer() {

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

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

}
