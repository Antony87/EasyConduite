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

import com.fasterxml.jackson.databind.ObjectMapper;
import easyconduite.tools.jackson.MapperSingleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.MessageFormat;

public class KodiRequest {

    private static final Logger LOG = LogManager.getLogger(KodiRequest.class);
    public static final String OPEN ="'{'\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"Player.Open\",\"params\":'{'\"item\":'{'\"file\":\"{0}\"'}}}'";
    public static final String ITEM ="'{'\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"Player.GetItem\",\"params\":'{'\"properties\": [\"title\",\"duration\", \"file\"],\"playerid\":{0}'}}'";
    public static final String ACTIVE ="{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"Player.GetActivePlayers\"}";
    public static final String STOP="'{'\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"Player.Stop\",\"params\":'{'\"playerid\":{0}'}}'";

    public static String getJsonRequest(String... arguments){
        return MessageFormat.format(arguments[0], arguments[1]);
    }

    private KodiRequest() {
    }

    public static <T> T buildResponse(String json, Class classe){
        final ObjectMapper mapperJson = MapperSingleton.getMapper().getMapperJson();
        T kodiResponse = null;
        try {
            kodiResponse = (T) mapperJson.readValue(json,classe);
        } catch (IOException e) {
            LOG.error("Error occured during json readValue of {}",json);
        }
        return kodiResponse;
    }
}
