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

package easyconduite.view;

import easyconduite.controllers.MainController;
import easyconduite.model.UIMediaPlayable;
import easyconduite.media.AudioMedia;
import easyconduite.media.RemoteMedia;

public class MediaUIFactory {

    public static <T> UIMediaPlayable createMediaUI(T mediaPlayable, MainController controller) {

        if (mediaPlayable instanceof AudioMedia) {
            return new AudioMediaUI((AudioMedia) mediaPlayable, controller);
        } else if (mediaPlayable instanceof RemoteMedia) {
            return new RemoteMediaUI((RemoteMedia) mediaPlayable, controller);
        }
        return null;
    }
}
