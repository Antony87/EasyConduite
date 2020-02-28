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

package easyconduite.tools.kodi;

import easyconduite.exception.RemotePlayableException;
import easyconduite.model.AbstractMedia;
import easyconduite.model.RemotePlayable;
import easyconduite.objects.media.RemotePlayer;
import easyconduite.util.HTTPHandler;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class KodiPlayer implements RemotePlayable {

    private static final Logger LOG = LogManager.getLogger(KodiPlayer.class);
    private final RemotePlayer remotePlayer;
    private URI kodiRpcURI;
    private HttpPost openHttpPost;
    private HttpPost pingHttpPost;
    private HttpPost activeHttpPost;
    private AbstractMedia.MediaStatus status;

    public KodiPlayer(RemotePlayer remote) throws RemotePlayableException {
        LOG.debug("KodiPlayer constructor called");
        remotePlayer = remote;
        kodiRpcURI = null;
        try {
            kodiRpcURI = new URIBuilder().setScheme("http").setHost(remotePlayer.getHost()).setPort(remotePlayer.getPort()).setPath("/jsonrpc").build();
            pingHttpPost = getOpenHttPost(new KodiRequestBuilder(KodiMethods.PING).build());
            final File file = new File(remotePlayer.getResource());
            openHttpPost = getOpenHttPost(new KodiRequestBuilder(KodiMethods.OPEN).setFile(file).build());
            activeHttpPost = getOpenHttPost(new KodiRequestBuilder(KodiMethods.GET_ACTIVE_PLAYERS).build());

        } catch (URISyntaxException e) {
            throw new RemotePlayableException("Exception JsonProcessing or URI Syntax", e);
        }
    }

    private HttpPost getOpenHttPost(String kodiRequest) {
        final HttpPost httpPost = new HttpPost();
        httpPost.setURI(kodiRpcURI);
        final StringEntity entity = new StringEntity(kodiRequest, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        return httpPost;
    }

    @Override
    public boolean isActive() {
        final HttpClient httpClient = HTTPHandler.getInstance().getHttpclient();
        try {
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(pingHttpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                LOG.trace("Response = {}", responseString);
                if (responseString.contains("\"pong\""))
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getActivePlayerId(){
        final HttpClient httpClient = HTTPHandler.getInstance().getHttpclient();
        Integer playerId = null;
        try {
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(activeHttpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                LOG.trace("Response = {}", responseString);
                final KodiResponse kodiResponse = KodiResponse.build(responseString);
                final List<ActivePlayer> result = kodiResponse.getResult();
                if(!result.isEmpty()){
                    ActivePlayer activePlayer = kodiResponse.getResult().get(0);
                    LOG.trace("ActivePlayer = {}", activePlayer);
                    playerId = activePlayer.getPlayerid();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playerId;
    }

    @Override
    public AbstractMedia.MediaStatus play() {
        final HttpClient httpClient = HTTPHandler.getInstance().getHttpclient();
        status = AbstractMedia.MediaStatus.HALTED;
        if(getActivePlayerId()==null){
            try {
                CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(openHttpPost);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                    LOG.trace("Response = {}", responseString);
                    if (responseString.contains("\"result\"") && responseString.contains("\"OK\""))
                        status = AbstractMedia.MediaStatus.PLAYING;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    @Override
    public AbstractMedia.MediaStatus pause() {
        return null;
    }

    @Override
    public AbstractMedia.MediaStatus stop() {
        return null;
    }

    @Override
    public AbstractMedia.MediaStatus getStatus() {
        return status;
    }
}
