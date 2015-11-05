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
package easyconduite.objects;

import easyconduite.model.AbstractPlayer;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

/**
 *
 * @author V902832
 */
public class SoundPlayer extends AbstractPlayer{
    
    private final FloatProperty volume = new SimpleFloatProperty();
    
    private Integer fadeInDelay;
    
    private Integer fadeOutDelay;
    
    

    public float getVolume() {
        return volume.get();
    }

    public void setVolume(float value) {
        volume.set(value);
    }

    public FloatProperty volumeProperty() {
        return volume;
    }

    public Integer getFadeInDelay() {
        return fadeInDelay;
    }

    public void setFadeInDelay(Integer fadeInDelay) {
        this.fadeInDelay = fadeInDelay;
    }

    public Integer getFadeOutDelay() {
        return fadeOutDelay;
    }

    public void setFadeOutDelay(Integer fadeOutDelay) {
        this.fadeOutDelay = fadeOutDelay;
    }
    
    @Override
    public void play() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
