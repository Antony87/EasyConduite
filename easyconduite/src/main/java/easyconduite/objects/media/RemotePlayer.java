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
import easyconduite.model.EasyMedia;
import easyconduite.util.HTTPHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.net.URL;

public class RemotePlayer extends EasyMedia {

    enum Type{
        KODI,VLC
    }

    private final Type typeRemotePlayer;

    private URL remotePlayerURL;

    private int port;

    @JsonIgnore
    private final HttpClient httpClient;

    private HttpPost playHttpPost;

    private HttpPost pauseHttpPost;

    private HttpPost stopHttpPost;

    public RemotePlayer(Type type) {
        typeRemotePlayer=type;
        httpClient = HTTPHandler.getInstance().getHttpclient();
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

    @Override
    public void initPlayer() throws EasyconduiteException {


        if(typeRemotePlayer.equals(Type.KODI)){


        }


    }

}
