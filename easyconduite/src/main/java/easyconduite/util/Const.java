/*
 * Copyright (C) 2014 antony fons
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

import easyconduite.ui.AudioMediaUI;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 *
 * @author antony fons
 */
public class Const {

    public enum Fade {

        IN, OUT;
    }

    public static final Background PLAY_BACKG = new Background(new BackgroundFill(Color.web("#455473"), CornerRadii.EMPTY, Insets.EMPTY));

    public static final Background STOP_BACKG = new Background(new BackgroundFill(Color.web("#535965"), CornerRadii.EMPTY, Insets.EMPTY));

    public static final Image REPEAT_IMAGE = new Image(AudioMediaUI.class.getResourceAsStream("/icons/repeat.png"), 18, 18, true, false);

    public static final String NAME_ICON_PLAY = "/icons/PlayGreenButton.png";

    public static final String NAME_ICON_PAUSE = "/icons/PauseBlueButton.png";

    public static final Effect SHADOW_EFFECT = new DropShadow(5d, 2d, 2d, Color.BLACK);

    public static final Effect KEYCODE_LABEL_BLOOM = new Bloom(0.4);

    public static Transition createFade(Fade fade, Duration duration) {

        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.millis(duration.toMillis()));
            }
            @Override
            protected void interpolate(double frac) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        return null;

    }

}
