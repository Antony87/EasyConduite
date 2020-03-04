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
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.HTTPHandler;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class KodiManager implements RemotePlayable {

    private static final Logger LOG = LogManager.getLogger(KodiManager.class);
    private static KodiManager INSTANCE;
    private final Queue<RemoteMedia> clientActionsQueue;
    private final String activePlayerRequest;
    private final Map<String, MediasHost> mediaHostMap;


    private KodiManager() {
        activePlayerRequest = KodiRequestHelper.getMethodRequest(KodiMethods.GET_ACTIVE_PLAYERS);
        clientActionsQueue = new ConcurrentLinkedQueue<>();
        mediaHostMap = new ConcurrentHashMap<>();


        final ScheduledService<Void> hostListenerService = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        // Itération sur chaque host
                        for (Map.Entry<String, MediasHost> mediaHost : mediaHostMap.entrySet()) {
                            final String host = mediaHost.getKey();
                            final Integer activePlayer = getActivePlayer(host);
                            final List<RemoteMedia> mediaList = mediaHost.getValue().getMediaList();
                            // obtention de l'état du host (player actif, etc)
                            if (activePlayer == null) {
                                mediaList.forEach(remoteMedia -> {
                                    if (remoteMedia.statusProperty().getValue() != MediaStatus.STOPPED) {
                                        remoteMedia.setStatus(MediaStatus.STOPPED);
                                    }
                                });
                            } else {
                                try {
                                    final KodiItemResponse.KodiItem item = getItem(host, activePlayer);
                                    for (RemoteMedia remoteMedia : mediaList) {
                                        if (isItemEqualsFile(item.getFile(), remoteMedia.getResource())) {
                                            remoteMedia.setStatus(MediaStatus.PLAYING);
                                        } else {
                                            remoteMedia.setStatus(MediaStatus.STOPPED);
                                        }
                                    }
                                } catch (RemotePlayableException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        // traitement des actions pour ce host
                        RemoteMedia media;
                        do {
                            media = clientActionsQueue.poll();
                            if(media!=null) executeAction(media);
                        } while (media !=null);
                        return null;
                    }
                };
            }
        };
        hostListenerService.setPeriod(Duration.millis(500));
        hostListenerService.start();
    }

    public static KodiManager getInstance() {
        if (INSTANCE == null) {
            synchronized (EasyConduitePropertiesHandler.class) {
                INSTANCE = new KodiManager();
            }
        }
        return INSTANCE;
    }

    public void pushAction(RemoteMedia media){
        clientActionsQueue.offer(media);
    }

    private void executeAction(RemoteMedia media){

    }

    private boolean isItemEqualsFile(String itemFileString, URI mediaResource) {
        final File itemFile = new File(itemFileString);
        final File mediaFile = new File(mediaResource);
        if (itemFile.equals(mediaFile)) return true;
        return false;
    }

    public void registerKodiPlayer(RemoteMedia media) throws URISyntaxException {
        final String host = media.getHost();
        if (!mediaHostMap.containsKey(host)) {
            final MediasHost mediasHost = new MediasHost(media);
            mediasHost.getMediaList().add(media);
            mediaHostMap.put(host, mediasHost);
            LOG.debug("kodiMedia created to registred list : {}", media);
        } else {
            final MediasHost mediaHost = mediaHostMap.get(host);
            List<RemoteMedia> mediasList = mediaHost.getMediaList();
            if (mediasList.contains(media)) {
                LOG.debug("This media {} is already registrered", media);
                throw new IllegalArgumentException("This media is not registrered");
            }
            mediasList.add(media);
            LOG.debug("kodiMedia added to registred list : {}", media);
        }
    }

    public void unRegisterKodiPlayer(RemoteMedia media) {
        final String host = media.getHost();
        if (!mediaHostMap.containsKey(host)) {
            LOG.debug("This host {} is not registrered", host);
            throw new IllegalArgumentException("This host is not registrered");
        } else {
            final MediasHost mediasHost = mediaHostMap.get(host);
            final List<RemoteMedia> mediaList = mediasHost.getMediaList();
            if (!mediaList.contains(media)) {
                LOG.debug("This media {} is not registrered", media);
                throw new IllegalArgumentException("This media is not registrered");
            }
            mediaList.remove(media);
            if (mediaList.isEmpty()) mediaHostMap.remove(host);
        }
    }


    public Integer getActivePlayer(String hostAdr) {
        LOG.trace("getActivePlayer called to {}", hostAdr);
        Integer playerId = null;
        try {
            final MediasHost mediasHost = mediaHostMap.get(hostAdr);
            if (mediasHost == null) throw new IllegalArgumentException("MediaHost is NULL");
            final HttpPost httpPost = HTTPHandler.getHttPost(activePlayerRequest, mediasHost.getUri());
            CloseableHttpResponse response = (CloseableHttpResponse) mediasHost.getHttpClient().execute(httpPost);
            if (response.getCode() == HttpStatus.SC_OK) {
                final String responseString = HTTPHandler.getResponse(response);
                LOG.trace("getActivePlayerId Response = {}", responseString);
                final KodiActiveResponse kodiActiveResponse = KodiRequestHelper.build(responseString, KodiActiveResponse.class);
                final List<KodiActiveResponse.KodiActivePlayer> result = kodiActiveResponse.getResult();
                if (!result.isEmpty()) {
                    playerId = kodiActiveResponse.getResult().get(0).getPlayerid();
                    LOG.trace("KodiActivePlayer = {}", playerId);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            LOG.error("Exception within getActivePlayer",e.getMessage());
            playerId=null;
        }
        return playerId;
    }

    public KodiItemResponse.KodiItem getItem(String hostAdr, Integer playerId) throws RemotePlayableException {
        LOG.debug("getItem called to {} with playerId = {}", hostAdr, playerId);
        KodiItemResponse.KodiItem kodiItem = null;
        try {
            if (hostAdr == null) throw new IllegalArgumentException("hostAdr is NULL");
            if (playerId == null) throw new IllegalArgumentException("playerId is NULL");
            final MediasHost mediasHost = mediaHostMap.get(hostAdr);
            final HttpPost itemHttpPost = HTTPHandler.getHttPost(KodiRequestHelper.getGetItemRequest(playerId), mediasHost.getUri());
            final CloseableHttpResponse response = (CloseableHttpResponse) mediasHost.getHttpClient().execute(itemHttpPost);
            if (response.getCode() == HttpStatus.SC_OK) {
                final String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                if (!responseString.contains("error")) {
                    final KodiItemResponse kodiResponse = KodiRequestHelper.build(responseString, KodiItemResponse.class);
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

    public Map<String, MediasHost> getMediaHostMap() {
        return mediaHostMap;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    protected static class MediasHost {
        private final String host;
        private final HttpClient httpClient;
        private final List<RemoteMedia> mediaList;
        private URI uri;

        private MediasHost(RemoteMedia media) throws URISyntaxException {
            if (media == null)
                throw new IllegalArgumentException("Parameter media can not be NULL");
            this.host = media.getHost();
            this.httpClient = HttpClients.createDefault();
            this.mediaList = new ArrayList<>();
            uri = new URIBuilder().setScheme("http").setHost(host).setPort(media.getPort()).setPath("/jsonrpc").build();
        }

        public HttpClient getHttpClient() {
            return httpClient;
        }

        public List<RemoteMedia> getMediaList() {
            return mediaList;
        }

        public URI getUri() {
            return uri;
        }
    }
}
