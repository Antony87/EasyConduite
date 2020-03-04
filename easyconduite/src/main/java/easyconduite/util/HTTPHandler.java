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

package easyconduite.util;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class HTTPHandler {

    private static  HTTPHandler INSTANCE;

    private final CloseableHttpClient httpclient;

    private HTTPHandler() {
        httpclient = HttpClients.createDefault();
    }

    public static HTTPHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (HTTPHandler.class) {
                INSTANCE = new HTTPHandler();
            }
        }
        return INSTANCE;
    }

    public static String getResponse(CloseableHttpResponse response) throws IOException {
        return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
    }

    public static  HttpPost getHttPost(String request, URI hostUri) {
        final HttpPost httpPost = new HttpPost(hostUri);
        final StringEntity entity = new StringEntity(request, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        return httpPost;
    }

    public CloseableHttpClient getHttpclient() {
        return httpclient;
    }
}
