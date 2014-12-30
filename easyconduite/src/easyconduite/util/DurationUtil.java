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

import java.time.Duration;
import java.time.format.DateTimeParseException;

/**
 *
 * @author antony fons
 */
public class DurationUtil {

    public static boolean isParsableToDuration(String textField) {

        String[] values = textField.split(":");

        if (null != values && values.length == 2) {
            StringBuilder stb = new StringBuilder();
            stb.append("PT");
            stb.append(values[0]);
            stb.append("H");
            stb.append(values[1]);
            stb.append("M");

            try {
                Duration.parse(stb);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }

        }

        return false;
    }

    public static Duration parseFromConduite(String textField) {

        if (isParsableToDuration(textField)) {
            String[] values = textField.split(":");
            StringBuilder stb = new StringBuilder();
            stb.append("PT");
            stb.append(values[0]);
            stb.append("H");
            stb.append(values[1]);
            stb.append("M");
            return Duration.parse(stb);
        }
        return null;

    }

    public static String toStringForConduite(Duration duree) {

        if (null != duree) {
            StringBuilder stb = new StringBuilder();
            stb.append(duree.toHours());
            stb.append(":");
            stb.append(duree.minusHours(duree.toHours()).toMinutes());

            return stb.toString();
        }

        return null;

    }

}
