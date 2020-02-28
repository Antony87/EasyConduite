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

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivePlayer {
    @JsonProperty("playerid")
    int playerid;
    @JsonProperty("playertype")
    String playertype;
    @JsonProperty("type")
    String type;

    public ActivePlayer() {
    }

    public int getPlayerid() {
        return playerid;
    }

    public void setPlayerid(int playerid) {
        this.playerid = playerid;
    }

    public String getPlayertype() {
        return playertype;
    }

    public void setPlayertype(String playertype) {
        this.playertype = playertype;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ActivePlayer{" +
                "playerid=" + playerid +
                ", playertype='" + playertype + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
