/*
 * Copyright (C) 2017 Antony Fons
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package easyconduite.tools;

import javafx.scene.input.KeyCode;

/**
 * Tools to manage keyboard KeyCode.
 *
 * @author antony
 */
public class KeyCodeUtil {

    public static String toString(final KeyCode code) {
        if (isValid(code)) {

            final String sCode = code.getName();
            if (sCode.startsWith("Numpad")) {
                return sCode.substring(6);
            } else {
                return code.getName();
            }

        } else {
            return "";
        }
    }

    public KeyCode fromString(String string) {

        KeyCode.getKeyCode(string);

        return KeyCode.getKeyCode(string);
    }

    public static boolean isValid(final KeyCode code) {
        return !(code == KeyCode.UNDEFINED || code == null);
    }

}
