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

package easyconduite.tools;

import easyconduite.media.RemoteMedia;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientForMedias {

    private final HttpClient httpClient;
    private final List<RemoteMedia> mediaList;
    private URI uri;

    public HttpClientForMedias(RemoteMedia media) throws URISyntaxException {
        if (media == null)
            throw new IllegalArgumentException("Parameter media can not be NULL");
        final String host = media.getHost();
        this.httpClient = HttpClients.createDefault();
        this.mediaList = new ArrayList<>();
        if(media.getType().equals(RemoteMedia.Type.KODI)){
            uri = new URIBuilder().setScheme("http").setHost(host).setPort(media.getPort()).setPath("/jsonrpc").build();
        }
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
