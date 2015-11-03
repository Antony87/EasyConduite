/*
 * Copyright (C) 2014 antony
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

import javafx.scene.input.KeyCode;

/**
 *
 * @author antony
 */
public class KeyCodeUtil {

    public static String toString(final KeyCode code) {
        if (isValid(code)) {

            String sCode = code.getName();
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

    ;
    
    public static boolean isValid(final KeyCode code) {

        if (code == KeyCode.UNDEFINED || code == null) {
            return false;
        } else {
            return true;
        }
    }

}
