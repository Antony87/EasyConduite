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
import easyconduite.util.PersistenceHelper;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
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

public class KodiPlayer implements RemotePlayable {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APP_JSON = "application/json";
    private static final int HTTP_OK = 200;
    private static final Logger LOG = LogManager.getLogger(KodiPlayer.class);
    private final HttpClient client;
    private final RemoteMedia remoteMedia;
    private HttpRequest playRequest;
    private HttpRequest stopRequest;
    private HttpRequest activeRequest;

    public KodiPlayer(RemoteMedia media) {
        LOG.debug("Constructor called with RemoteMedia {}", media);
        remoteMedia = media;
        client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
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

        playRequest = HttpRequest.newBuilder().uri(hostUri).POST(HttpRequest.BodyPublishers.ofString(jsonRequestPlay)).header(CONTENT_TYPE, APP_JSON).build();
        stopRequest = HttpRequest.newBuilder().uri(hostUri).POST(HttpRequest.BodyPublishers.ofString(jsonRequestStop)).header(CONTENT_TYPE, APP_JSON).build();
        activeRequest = HttpRequest.newBuilder().uri(hostUri).POST(HttpRequest.BodyPublishers.ofString(jsonRequestActive)).header(CONTENT_TYPE, APP_JSON).build();

        this.remoteMedia.setStatut(MediaStatus.UNKNOWN);

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
                        final MediaStatus status = remoteMedia.getStatut();
                        if (status == MediaStatus.HALTED || status == MediaStatus.UNKNOWN) {
                            remoteMedia.setStatut(MediaStatus.READY);
                        }
                    }
                } else {
                    remoteMedia.setStatut(MediaStatus.HALTED);
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
                remoteMedia.statutProperty().setValue(MediaStatus.PLAYING);
            } else {
                remoteMedia.statutProperty().setValue(MediaStatus.HALTED);
            }
        });
    }

    @Override
    public void pause() {
        client.sendAsync(stopRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(reponse -> {
            if (reponse.statusCode() == HTTP_OK) {
                remoteMedia.statutProperty().setValue(MediaStatus.STOPPED);
            } else {
                remoteMedia.statutProperty().setValue(MediaStatus.HALTED);
            }
        });
    }

    @Override
    public void stop() {
        client.sendAsync(stopRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(reponse -> {
            if (reponse.statusCode() == HTTP_OK) {
                remoteMedia.statutProperty().setValue(MediaStatus.STOPPED);
            } else {
                remoteMedia.statutProperty().setValue(MediaStatus.HALTED);
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
                    return null;
                }
            };
        }
    }
}
