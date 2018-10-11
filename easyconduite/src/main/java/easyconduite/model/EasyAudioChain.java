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
package easyconduite.model;

import easyconduite.objects.AudioMedia;

/**
 * Chain of responsability precises configuration chain along controllers, UI,
 * players ; affected to AudioMedia.
 *
 * @author antony
 */
public interface EasyAudioChain {

    /**
     * Set next element concerned to AudioMedia.
     *
     * @param next
     */
    public void setNext(EasyAudioChain next);

    /**
     * Element's configuration method uses AudioMedia.
     *
     * @param audioMedia
     */
    public void updateFromAudioMedia(AudioMedia audioMedia);

    /**
     * Method to remove childs and dependencies along AudioMedia chain of
     * responsability.
     * @param audioMedia
     */
    public void removeChild(AudioMedia audioMedia);

}
