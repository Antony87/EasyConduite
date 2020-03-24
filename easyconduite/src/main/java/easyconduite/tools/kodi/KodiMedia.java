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
import easyconduite.util.PersistenceHelper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class KodiMedia {

    private HttpPost openHttpPost;
    private RemoteMedia remoteMedia;
    private static final Logger LOG = LogManager.getLogger(KodiMedia.class);

    public KodiMedia(RemoteMedia remoteMedia) throws RemotePlayableException {
        LOG.trace("Constructor called with RemoteMedia {}", remoteMedia);
        this.remoteMedia = remoteMedia;

        try {
            final URI hostUri = new URIBuilder().setScheme("http").setHost(remoteMedia.getHost()).setPort(remoteMedia.getPort()).setPath("/jsonrpc").build();
            LOG.trace("Host URI generated : {}", hostUri);
            openHttpPost = new HttpPost(hostUri);
            final String fileString = PersistenceHelper.separatorsToSystem(new File(remoteMedia.getResource()).getCanonicalPath());
            final String jsonRequest = KodiRequest.getJsonRequest(KodiRequest.OPEN, fileString);
            final StringEntity entity = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);
            openHttpPost.setEntity(entity);
            LOG.trace("openHttpPost URI generated : {}", new String(openHttpPost.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8));




        } catch (IOException | URISyntaxException e) {
            LOG.error("Error occured during KodiMedia constructor with RemoteMedia {}",remoteMedia,e);
            throw new RemotePlayableException();
        }
    }

    public RemoteMedia getRemoteMedia() {
        return remoteMedia;
    }

    public void setRemoteMedia(RemoteMedia remoteMedia) {
        this.remoteMedia = remoteMedia;
    }

    public HttpPost getOpenHttpPost() {
        return openHttpPost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KodiMedia)) return false;
        KodiMedia kodiMedia = (KodiMedia) o;
        return remoteMedia.equals(kodiMedia.remoteMedia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remoteMedia);
    }

    @Override
    public String toString() {
        return "KodiMedia{" +
                "remoteMedia=" + remoteMedia +
                '}';
    }
}
