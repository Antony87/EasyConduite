/*
 * Copyright (C) 2015 Antony Fons
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
package easyconduite.model;

import easyconduite.objects.Media;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author V902832
 */
public abstract class AbstractPlayer {
    
    private Media media;
    
    private final BooleanProperty repeat = new SimpleBooleanProperty(false);
    
    public abstract void play();
    
    public abstract void pause();
    
    public abstract void stop();
    

    public boolean isRepeat() {
        return repeat.get();
    }

    public void setRepeat(boolean value) {
        repeat.set(value);
    }

    public BooleanProperty repeatProperty() {
        return repeat;
    }
       
    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }
    
    
    
}
