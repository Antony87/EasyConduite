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
import easyconduite.util.EasyConduitePropertiesHandler;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final Map<String, KodiHost> mapKodiHosts;

    private KodiManager() {
        LOG.trace("Construct KodiManager singleton");
        httpClient = HttpClients.createDefault();
        clientActionsQueue = new ConcurrentLinkedQueue<>();
        mapKodiHosts = new ConcurrentHashMap<>();
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


    public void registerKodiMedia(RemoteMedia media) throws RemotePlayableException {
        LOG.debug("registerKodiMedia with RemoteMedia {}", media);
        final String hostKey = media.getHost() + ":" + media.getPort();
        final KodiMedia kodiMedia = new KodiMedia(media);
        if (mapKodiHosts.containsKey(hostKey)) {
            // si le host est déja dans la mapKodiHosts
            final List<KodiMedia> kodiMediaList = mapKodiHosts.get(hostKey).getKodiMedialist();
            if (!kodiMediaList.contains(kodiMedia)) {
                kodiMediaList.add(kodiMedia);
            }
        } else {
            // si le host n'est pas connu de la mapKodiHosts
            final KodiHost kodiHost = new KodiHost(media.getHost(), media.getPort());
            kodiHost.getKodiMedialist().add(kodiMedia);
            mapKodiHosts.putIfAbsent(hostKey, kodiHost);
        }
        LOG.debug("mapKodiHosts {}", mapKodiHosts);
    }

    public void unRegisterKodiMedia(RemoteMedia media) {
        final KodiHost kodiHost = getKodiHost(media);
        if (kodiHost != null) {
            final List<KodiMedia> kodiMediaList = kodiHost.getKodiMedialist();
            if (kodiMediaList.isEmpty()) {
                final String hostKey = media.getHost() + ":" + media.getPort();
                mapKodiHosts.remove(hostKey, kodiHost);
            } else {
                kodiMediaList.forEach(kodiMedia -> {
                    if (kodiMedia.getRemoteMedia().equals(media)) kodiMediaList.remove(kodiMedia);
                });
            }
        }
    }

    private KodiHost getKodiHost(RemoteMedia media) {
        return mapKodiHosts.get(media.getHost() + ":" + media.getPort());
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

    public Integer getActivePlayer(KodiHost kodiHost) {
        Integer playerId = null;
        boolean activeHost=false;
        try {
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(kodiHost.getActiveHttpPost());
            String responseString;
            if (response.getCode() == HttpStatus.SC_OK) {
                responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                final KodiActiveResponse kodiActiveResponse = KodiRequest.buildResponse(responseString, KodiActiveResponse.class);
                final List<KodiActiveResponse.KodiActivePlayer> result = kodiActiveResponse.getResult();
                if (!result.isEmpty()) {
                    playerId = kodiActiveResponse.getResult().get(0).getPlayerid();
                    activeHost=true;
                }
            } else {
                LOG.error("Error occured with Http Code {}", response.getCode());
                activeHost=false;
            }
            LOG.trace("PlayerId is {} for KodiHost {}", playerId, kodiHost);
        } catch (IOException e) {
            LOG.trace("Exception within getActivePlayer with host {} Media is deactivated", kodiHost, e.getMessage());
            activeHost=false;
        }
        for (KodiMedia media : kodiHost.getKodiMedialist()) {
            media.getRemoteMedia().setActiveHost(activeHost);
        }
        return playerId;
    }

    public boolean stopKodiMedia(RemoteMedia media) {
        LOG.trace("stopKodiMedia called with {}", media);
        // si le media est déja stoppé, on considère l'action finie.
        if (media.getStatus() == MediaStatus.STOPPED) return true;
        final KodiHost kodiHost = getKodiHost(media);
        // si il n'y a pas de player actif, on considère le média stoppé
        final Integer activePlayer = getActivePlayer(kodiHost);
        if (activePlayer == null) return true;
        String responseString;
        try {
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(kodiHost.getStopHttpPost());
            if (response.getCode() == HttpStatus.SC_OK) {
                responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                LOG.trace("Reponse POST : {}", responseString);
                if (responseString.contains(RESULT) && responseString.contains(OK)) return true;
            }
        } catch (IOException e) {
            LOG.error("Error occured whith kodiHost {} and RemoteMedia {} ", kodiHost, media, e);
        }
        return false;
    }

    public Map<String, KodiHost> getMapKodiHosts() {
        return mapKodiHosts;
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
                    for (Map.Entry<String, KodiHost> entryHost : mapKodiHosts.entrySet()) {
                        final KodiHost kodiHost = entryHost.getValue();
                        final Integer activePlayer = getActivePlayer(kodiHost);
                        if (activePlayer == null) {
                            final List<KodiMedia> kodiMediaList = kodiHost.getKodiMedialist();
                            kodiMediaList.forEach(kodiMedia -> {
                                final RemoteMedia remoteMedia = kodiMedia.getRemoteMedia();
                                remoteMedia.setStatus(MediaStatus.STOPPED);
                            });
                        }
                    }

                    RemoteMedia media;
                    do {
                        media = clientActionsQueue.poll();
                        if (media != null) {
                            try {
                                executeAction(media);
                            } catch (RemotePlayableException e) {
                                e.printStackTrace();
                            }
                        }
                    } while (media != null);
                    return null;
                }
            };
        }

        private void updateStatusOtherMedias(RemoteMedia remoteMedia, MediaStatus othersStatus) {
            final KodiHost kodiHost = getKodiHost(remoteMedia);
            final List<KodiMedia> kodiMediaList = kodiHost.getKodiMedialist();
            kodiMediaList.forEach(kodiMedia -> {
                if (!kodiMedia.getRemoteMedia().equals(remoteMedia)) {
                    kodiMedia.getRemoteMedia().statusProperty().setValue(othersStatus);
                    LOG.trace("other media {} set to {}", kodiMedia, othersStatus);
                }
            });
        }

        private boolean openKodiMedia(RemoteMedia media) throws RemotePlayableException {
            LOG.debug("openKodiMedia called with RemoteMedia {}", media);
            final List<KodiMedia> kodiMedialist = getKodiHost(media).getKodiMedialist();
            final Optional<KodiMedia> optionnel = kodiMedialist.stream().filter(m -> m.getRemoteMedia().equals(media)).findFirst();
            if (optionnel.isPresent()) {
                try {
                    CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(optionnel.get().getOpenHttpPost());
                    String responseString;
                    if (response.getCode() == HttpStatus.SC_OK) {
                        responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                        if (responseString.contains(RESULT) && responseString.contains(OK)) return true;
                    } else {
                        LOG.error("Error occured with Http Code {}", response.getCode());
                    }
                } catch (IOException e) {
                    LOG.error("Error occured during openKodiMedia");
                    throw new RemotePlayableException(e.getMessage());
                }
            }
            return false;
        }

        private void executeAction(RemoteMedia media) throws RemotePlayableException {
            RemoteMedia.Action action = media.getAction();
            LOG.trace("Action called on media {}", media.getAction());
            switch (action) {
                case PLAY:
                    if (openKodiMedia(media)) {
                        media.statusProperty().setValue(MediaStatus.PLAYING);
                        updateStatusOtherMedias(media, MediaStatus.STOPPED);
                        media.setAction(null);
                    }
                    break;
                case STOP:
                    if (stopKodiMedia(media)) {
                        media.setStatus(MediaStatus.STOPPED);
                        media.setAction(null);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
