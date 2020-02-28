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

import easyconduite.exception.EasyconduiteException;
import easyconduite.util.EasyConduitePropertiesHandler;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public class KodiRequestBuilder {

    private String jsonrpc = "2.0";
    private String id = "1";
    private KodiMethods method;
    private String filePath;

    public KodiRequestBuilder(KodiMethods method) {
        this.method = method;
    }

    public KodiRequestBuilder setFile(File file) {
        try {
            this.filePath = separatorsToSystem(file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    private String separatorsToSystem(String res) {
        if (res==null) return null;
        if (File.separatorChar=='\\') {
            // From Windows to Linux/Mac
            return res.replace('\\', '/');
        }
        return res;
    }


    public String build() {

        String json = "";

        try {
            final Properties kodiProps = EasyConduitePropertiesHandler.getInstance().getKodiProperties();
            String pattern = kodiProps.getProperty(method.getMethod());
            switch (method) {
                case OPEN:
                    json = MessageFormat.format(pattern, filePath);
                    break;
                default:
                    json = MessageFormat.format(pattern, "");
            }

        } catch (EasyconduiteException e) {
            e.printStackTrace();
        }
        return json;
    }

}
