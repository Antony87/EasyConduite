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
import easyconduite.model.RemotePlayable;
import easyconduite.tools.kodi.KodiManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.Objects;

public class RemoteMedia extends AbstractMedia {

    @JsonIgnore
    private static final Logger LOG = LogManager.getLogger(RemoteMedia.class);
    @JsonIgnore
    private Action action;

    private URI resource;

    private Type type;

    private String host;

    private int port;

    @JsonIgnore
    private RemotePlayable remoteManager;

    @JsonIgnore
    private ObjectProperty<MediaStatus> status = new SimpleObjectProperty<>();

    public RemoteMedia() {
        super();
    }

    public RemoteMedia(Type type) {
        this();
        setDuration(new Duration(0));
        this.type = type;
        this.status.setValue(MediaStatus.STOPPED);
    }

    @Override
    public void play() {
        if (isInitialized()) remoteManager.play(this);
    }

    @Override
    public void pause() {
        // reste à implementer
    }

    @Override
    public void stop() {
        if (isInitialized()) remoteManager.stop(this);
    }

    @Override
    public void initPlayer() throws EasyconduiteException {

        if (type.equals(Type.KODI)) {
            remoteManager = KodiManager.getInstance();
            try {
                ((KodiManager) remoteManager).registerKodiMedia(this);
                setInitialized(true);
            } catch (RemotePlayableException e) {
                LOG.error("Error occured with RemoteMedia {}", this);
                throw new EasyconduiteException(e.getMessage());
            }
        }
    }

    @Override
    public void closePlayer() {
        //TODO a implementer
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public URI getResource() {
        return resource;
    }

    public void setResource(URI resource) {
        this.resource = resource;
    }

    public MediaStatus getStatus() {
        return status.get();
    }

    public void setStatus(MediaStatus mediaStatus) {
        this.status.set(mediaStatus);
    }

    public ObjectProperty<MediaStatus> statusProperty() {
        return status;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
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
                ", player=" + remoteManager +
                ", status=" + status +
                "} " + super.toString();
    }

    public enum Type {
        KODI, VLC
    }

    public enum Action {
        PLAY, STOP, PAUSE
    }
}
