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

import easyconduite.exception.EasyconduiteException;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.Properties;

class EasyConduitePropertiesHandlerTest {

    @Test
    void getKodiProperties() throws EasyconduiteException {
        Properties appProps = EasyConduitePropertiesHandler.getInstance().getKodiProperties();
        System.out.println(appProps.getProperty("kodi.open"));

        String open = appProps.getProperty("Player.Open");

        System.out.println(MessageFormat.format(open,"toto"));

        String ping = appProps.getProperty("JSONRPC.Ping");
        System.out.println(MessageFormat.format(ping,""));
    }
}