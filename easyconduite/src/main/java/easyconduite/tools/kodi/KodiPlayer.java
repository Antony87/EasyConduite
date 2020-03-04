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
import easyconduite.model.RemotePlayable;
import easyconduite.objects.media.MediaStatus;
import easyconduite.objects.media.RemoteMedia;
import easyconduite.util.HTTPHandler;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
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
    private final HttpClient httpClient;
    private URI kodiRpcURI;
    private HttpPost openHttpPost;
    private HttpPost pingHttpPost;
    private HttpPost activeHttpPost;
    private final File resourceFile;
    private MediaStatus status;

    public KodiPlayer(RemoteMedia remote) throws RemotePlayableException {
        LOG.debug("KodiPlayer constructor called");
        kodiRpcURI = null;
        httpClient = HTTPHandler.getInstance().getHttpclient();
        resourceFile = new File(remote.getResource());
        try {
            kodiRpcURI = new URIBuilder().setScheme("http").setHost(remote.getHost()).setPort(remote.getPort()).setPath("/jsonrpc").build();
            pingHttpPost = getHttPost(KodiRequestHelper.getMethodRequest(KodiMethods.PING));
            openHttpPost = getHttPost(KodiRequestHelper.getOpenRequest(resourceFile));
            activeHttpPost = getHttPost(KodiRequestHelper.getMethodRequest(KodiMethods.GET_ACTIVE_PLAYERS));


        } catch (URISyntaxException e) {
            throw new RemotePlayableException("Exception JsonProcessing or URI Syntax", e);
        }
    }

    private HttpPost getHttPost(String kodiRequest) {
        final HttpPost httpPost = new HttpPost(kodiRpcURI);
        final StringEntity entity = new StringEntity(kodiRequest, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        return httpPost;
    }

    @Override
    public boolean isActive() {
        try {
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(pingHttpPost);
            if (response.getCode() == HttpStatus.SC_OK) {
                final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                LOG.trace("Ping Response = {}", responseString);
                if (responseString.contains("\"pong\""))
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getActivePlayerId() {
        Integer playerId = null;
        try {
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(activeHttpPost);
            if (response.getCode() == HttpStatus.SC_OK) {
                final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                LOG.trace("getActivePlayerId Response = {}", responseString);
                final KodiActiveResponse kodiActiveResponse = KodiRequestHelper.build(responseString, KodiActiveResponse.class);
                final List<KodiActiveResponse.KodiActivePlayer> result = kodiActiveResponse.getResult();
                if (!result.isEmpty()) {
                    playerId = kodiActiveResponse.getResult().get(0).getPlayerid();
                    LOG.trace("KodiActivePlayer = {}", playerId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playerId;
    }

    public KodiItemResponse.KodiItem getItem(Integer playerId) {
        final HttpPost itemHttpPost = getHttPost(KodiRequestHelper.getGetItemRequest(playerId));
        KodiItemResponse.KodiItem kodiItem = null;
        if (playerId != null) {
            try {
                CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(itemHttpPost);
                if (response.getCode() == HttpStatus.SC_OK) {
                    LOG.trace("HTTPPost {}", itemHttpPost.getEntity());
                    final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                    if (!responseString.contains("error")) {
                        final KodiItemResponse kodiResponse = KodiRequestHelper.build(responseString, KodiItemResponse.class);
                        kodiItem = kodiResponse.getResult().getItem();
                        LOG.trace("KodiItemResponseResponse = {}", kodiItem);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return kodiItem;
    }

    @Override
    public MediaStatus play() {
        status = MediaStatus.UNKNOWN;
        final Integer activePlayerId = getActivePlayerId();
        if (activePlayerId != null) {
            final KodiItemResponse.KodiItem kodiItem = getItem(activePlayerId);
            if(kodiItem!=null){
                File itemFile = new File(kodiItem.getFile());
                if(!itemFile.equals(resourceFile)){
                    stop();
                }
            }
        }
        if (status!= MediaStatus.PLAYING) {
            try {
                CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(openHttpPost);
                if (response.getCode() == HttpStatus.SC_OK) {
                    final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                    LOG.trace("Play Response = {}", responseString);
                    if (responseString.contains("\"result\"") && responseString.contains("\"OK\""))
                        status = MediaStatus.PLAYING;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    @Override
    public MediaStatus pause() {
        return null;
    }

    @Override
    public MediaStatus stop() {
        status = MediaStatus.UNKNOWN;

            final HttpPost itemHttpPost = getHttPost(KodiRequestHelper.getGetStopRequest(1));
            try {
                CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(openHttpPost);
                final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                LOG.trace("Stop Response = {}", responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            status = MediaStatus.STOPPED;

        return status;
    }

    @Override
    public MediaStatus getStatus() {
        return status;
    }

}
