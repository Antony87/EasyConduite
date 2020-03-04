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

package easyconduite.objects.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import easyconduite.model.AbstractMedia;
import easyconduite.model.RemotePlayable;
import easyconduite.tools.kodi.KodiManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class RemoteMedia extends AbstractMedia {

    public enum Type{
        KODI,VLC
    }

    public enum Action{
        PLAY,STOP,PAUSE
    }

    private URI resource;

    private Type type;

    private String host;

    private int port;

    @JsonIgnore
    private RemotePlayable player;

    @JsonIgnore
    private ObjectProperty<MediaStatus> status = new SimpleObjectProperty<>();

    public RemoteMedia() {
        super();
    }

    public RemoteMedia(Type type) {
        this();
        setDuration(new Duration(0));
        this.type =type;
        this.status.setValue(MediaStatus.UNKNOWN);
    }

    @Override
    public void play() {
        final MediaStatus mediaStatus = player.play();
        statusProperty().setValue(mediaStatus);
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void initPlayer() {

        if(type.equals(Type.KODI)){
            KodiManager observer = KodiManager.getInstance();
            try {
                observer.registerKodiPlayer(this);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closePlayer() {

    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Type getType() {
        return type;
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

    public ObjectProperty<MediaStatus> statusProperty() {
        return status;
    }

    public void setStatus(MediaStatus mediaStatus) {
        this.status.set(mediaStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteMedia)) return false;
        if (!super.getName().equals(((RemoteMedia) o).getName())) return false;
        RemoteMedia that = (RemoteMedia) o;
        return port == that.port &&
                Objects.equals(resource, that.resource) &&
                type == that.type &&
                Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getName(), resource, type, host, port);
    }

    @Override
    public String toString() {
        return "RemoteMedia{" +
                "resource=" + resource +
                ", type=" + type +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", player=" + player +
                ", status=" + status +
                "} " + super.toString();
    }
}
