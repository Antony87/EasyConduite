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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author antony fons
 */
public class DurationUtil {

    private static final String DURATION_REGEX = "([0-9]?[0-9]{1}):([0-5]{1}[0-9]{1})";

    private static final Pattern DURATION_PATTERN = Pattern.compile(DURATION_REGEX);

    public static boolean isParsableToDuration(String textField) {

        boolean isParsable = false;

        if (null != textField && !textField.isEmpty()) {
            Matcher m = DURATION_PATTERN.matcher(textField);
            isParsable = m.matches();
        }

        return isParsable;
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

            long minutes = duree.toMinutes() - duree.toHours() * 60;
            long heures = duree.toHours();

            String s = String.format("%02d:%02d", heures, minutes);
            return s;
        }

        return null;

    }

}
