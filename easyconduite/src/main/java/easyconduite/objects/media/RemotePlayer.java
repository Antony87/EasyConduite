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
import easyconduite.exception.EasyconduiteException;
import easyconduite.exception.RemotePlayableException;
import easyconduite.model.AbstractMedia;
import easyconduite.model.RemotePlayable;
import easyconduite.tools.kodi.KodiPlayer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;

import java.net.URI;

public class RemotePlayer extends AbstractMedia {

    public enum Type{
        KODI,VLC
    }

    private URI resource;

    private Type type;

    private String host;

    private int port;

    @JsonIgnore
    private RemotePlayable player;

    @JsonIgnore
    private ObjectProperty<MediaStatus> status = new SimpleObjectProperty<>();

    public RemotePlayer() {
        super();
    }

    public RemotePlayer(Type type) {
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
    public void initPlayer() throws EasyconduiteException {

        if(type.equals(Type.KODI)){
            try {
                player = new KodiPlayer(this);
            } catch (RemotePlayableException e) {
                throw new EasyconduiteException("",e);
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
}
