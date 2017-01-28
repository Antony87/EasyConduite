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
package easyconduite.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class Chrono {

    public static Timeline getTimeline(final Label label) {

        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        final Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, 0, 0, 0);

        final Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(1), (ActionEvent event) -> {
                    calendar.add(Calendar.SECOND, 1);
                    label.setText(format.format(calendar.getTime()));
                }));
        timeline.setCycleCount(Animation.INDEFINITE);

        return timeline;

    }

}
