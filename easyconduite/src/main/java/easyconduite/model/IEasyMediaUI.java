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
 * Interface qui défini le comportement de l'UI d'un Média.
 */
public interface IEasyMediaUI {

    /**
     * Method which updates the UI fields according to the values ​of the EasyMedia object.
     * See {@link EasyMedia}.
     */
    void actualizeUI();

    /**
     * Method which return the EasyMedia object.
     * @return Object EasyMedia.
     */
    EasyMedia getEasyMedia();

    /**
     * Method that changes the status play or pause of a player.
     */
    void playPause();

    void stop();

    boolean isSelected();

    void setSelected(boolean selected);
}
