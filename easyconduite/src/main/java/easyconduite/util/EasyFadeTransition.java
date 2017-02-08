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
package easyconduite.util;

import javafx.animation.Transition;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author antony
 */
public class EasyFadeTransition extends Transition {

    public static enum Way {

        IN, OUT;
    }
    
    private final double startVolume;

    private final MediaPlayer player;

    public EasyFadeTransition(MediaPlayer player, Duration fadeDuration, Way sens) {
        super();
        this.player = player;
        setCycleDuration(fadeDuration);
        if(sens.equals(Way.IN)){
            this.startVolume=0.0d;
        }else{
          this.startVolume=this.player.getVolume();  
        }
    }

    public double getStartVolume() {
        return startVolume;
    }
    
    

    @Override
    protected void interpolate(double frac) {
        this.player.setVolume(frac);
    }
}
