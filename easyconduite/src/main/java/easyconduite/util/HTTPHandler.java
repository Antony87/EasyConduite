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

import easyconduite.tools.kodi.KodiManager;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class HTTPHandler {

    private static final Logger LOG = LogManager.getLogger(HTTPHandler.class);

    private HTTPHandler() {
    }


    public static String getResponse(HttpClient httpClient, URI hostUri, String jsonRequest) throws IOException {
        LOG.trace("Enter in with hostUri {} and jsonRequest {}",hostUri,jsonRequest);
        final HttpPost httpPost = new HttpPost(hostUri);
        final StringEntity entity = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        String responseString = "";
            response = (CloseableHttpResponse) httpClient.execute(httpPost);
            if(response.getCode() == HttpStatus.SC_OK){
                responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            }else{
                LOG.error("Error occured with Http Code {}",response.getCode());
            }
            LOG.trace("Response string : {}",responseString);
        return responseString;
    }
}
