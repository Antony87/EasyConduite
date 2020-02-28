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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import easyconduite.tools.jackson.MapperSingleton;

import java.io.IOException;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KodiResponse {

    public String jsonrpc;
    public String id;
    @JsonProperty("result")
    public List<ActivePlayer> result;

    public KodiResponse() {
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ActivePlayer> getResult() {
        return result;
    }

    public void setResult(List<ActivePlayer> result) {
        this.result = result;
    }

    @JsonIgnore
    public static KodiResponse build(String json){
        final ObjectMapper mapperJson = MapperSingleton.getMapper().getMapperJson();
        KodiResponse kodiResponse = null;
        try {
                kodiResponse = mapperJson.readValue(json,KodiResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kodiResponse;
    }
}
