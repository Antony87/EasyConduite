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

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class KodiHost {

    private HttpPost activeHttpPost;
    private HttpPost stopHttpPost;
    private String host;
    private int port;
    private final List<KodiMedia> kodiMedialist;

    public KodiHost(String host, int port) {
        kodiMedialist = new CopyOnWriteArrayList<>();
        this.host = host;
        this.port = port;
        try {
            final URI hostUri = new URIBuilder().setScheme("http").setHost(this.host).setPort(this.port).setPath("/jsonrpc").build();
            activeHttpPost = new HttpPost(hostUri);
            final String jsonRequest = KodiRequest.getJsonRequest(KodiRequest.ACTIVE, null);
            final StringEntity entity = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);
            activeHttpPost.setEntity(entity);

            stopHttpPost = new HttpPost(hostUri);
            final String jsonStop = KodiRequest.getJsonRequest(KodiRequest.STOP, "1");
            final StringEntity stopEntity = new StringEntity(jsonStop, ContentType.APPLICATION_JSON);
            stopHttpPost.setEntity(stopEntity);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    public HttpPost getActiveHttpPost() {
        return activeHttpPost;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public List<KodiMedia> getKodiMedialist() {
        return kodiMedialist;
    }

    public HttpPost getStopHttpPost() {
        return stopHttpPost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KodiHost)) return false;
        KodiHost kodiHost = (KodiHost) o;
        return port == kodiHost.port &&
                host.equals(kodiHost.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return "KodiHost{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", kodiMedialist=" + kodiMedialist +
                '}';
    }
}
