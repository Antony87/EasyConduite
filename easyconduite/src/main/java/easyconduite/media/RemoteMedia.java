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
import easyconduite.exception.RemotePlayableException;
import easyconduite.model.AbstractMedia;
import easyconduite.model.IRemotePlayer;
import easyconduite.tools.kodi.KodiPlayer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Objects;

public class RemoteMedia extends AbstractMedia {

    @JsonIgnore
    private static final Logger LOG = LogManager.getLogger(RemoteMedia.class);
    @JsonIgnore
    private final ObjectProperty<MediaStatus> statut = new SimpleObjectProperty();
    private Path resource;
    private double volume = 0.5;
    private RemoteType type;
    private String host;
    private int port;
    @JsonIgnore
    private IRemotePlayer remotePlayer;

    public RemoteMedia() {
        super();
    }

    public RemoteMedia(RemoteType type) {
        this();
        setDuration(new Duration(0));
        this.type = type;
        setStatut(MediaStatus.UNKNOWN);
    }

    @Override
    public void play() {
        switch (statut.getValue()) {
            case READY:
            case STOPPED:
            case PAUSED:
                remotePlayer.play();
                setStatut(MediaStatus.PLAYING);
                break;
            default:
                break;
        }
    }

    @Override
    public void pause() {
        switch (statut.getValue()) {
            case PLAYING:
                remotePlayer.stop();
                setStatut(MediaStatus.STOPPED);
                break;
            case READY:
            case STOPPED:
            case PAUSED:
            case HALTED:
            case UNKNOWN:
            default:
                break;
        }
    }

    @Override
    public void stop() {
        switch (statut.getValue()) {
            case PAUSED:
            case PLAYING:
                remotePlayer.stop();
                setStatut(MediaStatus.STOPPED);
                break;
            case READY:
            case STOPPED:
            case HALTED:
            case UNKNOWN:
            default:
                break;
        }
    }

    @Override
    public void initPlayer() throws EasyconduiteException {
        if (type.equals(RemoteType.KODI)) {
            if (remotePlayer == null) remotePlayer = new KodiPlayer(this);
            try {
                ((KodiPlayer) remotePlayer).initializePlayer();
            } catch (RemotePlayableException e) {
                throw new EasyconduiteException(e.getMessage());
            }
        }
    }

    @Override
    public void closePlayer() {
        remotePlayer=null;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public RemoteType getType() {
        return type;
    }

    public void setType(RemoteType remoteType) {
        this.type = remoteType;
    }

    public Path getResource() {
        return resource;
    }

    public void setResource(Path resource) {
        this.resource = resource;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public MediaStatus getStatut() {
        return statut.get();
    }

    public void setStatut(MediaStatus statut) {
        this.statut.set(statut);
    }

    public ObjectProperty<MediaStatus> statutProperty() {
        return statut;
    }

    public IRemotePlayer getRemotePlayer() {
        return remotePlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteMedia)) return false;
        RemoteMedia that = (RemoteMedia) o;
        return port == that.port &&
                resource.equals(that.resource) &&
                type == that.type &&
                host.equals(that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, type, host, port);
    }

    @Override
    public String toString() {
        return "RemoteMedia{" +
                "resource=" + resource +
                ", type=" + type +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", player=" + remotePlayer +
                ", status=" + statut +
                "} " + super.toString();
    }

    public enum RemoteType {
        KODI, VLC
    }
}
