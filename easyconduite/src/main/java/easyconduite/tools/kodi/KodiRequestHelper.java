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
import easyconduite.exception.EasyconduiteException;
import easyconduite.tools.jackson.MapperSingleton;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.PersistenceHelper;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public class KodiRequestHelper {

    public static String getOpenRequest(File file){
        String json = "";
        final Properties kodiProps;
        try {
            kodiProps = EasyConduitePropertiesHandler.getInstance().getKodiProperties();
            String pattern = kodiProps.getProperty(KodiMethods.OPEN.getMethod());
            json = MessageFormat.format(pattern, PersistenceHelper.separatorsToSystem(file.getCanonicalPath()));
        } catch (EasyconduiteException | IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getGetItemRequest(Integer playerId){
        String json = "";
        final Properties kodiProps;
        try {
            kodiProps = EasyConduitePropertiesHandler.getInstance().getKodiProperties();
            String pattern = kodiProps.getProperty(KodiMethods.GET_ITEM.getMethod());
            json = MessageFormat.format(pattern, playerId);
        } catch (EasyconduiteException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getGetStopRequest(Integer playerId){
        String json = "";
        final Properties kodiProps;
        try {
            kodiProps = EasyConduitePropertiesHandler.getInstance().getKodiProperties();
            String pattern = kodiProps.getProperty(KodiMethods.STOP.getMethod());
            json = MessageFormat.format(pattern, playerId);
        } catch (EasyconduiteException e) {
            e.printStackTrace();
        }
        return json;
    }


    public static String getMethodRequest(KodiMethods method){
        String json = "";
        final Properties kodiProps;
        try {
            kodiProps = EasyConduitePropertiesHandler.getInstance().getKodiProperties();
            String pattern = kodiProps.getProperty(method.getMethod());
            json = MessageFormat.format(pattern, "");
        } catch (EasyconduiteException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static <T> T build(String json, Class classe){
        final ObjectMapper mapperJson = MapperSingleton.getMapper().getMapperJson();
        T kodiResponse = null;
        try {
            kodiResponse = (T) mapperJson.readValue(json,classe);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kodiResponse;
    }
}
