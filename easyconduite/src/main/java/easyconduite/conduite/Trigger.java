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

package easyconduite.conduite;

import java.util.ArrayList;
import java.util.List;

/**
 * this class implements track trigger features.
 */
public class Trigger implements Comparable {

    private int index;

    private final List<MediaAction> mediaActions;

    public Trigger() {
       mediaActions = new ArrayList<>();
    }

    public void playActions() {
        mediaActions.forEach(mediaAction -> mediaAction.switchToStatut());
    }

    @Override
    public int compareTo(Object o) {
        Trigger trigger = (Trigger) o;
        if (index == trigger.index) return 0;
        if (index > trigger.index) return 1;
        return -1;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<MediaAction> getMediaActions() {
        return mediaActions;
    }
}
