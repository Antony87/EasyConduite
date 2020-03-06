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
import easyconduite.tools.HttpMedias;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.HTTPHandler;
import easyconduite.util.PersistenceHelper;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class KodiManager implements RemotePlayable {

    private static final Logger LOG = LogManager.getLogger(KodiManager.class);
    private static final String RESULT = "\"result\"";
    private static final String OK = "\"OK\"";
    private static KodiManager instance;
    private final HttpClient httpClient;
    private final Queue<RemoteMedia> clientActionsQueue;
    private final Map<String, HttpMedias> mediaHostMap;

    private KodiManager() {
        LOG.trace("Construct KodiManager singleton");
        httpClient = HttpClients.createDefault();
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

    public void registerKodiMedia(RemoteMedia media) throws RemotePlayableException {
        final String host = media.getHost();
        if (!mediaHostMap.containsKey(host)) {
            final HttpMedias httpMedias = new HttpMedias();
            httpMedias.addToMediaList(media);
            mediaHostMap.put(host, httpMedias);
            LOG.debug("kodiMedia created to registred list : {}", media);
        } else {
            final HttpMedias mediaHost = mediaHostMap.get(host);
            List<RemoteMedia> mediasList = mediaHost.getMediaList();
            if (mediasList.contains(media)) {
                LOG.debug("This media {} is already registrered", media);
                throw new RemotePlayableException("This media is not registrered");
            }
            mediasList.add(media);
            LOG.debug("kodiMedia added to registred list : {}", media);
        }
    }

    public void unRegisterKodiMedia(RemoteMedia media) throws RemotePlayableException {
        final String host = media.getHost();
        if (!mediaHostMap.containsKey(host)) {
            LOG.debug("This host {} is not registrered", host);
            throw new IllegalArgumentException("This host is not registrered");
        } else {
            final HttpMedias httpMedias = mediaHostMap.get(host);
            final List<RemoteMedia> mediaList = httpMedias.getMediaList();
            if (!mediaList.contains(media)) {
                LOG.debug("This media {} is not registrered", media);
                throw new RemotePlayableException("This media is not registrered");
            }
            mediaList.remove(media);
            if (mediaList.isEmpty()) mediaHostMap.remove(host);
        }
    }

    private boolean openKodiMedia(RemoteMedia media) {
        final String host = media.getHost();
        final HttpMedias httpMedias = mediaHostMap.get(host);
        try {
            final String fileString = PersistenceHelper.separatorsToSystem(new File(media.getResource()).getCanonicalPath());
            final String jsonRequest = KodiRequest.getJsonRequest(KodiRequest.OPEN, fileString);
            final String responseString = HTTPHandler.getResponse(httpClient, httpMedias.getUri(), jsonRequest);
            if (responseString.contains(RESULT) && responseString.contains(OK)) {
                return true;
            }
        } catch (IOException e) {
            LOG.error("This media {} can not be opened", media);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void stop(RemoteMedia media) {
        media.setAction(RemoteMedia.Action.STOP);
        clientActionsQueue.offer(media);
    }

    public Integer getActivePlayer(String hostAdr) {
        Integer playerId = null;
        try {
            final HttpMedias httpMedias = mediaHostMap.get(hostAdr);
            final String jsonRequest = KodiRequest.getJsonRequest(KodiRequest.ACTIVE, null);
            final String responseString = HTTPHandler.getResponse(httpClient, httpMedias.getUri(), jsonRequest);
            final KodiActiveResponse kodiActiveResponse = KodiRequest.buildResponse(responseString, KodiActiveResponse.class);
            final List<KodiActiveResponse.KodiActivePlayer> result = kodiActiveResponse.getResult();
            if (!result.isEmpty()) {
                playerId = kodiActiveResponse.getResult().get(0).getPlayerid();
            }
        } catch (IOException e) {
            LOG.error("Exception within getActivePlayer with host {}", hostAdr, e);
        }
        return playerId;
    }

    public KodiItemResponse.KodiItem getItem(String hostAdr, Integer playerId) throws RemotePlayableException {
        KodiItemResponse.KodiItem kodiItem = null;
        try {
            if (playerId == null) throw new RemotePlayableException("playerId is NULL");
            final HttpMedias httpMedias = mediaHostMap.get(hostAdr);
            final String jsonRequest = KodiRequest.getJsonRequest(KodiRequest.ITEM, playerId.toString());
            final String responseString = HTTPHandler.getResponse(httpClient, httpMedias.getUri(), jsonRequest);
            if (!responseString.contains("error")) {
                final KodiItemResponse kodiResponse = KodiRequest.buildResponse(responseString, KodiItemResponse.class);
                kodiItem = kodiResponse.getResult().getItem();
            }
        } catch (IOException e) {
            LOG.error("Exception within getItem with host {} and playerId {}", hostAdr, playerId, e);
            throw new RemotePlayableException();
        }
        return kodiItem;
    }

    public boolean stopKodiMedia(RemoteMedia media) {
        // si le media est déja stoppé, on considère l'action finie.
        if (media.getStatus() == MediaStatus.STOPPED) return true;
        final String host = media.getHost();
        // si il n'y a pas de player actif, on considère le média stoppé
        final Integer activePlayer = getActivePlayer(host);
        if (activePlayer == null) return true;
        try {
            final HttpMedias httpMedias = mediaHostMap.get(host);
            final String jsonRequest = KodiRequest.getJsonRequest(KodiRequest.STOP, activePlayer.toString());
            final String responseString = HTTPHandler.getResponse(httpClient, httpMedias.getUri(), jsonRequest);
            if (responseString.contains(RESULT) && responseString.contains(OK)) {
                return true;
            }
        } catch (IOException e) {
            LOG.error("Error occured whith host {} and RemoteMedia {} ", host, media, e);
        }
        return false;
    }

    public Map<String, HttpMedias> getMediaHostMap() {
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
                    for (Map.Entry<String, HttpMedias> mediaHost : mediaHostMap.entrySet()) {
                        LOG.trace("Task listener sur MediaHost {}",mediaHost.getKey());
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
            LOG.trace("Action called on media {}", media.getAction());
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
