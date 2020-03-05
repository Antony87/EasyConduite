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
import easyconduite.media.MediaStatus;
import easyconduite.media.RemoteMedia;
import easyconduite.model.RemotePlayable;
import easyconduite.tools.HttpClientForMedias;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.HTTPHandler;
import easyconduite.util.PersistenceHelper;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class KodiManager implements RemotePlayable {

    private static final Logger LOG = LogManager.getLogger(KodiManager.class);
    private static KodiManager instance;
    private final Queue<RemoteMedia> clientActionsQueue;
    private final String activePlayerRequest;
    private final Map<String, HttpClientForMedias> mediaHostMap;

    private KodiManager() {
        activePlayerRequest = KodiRequest.getJsonRequest(KodiRequest.ACTIVE,null);
        clientActionsQueue = new ConcurrentLinkedQueue<>();
        mediaHostMap = new ConcurrentHashMap<>();
        HostListenerService hostListenerService = new HostListenerService();
        hostListenerService.setPeriod(Duration.millis(500));
        hostListenerService.start();
    }

    public static KodiManager getInstance() {
        if (instance == null) {
            synchronized (EasyConduitePropertiesHandler.class) {
                instance = new KodiManager();
            }
        }
        return instance;
    }

    private void updateMediaStatus(Integer activePlayer, RemoteMedia remoteMedia) {
        final MediaStatus mediaStatus = remoteMedia.getStatus();
        if (activePlayer == null) {
            if (mediaStatus != MediaStatus.STOPPED) {
                remoteMedia.setStatus(MediaStatus.STOPPED);
            }
        } else {
            try {
                final String host = remoteMedia.getHost();
                final KodiItemResponse.KodiItem item = getItem(host, activePlayer);
                if (isItemEqualsFile(item.getFile(), remoteMedia.getResource())) {
                    remoteMedia.setStatus(MediaStatus.PLAYING);
                } else {
                    remoteMedia.setStatus(MediaStatus.STOPPED);
                }
            } catch (RemotePlayableException e) {
                LOG.error("Error during update RemoteMedia {} on KodiPlayer {} Host {}", remoteMedia.getResource(), activePlayer, remoteMedia.getHost());
            }
        }
    }

    private boolean isItemEqualsFile(String itemFileString, URI mediaResource) {
        final File itemFile = new File(itemFileString);
        final File mediaFile = new File(mediaResource);
        return itemFile.equals(mediaFile);
    }

    public void registerKodiMedia(RemoteMedia media) throws URISyntaxException {
        final String host = media.getHost();
        if (!mediaHostMap.containsKey(host)) {
            final HttpClientForMedias httpClientForMedias = new HttpClientForMedias(media);
            httpClientForMedias.getMediaList().add(media);
            mediaHostMap.put(host, httpClientForMedias);
            LOG.debug("kodiMedia created to registred list : {}", media);
        } else {
            final HttpClientForMedias mediaHost = mediaHostMap.get(host);
            List<RemoteMedia> mediasList = mediaHost.getMediaList();
            if (mediasList.contains(media)) {
                LOG.debug("This media {} is already registrered", media);
                throw new IllegalArgumentException("This media is not registrered");
            }
            mediasList.add(media);
            LOG.debug("kodiMedia added to registred list : {}", media);
        }
    }

    public void unRegisterKodiMedia(RemoteMedia media) {
        final String host = media.getHost();
        if (!mediaHostMap.containsKey(host)) {
            LOG.debug("This host {} is not registrered", host);
            throw new IllegalArgumentException("This host is not registrered");
        } else {
            final HttpClientForMedias httpClientForMedias = mediaHostMap.get(host);
            final List<RemoteMedia> mediaList = httpClientForMedias.getMediaList();
            if (!mediaList.contains(media)) {
                LOG.debug("This media {} is not registrered", media);
                throw new IllegalArgumentException("This media is not registrered");
            }
            mediaList.remove(media);
            if (mediaList.isEmpty()) mediaHostMap.remove(host);
        }
    }

    private boolean openKodiMedia(RemoteMedia media) {
        CloseableHttpResponse response = null;
        HttpPost openHttpPost = null;
        try {
            if (media == null) throw new IllegalArgumentException("RemoteMedia is NULL");
            final String host = media.getHost();
            if (host == null) throw new IllegalArgumentException("host is NULL");
            final HttpClientForMedias httpClientForMedias = mediaHostMap.get(host);
            final String fileString = PersistenceHelper.separatorsToSystem(new File(media.getResource()).getCanonicalPath());
            final String jsonRequest = KodiRequest.getJsonRequest(KodiRequest.OPEN,fileString);
            openHttpPost = HTTPHandler.getHttPost(jsonRequest, httpClientForMedias.getUri());
            response = (CloseableHttpResponse) httpClientForMedias.getHttpClient().execute(openHttpPost);
            if (response.getCode() == HttpStatus.SC_OK) {
                final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                if (responseString.contains("\"result\"") && responseString.contains("\"OK\"")) {
                    return true;
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            LOG.error("Error occured during HttpPost {} whith Response {} ", openHttpPost, response, e);
            return false;
        }
        return false;
    }

    @Override
    public void play(RemoteMedia media) {
        media.setAction(RemoteMedia.Action.PLAY);
        clientActionsQueue.offer(media);
    }

    @Override
    public void pause(RemoteMedia media) {

    }

    @Override
    public void stop(RemoteMedia media) {
        media.setAction(RemoteMedia.Action.STOP);
        clientActionsQueue.offer(media);
    }

    public Integer getActivePlayer(String hostAdr) {
        Integer playerId = null;
        try {
            final HttpClientForMedias httpClientForMedias = mediaHostMap.get(hostAdr);
            if (httpClientForMedias == null)
                throw new IllegalArgumentException("MediaHost is NULL");
            final HttpPost httpPost = HTTPHandler.getHttPost(activePlayerRequest, httpClientForMedias.getUri());
            final CloseableHttpResponse response = (CloseableHttpResponse) httpClientForMedias.getHttpClient().execute(httpPost);
            if (response.getCode() == HttpStatus.SC_OK) {
                final String responseString = HTTPHandler.getResponse(response);
                LOG.trace("getActivePlayerId Response = {}", responseString);
                final KodiActiveResponse kodiActiveResponse = KodiRequest.build(responseString, KodiActiveResponse.class);
                final List<KodiActiveResponse.KodiActivePlayer> result = kodiActiveResponse.getResult();
                if (!result.isEmpty()) {
                    playerId = kodiActiveResponse.getResult().get(0).getPlayerid();
                    LOG.trace("KodiActivePlayer = {}", playerId);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            LOG.error("Exception within getActivePlayer with host {}", hostAdr, e);
            playerId = null;
        }
        return playerId;
    }

    public KodiItemResponse.KodiItem getItem(String hostAdr, Integer playerId) throws RemotePlayableException {
        KodiItemResponse.KodiItem kodiItem = null;
        try {
            if (hostAdr == null) throw new IllegalArgumentException("hostAdr is NULL");
            if (playerId == null) throw new IllegalArgumentException("playerId is NULL");
            final HttpClientForMedias httpClientForMedias = mediaHostMap.get(hostAdr);
            final HttpPost itemHttpPost = HTTPHandler.getHttPost(KodiRequest.getGetItemRequest(playerId), httpClientForMedias.getUri());
            final CloseableHttpResponse response = (CloseableHttpResponse) httpClientForMedias.getHttpClient().execute(itemHttpPost);
            if (response.getCode() == HttpStatus.SC_OK) {
                final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                if (!responseString.contains("error")) {
                    final KodiItemResponse kodiResponse = KodiRequest.build(responseString, KodiItemResponse.class);
                    kodiItem = kodiResponse.getResult().getItem();
                    LOG.trace("KodiItemResponseResponse = {}", kodiItem);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new RemotePlayableException();
        }
        return kodiItem;
    }

    public boolean stopKodiMedia(RemoteMedia media) {
        // si le media est déja stoppé, on considère l'action finie.
        if (media.getStatus() == MediaStatus.STOPPED) return true;
        final String host = media.getHost();
        if (host == null) throw new IllegalArgumentException("host is NULL");
        // si il n'y a pas de player actif, on considère le média stoppé
        final Integer activePlayer = getActivePlayer(host);
        if (activePlayer == null) return true;
        CloseableHttpResponse response = null;
        HttpPost stopHttpPost = null;
        try {
            final HttpClientForMedias httpClientForMedias = mediaHostMap.get(host);
            stopHttpPost = HTTPHandler.getHttPost(KodiRequest.getGetStopRequest(activePlayer), httpClientForMedias.getUri());
            response = (CloseableHttpResponse) httpClientForMedias.getHttpClient().execute(stopHttpPost);
            if (response.getCode() == HttpStatus.SC_OK) {
                final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                if (responseString.contains("\"result\"") && responseString.contains("\"OK\"")) {
                    return true;
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            LOG.error("Error occured during HttpPost {} whith Response {} ", stopHttpPost, response, e);
        }
        return false;
    }

    public Map<String, HttpClientForMedias> getMediaHostMap() {
        return mediaHostMap;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    private class HostListenerService extends ScheduledService<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
                    // Itération sur chaque host
                    for (Map.Entry<String, HttpClientForMedias> mediaHost : mediaHostMap.entrySet()) {
                        final String host = mediaHost.getKey();
                        final Integer activePlayer = getActivePlayer(host);
                        final List<RemoteMedia> mediaList = mediaHost.getValue().getMediaList();
                        mediaList.forEach(remoteMedia -> updateMediaStatus(activePlayer, remoteMedia));
                    }

                    RemoteMedia media;
                    do {
                        media = clientActionsQueue.poll();
                        if (media != null) executeAction(media);
                    } while (media != null);
                    return null;
                }
            };
        }

        private void executeAction(RemoteMedia media) {
            RemoteMedia.Action action = media.getAction();
            switch (action) {
                case PLAY:
                    if (media.getStatus() != MediaStatus.PLAYING && openKodiMedia(media)) {
                        media.setAction(null);
                    }
                    break;
                case STOP:
                    if (media.getStatus() != MediaStatus.STOPPED && stopKodiMedia(media)) {
                        media.setAction(null);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
