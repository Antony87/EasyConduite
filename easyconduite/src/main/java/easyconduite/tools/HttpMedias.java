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
import org.apache.hc.core5.net.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HttpMedias {

    private static final Logger LOG = LogManager.getLogger(HttpMedias.class);
    private List<RemoteMedia> mediaList;
    private URI uri;

    public HttpMedias()  {
        mediaList = new ArrayList<>();
//        if (media == null){
//            LOG.error("RemoteMedia can not be NULL");
//            throw new IllegalArgumentException("Parameter media can not be NULL");
//        }
//        final String host = media.getHost();
//        if (host == null || host.isEmpty()) {
//            LOG.error("Host can not be NULL within {}",media);
//            throw new IllegalArgumentException("Parameter host can not be NULL");
//        }
//        final URI resource = media.getResource();
//        if (resource == null || resource.toString().isEmpty()) {
//            LOG.error("URI Resource can not be NULL within {}",media);
//            throw new IllegalArgumentException("Parameter host can not be NULL");
//        }
//        this.mediaList = new ArrayList<>();
//        if(media.getType().equals(RemoteMedia.Type.KODI)){
//            try {
//
//            } catch (URISyntaxException e) {
//                throw new IllegalArgumentException("Error during URI Builder");
//            }
//        }
    }

    public List<RemoteMedia> getMediaList() {
        return mediaList;
    }

    public void addToMediaList(RemoteMedia media){
        try {
            uri = new URIBuilder().setScheme("http").setHost(media.getHost()).setPort(media.getPort()).setPath("/jsonrpc").build();
            mediaList.add(media);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public URI getUri() {
        return uri;
    }
}
