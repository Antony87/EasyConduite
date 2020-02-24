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

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HTTPHandler {

    private static  HTTPHandler INSTANCE;

    private final CloseableHttpClient httpclient;

    private HTTPHandler() {
        httpclient = HttpClients.createDefault();
    }

    public static HTTPHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (EasyConduitePropertiesHandler.class) {
                INSTANCE = new HTTPHandler();
            }
        }
        return INSTANCE;
    }

    public CloseableHttpClient getHttpclient() {
        return httpclient;
    }
}
