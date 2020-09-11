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

package easyconduite.model;

/**
 * This interface defines the behavior of the UI of a media.
 * <p>{@link AbstractUIMedia}</p>
 */
public interface UIMediaPlayable {

    /**
     * Method which return the AbstractMedia object.
     * @return Object AbstractMedia.
     */
    MediaPlayable getMediaPlayable();

    /**
     * Method that changes the status play or pause of a player.
     */
    void playPause();

    void stop();

    void setSelected(boolean b);

    boolean isSelected();

    void actualizeUI();

}
