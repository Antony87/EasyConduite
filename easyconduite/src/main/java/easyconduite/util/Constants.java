/*
 * Copyright (C) 2014 antony fons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package easyconduite.util;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javafx.scene.input.DataFormat;
import javafx.util.Duration;

/**
 * This class contains severals application's constants
 *
 * @author antony fons
 */
public class Constants {

    
    public static final TimeZone TZ = TimeZone.getTimeZone("UTC");

    /**
     * Constant Path for user datas.
     */
    public static final File FILE_EASYCONDUITE_PROPS = Paths.get("easyconduite.dat").toFile();

    /**
     * Constant for resource bundle basename.
     */
    public static final String RESOURCE_BASENAME = "locale.LabelsBundle";

    /**
     * Format a Duration with SimpleDateFormat.
     *
     * @param duration
     * @param formater
     * @return
     */
    public static String getFormatedDuration(Duration duration, SimpleDateFormat formater) {
        return formater.format(new Date((long) duration.toMillis()));
    }
    
    public final static DataFormat DATA_FORMAT_INTEGER = new DataFormat("java.lang.Integer");

}
