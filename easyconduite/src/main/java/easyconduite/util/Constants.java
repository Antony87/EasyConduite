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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
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
 * This class contains severals application's constants
 *
 * @author antony fons
 */
public class Constants {

    /**
     * Background color for AudioMedia UI when playings.
     */
    public static final Background PLAY_BACKG = new Background(new BackgroundFill(Color.web("#455473"), CornerRadii.EMPTY, Insets.EMPTY));

    /**
     * Background color when AudioMedia UI is paused.
     */
    public static final Background PAUSE_BACKG = new Background(new BackgroundFill(Color.web("#4c576c"), CornerRadii.EMPTY, Insets.EMPTY));

    /**
     * Background color when AudioMedia UI is stopped.
     */
    public static final Background STOP_BACKG = new Background(new BackgroundFill(Color.web("#535965"), CornerRadii.EMPTY, Insets.EMPTY));

    /**
     * Image for the repeat image icon.
     */
    public static final Image REPEAT_IMAGE = new Image(AudioMediaUI.class.getResourceAsStream("/icons/repeat.png"), 18, 18, true, false);

    /**
     * Icon play image path.
     */
    public static final String NAME_ICON_PLAY = "/icons/PlayGreenButton.png";

    /**
     * Icon pause image path.
     */
    public static final String NAME_ICON_PAUSE = "/icons/PauseBlueButton.png";

    /**
     * Shadow effect applied when audio's playing.
     */
    public static final Effect SHADOW_EFFECT = new DropShadow(5d, 2d, 2d, Color.BLACK);

    /**
     * Blomm effect applied to key label when audio's playing.
     */
    public static final Effect KEYCODE_LABEL_BLOOM = new Bloom(0.4);

    public static final TimeZone TZ = TimeZone.getTimeZone("UTC");

    /**
     * Constant Path for user datas.
     */
    public static final Path FILE_USER_DATA = Paths.get("user.dat");

    /**
     * Format a Duration with SimpleDateFormat.
     *
     * @param duration
     * @param formater
     * @return
     */
    public static String getFormatedDuration(Duration duration, SimpleDateFormat formater) {
        return formater.format(new Date((long) duration.toMillis()));
    }

}
