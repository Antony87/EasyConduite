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
import easyconduite.media.RemoteMedia;
import easyconduite.model.IRemotePlayer;
import easyconduite.util.PersistenceHelper;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class KodiPlayer implements IRemotePlayer {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APP_JSON = "application/json";
    private static final int HTTP_OK = 200;
    private static final Logger LOG = LogManager.getLogger(KodiPlayer.class);
    private final HttpClient client;
    private final HttpClient client2;
    private final RemoteMedia remoteMedia;
    private String mediaFileName;
    private HttpRequest playRequest;
    private HttpRequest stopRequest;
    private HttpRequest activeRequest;
    private HttpRequest itemRequest;

    public KodiPlayer(RemoteMedia media) {
        LOG.debug("Constructor called with RemoteMedia {}", media);
        remoteMedia = media;
        mediaFileName = remoteMedia.getResource().getFileName().toString();
        client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        client2 = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    }

    public void initializePlayer() throws RemotePlayableException {
        final URI hostUri;
        try {
            hostUri = new URI("http", null, remoteMedia.getHost(), remoteMedia.getPort(), "/jsonrpc", null, null);
        } catch (URISyntaxException e) {
            LOG.error("Error create URI with RemoteMedia {}", remoteMedia);
            throw new RemotePlayableException(e.getMessage());
        }
        final String fileString = PersistenceHelper.separatorsToSystem(this.remoteMedia.getResource().toString());
        final String jsonRequestPlay = KodiRequest.getJsonRequest(KodiRequest.OPEN, fileString);
        final String jsonRequestStop = KodiRequest.getJsonRequest(KodiRequest.STOP, "1");
        final String jsonRequestActive = KodiRequest.getJsonRequest(KodiRequest.ACTIVE, null);
        final String jsonRequestItem = KodiRequest.getJsonRequest(KodiRequest.ITEM, "1");

        playRequest = HttpRequest.newBuilder().uri(hostUri).POST(HttpRequest.BodyPublishers.ofString(jsonRequestPlay)).header(CONTENT_TYPE, APP_JSON).build();
        stopRequest = HttpRequest.newBuilder().uri(hostUri).POST(HttpRequest.BodyPublishers.ofString(jsonRequestStop)).header(CONTENT_TYPE, APP_JSON).build();
        activeRequest = HttpRequest.newBuilder().uri(hostUri).POST(HttpRequest.BodyPublishers.ofString(jsonRequestActive)).header(CONTENT_TYPE, APP_JSON).build();
        itemRequest = HttpRequest.newBuilder().uri(hostUri).POST(HttpRequest.BodyPublishers.ofString(jsonRequestItem)).header(CONTENT_TYPE, APP_JSON).build();
        LOG.trace("item request {}",jsonRequestItem);

        this.remoteMedia.setStatus(MediaPlayer.Status.UNKNOWN);

        final KodiPlayer.ActiveHostObserver hostObserver = new KodiPlayer.ActiveHostObserver();
        hostObserver.setPeriod(Duration.millis(2000));
        hostObserver.start();

    }

    private void setStatusFromHost() {
        try {
            client.sendAsync(activeRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(reponse -> {
                if (reponse.statusCode() == HTTP_OK) {
                    final String responseString = new String(reponse.body().getBytes(), StandardCharsets.UTF_8);
                    final KodiActiveResponse kodiActiveResponse = KodiRequest.buildResponse(responseString, KodiActiveResponse.class);
                    if (kodiActiveResponse.getId() != null) {
                        final MediaPlayer.Status status = remoteMedia.getStatus();
                        if (status == MediaPlayer.Status.HALTED || status == MediaPlayer.Status.UNKNOWN) {
                            remoteMedia.setStatus(MediaPlayer.Status.READY);
                        }
                    }
                } else {
                    remoteMedia.setStatus(MediaPlayer.Status.HALTED);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMediaStatus() {
        try {
            client.sendAsync(itemRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(reponse -> {
                if (reponse.statusCode() == HTTP_OK) {
                    try{
                        final String responseString = new String(reponse.body().getBytes(), StandardCharsets.UTF_8);
                        final KodiItemResponse kodiItemResponse = KodiRequest.buildResponse(responseString, KodiItemResponse.class);
                        final KodiItemResponse.KodiItem kodiItem = kodiItemResponse.getResult().getItem();
                        if (kodiItem.getLabel().equals(this.mediaFileName)) {
                            remoteMedia.setStatus(MediaPlayer.Status.PLAYING);
                        } else {
                            remoteMedia.setStatus(MediaPlayer.Status.READY);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } else {
                    remoteMedia.setStatus(MediaPlayer.Status.HALTED);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RemoteMedia getRemoteMedia() {
        return remoteMedia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KodiPlayer)) return false;
        KodiPlayer kodiPlayer = (KodiPlayer) o;
        return remoteMedia.equals(kodiPlayer.remoteMedia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remoteMedia);
    }

    @Override
    public String toString() {
        return "KodiPlayer{" +
                "remoteMedia=" + remoteMedia +
                '}';
    }

    @Override
    public void play() {
        client.sendAsync(playRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(reponse -> {
            if (reponse.statusCode() == HTTP_OK) {
                remoteMedia.statusProperty().setValue(MediaPlayer.Status.PLAYING);
            } else {
                remoteMedia.statusProperty().setValue(MediaPlayer.Status.HALTED);
            }
        });
    }

    @Override
    public void pause() {
        client.sendAsync(stopRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(reponse -> {
            if (reponse.statusCode() == HTTP_OK) {
                remoteMedia.statusProperty().setValue(MediaPlayer.Status.STOPPED);
            } else {
                remoteMedia.statusProperty().setValue(MediaPlayer.Status.HALTED);
            }
        });
    }

    @Override
    public void stop() {
        client.sendAsync(stopRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(reponse -> {
            if (reponse.statusCode() == HTTP_OK) {
                remoteMedia.statusProperty().setValue(MediaPlayer.Status.STOPPED);
            } else {
                remoteMedia.statusProperty().setValue(MediaPlayer.Status.HALTED);
            }
        });
    }

    private class ActiveHostObserver extends ScheduledService<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
                    setStatusFromHost();
                    setMediaStatus();
                    return null;
                }
            };
        }
    }
}
