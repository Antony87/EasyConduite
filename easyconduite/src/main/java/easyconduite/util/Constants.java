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

import javafx.scene.input.DataFormat;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
    
    public static String formatTime(Duration duration) {
        if (duration.greaterThan(Duration.ZERO)) {
            final double millis = duration.toMillis();
            final int dec = (int) ((millis / 100) % 10);
            final int seconds = (int) ((millis / 1000) % 60);
            final int minutes = (int) (millis / (1000 * 60));
            return String.format("%02d:%02d:%02d", minutes, seconds, dec);
        }
        return null;
    }
    
    public final static DataFormat DATA_FORMAT_INTEGER = new DataFormat("java.lang.Integer");
    
    public final static DataFormat DATA_FORMAT_UUID = new DataFormat("java.util.UUID");
    
    public final static DataFormat DATA_FORMAT_AUDIOMEDIA = new DataFormat("easyconduite.objects.AudioMedia");

}
